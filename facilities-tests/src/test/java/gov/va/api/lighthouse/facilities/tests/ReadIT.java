package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.lighthouse.facilities.tests.FacilitiesRequest.facilitiesRequest;
import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;

import gov.va.api.lighthouse.facilities.api.v0.FacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilityReadResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

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
}
