package gov.va.api.lighthouse.facilities.tests.v0;

import static gov.va.api.lighthouse.facilities.tests.FacilitiesRequest.facilitiesRequest;

import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import org.junit.jupiter.api.Test;

public class BulkIT {
  private static final String ALL_PATH = "v0/facilities/all";

  @Test
  void all_csv() {
    facilitiesRequest("text/csv", ALL_PATH, 200);
  }

  @Test
  void all_geoJson() {
    facilitiesRequest("application/geo+json", ALL_PATH, 200)
        .expectValid(GeoFacilitiesResponse.class);
  }

  @Test
  void all_json() {
    facilitiesRequest("application/json", ALL_PATH, 200).expectValid(GeoFacilitiesResponse.class);
  }

  @Test
  void all_noAccept() {
    facilitiesRequest(null, ALL_PATH, 200).expectValid(GeoFacilitiesResponse.class);
  }

  @Test
  void all_vndGeoJson() {
    facilitiesRequest("application/vnd.geo+json", ALL_PATH, 200)
        .expectValid(GeoFacilitiesResponse.class);
  }
}
