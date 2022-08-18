package gov.va.api.lighthouse.facilities.collector;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class AccessToPwtCollectorTest {
  @Test
  @SneakyThrows
  public void exceptions() {
    final RestTemplate mockRestTemplate = mock(RestTemplate.class);
    when(mockRestTemplate.exchange(
            startsWith("http"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
        .thenThrow(new RestClientException("oh noez"));

    final InsecureRestTemplateProvider mockInsecureRestTemplateProvider =
        mock(InsecureRestTemplateProvider.class);
    when(mockInsecureRestTemplateProvider.restTemplate()).thenReturn(mockRestTemplate);

    assertThatThrownBy(
            () -> new AccessToPwtCollector(mockInsecureRestTemplateProvider, "http://atp/"))
        .isInstanceOf(RestClientException.class)
        .hasMessage("oh noez");
  }
}
