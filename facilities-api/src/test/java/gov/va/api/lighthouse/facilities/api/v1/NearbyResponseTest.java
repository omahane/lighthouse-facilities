package gov.va.api.lighthouse.facilities.api.v1;

import static gov.va.api.lighthouse.facilities.api.TestUtils.getExpectedJson;
import static gov.va.api.lighthouse.facilities.api.v1.SerializerUtil.createMapper;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class NearbyResponseTest {
  @Test
  @SneakyThrows
  void allFieldsEmpty() {
    // Null out fields for response
    String jsonEmptyResponse = getExpectedJson("v1/NearbyResponse/responseWithNullFields.json");
    NearbyResponse emptyResponse = NearbyResponse.builder().data(null).meta(null).build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyResponse))
        .isEqualTo(jsonEmptyResponse);
    // Response with empty fields
    emptyResponse = NearbyResponse.builder().data(emptyList()).meta(null).build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyResponse))
        .isEqualTo(jsonEmptyResponse);
    jsonEmptyResponse = getExpectedJson("v1/NearbyResponse/responseWithEmptyDataMeta.json");
    emptyResponse =
        NearbyResponse.builder()
            .data(emptyList())
            .meta(NearbyResponse.Meta.builder().build())
            .build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyResponse))
        .isEqualTo(jsonEmptyResponse);
  }

  @SneakyThrows
  private void assertReadable(String json) {
    NearbyResponse f =
        createMapper().readValue(getClass().getResourceAsStream(json), NearbyResponse.class);
    assertThat(f).isEqualTo(sample());
  }

  @Test
  @SneakyThrows
  void emptyMeta() {
    // Empty
    assertThat(NearbyResponse.Meta.builder().build().isEmpty()).isTrue();
    assertThat(NearbyResponse.Meta.builder().bandVersion("   ").build().isEmpty()).isTrue();
    // Not empty
    assertThat(NearbyResponse.Meta.builder().bandVersion("band version").build().isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void emptyNearby() {
    // Empty
    assertThat(NearbyResponse.Nearby.builder().build().isEmpty()).isTrue();
    assertThat(NearbyResponse.Nearby.builder().id("   ").build().isEmpty()).isTrue();
    assertThat(
            NearbyResponse.Nearby.builder()
                .attributes(NearbyResponse.NearbyAttributes.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    // Not empty
    assertThat(NearbyResponse.Nearby.builder().id("test").build().isEmpty()).isFalse();
    assertThat(
            NearbyResponse.Nearby.builder()
                .attributes(
                    NearbyResponse.NearbyAttributes.builder().maxTime(Integer.MAX_VALUE).build())
                .build()
                .isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void emptyNearbyAttributes() {
    // Empty
    assertThat(NearbyResponse.NearbyAttributes.builder().build().isEmpty()).isTrue();
    // Not empty
    assertThat(
            NearbyResponse.NearbyAttributes.builder().minTime(Integer.MIN_VALUE).build().isEmpty())
        .isFalse();
    assertThat(
            NearbyResponse.NearbyAttributes.builder().maxTime(Integer.MAX_VALUE).build().isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void isEmpty() {
    // Empty
    assertThat(NearbyResponse.builder().build().isEmpty()).isTrue();
    assertThat(NearbyResponse.builder().data(emptyList()).build().isEmpty()).isTrue();
    assertThat(
            NearbyResponse.builder().meta(NearbyResponse.Meta.builder().build()).build().isEmpty())
        .isTrue();
    // Not empty
    assertThat(
            NearbyResponse.builder()
                .data(
                    List.of(
                        NearbyResponse.Nearby.builder()
                            .type(NearbyResponse.Type.NearbyFacility)
                            .build()))
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            NearbyResponse.builder()
                .meta(NearbyResponse.Meta.builder().bandVersion("test").build())
                .build()
                .isEmpty())
        .isFalse();
  }

  private NearbyResponse.Nearby nearbyFacility(
      String facilityId, Integer minTime, Integer maxTime, String related) {
    return NearbyResponse.Nearby.builder()
        .id(facilityId)
        .type(NearbyResponse.Type.NearbyFacility)
        .attributes(
            NearbyResponse.NearbyAttributes.builder().minTime(minTime).maxTime(maxTime).build())
        .build();
  }

  private NearbyResponse sample() {
    return NearbyResponse.builder()
        .data(
            List.of(
                nearbyFacility(
                    "vha_548GC", 70, 80, "/services/va_facilities/v0/facilities/vha_548GC"),
                nearbyFacility(
                    "vha_548GF", 80, 90, "/services/va_facilities/v0/facilities/vha_548GF"),
                nearbyFacility(
                    "vha_548QA", 60, 70, "/services/va_facilities/v0/facilities/vha_548QA"),
                nearbyFacility(
                    "vha_548GA", 50, 60, "/services/va_facilities/v0/facilities/vha_548GA"),
                nearbyFacility(
                    "vha_548GE", 50, 60, "/services/va_facilities/v0/facilities/vha_548GE"),
                nearbyFacility("vha_675", 50, 60, "/services/va_facilities/v0/facilities/vha_675"),
                nearbyFacility(
                    "vha_675GA", 10, 20, "/services/va_facilities/v0/facilities/vha_675GA"),
                nearbyFacility(
                    "vha_675GB", 70, 80, "/services/va_facilities/v0/facilities/vha_675GB"),
                nearbyFacility(
                    "vha_675GD", 80, 90, "/services/va_facilities/v0/facilities/vha_675GD"),
                nearbyFacility(
                    "vha_675GF", 80, 90, "/services/va_facilities/v0/facilities/vha_675GF"),
                nearbyFacility(
                    "vha_675GG", 60, 70, "/services/va_facilities/v0/facilities/vha_675GG"),
                nearbyFacility(
                    "vha_675QB", 70, 80, "/services/va_facilities/v0/facilities/vha_675QB"),
                nearbyFacility(
                    "vha_675QC", 70, 80, "/services/va_facilities/v0/facilities/vha_675QC"),
                nearbyFacility(
                    "vha_675QD", 60, 70, "/services/va_facilities/v0/facilities/vha_675QD"),
                nearbyFacility(
                    "vha_675QE", 60, 70, "/services/va_facilities/v0/facilities/vha_675QE"),
                nearbyFacility(
                    "vha_675GC", 60, 70, "/services/va_facilities/v0/facilities/vha_675GC")))
        .meta(NearbyResponse.Meta.builder().bandVersion("APR2021").build())
        .build();
  }

  @Test
  void unmarshallSample() {
    assertReadable("/v1/nearby.json");
  }
}
