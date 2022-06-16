package gov.va.api.lighthouse.facilities.api.pssg.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.pssg.BandUpdateResponse;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class DeserializerTest {
  @SneakyThrows
  private <T> void assertJson(String json, Class<T> expectedClass, T expectedValue) {
    assertThat(createMapper().readValue(json, expectedClass)).isEqualTo(expectedValue);
  }

  @Test
  @SneakyThrows
  void deserializeBandUpdateResponse() {
    // Empty
    BandUpdateResponse response =
        BandUpdateResponse.builder().bandsUpdated(emptyList()).bandsCreated(emptyList()).build();
    assertJson("{}", BandUpdateResponse.class, response);
    response =
        BandUpdateResponse.builder().bandsUpdated(emptyList()).bandsCreated(emptyList()).build();
    assertJson("{\"bandsCreated\":[],\"bandsUpdated\":[]}", BandUpdateResponse.class, response);
    // Not empty
    response =
        BandUpdateResponse.builder()
            .bandsUpdated(List.of("vba_123", "vba_456"))
            .bandsCreated(List.of("vba_789"))
            .build();
    assertJson(
        "{\"bandsCreated\":[\"vba_789\"],\"bandsUpdated\":[\"vba_123\",\"vba_456\"]}",
        BandUpdateResponse.class,
        response);
    response =
        BandUpdateResponse.builder()
            .bandsUpdated(emptyList())
            .bandsCreated(List.of("vba_789"))
            .build();
    assertJson("{\"bandsCreated\":[\"vba_789\"]}", BandUpdateResponse.class, response);
    response =
        BandUpdateResponse.builder()
            .bandsUpdated(List.of("vba_123", "vba_456"))
            .bandsCreated(emptyList())
            .build();
    assertJson("{\"bandsUpdated\":[\"vba_123\",\"vba_456\"]}", BandUpdateResponse.class, response);
  }
}
