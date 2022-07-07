package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.lighthouse.facilities.tests.FacilitiesRequest.facilitiesRequest;
import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.CLIENT_KEY_DEFAULT;
import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;

import gov.va.api.health.sentinel.ExpectedResponse;
import gov.va.api.lighthouse.facilities.api.v0.FacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilityReadResponse;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(RequiresFacilitiesExtension.class)
public class ReadIT {
  private static String readPath() {
    return "v0/facilities/" + systemDefinition().ids().facility();
  }

  @Test
  void readById_geoJson() {
    facilitiesRequest("application/geo+json", readPath(), 200)
        .expectValid(GeoFacilityReadResponse.class);
  }

  @Test
  void readById_json() {
    facilitiesRequest("application/json", readPath(), 200).expectValid(FacilityReadResponse.class);
  }

  @Test
  void readById_noAccept() {
    // default to application/json
    facilitiesRequest(null, readPath(), 200).expectValid(FacilityReadResponse.class);
  }

  @Test
  void readById_vndGeoJson() {
    facilitiesRequest("application/vnd.geo+json", readPath(), 200)
        .expectValid(GeoFacilityReadResponse.class);
  }

  /** To be removed. */
  @Order(1)
  @Test
  void reload() {
    SystemDefinitions.Service svcInternal = systemDefinition().facilitiesInternal();
    ExpectedResponse.of(
            RestAssured.given()
                .baseUri(svcInternal.url())
                .port(svcInternal.port())
                .relaxedHTTPSValidation()
                .header("client-key", System.getProperty("client-key", CLIENT_KEY_DEFAULT))
                .request(Method.GET, svcInternal.urlWithApiPath() + "internal/management/reload"))
        .expect(200);
  }
}
