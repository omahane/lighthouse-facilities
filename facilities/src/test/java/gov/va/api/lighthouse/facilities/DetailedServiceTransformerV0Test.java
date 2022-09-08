package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.CMS_OVERLAY_SERVICE_NAME_COVID_19;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.lighthouse.facilities.ServiceNameAggregatorV0.ServiceNameAggregate;
import gov.va.api.lighthouse.facilities.api.TypeOfService;
import gov.va.api.lighthouse.facilities.api.TypedService;
import gov.va.api.lighthouse.facilities.api.v0.DetailedService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DetailedServiceTransformerV0Test {
  private ServiceNameAggregatorV0 mockServiceNameAggregator;

  private ServiceNameAggregate mockServiceNameAggregate;

  @Test
  void datamartDetailedServiceWithEmptyAttributesRoundTrip() {
    when(mockServiceNameAggregate.serviceName(
            DatamartFacility.HealthService.Covid19Vaccine.serviceId()))
        .thenReturn(CMS_OVERLAY_SERVICE_NAME_COVID_19);
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
            DetailedServiceTransformerV0.toVersionAgnosticDetailedService(
                DetailedServiceTransformerV0.toDetailedService(
                    datamartDetailedService, mockServiceNameAggregate)))
        .usingRecursiveComparison()
        .isEqualTo(datamartDetailedService);
  }

  @Test
  void datamartDetailedServiceWithNullAttributesRoundTrip() {
    when(mockServiceNameAggregate.serviceName(
            DatamartFacility.HealthService.Covid19Vaccine.serviceId()))
        .thenReturn(CMS_OVERLAY_SERVICE_NAME_COVID_19);
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
            DetailedServiceTransformerV0.toVersionAgnosticDetailedService(
                DetailedServiceTransformerV0.toDetailedService(
                    datamartDetailedService, mockServiceNameAggregate)))
        .usingRecursiveComparison()
        .isEqualTo(datamartDetailedService);
  }

  @Test
  public void roundTripTransformation() {
    when(mockServiceNameAggregate.serviceName(
            DatamartFacility.HealthService.Cardiology.serviceId()))
        .thenReturn(DatamartFacility.HealthService.Cardiology.name());
    when(mockServiceNameAggregate.serviceName(
            DatamartFacility.HealthService.Covid19Vaccine.serviceId()))
        .thenReturn(CMS_OVERLAY_SERVICE_NAME_COVID_19);
    when(mockServiceNameAggregate.serviceName(DatamartFacility.HealthService.Urology.serviceId()))
        .thenReturn(DatamartFacility.HealthService.Urology.name());

    List<DatamartDetailedService> datamartDetailedServices =
        DatamartDetailedServicesTestUtils.datamartDetailedServices(true);
    assertThat(
            DetailedServiceTransformerV0.toVersionAgnosticDetailedServices(
                DetailedServiceTransformerV0.toDetailedServices(
                    datamartDetailedServices, mockServiceNameAggregator)))
        .containsAll(datamartDetailedServices);
  }

  @BeforeEach
  void setup() {
    mockServiceNameAggregate = mock(ServiceNameAggregate.class);
    mockServiceNameAggregator = mock(ServiceNameAggregatorV0.class);
    when(mockServiceNameAggregator.serviceNameAggregate()).thenReturn(mockServiceNameAggregate);
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
        NullPointerException.class,
        () -> DetailedServiceTransformerV0.toDetailedService(null, null));
  }

  @Test
  void toDetailedServicesEmptyArg() {
    assertThat(
            DetailedServiceTransformerV0.toDetailedServices(
                new ArrayList<>(), mockServiceNameAggregator))
        .isEmpty();
  }

  @Test
  void toDetailedServicesNullArgs() {
    assertThat(DetailedServiceTransformerV0.toDetailedServices(null, mockServiceNameAggregator))
        .isEqualTo(null);
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
  void toVersionAgnosticDetailedServiceName() {
    assertThat(
            DetailedServiceTransformerV0.toVersionAgnosticDetailedServiceName(
                "ApplyingForBenefits"))
        .isEqualTo("ApplyingForBenefits");
    assertThat(
            DetailedServiceTransformerV0.toVersionAgnosticDetailedServiceName("OnlineScheduling"))
        .isEqualTo("OnlineScheduling");
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
    assertThat(DetailedServiceTransformerV0.toDetailedServiceAddress(null)).isNull();
    assertThat(DetailedServiceTransformerV0.toVersionAgnosticDetailedServiceAddress(null)).isNull();
  }

  @Test
  void transformDetailedServiceEmailContact() {
    assertThat(DetailedServiceTransformerV0.toDetailedServiceEmailContact(null)).isNull();
    assertThat(DetailedServiceTransformerV0.toVersionAgnosticDetailedServiceEmailContact(null))
        .isNull();
  }

  @Test
  void transformDetailedServiceHours() {
    assertThat(DetailedServiceTransformerV0.toDetailedServiceHours(null)).isNull();
    assertThat(DetailedServiceTransformerV0.toVersionAgnosticDetailedServiceHours(null)).isNull();
  }

  @Test
  void transfromDetailedServiceAppointmentPhoneNumber() {
    assertThat(DetailedServiceTransformerV0.toDetailedServiceAppointmentPhoneNumber(null)).isNull();
    assertThat(
            DetailedServiceTransformerV0.toVersionAgnosticDetailedServiceAppointmentPhoneNumber(
                null))
        .isNull();
  }
}
