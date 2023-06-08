package gov.va.api.lighthouse.facilities.collector;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
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

    assertDoesNotThrow(
        () -> new AccessToPwtCollector(mockInsecureRestTemplateProvider, "http://atp/"));

    final Method loadAccessToPwtMethod =
        AccessToPwtCollector.class.getDeclaredMethod("loadAccessToPwt", null);
    loadAccessToPwtMethod.setAccessible(true);
    final AccessToPwtCollector collector =
        new AccessToPwtCollector(mockInsecureRestTemplateProvider, "http://atp/");
    assertDoesNotThrow(() -> loadAccessToPwtMethod.invoke(collector, null));
  }
}
