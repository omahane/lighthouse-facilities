package gov.va.api.lighthouse.facilities.tests.v1;

import static gov.va.api.lighthouse.facilities.tests.FacilitiesRequest.facilitiesRequest;
import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;

import gov.va.api.lighthouse.facilities.api.v1.NearbyResponse;
import org.junit.jupiter.api.Test;

public class NearbyIT {
  @Test
  void searchByLatLong() {
    final String latitude = systemDefinition().ids().latitude();
    final String longitude = systemDefinition().ids().longitude();
    final String request = "v1/nearby?lat=" + latitude + "&lng=" + longitude;
    facilitiesRequest("application/json", request, 200).expectValid(NearbyResponse.class);
  }

  @Test
  void searchByLatLongWithDriveTime() {
    final String latitude = systemDefinition().ids().latitude();
    final String longitude = systemDefinition().ids().longitude();
    final String request = "v1/nearby?lat=" + latitude + "&lng=" + longitude + "&drive_time=90";
    facilitiesRequest("application/json", request, 200).expectValid(NearbyResponse.class);
  }
}
