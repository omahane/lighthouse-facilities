package gov.va.api.lighthouse.facilities.api.v0.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import gov.va.api.lighthouse.facilities.api.v0.DetailedService;
import gov.va.api.lighthouse.facilities.api.v0.DetailedServicesResponse;
import gov.va.api.lighthouse.facilities.api.v0.DetailedServicesResponse.DetailedServicesMetadata;
import gov.va.api.lighthouse.facilities.api.v1.PageLinks;
import java.util.List;
import lombok.SneakyThrows;

public class DetailedServicesResponseDeserializer
    extends BaseListDeserializer<DetailedServicesResponse> {
  public DetailedServicesResponseDeserializer() {
    this(null);
  }

  public DetailedServicesResponseDeserializer(Class<DetailedServicesResponse> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public DetailedServicesResponse deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) {
    ObjectCodec oc = jsonParser.getCodec();
    JsonNode node = oc.readTree(jsonParser);

    // Read values using snake_case or camelCase representations
    JsonNode dataNode = node.get("data");
    JsonNode linksNode = node.get("links");
    JsonNode metaNode = node.get("meta");

    TypeReference<List<DetailedService>> detailedServicesRef = new TypeReference<>() {};

    return DetailedServicesResponse.builder()
        .data(
            dataNode != null
                ? filterOutInvalidDetailedServices(
                    createMapper().convertValue(dataNode, detailedServicesRef))
                : null)
        .links(linksNode != null ? createMapper().convertValue(linksNode, PageLinks.class) : null)
        .meta(
            metaNode != null
                ? createMapper().convertValue(metaNode, DetailedServicesMetadata.class)
                : null)
        .build();
  }
}
