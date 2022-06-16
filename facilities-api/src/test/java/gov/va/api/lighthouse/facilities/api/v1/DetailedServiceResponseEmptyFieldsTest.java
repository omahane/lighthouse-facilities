package gov.va.api.lighthouse.facilities.api.v1;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class DetailedServiceResponseEmptyFieldsTest {

  @Test
  @SneakyThrows
  void isEmpty() {
    // Empty
    assertThat(DetailedServiceResponse.builder().build().isEmpty()).isTrue();
    assertThat(
            DetailedServiceResponse.builder()
                .data(DetailedService.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    // Not empty
    assertThat(
            DetailedServiceResponse.builder()
                .data(DetailedService.builder().name("test").build())
                .build()
                .isEmpty())
        .isFalse();
  }
}
