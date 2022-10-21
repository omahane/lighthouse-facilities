package gov.va.api.lighthouse.facilities.collector;

import static com.google.common.base.Preconditions.checkState;
import static gov.va.api.lighthouse.facilities.api.UrlFormatHelper.withTrailingSlash;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.upperCase;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.DatamartFacility;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public abstract class BaseAccessToCareHandler implements IsAtcAware {
  private final InsecureRestTemplateProvider insecureRestTemplateProvider;

  private final String atcBaseUrl;

  protected Map<String, String> serviceIdToServiceNameMapping;

  private ListMultimap<String, AccessToCareEntry> accessToCareEntries;

  /** Default constructor for handling ATC service data. */
  public BaseAccessToCareHandler(
      @NonNull InsecureRestTemplateProvider insecureRestTemplateProvider,
      @NonNull String atcBaseUrl) {
    this.insecureRestTemplateProvider = insecureRestTemplateProvider;
    this.atcBaseUrl = withTrailingSlash(atcBaseUrl);
    reload();
  }

  protected List<AccessToCareEntry> accessToCareEntries(@NonNull String facilityId) {
    return accessToCareEntries.get(trimToEmpty(upperCase(facilityId, Locale.US)));
  }

  private ImmutableMap<String, String> buildServiceIdToServiceNameMapping(
      @NonNull ListMultimap<String, AccessToCareEntry> accessToCareEntries) {
    final Map<String, String> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    accessToCareEntries.values().stream()
        .filter(Objects::nonNull)
        .forEach(
            ace -> {
              final DatamartFacility.HealthService healthService = healthService(ace);
              if (healthService != null) {
                map.put(healthService.serviceId(), ace.apptTypeName());
              } else {
                log.info(
                    "No health service mapping found for [ {} ] ATC service", ace.apptTypeName());
              }
            });
    return ImmutableMap.copyOf(map);
  }

  protected DatamartFacility.HealthService healthService(AccessToCareEntry atc) {
    return atc == null
        ? null
        : HEALTH_SERVICES.get(trimToEmpty(upperCase(atc.apptTypeName(), Locale.US)));
  }

  private ImmutableListMultimap<String, AccessToCareEntry> loadAccessToCare() {
    ListMultimap<String, AccessToCareEntry> map = ArrayListMultimap.create();
    try {
      final Stopwatch totalWatch = Stopwatch.createStarted();
      String url =
          UriComponentsBuilder.fromHttpUrl(atcBaseUrl + "atcapis/v1.1/patientwaittimes")
              .build()
              .toUriString();
      final ResponseEntity<String> response =
          insecureRestTemplateProvider
              .restTemplate()
              .exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
      if (response.getStatusCode() == HttpStatus.OK) {
        List<AccessToCareEntry> entries =
            JacksonConfig.createMapper()
                .readValue(response.getBody(), new TypeReference<List<AccessToCareEntry>>() {});
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
      }
    } catch (final Exception ex) {
      log.error("Unable to load Access To Care data:", ex);
    }
    return ImmutableListMultimap.copyOf(map);
  }

  /** Reload ATC entries and rebuild service id to service name mapping. */
  public boolean reload() {
    accessToCareEntries = loadAccessToCare();
    serviceIdToServiceNameMapping = buildServiceIdToServiceNameMapping(accessToCareEntries);
    return true;
  }
}
