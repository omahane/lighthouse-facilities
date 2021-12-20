package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentNotIn;
import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;

import gov.va.api.health.sentinel.Environment;
import gov.va.api.health.sentinel.ExpectedResponse;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;

public class InternalProtectionIT {
  private static RequestSpecification requestSpecification() {
    SystemDefinitions.Service svc = systemDefinition().facilitiesInternal();
    return RestAssured.given().baseUri(svc.url()).port(svc.port()).relaxedHTTPSValidation();
  }

  @Test
  void deleteCmsOverlay() {
    SystemDefinitions.Service svc = systemDefinition().facilitiesInternal();
    ExpectedResponse.of(
            requestSpecification()
                .request(
                    Method.DELETE,
                    svc.urlWithApiPath() + "internal/management/facilities/{id}/cms-overlay",
                    "vba_NOPE"))
        .expect(401);
  }

  @Test
  void deleteFacility() {
    SystemDefinitions.Service svc = systemDefinition().facilitiesInternal();
    ExpectedResponse.of(
            requestSpecification()
                .request(
                    Method.DELETE,
                    svc.urlWithApiPath() + "internal/management/facilities/{id}",
                    "xxx_NOPE"))
        .expect(401);
  }

  @Test
  void getAllBandNames() {
    SystemDefinitions.Service svc = systemDefinition().facilitiesInternal();
    ExpectedResponse.of(
            requestSpecification()
                .request(Method.GET, svc.urlWithApiPath() + "internal/management/bands"))
        .expect(401);
  }

  @Test
  void getBand() {
    SystemDefinitions.Service svc = systemDefinition().facilitiesInternal();
    ExpectedResponse.of(
            requestSpecification()
                .request(
                    Method.GET, svc.urlWithApiPath() + "internal/management/bands/{name}", "NOPE"))
        .expect(401);
  }

  @Test
  void reload() {
    // In case endpoint is not secure
    // Don't reload in SLA'd environments
    assumeEnvironmentNotIn(Environment.LAB, Environment.PROD);
    SystemDefinitions.Service svc = systemDefinition().facilitiesInternal();
    ExpectedResponse.of(
            requestSpecification()
                .request(Method.GET, svc.urlWithApiPath() + "internal/management/reload"))
        .expect(401);
  }
}
