package gov.va.api.lighthouse.facilities.api.v1;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class DetailedServicesResponseEmptyFieldsTest {
  @Test
  @SneakyThrows
  void emptyMetaData() {
    // Empty
    assertThat(DetailedServicesResponse.DetailedServicesMetadata.builder().build().isEmpty())
        .isTrue();
    assertThat(
            DetailedServicesResponse.DetailedServicesMetadata.builder()
                .pagination(Pagination.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    // Not empty
    assertThat(
            DetailedServicesResponse.DetailedServicesMetadata.builder()
                .pagination(Pagination.builder().totalEntries(10).build())
                .build()
                .isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void isEmpty() {
    // Empty
    assertThat(DetailedServicesResponse.builder().build().isEmpty()).isTrue();
    assertThat(DetailedServicesResponse.builder().data(emptyList()).build().isEmpty()).isTrue();
    assertThat(
            DetailedServicesResponse.builder().links(PageLinks.builder().build()).build().isEmpty())
        .isTrue();
    assertThat(
            DetailedServicesResponse.builder()
                .meta(DetailedServicesResponse.DetailedServicesMetadata.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    // Not empty
    assertThat(
            DetailedServicesResponse.builder()
                .data(List.of(DetailedService.builder().name("test").build()))
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedServicesResponse.builder()
                .meta(
                    DetailedServicesResponse.DetailedServicesMetadata.builder()
                        .pagination(Pagination.builder().totalEntries(10).build())
                        .build())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedServicesResponse.builder()
                .links(PageLinks.builder().self("http://foo.bar").build())
                .build()
                .isEmpty())
        .isFalse();
  }
}
