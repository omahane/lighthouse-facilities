package gov.va.api.lighthouse.facilities;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.va.api.lighthouse.facilities.api.TypeOfService;
import gov.va.api.lighthouse.facilities.api.TypedService;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.PatientWaitTime;
import gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

class DetailedServiceTransformerV1Test {
  @Test
  void datamartDetailedServiceWithEmptyAttributesRoundTrip() {
    assertThat(
            DatamartDetailedServicesTestUtils
                .datamartDetailedServiceWithInvalidServiceIdEmptyAttributes())
        .usingRecursiveComparison()
        .isEqualTo(
            DatamartDetailedService.builder()
                .serviceInfo(
                    DatamartDetailedService.ServiceInfo.builder()
                        .serviceId(TypedService.INVALID_SVC_ID)
                        .serviceType(TypeOfService.Health)
                        .build())
                .phoneNumbers(emptyList())
                .serviceLocations(emptyList())
                .build());

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
    assertThat(
            DatamartDetailedServicesTestUtils
                .datamartDetailedServiceWithInvalidServiceIdNullAttributes())
        .usingRecursiveComparison()
        .isEqualTo(
            DatamartDetailedService.builder()
                .serviceInfo(
                    DatamartDetailedService.ServiceInfo.builder()
                        .serviceId(TypedService.INVALID_SVC_ID)
                        .serviceType(TypeOfService.Health)
                        .build())
                .build());

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
  void toDetailedServiceName() {
    assertThat(DetailedServiceTransformerV1.toDetailedServiceName("ApplyingForBenefits"))
        .isEqualTo("ApplyingForBenefits");
    assertThat(DetailedServiceTransformerV1.toDetailedServiceName("OnlineScheduling"))
        .isEqualTo("OnlineScheduling");
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
  void toVersionAgnosticDetailedServiceName() {
    assertThat(
            DetailedServiceTransformerV1.toVersionAgnosticDetailedServiceName(
                "ApplyingForBenefits"))
        .isEqualTo("ApplyingForBenefits");
    assertThat(
            DetailedServiceTransformerV1.toVersionAgnosticDetailedServiceName("OnlineScheduling"))
        .isEqualTo("OnlineScheduling");
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
  @SneakyThrows
  void toVersionAgnosticPatientWaitTime() {
    DetailedService detailedService =
        DetailedService.builder()
            .serviceInfo(
                DetailedService.ServiceInfo.builder()
                    .serviceId(HealthService.Cardiology.serviceId())
                    .build())
            .waitTime(
                PatientWaitTime.builder()
                    .newPatientWaitTime(BigDecimal.valueOf(34.4))
                    .establishedPatientWaitTime(BigDecimal.valueOf(3.25))
                    .effectiveDate(LocalDate.parse("2022-03-03"))
                    .build())
            .build();
    Method toVersionAgnosticPatientWaitTime =
        DetailedServiceTransformerV1.class.getDeclaredMethod(
            "toVersionAgnosticPatientWaitTime", DetailedService.PatientWaitTime.class);
    toVersionAgnosticPatientWaitTime.setAccessible(true);
    DatamartDetailedService.PatientWaitTime patientWaitTime =
        (DatamartDetailedService.PatientWaitTime)
            toVersionAgnosticPatientWaitTime.invoke(null, detailedService.waitTime());
    assertThat(patientWaitTime.newPatientWaitTime()).isEqualTo(BigDecimal.valueOf(34.4));
    assertThat(patientWaitTime.establishedPatientWaitTime()).isEqualTo(BigDecimal.valueOf(3.25));
    assertThat(patientWaitTime.effectiveDate()).isEqualTo(LocalDate.parse("2022-03-03"));
    System.out.println("test");
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
