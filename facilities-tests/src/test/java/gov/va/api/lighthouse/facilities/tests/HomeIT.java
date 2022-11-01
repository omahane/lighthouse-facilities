package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.lighthouse.facilities.tests.FacilitiesRequest.facilitiesRequest;

import org.junit.jupiter.api.Test;

public class HomeIT {
  @Test
  void metadata() {
    facilitiesRequest(null, "metadata", 200);
  }

  @Test
  void openApiJson_docs() {
    facilitiesRequest(null, "docs/v0/api", 200);
  }

  @Test
  void openApiJson_facilities() {
    facilitiesRequest(null, "v0/facilities/openapi.json", 200);
  }
}
