package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.capitalize;

import com.google.common.collect.Streams;
import gov.va.api.lighthouse.facilities.CmsOverlayEntity;
import gov.va.api.lighthouse.facilities.CmsOverlayHelper;
import gov.va.api.lighthouse.facilities.CmsOverlayRepository;
import gov.va.api.lighthouse.facilities.DatamartCmsOverlay;
import gov.va.api.lighthouse.facilities.DatamartDetailedService;
import gov.va.api.lighthouse.facilities.DatamartFacility;
import gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService;
import gov.va.api.lighthouse.facilities.DatamartFacility.HealthService;
import gov.va.api.lighthouse.facilities.DatamartFacility.OperatingStatus;
import gov.va.api.lighthouse.facilities.DatamartFacility.OtherService;
import gov.va.api.lighthouse.facilities.DatamartFacility.PatientWaitTime;
import gov.va.api.lighthouse.facilities.DatamartFacility.Service;
import gov.va.api.lighthouse.facilities.DatamartFacility.Service.Source;
import gov.va.api.lighthouse.facilities.DatamartFacility.Services;
import gov.va.api.lighthouse.facilities.DatamartFacility.WaitTimes;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CmsOverlayCollector extends BaseCmsOverlayHandler {
  private final CmsOverlayMapper cmsOverlayMapper;

  public CmsOverlayCollector(
      @Autowired CmsOverlayRepository cmsOverlayRepository,
      @Autowired CmsOverlayMapper cmsOverlayMapper) {
    super(cmsOverlayRepository);
    this.cmsOverlayMapper = cmsOverlayMapper;
  }

  /** Method for determining whether Covid service is contained within detailed services. */
  public static boolean containsCovidService(List<DatamartDetailedService> detailedServices) {
    return detailedServices != null
        && detailedServices.parallelStream()
            .anyMatch(
                ds ->
                    ds.serviceInfo().serviceId().equals(HealthService.Covid19Vaccine.serviceId()));
  }

  private static <K, V>
      Collector<AbstractMap.SimpleEntry<K, V>, ?, HashMap<K, V>> convertOverlayToMap() {
    return Collectors.toMap(
        AbstractMap.SimpleEntry::getKey,
        AbstractMap.SimpleEntry::getValue,
        (prev, next) -> next,
        HashMap::new);
  }

  private AbstractMap.SimpleEntry<String, Services> filterCmsOverlayServices(
      CmsOverlayEntity cmsOverlayEntity) {
    List<Service<BenefitsService>> benefitsServices = new ArrayList<>();
    List<Service<HealthService>> healthServices = new ArrayList<>();
    List<Service<OtherService>> otherServices = new ArrayList<>();
    cmsOverlayEntity.overlayServices().stream()
        .forEach(
            s -> {
              if (EnumUtils.isValidEnum(BenefitsService.class, capitalize(s))) {
                final BenefitsService benefitsService = BenefitsService.fromString(capitalize(s));
                final Optional<String> cmsServiceName =
                    cmsOverlayMapper.serviceNameForServiceId(benefitsService.serviceId());
                benefitsServices.add(
                    Service.<BenefitsService>builder()
                        .name(
                            cmsServiceName.isPresent()
                                ? cmsServiceName.get()
                                : benefitsService.name())
                        .serviceType(benefitsService)
                        .source(Source.CMS)
                        .build());
              }
              if (EnumUtils.isValidEnum(HealthService.class, capitalize(s))) {
                final HealthService healthService = HealthService.fromString(capitalize(s));
                final Optional<String> cmsServiceName =
                    cmsOverlayMapper.serviceNameForServiceId(healthService.serviceId());
                healthServices.add(
                    Service.<HealthService>builder()
                        .name(
                            cmsServiceName.isPresent()
                                ? cmsServiceName.get()
                                : healthService.name())
                        .serviceType(healthService)
                        .source(Source.CMS)
                        .build());
              }
              if (EnumUtils.isValidEnum(OtherService.class, capitalize(s))) {
                final OtherService otherService = OtherService.fromString(capitalize(s));
                final Optional<String> cmsServiceName =
                    cmsOverlayMapper.serviceNameForServiceId(otherService.serviceId());
                otherServices.add(
                    Service.<OtherService>builder()
                        .name(
                            cmsServiceName.isPresent() ? cmsServiceName.get() : otherService.name())
                        .serviceType(otherService)
                        .source(Source.CMS)
                        .build());
              }
            });
    if (healthServices.isEmpty() && benefitsServices.isEmpty() && otherServices.isEmpty()) {
      return null;
    }
    return new AbstractMap.SimpleEntry<>(
        cmsOverlayEntity.id().toIdString(),
        Services.builder()
            .benefits(benefitsServices)
            .health(healthServices)
            .other(otherServices)
            .build());
  }

  /** Return a map of facilities that have covid 19 vaccines. This is a V0 utility function. */
  public HashMap<String, Services> getCmsServices() {
    HashMap<String, Services> cmsOverlayServices =
        Streams.stream(cmsOverlayRepository.findAll())
            .map(this::filterCmsOverlayServices)
            .filter(Objects::nonNull)
            .collect(convertOverlayToMap());
    return cmsOverlayServices;
  }

  /** Obtain CMS sourced benefits service with most up-to-date service name. */
  public Set<Service<BenefitsService>> getCmsSourcedBenefitsServices(
      @NonNull List<Service<BenefitsService>> benefitsServices) {
    return benefitsServices.stream()
        .filter(dfhs -> cmsOverlayMapper.serviceNameForServiceId(dfhs.serviceId()).isPresent())
        .map(
            dfhs ->
                Service.<BenefitsService>builder()
                    .name(cmsOverlayMapper.serviceNameForServiceId(dfhs.serviceId()).get())
                    .serviceId(dfhs.serviceId())
                    .serviceType(BenefitsService.fromServiceId(dfhs.serviceId()).get())
                    .source(Service.Source.CMS)
                    .build())
        .collect(toSet());
  }

  /** Obtain CMS sourced health service with most up-to-date service name. */
  public Set<Service<HealthService>> getCmsSourcedHealthServices(
      @NonNull List<Service<HealthService>> healthServices) {
    return healthServices.stream()
        .filter(dfhs -> cmsOverlayMapper.serviceNameForServiceId(dfhs.serviceId()).isPresent())
        .map(
            dfhs ->
                Service.<HealthService>builder()
                    .name(cmsOverlayMapper.serviceNameForServiceId(dfhs.serviceId()).get())
                    .serviceId(dfhs.serviceId())
                    .serviceType(HealthService.fromServiceId(dfhs.serviceId()).get())
                    .source(Service.Source.CMS)
                    .build())
        .collect(toSet());
  }

  /** Obtain CMS sourced other service with most up-to-date service name. */
  public Set<Service<OtherService>> getCmsSourcedOtherServices(
      @NonNull List<Service<OtherService>> otherServices) {
    return otherServices.stream()
        .filter(dfhs -> cmsOverlayMapper.serviceNameForServiceId(dfhs.serviceId()).isPresent())
        .map(
            dfhs ->
                Service.<OtherService>builder()
                    .name(cmsOverlayMapper.serviceNameForServiceId(dfhs.serviceId()).get())
                    .serviceId(dfhs.serviceId())
                    .serviceType(OtherService.fromServiceId(dfhs.serviceId()).get())
                    .source(Service.Source.CMS)
                    .build())
        .collect(toSet());
  }

  /** Load and return map of CMS overlays for each facility id. */
  public HashMap<String, DatamartCmsOverlay> loadAndUpdateCmsOverlays() {
    HashMap<String, DatamartCmsOverlay> overlays =
        Streams.stream(cmsOverlayRepository.findAll())
            .map(this::makeOverlayFromEntity)
            .filter(Objects::nonNull)
            .collect(convertOverlayToMap());
    log.info(
        "Loaded {} overlays from {} db entities", overlays.size(), cmsOverlayRepository.count());
    return overlays;
  }

  private AbstractMap.SimpleEntry<String, DatamartCmsOverlay> makeOverlayFromEntity(
      CmsOverlayEntity cmsOverlayEntity) {
    DatamartCmsOverlay overlay;
    try {
      overlay =
          DatamartCmsOverlay.builder()
              .operatingStatus(
                  CmsOverlayHelper.getOperatingStatus(cmsOverlayEntity.cmsOperatingStatus()))
              .detailedServices(
                  cmsOverlayEntity.cmsServices() != null
                      ? // updateServiceUrlPaths(
                      // cmsOverlayEntity.id().toIdString(),
                      // )
                      CmsOverlayHelper.getDetailedServices(cmsOverlayEntity.cmsServices())
                      : null)
              .healthCareSystem(
                  CmsOverlayHelper.getHealthCareSystem(cmsOverlayEntity.healthCareSystem()))
              .build();
      // Save updates made to overlay with Covid services
      final OperatingStatus operatingStatus = overlay.operatingStatus();
      final List<DatamartDetailedService> detailedServices = overlay.detailedServices();
      if (containsCovidService(detailedServices)) {
        cmsOverlayRepository.save(
            CmsOverlayEntity.builder()
                .id(cmsOverlayEntity.id())
                .cmsOperatingStatus(CmsOverlayHelper.serializeOperatingStatus(operatingStatus))
                .cmsServices(CmsOverlayHelper.serializeDetailedServices(detailedServices))
                .overlayServices(cmsOverlayEntity.overlayServices())
                .healthCareSystem(
                    CmsOverlayHelper.serializeHealthCareSystem(overlay.healthCareSystem()))
                .build());
        log.info(
            "CMS overlay updated for {} facility", sanitize(cmsOverlayEntity.id().toIdString()));
      }
    } catch (Exception e) {
      log.warn(
          "Could not create CmsOverlay from CmsOverlayEntity with id {}",
          cmsOverlayEntity.id().toIdString());
      return null;
    }
    return new AbstractMap.SimpleEntry<>(cmsOverlayEntity.id().toIdString(), overlay);
  }

  /** Rebuild CMS service id to service name mapping. */
  public boolean reload() {
    return cmsOverlayMapper.reload();
  }

  /** Update CMS Detailed Service with wait times data from ATC during reload. */
  public void updateCmsServicesWithAtcWaitTimes(List<DatamartFacility> datamartFacilities) {
    Iterable<CmsOverlayEntity> existingOverlayEntities = cmsOverlayRepository.findAll();
    Map<String, CmsOverlayEntity> overlayEntityMap =
        Streams.stream(existingOverlayEntities)
            .collect(
                Collectors.toMap(
                    cmsOverlayEntity -> cmsOverlayEntity.id().toIdString(), Function.identity()));
    datamartFacilities.stream()
        .filter(df -> overlayEntityMap.containsKey(df.id()))
        .filter(df -> df.attributes().waitTimes() != null)
        .filter(df -> df.attributes().waitTimes().health() != null)
        .forEach(
            datamartFacility -> {
              WaitTimes atcWaitTimes = datamartFacility.attributes().waitTimes();
              List<PatientWaitTime> patientWaitTimes = atcWaitTimes.health();
              LocalDate effectiveDate = atcWaitTimes.effectiveDate();
              Map<String, PatientWaitTime> waitTimeMap =
                  patientWaitTimes.stream()
                      .collect(Collectors.toMap(s -> s.service().serviceId(), Function.identity()));
              CmsOverlayEntity cmsOverlayEntity = overlayEntityMap.get(datamartFacility.id());
              List<DatamartDetailedService> cmsDatamartDetailedServices =
                  Optional.ofNullable(
                          CmsOverlayHelper.getDetailedServices(cmsOverlayEntity.cmsServices()))
                      .orElse(List.of());
              cmsDatamartDetailedServices.stream()
                  .forEach(
                      cmsService -> {
                        String serviceId = cmsService.serviceInfo().serviceId();
                        PatientWaitTime patientWaitTime = waitTimeMap.get(serviceId);
                        if (patientWaitTime != null) {
                          cmsService.waitTime(
                              DatamartDetailedService.PatientWaitTime.builder()
                                  .newPatientWaitTime(patientWaitTime.newPatientWaitTime())
                                  .establishedPatientWaitTime(
                                      patientWaitTime.establishedPatientWaitTime())
                                  .effectiveDate(effectiveDate)
                                  .build());
                        }
                      });
              cmsOverlayEntity.cmsServices(
                  CmsOverlayHelper.serializeDetailedServices(cmsDatamartDetailedServices));
            });
    cmsOverlayRepository.saveAll(overlayEntityMap.values());
  }
}
