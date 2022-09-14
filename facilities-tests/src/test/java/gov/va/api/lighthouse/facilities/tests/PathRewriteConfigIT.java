package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.va.api.health.sentinel.ExpectedResponse;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Slf4j
public class PathRewriteConfigIT {
  private static RequestSpecification requestSpecification() {
    SystemDefinitions.Service svc = systemDefinition().facilities();
    return RestAssured.given().baseUri(svc.url()).port(svc.port()).relaxedHTTPSValidation();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "/facilities/actuator/health",
        "/va_facilities/actuator/health",
        "/services/va_facilities/actuator/health"
      })
  void pathIsRewritten(String path) {
    SystemDefinitions.Service svc = systemDefinition().facilities();
    assertThat(
            ExpectedResponse.of(
                    requestSpecification()
                        .contentType("application/json")
                        .request(Method.GET, svc.url() + path))
                .expect(200)
                .expectValid(HealthCheckStatus.class)
                .status())
        .isEqualTo("UP");
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  private static class HealthCheckStatus {
    @JsonProperty(value = "status")
    private String status;
  }
}
