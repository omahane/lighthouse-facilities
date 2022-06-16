package gov.va.api.lighthouse.facilities.api.v1;

import static gov.va.api.lighthouse.facilities.api.TestUtils.getExpectedJson;
import static gov.va.api.lighthouse.facilities.api.v1.SerializerUtil.createMapper;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class FacilitiesResponseEmptyFieldsTest {
  @Test
  @SneakyThrows
  void allFieldsEmpty() {
    // Null out fields for response
    String jsonEmptyResponse = getExpectedJson("v1/FacilitiesResponse/responseWithNullFields.json");
    FacilitiesResponse emptyResponse =
        FacilitiesResponse.builder().data(null).links(null).meta(null).build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyResponse))
        .isEqualTo(jsonEmptyResponse);
    // Response with empty fields
    emptyResponse = FacilitiesResponse.builder().data(emptyList()).links(null).meta(null).build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyResponse))
        .isEqualTo(jsonEmptyResponse);
    jsonEmptyResponse =
        getExpectedJson("v1/FacilitiesResponse/responseWithEmptyDataPageLinks.json");
    emptyResponse =
        FacilitiesResponse.builder()
            .data(emptyList())
            .links(PageLinks.builder().build())
            .meta(null)
            .build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyResponse))
        .isEqualTo(jsonEmptyResponse);
    jsonEmptyResponse =
        getExpectedJson("v1/FacilitiesResponse/responseWithEmptyDataPageLinksMeta.json");
    emptyResponse =
        FacilitiesResponse.builder()
            .data(emptyList())
            .links(PageLinks.builder().build())
            .meta(FacilitiesResponse.FacilitiesMetadata.builder().build())
            .build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyResponse))
        .isEqualTo(jsonEmptyResponse);
  }

  @Test
  @SneakyThrows
  void emptyDistance() {
    // Empty
    assertThat(FacilitiesResponse.Distance.builder().build().isEmpty()).isTrue();
    assertThat(FacilitiesResponse.Distance.builder().id("   ").build().isEmpty()).isTrue();
    // Not empty
    assertThat(FacilitiesResponse.Distance.builder().id("vha_402").build().isEmpty()).isFalse();
    assertThat(
            FacilitiesResponse.Distance.builder()
                .distance(BigDecimal.valueOf(100))
                .build()
                .isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void emptyFacilitiesMetadata() {
    // Empty
    assertThat(FacilitiesResponse.FacilitiesMetadata.builder().build().isEmpty()).isTrue();
    assertThat(
            FacilitiesResponse.FacilitiesMetadata.builder()
                .pagination(Pagination.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    assertThat(
            FacilitiesResponse.FacilitiesMetadata.builder()
                .distances(emptyList())
                .build()
                .isEmpty())
        .isTrue();
    // Not empty
    assertThat(
            FacilitiesResponse.FacilitiesMetadata.builder()
                .pagination(Pagination.builder().totalEntries(10).build())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            FacilitiesResponse.FacilitiesMetadata.builder()
                .distances(List.of(FacilitiesResponse.Distance.builder().id("test").build()))
                .build()
                .isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void isEmpty() {
    // Empty
    assertThat(FacilitiesResponse.builder().build().isEmpty()).isTrue();
    assertThat(FacilitiesResponse.builder().data(emptyList()).build().isEmpty()).isTrue();
    assertThat(
            FacilitiesResponse.builder()
                .meta(FacilitiesResponse.FacilitiesMetadata.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    assertThat(FacilitiesResponse.builder().links(PageLinks.builder().build()).build().isEmpty())
        .isTrue();
    // Not empty
    assertThat(
            FacilitiesResponse.builder()
                .data(List.of(Facility.builder().id("vha_402").build()))
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            FacilitiesResponse.builder()
                .links(PageLinks.builder().first("http://foo.bar").build())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            FacilitiesResponse.builder()
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(Pagination.builder().totalEntries(10).build())
                        .build())
                .build()
                .isEmpty())
        .isFalse();
  }
}
