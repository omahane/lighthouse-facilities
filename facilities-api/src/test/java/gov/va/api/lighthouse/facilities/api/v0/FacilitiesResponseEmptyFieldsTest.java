package gov.va.api.lighthouse.facilities.api.v0;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.TestUtils.getExpectedJson;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class FacilitiesResponseEmptyFieldsTest {

  @Test
  @SneakyThrows
  void allFieldsEmpty() {
    // Null out fields for response
    String jsonEmptyResponse = getExpectedJson("v0/FacilitiesResponse/responseWithNullFields.json");
    FacilitiesResponse emptyResponse =
        FacilitiesResponse.builder().data(null).links(null).meta(null).build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyResponse))
        .isEqualTo(jsonEmptyResponse);
    // Response with empty fields
    jsonEmptyResponse = getExpectedJson("v0/FacilitiesResponse/responseWithEmptyFields.json");
    emptyResponse = FacilitiesResponse.builder().data(emptyList()).links(null).meta(null).build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyResponse))
        .isEqualTo(jsonEmptyResponse);
    jsonEmptyResponse =
        getExpectedJson("v0/FacilitiesResponse/responseWithEmptyDataPageLinks.json");
    emptyResponse =
        FacilitiesResponse.builder()
            .data(emptyList())
            .links(PageLinks.builder().build())
            .meta(null)
            .build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyResponse))
        .isEqualTo(jsonEmptyResponse);
    jsonEmptyResponse =
        getExpectedJson("v0/FacilitiesResponse/responseWithEmptyDataPageLinksMeta.json");
    emptyResponse =
        FacilitiesResponse.builder()
            .data(emptyList())
            .links(PageLinks.builder().build())
            .meta(FacilitiesResponse.FacilitiesMetadata.builder().build())
            .build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyResponse))
        .isEqualTo(jsonEmptyResponse);
  }
}
