package gov.va.api.lighthouse.facilities.collector;

import static com.google.common.base.Preconditions.checkState;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.Audiology;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.Cardiology;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.Dermatology;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.EmergencyCare;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.Gastroenterology;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.Gynecology;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.MentalHealth;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.Ophthalmology;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.Optometry;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.Orthopedics;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.PrimaryCare;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.SpecialtyCare;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.UrgentCare;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.Urology;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.WomensHealth;
import static gov.va.api.lighthouse.facilities.api.UrlFormatHelper.withTrailingSlash;
import static gov.va.api.lighthouse.facilities.collector.LocalDateHelper.sliceToDate;
import static gov.va.api.lighthouse.facilities.collector.Transformers.allBlank;
import static gov.va.api.lighthouse.facilities.collector.Transformers.emptyToNull;
import static gov.va.api.lighthouse.facilities.collector.Transformers.isBlank;
import static java.util.stream.Collectors.toCollection;
import static org.apache.commons.lang3.StringUtils.compareIgnoreCase;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.upperCase;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.DatamartFacility.HealthService;
import gov.va.api.lighthouse.facilities.DatamartFacility.PatientWaitTime;
import gov.va.api.lighthouse.facilities.DatamartFacility.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/** Collector of ATC service data. */
@Slf4j
@Component
public class AccessToCareCollector {
  private final InsecureRestTemplateProvider insecureRestTemplateProvider;

  private final String atcBaseUrl;

  private final Map<String, HealthService> healthServices;

  private final ListMultimap<String, AccessToCareEntry> accessToCareEntries;

  private final Map<String, String> serviceIdToServiceNameMapping;

  /** Default access to care collector constructor. */
  public AccessToCareCollector(
      @Autowired @NonNull InsecureRestTemplateProvider insecureRestTemplateProvider,
      @Value("${access-to-care.url}") String atcBaseUrl) {
    this.insecureRestTemplateProvider = insecureRestTemplateProvider;
    this.atcBaseUrl = withTrailingSlash(atcBaseUrl);
    this.healthServices = initHealthServicesMap();
    this.accessToCareEntries = loadAccessToCare();
    this.serviceIdToServiceNameMapping = buildServiceIdToServiceNameMapping(accessToCareEntries);
  }

  private static Map<String, HealthService> initHealthServicesMap() {
    final Map<String, HealthService> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    map.put("AUDIOLOGY", Audiology);
    map.put("CARDIOLOGY", Cardiology);
    map.put("WOMEN'S HEALTH", WomensHealth);
    map.put("DERMATOLOGY", Dermatology);
    map.put("GASTROENTEROLOGY", Gastroenterology);
    map.put("OB/GYN", Gynecology);
    map.put("MENTAL HEALTH INDIVIDUAL", MentalHealth);
    map.put("OPHTHALMOLOGY", Ophthalmology);
    map.put("OPTOMETRY", Optometry);
    map.put("ORTHOPEDICS", Orthopedics);
    map.put("PRIMARY CARE", PrimaryCare);
    map.put("SPECIALTY CARE", SpecialtyCare);
    map.put("UROLOGY", Urology);
    return ImmutableMap.copyOf(map);
  }

  private static BigDecimal waitTimeNumber(BigDecimal waitTime) {
    if (waitTime == null || waitTime.compareTo(new BigDecimal("999")) >= 0) {
      return null;
    }
    return waitTime;
  }

  private List<AccessToCareEntry> accessToCareEntries(@NonNull String facilityId) {
    return accessToCareEntries.get(trimToEmpty(upperCase(facilityId, Locale.US)));
  }

  LocalDate atcEffectiveDate(@NonNull String facilityId) {
    return accessToCareEntries(facilityId).stream()
        .map(ace -> sliceToDate(ace.sliceEndDate()))
        .filter(Objects::nonNull)
        .sorted(Comparator.reverseOrder())
        .findFirst()
        .orElse(null);
  }

  private Map<String, String> buildServiceIdToServiceNameMapping(
      @NonNull ListMultimap<String, AccessToCareEntry> accessToCareEntries) {
    final Map<String, String> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    accessToCareEntries.values().stream()
        .filter(Objects::nonNull)
        .forEach(
            ace -> {
              final HealthService healthService = healthService(ace);
              if (healthService != null) {
                map.put(healthService.serviceId(), ace.apptTypeName());
              } else {
                log.info("No health service mapping found for [ {} ] ATC service", ace.apptTypeName());
              }
            });
    return ImmutableMap.copyOf(map);
  }

  HealthService healthService(AccessToCareEntry atc) {
    return atc == null
        ? null
        : healthServices.get(trimToEmpty(upperCase(atc.apptTypeName(), Locale.US)));
  }

  @SneakyThrows
  private ListMultimap<String, AccessToCareEntry> loadAccessToCare() {
    ListMultimap<String, AccessToCareEntry> map = ArrayListMultimap.create();
    try {
      final Stopwatch totalWatch = Stopwatch.createStarted();
      String url =
          UriComponentsBuilder.fromHttpUrl(atcBaseUrl + "atcapis/v1.1/patientwaittimes")
              .build()
              .toUriString();
      String response =
          insecureRestTemplateProvider
              .restTemplate()
              .exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class)
              .getBody();
      List<AccessToCareEntry> entries =
          JacksonConfig.createMapper()
              .readValue(response, new TypeReference<List<AccessToCareEntry>>() {});
      entries.stream()
          .forEach(
              entry -> {
                if (entry.facilityId() == null) {
                  log.warn("AccessToCare entry has null facility ID");
                } else {
                  map.put(upperCase("vha_" + entry.facilityId(), Locale.US), entry);
                }
              });
      log.info(
          "Loading patient wait times took {} millis for {} entries",
          totalWatch.stop().elapsed(TimeUnit.MILLISECONDS),
          entries.size());
      checkState(!entries.isEmpty(), "No AccessToCare entries");
    } catch (final Exception ex) {
      log.error("Issue with loading Access To Care data", ex);
      throw new CollectorExceptions.AccessToCareCollectorException(ex);
    }
    return ImmutableListMultimap.copyOf(map);
  }

  public Optional<String> serviceName(@NonNull String serviceId) {
    return Optional.ofNullable(serviceIdToServiceNameMapping.get(serviceId));
  }

  List<Service<HealthService>> servicesHealth(@NonNull String facilityId) {
    final List<Service<HealthService>> services =
        accessToCareEntries(facilityId).stream()
            .map(
                ace -> {
                  final HealthService healthService = healthService(ace);
                  return healthService != null
                      ? Service.<HealthService>builder().serviceType(healthService).build()
                      : null;
                })
            .filter(Objects::nonNull)
            .collect(toCollection(ArrayList::new));
    if (accessToCareEntries(facilityId).stream()
        .anyMatch(ace -> BooleanUtils.isTrue(ace.emergencyCare()))) {
      services.add(Service.<HealthService>builder().serviceType(EmergencyCare).build());
    }
    if (accessToCareEntries(facilityId).stream()
        .anyMatch(ace -> BooleanUtils.isTrue(ace.urgentCare()))) {
      services.add(Service.<HealthService>builder().serviceType(UrgentCare).build());
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
