package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;

public class HomeControllerV0Test {

  @Test
  @SneakyThrows
  void openapiJson() {
    assertThat(
            HomeControllerV0.builder()
                .openapi(new ByteArrayResource("{}".getBytes()))
                .build()
                .openapiJson())
        .isEqualTo("{}");
  }
}
