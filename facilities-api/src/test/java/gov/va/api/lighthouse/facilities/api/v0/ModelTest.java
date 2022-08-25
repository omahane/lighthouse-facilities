package gov.va.api.lighthouse.facilities.api.v0;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand.Attributes;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand.Geometry;
import gov.va.api.lighthouse.facilities.api.v0.Facility.ActiveStatus;
import gov.va.api.lighthouse.facilities.api.v0.Facility.FacilityAttributes;
import gov.va.api.lighthouse.facilities.api.v0.Facility.FacilityType;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatus;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatusCode;
import gov.va.api.lighthouse.facilities.api.v0.Facility.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class ModelTest {
  private Facility.Addresses addresses() {
    return Facility.Addresses.builder()
        .mailing(
            Facility.Address.builder()
                .address1("1")
                .address2("Two")
                .address3("Drive")
                .city("Melbourne")
                .state("FL")
                .zip("32935")
                .build())
        .physical(Facility.Address.builder().address1("PO BOX 101").build())
        .build();
  }

  @Test
  void apiError() {
    roundTrip(
        ApiError.builder()
            .errors(
                List.of(
                    ApiError.ErrorMessage.builder()
                        .title("Hello")
                        .detail("Its Me.")
                        .code("43110")
                        .status("Error")
                        .build()))
            .build());
  }

  @Test
  void facilitiesReadResponse() {
    roundTrip(
        FacilityReadResponse.builder()
            .facility(
                Facility.builder()
                    .id("98")
                    .type(Facility.Type.va_facilities)
                    .attributes(
                        Facility.FacilityAttributes.builder()
                            .name("Shanktopod")
                            .facilityType(Facility.FacilityType.va_benefits_facility)
                            .classification("VA Medical Center (VAMC)")
                            .latitude(BigDecimal.valueOf(38.9311137))
                            .longitude(BigDecimal.valueOf(-77.0109110499999))
                            .website("http://www.washingtondc.va.gov/")
                            .address(addresses())
                            .phone(phones())
                            .hours(hours())
                            .services(services())
                            .satisfaction(satisfaction())
                            .waitTimes(waitTimes())
                            .mobile(false)
                            .activeStatus(Facility.ActiveStatus.T)
                            .visn("20")
                            .build())
                    .build())
            .build());
  }

  @Test
  void facilitiesSearchResponse() {
    roundTrip(
        FacilitiesResponse.builder()
            .meta(
                FacilitiesResponse.FacilitiesMetadata.builder()
                    .distances(
                        List.of(
                            FacilitiesResponse.Distance.builder()
                                .id("BigBoi")
                                .distance(BigDecimal.valueOf(120.95))
                                .build()))
                    .build())
            .data(List.of(facility()))
            .build());
  }

  private Facility facility() {
    return Facility.builder()
        .id("98")
        .type(Type.va_facilities)
        .attributes(
            FacilityAttributes.builder()
                .name("Shanktopod")
                .facilityType(FacilityType.va_benefits_facility)
                .classification("VA Medical Center (VAMC)")
                .latitude(BigDecimal.valueOf(38.9311137))
                .longitude(BigDecimal.valueOf(-77.0109110499999))
                .website("http://www.washingtondc.va.gov/")
                .address(addresses())
                .phone(phones())
                .hours(hours())
                .services(services())
                .satisfaction(satisfaction())
                .waitTimes(waitTimes())
                .mobile(false)
                .activeStatus(ActiveStatus.T)
                .operatingStatus(operatingStatus())
                .visn("20")
                .build())
        .build();
  }

  @Test
  void genericError() {
    roundTrip(GenericError.builder().message("First Try Baby").build());
  }

  @Test
  void geoFacilitiesResponse() {
    roundTrip(
        GeoFacilitiesResponse.builder()
            .type(GeoFacilitiesResponse.Type.FeatureCollection)
            .features(List.of(geoFacility()))
            .build());
  }

  private GeoFacility geoFacility() {
    return GeoFacility.builder()
        .type(GeoFacility.Type.Feature)
        .geometry(
            GeoFacility.Geometry.builder()
                .type(GeoFacility.GeometryType.Point)
                .coordinates(
                    List.of(BigDecimal.valueOf(-77.0367761), BigDecimal.valueOf(38.9004181)))
                .build())
        .properties(
            GeoFacility.Properties.builder()
                .id("1234")
                .name("Cutsortium")
                .facilityType(Facility.FacilityType.va_health_facility)
                .classification("VA Medical Center (VAMC)")
                .website("http://www.washingtondc.va.gov")
                .address(addresses())
                .phone(phones())
                .hours(hours())
                .services(services())
                .satisfaction(satisfaction())
                .waitTimes(waitTimes())
                .mobile(true)
                .activeStatus(Facility.ActiveStatus.A)
                .operatingStatus(operatingStatus())
                .visn("20")
                .build())
        .build();
  }

  @Test
  void geoFacilityReadResponse() {
    roundTrip(GeoFacilityReadResponse.of(geoFacility()));
  }

  @Test
  void geoFacilityRoundTrip() {
    roundTrip(geoFacility());
  }

  private Facility.Hours hours() {
    return Facility.Hours.builder()
        .monday("CLOSED")
        .tuesday("CLOSED")
        .wednesday("CLOSED")
        .thursday("CLOSED")
        .friday("CLOSED")
        .saturday("CLOSED")
        .sunday("CLOSED")
        .build();
  }

  @Test
  void nearbyFacility() {
    roundTrip(
        NearbyResponse.builder()
            .data(
                List.of(
                    NearbyResponse.Nearby.builder()
                        .id("8")
                        .type(NearbyResponse.Type.NearbyFacility)
                        .attributes(
                            NearbyResponse.NearbyAttributes.builder()
                                .minTime(10)
                                .maxTime(20)
                                .build())
                        .build()))
            .build());
  }

  private OperatingStatus operatingStatus() {
    return OperatingStatus.builder()
        .code(OperatingStatusCode.NORMAL)
        .additionalInfo("rando text")
        .build();
  }

  private Facility.Phone phones() {
    return Facility.Phone.builder()
        .patientAdvocate("123-456-7989")
        .mentalHealthClinic("7412589630")
        .enrollmentCoordinator("1594782360")
        .main("(123)456-7890")
        .fax("(456)678-1230")
        .pharmacy("789-456-1230")
        .afterHours("(123)456-7890")
        .build();
  }

  @Test
  void pssgDriveTimeBand() {
    List<List<Double>> ring1 = PssgDriveTimeBand.newRing(2);
    ring1.add(PssgDriveTimeBand.coord(1, 2));
    ring1.add(PssgDriveTimeBand.coord(3, 4));

    List<List<Double>> ring2 = PssgDriveTimeBand.newRing(2);
    ring2.add(PssgDriveTimeBand.coord(5, 6));
    ring2.add(PssgDriveTimeBand.coord(7, 8));

    List<List<List<Double>>> rings = PssgDriveTimeBand.newListOfRings();
    rings.add(ring1);
    rings.add(ring2);

    roundTrip(
        PssgDriveTimeBand.builder()
            .attributes(Attributes.builder().stationNumber("No1").fromBreak(10).toBreak(20).build())
            .geometry(Geometry.builder().rings(rings).build())
            .build());
  }

  @SneakyThrows
  private <T> void roundTrip(T object) {
    ObjectMapper mapper = JacksonConfig.createMapper();
    String json = mapper.writeValueAsString(object);
    Object evilTwin = mapper.readValue(json, object.getClass());
    assertThat(evilTwin).isEqualTo(object);
  }

  private Facility.Satisfaction satisfaction() {
    return Facility.Satisfaction.builder()
        .health(
            Facility.PatientSatisfaction.builder()
                .primaryCareUrgent(BigDecimal.valueOf(8.0))
                .specialtyCareRoutine(BigDecimal.valueOf(8.0))
                .specialtyCareUrgent(BigDecimal.valueOf(8.0))
                .primaryCareRoutine(BigDecimal.valueOf(8.0))
                .build())
        .effectiveDate(LocalDate.parse("2020-01-20"))
        .build();
  }

  private Facility.Services services() {
    return Facility.Services.builder()
        .benefits(List.of(Facility.BenefitsService.eBenefitsRegistrationAssistance))
        .lastUpdated(LocalDate.parse("2020-03-12"))
        .build();
  }

  private Facility.WaitTimes waitTimes() {
    return Facility.WaitTimes.builder()
        .health(
            List.of(
                Facility.PatientWaitTime.builder()
                    .newPatientWaitTime(BigDecimal.valueOf(25))
                    .establishedPatientWaitTime(BigDecimal.valueOf(10))
                    .service(Facility.HealthService.Audiology)
                    .build()))
        .effectiveDate(LocalDate.parse("2020-03-12"))
        .build();
  }
}
