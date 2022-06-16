package gov.va.api.lighthouse.facilities.api.v0;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class SearchByZipGeoJsonTest {
  @SneakyThrows
  private void assertReadable(String json) {
    GeoFacilitiesResponse f =
        createMapper().readValue(getClass().getResourceAsStream(json), GeoFacilitiesResponse.class);
    assertThat(f).isEqualTo(sample());
  }

  private Facility.PatientWaitTime patientWaitTime(
      Facility.HealthService service, Double newPat, Double oldPat) {
    Facility.PatientWaitTime.PatientWaitTimeBuilder waitTime = Facility.PatientWaitTime.builder();
    if (service != null) {
      waitTime.service(service);
    }
    if (newPat != null) {
      waitTime.newPatientWaitTime(BigDecimal.valueOf(newPat));
    }
    if (oldPat != null) {
      waitTime.establishedPatientWaitTime(BigDecimal.valueOf(oldPat));
    }
    return waitTime.build();
  }

  private GeoFacilitiesResponse sample() {
    return GeoFacilitiesResponse.builder()
        .type(GeoFacilitiesResponse.Type.FeatureCollection)
        .features(
            List.of(
                GeoFacility.builder()
                    .type(GeoFacility.Type.Feature)
                    .geometry(
                        GeoFacility.Geometry.builder()
                            .type(GeoFacility.GeometryType.Point)
                            .coordinates(
                                List.of(
                                    BigDecimal.valueOf(-80.73907113),
                                    BigDecimal.valueOf(28.2552385700001)))
                            .build())
                    .properties(
                        GeoFacility.Properties.builder()
                            .id("vha_675GA")
                            .name("Viera VA Clinic")
                            .facilityType(Facility.FacilityType.va_health_facility)
                            .classification("Health Care Center (HCC)")
                            .website("https://www.orlando.va.gov/locations/Viera.asp")
                            .timeZone("America/New_York")
                            .address(
                                Facility.Addresses.builder()
                                    .mailing(Facility.Address.builder().build())
                                    .physical(
                                        Facility.Address.builder()
                                            .zip("32940-8007")
                                            .city("Viera")
                                            .state("FL")
                                            .address1("2900 Veterans Way")
                                            .build())
                                    .build())
                            .phone(
                                Facility.Phone.builder()
                                    .fax("321-637-3515")
                                    .main("321-637-3788")
                                    .healthConnect("512-325-1255")
                                    .pharmacy("877-646-4550")
                                    .afterHours("877-741-3400")
                                    .patientAdvocate("407-631-1187")
                                    .mentalHealthClinic("321-637-3788")
                                    .enrollmentCoordinator("321-637-3527")
                                    .build())
                            .hours(
                                Facility.Hours.builder()
                                    .mon("730AM-430PM")
                                    .tues("730AM-430PM")
                                    .wed("730AM-430PM")
                                    .thurs("730AM-430PM")
                                    .fri("730AM-430PM")
                                    .sat("Closed")
                                    .sun("Closed")
                                    .build())
                            .services(
                                Facility.Services.builder()
                                    .other(new ArrayList<>())
                                    .health(
                                        List.of(
                                            Facility.HealthService.PrimaryCare,
                                            Facility.HealthService.MentalHealthCare,
                                            Facility.HealthService.Audiology,
                                            Facility.HealthService.Cardiology,
                                            Facility.HealthService.Dermatology,
                                            Facility.HealthService.Gastroenterology,
                                            Facility.HealthService.Ophthalmology,
                                            Facility.HealthService.Optometry,
                                            Facility.HealthService.Orthopedics,
                                            Facility.HealthService.Urology,
                                            Facility.HealthService.SpecialtyCare,
                                            Facility.HealthService.DentalServices))
                                    .lastUpdated(LocalDate.parse("2020-03-02"))
                                    .build())
                            .satisfaction(
                                Facility.Satisfaction.builder()
                                    .health(
                                        Facility.PatientSatisfaction.builder()
                                            .primaryCareUrgent(BigDecimal.valueOf(0.74))
                                            .primaryCareRoutine(BigDecimal.valueOf(0.83))
                                            .build())
                                    .effectiveDate(LocalDate.parse("2019-06-20"))
                                    .build())
                            .waitTimes(
                                Facility.WaitTimes.builder()
                                    .health(
                                        List.of(
                                            patientWaitTime(
                                                Facility.HealthService.Urology,
                                                32.047619,
                                                9.879032),
                                            patientWaitTime(
                                                Facility.HealthService.Audiology,
                                                1.706967,
                                                2.126855),
                                            patientWaitTime(
                                                Facility.HealthService.Optometry,
                                                76.396226,
                                                7.900787),
                                            patientWaitTime(
                                                Facility.HealthService.Cardiology, 18.657142, 6.4),
                                            patientWaitTime(
                                                Facility.HealthService.Dermatology,
                                                0.616666,
                                                0.555555),
                                            patientWaitTime(
                                                Facility.HealthService.Orthopedics,
                                                24.682539,
                                                4.995024),
                                            patientWaitTime(
                                                Facility.HealthService.PrimaryCare,
                                                26.405405,
                                                1.545372),
                                            patientWaitTime(
                                                Facility.HealthService.Ophthalmology,
                                                47.571428,
                                                3.258992),
                                            patientWaitTime(
                                                Facility.HealthService.SpecialtyCare,
                                                20.963572,
                                                5.775406),
                                            patientWaitTime(
                                                Facility.HealthService.Gastroenterology,
                                                22.151515,
                                                4.943661),
                                            patientWaitTime(
                                                Facility.HealthService.MentalHealthCare,
                                                7.592814,
                                                3.97159)))
                                    .effectiveDate(LocalDate.parse("2020-03-02"))
                                    .build())
                            .mobile(false)
                            .activeStatus(Facility.ActiveStatus.A)
                            .visn("8")
                            .build())
                    .build()))
        .build();
  }

  @Test
  void unmarshallSample() {
    assertReadable("/v0/search-zip-geojson.json");
  }
}
