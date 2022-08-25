package gov.va.api.lighthouse.facilities.api.pssg.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static java.util.Collections.emptyList;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import gov.va.api.lighthouse.facilities.api.pssg.BandUpdateResponse;
import java.util.List;
import lombok.SneakyThrows;

public class BandUpdateResponseDeserializer extends StdDeserializer<BandUpdateResponse> {

  public BandUpdateResponseDeserializer() {
    this(null);
  }

  public BandUpdateResponseDeserializer(Class<BandUpdateResponse> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public BandUpdateResponse deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) {
    ObjectCodec oc = jsonParser.getCodec();
    JsonNode node = oc.readTree(jsonParser);
    JsonNode bandsUpdatedNode = node.get("bandsUpdated");
    JsonNode bandsCreatedNode = node.get("bandsCreated");
    List<String> bandsUpdated =
        bandsUpdatedNode != null
            ? createMapper().convertValue(bandsUpdatedNode, List.class)
            : emptyList();
    List<String> bandsCreated =
        bandsCreatedNode != null
            ? createMapper().convertValue(bandsCreatedNode, List.class)
            : emptyList();
    return BandUpdateResponse.builder()
        .bandsUpdated(bandsUpdated)
        .bandsCreated(bandsCreated)
        .build();
  }
}
