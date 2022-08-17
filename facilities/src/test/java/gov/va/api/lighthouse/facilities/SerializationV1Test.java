package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.FacilitiesJacksonConfigV1.createMapper;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.v1.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v1.FacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v1.NearbyResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

/**
 * These tests exercise round-trip deserialization and re-serialization of responses from the
 * original Rails Facilities API, verifying equality. Responses are used precisely as originally
 * captured, with the exception of string trimming. We have not implemented custom deserialization
 * behavior, so string-trimming is still applied during deserialization. Because custom
 * deserialization is not required in the app in production, we can accept this for unit tests; the
 * source messages have been edited to replace empty strings with null, and to remove any trailing
 * whitespace.
 */
public class SerializationV1Test {

  @Test
  @SneakyThrows
  void all() {
    String path = "/v1/all.json";
    roundTrip(path, FacilitiesResponse.class);
  }

  @Test
  @SneakyThrows
  void nearby() {
    String path = "/v1/nearby.json";
    roundTrip(path, NearbyResponse.class);
  }

  @Test
  @SneakyThrows
  void readBenefits() {
    String path = "/v1/read-benefits.json";
    roundTrip(path, FacilityReadResponse.class);
  }

  @Test
  @SneakyThrows
  void readCemetery() {
    String path = "/v1/read-cemetery.json";
    roundTrip(path, FacilityReadResponse.class);
  }

  @Test
  @SneakyThrows
  void readHealth() {
    String path = "/v1/read-health.json";
    roundTrip(path, FacilityReadResponse.class);
  }

  @Test
  @SneakyThrows
  void readStateCemetery() {
    String path = "/v1/read-state-cemetery.json";
    roundTrip(path, FacilityReadResponse.class);
  }

  @Test
  @SneakyThrows
  void readVetCenter() {
    String path = "/v1/read-vet-center.json";
    roundTrip(path, FacilityReadResponse.class);
  }

  @SneakyThrows
  private <T> void roundTrip(String path, Class<T> clazz) {
    T response = createMapper().readValue(getClass().getResourceAsStream(path), clazz);
    assertThat(response).isExactlyInstanceOf(clazz);
    String actual = createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(response);
    String expected =
        createMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(createMapper().readTree(getClass().getResourceAsStream(path)));
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  void searchByBbox() {
    String path = "/v1/search-bbox.json";
    roundTrip(path, FacilitiesResponse.class);
  }

  @Test
  @SneakyThrows
  void searchByIds() {
    String path = "/v1/search-ids.json";
    roundTrip(path, FacilitiesResponse.class);
  }

  @Test
  @SneakyThrows
  void searchByLatLong() {
    String path = "/v1/search-lat-long.json";
    roundTrip(path, FacilitiesResponse.class);
  }

  @Test
  @SneakyThrows
  void searchByState() {
    String path = "/v1/search-state.json";
    roundTrip(path, FacilitiesResponse.class);
  }

  @Test
  @SneakyThrows
  void searchByZip() {
    String path = "/v1/search-zip.json";
    roundTrip(path, FacilitiesResponse.class);
  }
}
