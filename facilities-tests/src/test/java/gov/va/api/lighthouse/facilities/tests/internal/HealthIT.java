package gov.va.api.lighthouse.facilities.tests.internal;

import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentIn;
import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.ImmutableSet;
import gov.va.api.health.sentinel.Environment;
import gov.va.api.health.sentinel.ExpectedResponse;
import gov.va.api.lighthouse.facilities.tests.SystemDefinitions;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import java.time.Instant;
import java.util.Set;
import lombok.Data;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class HealthIT {

  @BeforeAll
  static void assumeEnvironment() {
    assumeEnvironmentIn(Environment.LOCAL);
  }

  @Test
  void collectorBackendHealth() {
    String path = "collector/health";
    Set<Instant> times = ImmutableSet.of(timeOf(path), timeOf(path), timeOf(path));
    assertThat(times.size()).isLessThan(3);
  }

  @Test
  void reloadHealth() {
    String path = "collection/status";
    Set<Instant> times = ImmutableSet.of(timeOf(path), timeOf(path), timeOf(path));
    assertThat(times.size()).isLessThan(3);
  }

  private Instant timeOf(@NonNull String path) {
    SystemDefinitions.Service svc = systemDefinition().facilities();
    Health health =
        ExpectedResponse.of(
                RestAssured.given()
                    .baseUri(svc.url())
                    .port(svc.port())
                    .relaxedHTTPSValidation()
                    .request(Method.GET, svc.urlWithApiPath() + path))
            .expectValid(Health.class);
    Instant time = health.details().time();
    assertThat(time).isNotNull();
    return time;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  private static final class Health {
    Details details;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  private static final class Details {
    Instant time;
  }
}
