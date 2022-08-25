package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getLastUpdated;
import static java.util.Collections.emptyList;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService;
import gov.va.api.lighthouse.facilities.DatamartFacility.HealthService;
import gov.va.api.lighthouse.facilities.DatamartFacility.OtherService;
import gov.va.api.lighthouse.facilities.DatamartFacility.Services;
import java.time.LocalDate;
import java.util.List;
import lombok.SneakyThrows;

public class DatamartServicesDeserializer extends StdDeserializer<Services> {
  public DatamartServicesDeserializer() {
    this(null);
  }

  public DatamartServicesDeserializer(Class<Services> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public Services deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) {
    ObjectCodec oc = jsonParser.getCodec();
    JsonNode node = oc.readTree(jsonParser);

    // Read values using snake_case or camelCase representations
    JsonNode benefitsNode = node.get("benefits");
    JsonNode healthNode = node.get("health");
    JsonNode otherNode = node.get("other");
    JsonNode lastUpdatedNode = getLastUpdated(node);

    TypeReference<List<BenefitsService>> benefitsRef = new TypeReference<>() {};
    TypeReference<List<HealthService>> healthRef = new TypeReference<>() {};
    TypeReference<List<OtherService>> otherRef = new TypeReference<>() {};

    return Services.builder()
        .benefits(
            healthNode != null
                ? createMapper().convertValue(benefitsNode, benefitsRef)
                : emptyList())
        .health(
            healthNode != null ? createMapper().convertValue(healthNode, healthRef) : emptyList())
        .other(healthNode != null ? createMapper().convertValue(otherNode, otherRef) : emptyList())
        .lastUpdated(
            lastUpdatedNode != null
                ? createMapper().convertValue(lastUpdatedNode, LocalDate.class)
                : null)
        .build();
  }
}
