package gov.va.api.lighthouse.facilities.api.pssg.serializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import gov.va.api.lighthouse.facilities.api.pssg.BandUpdateResponse;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class SerializerTest {
  @SneakyThrows
  private <T> void assertJson(T value, String expectedJson) {
    assertThat(createMapper().writeValueAsString(value)).isEqualTo(expectedJson);
  }

  @SneakyThrows
  private <T> void assertJsonIsEmpty(T value) {
    assertJson(value, "{}");
  }

  @Test
  @SneakyThrows
  void exceptions() {
    assertThatThrownBy(() -> BandUpdateResponse.builder().build())
        .isInstanceOf(NullPointerException.class)
        .hasMessage("bandsCreated is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void serializeBandUpdateResponse() {
    // Empty
    BandUpdateResponse response =
        BandUpdateResponse.builder().bandsUpdated(emptyList()).bandsCreated(emptyList()).build();
    assertJsonIsEmpty(response);
    // Not empty
    response =
        BandUpdateResponse.builder()
            .bandsUpdated(List.of("vba_123", "vba_456"))
            .bandsCreated(List.of("vba_789"))
            .build();
    assertJson(
        response, "{\"bandsCreated\":[\"vba_789\"],\"bandsUpdated\":[\"vba_123\",\"vba_456\"]}");
    response =
        BandUpdateResponse.builder()
            .bandsUpdated(emptyList())
            .bandsCreated(List.of("vba_789"))
            .build();
    assertJson(response, "{\"bandsCreated\":[\"vba_789\"]}");
    response =
        BandUpdateResponse.builder()
            .bandsUpdated(List.of("vba_123", "vba_456"))
            .bandsCreated(emptyList())
            .build();
    assertJson(response, "{\"bandsUpdated\":[\"vba_123\",\"vba_456\"]}");
  }
}
