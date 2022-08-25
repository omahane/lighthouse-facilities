package gov.va.api.lighthouse.facilities.api.v1;

import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildLinkerUrlV1;
import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildServicesLink;
import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildTypedServiceLink;
import static gov.va.api.lighthouse.facilities.api.v1.SerializerUtil.createMapper;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class SearchByZipJsonTest {
  @SneakyThrows
  private void assertReadable(String json) {
    FacilitiesResponse f =
        createMapper().readValue(getClass().getResourceAsStream(json), FacilitiesResponse.class);
    assertThat(f).isEqualTo(sample());
  }

  private DetailedService.PatientWaitTime patientWaitTime(
      Double newPat, Double oldPat, LocalDate effectDate) {
    DetailedService.PatientWaitTime.PatientWaitTimeBuilder waitTime =
        DetailedService.PatientWaitTime.builder();
    if (newPat != null) {
      waitTime.newPatientWaitTime(BigDecimal.valueOf(newPat));
    }
    if (oldPat != null) {
      waitTime.establishedPatientWaitTime(BigDecimal.valueOf(oldPat));
    }
    return waitTime.build();
  }

  private FacilitiesResponse sample() {
    final var linkerUrl = buildLinkerUrlV1("http://foo/", "bar");
    final var facilityId = "vha_675GA";
    return FacilitiesResponse.builder()
        .links(
            PageLinks.builder()
                .self("https://dev-api.vets.gov/services/va_facilities/v0/facilities?zip=32940")
                .first(
                    "https://dev-api.vets.gov/services/va_facilities/v0/facilities?page=1&per_page=10&zip=32940")
                .last(
                    "https://dev-api.vets.gov/services/va_facilities/v0/facilities?page=1&per_page=10&zip=32940")
                .build())
        .meta(
            FacilitiesResponse.FacilitiesMetadata.builder()
                .pagination(
                    Pagination.builder()
                        .currentPage(1)
                        .entriesPerPage(10)
                        .totalEntries(1)
                        .totalPages(1)
                        .build())
                .build())
        .data(
            List.of(
                Facility.builder()
                    .id(facilityId)
                    .type(Facility.Type.va_facilities)
                    .attributes(
                        Facility.FacilityAttributes.builder()
                            .name("Viera VA Clinic")
                            .facilityType(Facility.FacilityType.va_health_facility)
                            .classification("Health Care Center (HCC)")
                            .website("https://www.orlando.va.gov/locations/Viera.asp")
                            .latitude(BigDecimal.valueOf(28.2552385700001))
                            .longitude(BigDecimal.valueOf(-80.73907113))
                            .timeZone("America/New_York")
                            .address(
                                Facility.Addresses.builder()
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
                                    .healthConnect("312-122-4516")
                                    .pharmacy("877-646-4550")
                                    .afterHours("877-741-3400")
                                    .patientAdvocate("407-631-1187")
                                    .mentalHealthClinic("321-637-3788")
                                    .enrollmentCoordinator("321-637-3527")
                                    .build())
                            .hours(
                                Facility.Hours.builder()
                                    .monday("730AM-430PM")
                                    .tuesday("730AM-430PM")
                                    .wednesday("730AM-430PM")
                                    .thursday("730AM-430PM")
                                    .friday("730AM-430PM")
                                    .saturday("Closed")
                                    .sunday("Closed")
                                    .build())
                            .services(
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
                                                        Facility.HealthService.PrimaryCare
                                                            .serviceId()))
                                                .build(),
                                            Facility.Service.<Facility.HealthService>builder()
                                                .serviceType(Facility.HealthService.MentalHealth)
                                                .name(Facility.HealthService.MentalHealth.name())
                                                .link(
                                                    buildTypedServiceLink(
                                                        linkerUrl,
                                                        facilityId,
                                                        Facility.HealthService.MentalHealth
                                                            .serviceId()))
                                                .build(),
                                            Facility.Service.<Facility.HealthService>builder()
                                                .serviceType(Facility.HealthService.Audiology)
                                                .name(Facility.HealthService.Audiology.name())
                                                .link(
                                                    buildTypedServiceLink(
                                                        linkerUrl,
                                                        facilityId,
                                                        Facility.HealthService.Audiology
                                                            .serviceId()))
                                                .build(),
                                            Facility.Service.<Facility.HealthService>builder()
                                                .serviceType(Facility.HealthService.Cardiology)
                                                .name(Facility.HealthService.Cardiology.name())
                                                .link(
                                                    buildTypedServiceLink(
                                                        linkerUrl,
                                                        facilityId,
                                                        Facility.HealthService.Cardiology
                                                            .serviceId()))
                                                .build(),
                                            Facility.Service.<Facility.HealthService>builder()
                                                .serviceType(Facility.HealthService.Dermatology)
                                                .name(Facility.HealthService.Dermatology.name())
                                                .link(
                                                    buildTypedServiceLink(
                                                        linkerUrl,
                                                        facilityId,
                                                        Facility.HealthService.Dermatology
                                                            .serviceId()))
                                                .build(),
                                            Facility.Service.<Facility.HealthService>builder()
                                                .serviceType(
                                                    Facility.HealthService.Gastroenterology)
                                                .name(
                                                    Facility.HealthService.Gastroenterology.name())
                                                .link(
                                                    buildTypedServiceLink(
                                                        linkerUrl,
                                                        facilityId,
                                                        Facility.HealthService.Gastroenterology
                                                            .serviceId()))
                                                .build(),
                                            Facility.Service.<Facility.HealthService>builder()
                                                .serviceType(Facility.HealthService.Ophthalmology)
                                                .name(Facility.HealthService.Ophthalmology.name())
                                                .link(
                                                    buildTypedServiceLink(
                                                        linkerUrl,
                                                        facilityId,
                                                        Facility.HealthService.Ophthalmology
                                                            .serviceId()))
                                                .build(),
                                            Facility.Service.<Facility.HealthService>builder()
                                                .serviceType(Facility.HealthService.Optometry)
                                                .name(Facility.HealthService.Optometry.name())
                                                .link(
                                                    buildTypedServiceLink(
                                                        linkerUrl,
                                                        facilityId,
                                                        Facility.HealthService.Optometry
                                                            .serviceId()))
                                                .build(),
                                            Facility.Service.<Facility.HealthService>builder()
                                                .serviceType(Facility.HealthService.Orthopedics)
                                                .name(Facility.HealthService.Orthopedics.name())
                                                .link(
                                                    buildTypedServiceLink(
                                                        linkerUrl,
                                                        facilityId,
                                                        Facility.HealthService.Orthopedics
                                                            .serviceId()))
                                                .build(),
                                            Facility.Service.<Facility.HealthService>builder()
                                                .serviceType(Facility.HealthService.Urology)
                                                .name(Facility.HealthService.Urology.name())
                                                .link(
                                                    buildTypedServiceLink(
                                                        linkerUrl,
                                                        facilityId,
                                                        Facility.HealthService.Urology.serviceId()))
                                                .build(),
                                            Facility.Service.<Facility.HealthService>builder()
                                                .serviceType(Facility.HealthService.Dental)
                                                .name(Facility.HealthService.Dental.name())
                                                .link(
                                                    buildTypedServiceLink(
                                                        linkerUrl,
                                                        facilityId,
                                                        Facility.HealthService.Dental.serviceId()))
                                                .build()))
                                    .link(buildServicesLink(linkerUrl, facilityId))
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
                            .mobile(false)
                            .activeStatus(Facility.ActiveStatus.A)
                            .visn("8")
                            .build())
                    .build()))
        .build();
  }

  @Test
  void unmarshallSample() {
    assertReadable("/v1/search-zip.json");
  }
}
