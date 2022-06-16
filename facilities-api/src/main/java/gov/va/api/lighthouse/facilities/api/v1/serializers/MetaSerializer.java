package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.NearbyResponse.Meta;
import lombok.SneakyThrows;

public class MetaSerializer extends NonEmptySerializer<Meta> {

  public MetaSerializer() {
    this(null);
  }

  public MetaSerializer(Class<Meta> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(Meta value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "bandVersion", value.bandVersion());
    jgen.writeEndObject();
  }
}
