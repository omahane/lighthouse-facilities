package gov.va.api.lighthouse.facilities.tests.v1;

import static gov.va.api.lighthouse.facilities.tests.FacilitiesRequest.facilitiesRequest;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

public class HealthCheckIT {

  @Test
  void healthCheckEndpoint() {
    facilitiesRequest("application/json", "v1/actuator/health", 200)
        .response()
        .then()
        .body("status", equalTo("UP"));
  }
}
