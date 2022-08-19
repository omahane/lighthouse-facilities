package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.Pensions;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.Audiology;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.Cardiology;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.Covid19Vaccine;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.EmergencyCare;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.PrimaryCare;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.WomensHealth;
import static gov.va.api.lighthouse.facilities.DatamartFacility.OtherService.OnlineScheduling;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ServiceNameAggregatorTest {

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
        .thenReturn(Optional.of(PrimaryCare.name()));
    when(mockCmsOverlayMapper.serviceNameForServiceId(EmergencyCare.serviceId()))
        .thenReturn(Optional.of(EmergencyCare.name()));
  }

  @Test
  void serviceName() {
    ServiceNameAggregator serviceNameAggregator =
        new ServiceNameAggregator(mockAccessToCareMapper, mockCmsOverlayMapper);
    // Service present in ATC, but absent from CMS. ATC service name expected to be displayed
    assertThat(serviceNameAggregator.serviceName(WomensHealth.serviceId()))
        .isEqualTo("WOMEN'S HEALTH");
    // Service present in CMS, but absent from ATC. CMS service name expected to be displayed
    assertThat(serviceNameAggregator.serviceName(Covid19Vaccine.serviceId()))
        .isEqualTo("COVID-19 vaccines");
    assertThat(serviceNameAggregator.serviceName(PrimaryCare.serviceId()))
        .isEqualTo(PrimaryCare.name());
    assertThat(serviceNameAggregator.serviceName(EmergencyCare.serviceId()))
        .isEqualTo(EmergencyCare.name());
    // Service present in both CMS and ATC. CMS service name expected to be displayed
    assertThat(serviceNameAggregator.serviceName(Audiology.serviceId()))
        .isEqualTo(Audiology.name());
    assertThat(serviceNameAggregator.serviceName(Cardiology.serviceId()))
        .isEqualTo(Cardiology.name());
    // Service not present in either CMS or ATC, but is a valid service. Service enum name expected
    // to be displayed
    assertThat(serviceNameAggregator.serviceName(Pensions.serviceId())).isEqualTo(Pensions.name());
    assertThat(serviceNameAggregator.serviceName(OnlineScheduling.serviceId()))
        .isEqualTo(OnlineScheduling.name());
    // Service not present in either CMS or ATC. Service is NOT valid service. Service name expected
    // to be null
    assertThat(serviceNameAggregator.serviceName("noSuchServiceId")).isNull();
    assertThat(serviceNameAggregator.serviceName(null)).isNull();
  }
}
