package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.va.api.lighthouse.facilities.api.v1.DetailedService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class DetailedServiceTransformerV1Test {
  @Test
  void datamartDetailedServiceWithEmptyAttributesRoundTrip() {
    assertThatThrownBy(
            () ->
                DatamartDetailedServicesTestUtils
                    .datamartDetailedServiceWithInvalidServiceIdEmptyAttributes())
        .isInstanceOf(Exception.class)
        .hasMessage("Unrecognized service id: emptyService");

    DatamartDetailedService datamartDetailedService =
        DatamartDetailedServicesTestUtils.datamartDetailedServiceWithEmptyAttributes();
    assertThat(
            DetailedServiceTransformerV1.toVersionAgnosticDetailedService(
                DetailedServiceTransformerV1.toDetailedService(datamartDetailedService)))
        .usingRecursiveComparison()
        .isEqualTo(datamartDetailedService);
  }

  @Test
  void datamartDetailedServiceWithNullAttributesRoundTrip() {
    assertThatThrownBy(
            () ->
                DatamartDetailedServicesTestUtils
                    .datamartDetailedServiceWithInvalidServiceIdNullAttributes())
        .isInstanceOf(Exception.class)
        .hasMessage("Unrecognized service id: emptyService");

    DatamartDetailedService datamartDetailedService =
        DatamartDetailedServicesTestUtils.datamartDetailedServiceWithNullAttributes();
    assertThat(
            DetailedServiceTransformerV1.toVersionAgnosticDetailedService(
                DetailedServiceTransformerV1.toDetailedService(datamartDetailedService)))
        .usingRecursiveComparison()
        .isEqualTo(datamartDetailedService);
  }

  @Test
  public void roundTripTransformation() {
    List<DatamartDetailedService> datamartDetailedServices =
        DatamartDetailedServicesTestUtils.datamartDetailedServices(true);
    assertThat(
            DetailedServiceTransformerV1.toVersionAgnosticDetailedServices(
                DetailedServiceTransformerV1.toDetailedServices(datamartDetailedServices)))
        .containsAll(datamartDetailedServices);
  }

  @Test
  void toDatamartDetailedServiceLocation() {
    assertThat(
            DetailedServiceTransformerV1.toDetailedServiceLocation(
                (DatamartDetailedService.DetailedServiceLocation) null))
        .isNull();
  }

  @Test
  void toDetailedServiceEmailContacts() {
    assertThat(DetailedServiceTransformerV1.toDetailedServiceEmailContacts(null)).isNull();
    assertThat(DetailedServiceTransformerV1.toDetailedServiceEmailContacts(new ArrayList<>()))
        .isEmpty();
  }

  @Test
  void toDetailedServiceNullArgs() {
    assertThrows(
        NullPointerException.class, () -> DetailedServiceTransformerV1.toDetailedService(null));
  }

  @Test
  void toDetailedServicesEmptyArg() {
    assertThat(DetailedServiceTransformerV1.toDetailedServices(new ArrayList<>())).isEmpty();
  }

  @Test
  void toDetailedServicesNullArgs() {
    assertThat(DetailedServiceTransformerV1.toDetailedServices(null)).isEqualTo(null);
  }

  @Test
  void toVersionAgnosticDetailedServiceEmailContacts() {
    assertThat(DetailedServiceTransformerV1.toVersionAgnosticDetailedServiceEmailContacts(null))
        .isNull();
    assertThat(
            DetailedServiceTransformerV1.toVersionAgnosticDetailedServiceEmailContacts(
                new ArrayList<>()))
        .isEmpty();
  }

  @Test
  void toVersionAgnosticDetailedServiceLocation() {
    assertThat(
            DetailedServiceTransformerV1.toVersionAgnosticDetailedServiceLocation(
                (DetailedService.DetailedServiceLocation) null))
        .isNull();
  }

  @Test
  void toVersionAgnosticDetailedServiceNullArgs() {
    assertThrows(
        NullPointerException.class,
        () -> DetailedServiceTransformerV1.toVersionAgnosticDetailedService(null));
  }

  @Test
  void toVersionAgnosticDetailedServicesEmptyArg() {
    assertThat(DetailedServiceTransformerV1.toVersionAgnosticDetailedServices(new ArrayList<>()))
        .isEmpty();
  }

  @Test
  void toVersionAgnosticDetailedServicesNullArgs() {
    assertThat(DetailedServiceTransformerV1.toVersionAgnosticDetailedServices(null))
        .isEqualTo(null);
  }

  @Test
  void transformDetailedServiceAddress() {
    assertThat(
            DetailedServiceTransformerV1.toDetailedServiceAddress(
                (DatamartDetailedService.DetailedServiceAddress) null))
        .isNull();
    assertThat(
            DetailedServiceTransformerV1.toVersionAgnosticDetailedServiceAddress(
                (DetailedService.DetailedServiceAddress) null))
        .isNull();
  }

  @Test
  void transformDetailedServiceEmailContact() {
    assertThat(
            DetailedServiceTransformerV1.toDetailedServiceEmailContact(
                (DatamartDetailedService.DetailedServiceEmailContact) null))
        .isNull();
    assertThat(
            DetailedServiceTransformerV1.toVersionAgnosticDetailedServiceEmailContact(
                (DetailedService.DetailedServiceEmailContact) null))
        .isNull();
  }

  @Test
  void transformDetailedServiceHours() {
    assertThat(
            DetailedServiceTransformerV1.toDetailedServiceHours(
                (DatamartDetailedService.DetailedServiceHours) null))
        .isNull();
    assertThat(
            DetailedServiceTransformerV1.toVersionAgnosticDetailedServiceHours(
                (DetailedService.DetailedServiceHours) null))
        .isNull();
  }

  @Test
  void transfromDetailedServiceAppointmentPhoneNumber() {
    assertThat(
            DetailedServiceTransformerV1.toDetailedServiceAppointmentPhoneNumber(
                (DatamartDetailedService.AppointmentPhoneNumber) null))
        .isNull();
    assertThat(
            DetailedServiceTransformerV1.toVersionAgnosticDetailedServiceAppointmentPhoneNumber(
                (DetailedService.AppointmentPhoneNumber) null))
        .isNull();
  }
}
