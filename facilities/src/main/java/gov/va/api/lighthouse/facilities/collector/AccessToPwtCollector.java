package gov.va.api.lighthouse.facilities.collector;

import static com.google.common.base.Preconditions.checkState;
import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.UrlFormatHelper.withTrailingSlash;
import static gov.va.api.lighthouse.facilities.collector.LocalDateHelper.sliceToDate;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.upperCase;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/** Access to Pwt Collector. */
@Slf4j
@Component
public class AccessToPwtCollector {
  private final InsecureRestTemplateProvider insecureRestTemplateProvider;

  private final String atpBaseUrl;

  private final ListMultimap<String, AccessToPwtEntry> accessToPwtEntries;

  /** Default Access to Pwt Collector constructor. */
  public AccessToPwtCollector(
      @Autowired @NonNull InsecureRestTemplateProvider insecureRestTemplateProvider,
      @Value("${access-to-pwt.url}") String atpBaseUrl) {
    this.insecureRestTemplateProvider = insecureRestTemplateProvider;
    this.atpBaseUrl = withTrailingSlash(atpBaseUrl);
    this.accessToPwtEntries = loadAccessToPwt();
  }

  private List<AccessToPwtEntry> accessToPwtEntries(@NonNull String facilityId) {
    return accessToPwtEntries.get(trimToEmpty(upperCase(facilityId, Locale.US)));
  }

  LocalDate atpEffectiveDate(@NonNull String facilityId) {
    return accessToPwtEntries(facilityId).stream()
        .map(ape -> sliceToDate(ape.sliceEndDate()))
        .filter(Objects::nonNull)
        .sorted(Comparator.reverseOrder())
        .findFirst()
        .orElse(null);
  }

  @SneakyThrows
  private ListMultimap<String, AccessToPwtEntry> loadAccessToPwt() {
    final Stopwatch watch = Stopwatch.createStarted();
    final ListMultimap<String, AccessToPwtEntry> map = ArrayListMultimap.create();
    try {
      final String url =
          UriComponentsBuilder.fromHttpUrl(atpBaseUrl + "Shep/getRawData")
              .queryParam("location", "*")
              .build()
              .toUriString();
      final ResponseEntity<String> response =
          insecureRestTemplateProvider
              .restTemplate()
              .exchange(url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
      if (response.getStatusCode().is2xxSuccessful()) {
        final List<AccessToPwtEntry> entries =
            createMapper()
                .readValue(response.getBody(), new TypeReference<List<AccessToPwtEntry>>() {});
        for (final AccessToPwtEntry entry : entries) {
          if (entry.facilityId() == null) {
            log.warn("AccessToPwt entry has null facility ID");
            continue;
          }
          map.put(upperCase("vha_" + entry.facilityId(), Locale.US), entry);
        }
        checkState(!entries.isEmpty(), "No AccessToPwt entries");
      } else {
        log.error("Access to Pwt call returned {} HTTP Status", response.getStatusCode().value());
      }
    } catch (final Throwable t) {
      log.error("Unable to load Access to Pwt data:", t);
    } finally {
      log.info(
          "Loading satisfaction scores took {} millis for {} entries",
          watch.stop().elapsed(TimeUnit.MILLISECONDS),
          map.size());
    }
    return ImmutableListMultimap.copyOf(map);
  }

  BigDecimal satisfactionScore(@NonNull String facilityId, String type) {
    return accessToPwtEntries(facilityId).stream()
        .filter(atp -> equalsIgnoreCase(atp.apptTypeName(), type))
        .map(atp -> atp.shepScore())
        .min(Comparator.naturalOrder())
        .orElse(null);
  }
}
