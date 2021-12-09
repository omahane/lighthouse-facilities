package gov.va.api.lighthouse.facilities;

import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;
import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.CMS_OVERLAY_SERVICE_NAME_COVID_19;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.api.cms.DetailedService;
import gov.va.api.lighthouse.facilities.api.v1.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v1.CmsOverlayResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import org.springframework.web.bind.annotation.RestController;

/** CMS Overlay Controller for version 1 facilities. */
@Slf4j
@Builder
@Validated
@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CmsOverlayControllerV1 extends BaseCmsOverlayController {

  private static final ObjectMapper DATAMART_MAPPER =
      DatamartFacilitiesJacksonConfig.createMapper();

  private final FacilityRepository facilityRepository;

  private final CmsOverlayRepository cmsOverlayRepository;

  @GetMapping(
      value = {"/v1/facilities/{facility_id}/services/{service_id}"},
      produces = "application/json")
  @SneakyThrows
  ResponseEntity<DetailedService> getDetailedService(
      @PathVariable("facility_id") String facilityId,
      @PathVariable("service_id") String serviceId) {
    return ResponseEntity.ok(getOverlayDetailedService(facilityId, serviceId));
  }

  @GetMapping(
      value = {"/v1/facilities/{id}/services"},
      produces = "application/json")
  @SneakyThrows
  ResponseEntity<List<DetailedService>> getDetailedServices(@PathVariable("id") String facilityId) {
    return ResponseEntity.ok(getOverlayDetailedServices(facilityId));
  }

  @SneakyThrows
  protected Optional<CmsOverlayEntity> getExistingOverlayEntity(@NonNull FacilityEntity.Pk pk) {
    return cmsOverlayRepository.findById(pk);
  }

  @GetMapping(
      value = {"/v1/facilities/{id}/cms-overlay"},
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
      value = {"/v1/facilities/{id}/cms-overlay"},
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
      List<DetailedService> activeServices =
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
      List<DetailedService> overlayServices = overlay.detailedServices();
      if (overlayServices != null) {
        List<DetailedService> toSaveDetailedServices =
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
    List<DetailedService> toSaveDetailedServices;
    if (existingCmsOverlayEntity.isEmpty()) {
      toSaveDetailedServices = getActiveServicesFromOverlay(id, overlay.detailedServices());
    } else {
      toSaveDetailedServices =
          findServicesToSave(
              existingCmsOverlayEntity.get(), id, overlay.detailedServices(), DATAMART_MAPPER);
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
      facilityEntity.facility(DATAMART_MAPPER.writeValueAsString(facility));
    }
    if (!toSaveDetailedServices.isEmpty()) {
      Set<String> detailedServices = new HashSet<>();
      for (DetailedService service : toSaveDetailedServices) {
        if (service.name().equals(CMS_OVERLAY_SERVICE_NAME_COVID_19)) {
          detailedServices.add("Covid19Vaccine");
        } else {
          detailedServices.add(service.name());
        }
      }
      facilityEntity.overlayServices(detailedServices);
    }
    facilityRepository.save(facilityEntity);
  }
}
