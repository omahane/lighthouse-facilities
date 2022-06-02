package gov.va.api.lighthouse.facilities;

import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;
import static gov.va.api.lighthouse.facilities.DatamartFacilitiesJacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.v0.DetailedService.getServiceIdFromServiceName;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.api.v0.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v0.CmsOverlayResponse;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.DataBinder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** CMS Overlay Controller for version 0 facilities. */
@Slf4j
@Builder
@Validated
@RestController
@RequestMapping(value = "/v0")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CmsOverlayControllerV0 extends BaseCmsOverlayController {
  private static final ObjectMapper DATAMART_MAPPER = createMapper();

  private final FacilityRepository facilityRepository;

  private final CmsOverlayRepository cmsOverlayRepository;

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
                CmsOverlayTransformerV0.toCmsOverlay(
                    DatamartCmsOverlay.builder()
                        .operatingStatus(
                            CmsOverlayHelper.getOperatingStatus(
                                cmsOverlayEntity.cmsOperatingStatus()))
                        .detailedServices(
                            CmsOverlayHelper.getDetailedServices(cmsOverlayEntity.cmsServices())
                                .parallelStream()
                                .filter(ds -> isRecognizedServiceId(ds.serviceInfo().serviceId()))
                                .collect(Collectors.toList()))
                        .healthCareSystem(
                            CmsOverlayHelper.getHealthCareSystem(
                                cmsOverlayEntity.healthCareSystem()))
                        .build()))
            .build();
    return ResponseEntity.ok(response);
  }

  @InitBinder
  void initDirectFieldAccess(DataBinder dataBinder) {
    dataBinder.initDirectFieldAccess();
  }

  /** Determine whether specified service id matches that for V0 service. */
  protected boolean isRecognizedServiceId(@NonNull String serviceId) {
    return Facility.HealthService.isRecognizedServiceId(serviceId)
        || Facility.BenefitsService.isRecognizedServiceId(serviceId)
        || Facility.OtherService.isRecognizedServiceId(serviceId);
  }

  private void populateServiceIdAndFilterOutInvalid(@NonNull CmsOverlay overlay) {
    if (isNotEmpty(overlay.detailedServices())) {
      overlay.detailedServices(
          overlay.detailedServices().parallelStream()
              .map(
                  ds ->
                      isNotEmpty(ds.serviceId())
                          ? ds
                          : ds.serviceId(getServiceIdFromServiceName(ds.name())))
              .filter(ds -> isRecognizedServiceId(ds.serviceId()))
              .collect(Collectors.toList()));
    }
  }

  /** Upload CMS overlay associated with specified facility. */
  @PostMapping(
      value = {"/facilities/{id}/cms-overlay"},
      produces = "application/json",
      consumes = "application/json")
  @SneakyThrows
  ResponseEntity<Void> saveOverlay(
      @PathVariable("id") String id, @Valid @RequestBody CmsOverlay overlay) {
    populateServiceIdAndFilterOutInvalid(overlay);
    DatamartCmsOverlay datamartCmsOverlay =
        filterOutUnrecognizedServicesFromOverlay(
            CmsOverlayTransformerV0.toVersionAgnostic(overlay));
    Optional<CmsOverlayEntity> existingCmsOverlayEntity =
        getExistingOverlayEntity(FacilityEntity.Pk.fromIdString(id));
    updateCmsOverlayData(existingCmsOverlayEntity, id, datamartCmsOverlay);
    overlay.detailedServices(
        DetailedServiceTransformerV0.toDetailedServices(datamartCmsOverlay.detailedServices()));
    Optional<FacilityEntity> existingFacilityEntity =
        facilityRepository.findById(FacilityEntity.Pk.fromIdString(id));
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
              .healthCareSystem(
                  CmsOverlayHelper.serializeHealthCareSystem(overlay.healthCareSystem()))
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
      if (overlay.healthCareSystem != null) {
        cmsOverlayEntity.healthCareSystem(
            CmsOverlayHelper.serializeHealthCareSystem(overlay.healthCareSystem()));
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
        if (service
            .serviceInfo()
            .serviceId()
            .equals(DatamartFacility.HealthService.Covid19Vaccine.serviceId())) {
          detailedServices.add(DatamartFacility.HealthService.Covid19Vaccine.serviceId());
          if (facilityEntity.services() != null) {
            facilityEntity
                .services()
                .add(DatamartFacility.HealthService.Covid19Vaccine.serviceId());
          } else {
            facilityEntity.services(
                Set.of(DatamartFacility.HealthService.Covid19Vaccine.serviceId()));
          }

          facilityHealthServices.add(DatamartFacility.HealthService.Covid19Vaccine);
        } else {
          detailedServices.add(service.serviceInfo().serviceId());
          // TODO: Update facility services:
          //   If service info is of service type health, update facility health services
          //   If service info is of service type benefits, update facility benefits services
          //   If service info is of service type other, update facility other services
        }
      }
      facilityEntity.overlayServices(detailedServices);
    }

    if (facility != null) {
      DatamartFacility.OperatingStatus operatingStatus = overlay.operatingStatus();
      if (operatingStatus != null) {
        facility.attributes().operatingStatus(operatingStatus);
        facility
            .attributes()
            .activeStatus(
                operatingStatus.code() == DatamartFacility.OperatingStatusCode.CLOSED
                    ? DatamartFacility.ActiveStatus.T
                    : DatamartFacility.ActiveStatus.A);
      }
      if (overlay.detailedServices() != null) {
        // Only add Covid-19 detailed service, if present, to facility attributes
        facility
            .attributes()
            .detailedServices(
                toSaveDetailedServices.isEmpty()
                    ? null
                    : toSaveDetailedServices.parallelStream()
                        .filter(
                            ds ->
                                DatamartFacility.HealthService.Covid19Vaccine.serviceId()
                                    .equals(ds.serviceInfo().serviceId()))
                        .collect(Collectors.toList()));
      }

      if (facility.attributes().services().health() != null) {
        facilityHealthServices.addAll(facility.attributes().services().health());
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
