package gov.va.api.lighthouse.facilities.api.v0;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.TestUtils.getExpectedJson;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class FacilitiesIdsResponseEmptyFieldsTest {

  @Test
  @SneakyThrows
  void allFieldsEmpty() {
    // Null out fields for response
    String jsonEmptyResponse =
        getExpectedJson("v0/FacilitiesIdsResponse/responseWithNullFields.json");
    FacilitiesIdsResponse emptyResponse = FacilitiesIdsResponse.builder().data(null).build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyResponse))
        .isEqualTo(jsonEmptyResponse);
    // Empty fields for response
    jsonEmptyResponse = getExpectedJson("v0/FacilitiesIdsResponse/responseWithEmptyFields.json");
    emptyResponse = FacilitiesIdsResponse.builder().data(emptyList()).build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyResponse))
        .isEqualTo(jsonEmptyResponse);
  }
}
