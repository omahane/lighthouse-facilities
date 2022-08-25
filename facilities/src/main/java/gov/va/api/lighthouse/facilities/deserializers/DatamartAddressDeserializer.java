package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getAddress1;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getAddress2;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getAddress3;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import gov.va.api.lighthouse.facilities.DatamartFacility.Address;
import lombok.SneakyThrows;

public class DatamartAddressDeserializer extends StdDeserializer<Address> {
  public DatamartAddressDeserializer() {
    this(null);
  }

  public DatamartAddressDeserializer(Class<Address> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public Address deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
    ObjectCodec oc = jsonParser.getCodec();
    JsonNode node = oc.readTree(jsonParser);

    // Read values using snake_case or camelCase representations
    JsonNode address1Node = getAddress1(node);
    JsonNode address2Node = getAddress2(node);
    JsonNode address3Node = getAddress3(node);
    JsonNode cityNode = node.get("city");
    JsonNode stateNode = node.get("state");
    JsonNode zipNode = node.get("zip");

    return Address.builder()
        .address1(
            address1Node != null ? createMapper().convertValue(address1Node, String.class) : null)
        .address2(
            address2Node != null ? createMapper().convertValue(address2Node, String.class) : null)
        .address3(
            address3Node != null ? createMapper().convertValue(address3Node, String.class) : null)
        .city(cityNode != null ? createMapper().convertValue(cityNode, String.class) : null)
        .state(stateNode != null ? createMapper().convertValue(stateNode, String.class) : null)
        .zip(zipNode != null ? createMapper().convertValue(zipNode, String.class) : null)
        .build();
  }
}
