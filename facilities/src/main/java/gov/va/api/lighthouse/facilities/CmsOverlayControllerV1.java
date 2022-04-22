package gov.va.api.lighthouse.facilities;

import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;
import static gov.va.api.lighthouse.facilities.ControllersV1.page;
import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.CMS_OVERLAY_SERVICE_NAME_COVID_19;
import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.api.v1.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v1.CmsOverlayResponse;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService;
import gov.va.api.lighthouse.facilities.api.v1.DetailedServiceResponse;
import gov.va.api.lighthouse.facilities.api.v1.DetailedServicesResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.DataBinder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** CMS Overlay Controller for version 1 facilities. */
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/v1")
public class CmsOverlayControllerV1 extends BaseCmsOverlayController {

  private static final ObjectMapper DATAMART_MAPPER =
      DatamartFacilitiesJacksonConfig.createMapper();

  private final FacilityRepository facilityRepository;

  private final CmsOverlayRepository cmsOverlayRepository;

  private final String linkerUrl;

  @Builder
  CmsOverlayControllerV1(
      @Autowired FacilityRepository facilityRepository,
      @Autowired CmsOverlayRepository cmsOverlayRepository,
      @Value("${facilities.url}") String baseUrl,
      @Value("${facilities.base-path}") String basePath) {
    this.facilityRepository = facilityRepository;
    this.cmsOverlayRepository = cmsOverlayRepository;
    String url = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    String path = basePath.replaceAll("/$", "");
    path = path.isEmpty() ? path : path + "/";
    linkerUrl = url + path + "v1/";
  }

  @GetMapping(
      value = {"/facilities/{facility_id}/services/{service_id}"},
      produces = "application/json")
  @SneakyThrows
  ResponseEntity<DetailedServiceResponse> getDetailedService(
      @PathVariable("facility_id") String facilityId,
      @PathVariable("service_id") String serviceId) {
    return ResponseEntity.ok(
        DetailedServiceResponse.builder()
            .data(
                DetailedServiceTransformerV1.toDetailedService(
                    getOverlayDetailedService(facilityId, serviceId)))
            .build());
  }

  @GetMapping(
      value = {"/facilities/{id}/services"},
      produces = "application/json")
  @SneakyThrows
  ResponseEntity<DetailedServicesResponse> getDetailedServices(
      @PathVariable("id") String facilityId,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "10") @Min(0) int perPage) {
    List<DetailedService> services =
        DetailedServiceTransformerV1.toDetailedServices(getOverlayDetailedServices(facilityId));
    PageLinkerV1 linker =
        PageLinkerV1.builder()
            .url(linkerUrl + "facilities/" + facilityId + "/services")
            .params(Parameters.builder().add("page", page).add("per_page", perPage).build())
            .totalEntries(services.size())
            .build();
    List<DetailedService> servicesPage = page(services, page, perPage);
    DetailedServicesResponse response =
        DetailedServicesResponse.builder()
            .data(servicesPage.stream().collect(toList()))
            .links(linker.links())
            .meta(
                DetailedServicesResponse.DetailedServicesMetadata.builder()
                    .pagination(linker.pagination())
                    .build())
            .build();
    return ResponseEntity.ok(response);
  }

  @SneakyThrows
  protected Optional<CmsOverlayEntity> getExistingOverlayEntity(@NonNull FacilityEntity.Pk pk) {
    return cmsOverlayRepository.findById(pk);
  }

  @GetMapping(
      value = {"/facilities/{id}/cms-overlay"},
      produces = "application/json")
  @SneakyThrows
  ResponseEntity<CmsOverlayResponse> getOverlay(@PathVariable("id") String id) {
    FacilityEntity.Pk pk = FacilityEntity.Pk.fromIdString(id);
    Optional<CmsOverlayEntity> existingOverlayEntity = getExistingOverlayEntity(pk);
    if (!existingOverlayEntity.isPresent()) {
      throw new ExceptionsUtils.NotFound(id);
    }
    CmsOverlayEntity cmsOverlayEntity = existingOverlayEntity.get();
    CmsOverlayResponse response =
        CmsOverlayResponse.builder()
            .overlay(
                CmsOverlayTransformerV1.toCmsOverlay(
                    DatamartCmsOverlay.builder()
                        .operatingStatus(
                            CmsOverlayHelper.getOperatingStatus(
                                cmsOverlayEntity.cmsOperatingStatus()))
                        .detailedServices(
                            CmsOverlayHelper.getDetailedServices(cmsOverlayEntity.cmsServices()))
                        .build()))
            .build();
    return ResponseEntity.ok(response);
  }

  @InitBinder
  void initDirectFieldAccess(DataBinder dataBinder) {
    dataBinder.initDirectFieldAccess();
  }

  @PostMapping(
      value = {"/facilities/{id}/cms-overlay"},
      produces = "application/json",
      consumes = "application/json")
  @SneakyThrows
  ResponseEntity<Void> saveOverlay(
      @PathVariable("id") String id, @Valid @RequestBody CmsOverlay overlay) {
    Optional<FacilityEntity> existingFacilityEntity =
        facilityRepository.findById(FacilityEntity.Pk.fromIdString(id));
    Optional<CmsOverlayEntity> existingCmsOverlayEntity =
        getExistingOverlayEntity(FacilityEntity.Pk.fromIdString(id));
    DatamartCmsOverlay datamartCmsOverlay = CmsOverlayTransformerV1.toVersionAgnostic(overlay);
    updateCmsOverlayData(existingCmsOverlayEntity, id, datamartCmsOverlay);
    overlay.detailedServices(
        DetailedServiceTransformerV1.toDetailedServices(datamartCmsOverlay.detailedServices()));
    if (existingFacilityEntity.isEmpty()) {
      log.info("Received Unknown Facility ID ({}) for CMS Overlay", sanitize(id));
      return ResponseEntity.accepted().build();
    } else {
      updateFacilityData(
          existingFacilityEntity.get(), existingCmsOverlayEntity, id, datamartCmsOverlay);
      return ResponseEntity.ok().build();
    }
  }

  @SneakyThrows
  void updateCmsOverlayData(
      Optional<CmsOverlayEntity> existingCmsOverlayEntity, String id, DatamartCmsOverlay overlay) {
    CmsOverlayEntity cmsOverlayEntity;
    if (existingCmsOverlayEntity.isEmpty()) {
      List<DatamartDetailedService> activeServices =
          getActiveServicesFromOverlay(id, overlay.detailedServices());
      cmsOverlayEntity =
          CmsOverlayEntity.builder()
              .id(FacilityEntity.Pk.fromIdString(id))
              .cmsOperatingStatus(
                  CmsOverlayHelper.serializeOperatingStatus(overlay.operatingStatus()))
              .cmsServices(CmsOverlayHelper.serializeDetailedServices(activeServices))
              .build();
    } else {
      cmsOverlayEntity = existingCmsOverlayEntity.get();
      if (overlay.operatingStatus() != null) {
        cmsOverlayEntity.cmsOperatingStatus(
            CmsOverlayHelper.serializeOperatingStatus(overlay.operatingStatus()));
      }
      List<DatamartDetailedService> overlayServices = overlay.detailedServices();
      if (overlayServices != null) {
        List<DatamartDetailedService> toSaveDetailedServices =
            findServicesToSave(cmsOverlayEntity, id, overlay.detailedServices(), DATAMART_MAPPER);
        cmsOverlayEntity.cmsServices(
            CmsOverlayHelper.serializeDetailedServices(toSaveDetailedServices));
      }
    }
    cmsOverlayRepository.save(cmsOverlayEntity);
  }

  @SneakyThrows
  void updateFacilityData(
      FacilityEntity facilityEntity,
      Optional<CmsOverlayEntity> existingCmsOverlayEntity,
      String id,
      DatamartCmsOverlay overlay) {
    DatamartFacility facility =
        DATAMART_MAPPER.readValue(facilityEntity.facility(), DatamartFacility.class);
    // Only save active services from the overlay if they exist
    List<DatamartDetailedService> toSaveDetailedServices;
    if (existingCmsOverlayEntity.isEmpty()) {
      toSaveDetailedServices = getActiveServicesFromOverlay(id, overlay.detailedServices());
    } else {
      toSaveDetailedServices =
          findServicesToSave(
              existingCmsOverlayEntity.get(), id, overlay.detailedServices(), DATAMART_MAPPER);
    }

    Set<DatamartFacility.HealthService> facilityHealthServices = new HashSet<>();
    if (!toSaveDetailedServices.isEmpty()) {
      Set<String> detailedServices = new HashSet<>();
      for (DatamartDetailedService service : toSaveDetailedServices) {
        if (service.name().equals(CMS_OVERLAY_SERVICE_NAME_COVID_19)) {
          detailedServices.add("Covid19Vaccine");
          if (facilityEntity.services() != null) {
            facilityEntity.services().add("Covid19Vaccine");
          } else {
            facilityEntity.services(Set.of("Covid19Vaccine"));
          }
          facilityHealthServices.add(DatamartFacility.HealthService.Covid19Vaccine);
        } else {
          detailedServices.add(service.name());
        }
      }
      facilityEntity.overlayServices(detailedServices);
    }

    if (facility != null) {
      DatamartFacility.OperatingStatus operatingStatus = overlay.operatingStatus();
      if (operatingStatus != null) {
        facility.attributes().operatingStatus(operatingStatus);
        if (operatingStatus.code() == DatamartFacility.OperatingStatusCode.CLOSED) {
          facility.attributes().activeStatus(DatamartFacility.ActiveStatus.T);
        } else {
          facility.attributes().activeStatus(DatamartFacility.ActiveStatus.A);
        }
      }
      if (overlay.detailedServices() != null) {
        facility
            .attributes()
            .detailedServices(toSaveDetailedServices.isEmpty() ? null : toSaveDetailedServices);
      }

      if (facility.attributes().services.health() != null) {
        facilityHealthServices.addAll(facility.attributes().services.health());
      }

      List<DatamartFacility.HealthService> facilityHealthServiceList =
          new ArrayList<>(facilityHealthServices);
      Collections.sort(facilityHealthServiceList);
      facility.attributes().services().health(facilityHealthServiceList);

      facilityEntity.facility(DATAMART_MAPPER.writeValueAsString(facility));
    }

    facilityRepository.save(facilityEntity);
  }
}
