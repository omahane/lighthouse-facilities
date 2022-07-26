package gov.va.api.lighthouse.facilities;

import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;
import static gov.va.api.lighthouse.facilities.api.TypedService.INVALID_SVC_ID;
import static org.apache.commons.lang3.StringUtils.capitalize;

import gov.va.api.lighthouse.facilities.api.TypedService;
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
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
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
@Validated
@RestController
@RequestMapping(value = "/v0")
public class CmsOverlayControllerV0 extends BaseCmsOverlayController {
  @Builder
  CmsOverlayControllerV0(
      @Autowired FacilityRepository facilityRepository,
      @Autowired CmsOverlayRepository cmsOverlayRepository) {
    super(facilityRepository, cmsOverlayRepository);
  }

  /** Obtain service id for specified service name. */
  private static String getServiceIdFromServiceName(@NonNull String serviceName) {
    return Facility.HealthService.isRecognizedEnumOrCovidService(serviceName)
        ? Facility.HealthService.fromString(serviceName).serviceId()
        : Facility.BenefitsService.isRecognizedServiceEnum(serviceName)
            ? Facility.BenefitsService.fromString(serviceName).serviceId()
            : Facility.OtherService.isRecognizedServiceEnum(serviceName)
                ? Facility.OtherService.fromString(serviceName).serviceId()
                : INVALID_SVC_ID;
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

  /** Determine whether specified service id matches that for V0 or V1 service. */
  protected boolean isRecognizedServiceId(@NonNull String serviceId) {
    return Facility.HealthService.isRecognizedServiceId(serviceId)
        || Facility.BenefitsService.isRecognizedServiceId(serviceId)
        || Facility.OtherService.isRecognizedServiceId(serviceId)
        || super.isRecognizedServiceId(serviceId);
  }

  /**
   * Populate service id based on service name and filter out services with unrecognized service
   * ids.
   */
  private void populateServiceIdAndFilterOutInvalid(@NonNull DatamartCmsOverlay overlay) {
    if (ObjectUtils.isNotEmpty(overlay.detailedServices())) {
      overlay.detailedServices(
          overlay.detailedServices().parallelStream()
              // Filter out services with invalid service info blocks
              .filter(dds -> dds.serviceInfo() != null)
              .map(
                  dds -> {
                    if (StringUtils.isEmpty(dds.serviceInfo().serviceId())) {
                      dds.serviceInfo()
                          .serviceId(getServiceIdFromServiceName(dds.serviceInfo().name()));
                    }
                    if (dds.serviceInfo().serviceType() == null) {
                      final Optional<? extends TypedService> typedService =
                          getTypedServiceForServiceId(dds.serviceInfo().serviceId());
                      dds.serviceInfo()
                          .serviceType(
                              typedService.isPresent() ? typedService.get().serviceType() : null);
                    }
                    return dds;
                  })
              // Filter out services with invalid service ids
              .filter(dds -> isRecognizedServiceId(dds.serviceInfo().serviceId()))
              // Filter out services with invalid service types
              .filter(dds -> dds.serviceInfo().serviceType() != null)
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
      @PathVariable("id") String id, @Valid @RequestBody DatamartCmsOverlay overlay) {
    populateServiceIdAndFilterOutInvalid(overlay);
    Optional<CmsOverlayEntity> existingCmsOverlayEntity =
        getExistingOverlayEntity(FacilityEntity.Pk.fromIdString(id));
    updateCmsOverlayData(existingCmsOverlayEntity, id, overlay);
    Optional<FacilityEntity> existingFacilityEntity =
        facilityRepository.findById(FacilityEntity.Pk.fromIdString(id));
    if (existingFacilityEntity.isEmpty()) {
      log.info("Received Unknown Facility ID ({}) for CMS Overlay", sanitize(id));
      return ResponseEntity.accepted().build();
    } else {
      updateFacilityData(existingFacilityEntity.get(), existingCmsOverlayEntity, id, overlay);
      return ResponseEntity.ok().build();
    }
  }

  @SneakyThrows
  void updateFacilityData(
      @NonNull FacilityEntity facilityEntity,
      @NonNull Optional<CmsOverlayEntity> existingCmsOverlayEntity,
      String id,
      @NonNull DatamartCmsOverlay overlay) {
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
      Set<String> detailedServiceIds = new HashSet<>();
      toSaveDetailedServices.stream()
          .forEach(
              service -> {
                // Update detailed services
                detailedServiceIds.add(capitalize(service.serviceInfo().serviceId()));
                // Update facility health services
                if (service
                    .serviceInfo()
                    .serviceId()
                    .equals(DatamartFacility.HealthService.Covid19Vaccine.serviceId())) {
                  if (facilityEntity.services() != null) {
                    facilityEntity
                        .services()
                        .add(capitalize(DatamartFacility.HealthService.Covid19Vaccine.serviceId()));
                  } else {
                    facilityEntity.services(
                        Set.of(
                            capitalize(DatamartFacility.HealthService.Covid19Vaccine.serviceId())));
                  }
                  facilityHealthServices.add(DatamartFacility.HealthService.Covid19Vaccine);
                }
              });
      facilityEntity.overlayServices(detailedServiceIds);
    }

    DatamartFacility facility =
        DATAMART_MAPPER.readValue(facilityEntity.facility(), DatamartFacility.class);

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
        facility
            .attributes()
            .detailedServices(
                toSaveDetailedServices.isEmpty()
                    ? null
                    : toSaveDetailedServices.parallelStream()
                        .filter(dds -> dds.active())
                        .filter(
                            dds ->
                                DatamartFacility.HealthService.Covid19Vaccine.serviceId()
                                    .equals(dds.serviceInfo().serviceId()))
                        .collect(Collectors.toList()));
      }

      if (facility.attributes().services().health() != null) {
        facilityHealthServices.addAll(facility.attributes().services().health());
      }

      if (overlay.detailedServices() != null) {
        List<String> disabledCmsServiceIds =
            overlay.detailedServices().stream()
                .filter(dds -> !dds.active())
                .map(dds -> dds.serviceInfo().serviceId())
                .collect(Collectors.toList());

        disabledCmsServiceIds.stream()
            .forEach(
                disabledServiceId -> {
                  if (disabledServiceId.equals(
                      DatamartFacility.HealthService.Covid19Vaccine.serviceId())) {
                    facilityHealthServices.remove(DatamartFacility.HealthService.Covid19Vaccine);
                    facilityEntity
                        .services()
                        .remove(
                            capitalize(DatamartFacility.HealthService.Covid19Vaccine.serviceId()));
                  }
                });
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
