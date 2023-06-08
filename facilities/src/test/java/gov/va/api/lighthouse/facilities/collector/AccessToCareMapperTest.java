package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.Cardiology;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.MentalHealth;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class AccessToCareMapperTest {
  private static AccessToCareMapper accessToCareMapper;

  @BeforeAll
  @SneakyThrows
  static void setup() {
    final var mockAtcBaseUrl = "http://atc/";
    final var accessToCareEntries =
        List.of(
            AccessToCareEntry.builder().facilityId("689").apptTypeName("AUDIOLOGY").build(),
            AccessToCareEntry.builder().facilityId("689").apptTypeName("CARDIOLOGY").build(),
            AccessToCareEntry.builder().facilityId("689").apptTypeName("WOMEN'S HEALTH").build(),
            AccessToCareEntry.builder().facilityId("689").apptTypeName("DERMATOLOGY").build(),
            AccessToCareEntry.builder().facilityId("689").apptTypeName("GASTROENTEROLOGY").build(),
            AccessToCareEntry.builder().facilityId("689").apptTypeName("OB/GYN").build(),
            AccessToCareEntry.builder()
                .facilityId("689")
                .apptTypeName("MENTAL HEALTH INDIVIDUAL")
                .build(),
            AccessToCareEntry.builder().facilityId("689").apptTypeName("OPTOMETRY").build(),
            AccessToCareEntry.builder().facilityId("689").apptTypeName("ORTHOPEDICS").build(),
            AccessToCareEntry.builder().facilityId("689").apptTypeName("PRIMARY CARE").build(),
            AccessToCareEntry.builder().facilityId("689").apptTypeName("SPECIALTY CARE").build(),
            AccessToCareEntry.builder().facilityId("689").apptTypeName("UROLOGY").build());

    final RestTemplate mockRestTemplate = mock(RestTemplate.class);
    when(mockRestTemplate.exchange(
            startsWith(mockAtcBaseUrl),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(
            ResponseEntity.of(
                Optional.of(JacksonConfig.createMapper().writeValueAsString(accessToCareEntries))));
    final InsecureRestTemplateProvider mockInsecureRestTemplateProvider =
        mock(InsecureRestTemplateProvider.class);
    when(mockInsecureRestTemplateProvider.restTemplate()).thenReturn(mockRestTemplate);

    accessToCareMapper = new AccessToCareMapper(mockInsecureRestTemplateProvider, mockAtcBaseUrl);
  }

  @Test
  @SneakyThrows
  public void healthService() {
    // Valid Service
    assertThat(
            accessToCareMapper.healthService(
                AccessToCareEntry.builder().apptTypeName("CARDIOLOGY").build()))
        .isEqualTo(Cardiology);
    assertThat(
            accessToCareMapper.healthService(
                AccessToCareEntry.builder().apptTypeName("Cardiology").build()))
        .isEqualTo(Cardiology);
    assertThat(
            accessToCareMapper.healthService(
                AccessToCareEntry.builder().apptTypeName("MENTAL HEALTH INDIVIDUAL").build()))
        .isEqualTo(MentalHealth);
    assertThat(
            accessToCareMapper.healthService(
                AccessToCareEntry.builder().apptTypeName("Mental Health Individual").build()))
        .isEqualTo(MentalHealth);
    // Invalid Service
    final AccessToCareEntry nullAtc = null;
    assertThat(accessToCareMapper.healthService(nullAtc)).isNull();
    assertThat(
            accessToCareMapper.healthService(
                AccessToCareEntry.builder().apptTypeName("NO SUCH SERVICE").build()))
        .isNull();
  }
}
