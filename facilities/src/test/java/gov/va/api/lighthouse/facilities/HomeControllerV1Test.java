package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;

public class HomeControllerV1Test {

  @Test
  @SneakyThrows
  void openapiJson() {
    assertThat(
            HomeControllerV1.builder()
                .openapi(new ByteArrayResource("{}".getBytes()))
                .build()
                .openapiJson())
        .isEqualTo("{}");
  }
}
