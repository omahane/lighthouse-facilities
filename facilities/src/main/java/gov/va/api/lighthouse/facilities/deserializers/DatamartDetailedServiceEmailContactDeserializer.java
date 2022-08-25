package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getEmailAddress;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getEmailLabel;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import gov.va.api.lighthouse.facilities.DatamartDetailedService.DetailedServiceEmailContact;
import lombok.SneakyThrows;

public class DatamartDetailedServiceEmailContactDeserializer
    extends StdDeserializer<DetailedServiceEmailContact> {
  public DatamartDetailedServiceEmailContactDeserializer() {
    this(null);
  }

  public DatamartDetailedServiceEmailContactDeserializer(Class<DetailedServiceEmailContact> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public DetailedServiceEmailContact deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) {
    ObjectCodec oc = jsonParser.getCodec();
    JsonNode node = oc.readTree(jsonParser);

    // Read values using snake_case or camelCase representations
    JsonNode emailAddressNode = getEmailAddress(node);
    JsonNode emailLabelNode = getEmailLabel(node);

    return DetailedServiceEmailContact.builder()
        .emailAddress(
            emailAddressNode != null
                ? createMapper().convertValue(emailAddressNode, String.class)
                : null)
        .emailLabel(
            emailLabelNode != null
                ? createMapper().convertValue(emailLabelNode, String.class)
                : null)
        .build();
  }
}
