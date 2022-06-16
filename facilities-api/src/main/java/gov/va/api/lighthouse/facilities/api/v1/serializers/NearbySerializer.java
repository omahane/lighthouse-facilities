package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.NearbyResponse.Nearby;
import lombok.SneakyThrows;

public class NearbySerializer extends NonEmptySerializer<Nearby> {

  public NearbySerializer() {
    this(null);
  }

  public NearbySerializer(Class<Nearby> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(Nearby value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "id", value.id());
    writeNonEmpty(jgen, "type", value.type());
    writeNonEmpty(jgen, "attributes", value.attributes());
    jgen.writeEndObject();
  }
}
