package gov.va.api.lighthouse.facilities.tests.v1;

import static gov.va.api.lighthouse.facilities.tests.FacilitiesRequest.facilitiesRequest;

import gov.va.api.lighthouse.facilities.api.v1.FacilitiesResponse;
import org.junit.jupiter.api.Test;

public class BulkIT {
  private static final String ALL_PATH = "v1/facilities";

  @Test
  void all_csv() {
    facilitiesRequest("text/csv", ALL_PATH, 200);
  }

  @Test
  void all_json() {
    facilitiesRequest("application/json", ALL_PATH, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void all_noAccept() {
    facilitiesRequest(null, ALL_PATH, 200).expectValid(FacilitiesResponse.class);
  }
}
