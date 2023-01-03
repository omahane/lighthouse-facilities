package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.CLIENT_KEY_DEFAULT;
import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.sentinel.ExpectedResponse;
import gov.va.api.lighthouse.facilities.DatamartFacility;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

@Slf4j
public class CollectorIT {
  private static ExpectedResponse makeRequest(String path, Integer expectedStatus) {
    SystemDefinitions.Service svc = systemDefinition().facilitiesInternal();
    log.info("Expect {} is status code ({})", svc.apiPath() + path, expectedStatus);
    return ExpectedResponse.of(
            RestAssured.given()
                .baseUri(svc.url())
                .port(svc.port())
                .relaxedHTTPSValidation()
                .header("client-key", System.getProperty("client-key", CLIENT_KEY_DEFAULT))
                .request(Method.GET, svc.urlWithApiPath() + path))
        .expect(expectedStatus);
  }

  @Test
  @DisabledIfEnvironmentVariable(
      named = "COLLECTOR_IT_COLLECT_FACILITIES_ENABLED",
      matches = "false")
  void collectFacilities() {
    var items =
        makeRequest("internal/collector/facilities", 200).expectListOf(DatamartFacility.class);
    assertThat(items.size()).isGreaterThan(2000);
  }

  @Test
  void mentalHealth() {
    var items =
        makeRequest("internal/collector/mental-health-contact", 200).expectListOf(Map.class);
    assertThat(items.size()).isGreaterThan(500);
  }

  @Test
  void stopCodes() {
    var items = makeRequest("internal/collector/stop-code", 200).expectListOf(Map.class);
    assertThat(items.size()).isGreaterThan(1000);
  }

  @Test
  void vast() {
    var items = makeRequest("internal/collector/vast", 200).expectListOf(Map.class);
    assertThat(items.size()).isGreaterThan(1000);
  }
}
