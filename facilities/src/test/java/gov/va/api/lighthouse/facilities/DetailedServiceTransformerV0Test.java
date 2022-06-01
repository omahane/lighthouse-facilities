package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.va.api.lighthouse.facilities.api.v0.DetailedService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class DetailedServiceTransformerV0Test {
  @Test
  void datamartDetailedServiceWithEmptyAttributesRoundTrip() {
    DatamartDetailedService datamartDetailedService =
        DatamartDetailedServicesTestUtils.datamartDetailedServiceWithEmptyAttributes();
    assertThat(
            DetailedServiceTransformerV0.toVersionAgnosticDetailedService(
                DetailedServiceTransformerV0.toDetailedService(datamartDetailedService)))
        .usingRecursiveComparison()
        .isEqualTo(datamartDetailedService);
  }

  @Test
  void datamartDetailedServiceWithNullAttributesRoundTrip() {
    DatamartDetailedService datamartDetailedService =
        DatamartDetailedServicesTestUtils.datamartDetailedServiceWithNullAttributes();
    assertThat(
            DetailedServiceTransformerV0.toVersionAgnosticDetailedService(
                DetailedServiceTransformerV0.toDetailedService(datamartDetailedService)))
        .usingRecursiveComparison()
        .isEqualTo(datamartDetailedService);
  }

  @Test
  public void roundTripTransformation() {
    List<DatamartDetailedService> datamartDetailedServices =
        DatamartDetailedServicesTestUtils.datamartDetailedServices(true);
    assertThat(
            DetailedServiceTransformerV0.toVersionAgnosticDetailedServices(
                DetailedServiceTransformerV0.toDetailedServices(datamartDetailedServices)))
        .containsAll(datamartDetailedServices);
  }

  @Test
  void toDetailedServiceEmailContacts() {
    assertThat(DetailedServiceTransformerV0.toDetailedServiceEmailContacts(null)).isNull();
    assertThat(DetailedServiceTransformerV0.toDetailedServiceEmailContacts(new ArrayList<>()))
        .isEmpty();
  }

  @Test
  void toDetailedServiceNullArgs() {
    assertThrows(
        NullPointerException.class, () -> DetailedServiceTransformerV0.toDetailedService(null));
  }

  @Test
  void toDetailedServicesEmptyArg() {
    assertThat(DetailedServiceTransformerV0.toDetailedServices(new ArrayList<>())).isEmpty();
  }

  @Test
  void toDetailedServicesNullArgs() {
    assertThat(DetailedServiceTransformerV0.toDetailedServices(null)).isEqualTo(null);
  }

  @Test
  void toVersionAgnosticDetailedServiceEmailContacts() {
    assertThat(DetailedServiceTransformerV0.toVersionAgnosticDetailedServiceEmailContacts(null))
        .isNull();
    assertThat(
            DetailedServiceTransformerV0.toVersionAgnosticDetailedServiceEmailContacts(
                new ArrayList<>()))
        .isEmpty();
  }

  @Test
  void toVersionAgnosticDetailedServiceNullArgs() {
    assertThrows(
        NullPointerException.class,
        () -> DetailedServiceTransformerV0.toVersionAgnosticDetailedService(null));
  }

  @Test
  void toVersionAgnosticDetailedServicesEmptyArg() {
    assertThat(DetailedServiceTransformerV0.toVersionAgnosticDetailedServices(new ArrayList<>()))
        .isEmpty();
  }

  @Test
  void toVersionAgnosticDetailedServicesNullArgs() {
    assertThat(DetailedServiceTransformerV0.toVersionAgnosticDetailedServices(null))
        .isEqualTo(null);
  }

  @Test
  void transformDatamartDetailedServiceLocation() {
    assertThat(
            DetailedServiceTransformerV0.transformDetailedServiceLocation(
                (DatamartDetailedService.DetailedServiceLocation) null))
        .isNull();
  }

  @Test
  void transformDetailedServiceAddress() {
    assertThat(
            DetailedServiceTransformerV0.transformDetailedServiceAddress(
                (DatamartDetailedService.DetailedServiceAddress) null))
        .isNull();
    assertThat(
            DetailedServiceTransformerV0.transformDetailedServiceAddress(
                (DetailedService.DetailedServiceAddress) null))
        .isNull();
  }

  @Test
  void transformDetailedServiceEmailContact() {
    assertThat(
            DetailedServiceTransformerV0.transformDetailedServiceEmailContact(
                (DatamartDetailedService.DetailedServiceEmailContact) null))
        .isNull();
    assertThat(
            DetailedServiceTransformerV0.transformDetailedServiceEmailContact(
                (DetailedService.DetailedServiceEmailContact) null))
        .isNull();
  }

  @Test
  void transformDetailedServiceHours() {
    assertThat(
            DetailedServiceTransformerV0.transformDetailedServiceHours(
                (DatamartDetailedService.DetailedServiceHours) null))
        .isNull();
    assertThat(
            DetailedServiceTransformerV0.transformDetailedServiceHours(
                (DetailedService.DetailedServiceHours) null))
        .isNull();
  }

  @Test
  void transformDetailedServiceLocation() {
    assertThat(
            DetailedServiceTransformerV0.transformDetailedServiceLocation(
                (DetailedService.DetailedServiceLocation) null))
        .isNull();
  }

  @Test
  void transfromDetailedServiceAppointmentPhoneNumber() {
    assertThat(
            DetailedServiceTransformerV0.transfromDetailedServiceAppointmentPhoneNumber(
                (DatamartDetailedService.AppointmentPhoneNumber) null))
        .isNull();
    assertThat(
            DetailedServiceTransformerV0.transfromDetailedServiceAppointmentPhoneNumber(
                (DetailedService.AppointmentPhoneNumber) null))
        .isNull();
  }
}
