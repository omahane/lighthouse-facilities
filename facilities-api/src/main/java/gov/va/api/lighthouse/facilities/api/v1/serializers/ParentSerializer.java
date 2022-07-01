package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.Facility.Parent;
import lombok.SneakyThrows;

public class ParentSerializer extends NonEmptySerializer<Parent> {
  public ParentSerializer() {
    this(null);
  }

  public ParentSerializer(Class<Parent> p) {
    super(p);
  }

  @Override
  @SneakyThrows
  public void serialize(Parent value, JsonGenerator jgen, SerializerProvider serializerProvider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "id", value.id());
    writeNonEmpty(jgen, "link", value.link());
    jgen.writeEndObject();
  }
}
