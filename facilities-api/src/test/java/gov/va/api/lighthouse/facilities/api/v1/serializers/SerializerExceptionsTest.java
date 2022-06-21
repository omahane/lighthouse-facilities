package gov.va.api.lighthouse.facilities.api.v1.serializers;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class SerializerExceptionsTest {
  @Test
  @SneakyThrows
  void addressExceptions() {
    assertNpeThrown(
        null,
        new AddressSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.Facility$Address.zip()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void addressesExceptions() {
    assertNpeThrown(
        null,
        new AddressesSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.Facility$Addresses.mailing()\" because \"value\" is null");
  }

  @SneakyThrows
  private <T, U extends StdSerializer<T>> void assertNpeThrown(
      T value, U serializer, JsonGenerator jgen, SerializerProvider provider, String expectedMsg) {
    assertThatThrownBy(() -> serializer.serialize(value, jgen, provider))
        .isInstanceOf(NullPointerException.class)
        .hasMessage(expectedMsg);
  }

  @Test
  @SneakyThrows
  void cmsOverlayExceptions() {
    assertNpeThrown(
        null,
        new CmsOverlaySerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.CmsOverlay.operatingStatus()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void cmsOverlayResponseExceptions() {
    assertNpeThrown(
        null,
        new CmsOverlayResponseSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.CmsOverlayResponse.overlay()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void detailedServiceAddressExceptions() {
    assertNpeThrown(
        null,
        new DetailedServiceAddressSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.DetailedService$DetailedServiceAddress.buildingNameNumber()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void detailedServiceAppointmentPhoneNumberExceptions() {
    assertNpeThrown(
        null,
        new DetailedServiceAppointmentPhoneNumberSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.DetailedService$AppointmentPhoneNumber.extension()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void detailedServiceEmailContactExceptions() {
    assertNpeThrown(
        null,
        new DetailedServiceEmailContactSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.DetailedService$DetailedServiceEmailContact.emailAddress()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void detailedServiceExceptions() {
    assertNpeThrown(
        null,
        new DetailedServiceSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.DetailedService.serviceInfo()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void detailedServiceHoursExceptions() {
    assertNpeThrown(
        null,
        new DetailedServiceHoursSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.DetailedService$DetailedServiceHours.monday()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void detailedServiceLocationExceptions() {
    assertNpeThrown(
        null,
        new DetailedServiceLocationSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.DetailedService$DetailedServiceLocation.serviceLocationAddress()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void detailedServiceResponseExceptions() {
    assertNpeThrown(
        null,
        new DetailedServiceResponseSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.DetailedServiceResponse.data()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void detailedServicesMetadataExceptions() {
    assertNpeThrown(
        null,
        new DetailedServicesMetadataSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.DetailedServicesResponse$DetailedServicesMetadata.pagination()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void detailedServicesResponseExceptions() {
    assertNpeThrown(
        null,
        new DetailedServicesResponseSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.DetailedServicesResponse.data()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void distanceExceptions() {
    assertNpeThrown(
        null,
        new DistanceSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.FacilitiesResponse$Distance.id()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void facilitiesIdsResponseExceptions() {
    assertNpeThrown(
        null,
        new FacilitiesIdsResponseSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.FacilitiesIdsResponse.data()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void facilitiesMetadataExceptions() {
    assertNpeThrown(
        null,
        new FacilitiesMetadataSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.FacilitiesResponse$FacilitiesMetadata.pagination()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void facilitiesResponseExceptions() {
    assertNpeThrown(
        null,
        new FacilitiesResponseSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.FacilitiesResponse.data()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void facilityAttributesExceptions() {
    assertNpeThrown(
        null,
        new FacilityAttributesSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.Facility$FacilityAttributes.name()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void facilityExceptions() {
    assertNpeThrown(
        null,
        new FacilitySerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.Facility.id()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void facilityReadResponseExceptions() {
    assertNpeThrown(
        null,
        new FacilityReadResponseSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.FacilityReadResponse.facility()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void hoursExceptions() {
    assertNpeThrown(
        null,
        new HoursSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.Facility$Hours.monday()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void nearbyExceptions() {
    assertNpeThrown(
        null,
        new NearbySerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.NearbyResponse$Nearby.id()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void nearbyResponseAttributesExceptions() {
    assertNpeThrown(
        null,
        new NearbyAttributesSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.NearbyResponse$NearbyAttributes.minTime()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void nearbyResponseExceptions() {
    assertNpeThrown(
        null,
        new NearbyResponseSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.NearbyResponse.data()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void nearbyResponseMetaExceptions() {
    assertNpeThrown(
        null,
        new MetaSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.NearbyResponse$Meta.bandVersion()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void operatingStatusExceptions() {
    assertNpeThrown(
        null,
        new OperatingStatusSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.Facility$OperatingStatus.code()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void pageLinksExceptions() {
    JsonStreamContext mockStreamContext = mock(JsonStreamContext.class);
    var mockGenerator = mock(JsonGenerator.class);
    when(mockGenerator.getOutputContext()).thenReturn(mockStreamContext);
    assertNpeThrown(
        null,
        new PageLinksSerializer(),
        mockGenerator,
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.PageLinks.self()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void paginationExceptions() {
    assertNpeThrown(
        null,
        new PaginationSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.Pagination.currentPage()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void patientSatisfactionExceptions() {
    assertNpeThrown(
        null,
        new PatientSatisfactionSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.Facility$PatientSatisfaction.primaryCareUrgent()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void patientWaitTimesExceptions() {
    assertNpeThrown(
        null,
        new PatientWaitTimeSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.DetailedService$PatientWaitTime.newPatientWaitTime()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void phoneExceptions() {
    assertNpeThrown(
        null,
        new PhoneSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.Facility$Phone.fax()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void reloadResponseExceptions() {
    assertNpeThrown(
        null,
        new ReloadResponseSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.ReloadResponse.facilitiesUpdated()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void reloadResponseProblemExceptions() {
    assertNpeThrown(
        null,
        new ReloadResponseProblemSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.ReloadResponse$Problem.facilityId()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void reloadResponseTimingExceptions() {
    assertNpeThrown(
        null,
        new ReloadResponseTimingSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.ReloadResponse$Timing.start()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void satisfactionExceptions() {
    assertNpeThrown(
        null,
        new SatisfactionSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.Facility$Satisfaction.health()\" because \"value\" is null");
  }

  @Test
  @SneakyThrows
  void servicesExceptions() {
    assertNpeThrown(
        null,
        new ServicesSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.v1.Facility$Services.other()\" because \"value\" is null");
  }
}
