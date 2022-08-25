package gov.va.api.lighthouse.facilities.api.pssg.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static java.util.Collections.emptyList;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import gov.va.api.lighthouse.facilities.api.pssg.BandUpdateResponse;
import java.util.List;
import lombok.SneakyThrows;

public class BandUpdateResponseDeserializer extends StdDeserializer<BandUpdateResponse> {

  private static final ObjectMapper MAPPER = createMapper();

  public BandUpdateResponseDeserializer() {
    this(null);
  }

  public BandUpdateResponseDeserializer(Class<BandUpdateResponse> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public BandUpdateResponse deserialize(
      JsonParser jp, DeserializationContext deserializationContext) {
    JsonNode node = jp.getCodec().readTree(jp);
    JsonNode bandsUpdatedNode = node.get("bandsUpdated");
    JsonNode bandsCreatedNode = node.get("bandsCreated");

    TypeReference<List<String>> bandsList = new TypeReference<>() {};
    return BandUpdateResponse.builder()
        .bandsUpdated(
            isBlank(bandsUpdatedNode)
                ? emptyList()
                : MAPPER.convertValue(bandsUpdatedNode, bandsList))
        .bandsCreated(
            isBlank(bandsCreatedNode)
                ? emptyList()
                : MAPPER.convertValue(bandsCreatedNode, bandsList))
        .build();
  }

  private boolean isBlank(JsonNode node) {
    return node == null || node.isNull();
  }
}
