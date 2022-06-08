package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.DatamartFacilitiesJacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.updateServiceUrlPaths;
import static org.apache.commons.lang3.StringUtils.capitalize;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.api.TypeOfService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;

public abstract class BaseCmsOverlayController {
  protected static final ObjectMapper DATAMART_MAPPER = createMapper();

  protected final FacilityRepository facilityRepository;

  private final CmsOverlayRepository cmsOverlayRepository;

  public BaseCmsOverlayController(
      @NonNull FacilityRepository facilityRepository,
      @NonNull CmsOverlayRepository cmsOverlayRepository) {
    this.facilityRepository = facilityRepository;
    this.cmsOverlayRepository = cmsOverlayRepository;
  }

  /** Filter out unrecognized datamart detailed services from overlay. */
  @SneakyThrows
  protected DatamartCmsOverlay filterOutUnrecognizedServicesFromOverlay(
      @NonNull DatamartCmsOverlay overlay) {
    if (ObjectUtils.isNotEmpty(overlay.detailedServices())) {
      overlay.detailedServices(
          overlay.detailedServices().parallelStream()
              .filter(ds -> isRecognizedServiceId(ds.serviceInfo().serviceId()))
              .collect(Collectors.toList()));
    }
    return overlay;
  }

  @SneakyThrows
  protected List<DatamartDetailedService> findServicesToSave(
      CmsOverlayEntity cmsOverlayEntity,
      String id,
      List<DatamartDetailedService> detailedServices,
      ObjectMapper mapper) {
    final List<DatamartDetailedService> ds =
        (detailedServices == null) ? Collections.emptyList() : detailedServices;
    final List<String> overlayServiceIds =
        ds.parallelStream().map(dds -> dds.serviceInfo().serviceId()).collect(Collectors.toList());
    // Detailed services represented in pre-serviceInfo block format that have unrecognized service
    // names will have null serviceInfo block when deserialized.
    final List<DatamartDetailedService> currentDetailedServices =
        cmsOverlayEntity.cmsServices() == null
            ? Collections.emptyList()
            : List.of(
                    mapper.readValue(
                        cmsOverlayEntity.cmsServices(), DatamartDetailedService[].class))
                .parallelStream()
                .filter(dds -> dds.serviceInfo() != null)
                .collect(Collectors.toList());
    final List<DatamartDetailedService> finalDetailedServices = new ArrayList<>();
    finalDetailedServices.addAll(
        currentDetailedServices.parallelStream()
            .filter(
                currentDetailedService ->
                    !overlayServiceIds.contains(currentDetailedService.serviceInfo().serviceId()))
            .collect(Collectors.toList()));
    finalDetailedServices.addAll(
        ds.parallelStream().filter(d -> d.active()).collect(Collectors.toList()));
    updateServiceUrlPaths(id, finalDetailedServices);
    finalDetailedServices.sort(Comparator.comparing(dds -> dds.serviceInfo().serviceId()));
    return finalDetailedServices;
  }

  protected List<DatamartDetailedService> getActiveServicesFromOverlay(
      String id, List<DatamartDetailedService> detailedServices) {
    final List<DatamartDetailedService> activeServices = new ArrayList<>();
    if (detailedServices != null) {
      activeServices.addAll(
          detailedServices.parallelStream().filter(d -> d.active()).collect(Collectors.toList()));
    }
    updateServiceUrlPaths(id, activeServices);
    activeServices.sort(Comparator.comparing(dds -> dds.serviceInfo().serviceId()));
    return activeServices;
  }

  @SneakyThrows
  protected Optional<CmsOverlayEntity> getExistingOverlayEntity(@NonNull FacilityEntity.Pk pk) {
    return cmsOverlayRepository.findById(pk);
  }

  @SneakyThrows
  protected DatamartDetailedService getOverlayDetailedService(
      @NonNull String facilityId, @NonNull String serviceId) {
    List<DatamartDetailedService> detailedServices =
        getOverlayDetailedServices(facilityId).parallelStream()
            .filter(ds -> ds.serviceInfo().serviceId().equals(serviceId))
            .collect(Collectors.toList());
    return detailedServices.isEmpty() ? null : detailedServices.get(0);
  }

  @SneakyThrows
  protected List<DatamartDetailedService> getOverlayDetailedServices(@NonNull String facilityId) {
    FacilityEntity.Pk pk = FacilityEntity.Pk.fromIdString(facilityId);
    Optional<CmsOverlayEntity> existingOverlayEntity = getExistingOverlayEntity(pk);
    if (!existingOverlayEntity.isPresent()) {
      throw new ExceptionsUtils.NotFound(facilityId);
    }
    return CmsOverlayHelper.getDetailedServices(existingOverlayEntity.get().cmsServices());
  }

  /** Determine whether specified service id matches that for service. */
  protected abstract boolean isRecognizedServiceId(String serviceId);

  @SneakyThrows
  protected void updateCmsOverlayData(
      @NonNull Optional<CmsOverlayEntity> existingCmsOverlayEntity,
      String id,
      @NonNull DatamartCmsOverlay overlay) {
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
              .overlayServices(
                  activeServices.parallelStream()
                      .map(dds -> dds.serviceInfo().serviceId())
                      .collect(Collectors.toSet()))
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
        cmsOverlayEntity.overlayServices(
            toSaveDetailedServices.parallelStream()
                .map(dds -> dds.serviceInfo().serviceId())
                .collect(Collectors.toSet()));
      }
      if (overlay.healthCareSystem() != null) {
        cmsOverlayEntity.healthCareSystem(
            CmsOverlayHelper.serializeHealthCareSystem(overlay.healthCareSystem()));
      }
    }
    cmsOverlayRepository.save(cmsOverlayEntity);
  }

  @SneakyThrows
  protected void updateFacilityData(
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

    final Set<DatamartFacility.BenefitsService> facilityBenefitsServices =
        Collections.synchronizedSet(new HashSet<>());
    final Set<DatamartFacility.HealthService> facilityHealthServices =
        Collections.synchronizedSet(new HashSet<>());
    final Set<DatamartFacility.OtherService> facilityOtherServices =
        Collections.synchronizedSet(new HashSet<>());

    if (!toSaveDetailedServices.isEmpty()) {
      final Set<String> detailedServiceIds = Collections.synchronizedSet(new HashSet<>());
      toSaveDetailedServices.parallelStream()
          .forEach(
              service -> {
                if (service
                    .serviceInfo()
                    .serviceId()
                    .equals(DatamartFacility.HealthService.Covid19Vaccine.serviceId())) {
                  // Covid-19 Health facility service
                  detailedServiceIds.add(DatamartFacility.HealthService.Covid19Vaccine.serviceId());
                  if (service.active()) {
                    if (facilityEntity.services() != null) {
                      facilityEntity
                          .services()
                          .add(
                              capitalize(
                                  DatamartFacility.HealthService.Covid19Vaccine.serviceId()));
                    } else {
                      facilityEntity.services(
                          Set.of(
                              capitalize(
                                  DatamartFacility.HealthService.Covid19Vaccine.serviceId())));
                    }

                    facilityHealthServices.add(DatamartFacility.HealthService.Covid19Vaccine);
                  }
                } else {
                  // Update detailed services
                  detailedServiceIds.add(service.serviceInfo().serviceId());
                  // Update facility services
                  if (service.active()) {
                    if (TypeOfService.Benefits.equals(service.serviceInfo().serviceType())) {
                      // Benefits facility services
                      Optional<DatamartFacility.BenefitsService> benefitsService =
                          DatamartFacility.BenefitsService.fromServiceId(
                              service.serviceInfo().serviceId());
                      if (benefitsService.isPresent()) {
                        facilityBenefitsServices.add(benefitsService.get());
                      }
                    } else if (TypeOfService.Health.equals(service.serviceInfo().serviceType())) {
                      // Health (other than Covid-19) facility services
                      Optional<DatamartFacility.HealthService> healthService =
                          DatamartFacility.HealthService.fromServiceId(
                              service.serviceInfo().serviceId());
                      if (healthService.isPresent()) {
                        facilityHealthServices.add(healthService.get());
                      }
                    } else if (TypeOfService.Other.equals(service.serviceInfo().serviceType())) {
                      // Other facility services
                      Optional<DatamartFacility.OtherService> otherService =
                          DatamartFacility.OtherService.fromServiceId(
                              service.serviceInfo().serviceId());
                      if (otherService.isPresent()) {
                        facilityOtherServices.add(otherService.get());
                      }
                    }
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
        // Only add Covid-19 detailed service, if present, to facility attributes
        facility
            .attributes()
            .detailedServices(
                toSaveDetailedServices.isEmpty()
                    ? null
                    : toSaveDetailedServices.parallelStream()
                        .filter(ds -> ds.active())
                        .filter(
                            ds ->
                                DatamartFacility.HealthService.Covid19Vaccine.serviceId()
                                    .equals(ds.serviceInfo().serviceId()))
                        .collect(Collectors.toList()));
      }

      // Update facility benefits services
      if (facility.attributes().services().benefits() != null) {
        facilityBenefitsServices.addAll(facility.attributes().services().benefits());
      }
      List<DatamartFacility.BenefitsService> facilityBenefitsServiceList =
          new ArrayList<>(facilityBenefitsServices);
      Collections.sort(facilityBenefitsServiceList);
      facility.attributes().services().benefits(facilityBenefitsServiceList);

      // Update facility health services
      if (facility.attributes().services().health() != null) {
        facilityHealthServices.addAll(facility.attributes().services().health());
      }
      List<DatamartFacility.HealthService> facilityHealthServiceList =
          new ArrayList<>(facilityHealthServices);
      Collections.sort(facilityHealthServiceList);
      facility.attributes().services().health(facilityHealthServiceList);

      // Update facility other services
      if (facility.attributes().services().other() != null) {
        facilityOtherServices.addAll(facility.attributes().services().other());
      }
      List<DatamartFacility.OtherService> facilityOtherServiceList =
          new ArrayList<>(facilityOtherServices);
      Collections.sort(facilityOtherServiceList);
      facility.attributes().services().other(facilityOtherServiceList);

      facilityEntity.facility(DATAMART_MAPPER.writeValueAsString(facility));
    }

    facilityRepository.save(facilityEntity);
  }
}
