package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.NearbyResponse;
import lombok.SneakyThrows;

public class NearbyResponseSerializer extends NonEmptySerializer<NearbyResponse> {

  public NearbyResponseSerializer() {
    this(null);
  }

  public NearbyResponseSerializer(Class<NearbyResponse> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(NearbyResponse value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "data", value.data());
    writeNonEmpty(jgen, "meta", value.meta());
    jgen.writeEndObject();
  }
}
