package gov.va.api.lighthouse.facilities.api.v1.serializers;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.CanBeEmpty;
import gov.va.api.lighthouse.facilities.api.v1.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v1.CmsOverlayResponse;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService;
import gov.va.api.lighthouse.facilities.api.v1.DetailedServiceResponse;
import gov.va.api.lighthouse.facilities.api.v1.DetailedServicesResponse;
import gov.va.api.lighthouse.facilities.api.v1.FacilitiesIdsResponse;
import gov.va.api.lighthouse.facilities.api.v1.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import gov.va.api.lighthouse.facilities.api.v1.Facility.Address;
import gov.va.api.lighthouse.facilities.api.v1.Facility.Addresses;
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

public class SerializerIsEmptyTest {
  @Test
  @SneakyThrows
  void addressIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new AddressSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Address.builder().build(), new AddressSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Address.builder().city("   ").build(),
        new AddressSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        Address.builder().city("Melbourne").build(),
        new AddressSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void addressesIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new AddressesSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Addresses.builder().build(), new AddressesSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Addresses.builder().mailing(Address.builder().build()).build(),
        new AddressesSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Addresses.builder().mailing(Address.builder().city("   ").build()).build(),
        new AddressesSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        Addresses.builder().mailing(Address.builder().city("Melbourne").build()).build(),
        new AddressesSerializer(),
        mock(SerializerProvider.class));
  }

  @SneakyThrows
  private <T extends CanBeEmpty, U extends NonEmptySerializer<T>>
      void assertIsEmptyUsingObjectSerializer(T value, U serializer, SerializerProvider provider) {
    assertThat(serializer.isEmpty(provider, value)).isTrue();
  }

  @SneakyThrows
  private <T extends CanBeEmpty, U extends NonEmptySerializer<T>>
      void assertIsNotEmptyUsingObjectSerializer(
          T value, U serializer, SerializerProvider provider) {
    assertThat(serializer.isEmpty(provider, value)).isFalse();
  }

  @Test
  @SneakyThrows
  void cmsOverlayIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new CmsOverlaySerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        CmsOverlay.builder().build(), new CmsOverlaySerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        CmsOverlay.builder().detailedServices(emptyList()).build(),
        new CmsOverlaySerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        CmsOverlay.builder()
            .operatingStatus(
                Facility.OperatingStatus.builder()
                    .code(Facility.OperatingStatusCode.NORMAL)
                    .build())
            .build(),
        new CmsOverlaySerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void cmsOverlayResponseIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new CmsOverlayResponseSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        CmsOverlayResponse.builder().build(),
        new CmsOverlayResponseSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        CmsOverlayResponse.builder()
            .overlay(CmsOverlay.builder().detailedServices(emptyList()).build())
            .build(),
        new CmsOverlayResponseSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        CmsOverlayResponse.builder()
            .overlay(
                CmsOverlay.builder()
                    .operatingStatus(
                        Facility.OperatingStatus.builder()
                            .code(Facility.OperatingStatusCode.NORMAL)
                            .build())
                    .build())
            .build(),
        new CmsOverlayResponseSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void detailedServiceAddressIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new DetailedServiceAddressSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        DetailedService.DetailedServiceAddress.builder().build(),
        new DetailedServiceAddressSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        DetailedService.DetailedServiceAddress.builder().city("   ").build(),
        new DetailedServiceAddressSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        DetailedService.DetailedServiceAddress.builder().city("Melbourne").build(),
        new DetailedServiceAddressSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void detailedServiceAppointmentPhoneNumberIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null,
        new DetailedServiceAppointmentPhoneNumberSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        DetailedService.AppointmentPhoneNumber.builder().build(),
        new DetailedServiceAppointmentPhoneNumberSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        DetailedService.AppointmentPhoneNumber.builder().label("   ").build(),
        new DetailedServiceAppointmentPhoneNumberSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        DetailedService.AppointmentPhoneNumber.builder().label("test").build(),
        new DetailedServiceAppointmentPhoneNumberSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void detailedServiceEmailContactIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new DetailedServiceEmailContactSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        DetailedService.DetailedServiceEmailContact.builder().build(),
        new DetailedServiceEmailContactSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        DetailedService.DetailedServiceEmailContact.builder().emailLabel("   ").build(),
        new DetailedServiceEmailContactSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        DetailedService.DetailedServiceEmailContact.builder().emailLabel("test").build(),
        new DetailedServiceEmailContactSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void detailedServiceHoursIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new DetailedServiceHoursSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        DetailedService.DetailedServiceHours.builder().build(),
        new DetailedServiceHoursSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        DetailedService.DetailedServiceHours.builder().tuesday("   ").build(),
        new DetailedServiceHoursSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        DetailedService.DetailedServiceHours.builder().monday("9AM-5PM").build(),
        new DetailedServiceHoursSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void detailedServiceIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new DetailedServiceSerializer(), mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        DetailedService.builder()
            .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
            .build(),
        new DetailedServiceSerializer(),
        mock(SerializerProvider.class));
    assertIsNotEmptyUsingObjectSerializer(
        DetailedService.builder()
            .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
            .name("   ")
            .build(),
        new DetailedServiceSerializer(),
        mock(SerializerProvider.class));
    assertIsNotEmptyUsingObjectSerializer(
        DetailedService.builder()
            .serviceId(uncapitalize(uncapitalize(Facility.HealthService.Covid19Vaccine.name())))
            .name("COVID-19 vaccines")
            .build(),
        new DetailedServiceSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void detailedServiceLocationIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new DetailedServiceLocationSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        DetailedService.DetailedServiceLocation.builder().build(),
        new DetailedServiceLocationSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        DetailedService.DetailedServiceLocation.builder()
            .appointmentPhoneNumbers(emptyList())
            .build(),
        new DetailedServiceLocationSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        DetailedService.DetailedServiceLocation.builder()
            .additionalHoursInfo("additional info")
            .build(),
        new DetailedServiceLocationSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void detailedServiceResponseIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new DetailedServiceResponseSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        DetailedServiceResponse.builder().build(),
        new DetailedServiceResponseSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        DetailedServiceResponse.builder()
            .data(
                DetailedService.builder()
                    .serviceId(uncapitalize(Facility.HealthService.Cardiology.name()))
                    .name("   ")
                    .build())
            .build(),
        new DetailedServiceResponseSerializer(),
        mock(SerializerProvider.class));
    assertIsNotEmptyUsingObjectSerializer(
        DetailedServiceResponse.builder()
            .data(
                DetailedService.builder()
                    .serviceId(uncapitalize(Facility.HealthService.Covid19Vaccine.name()))
                    .name("COVID-19 vaccines")
                    .build())
            .build(),
        new DetailedServiceResponseSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void detailedServicesMetadataIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new DetailedServicesMetadataSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        DetailedServicesResponse.DetailedServicesMetadata.builder().build(),
        new DetailedServicesMetadataSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        DetailedServicesResponse.DetailedServicesMetadata.builder()
            .pagination(Pagination.builder().build())
            .build(),
        new DetailedServicesMetadataSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        DetailedServicesResponse.DetailedServicesMetadata.builder()
            .pagination(Pagination.builder().totalEntries(10).build())
            .build(),
        new DetailedServicesMetadataSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void detailedServicesResponseIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new DetailedServicesResponseSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        DetailedServicesResponse.builder().build(),
        new DetailedServicesResponseSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        DetailedServicesResponse.builder().data(emptyList()).build(),
        new DetailedServicesResponseSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        DetailedServicesResponse.builder()
            .links(PageLinks.builder().self("http://foo.bar").build())
            .build(),
        new DetailedServicesResponseSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void distanceIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new DistanceSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        FacilitiesResponse.Distance.builder().build(),
        new DistanceSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        FacilitiesResponse.Distance.builder().id("   ").build(),
        new DistanceSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        FacilitiesResponse.Distance.builder().distance(BigDecimal.TEN).build(),
        new DistanceSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void facilitiesIdsResponseIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new FacilitiesIdsResponseSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        FacilitiesIdsResponse.builder().build(),
        new FacilitiesIdsResponseSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        FacilitiesIdsResponse.builder().data(emptyList()).build(),
        new FacilitiesIdsResponseSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        FacilitiesIdsResponse.builder().data(List.of("[\"vha_688\"]")).build(),
        new FacilitiesIdsResponseSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void facilitiesMetadataIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new FacilitiesMetadataSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        FacilitiesResponse.FacilitiesMetadata.builder().build(),
        new FacilitiesMetadataSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        FacilitiesResponse.FacilitiesMetadata.builder().distances(emptyList()).build(),
        new FacilitiesMetadataSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        FacilitiesResponse.FacilitiesMetadata.builder()
            .pagination(Pagination.builder().totalEntries(10).build())
            .build(),
        new FacilitiesMetadataSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void facilitiesResponseIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new FacilitiesResponseSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        FacilitiesResponse.builder().build(),
        new FacilitiesResponseSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        FacilitiesResponse.builder().data(emptyList()).build(),
        new FacilitiesResponseSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        FacilitiesResponse.builder()
            .links(PageLinks.builder().self("http://foo.bar").build())
            .build(),
        new FacilitiesResponseSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void facilityAttributesIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new FacilityAttributesSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Facility.FacilityAttributes.builder().build(),
        new FacilityAttributesSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Facility.FacilityAttributes.builder().instructions(emptyList()).build(),
        new FacilityAttributesSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        Facility.FacilityAttributes.builder()
            .facilityType(Facility.FacilityType.va_health_facility)
            .build(),
        new FacilityAttributesSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void facilityIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new FacilitySerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Facility.builder().build(), new FacilitySerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Facility.builder().id("   ").build(),
        new FacilitySerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        Facility.builder().id("vha_688").build(),
        new FacilitySerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void facilityReadResponseIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new FacilityReadResponseSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        FacilityReadResponse.builder().build(),
        new FacilityReadResponseSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        FacilityReadResponse.builder().facility(Facility.builder().id("   ").build()).build(),
        new FacilityReadResponseSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        FacilityReadResponse.builder()
            .facility(Facility.builder().type(Facility.Type.va_facilities).build())
            .build(),
        new FacilityReadResponseSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void hoursIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(null, new HoursSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Facility.Hours.builder().build(), new HoursSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Facility.Hours.builder().monday("   ").build(),
        new HoursSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        Facility.Hours.builder().wednesday("9AM-5PM").build(),
        new HoursSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void nearbyIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new NearbySerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        NearbyResponse.Nearby.builder().build(),
        new NearbySerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        NearbyResponse.Nearby.builder()
            .attributes(NearbyResponse.NearbyAttributes.builder().build())
            .build(),
        new NearbySerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        NearbyResponse.Nearby.builder().type(NearbyResponse.Type.NearbyFacility).build(),
        new NearbySerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void nearbyResponseAttributesIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new NearbyAttributesSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        NearbyResponse.NearbyAttributes.builder().build(),
        new NearbyAttributesSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        NearbyResponse.NearbyAttributes.builder().minTime(null).build(),
        new NearbyAttributesSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        NearbyResponse.NearbyAttributes.builder().minTime(Integer.MIN_VALUE).build(),
        new NearbyAttributesSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void nearbyResponseIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new NearbyResponseSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        NearbyResponse.builder().build(),
        new NearbyResponseSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        NearbyResponse.builder().data(emptyList()).build(),
        new NearbyResponseSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        NearbyResponse.builder()
            .meta(NearbyResponse.Meta.builder().bandVersion("APR2021").build())
            .build(),
        new NearbyResponseSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void nearbyResponseMetaIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(null, new MetaSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        NearbyResponse.Meta.builder().build(),
        new MetaSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        NearbyResponse.Meta.builder().bandVersion("   ").build(),
        new MetaSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        NearbyResponse.Meta.builder().bandVersion("APR2021").build(),
        new MetaSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void operatingStatusIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new OperatingStatusSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Facility.OperatingStatus.builder().build(),
        new OperatingStatusSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Facility.OperatingStatus.builder().additionalInfo("   ").build(),
        new OperatingStatusSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        Facility.OperatingStatus.builder().code(Facility.OperatingStatusCode.NORMAL).build(),
        new OperatingStatusSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void pageLinksIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new PageLinksSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        PageLinks.builder().build(), new PageLinksSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        PageLinks.builder().self("   ").build(),
        new PageLinksSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        PageLinks.builder().self("http://foo.bar").build(),
        new PageLinksSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void paginationIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new PaginationSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Pagination.builder().build(), new PaginationSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Pagination.builder().totalEntries(null).build(),
        new PaginationSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        Pagination.builder().totalEntries(10).build(),
        new PaginationSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void patientSatisfactionIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new PatientSatisfactionSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Facility.PatientSatisfaction.builder().build(),
        new PatientSatisfactionSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Facility.PatientSatisfaction.builder().primaryCareUrgent(null).build(),
        new PatientSatisfactionSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        Facility.PatientSatisfaction.builder().primaryCareUrgent(BigDecimal.TEN).build(),
        new PatientSatisfactionSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void patientWaitTimesIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new PatientWaitTimeSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Facility.PatientWaitTime.builder().build(),
        new PatientWaitTimeSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Facility.PatientWaitTime.builder().newPatientWaitTime(null).build(),
        new PatientWaitTimeSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        Facility.PatientWaitTime.builder().service(Facility.HealthService.Cardiology).build(),
        new PatientWaitTimeSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void phoneIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(null, new PhoneSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Facility.Phone.builder().build(), new PhoneSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Facility.Phone.builder().main("   ").build(),
        new PhoneSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        Facility.Phone.builder().fax("202-555-1212").build(),
        new PhoneSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void reloadResponseIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new ReloadResponseSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        ReloadResponse.builder().build(),
        new ReloadResponseSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        ReloadResponse.builder().problems(emptyList()).build(),
        new ReloadResponseSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        ReloadResponse.builder()
            .timing(ReloadResponse.Timing.builder().complete(Instant.now()).build())
            .build(),
        new ReloadResponseSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void reloadResponseProblemIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new ReloadResponseProblemSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        ReloadResponse.Problem.builder().build(),
        new ReloadResponseProblemSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        ReloadResponse.Problem.builder().description("   ").build(),
        new ReloadResponseProblemSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        ReloadResponse.Problem.builder().facilityId("vha_402").build(),
        new ReloadResponseProblemSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void reloadResponseTimingIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new ReloadResponseTimingSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        ReloadResponse.Timing.builder().build(),
        new ReloadResponseTimingSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        ReloadResponse.Timing.builder().totalDuration(null).build(),
        new ReloadResponseTimingSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        ReloadResponse.Timing.builder().complete(Instant.now()).build(),
        new ReloadResponseTimingSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void satisfactionIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new SatisfactionSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Facility.Satisfaction.builder().build(),
        new SatisfactionSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Facility.Satisfaction.builder()
            .health(Facility.PatientSatisfaction.builder().build())
            .build(),
        new SatisfactionSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        Facility.Satisfaction.builder()
            .health(
                Facility.PatientSatisfaction.builder().primaryCareUrgent(BigDecimal.TEN).build())
            .build(),
        new SatisfactionSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void servicesIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new ServicesSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Facility.Services.builder().build(),
        new ServicesSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Facility.Services.builder().benefits(emptyList()).build(),
        new ServicesSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        Facility.Services.builder().health(List.of(Facility.HealthService.PrimaryCare)).build(),
        new ServicesSerializer(),
        mock(SerializerProvider.class));
  }

  @Test
  @SneakyThrows
  void waitTimesIsEmpty() {
    // Empty
    assertIsEmptyUsingObjectSerializer(
        null, new WaitTimesSerializer(), mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Facility.WaitTimes.builder().build(),
        new WaitTimesSerializer(),
        mock(SerializerProvider.class));
    assertIsEmptyUsingObjectSerializer(
        Facility.WaitTimes.builder().health(emptyList()).build(),
        new WaitTimesSerializer(),
        mock(SerializerProvider.class));
    // Not empty
    assertIsNotEmptyUsingObjectSerializer(
        Facility.WaitTimes.builder().effectiveDate(LocalDate.now()).build(),
        new WaitTimesSerializer(),
        mock(SerializerProvider.class));
  }
}
