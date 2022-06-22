package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.DatamartFacilitiesJacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.updateServiceUrlPaths;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.DatamartFacility.PatientWaitTime;
import gov.va.api.lighthouse.facilities.DatamartFacility.WaitTimes;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
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

  private void applyAtcWaitTimeToCmsService(
      DatamartDetailedService cmsService,
      Map<String, DatamartFacility.PatientWaitTime> waitTimeMap,
      LocalDate effectiveDate) {
    String serviceId = cmsService.serviceInfo().serviceId();
    PatientWaitTime patientWaitTime = waitTimeMap.get(serviceId);
    if (patientWaitTime != null) {
      cmsService.waitTime(
          DatamartDetailedService.PatientWaitTime.builder()
              .newPatientWaitTime(patientWaitTime.newPatientWaitTime())
              .establishedPatientWaitTime(patientWaitTime.establishedPatientWaitTime())
              .effectiveDate(effectiveDate)
              .build());
    }
  }

  @SneakyThrows
  private void applyAtcWaitTimeToCmsServices(
      List<DatamartDetailedService> cmsDatamartDetailedServices, String facilityId) {
    FacilityEntity.Pk pk = FacilityEntity.Pk.fromIdString(facilityId);
    Optional<FacilityEntity> opt = facilityRepository.findById(pk);
    if (opt.isEmpty()) {
      // No ATC wait times to process if facility doesn't exist
      return;
    }
    DatamartFacility datamartFacility =
        DATAMART_MAPPER.readValue(opt.get().facility(), DatamartFacility.class);
    WaitTimes atcWaitTimes = datamartFacility.attributes().waitTimes();
    if (atcWaitTimes == null || atcWaitTimes.health() == null) {
      return;
    }
    List<PatientWaitTime> patientWaitTimes = atcWaitTimes.health();
    LocalDate effectiveDate = atcWaitTimes.effectiveDate();
    Map<String, PatientWaitTime> waitTimeMap =
        patientWaitTimes.stream()
            .collect(Collectors.toMap(s -> uncapitalize(s.service().name()), Function.identity()));
    cmsDatamartDetailedServices.stream()
        .forEach(
            cmsService -> applyAtcWaitTimeToCmsService(cmsService, waitTimeMap, effectiveDate));
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
        (detailedServices == null)
            ? Collections.emptyList()
            : Collections.synchronizedList(detailedServices);
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
    final List<DatamartDetailedService> finalDetailedServices =
        Collections.synchronizedList(new ArrayList<>());
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
    final List<DatamartDetailedService> activeServices =
        Collections.synchronizedList(new ArrayList<>());
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
    List<DatamartDetailedService> cmsServices = overlay.detailedServices();
    applyAtcWaitTimeToCmsServices(cmsServices, id);
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
                      .map(dds -> capitalize(dds.serviceInfo().serviceId()))
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
                .map(dds -> capitalize(dds.serviceInfo().serviceId()))
                .collect(Collectors.toSet()));
      }
      if (overlay.healthCareSystem() != null) {
        cmsOverlayEntity.healthCareSystem(
            CmsOverlayHelper.serializeHealthCareSystem(overlay.healthCareSystem()));
      }
    }
    cmsOverlayRepository.save(cmsOverlayEntity);
  }
}
