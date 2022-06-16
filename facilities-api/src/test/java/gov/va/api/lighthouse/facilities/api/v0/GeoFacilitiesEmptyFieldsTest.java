package gov.va.api.lighthouse.facilities.api.v0;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.TestUtils.getExpectedJson;
import static org.assertj.core.api.Assertions.assertThat;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class GeoFacilitiesEmptyFieldsTest {

  @Test
  @SneakyThrows
  void allFieldsEmpty() {
    // Null out fields for response
    String jsonEmptyFacility = getExpectedJson("v0/GeoFacility/geoFacilityWithNullFields.json");
    GeoFacility emptyFacility =
        GeoFacility.builder().type(null).geometry(null).properties(null).build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyFacility))
        .isEqualTo(jsonEmptyFacility);
    // Response with empty fields
    jsonEmptyFacility = getExpectedJson("v0/GeoFacility/geoFacilityWithTypeOnly.json");
    emptyFacility =
        GeoFacility.builder()
            .type(GeoFacility.Type.Feature)
            .geometry(null)
            .properties(null)
            .build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyFacility))
        .isEqualTo(jsonEmptyFacility);
    jsonEmptyFacility = getExpectedJson("v0/GeoFacility/geoFacilityWithEmptyGeometry.json");
    emptyFacility =
        GeoFacility.builder()
            .type(GeoFacility.Type.Feature)
            .geometry(GeoFacility.Geometry.builder().build())
            .properties(null)
            .build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyFacility))
        .isEqualTo(jsonEmptyFacility);
    jsonEmptyFacility = getExpectedJson("v0/GeoFacility/geoFacilityWithEmptyProperties.json");
    emptyFacility =
        GeoFacility.builder()
            .type(GeoFacility.Type.Feature)
            .geometry(GeoFacility.Geometry.builder().build())
            .properties(GeoFacility.Properties.builder().build())
            .build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyFacility))
        .isEqualTo(jsonEmptyFacility);
  }
}
