package gov.va.api.lighthouse.facilities.api.v1;

import static gov.va.api.lighthouse.facilities.api.v1.SerializerUtil.createMapper;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class ReadHealthByIdJsonTest {
  @SneakyThrows
  private void assertReadable(String json) {
    FacilityReadResponse f =
        createMapper().readValue(getClass().getResourceAsStream(json), FacilityReadResponse.class);
    assertThat(f).isEqualTo(sample());
  }

  private Facility.PatientWaitTime patientWaitTime(
      Facility.HealthService service, Double newPat, Double oldPat) {
    Facility.PatientWaitTime.PatientWaitTimeBuilder wait = Facility.PatientWaitTime.builder();
    if (service != null) {
      wait.service(service);
    }
    if (newPat != null) {
      wait.newPatientWaitTime(BigDecimal.valueOf(newPat));
    }
    if (oldPat != null) {
      wait.establishedPatientWaitTime(BigDecimal.valueOf(oldPat));
    }

    return wait.build();
  }

  private FacilityReadResponse sample() {
    return FacilityReadResponse.builder()
        .facility(
            Facility.builder()
                .id("vha_402GA")
                .type(Facility.Type.va_facilities)
                .attributes(
                    Facility.FacilityAttributes.builder()
                        .name("Caribou VA Clinic")
                        .facilityType(Facility.FacilityType.va_health_facility)
                        .classification("Primary Care CBOC")
                        .website("https://www.maine.va.gov/locations/caribou.asp")
                        .latitude(BigDecimal.valueOf(46.8780264900001))
                        .longitude(BigDecimal.valueOf(-68.00939541))
                        .timeZone("America/New_York")
                        .address(
                            Facility.Addresses.builder()
                                .physical(
                                    Facility.Address.builder()
                                        .zip("04736-3567")
                                        .city("Caribou")
                                        .state("ME")
                                        .address1("163 Van Buren Road")
                                        .address3("Suite 6")
                                        .build())
                                .build())
                        .phone(
                            Facility.Phone.builder()
                                .fax("207-493-3877")
                                .main("207-493-3800")
                                .pharmacy("207-623-8411 x5770")
                                .afterHours("844-750-8426")
                                .patientAdvocate("207-623-5760")
                                .mentalHealthClinic("207-623-8411 x 7490")
                                .enrollmentCoordinator("207-623-8411 x5688")
                                .build())
                        .hours(
                            Facility.Hours.builder()
                                .monday("700AM-430PM")
                                .tuesday("700AM-430PM")
                                .wednesday("700AM-430PM")
                                .thursday("700AM-430PM")
                                .friday("700AM-430PM")
                                .saturday("Closed")
                                .sunday("Closed")
                                .build())
                        .services(
                            Facility.Services.builder()
                                .health(
                                    List.of(
                                        Facility.HealthService.EmergencyCare,
                                        Facility.HealthService.PrimaryCare,
                                        Facility.HealthService.MentalHealth,
                                        Facility.HealthService.Dermatology))
                                .lastUpdated(LocalDate.parse("2020-02-24"))
                                .build())
                        .satisfaction(
                            Facility.Satisfaction.builder()
                                .health(
                                    Facility.PatientSatisfaction.builder()
                                        .primaryCareUrgent(BigDecimal.valueOf(0.89))
                                        .primaryCareRoutine(BigDecimal.valueOf(0.91))
                                        .build())
                                .effectiveDate(LocalDate.parse("2019-06-20"))
                                .build())
                        .waitTimes(
                            Facility.WaitTimes.builder()
                                .health(
                                    List.of(
                                        patientWaitTime(
                                            Facility.HealthService.Dermatology, 3.714285, null),
                                        patientWaitTime(
                                            Facility.HealthService.PrimaryCare,
                                            13.727272,
                                            10.392441),
                                        patientWaitTime(
                                            Facility.HealthService.MentalHealth, 5.75, 2.634703)))
                                .effectiveDate(LocalDate.parse("2020-02-24"))
                                .build())
                        .mobile(false)
                        .activeStatus(Facility.ActiveStatus.A)
                        .visn("1")
                        .build())
                .build())
        .build();
  }

  @Test
  void unmarshallSample() {
    assertReadable("/v1/read-health.json");
  }
}
