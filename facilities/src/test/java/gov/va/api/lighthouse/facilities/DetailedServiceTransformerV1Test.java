package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.va.api.lighthouse.facilities.api.v1.DetailedService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class DetailedServiceTransformerV1Test {
  @Test
  void datamartDetailedServiceWithEmptyAttributesRoundTrip() {
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
  void transformDatamartDetailedServiceLocation() {
    assertThat(
            DetailedServiceTransformerV1.transformDetailedServiceLocation(
                (DatamartDetailedService.DetailedServiceLocation) null))
        .isNull();
  }

  @Test
  void transformDetailedServiceAddress() {
    assertThat(
            DetailedServiceTransformerV1.transformDetailedServiceAddress(
                (DatamartDetailedService.DetailedServiceAddress) null))
        .isNull();
    assertThat(
            DetailedServiceTransformerV1.transformDetailedServiceAddress(
                (DetailedService.DetailedServiceAddress) null))
        .isNull();
  }

  @Test
  void transformDetailedServiceEmailContact() {
    assertThat(
            DetailedServiceTransformerV1.transformDetailedServiceEmailContact(
                (DatamartDetailedService.DetailedServiceEmailContact) null))
        .isNull();
    assertThat(
            DetailedServiceTransformerV1.transformDetailedServiceEmailContact(
                (DetailedService.DetailedServiceEmailContact) null))
        .isNull();
  }

  @Test
  void transformDetailedServiceHours() {
    assertThat(
            DetailedServiceTransformerV1.transformDetailedServiceHours(
                (DatamartDetailedService.DetailedServiceHours) null))
        .isNull();
    assertThat(
            DetailedServiceTransformerV1.transformDetailedServiceHours(
                (DetailedService.DetailedServiceHours) null))
        .isNull();
  }

  @Test
  void transformDetailedServiceLocation() {
    assertThat(
            DetailedServiceTransformerV1.transformDetailedServiceLocation(
                (DetailedService.DetailedServiceLocation) null))
        .isNull();
  }

  @Test
  void transfromDetailedServiceAppointmentPhoneNumber() {
    assertThat(
            DetailedServiceTransformerV1.transfromDetailedServiceAppointmentPhoneNumber(
                (DatamartDetailedService.AppointmentPhoneNumber) null))
        .isNull();
    assertThat(
            DetailedServiceTransformerV1.transfromDetailedServiceAppointmentPhoneNumber(
                (DetailedService.AppointmentPhoneNumber) null))
        .isNull();
  }
}
