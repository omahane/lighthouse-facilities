package gov.va.api.lighthouse.facilities.tests.v0;

import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.CLIENT_KEY_DEFAULT;
import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;

import gov.va.api.lighthouse.facilities.tests.SystemDefinitions;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

@Slf4j
public class RequiresFacilitiesExtension implements BeforeAllCallback {
  private static boolean loaded;

  private static boolean isTestFacilityAvailable() {
    var response =
        requestSpecification()
            .request(
                Method.GET,
                systemDefinition().facilities().urlWithApiPath()
                    + "v0/facilities/"
                    + systemDefinition().ids().facility());
    return response.statusCode() == 200;
  }

  /** Statically synchronized to manage state for the entire test suite. */
  @Synchronized
  private static void loadIfNecessary() {
    if (loaded) {
      return;
    }
    if (!isTestFacilityAvailable()) {
      log.info("Facilities do not appear to be loaded. Loading now.");
      reloadFacilities();
    }
    loaded = true;
  }

  private static void reloadFacilities() {
    SystemDefinitions.Service svc = systemDefinition().facilitiesInternal();
    var response =
        requestSpecification()
            .header("client-key", System.getProperty("client-key", CLIENT_KEY_DEFAULT))
            .log()
            .uri()
            .request(Method.GET, svc.urlWithApiPath() + "internal/management/reload");
    if (response.statusCode() != 200) {
      log.warn(
          "Facility loading appears to have failed with status {}\n{}",
          response.statusCode(),
          response.getBody().prettyPrint());
    }
  }

  private static RequestSpecification requestSpecification() {
    SystemDefinitions.Service svc = systemDefinition().facilities();
    return RestAssured.given().baseUri(svc.url()).port(svc.port()).relaxedHTTPSValidation();
  }

  @Override
  public void beforeAll(ExtensionContext context) {
    loadIfNecessary();
  }
}
