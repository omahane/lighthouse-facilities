package gov.va.api.lighthouse.facilities;

import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;
import static gov.va.api.lighthouse.facilities.ControllersV1.page;
import static gov.va.api.lighthouse.facilities.DatamartFacility.Service;
import static gov.va.api.lighthouse.facilities.FacilityServicesUtils.populate;
import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildLinkerUrlV1;
import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildServicesLink;
import static gov.va.api.lighthouse.facilities.api.TypedService.INVALID_SVC_ID;
import static java.util.stream.Collectors.toList;

import gov.va.api.lighthouse.facilities.DatamartFacility.Service.Source;
import gov.va.api.lighthouse.facilities.api.TypeOfService;
import gov.va.api.lighthouse.facilities.api.TypedService;
import gov.va.api.lighthouse.facilities.api.v1.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v1.CmsOverlayResponse;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService;
import gov.va.api.lighthouse.facilities.api.v1.DetailedServiceResponse;
import gov.va.api.lighthouse.facilities.api.v1.DetailedServicesResponse;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
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
  private final String linkerUrl;

  @Builder
  CmsOverlayControllerV1(
      @Autowired FacilityRepository facilityRepository,
      @Autowired FacilityServicesRepository facilityServicesRepository,
      @Autowired CmsOverlayRepository cmsOverlayRepository,
      @Value("${facilities.url}") String baseUrl,
      @Value("${facilities.base-path}") String basePath) {
    super(facilityRepository, facilityServicesRepository, cmsOverlayRepository);
    linkerUrl = buildLinkerUrlV1(baseUrl, basePath);
  }

  /** Filter DetailedServices using serviceIds and serviceType parameters. */
  public static List<DetailedService> filterServices(
      List<DetailedService> services, String serviceType, List<String> serviceIds) {
    Predicate<DetailedService> isDetailedServiceIdPresent =
        s -> serviceIds.contains(s.serviceInfo().serviceId());
    Predicate<DetailedService> isDetailedServiceTypeValid =
        s ->
            EnumUtils.isValidEnum(
                DetailedService.ServiceType.class, StringUtils.capitalize(serviceType));
    Predicate<DetailedService> doesServiceTypeMatchDetailedServiceType =
        s -> serviceType.equals(s.serviceInfo().serviceType().toString().toLowerCase());
    if (!serviceIds.isEmpty()) {
      services = services.stream().filter(isDetailedServiceIdPresent).collect(toList());
    }
    if (!serviceType.isEmpty()) {
      services =
          services.stream()
              .filter(isDetailedServiceTypeValid.and(doesServiceTypeMatchDetailedServiceType))
              .collect(toList());
    }
    return services;
  }

  /**
   * Obtain service id for specified service name. Used when CMS overlay service is uploaded with no
   * unique service identifier.
   */
  @SneakyThrows
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
      value = {"/facilities/{facilityId}/services/{serviceId}"},
      produces = "application/json")
  @SneakyThrows
  ResponseEntity<DetailedServiceResponse> getDetailedService(
      @PathVariable("facilityId") String facilityId, @PathVariable("serviceId") String serviceId) {
    return ResponseEntity.ok(
        DetailedServiceResponse.builder()
            .data(
                DetailedServiceTransformerV1.toDetailedService(
                    getOverlayDetailedService(facilityId, serviceId)))
            .build());
  }

  @GetMapping(
      value = {"/facilities/{facilityId}/services"},
      produces = "application/json")
  @SneakyThrows
  ResponseEntity<DetailedServicesResponse> getDetailedServices(
      @PathVariable("facilityId") String facilityId,
      @RequestParam(value = "serviceIds", required = false, defaultValue = "")
          List<String> serviceIds,
      @RequestParam(value = "serviceType", required = false, defaultValue = "") String serviceType,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "10") @Min(0) int perPage) {
    List<DetailedService> services =
        filterServices(
            DetailedServiceTransformerV1.toDetailedServices(getOverlayDetailedServices(facilityId)),
            serviceType,
            serviceIds);
    PageLinkerV1 linker =
        PageLinkerV1.builder()
            .url(buildServicesLink(linkerUrl, facilityId))
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

  @GetMapping(
      value = {"/facilities/{facilityId}/cms-overlay"},
      produces = "application/json")
  @SneakyThrows
  ResponseEntity<CmsOverlayResponse> getOverlay(@PathVariable("facilityId") String id) {
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

  @InitBinder
  void initDirectFieldAccess(DataBinder dataBinder) {
    dataBinder.initDirectFieldAccess();
  }

  /** Record date when CMS uploaded overlay services. */
  @SneakyThrows
  private void markDateWhenCmsUploadedOverlayServices(@NonNull CmsOverlay overlay) {
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
  private void populateServiceInfoAndFilterOutInvalid(@NonNull CmsOverlay overlay) {
    if (ObjectUtils.isNotEmpty(overlay.detailedServices())) {
      overlay.detailedServices(
          overlay.detailedServices().stream()
              // Filter out services with invalid service info blocks
              .filter(ds -> ds.serviceInfo() != null)
              .map(
                  ds -> {
                    // Update service id
                    if (StringUtils.isBlank(ds.serviceInfo().serviceId())) {
                      ds.serviceInfo()
                          .serviceId(getServiceIdFromServiceName(ds.serviceInfo().name()));
                    }
                    // Update service type
                    if (ds.serviceInfo().serviceType() == null) {
                      final Optional<? extends TypedService> typedService =
                          getTypedServiceForServiceId(ds.serviceInfo().serviceId());
                      ds.serviceInfo()
                          .serviceType(
                              typedService.isPresent() ? typedService.get().serviceType() : null);
                    }
                    return ds;
                  })
              // Filter out services with invalid service ids
              .filter(ds -> isRecognizedServiceId(ds.serviceInfo().serviceId()))
              // Filter out services with invalid service types
              .filter(ds -> ds.serviceInfo().serviceType() != null)
              .collect(Collectors.toList()));
    }
  }

  /** Upload CMS overlay associated with specified facility. */
  @PostMapping(
      value = {"/facilities/{facilityId}/cms-overlay"},
      produces = "application/json",
      consumes = "application/json")
  @SneakyThrows
  ResponseEntity<Void> saveOverlay(
      @PathVariable("facilityId") String id, @Valid @RequestBody CmsOverlay overlay) {
    populateServiceInfoAndFilterOutInvalid(overlay);
    markDateWhenCmsUploadedOverlayServices(overlay);
    DatamartCmsOverlay datamartCmsOverlay =
        filterOutUnrecognizedServicesFromOverlay(
            CmsOverlayTransformerV1.toVersionAgnostic(overlay));
    Optional<CmsOverlayEntity> existingCmsOverlayEntity =
        getExistingOverlayEntity(FacilityEntity.Pk.fromIdString(id));
    updateCmsOverlayData(existingCmsOverlayEntity, id, datamartCmsOverlay);
    overlay.detailedServices(
        DetailedServiceTransformerV1.toDetailedServices(datamartCmsOverlay.detailedServices()));
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
    final Set<Service<DatamartFacility.BenefitsService>> facilityBenefitsServices = new HashSet<>();
    final Set<Service<DatamartFacility.HealthService>> facilityHealthServices = new HashSet<>();
    final Set<Service<DatamartFacility.OtherService>> facilityOtherServices = new HashSet<>();
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
                                DatamartFacility.HealthService.Covid19Vaccine.serviceId()
                                    .equals(dds.serviceInfo().serviceId()))
                        .collect(Collectors.toList()));
        // Determine which overlay services are inactive
        final List<String> disabledCmsServiceIds =
            (overlay.detailedServices() != null)
                ? overlay.detailedServices().stream()
                    .filter(dds -> !dds.active())
                    .map(dds -> dds.serviceInfo().serviceId())
                    .collect(Collectors.toList())
                : Collections.emptyList();
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
      List<Service<DatamartFacility.HealthService>> facilityHealthServiceList =
          new ArrayList<>(facilityHealthServices);
      Collections.sort(facilityHealthServiceList);
      facility.attributes().services().health(facilityHealthServiceList);
      facilityEntity
          .services()
          .addAll(convertToSetOfVersionAgnosticStrings(facilityHealthServiceList));
      List<Service<DatamartFacility.BenefitsService>> facilityBenefitsServiceList =
          new ArrayList<>(facilityBenefitsServices);
      Collections.sort(facilityBenefitsServiceList);
      facility.attributes().services().benefits(facilityBenefitsServiceList);
      facilityEntity
          .services()
          .addAll(convertToSetOfVersionAgnosticStrings(facilityBenefitsServiceList));
      List<Service<DatamartFacility.OtherService>> facilityOtherServiceList =
          new ArrayList<>(facilityOtherServices);
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
      @NonNull FacilityEntity record, DatamartFacility.Services facilityServices) {
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
