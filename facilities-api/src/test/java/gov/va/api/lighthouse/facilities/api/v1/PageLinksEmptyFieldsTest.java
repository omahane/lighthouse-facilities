package gov.va.api.lighthouse.facilities.api.v1;

import static gov.va.api.lighthouse.facilities.api.TestUtils.getExpectedJson;
import static gov.va.api.lighthouse.facilities.api.v1.SerializerUtil.createMapper;
import static org.assertj.core.api.Assertions.assertThat;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

public class PageLinksEmptyFieldsTest {

  @Test
  @SneakyThrows
  void allFieldsEmpty() {
    // Null out fields for page links
    String jsonEmptyPageLinks = getExpectedJson("v1/PageLinks/pageLinksWithNullFields.json");
    PageLinks emptyPageLinks =
        PageLinks.builder()
            .first(null)
            .prev(null)
            .next(null)
            .last(null)
            .self(null)
            .related(null)
            .build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyPageLinks))
        .isEqualTo(jsonEmptyPageLinks);
    // Page links with empty fields
    emptyPageLinks =
        PageLinks.builder()
            .first(StringUtils.EMPTY)
            .prev(StringUtils.EMPTY)
            .next(StringUtils.EMPTY)
            .last(StringUtils.EMPTY)
            .self(StringUtils.EMPTY)
            .related(StringUtils.EMPTY)
            .build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyPageLinks))
        .isEqualTo(jsonEmptyPageLinks);
    // Page links with populated fields
    String jsonPopulatedPageLinks =
        getExpectedJson("v1/PageLinks/pageLinksWithPopulatedFields.json");
    PageLinks populatedPageLinks =
        PageLinks.builder()
            .first("http://foo.bar/first")
            .prev("http://foo.bar/prev")
            .next("http://foo.bar/next")
            .last("http://foo.bar/last")
            .self("http://foo.bar/self")
            .build();
    assertThat(
            createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(populatedPageLinks))
        .isEqualTo(jsonPopulatedPageLinks);
  }

  @Test
  @SneakyThrows
  void isEmpty() {
    assertThat(PageLinks.builder().build().isEmpty()).isTrue();
    // Blank URL values
    String url = "   ";
    assertThat(PageLinks.builder().related(url).build().isEmpty()).isTrue();
    assertThat(PageLinks.builder().first(url).build().isEmpty()).isTrue();
    assertThat(PageLinks.builder().prev(url).build().isEmpty()).isTrue();
    assertThat(PageLinks.builder().self(url).build().isEmpty()).isTrue();
    assertThat(PageLinks.builder().next(url).build().isEmpty()).isTrue();
    assertThat(PageLinks.builder().last(url).build().isEmpty()).isTrue();
    // Valid URL values
    url = "http://foo.bar";
    assertThat(PageLinks.builder().related(url).build().isEmpty()).isFalse();
    assertThat(PageLinks.builder().first(url).build().isEmpty()).isFalse();
    assertThat(PageLinks.builder().prev(url).build().isEmpty()).isFalse();
    assertThat(PageLinks.builder().self(url).build().isEmpty()).isFalse();
    assertThat(PageLinks.builder().next(url).build().isEmpty()).isFalse();
    assertThat(PageLinks.builder().last(url).build().isEmpty()).isFalse();
  }
}
