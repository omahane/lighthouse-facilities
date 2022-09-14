package gov.va.api.lighthouse.facilities.api.v0;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.TestUtils.getExpectedJson;
import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import gov.va.api.lighthouse.facilities.api.v0.Facility.BenefitsService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OtherService;
import java.util.Arrays;
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

  @Test
  void fromServiceId() {
    // Benefit services
    Arrays.stream(BenefitsService.values())
        .forEach(
            bs -> assertThat(BenefitsService.fromServiceId(bs.serviceId()).get()).isEqualTo(bs));
    assertThat(BenefitsService.fromServiceId("noSuchServiceId").isPresent()).isFalse();
    assertThat(BenefitsService.fromServiceId("   ").isPresent()).isFalse();
    assertThat(BenefitsService.fromServiceId("").isPresent()).isFalse();
    assertThat(BenefitsService.fromServiceId(null).isPresent()).isFalse();
    // Health services
    Arrays.stream(HealthService.values())
        .forEach(hs -> assertThat(HealthService.fromServiceId(hs.serviceId()).get()).isEqualTo(hs));
    assertThat(HealthService.fromServiceId("noSuchServiceId").isPresent()).isFalse();
    assertThat(HealthService.fromServiceId("   ").isPresent()).isFalse();
    assertThat(HealthService.fromServiceId("").isPresent()).isFalse();
    assertThat(HealthService.fromServiceId(null).isPresent()).isFalse();
    // Other services
    Arrays.stream(OtherService.values())
        .forEach(os -> assertThat(OtherService.fromServiceId(os.serviceId()).get()).isEqualTo(os));
    assertThat(OtherService.fromServiceId("noSuchServiceId").isPresent()).isFalse();
    assertThat(OtherService.fromServiceId("   ").isPresent()).isFalse();
    assertThat(OtherService.fromServiceId("").isPresent()).isFalse();
    assertThat(OtherService.fromServiceId(null).isPresent()).isFalse();
  }

  @Test
  void fromString() {
    // Benefit services
    Arrays.stream(BenefitsService.values())
        .forEach(bs -> assertThat(BenefitsService.fromString(bs.name())).isEqualTo(bs));
    Arrays.stream(BenefitsService.values())
        .forEach(
            bs -> assertThat(BenefitsService.fromString(uncapitalize(bs.name()))).isEqualTo(bs));
    assertThatThrownBy(() -> BenefitsService.fromString("noSuchService"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "No enum constant gov.va.api.lighthouse.facilities.api.v0.Facility.BenefitsService.NoSuchService");
    // Health services
    Arrays.stream(HealthService.values())
        .forEach(hs -> assertThat(HealthService.fromString(hs.name())).isEqualTo(hs));
    Arrays.stream(HealthService.values())
        .forEach(hs -> assertThat(HealthService.fromString(uncapitalize(hs.name()))).isEqualTo(hs));
    assertThatThrownBy(() -> HealthService.fromString("noSuchService"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "No enum constant gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.NoSuchService");
    // Other services
    Arrays.stream(OtherService.values())
        .forEach(os -> assertThat(OtherService.fromString(os.name())).isEqualTo(os));
    Arrays.stream(OtherService.values())
        .forEach(os -> assertThat(OtherService.fromString(uncapitalize(os.name()))).isEqualTo(os));
    assertThatThrownBy(() -> OtherService.fromString("noSuchService"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "No enum constant gov.va.api.lighthouse.facilities.api.v0.Facility.OtherService.NoSuchService");
  }

  @Test
  void isRecognizedEnumOrCovidService() {
    Arrays.stream(HealthService.values())
        .forEach(
            hs -> assertThat(HealthService.isRecognizedEnumOrCovidService(hs.name())).isTrue());
    assertThat(HealthService.isRecognizedEnumOrCovidService("COVID-19 vaccines")).isTrue();
    assertThat(HealthService.isRecognizedEnumOrCovidService("NoSuchService")).isFalse();
    assertThat(HealthService.isRecognizedEnumOrCovidService("   ")).isFalse();
    assertThat(HealthService.isRecognizedEnumOrCovidService("")).isFalse();
    assertThat(HealthService.isRecognizedEnumOrCovidService(null)).isFalse();
  }

  @Test
  void isRecognizedServiceEnum() {
    // Benefit services
    Arrays.stream(BenefitsService.values())
        .forEach(bs -> assertThat(BenefitsService.isRecognizedServiceEnum(bs.name())).isTrue());
    assertThat(BenefitsService.isRecognizedServiceId("noSuchService")).isFalse();
    assertThat(BenefitsService.isRecognizedServiceId("   ")).isFalse();
    assertThat(BenefitsService.isRecognizedServiceId("")).isFalse();
    assertThat(BenefitsService.isRecognizedServiceId(null)).isFalse();
    // Health services
    Arrays.stream(HealthService.values())
        .forEach(hs -> assertThat(HealthService.isRecognizedServiceEnum(hs.name())).isTrue());
    assertThat(HealthService.isRecognizedServiceId("noSuchService")).isFalse();
    assertThat(HealthService.isRecognizedServiceId("   ")).isFalse();
    assertThat(HealthService.isRecognizedServiceId("")).isFalse();
    assertThat(HealthService.isRecognizedServiceId(null)).isFalse();
    // Other services
    Arrays.stream(OtherService.values())
        .forEach(os -> assertThat(OtherService.isRecognizedServiceEnum(os.name())).isTrue());
    assertThat(OtherService.isRecognizedServiceId("noSuchService")).isFalse();
    assertThat(OtherService.isRecognizedServiceId("   ")).isFalse();
    assertThat(OtherService.isRecognizedServiceId("")).isFalse();
    assertThat(OtherService.isRecognizedServiceId(null)).isFalse();
  }

  @Test
  void isRecognizedServiceId() {
    // Benefit services
    Arrays.stream(BenefitsService.values())
        .forEach(bs -> assertThat(BenefitsService.isRecognizedServiceId(bs.serviceId())).isTrue());
    assertThat(BenefitsService.isRecognizedServiceId("noSuchServiceId")).isFalse();
    assertThat(BenefitsService.isRecognizedServiceId("   ")).isFalse();
    assertThat(BenefitsService.isRecognizedServiceId("")).isFalse();
    assertThat(BenefitsService.isRecognizedServiceId(null)).isFalse();
    // Health services
    Arrays.stream(HealthService.values())
        .forEach(hs -> assertThat(HealthService.isRecognizedServiceId(hs.serviceId())).isTrue());
    assertThat(HealthService.isRecognizedServiceId("noSuchServiceId")).isFalse();
    assertThat(HealthService.isRecognizedServiceId("   ")).isFalse();
    assertThat(HealthService.isRecognizedServiceId("")).isFalse();
    assertThat(HealthService.isRecognizedServiceId(null)).isFalse();
    // Other services
    Arrays.stream(OtherService.values())
        .forEach(os -> assertThat(OtherService.isRecognizedServiceId(os.serviceId())).isTrue());
    assertThat(OtherService.isRecognizedServiceId("noSuchServiceId")).isFalse();
    assertThat(OtherService.isRecognizedServiceId("   ")).isFalse();
    assertThat(OtherService.isRecognizedServiceId("")).isFalse();
    assertThat(OtherService.isRecognizedServiceId(null)).isFalse();
  }

  @Test
  void isRecognizedServiceNameException() {
    // Filter on the recognized service name exceptions
    Arrays.stream(HealthService.values())
        .filter(
            hs ->
                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Dental.serviceId()
                        .equals(hs.serviceId())
                    || gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealth
                        .serviceId()
                        .equals(hs.serviceId()))
        .forEach(
            hs -> assertThat(HealthService.isRecognizedServiceNameException(hs.name())).isTrue());
    // Filter out recognized service name exceptions
    Arrays.stream(HealthService.values())
        .filter(
            hs ->
                !gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Dental.serviceId()
                        .equals(hs.serviceId())
                    && !gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealth
                        .serviceId()
                        .equals(hs.serviceId()))
        .forEach(
            hs -> assertThat(HealthService.isRecognizedServiceNameException(hs.name())).isFalse());
    assertThat(HealthService.isRecognizedServiceNameException("noSuchService")).isFalse();
    assertThat(HealthService.isRecognizedServiceNameException("   ")).isFalse();
    assertThat(HealthService.isRecognizedServiceNameException("")).isFalse();
    assertThat(HealthService.isRecognizedServiceNameException(null)).isFalse();
  }
}
