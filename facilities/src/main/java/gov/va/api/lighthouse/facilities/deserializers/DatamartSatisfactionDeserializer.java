package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getEffectiveDate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import gov.va.api.lighthouse.facilities.DatamartFacility.PatientSatisfaction;
import gov.va.api.lighthouse.facilities.DatamartFacility.Satisfaction;
import java.time.LocalDate;
import lombok.SneakyThrows;

public class DatamartSatisfactionDeserializer extends StdDeserializer<Satisfaction> {
  public DatamartSatisfactionDeserializer() {
    this(null);
  }

  public DatamartSatisfactionDeserializer(Class<Satisfaction> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public Satisfaction deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) {
    ObjectCodec oc = jsonParser.getCodec();
    JsonNode node = oc.readTree(jsonParser);

    // Read values using snake_case or camelCase representations
    JsonNode healthNode = node.get("health");
    JsonNode effectiveDateNode = getEffectiveDate(node);

    return Satisfaction.builder()
        .health(
            healthNode != null
                ? createMapper().convertValue(healthNode, PatientSatisfaction.class)
                : null)
        .effectiveDate(
            effectiveDateNode != null
                ? createMapper().convertValue(effectiveDateNode, LocalDate.class)
                : null)
        .build();
  }
}
