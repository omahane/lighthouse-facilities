package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import lombok.SneakyThrows;

public class FacilitySerializer extends NonEmptySerializer<Facility> {

  public FacilitySerializer() {
    this(null);
  }

  public FacilitySerializer(Class<Facility> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(Facility value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "id", value.id());
    writeNonEmpty(jgen, "type", value.type());
    writeNonEmpty(jgen, "attributes", value.attributes());
    jgen.writeEndObject();
  }
}
