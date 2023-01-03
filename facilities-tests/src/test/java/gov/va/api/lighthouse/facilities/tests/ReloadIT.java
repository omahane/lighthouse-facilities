package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentIn;
import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.CLIENT_KEY_DEFAULT;
import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;

import gov.va.api.health.sentinel.Environment;
import gov.va.api.health.sentinel.ExpectedResponse;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;

public class ReloadIT {
  private static RequestSpecification requestSpecification() {
    SystemDefinitions.Service svc = systemDefinition().facilitiesInternal();
    return RestAssured.given()
        .baseUri(svc.url())
        .port(svc.port())
        .relaxedHTTPSValidation()
        .header("client-key", System.getProperty("client-key", CLIENT_KEY_DEFAULT));
  }

  @Test
  void reload() {
    assumeEnvironmentIn(Environment.LOCAL);
    SystemDefinitions.Service svc = systemDefinition().facilitiesInternal();
    ExpectedResponse.of(
            requestSpecification()
                .request(Method.GET, svc.urlWithApiPath() + "internal/management/reload"))
        .expect(200);
  }
}
