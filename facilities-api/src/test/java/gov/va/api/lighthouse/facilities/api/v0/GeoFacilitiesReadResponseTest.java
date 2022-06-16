package gov.va.api.lighthouse.facilities.api.v0;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class GeoFacilitiesReadResponseTest {

  @Test
  @SneakyThrows
  void of() {
    GeoFacility orig =
        GeoFacility.builder()
            .type(GeoFacility.Type.Feature)
            .geometry(GeoFacility.Geometry.builder().type(GeoFacility.GeometryType.Point).build())
            .properties(
                GeoFacility.Properties.builder()
                    .facilityType(Facility.FacilityType.va_health_facility)
                    .build())
            .build();
    GeoFacilityReadResponse copy = GeoFacilityReadResponse.of(orig);
    assertThat(copy.type()).usingRecursiveComparison().isEqualTo(orig.type());
    assertThat(copy.properties()).usingRecursiveComparison().isEqualTo(orig.properties());
    assertThat(copy.geometry()).usingRecursiveComparison().isEqualTo(orig.geometry());

    assertThatThrownBy(() -> GeoFacilityReadResponse.of(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("facility is marked non-null but is null");
  }
}
