package gov.va.api.lighthouse.facilities.api.v1;

import static gov.va.api.lighthouse.facilities.api.TestUtils.getExpectedJson;
import static gov.va.api.lighthouse.facilities.api.v1.SerializerUtil.createMapper;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class FacilitiesIdsResponseEmptyFieldsTest {

  @Test
  @SneakyThrows
  void allFieldsEmpty() {
    // Null out fields for response
    String jsonEmptyResponse =
        getExpectedJson("v1/FacilitiesIdsResponse/responseWithNullFields.json");
    FacilitiesIdsResponse emptyResponse = FacilitiesIdsResponse.builder().data(null).build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyResponse))
        .isEqualTo(jsonEmptyResponse);
    // Empty fields for response
    emptyResponse = FacilitiesIdsResponse.builder().data(emptyList()).build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyResponse))
        .isEqualTo(jsonEmptyResponse);
  }

  @Test
  @SneakyThrows
  void isEmpty() {
    // Empty
    assertThat(FacilitiesIdsResponse.builder().build().isEmpty()).isTrue();
    assertThat(FacilitiesIdsResponse.builder().data(emptyList()).build().isEmpty()).isTrue();
    // Not empty
    assertThat(FacilitiesIdsResponse.builder().data(List.of("[\"vha_688\"]")).build().isEmpty())
        .isFalse();
  }
}
