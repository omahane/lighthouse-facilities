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
  void toDatamartDetailedServiceLocation() {
    assertThat(
            DetailedServiceTransformerV0.toDetailedServiceLocation(
                (DatamartDetailedService.DetailedServiceLocation) null))
        .isNull();
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
  void toVersionAgnosticDetailedServiceLocation() {
    assertThat(
            DetailedServiceTransformerV0.toVersionAgnosticDetailedServiceLocation(
                (DetailedService.DetailedServiceLocation) null))
        .isNull();
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
  void transformDetailedServiceAddress() {
    assertThat(
            DetailedServiceTransformerV0.toDetailedServiceAddress(
                (DatamartDetailedService.DetailedServiceAddress) null))
        .isNull();
    assertThat(
            DetailedServiceTransformerV0.toVersionAgnosticDetailedServiceAddress(
                (DetailedService.DetailedServiceAddress) null))
        .isNull();
  }

  @Test
  void transformDetailedServiceEmailContact() {
    assertThat(
            DetailedServiceTransformerV0.toDetailedServiceEmailContact(
                (DatamartDetailedService.DetailedServiceEmailContact) null))
        .isNull();
    assertThat(
            DetailedServiceTransformerV0.toVersionAgnosticDetailedServiceEmailContact(
                (DetailedService.DetailedServiceEmailContact) null))
        .isNull();
  }

  @Test
  void transformDetailedServiceHours() {
    assertThat(
            DetailedServiceTransformerV0.toDetailedServiceHours(
                (DatamartDetailedService.DetailedServiceHours) null))
        .isNull();
    assertThat(
            DetailedServiceTransformerV0.toVersionAgnosticDetailedServiceHours(
                (DetailedService.DetailedServiceHours) null))
        .isNull();
  }

  @Test
  void transfromDetailedServiceAppointmentPhoneNumber() {
    assertThat(
            DetailedServiceTransformerV0.toDetailedServiceAppointmentPhoneNumber(
                (DatamartDetailedService.AppointmentPhoneNumber) null))
        .isNull();
    assertThat(
            DetailedServiceTransformerV0.toVersionAgnosticDetailedServiceAppointmentPhoneNumber(
                (DetailedService.AppointmentPhoneNumber) null))
        .isNull();
  }
}
