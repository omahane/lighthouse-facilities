package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.FacilitiesJacksonConfigV1.createMapper;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import gov.va.api.lighthouse.facilities.api.v1.DetailedService;
import gov.va.api.lighthouse.facilities.api.v1.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v1.FacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v1.NearbyResponse;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
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
  void jacksonConfigQuietlyMap() {
    DetailedService emptyDetailedService =
        DetailedService.builder()
            .serviceId(DetailedService.INVALID_SVC_ID)
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    String json =
        FacilitiesJacksonConfigV1.quietlyWriteValueAsString(
            FacilitiesJacksonConfigV1.createMapper(), emptyDetailedService);
    assertThat(json).isEqualTo("{\"serviceId\":\"INVALID_ID\"}");
    assertThat(
            FacilitiesJacksonConfigV1.quietlyMap(
                FacilitiesJacksonConfigV1.createMapper(), json, DetailedService.class))
        .usingRecursiveComparison()
        .isEqualTo(emptyDetailedService);
    assertThat(
            FacilitiesJacksonConfigV1.quietlyMap(
                FacilitiesJacksonConfigV1.createMapper(),
                new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)),
                DetailedService.class))
        .usingRecursiveComparison()
        .isEqualTo(emptyDetailedService);
    // Exceptions
    assertThat(
            FacilitiesJacksonConfigV1.quietlyWriteValueAsString(
                FacilitiesJacksonConfigV1.createMapper(), null))
        .isEqualTo("null");

    assertThatThrownBy(() -> FacilitiesJacksonConfigV1.quietlyWriteValueAsString(null, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage(
            "Cannot invoke \"com.fasterxml.jackson.databind.ObjectMapper.writeValueAsString(Object)\" because \"mapper\" is null");
    assertThatThrownBy(
            () ->
                FacilitiesJacksonConfigV1.quietlyMap(
                    FacilitiesJacksonConfigV1.createMapper(),
                    "{\"serviceId\":\"INVALID_ID\"}",
                    null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unrecognized Type: [null]");

    assertThatThrownBy(
            () ->
                FacilitiesJacksonConfigV1.quietlyMap(
                    FacilitiesJacksonConfigV1.createMapper(),
                    new ByteArrayInputStream(
                        "{\"serviceId\":\"INVALID_ID\"}".getBytes(StandardCharsets.UTF_8)),
                    null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unrecognized Type: [null]");
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
