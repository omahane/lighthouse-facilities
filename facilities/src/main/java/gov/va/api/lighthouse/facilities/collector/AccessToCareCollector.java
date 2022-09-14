package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.EmergencyCare;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.UrgentCare;
import static gov.va.api.lighthouse.facilities.collector.LocalDateHelper.sliceToDate;
import static gov.va.api.lighthouse.facilities.collector.Transformers.allBlank;
import static gov.va.api.lighthouse.facilities.collector.Transformers.emptyToNull;
import static gov.va.api.lighthouse.facilities.collector.Transformers.isBlank;
import static java.util.stream.Collectors.toCollection;
import static org.apache.commons.lang3.StringUtils.compareIgnoreCase;

import gov.va.api.lighthouse.facilities.DatamartFacility.HealthService;
import gov.va.api.lighthouse.facilities.DatamartFacility.PatientWaitTime;
import gov.va.api.lighthouse.facilities.DatamartFacility.Service;
import gov.va.api.lighthouse.facilities.DatamartFacility.Service.Source;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.NonNull;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Collector of ATC service data. */
@Component
public class AccessToCareCollector extends BaseAccessToCareHandler {

  /** Default access to care collector constructor. */
  public AccessToCareCollector(
      @Autowired InsecureRestTemplateProvider insecureRestTemplateProvider,
      @Value("${access-to-care.url}") String atcBaseUrl) {
    super(insecureRestTemplateProvider, atcBaseUrl);
  }

  private static BigDecimal waitTimeNumber(BigDecimal waitTime) {
    if (waitTime == null || waitTime.compareTo(new BigDecimal("999")) >= 0) {
      return null;
    }
    return waitTime;
  }

  LocalDate atcEffectiveDate(@NonNull String facilityId) {
    return accessToCareEntries(facilityId).stream()
        .map(ace -> sliceToDate(ace.sliceEndDate()))
        .filter(Objects::nonNull)
        .sorted(Comparator.reverseOrder())
        .findFirst()
        .orElse(null);
  }

  List<Service<HealthService>> servicesHealth(@NonNull String facilityId) {
    final List<Service<HealthService>> services =
        accessToCareEntries(facilityId).stream()
            .map(
                ace -> {
                  final HealthService healthService = healthService(ace);
                  return healthService != null
                      ? Service.<HealthService>builder()
                          .serviceType(healthService)
                          .source(Source.ATC)
                          .build()
                      : null;
                })
            .filter(Objects::nonNull)
            .collect(toCollection(ArrayList::new));
    if (accessToCareEntries(facilityId).stream()
        .anyMatch(ace -> BooleanUtils.isTrue(ace.emergencyCare()))) {
      services.add(
          Service.<HealthService>builder().serviceType(EmergencyCare).source(Source.ATC).build());
    }
    if (accessToCareEntries(facilityId).stream()
        .anyMatch(ace -> BooleanUtils.isTrue(ace.urgentCare()))) {
      services.add(
          Service.<HealthService>builder().serviceType(UrgentCare).source(Source.ATC).build());
    }
    return services;
  }

  private PatientWaitTime waitTime(AccessToCareEntry atc) {
    final var healthService = healthService(atc);

    if (atc == null
        || isBlank(healthService)
        || allBlank(waitTimeNumber(atc.newWaitTime()), waitTimeNumber(atc.estWaitTime()))) {
      return null;
    }
    return PatientWaitTime.builder()
        .service(healthService)
        .newPatientWaitTime(waitTimeNumber(atc.newWaitTime()))
        .establishedPatientWaitTime(waitTimeNumber(atc.estWaitTime()))
        .build();
  }

  List<PatientWaitTime> waitTimesHealth(@NonNull String facilityId) {
    List<PatientWaitTime> results =
        accessToCareEntries(facilityId).stream()
            .map(ace -> waitTime(ace))
            .filter(Objects::nonNull)
            .collect(toCollection(ArrayList::new));
    Collections.sort(
        results, (left, right) -> compareIgnoreCase(left.service().name(), right.service().name()));
    return emptyToNull(results);
  }
}
