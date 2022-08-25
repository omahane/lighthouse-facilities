package gov.va.api.lighthouse.facilities.api.v1.serializers;

import static gov.va.api.lighthouse.facilities.api.v1.SerializerUtil.createMapper;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.v1.CanBeEmpty;
import gov.va.api.lighthouse.facilities.api.v1.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v1.CmsOverlayResponse;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService;
import gov.va.api.lighthouse.facilities.api.v1.DetailedServiceResponse;
import gov.va.api.lighthouse.facilities.api.v1.DetailedServicesResponse;
import gov.va.api.lighthouse.facilities.api.v1.FacilitiesIdsResponse;
import gov.va.api.lighthouse.facilities.api.v1.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import gov.va.api.lighthouse.facilities.api.v1.FacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v1.NearbyResponse;
import gov.va.api.lighthouse.facilities.api.v1.PageLinks;
import gov.va.api.lighthouse.facilities.api.v1.Pagination;
import gov.va.api.lighthouse.facilities.api.v1.ReloadResponse;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class SerializerTest {
  @SneakyThrows
  private <T extends CanBeEmpty> void assertJson(T value, String expectedJson) {
    assertThat(createMapper().writeValueAsString(value)).isEqualTo(expectedJson);
  }

  @SneakyThrows
  private <T extends CanBeEmpty> void assertJsonIsEmpty(T value) {
    assertJson(value, "{}");
  }

  @Test
  @SneakyThrows
  void serializeAddress() {
    // Empty
    Facility.Address address = Facility.Address.builder().build();
    assertJsonIsEmpty(address);
    address = Facility.Address.builder().city("   ").build();
    assertJsonIsEmpty(address);
    // Not empty
    address = Facility.Address.builder().city("Melbourne").build();
    assertJson(address, "{\"city\":\"Melbourne\"}");
    Facility facility =
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
                    .build())
            .build();
    assertJson(
        facility,
        "{\"id\":\"nca_s402\",\"attributes\":{\"address\":{\"mailing\":{\"address1\":\"50 Irving Street, Northwest\"}}}}");
  }

  @Test
  @SneakyThrows
  void serializeAddresses() {
    // Empty
    Facility.Addresses addresses = Facility.Addresses.builder().build();
    assertJsonIsEmpty(addresses);
    addresses = Facility.Addresses.builder().mailing(Facility.Address.builder().build()).build();
    assertJsonIsEmpty(addresses);
    // Not empty
    addresses =
        Facility.Addresses.builder()
            .mailing(Facility.Address.builder().state("FL").build())
            .build();
    assertJson(addresses, "{\"mailing\":{\"state\":\"FL\"}}");
  }

  @Test
  @SneakyThrows
  void serializeCmsOverlay() {
    // Empty
    CmsOverlay overlay = CmsOverlay.builder().build();
    assertJsonIsEmpty(overlay);
    overlay = CmsOverlay.builder().detailedServices(emptyList()).build();
    assertJsonIsEmpty(overlay);
    // Not empty
    overlay =
        CmsOverlay.builder()
            .operatingStatus(
                Facility.OperatingStatus.builder()
                    .code(Facility.OperatingStatusCode.NORMAL)
                    .build())
            .build();
    assertJson(overlay, "{\"operatingStatus\":{\"code\":\"NORMAL\"}}");
  }

  @Test
  @SneakyThrows
  void serializeCmsOverlayResponse() {
    // Empty
    CmsOverlayResponse response = CmsOverlayResponse.builder().build();
    assertJsonIsEmpty(response);
    response = CmsOverlayResponse.builder().overlay(CmsOverlay.builder().build()).build();
    assertJsonIsEmpty(response);
    // Not empty
    response =
        CmsOverlayResponse.builder()
            .overlay(
                CmsOverlay.builder()
                    .operatingStatus(
                        Facility.OperatingStatus.builder()
                            .code(Facility.OperatingStatusCode.NORMAL)
                            .build())
                    .build())
            .build();
    assertJson(response, "{\"overlay\":{\"operatingStatus\":{\"code\":\"NORMAL\"}}}");
  }

  @Test
  @SneakyThrows
  void serializeDetailedService() {
    // Not empty
    DetailedService detailedService =
        DetailedService.builder()
            .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
            .build();
    assertJson(detailedService, "{\"serviceId\":\"cardiology\"}");
    detailedService =
        DetailedService.builder()
            .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
            .serviceLocations(emptyList())
            .build();
    assertJson(detailedService, "{\"serviceId\":\"cardiology\"}");
    detailedService =
        DetailedService.builder()
            .serviceId(uncapitalize(Facility.HealthService.Covid19Vaccine.name()))
            .name("COVID-19 vaccines")
            .build();
    assertJson(
        detailedService, "{\"serviceId\":\"covid19Vaccine\",\"name\":\"COVID-19 vaccines\"}");
  }

  @Test
  @SneakyThrows
  void serializeDetailedServiceAddress() {
    // Empty
    DetailedService.DetailedServiceAddress detailedServiceAddress =
        DetailedService.DetailedServiceAddress.builder().build();
    assertJsonIsEmpty(detailedServiceAddress);
    detailedServiceAddress = DetailedService.DetailedServiceAddress.builder().city("   ").build();
    assertJsonIsEmpty(detailedServiceAddress);
    // Not empty
    detailedServiceAddress = DetailedService.DetailedServiceAddress.builder().state("FL").build();
    assertJson(detailedServiceAddress, "{\"state\":\"FL\"}");
  }

  @Test
  @SneakyThrows
  void serializeDetailedServiceAppointmentPhoneNumber() {
    // Empty
    DetailedService.AppointmentPhoneNumber appointmentPhoneNumber =
        DetailedService.AppointmentPhoneNumber.builder().build();
    assertJsonIsEmpty(appointmentPhoneNumber);
    appointmentPhoneNumber = DetailedService.AppointmentPhoneNumber.builder().number("   ").build();
    assertJsonIsEmpty(appointmentPhoneNumber);
    // Not empty
    appointmentPhoneNumber =
        DetailedService.AppointmentPhoneNumber.builder().number("937-268-6511").build();
    assertJson(appointmentPhoneNumber, "{\"number\":\"937-268-6511\"}");
  }

  @Test
  @SneakyThrows
  void serializeDetailedServiceEmailContact() {
    // Empty
    DetailedService.DetailedServiceEmailContact emailContact =
        DetailedService.DetailedServiceEmailContact.builder().build();
    assertJsonIsEmpty(emailContact);
    emailContact =
        DetailedService.DetailedServiceEmailContact.builder().emailAddress("   ").build();
    assertJsonIsEmpty(emailContact);
    // Not empty
    emailContact =
        DetailedService.DetailedServiceEmailContact.builder()
            .emailAddress("georgea@va.gov")
            .build();
    assertJson(emailContact, "{\"emailAddress\":\"georgea@va.gov\"}");
  }

  @Test
  @SneakyThrows
  void serializeDetailedServiceHours() {
    // Empty
    DetailedService.DetailedServiceHours hours =
        DetailedService.DetailedServiceHours.builder().build();
    assertJsonIsEmpty(hours);
    hours = DetailedService.DetailedServiceHours.builder().monday("   ").build();
    assertJsonIsEmpty(hours);
    // Not empty
    hours = DetailedService.DetailedServiceHours.builder().saturday("Closed").build();
    assertJson(hours, "{\"saturday\":\"Closed\"}");
  }

  @Test
  @SneakyThrows
  void serializeDetailedServiceLocation() {
    // Empty
    DetailedService.DetailedServiceLocation serviceLocation =
        DetailedService.DetailedServiceLocation.builder().build();
    assertJsonIsEmpty(serviceLocation);
    serviceLocation =
        DetailedService.DetailedServiceLocation.builder()
            .appointmentPhoneNumbers(emptyList())
            .build();
    assertJsonIsEmpty(serviceLocation);
    // Not empty
    serviceLocation =
        DetailedService.DetailedServiceLocation.builder()
            .additionalHoursInfo("additional hours info")
            .build();
    assertJson(serviceLocation, "{\"additionalHoursInfo\":\"additional hours info\"}");
  }

  @Test
  @SneakyThrows
  void serializeDetailedServiceResponse() {
    // Empty
    DetailedServiceResponse response = DetailedServiceResponse.builder().build();
    assertJsonIsEmpty(response);
    // Not empty
    response =
        DetailedServiceResponse.builder()
            .data(
                DetailedService.builder()
                    .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
                    .build())
            .build();
    assertJson(response, "{\"data\":{\"serviceId\":\"cardiology\"}}");
    response =
        DetailedServiceResponse.builder()
            .data(
                DetailedService.builder()
                    .serviceId(uncapitalize(Facility.HealthService.Covid19Vaccine.name()))
                    .name("COVID-19 vaccines")
                    .build())
            .build();
    assertJson(
        response, "{\"data\":{\"serviceId\":\"covid19Vaccine\",\"name\":\"COVID-19 vaccines\"}}");
  }

  @Test
  @SneakyThrows
  void serializeDetailedServicesMetadata() {
    // Empty
    DetailedServicesResponse.DetailedServicesMetadata metadata =
        DetailedServicesResponse.DetailedServicesMetadata.builder().build();
    assertJsonIsEmpty(metadata);
    metadata =
        DetailedServicesResponse.DetailedServicesMetadata.builder()
            .pagination(Pagination.builder().build())
            .build();
    assertJsonIsEmpty(metadata);
    // Not empty
    metadata =
        DetailedServicesResponse.DetailedServicesMetadata.builder()
            .pagination(Pagination.builder().totalEntries(10).build())
            .build();
    assertJson(metadata, "{\"pagination\":{\"totalEntries\":10}}");
  }

  @Test
  @SneakyThrows
  void serializeDetailedServicesResponse() {
    // Empty
    DetailedServicesResponse response = DetailedServicesResponse.builder().build();
    assertJsonIsEmpty(response);
    response = DetailedServicesResponse.builder().data(emptyList()).build();
    assertJsonIsEmpty(response);
    // Not empty
    response =
        DetailedServicesResponse.builder()
            .links(PageLinks.builder().self("http://foo.bar").build())
            .build();
    assertJson(response, "{\"links\":{\"self\":\"http://foo.bar\"}}");
  }

  @Test
  @SneakyThrows
  void serializeDistance() {
    // Empty
    FacilitiesResponse.Distance distance = FacilitiesResponse.Distance.builder().build();
    assertJsonIsEmpty(distance);
    distance = FacilitiesResponse.Distance.builder().id("   ").build();
    assertJsonIsEmpty(distance);
    // Not empty
    distance = FacilitiesResponse.Distance.builder().distance(BigDecimal.TEN).build();
    assertJson(distance, "{\"distance\":10}");
  }

  @Test
  @SneakyThrows
  void serializeFacilitiesMetadata() {
    // Empty
    FacilitiesResponse.FacilitiesMetadata metadata =
        FacilitiesResponse.FacilitiesMetadata.builder().build();
    assertJsonIsEmpty(metadata);
    metadata = FacilitiesResponse.FacilitiesMetadata.builder().distances(emptyList()).build();
    assertJsonIsEmpty(metadata);
    // Not empty
    metadata =
        FacilitiesResponse.FacilitiesMetadata.builder()
            .pagination(Pagination.builder().totalEntries(10).build())
            .build();
    assertJson(metadata, "{\"pagination\":{\"totalEntries\":10}}");
  }

  @Test
  @SneakyThrows
  void serializeFacilitiesResponse() {
    // Empty
    FacilitiesResponse response = FacilitiesResponse.builder().build();
    assertJsonIsEmpty(response);
    response = FacilitiesResponse.builder().data(emptyList()).build();
    assertJsonIsEmpty(response);
    response = FacilitiesResponse.builder().links(PageLinks.builder().first("   ").build()).build();
    assertJsonIsEmpty(response);
    // Not empty
    response =
        FacilitiesResponse.builder()
            .links(PageLinks.builder().self("http://foo.bar").build())
            .build();
    assertJson(response, "{\"links\":{\"self\":\"http://foo.bar\"}}");
  }

  @Test
  @SneakyThrows
  void serializeFacility() {
    // Empty
    Facility facility = Facility.builder().build();
    assertJsonIsEmpty(facility);
    facility =
        Facility.builder()
            .attributes(Facility.FacilityAttributes.builder().instructions(emptyList()).build())
            .build();
    assertJsonIsEmpty(facility);
    facility = Facility.builder().id("   ").build();
    assertJsonIsEmpty(facility);
    // Not empty
    facility = Facility.builder().type(Facility.Type.va_facilities).build();
    assertJson(facility, "{\"type\":\"va_facilities\"}");
  }

  @Test
  @SneakyThrows
  void serializeFacilityAttributes() {
    // Empty
    Facility.FacilityAttributes attributes = Facility.FacilityAttributes.builder().build();
    assertJsonIsEmpty(attributes);
    attributes =
        Facility.FacilityAttributes.builder().address(Facility.Addresses.builder().build()).build();
    assertJsonIsEmpty(attributes);
    attributes =
        Facility.FacilityAttributes.builder()
            .address(
                Facility.Addresses.builder()
                    .mailing(Facility.Address.builder().address1("   ").build())
                    .build())
            .build();
    assertJsonIsEmpty(attributes);
    // Not empty
    attributes =
        Facility.FacilityAttributes.builder()
            .address(
                Facility.Addresses.builder()
                    .mailing(Facility.Address.builder().city("  Melbourne  ").build())
                    .build())
            .build();
    assertJson(attributes, "{\"address\":{\"mailing\":{\"city\":\"Melbourne\"}}}");
  }

  @Test
  @SneakyThrows
  void serializeFacilityIdsResponse() {
    // Empty
    FacilitiesIdsResponse response = FacilitiesIdsResponse.builder().build();
    assertJsonIsEmpty(response);
    response = FacilitiesIdsResponse.builder().data(emptyList()).build();
    assertJsonIsEmpty(response);
    // Not empty
    response = FacilitiesIdsResponse.builder().data(List.of("[\"vha_688\"]")).build();
    assertJson(response, "{\"data\":[\"[\\\"vha_688\\\"]\"]}");
  }

  @Test
  @SneakyThrows
  void serializeFacilityReadResponse() {
    // Empty
    FacilityReadResponse response = FacilityReadResponse.builder().build();
    assertJsonIsEmpty(response);
    response = FacilityReadResponse.builder().facility(Facility.builder().build()).build();
    assertJsonIsEmpty(response);
    response =
        FacilityReadResponse.builder().facility(Facility.builder().id("   ").build()).build();
    assertJsonIsEmpty(response);
    // Not empty
    response =
        FacilityReadResponse.builder()
            .facility(Facility.builder().type(Facility.Type.va_facilities).build())
            .build();
    assertJson(response, "{\"data\":{\"type\":\"va_facilities\"}}");
  }

  @Test
  @SneakyThrows
  void serializeHours() {
    // Empty
    Facility.Hours hours = Facility.Hours.builder().build();
    assertJsonIsEmpty(hours);
    hours = Facility.Hours.builder().sunday("   ").build();
    assertJsonIsEmpty(hours);
    // Not empty
    hours = Facility.Hours.builder().saturday("   Closed  ").build();
    assertJson(hours, "{\"saturday\":\"Closed\"}");
  }

  @Test
  @SneakyThrows
  void serializeMeta() {
    // Empty
    NearbyResponse.Meta meta = NearbyResponse.Meta.builder().build();
    assertJsonIsEmpty(meta);
    meta = NearbyResponse.Meta.builder().bandVersion("   ").build();
    assertJsonIsEmpty(meta);
    // Not empty
    meta = NearbyResponse.Meta.builder().bandVersion("APR2021").build();
    assertJson(meta, "{\"bandVersion\":\"APR2021\"}");
  }

  @Test
  @SneakyThrows
  void serializeNearby() {
    // Empty
    NearbyResponse.Nearby nearby = NearbyResponse.Nearby.builder().build();
    assertJsonIsEmpty(nearby);
    nearby = NearbyResponse.Nearby.builder().id("   ").build();
    assertJsonIsEmpty(nearby);
    nearby =
        NearbyResponse.Nearby.builder()
            .attributes(NearbyResponse.NearbyAttributes.builder().build())
            .build();
    assertJsonIsEmpty(nearby);
    // Not empty
    nearby = NearbyResponse.Nearby.builder().type(NearbyResponse.Type.NearbyFacility).build();
    assertJson(nearby, "{\"type\":\"nearby_facility\"}");
  }

  @Test
  @SneakyThrows
  void serializeNearbyAttributes() {
    // Empty
    NearbyResponse.NearbyAttributes attributes = NearbyResponse.NearbyAttributes.builder().build();
    assertJsonIsEmpty(attributes);
    attributes = NearbyResponse.NearbyAttributes.builder().minTime(null).build();
    assertJsonIsEmpty(attributes);
    // Not empty
    attributes = NearbyResponse.NearbyAttributes.builder().maxTime(Integer.MAX_VALUE).build();
    assertJson(attributes, "{\"maxTime\":2147483647}");
  }

  @Test
  @SneakyThrows
  void serializeNearbyResponse() {
    // Empty
    NearbyResponse response = NearbyResponse.builder().build();
    assertJsonIsEmpty(response);
    response = NearbyResponse.builder().data(emptyList()).build();
    assertJsonIsEmpty(response);
    // Not empty
    response =
        NearbyResponse.builder()
            .meta(NearbyResponse.Meta.builder().bandVersion("MAR2021").build())
            .build();
    assertJson(response, "{\"meta\":{\"bandVersion\":\"MAR2021\"}}");
  }

  @Test
  @SneakyThrows
  void serializeOperatingStatus() {
    // Empty
    Facility.OperatingStatus status = Facility.OperatingStatus.builder().build();
    assertJsonIsEmpty(status);
    status = Facility.OperatingStatus.builder().additionalInfo("   ").build();
    assertJsonIsEmpty(status);
    // Not empty
    status = Facility.OperatingStatus.builder().code(Facility.OperatingStatusCode.NORMAL).build();
    assertJson(status, "{\"code\":\"NORMAL\"}");
  }

  @Test
  @SneakyThrows
  void serializePageLinks() {
    // Empty
    PageLinks links = PageLinks.builder().build();
    assertJsonIsEmpty(links);
    links = PageLinks.builder().next("   ").build();
    assertJsonIsEmpty(links);
    // Not empty
    links = PageLinks.builder().last("  http://foo.bar/biz   ").build();
    assertJson(links, "{\"last\":\"http://foo.bar/biz\"}");
  }

  @Test
  @SneakyThrows
  void serializePagination() {
    // Empty
    Pagination pagination = Pagination.builder().build();
    assertJsonIsEmpty(pagination);
    // Not empty
    pagination = Pagination.builder().totalEntries(10).build();
    assertJson(pagination, "{\"totalEntries\":10}");
  }

  @Test
  @SneakyThrows
  void serializePatientWaitTimes() {
    // Empty
    Facility.PatientWaitTime waitTimes = Facility.PatientWaitTime.builder().build();
    assertJsonIsEmpty(waitTimes);
    // Not empty
    waitTimes =
        Facility.PatientWaitTime.builder().service(Facility.HealthService.Cardiology).build();
    assertJson(waitTimes, "{\"service\":\"cardiology\"}");
  }

  @Test
  @SneakyThrows
  void serializePhone() {
    // Empty
    Facility.Phone phone = Facility.Phone.builder().build();
    assertJsonIsEmpty(phone);
    phone = Facility.Phone.builder().fax("   ").build();
    assertJsonIsEmpty(phone);
    // Not empty
    phone = Facility.Phone.builder().main("202-555-1212").build();
    assertJson(phone, "{\"main\":\"202-555-1212\"}");
  }

  @Test
  @SneakyThrows
  void serializeReloadResponse() {
    // Empty
    ReloadResponse response = ReloadResponse.builder().build();
    assertJsonIsEmpty(response);
    response = ReloadResponse.builder().problems(emptyList()).build();
    assertJsonIsEmpty(response);
    // Not empty
    response = ReloadResponse.builder().facilitiesCreated(List.of("[\"vha_402\"]")).build();
    assertJson(response, "{\"facilitiesCreated\":[\"[\\\"vha_402\\\"]\"]}");
  }

  @Test
  @SneakyThrows
  void serializeReloadResponseProblem() {
    // Empty
    ReloadResponse.Problem problem = ReloadResponse.Problem.builder().build();
    assertJsonIsEmpty(problem);
    problem = ReloadResponse.Problem.builder().description("   ").build();
    assertJsonIsEmpty(problem);
    // Not empty
    problem = ReloadResponse.Problem.builder().facilityId("vha_402").build();
    assertJson(problem, "{\"facilityId\":\"vha_402\"}");
  }

  @Test
  @SneakyThrows
  void serializeReloadResponseTiming() {
    // Empty
    ReloadResponse.Timing timing = ReloadResponse.Timing.builder().build();
    assertJsonIsEmpty(timing);
    timing = ReloadResponse.Timing.builder().start(null).build();
    assertJsonIsEmpty(timing);
    // Not empty
    timing =
        ReloadResponse.Timing.builder().complete(Instant.parse("2022-01-19T02:20:00Z")).build();
    assertJson(timing, "{\"complete\":\"2022-01-19T02:20:00Z\"}");
  }

  @Test
  @SneakyThrows
  void serializeSatisfaction() {
    // Empty
    Facility.Satisfaction satisfaction = Facility.Satisfaction.builder().build();
    assertJsonIsEmpty(satisfaction);
    satisfaction =
        Facility.Satisfaction.builder()
            .health(Facility.PatientSatisfaction.builder().build())
            .build();
    assertJsonIsEmpty(satisfaction);
    // Not empty
    satisfaction =
        Facility.Satisfaction.builder()
            .health(
                Facility.PatientSatisfaction.builder().primaryCareUrgent(BigDecimal.TEN).build())
            .build();
    assertJson(satisfaction, "{\"health\":{\"primaryCareUrgent\":10}}");
  }

  @Test
  @SneakyThrows
  void serializeServices() {
    // Empty
    Facility.Services services = Facility.Services.builder().build();
    assertJsonIsEmpty(services);
    services = Facility.Services.builder().health(emptyList()).build();
    assertJsonIsEmpty(services);
    // Not empty
    services =
        Facility.Services.builder().benefits(List.of(Facility.BenefitsService.Pensions)).build();
    assertJson(services, "{\"benefits\":[\"Pensions\"]}");
  }

  @Test
  @SneakyThrows
  void serializeWaitTimes() {
    // Empty
    Facility.WaitTimes waitTimes = Facility.WaitTimes.builder().build();
    assertJsonIsEmpty(waitTimes);
    // Not empty
    waitTimes = Facility.WaitTimes.builder().effectiveDate(LocalDate.parse("2021-12-25")).build();
    assertJson(waitTimes, "{\"effectiveDate\":\"2021-12-25\"}");
  }
}
