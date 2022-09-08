package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService.Pensions;
import static gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Audiology;
import static gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Cardiology;
import static gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Covid19Vaccine;
import static gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.EmergencyCare;
import static gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.PrimaryCare;
import static gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Smoking;
import static gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.WomensHealth;
import static gov.va.api.lighthouse.facilities.api.v1.Facility.OtherService.OnlineScheduling;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.lighthouse.facilities.ServiceNameAggregatorV1.ServiceNameAggregate;
import gov.va.api.lighthouse.facilities.collector.AccessToCareMapper;
import gov.va.api.lighthouse.facilities.collector.CmsOverlayMapper;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ServiceNameAggregatorV1Test {

  private static AccessToCareMapper mockAccessToCareMapper;
  private static CmsOverlayMapper mockCmsOverlayMapper;

  @BeforeAll
  static void setup() {
    mockAccessToCareMapper = mock(AccessToCareMapper.class);
    when(mockAccessToCareMapper.serviceIds())
        .thenReturn(
            Set.of(Audiology.serviceId(), Cardiology.serviceId(), WomensHealth.serviceId()));
    when(mockAccessToCareMapper.serviceNameForServiceId(Audiology.serviceId()))
        .thenReturn(Optional.of("AUDIOLOGY"));
    when(mockAccessToCareMapper.serviceNameForServiceId(Cardiology.serviceId()))
        .thenReturn(Optional.of("CARDIOLOGY"));
    when(mockAccessToCareMapper.serviceNameForServiceId(WomensHealth.serviceId()))
        .thenReturn(Optional.of("WOMEN'S HEALTH"));
    when(mockAccessToCareMapper.reload()).thenReturn(true);

    mockCmsOverlayMapper = mock(CmsOverlayMapper.class);
    when(mockCmsOverlayMapper.serviceIds())
        .thenReturn(
            Set.of(
                Audiology.serviceId(),
                Cardiology.serviceId(),
                Covid19Vaccine.serviceId(),
                PrimaryCare.serviceId(),
                EmergencyCare.serviceId()));
    when(mockCmsOverlayMapper.serviceNameForServiceId(Audiology.serviceId()))
        .thenReturn(Optional.of(Audiology.name()));
    when(mockCmsOverlayMapper.serviceNameForServiceId(Cardiology.serviceId()))
        .thenReturn(Optional.of(Cardiology.name()));
    when(mockCmsOverlayMapper.serviceNameForServiceId(Covid19Vaccine.serviceId()))
        .thenReturn(Optional.of("COVID-19 vaccines"));
    when(mockCmsOverlayMapper.serviceNameForServiceId(PrimaryCare.serviceId()))
        .thenReturn(Optional.of("New CMS Primary Care"));
    when(mockCmsOverlayMapper.serviceNameForServiceId(EmergencyCare.serviceId()))
        .thenReturn(Optional.of("CMS Complete Emergency Care"));
    when(mockCmsOverlayMapper.reload()).thenReturn(true);
  }

  @Test
  void serviceName() {
    ServiceNameAggregatorV1 serviceNameAggregator =
        new ServiceNameAggregatorV1(mockAccessToCareMapper, mockCmsOverlayMapper);
    ServiceNameAggregate serviceNameAggregate = serviceNameAggregator.serviceNameAggregate();
    // Service present in ATC, but absent from CMS. ATC service name expected to be displayed
    assertThat(serviceNameAggregate.serviceName(WomensHealth.serviceId()))
        .isEqualTo(WomensHealth.name());
    // Service present in CMS, but absent from ATC. CMS service name expected to be displayed
    assertThat(serviceNameAggregate.serviceName(Covid19Vaccine.serviceId()))
        .isEqualTo("COVID-19 vaccines");
    assertThat(serviceNameAggregate.serviceName(PrimaryCare.serviceId()))
        .isEqualTo("New CMS Primary Care");
    assertThat(serviceNameAggregate.serviceName(capitalize(PrimaryCare.serviceId())))
        .isEqualTo("New CMS Primary Care");
    assertThat(serviceNameAggregate.serviceName(EmergencyCare.serviceId()))
        .isEqualTo("CMS Complete Emergency Care");
    assertThat(serviceNameAggregate.serviceName(capitalize(EmergencyCare.serviceId())))
        .isEqualTo("CMS Complete Emergency Care");
    // Service present in both CMS and ATC. CMS service name expected to be displayed
    assertThat(serviceNameAggregate.serviceName(Audiology.serviceId())).isEqualTo(Audiology.name());
    assertThat(serviceNameAggregate.serviceName(capitalize(Audiology.serviceId())))
        .isEqualTo(Audiology.name());
    assertThat(serviceNameAggregate.serviceName(Cardiology.serviceId()))
        .isEqualTo(Cardiology.name());
    assertThat(serviceNameAggregate.serviceName(capitalize(Cardiology.serviceId())))
        .isEqualTo(Cardiology.name());
    // Service not present in either CMS or ATC, but is a valid service. Service enum name expected
    // to be displayed
    assertThat(serviceNameAggregate.serviceName(Pensions.serviceId())).isEqualTo(Pensions.name());
    assertThat(serviceNameAggregate.serviceName(OnlineScheduling.serviceId()))
        .isEqualTo(OnlineScheduling.name());
    assertThat(serviceNameAggregate.serviceName(Smoking.serviceId())).isEqualTo(Smoking.name());
    // Service not present in either CMS or ATC. Service is NOT valid service. Service name expected
    // to be null
    assertThat(serviceNameAggregate.serviceName("noSuchServiceId")).isNull();
    assertThat(serviceNameAggregate.serviceName(null)).isNull();
  }
}
