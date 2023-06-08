package gov.va.api.lighthouse.facilities;

import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService;
import static gov.va.api.lighthouse.facilities.DatamartFacility.Service;
import static gov.va.api.lighthouse.facilities.FacilityServicesUtils.populate;
import static gov.va.api.lighthouse.facilities.api.TypedService.INVALID_SVC_ID;

import gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService;
import gov.va.api.lighthouse.facilities.DatamartFacility.OtherService;
import gov.va.api.lighthouse.facilities.DatamartFacility.Service.Source;
import gov.va.api.lighthouse.facilities.DatamartFacility.Services;
import gov.va.api.lighthouse.facilities.api.TypeOfService;
import gov.va.api.lighthouse.facilities.api.TypedService;
import gov.va.api.lighthouse.facilities.api.v0.CmsOverlayResponse;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
      @Autowired FacilityServicesRepository facilityServicesRepository,
      @Autowired CmsOverlayRepository cmsOverlayRepository) {
    super(facilityRepository, facilityServicesRepository, cmsOverlayRepository);
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
                        .core(CmsOverlayHelper.getCore(cmsOverlayEntity.core()))
                        .build()))
            .build();
    return ResponseEntity.ok(response);
  }

  /**
   * Obtain service id for specified service name. Used when CMS overlay service is uploaded with no
   * unique service identifier.
   */
  @SneakyThrows
  private String getServiceIdFromServiceName(@NonNull String serviceName) {
    return Facility.HealthService.isRecognizedEnumOrCovidService(serviceName)
        ? Facility.HealthService.fromString(serviceName).serviceId()
        : Facility.BenefitsService.isRecognizedServiceEnum(serviceName)
            ? Facility.BenefitsService.fromString(serviceName).serviceId()
            : Facility.OtherService.isRecognizedServiceEnum(serviceName)
                ? Facility.OtherService.fromString(serviceName).serviceId()
                : INVALID_SVC_ID;
  }

  @InitBinder
  void initDirectFieldAccess(DataBinder dataBinder) {
    dataBinder.initDirectFieldAccess();
  }

  /** Determine whether specified service id matches that for V0 or V1 service. */
  @SneakyThrows
  protected boolean isRecognizedServiceId(@NonNull String serviceId) {
    return Facility.HealthService.isRecognizedServiceId(serviceId)
        || Facility.BenefitsService.isRecognizedServiceId(serviceId)
        || Facility.OtherService.isRecognizedServiceId(serviceId)
        || super.isRecognizedServiceId(serviceId);
  }

  /** Record date when CMS uploaded overlay services. */
  @SneakyThrows
  private void markDateWhenCmsUploadedOverlayServices(@NonNull DatamartCmsOverlay overlay) {
    if (ObjectUtils.isNotEmpty(overlay.detailedServices())) {
      final var currentDateTime = LocalDateTime.now(ZoneId.of("UTC").normalized());
      overlay.detailedServices().stream()
          .forEach(
              dds -> {
                dds.lastUpdated(currentDateTime);
              });
    }
  }

  /**
   * Populate service id based on service name and filter out services with unrecognized service
   * ids.
   */
  @SneakyThrows
  private void populateServiceIdAndFilterOutInvalid(@NonNull DatamartCmsOverlay overlay) {
    if (ObjectUtils.isNotEmpty(overlay.detailedServices())) {
      overlay.detailedServices(
          overlay.detailedServices().stream()
              // Filter out services with invalid service info blocks
              .filter(dds -> dds.serviceInfo() != null)
              .map(
                  dds -> {
                    if (StringUtils.isBlank(dds.serviceInfo().serviceId())) {
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
    markDateWhenCmsUploadedOverlayServices(overlay);
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
    final Set<Service<BenefitsService>> facilityBenefitsServices = new HashSet<>();
    final Set<Service<HealthService>> facilityHealthServices = new HashSet<>();
    final Set<Service<OtherService>> facilityOtherServices = new HashSet<>();
    if (!toSaveDetailedServices.isEmpty()) {
      final Set<String> detailedServiceIds = new HashSet<>();
      toSaveDetailedServices.stream()
          .forEach(
              service -> {
                if (TypeOfService.Benefits.equals(service.serviceInfo().serviceType())) {
                  Optional<DatamartFacility.BenefitsService> benefitsService =
                      DatamartFacility.BenefitsService.fromServiceId(
                          service.serviceInfo().serviceId());
                  if (benefitsService.isPresent()) {
                    // Update detailed service ids
                    detailedServiceIds.add(benefitsService.get().name());
                    // Update benefits facility services
                    facilityBenefitsServices.add(
                        Service.<DatamartFacility.BenefitsService>builder()
                            .name(service.serviceInfo().name())
                            .serviceType(benefitsService.get())
                            .source(Source.CMS)
                            .build());
                  }
                } else if (TypeOfService.Health.equals(service.serviceInfo().serviceType())) {
                  Optional<DatamartFacility.HealthService> healthService =
                      DatamartFacility.HealthService.fromServiceId(
                          service.serviceInfo().serviceId());
                  if (healthService.isPresent()) {
                    // Update detailed service ids
                    detailedServiceIds.add(healthService.get().name());
                    // Update health facility services
                    facilityHealthServices.add(
                        Service.<DatamartFacility.HealthService>builder()
                            .name(service.serviceInfo().name())
                            .serviceType(healthService.get())
                            .source(Source.CMS)
                            .build());
                  }
                } else if (TypeOfService.Other.equals(service.serviceInfo().serviceType())) {
                  Optional<DatamartFacility.OtherService> otherService =
                      DatamartFacility.OtherService.fromServiceId(
                          service.serviceInfo().serviceId());
                  if (otherService.isPresent()) {
                    // Update detailed service ids
                    detailedServiceIds.add(otherService.get().name());
                    // Update other facility services
                    facilityOtherServices.add(
                        Service.<DatamartFacility.OtherService>builder()
                            .name(service.serviceInfo().name())
                            .serviceType(otherService.get())
                            .source(Source.CMS)
                            .build());
                  }
                }
              });
      facilityEntity.overlayServices(detailedServiceIds);
    }
    final DatamartFacility facility =
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
        // Only add Covid-19 detailed service, if present, to facility attributes detailed_services
        // list
        facility
            .attributes()
            .detailedServices(
                toSaveDetailedServices.isEmpty()
                    ? null
                    : toSaveDetailedServices.parallelStream()
                        .filter(
                            dds ->
                                HealthService.Covid19Vaccine.serviceId()
                                    .equals(dds.serviceInfo().serviceId()))
                        .collect(Collectors.toList()));
        List<String> disabledCmsServiceIds =
            overlay.detailedServices().stream()
                .filter(dds -> !dds.active())
                .map(dds -> dds.serviceInfo().serviceId())
                .collect(Collectors.toList());
        if (facility.attributes().services().benefits() != null) {
          facilityBenefitsServices.addAll(
              facility.attributes().services().benefits().stream()
                  // Remove inactive benefits services
                  .filter(
                      bs ->
                          !(disabledCmsServiceIds.contains(bs.serviceId())
                              && bs.source().equals(Source.CMS)))
                  // Remove similar benefits services
                  .filter(
                      bs ->
                          !containsSimilarService(
                              facilityBenefitsServices, bs.serviceId(), bs.source()))
                  .collect(Collectors.toList()));
        }
        if (facility.attributes().services().health() != null) {
          facilityHealthServices.addAll(
              facility.attributes().services().health().stream()
                  // Remove inactive health services
                  .filter(
                      hs ->
                          !(disabledCmsServiceIds.contains(hs.serviceId())
                              && hs.source().equals(Source.CMS)))
                  // Remove similar health services
                  .filter(
                      hs ->
                          !containsSimilarService(
                              facilityHealthServices, hs.serviceId(), hs.source()))
                  .collect(Collectors.toList()));
        }
        if (facility.attributes().services().other() != null) {
          facilityOtherServices.addAll(
              facility.attributes().services().other().stream()
                  // Remove inactive other services
                  .filter(
                      os ->
                          !(disabledCmsServiceIds.contains(os.serviceId())
                              && os.source().equals(Source.CMS)))
                  // Remove similar other services
                  .filter(
                      os ->
                          !containsSimilarService(
                              facilityOtherServices, os.serviceId(), os.source()))
                  .collect(Collectors.toList()));
        }
      }

      // Reset facility services
      facilityEntity.services(new HashSet<>());

      List<Service<HealthService>> facilityHealthServiceList =
          new ArrayList<>(facilityHealthServices);
      Collections.sort(facilityHealthServiceList);
      facility.attributes().services().health(facilityHealthServiceList);
      facilityEntity
          .services()
          .addAll(convertToSetOfVersionAgnosticStrings(facilityHealthServiceList));

      List<Service<BenefitsService>> facilityBenefitsServiceList =
          new ArrayList<>(facilityBenefitsServices);
      Collections.sort(facilityBenefitsServiceList);
      facility.attributes().services().benefits(facilityBenefitsServiceList);
      facilityEntity
          .services()
          .addAll(convertToSetOfVersionAgnosticStrings(facilityBenefitsServiceList));

      List<Service<OtherService>> facilityOtherServiceList = new ArrayList<>(facilityOtherServices);
      Collections.sort(facilityOtherServiceList);
      facility.attributes().services().other(facilityOtherServiceList);
      facilityEntity
          .services()
          .addAll(convertToSetOfVersionAgnosticStrings(facilityOtherServiceList));

      // Update health connect phone number for facility attributes
      if (ObjectUtils.isNotEmpty(facility.attributes().phone())) {
        if (overlay.containsHealthConnectPhoneNumber()) {
          facility
              .attributes()
              .phone()
              .healthConnect(overlay.healthCareSystem().healthConnectPhone());
        } else {
          facility.attributes().phone().healthConnect(null);
        }
      }

      facilityEntity.facility(DATAMART_MAPPER.writeValueAsString(facility));
    }
    facilityRepository.save(facilityEntity);

    if (facility != null && ObjectUtils.isNotEmpty(facility.attributes())) {
      updateFacilityServicesData(facilityEntity, facility.attributes().services());
    }
  }

  @SneakyThrows
  private void updateFacilityServicesData(
      @NonNull FacilityEntity record, Services facilityServices) {
    if (ObjectUtils.isNotEmpty(facilityServices)) {
      updateFacilityServicesData(record, facilityServices.benefits());
      updateFacilityServicesData(record, facilityServices.health());
      updateFacilityServicesData(record, facilityServices.other());
    }
  }

  @SneakyThrows
  private <T extends TypedService> void updateFacilityServicesData(
      @NonNull FacilityEntity record, List<Service<T>> facilityServices) {
    if (ObjectUtils.isNotEmpty(facilityServices)) {
      // Update facility services
      facilityServices.stream()
          .forEach(
              fs -> {
                FacilityServicesEntity.Pk id =
                    FacilityServicesEntity.Pk.of(
                        record.id().type(),
                        record.id().stationNumber(),
                        Service.<T>builder()
                            .name(fs.name())
                            .serviceId(fs.serviceId())
                            .source(fs.source())
                            .build()
                            .toJson());
                Optional<FacilityServicesEntity> existing = facilityServicesRepository.findById(id);
                if (existing.isPresent()) {
                  try {
                    facilityServicesRepository.save(populate(existing.get(), fs));
                  } catch (final Exception e) {
                    e.printStackTrace();
                    log.error(
                        "Failed to save update to facility services record {}: {}",
                        id,
                        e.getMessage());
                    throw e;
                  }
                }
              });
    }
  }
}
