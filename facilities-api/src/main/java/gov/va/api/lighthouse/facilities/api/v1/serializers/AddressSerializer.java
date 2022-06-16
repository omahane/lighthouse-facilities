package gov.va.api.lighthouse.facilities.api.v1.serializers;

import static gov.va.api.lighthouse.facilities.api.v1.serializers.SerializerHelper.idStartsWith;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.Facility.Address;
import lombok.SneakyThrows;

public class AddressSerializer extends NonEmptySerializer<Address> {

  public AddressSerializer() {
    this(null);
  }

  public AddressSerializer(Class<Address> t) {
    super(t);
  }

  private static boolean emptyExcludeAddr1(Address value) {
    return value.zip() == null
        && value.city() == null
        && value.state() == null
        && value.address2() == null
        && value.address3() == null;
  }

  @Override
  @SneakyThrows
  public void serialize(Address value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    if (emptyExcludeAddr1(value) && idStartsWith(jgen, "nca_s")) {
      writeNonEmpty(jgen, "address_1", value.address1());
    } else {
      writeNonEmpty(jgen, "zip", value.zip());
      writeNonEmpty(jgen, "city", value.city());
      writeNonEmpty(jgen, "state", value.state());
      writeNonEmpty(jgen, "address1", value.address1());
      writeNonEmpty(jgen, "address2", value.address2());
      writeNonEmpty(jgen, "address3", value.address3());
    }
    jgen.writeEndObject();
  }
}
