package gov.va.api.lighthouse.facilities.api.v0;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.TestUtils.getExpectedJson;
import static org.assertj.core.api.Assertions.assertThat;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

public class PageLinksEmptyFieldsTest {

  @Test
  @SneakyThrows
  void allFieldsEmpty() {
    // Null out fields for page links
    String jsonEmptyPageLinks = getExpectedJson("v0/PageLinks/pageLinksWithNullFields.json");
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
    jsonEmptyPageLinks = getExpectedJson("v0/PageLinks/pageLinksWithEmptyFields.json");
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
        getExpectedJson("v0/PageLinks/pageLinksWithPopulatedFields.json");
    PageLinks populatedPageLinks =
        PageLinks.builder()
            .first("http://foo.bar/first")
            .prev("http://foo.bar/prev")
            .next("http://foo.bar/next")
            .last("http://foo.bar/last")
            .self("http://foo.bar/self")
            .related("http://foo.bar/related")
            .build();
    assertThat(
            createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(populatedPageLinks))
        .isEqualTo(jsonPopulatedPageLinks);
  }
}
