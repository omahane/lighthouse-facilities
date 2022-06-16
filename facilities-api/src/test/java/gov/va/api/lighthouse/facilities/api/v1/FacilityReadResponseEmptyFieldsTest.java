package gov.va.api.lighthouse.facilities.api.v1;

import static gov.va.api.lighthouse.facilities.api.TestUtils.getExpectedJson;
import static gov.va.api.lighthouse.facilities.api.v1.SerializerUtil.createMapper;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class FacilityReadResponseEmptyFieldsTest {

  @Test
  @SneakyThrows
  void isEmpty() {
    // Empty
    assertThat(FacilityReadResponse.builder().build().isEmpty()).isTrue();
    assertThat(
            FacilityReadResponse.builder().facility(Facility.builder().build()).build().isEmpty())
        .isTrue();
    // Not empty
    assertThat(
            FacilityReadResponse.builder()
                .facility(Facility.builder().id("vha_402").build())
                .build()
                .isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void satisfactionAndWaitTimesPopulated() {
    String expectedJson =
        getExpectedJson("v1/FacilityReadResponse/satisfactionAndWaitTimesPopulated.json");
    FacilityReadResponse readResponse =
        FacilityReadResponse.builder()
            .facility(
                Facility.builder()
                    .id("vha_402")
                    .attributes(
                        Facility.FacilityAttributes.builder()
                            .address(
                                Facility.Addresses.builder()
                                    .mailing(
                                        Facility.Address.builder()
                                            .address1("50 Irving Street, Northwest")
                                            .address2("Bldg 2")
                                            .address3("Suite 7")
                                            .city("Washington")
                                            .state("DC")
                                            .zip("20422-0001")
                                            .build())
                                    .build())
                            .satisfaction(
                                Facility.Satisfaction.builder()
                                    .health(
                                        Facility.PatientSatisfaction.builder()
                                            .primaryCareUrgent(BigDecimal.TEN)
                                            .build())
                                    .effectiveDate(LocalDate.parse("2022-01-13"))
                                    .build())
                            .waitTimes(
                                Facility.WaitTimes.builder()
                                    .health(
                                        List.of(
                                            Facility.PatientWaitTime.builder()
                                                .newPatientWaitTime(BigDecimal.ONE)
                                                .build()))
                                    .effectiveDate(LocalDate.parse("2022-01-13"))
                                    .build())
                            .build())
                    .build())
            .build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(readResponse))
        .isEqualToIgnoringWhitespace(expectedJson);
    expectedJson =
        getExpectedJson(
            "v1/FacilityReadResponse/satisfactionAndWaitTimesPopulatedOnlyAddress1.json");
    readResponse =
        FacilityReadResponse.builder()
            .facility(
                Facility.builder()
                    .id("nca_s402")
                    .attributes(
                        Facility.FacilityAttributes.builder()
                            .address(
                                Facility.Addresses.builder()
                                    .mailing(
                                        Facility.Address.builder()
                                            .address1("50 Irving Street, Northwest")
                                            .build())
                                    .build())
                            .satisfaction(
                                Facility.Satisfaction.builder()
                                    .health(
                                        Facility.PatientSatisfaction.builder()
                                            .primaryCareUrgent(BigDecimal.TEN)
                                            .build())
                                    .effectiveDate(LocalDate.parse("2022-01-13"))
                                    .build())
                            .waitTimes(
                                Facility.WaitTimes.builder()
                                    .health(
                                        List.of(
                                            Facility.PatientWaitTime.builder()
                                                .newPatientWaitTime(BigDecimal.ONE)
                                                .build()))
                                    .effectiveDate(LocalDate.parse("2022-01-13"))
                                    .build())
                            .build())
                    .build())
            .build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(readResponse))
        .isEqualToIgnoringWhitespace(expectedJson);
  }
}
