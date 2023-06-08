package gov.va.api.lighthouse.facilities.collector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class AccessToCareCollectorTest {
  private static AccessToCareCollector accessToCareCollector;

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

    accessToCareCollector =
        new AccessToCareCollector(mockInsecureRestTemplateProvider, mockAtcBaseUrl);
  }

  @Test
  @SneakyThrows
  public void emptyWaitTime() {
    Method waitTimeMethod =
        AccessToCareCollector.class.getDeclaredMethod("waitTime", AccessToCareEntry.class);
    waitTimeMethod.setAccessible(true);
    AccessToCareEntry nullAtc = null;
    assertThat(waitTimeMethod.invoke(accessToCareCollector, nullAtc)).isNull();
    assertThat(
            waitTimeMethod.invoke(
                accessToCareCollector,
                AccessToCareEntry.builder().apptTypeName("CARDIOLOGY").build()))
        .isNull();
  }

  @Test
  @SneakyThrows
  public void waitTimeNumbers() {
    Method waitTimeNumberMethod =
        AccessToCareCollector.class.getDeclaredMethod("waitTimeNumber", BigDecimal.class);
    waitTimeNumberMethod.setAccessible(true);
    BigDecimal waitTimeNumber = null;
    assertThat((BigDecimal) waitTimeNumberMethod.invoke(accessToCareCollector, waitTimeNumber))
        .isNull();
    waitTimeNumber = new BigDecimal(999);
    assertThat((BigDecimal) waitTimeNumberMethod.invoke(accessToCareCollector, waitTimeNumber))
        .isNull();
    waitTimeNumber = new BigDecimal("160");
    assertThat((BigDecimal) waitTimeNumberMethod.invoke(accessToCareCollector, waitTimeNumber))
        .isEqualTo(waitTimeNumber);
  }
}
