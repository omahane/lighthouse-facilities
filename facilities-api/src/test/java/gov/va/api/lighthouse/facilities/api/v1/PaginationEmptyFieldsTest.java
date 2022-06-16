package gov.va.api.lighthouse.facilities.api.v1;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class PaginationEmptyFieldsTest {

  @Test
  @SneakyThrows
  void isEmpty() {
    // Empty
    assertThat(Pagination.builder().build().isEmpty()).isTrue();
    // Not empty
    assertThat(Pagination.builder().currentPage(0).build().isEmpty()).isFalse();
    assertThat(Pagination.builder().entriesPerPage(10).build().isEmpty()).isFalse();
    assertThat(Pagination.builder().totalPages(3).build().isEmpty()).isFalse();
    assertThat(Pagination.builder().totalEntries(30).build().isEmpty()).isFalse();
  }
}
