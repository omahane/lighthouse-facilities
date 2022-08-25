package gov.va.api.lighthouse.facilities.api.v1;

import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildLinkerUrlV1;
import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildTypedServiceLink;
import static gov.va.api.lighthouse.facilities.api.TestUtils.getExpectedJson;
import static gov.va.api.lighthouse.facilities.api.v1.SerializerUtil.createMapper;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class FacilitiesEmptyFieldsTest {
  @Test
  @SneakyThrows
  void allFieldsEmpty() {
    // Null out fields for response
    String jsonEmptyFacility = getExpectedJson("v1/Facility/facilityWithNullFields.json");
    Facility emptyFacility = Facility.builder().id(null).type(null).attributes(null).build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyFacility))
        .isEqualTo(jsonEmptyFacility);
    // Response with empty fields
    jsonEmptyFacility = getExpectedJson("v1/Facility/facilityWithTypeOnly.json");
    emptyFacility =
        Facility.builder().id(null).type(Facility.Type.va_facilities).attributes(null).build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyFacility))
        .isEqualTo(jsonEmptyFacility);
    jsonEmptyFacility = getExpectedJson("v1/Facility/facilityWithEmptyAttributes.json");
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
  void emptyAddress() {
    assertThat(Facility.Address.builder().build().isEmpty()).isTrue();
    // Blank address values
    String blank = "   ";
    assertThat(Facility.Address.builder().address1(blank).build().isEmpty()).isTrue();
    assertThat(Facility.Address.builder().address2(blank).build().isEmpty()).isTrue();
    assertThat(Facility.Address.builder().address3(blank).build().isEmpty()).isTrue();
    assertThat(Facility.Address.builder().city(blank).build().isEmpty()).isTrue();
    assertThat(Facility.Address.builder().state(blank).build().isEmpty()).isTrue();
    assertThat(Facility.Address.builder().zip(blank).build().isEmpty()).isTrue();
    // Valid address values
    assertThat(Facility.Address.builder().address1("50 Irving Street, Northwest").build().isEmpty())
        .isFalse();
    assertThat(Facility.Address.builder().address2("Bldg 2").build().isEmpty()).isFalse();
    assertThat(Facility.Address.builder().address3("Suite 7").build().isEmpty()).isFalse();
    assertThat(Facility.Address.builder().city("Washington").build().isEmpty()).isFalse();
    assertThat(Facility.Address.builder().state("DC").build().isEmpty()).isFalse();
    assertThat(Facility.Address.builder().zip("20422-0001").build().isEmpty()).isFalse();
  }

  @Test
  @SneakyThrows
  void emptyAddresses() {
    // Empty
    assertThat(Facility.Addresses.builder().build().isEmpty()).isTrue();
    assertThat(
            Facility.Addresses.builder()
                .mailing(Facility.Address.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    assertThat(
            Facility.Addresses.builder()
                .physical(Facility.Address.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    // Not empty
    assertThat(
            Facility.Addresses.builder()
                .mailing(Facility.Address.builder().address1("50 Irving Street, Northwest").build())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            Facility.Addresses.builder()
                .physical(
                    Facility.Address.builder().address1("50 Irving Street, Northwest").build())
                .build()
                .isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void emptyFacilityAttributes() {
    assertThat(Facility.FacilityAttributes.builder().build().isEmpty()).isTrue();
    // Empty
    final String blank = "   ";
    assertThat(Facility.FacilityAttributes.builder().website(blank).build().isEmpty()).isTrue();
    assertThat(Facility.FacilityAttributes.builder().classification(blank).build().isEmpty())
        .isTrue();
    assertThat(Facility.FacilityAttributes.builder().name(blank).build().isEmpty()).isTrue();
    assertThat(Facility.FacilityAttributes.builder().visn(blank).build().isEmpty()).isTrue();
    assertThat(Facility.FacilityAttributes.builder().timeZone(blank).build().isEmpty()).isTrue();
    assertThat(
            Facility.FacilityAttributes.builder()
                .services(Facility.Services.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    assertThat(
            Facility.FacilityAttributes.builder()
                .address(Facility.Addresses.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    assertThat(
            Facility.FacilityAttributes.builder()
                .hours(Facility.Hours.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    assertThat(
            Facility.FacilityAttributes.builder()
                .phone(Facility.Phone.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    assertThat(
            Facility.FacilityAttributes.builder()
                .satisfaction(Facility.Satisfaction.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    assertThat(Facility.FacilityAttributes.builder().instructions(emptyList()).build().isEmpty())
        .isTrue();
    // Not empty
    assertThat(
            Facility.FacilityAttributes.builder()
                .facilityType(Facility.FacilityType.va_health_facility)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(Facility.FacilityAttributes.builder().latitude(BigDecimal.ZERO).build().isEmpty())
        .isFalse();
    assertThat(Facility.FacilityAttributes.builder().longitude(BigDecimal.ZERO).build().isEmpty())
        .isFalse();
    assertThat(Facility.FacilityAttributes.builder().website("http://foo.bar").build().isEmpty())
        .isFalse();
    assertThat(
            Facility.FacilityAttributes.builder()
                .activeStatus(Facility.ActiveStatus.A)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(Facility.FacilityAttributes.builder().mobile(Boolean.TRUE).build().isEmpty())
        .isFalse();
    assertThat(Facility.FacilityAttributes.builder().classification("health").build().isEmpty())
        .isFalse();
    assertThat(Facility.FacilityAttributes.builder().name("test").build().isEmpty()).isFalse();
    assertThat(Facility.FacilityAttributes.builder().timeZone("EST").build().isEmpty()).isFalse();
    assertThat(Facility.FacilityAttributes.builder().visn("visn").build().isEmpty()).isFalse();
    assertThat(
            Facility.FacilityAttributes.builder()
                .address(
                    Facility.Addresses.builder()
                        .mailing(Facility.Address.builder().city("Melbourne").build())
                        .build())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            Facility.FacilityAttributes.builder()
                .phone(Facility.Phone.builder().main("202-555-1212").build())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            Facility.FacilityAttributes.builder()
                .hours(Facility.Hours.builder().monday("9AM-5PM").build())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            Facility.FacilityAttributes.builder()
                .operationalHoursSpecialInstructions(List.of("special instructions"))
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            Facility.FacilityAttributes.builder()
                .services(
                    Facility.Services.builder()
                        .benefits(
                            List.of(
                                Facility.Service.<Facility.BenefitsService>builder()
                                    .serviceType(Facility.BenefitsService.Pensions)
                                    .name(Facility.BenefitsService.Pensions.name())
                                    .link(
                                        buildTypedServiceLink(
                                            buildLinkerUrlV1("http://foo/", "bar"),
                                            "vha_402",
                                            Facility.HealthService.PrimaryCare.serviceId()))
                                    .build()))
                        .build())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            Facility.FacilityAttributes.builder()
                .satisfaction(
                    Facility.Satisfaction.builder()
                        .health(
                            Facility.PatientSatisfaction.builder()
                                .primaryCareUrgent(BigDecimal.ZERO)
                                .build())
                        .build())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            Facility.FacilityAttributes.builder()
                .operatingStatus(
                    Facility.OperatingStatus.builder()
                        .code(Facility.OperatingStatusCode.NORMAL)
                        .build())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            Facility.FacilityAttributes.builder()
                .facilityType(Facility.FacilityType.va_health_facility)
                .build()
                .isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void emptyHours() {
    assertThat(Facility.Hours.builder().build().isEmpty()).isTrue();
    // Blank hours
    String hours = "   ";
    assertThat(Facility.Hours.builder().monday(hours).build().isEmpty()).isTrue();
    assertThat(Facility.Hours.builder().tuesday(hours).build().isEmpty()).isTrue();
    assertThat(Facility.Hours.builder().wednesday(hours).build().isEmpty()).isTrue();
    assertThat(Facility.Hours.builder().thursday(hours).build().isEmpty()).isTrue();
    assertThat(Facility.Hours.builder().friday(hours).build().isEmpty()).isTrue();
    assertThat(Facility.Hours.builder().saturday(hours).build().isEmpty()).isTrue();
    assertThat(Facility.Hours.builder().sunday(hours).build().isEmpty()).isTrue();
    // Valid hours
    hours = "9AM-5PM";
    assertThat(Facility.Hours.builder().monday(hours).build().isEmpty()).isFalse();
    assertThat(Facility.Hours.builder().tuesday(hours).build().isEmpty()).isFalse();
    assertThat(Facility.Hours.builder().wednesday(hours).build().isEmpty()).isFalse();
    assertThat(Facility.Hours.builder().thursday(hours).build().isEmpty()).isFalse();
    assertThat(Facility.Hours.builder().friday(hours).build().isEmpty()).isFalse();
    assertThat(Facility.Hours.builder().saturday(hours).build().isEmpty()).isFalse();
    assertThat(Facility.Hours.builder().sunday(hours).build().isEmpty()).isFalse();
  }

  @Test
  @SneakyThrows
  void emptyOperatingStatus() {
    // Empty
    assertThat(Facility.OperatingStatus.builder().build().isEmpty()).isTrue();
    assertThat(Facility.OperatingStatus.builder().additionalInfo("   ").build().isEmpty()).isTrue();
    // Not empty
    assertThat(
            Facility.OperatingStatus.builder().additionalInfo("additional info").build().isEmpty())
        .isFalse();
    assertThat(
            Facility.OperatingStatus.builder()
                .additionalInfo("   additional info   ")
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            Facility.OperatingStatus.builder()
                .code(Facility.OperatingStatusCode.NORMAL)
                .build()
                .isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void emptyPatientSatisfaction() {
    // Empty
    assertThat(Facility.PatientSatisfaction.builder().build().isEmpty()).isTrue();
    // Not empty
    assertThat(
            Facility.PatientSatisfaction.builder()
                .primaryCareRoutine(BigDecimal.ZERO)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            Facility.PatientSatisfaction.builder()
                .primaryCareUrgent(BigDecimal.ZERO)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            Facility.PatientSatisfaction.builder()
                .specialtyCareRoutine(BigDecimal.ZERO)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            Facility.PatientSatisfaction.builder()
                .specialtyCareUrgent(BigDecimal.ZERO)
                .build()
                .isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void emptyPatientWaitTimes() {
    // Empty
    assertThat(DetailedService.PatientWaitTime.builder().build().isEmpty()).isTrue();
    // Not empty
    assertThat(
            DetailedService.PatientWaitTime.builder()
                .newPatientWaitTime(BigDecimal.ZERO)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.PatientWaitTime.builder()
                .establishedPatientWaitTime(BigDecimal.ZERO)
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedService.PatientWaitTime.builder()
                .effectiveDate(LocalDate.parse("2020-03-09"))
                .build()
                .isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void emptyPhone() {
    assertThat(Facility.Phone.builder().build().isEmpty()).isTrue();
    // Blank phone number
    String phoneNumber = "   ";
    assertThat(Facility.Phone.builder().afterHours(phoneNumber).build().isEmpty()).isTrue();
    assertThat(Facility.Phone.builder().enrollmentCoordinator(phoneNumber).build().isEmpty())
        .isTrue();
    assertThat(Facility.Phone.builder().main(phoneNumber).build().isEmpty()).isTrue();
    assertThat(Facility.Phone.builder().healthConnect(phoneNumber).build().isEmpty()).isTrue();
    assertThat(Facility.Phone.builder().fax(phoneNumber).build().isEmpty()).isTrue();
    assertThat(Facility.Phone.builder().mentalHealthClinic(phoneNumber).build().isEmpty()).isTrue();
    assertThat(Facility.Phone.builder().patientAdvocate(phoneNumber).build().isEmpty()).isTrue();
    assertThat(Facility.Phone.builder().pharmacy(phoneNumber).build().isEmpty()).isTrue();
    // Valid phone number
    phoneNumber = "202-555-1212";
    assertThat(Facility.Phone.builder().afterHours(phoneNumber).build().isEmpty()).isFalse();
    assertThat(Facility.Phone.builder().enrollmentCoordinator(phoneNumber).build().isEmpty())
        .isFalse();
    assertThat(Facility.Phone.builder().main(phoneNumber).build().isEmpty()).isFalse();
    assertThat(Facility.Phone.builder().healthConnect(phoneNumber).build().isEmpty()).isFalse();
    assertThat(Facility.Phone.builder().fax(phoneNumber).build().isEmpty()).isFalse();
    assertThat(Facility.Phone.builder().mentalHealthClinic(phoneNumber).build().isEmpty())
        .isFalse();
    assertThat(Facility.Phone.builder().patientAdvocate(phoneNumber).build().isEmpty()).isFalse();
    assertThat(Facility.Phone.builder().pharmacy(phoneNumber).build().isEmpty()).isFalse();
  }

  @Test
  @SneakyThrows
  void emptySatisfaction() {
    // Empty
    assertThat(Facility.Satisfaction.builder().build().isEmpty()).isTrue();
    assertThat(
            Facility.Satisfaction.builder()
                .health(Facility.PatientSatisfaction.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    // Not empty
    assertThat(
            Facility.Satisfaction.builder()
                .health(
                    Facility.PatientSatisfaction.builder()
                        .primaryCareRoutine(BigDecimal.ZERO)
                        .build())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(Facility.Satisfaction.builder().effectiveDate(LocalDate.now()).build().isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void emptyServices() {
    // Empty
    assertThat(Facility.Services.builder().build().isEmpty()).isTrue();
    assertThat(Facility.Services.builder().health(emptyList()).build().isEmpty()).isTrue();
    assertThat(Facility.Services.builder().other(emptyList()).build().isEmpty()).isTrue();
    assertThat(Facility.Services.builder().benefits(emptyList()).build().isEmpty()).isTrue();
    // Not empty
    final var linkerUrl = buildLinkerUrlV1("http://foo/", "bar");
    final var facilityId = "vha_558GA";
    assertThat(
            Facility.Services.builder()
                .health(
                    List.of(
                        Facility.Service.<Facility.HealthService>builder()
                            .serviceType(Facility.HealthService.PrimaryCare)
                            .name(Facility.HealthService.PrimaryCare.name())
                            .link(
                                buildTypedServiceLink(
                                    linkerUrl,
                                    facilityId,
                                    Facility.HealthService.PrimaryCare.serviceId()))
                            .build()))
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            Facility.Services.builder()
                .other(
                    List.of(
                        Facility.Service.<Facility.OtherService>builder()
                            .serviceType(Facility.OtherService.OnlineScheduling)
                            .name(Facility.OtherService.OnlineScheduling.name())
                            .link(
                                buildTypedServiceLink(
                                    linkerUrl,
                                    facilityId,
                                    Facility.OtherService.OnlineScheduling.serviceId()))
                            .build()))
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            Facility.Services.builder()
                .benefits(
                    List.of(
                        Facility.Service.<Facility.BenefitsService>builder()
                            .serviceType(Facility.BenefitsService.Pensions)
                            .name(Facility.BenefitsService.Pensions.name())
                            .link(
                                buildTypedServiceLink(
                                    linkerUrl,
                                    facilityId,
                                    Facility.BenefitsService.Pensions.serviceId()))
                            .build()))
                .build()
                .isEmpty())
        .isFalse();
    assertThat(Facility.Services.builder().lastUpdated(LocalDate.now()).build().isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void facilityAttributesInstructions() {
    assertThat(
            Facility.FacilityAttributes.builder().instructions(List.of("new instructions")).build())
        .usingRecursiveComparison()
        .isEqualTo(
            Facility.FacilityAttributes.builder()
                .operationalHoursSpecialInstructions(List.of("new instructions"))
                .build());
  }

  @Test
  @SneakyThrows
  void isEmpty() {
    // Empty
    assertThat(Facility.builder().build().isEmpty()).isTrue();
    assertThat(
            Facility.builder()
                .attributes(Facility.FacilityAttributes.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    assertThat(Facility.builder().id("   ").build().isEmpty()).isTrue();
    // Not empty
    assertThat(Facility.builder().id("test").build().isEmpty()).isFalse();
    assertThat(Facility.builder().type(Facility.Type.va_facilities).build().isEmpty()).isFalse();
    assertThat(
            Facility.builder()
                .attributes(
                    Facility.FacilityAttributes.builder()
                        .facilityType(Facility.FacilityType.va_health_facility)
                        .build())
                .build()
                .isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void satisfactionAndWaitTimesPopulated() {
    String expectedJson = getExpectedJson("v1/Facility/satisfactionAndWaitTimesPopulated.json");
    Facility facility =
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
                    .build())
            .build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(facility))
        .isEqualToIgnoringWhitespace(expectedJson);
    expectedJson =
        getExpectedJson("v1/Facility/satisfactionAndWaitTimesPopulatedOnlyAddress1.json");
    facility =
        Facility.builder()
            .id("vha_402")
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
                    .build())
            .build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(facility))
        .isEqualToIgnoringWhitespace(expectedJson);
  }
}
