package gov.va.api.lighthouse.facilities.tests.v0;

import static gov.va.api.lighthouse.facilities.tests.FacilitiesRequest.facilitiesRequest;
import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import gov.va.api.lighthouse.facilities.api.v0.ApiError;
import gov.va.api.lighthouse.facilities.api.v0.NearbyResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class NearbyIT {
  @Test
  void searchByAddress() {
    final String errorResponse =
        "Bing error: Search by address is not supported, please use [lat,lng]";
    final String streetAddress = systemDefinition().ids().streetAddress();
    final String city = systemDefinition().ids().city();
    final String state = systemDefinition().ids().state();
    final String zip = systemDefinition().ids().zip();
    final String request =
        "v0/nearby?street_address="
            + streetAddress
            + "&city="
            + city
            + "&state="
            + state
            + "&zip="
            + zip;
    assertThat(
            facilitiesRequest("application/json", request, 400)
                .expectValid(ApiError.class)
                .errors()
                .get(0)
                .detail())
        .isEqualTo(errorResponse);
  }

  @Test
  void searchByAddressWithDriveTime() {
    final String errorResponse =
        "Bing error: Search by address is not supported, please use [lat,lng]";
    final String streetAddress = systemDefinition().ids().streetAddress();
    final String city = systemDefinition().ids().city();
    final String state = systemDefinition().ids().state();
    final String zip = systemDefinition().ids().zip();
    final String request =
        "v0/nearby?street_address="
            + streetAddress
            + "&city="
            + city
            + "&state="
            + state
            + "&zip="
            + zip
            + "&drive_time=90";
    assertThat(
            facilitiesRequest("application/json", request, 400)
                .expectValid(ApiError.class)
                .errors()
                .get(0)
                .detail())
        .isEqualTo(errorResponse);
  }

  @Test
  void searchByLatLong() {
    final String latitude = systemDefinition().ids().latitude();
    final String longitude = systemDefinition().ids().longitude();
    final String request = "v0/nearby?lat=" + latitude + "&lng=" + longitude;
    facilitiesRequest("application/json", request, 200).expectValid(NearbyResponse.class);
  }

  @Test
  void searchByLatLongWithDriveTime() {
    final String latitude = systemDefinition().ids().latitude();
    final String longitude = systemDefinition().ids().longitude();
    final String request = "v0/nearby?lat=" + latitude + "&lng=" + longitude + "&drive_time=90";
    facilitiesRequest("application/json", request, 200).expectValid(NearbyResponse.class);
  }

  @Test
  @SneakyThrows
  void searchIncorrectParamsMissingAddress() {
    final String errorResponse =
        "Parameter conditions \"lat, lng\" not met for actual request parameters: foo={bar}";
    final String request = "v0/nearby?foo=bar";
    assertThat(
            facilitiesRequest("application/json", request, 400)
                .expectValid(ApiError.class)
                .errors()
                .get(0)
                .detail())
        .isEqualTo(errorResponse);
  }
}
