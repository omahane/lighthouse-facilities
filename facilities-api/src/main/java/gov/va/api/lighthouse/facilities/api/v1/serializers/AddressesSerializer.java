package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.Facility.Addresses;
import lombok.SneakyThrows;

public class AddressesSerializer extends NonEmptySerializer<Addresses> {

  public AddressesSerializer() {
    this(null);
  }

  public AddressesSerializer(Class<Addresses> t) {
    super(t);
  }

  private static boolean empty(Addresses value) {
    return value.mailing() == null && value.physical() == null;
  }

  @Override
  @SneakyThrows
  public void serialize(Addresses value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    if (!empty(value)) {
      writeNonEmpty(jgen, "mailing", value.mailing());
      writeNonEmpty(jgen, "physical", value.physical());
    }
    jgen.writeEndObject();
  }
}
