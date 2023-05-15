package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.DetailedServiceAddress;
import lombok.SneakyThrows;

public class DetailedServiceAddressSerializer extends NonEmptySerializer<DetailedServiceAddress> {

  public DetailedServiceAddressSerializer() {
    this(null);
  }

  public DetailedServiceAddressSerializer(Class<DetailedServiceAddress> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(
      DetailedServiceAddress value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "buildingNameNumber", value.buildingNameNumber());
    writeNonEmpty(jgen, "wingFloorOrRoomNumber", value.wingFloorOrRoomNumber());
    writeNonEmpty(jgen, "addressLine1", value.address1());
    writeNonEmpty(jgen, "addressLine2", value.address2());
    writeNonEmpty(jgen, "city", value.city());
    writeNonEmpty(jgen, "state", value.state());
    writeNonEmpty(jgen, "zipCode", value.zipCode());
    writeNonEmpty(jgen, "countryCode", value.countryCode());
    jgen.writeEndObject();
  }
}
