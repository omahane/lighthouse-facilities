package gov.va.api.lighthouse.facilities;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.web.context.request.RequestContextHolder.setRequestAttributes;

import gov.va.api.lighthouse.facilities.collector.InsecureRestTemplateProvider;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;

@ExtendWith(MockitoExtension.class)
public class HealthControllerTest {
  @Mock FacilityRepository repository;

  @Mock InsecureRestTemplateProvider insecureRestTemplateProvider;

  @Mock JdbcTemplate jdbcTemplate;

  @Mock RestTemplate restTemplate;

  @SuppressWarnings("unchecked")
  private static void assertStatus(Health overallStatus, ExpectedStatus expected) {
    Map<String, Status> serviceStatus =
        ((List<Health>) overallStatus.getDetails().get("downstreamServices"))
            .stream()
                .collect(Collectors.toMap(h -> h.getStatus().getDescription(), Health::getStatus));
    assertThat(serviceStatus.get("Access to Care").getCode()).isEqualTo(expected.accessToCare());
    assertThat(serviceStatus.get("Access to PWT").getCode()).isEqualTo(expected.accessToPwt());
    assertThat(serviceStatus.get("State Cemeteries").getCode())
        .isEqualTo(expected.stateCemeteries());
  }

  private static ResponseEntity<String> notOk() {
    return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
  }

  private static ResponseEntity<String> ok() {
    return new ResponseEntity<>("[{}]", HttpStatus.OK);
  }

  private HealthController _controller() {
    return new HealthController(
        repository,
        insecureRestTemplateProvider,
        jdbcTemplate,
        "http://atc",
        "http://atp",
        "http://statecems");
  }

  @Test
  void collectionStatusHealth_clearCache() {
    _controller().clearCollectionStatusScheduler();
  }

  @Test
  void collectionStatusHealth_healthy() {
    when(repository.findLastUpdated()).thenReturn(Instant.now());
    ResponseEntity<Health> actual = _controller().collectionStatusHealth();
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(actual.getBody().getStatus()).isEqualTo(Status.UP);
  }

  @Test
  void collectionStatusHealth_nullLastUpdate() {
    when(repository.findLastUpdated()).thenReturn(null);
    ResponseEntity<Health> actual = _controller().collectionStatusHealth();
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    assertThat(actual.getBody().getStatus()).isEqualTo(Status.DOWN);
  }

  @Test
  void collectionStatusHealth_unhealthy() {
    when(repository.findLastUpdated()).thenReturn(Instant.parse("2020-01-20T02:20:00Z"));
    ResponseEntity<Health> actual = _controller().collectionStatusHealth();
    assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    assertThat(actual.getBody().getStatus()).isEqualTo(Status.DOWN);
  }

  @Test
  @SneakyThrows
  void collectorBackendHealth_allHealthyBackendServicesReturns200AndHealthWithUpStatuses() {
    setRequestAttributes(mock(RequestAttributes.class));
    when(insecureRestTemplateProvider.restTemplate()).thenReturn(restTemplate);
    when(restTemplate.exchange(
            startsWith("http://atc"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenReturn(ok());
    when(restTemplate.exchange(
            startsWith("http://atp"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenReturn(ok());
    when(restTemplate.exchange(
            startsWith("http://statecems"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(ok());
    when(jdbcTemplate.queryForObject(any(String.class), eq(Timestamp.class)))
        .thenReturn(Timestamp.from(Instant.now()));
    ResponseEntity<Health> response = _controller().collectorBackendHealth();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertStatus(
        requireNonNull(response.getBody()),
        ExpectedStatus.builder()
            .accessToCare("UP")
            .accessToPwt("UP")
            .stateCemeteries("UP")
            .lastUpdated("UP")
            .build());
  }

  @Test
  void collectorBackendHealth_clearCache() {
    _controller().clearCollectorBackendHealthScheduler();
  }

  @Test
  @SneakyThrows
  void
      collectorBackendHealth_oneOrMoreUnhealthyBackendServicesReturns503AndHealthWithDownStatuses() {
    setRequestAttributes(mock(RequestAttributes.class));
    when(insecureRestTemplateProvider.restTemplate()).thenReturn(restTemplate);
    when(restTemplate.exchange(
            startsWith("http://atc"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenReturn(notOk());
    when(restTemplate.exchange(
            startsWith("http://atp"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenReturn(notOk());
    when(restTemplate.exchange(
            startsWith("http://statecems"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(ok());
    when(jdbcTemplate.queryForObject(any(String.class), eq(Timestamp.class)))
        .thenReturn(Timestamp.from(Instant.now().minus(48, ChronoUnit.HOURS)));
    ResponseEntity<Health> response = _controller().collectorBackendHealth();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    assertStatus(
        requireNonNull(response.getBody()),
        ExpectedStatus.builder()
            .accessToCare("DOWN")
            .accessToPwt("DOWN")
            .stateCemeteries("UP")
            .lastUpdated("DOWN")
            .build());
    when(jdbcTemplate.queryForObject(any(String.class), eq(Timestamp.class))).thenReturn(null);
    response = _controller().collectorBackendHealth();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    assertStatus(
        requireNonNull(response.getBody()),
        ExpectedStatus.builder()
            .accessToCare("DOWN")
            .accessToPwt("DOWN")
            .stateCemeteries("UP")
            .build());
  }

  @Test
  @SneakyThrows
  void collectorBackendHealth_servicesUnreachable() {
    setRequestAttributes(mock(RequestAttributes.class));
    when(insecureRestTemplateProvider.restTemplate()).thenReturn(restTemplate);
    when(restTemplate.exchange(
            startsWith("http://atc"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenThrow(new ResourceAccessException("I/O error on GET request"));
    when(restTemplate.exchange(
            startsWith("http://atp"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenThrow(new ResourceAccessException("I/O error on GET request"));
    when(restTemplate.exchange(
            startsWith("http://statecems"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(ok());
    when(jdbcTemplate.queryForObject(any(String.class), eq(Timestamp.class)))
        .thenReturn(Timestamp.from(Instant.now().minus(48, ChronoUnit.HOURS)));
    ResponseEntity<Health> response = _controller().collectorBackendHealth();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    assertStatus(
        requireNonNull(response.getBody()),
        ExpectedStatus.builder()
            .accessToCare("DOWN")
            .accessToPwt("DOWN")
            .stateCemeteries("UP")
            .lastUpdated("DOWN")
            .build());
  }

  @Test
  @SneakyThrows
  void exceptions() {
    Method reloadLastUpdatedMethod =
        HealthController.class.getDeclaredMethod("testReloadLastUpdated", Instant.class);
    reloadLastUpdatedMethod.setAccessible(true);
    Instant nullInstant = null;
    assertThrows(
        NullPointerException.class, () -> reloadLastUpdatedMethod.invoke(null, nullInstant));
    Method etlLastUpdatedMethod =
        HealthController.class.getDeclaredMethod("testEtlLastUpdated", Instant.class);
    etlLastUpdatedMethod.setAccessible(true);
    assertThrows(NullPointerException.class, () -> etlLastUpdatedMethod.invoke(null, nullInstant));
    Method withTrailingSlashMethod =
        HealthController.class.getDeclaredMethod("withTrailingSlash", String.class);
    withTrailingSlashMethod.setAccessible(true);
    String nullUrl = null;
    assertThatThrownBy(() -> withTrailingSlashMethod.invoke(null, nullUrl))
        .isInstanceOf(InvocationTargetException.class)
        .hasCause(new NullPointerException("url is marked non-null but is null"));
  }

  @Test
  void healthResourceAccessException() {
    setRequestAttributes(mock(RequestAttributes.class));
    when(insecureRestTemplateProvider.restTemplate()).thenReturn(restTemplate);
    when(restTemplate.exchange(
            startsWith("http://atc"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenThrow(ResourceAccessException.class);
    when(restTemplate.exchange(
            startsWith("http://atp"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenReturn(ok());
    when(restTemplate.exchange(
            startsWith("http://statecems"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(ok());
    when(jdbcTemplate.queryForObject(any(String.class), eq(Timestamp.class)))
        .thenReturn(Timestamp.from(Instant.now()));
    ResponseEntity<Health> response = _controller().collectorBackendHealth();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
  }

  @Test
  void stateCemeteriesAccessException() {
    setRequestAttributes(mock(RequestAttributes.class));
    when(insecureRestTemplateProvider.restTemplate()).thenReturn(restTemplate);
    when(restTemplate.exchange(
            startsWith("http://atc"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenReturn(ok());
    when(restTemplate.exchange(
            startsWith("http://atp"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenReturn(ok());
    when(restTemplate.exchange(
            startsWith("http://statecems"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
        .thenThrow(ResourceAccessException.class);
    ResponseEntity<Health> response = _controller().collectorBackendHealth();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
  }

  @Test
  @SneakyThrows
  void verifyMissingTrailingSlashAppended() {
    String urlMissingTrailingSlash = "https://developer.va.gov";
    String urlWithTrailingSlash = "https://developer.va.gov/";
    Method withTrailingSlashMethod =
        HealthController.class.getDeclaredMethod("withTrailingSlash", String.class);
    withTrailingSlashMethod.setAccessible(true);
    HealthController controller =
        new HealthController(
            repository,
            insecureRestTemplateProvider,
            jdbcTemplate,
            "http://atc",
            "http://atp",
            "http://cemeteries");
    assertThat(withTrailingSlashMethod.invoke(controller, urlMissingTrailingSlash))
        .isEqualTo(urlWithTrailingSlash);
    assertThat(withTrailingSlashMethod.invoke(controller, urlWithTrailingSlash))
        .isEqualTo(urlWithTrailingSlash);
    assertThat(withTrailingSlashMethod.invoke(controller, urlMissingTrailingSlash))
        .isEqualTo(urlWithTrailingSlash);
    assertThat(withTrailingSlashMethod.invoke(controller, urlWithTrailingSlash))
        .isEqualTo(urlWithTrailingSlash);
  }

  @Test
  @SneakyThrows
  void withTrailingSlash() {
    Method withTrailingSlashMethod =
        HealthController.class.getDeclaredMethod("withTrailingSlash", String.class);
    withTrailingSlashMethod.setAccessible(true);
    assertThat(withTrailingSlashMethod.invoke(_controller(), "http://foo.bar"))
        .isEqualTo("http://foo.bar/");
    assertThat(withTrailingSlashMethod.invoke(_controller(), "http://foo.bar/"))
        .isEqualTo("http://foo.bar/");
  }

  @Value
  @Builder
  private static final class ExpectedStatus {
    String accessToCare;

    String accessToPwt;

    String stateCemeteries;

    String lastUpdated;
  }
}
