package gov.va.api.lighthouse.facilities.api.v0;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.TestUtils.getExpectedJson;
import static org.assertj.core.api.Assertions.assertThat;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class FacilityEmptyFieldsTest {
  @Test
  @SneakyThrows
  void allFieldsEmpty() {
    // Null out fields for response
    String jsonEmptyFacility = getExpectedJson("v0/Facility/facilityWithNullFields.json");
    Facility emptyFacility = Facility.builder().id(null).type(null).attributes(null).build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyFacility))
        .isEqualTo(jsonEmptyFacility);
    // Response with empty fields
    jsonEmptyFacility = getExpectedJson("v0/Facility/facilityWithTypeOnly.json");
    emptyFacility =
        Facility.builder().id(null).type(Facility.Type.va_facilities).attributes(null).build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyFacility))
        .isEqualTo(jsonEmptyFacility);
    jsonEmptyFacility = getExpectedJson("v0/Facility/facilityWithEmptyAttributes.json");
    emptyFacility =
        Facility.builder()
            .id("vha_402")
            .type(Facility.Type.va_facilities)
            .attributes(Facility.FacilityAttributes.builder().build())
            .build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyFacility))
        .isEqualTo(jsonEmptyFacility);
  }

  @Test
  @SneakyThrows
  void facilityAttributesInstructions() {
    assertThat(Facility.FacilityAttributes.builder().instructions("new instructions").build())
        .usingRecursiveComparison()
        .isEqualTo(
            Facility.FacilityAttributes.builder()
                .operationalHoursSpecialInstructions("new instructions")
                .build());
  }
}
