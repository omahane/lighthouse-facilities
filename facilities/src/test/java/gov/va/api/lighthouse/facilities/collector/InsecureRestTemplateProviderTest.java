package gov.va.api.lighthouse.facilities.collector;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class InsecureRestTemplateProviderTest {
  @Test
  @SneakyThrows
  void restTemplate() {
    InsecureRestTemplateProvider restTemplateProvider = new InsecureRestTemplateProvider();
    RestTemplate restTemplate = restTemplateProvider.restTemplate();
    assertThat(restTemplate.getRequestFactory())
        .isInstanceOfAny(HttpComponentsClientHttpRequestFactory.class);
  }
}
