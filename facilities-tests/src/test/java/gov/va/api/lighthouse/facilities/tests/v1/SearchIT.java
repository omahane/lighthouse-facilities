package gov.va.api.lighthouse.facilities.tests.v1;

import static gov.va.api.lighthouse.facilities.tests.FacilitiesRequest.facilitiesRequest;
import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;

import gov.va.api.lighthouse.facilities.api.v1.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.tests.v0.RequiresFacilitiesExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RequiresFacilitiesExtension.class)
public class SearchIT {
  @Test
  void searchByBoundingBox() {
    final String bbox = systemDefinition().ids().bbox();
    final String request = "v1" + "/facilities?" + bbox;
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByBoundingBoxWithServices() {
    final String bbox = systemDefinition().ids().bbox();
    final String request = "v1" + "/facilities?" + bbox + "&services[]=PrimaryCare";
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByBoundingBoxWithType() {
    final String bbox = systemDefinition().ids().bbox();
    final String request = "v1" + "/facilities?" + bbox + "&type=health";
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByBoundingBoxWithTypeAndServices() {
    final String bbox = systemDefinition().ids().bbox();
    final String request = "v1" + "/facilities?" + bbox + "&type=health&services[]=PrimaryCare";
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByIds() {
    final String idsCsv = systemDefinition().ids().facilityIdsCsv();
    final String request = "v1" + "/facilities?ids=" + idsCsv;
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByLatLong() {
    final String latitude = systemDefinition().ids().latitude();
    final String longitude = systemDefinition().ids().longitude();
    final String request = "v1" + "/facilities?lat=" + latitude + "&long=" + longitude;
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByLatLongWithIds() {
    final String latitude = systemDefinition().ids().latitude();
    final String longitude = systemDefinition().ids().longitude();
    final String idsCsv = systemDefinition().ids().facilityIdsCsv();
    final String request =
        "v1" + "/facilities?lat=" + latitude + "&long=" + longitude + "&ids=" + idsCsv;
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByLatLongWithServices() {
    final String latitude = systemDefinition().ids().latitude();
    final String longitude = systemDefinition().ids().longitude();
    final String request =
        "v1" + "/facilities?lat=" + latitude + "&long=" + longitude + "&services[]=PrimaryCare";
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByLatLongWithType() {
    final String latitude = systemDefinition().ids().latitude();
    final String longitude = systemDefinition().ids().longitude();
    final String request =
        "v1" + "/facilities?lat=" + latitude + "&long=" + longitude + "&type=health";
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByLatLongWithTypeAndServices() {
    final String latitude = systemDefinition().ids().latitude();
    final String longitude = systemDefinition().ids().longitude();
    final String request =
        "v1"
            + "/facilities?lat="
            + latitude
            + "&long="
            + longitude
            + "&type=health&services[]=PrimaryCare";
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByState() {
    final String state = systemDefinition().ids().state();
    final String request = "v1" + "/facilities?state=" + state;
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByStateWithServices() {
    final String state = systemDefinition().ids().state();
    final String request = "v1" + "/facilities?state=" + state + "&services[]=PrimaryCare";
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByStateWithType() {
    final String state = systemDefinition().ids().state();
    final String request = "v1" + "/facilities?state=" + state + "&type=health";
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByStateWithTypeAndServices() {
    final String state = systemDefinition().ids().state();
    final String request =
        "v1" + "/facilities?state=" + state + "&type=health&services[]=PrimaryCare";
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByVisn() {
    final String visn = systemDefinition().ids().visn();
    final String request = "v1" + "/facilities?visn=" + visn;
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByZip() {
    final String zip = systemDefinition().ids().zip();
    final String request = "v1" + "/facilities?zip=" + zip;
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByZipWithServices() {
    final String zip = systemDefinition().ids().zip();
    final String request = "v1" + "/facilities?zip=" + zip + "&services[]=PrimaryCare";
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByZipWithType() {
    final String zip = systemDefinition().ids().zip();
    final String request = "v1" + "/facilities?zip=" + zip + "&type=health";
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByZipWithTypeAndServices() {
    final String zip = systemDefinition().ids().zip();
    final String request = "v1" + "/facilities?zip=" + zip + "&type=health&services[]=PrimaryCare";
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchWithAllParameters() {
    final String bbox = systemDefinition().ids().bbox();
    final String zip = systemDefinition().ids().zip();
    final String state = systemDefinition().ids().state();
    final String visn = systemDefinition().ids().visn();
    final String latitude = systemDefinition().ids().latitude();
    final String longitude = systemDefinition().ids().longitude();
    final String idsCsv = systemDefinition().ids().facilityIdsCsv();
    final boolean mobile = systemDefinition().ids().mobile();
    final String request =
        String.format(
            "v1/facilities?%s&%s&%s&%s&%s&%s&%s&%s&type=health&services[]=PrimaryCare",
            bbox, zip, state, visn, latitude, longitude, idsCsv, mobile);
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }
}
