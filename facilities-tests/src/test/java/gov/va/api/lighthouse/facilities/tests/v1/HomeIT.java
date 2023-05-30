package gov.va.api.lighthouse.facilities.tests.v1;

import static gov.va.api.lighthouse.facilities.tests.FacilitiesRequest.facilitiesRequest;

import org.junit.jupiter.api.Test;

public class HomeIT {

  @Test
  void openApiJson_docs() {
    facilitiesRequest(null, "docs/v1/api", 200);
  }

  @Test
  void openApiJson_facilities() {
    facilitiesRequest(null, "v1/facilities/openapi.json", 200);
  }
}
