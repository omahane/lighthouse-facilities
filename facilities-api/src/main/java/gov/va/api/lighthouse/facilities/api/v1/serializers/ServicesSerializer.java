package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.Facility.Services;
import lombok.SneakyThrows;

public class ServicesSerializer extends NonEmptySerializer<Services> {

  public ServicesSerializer() {
    this(null);
  }

  public ServicesSerializer(Class<Services> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(Services value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "health", value.health());
    writeNonEmpty(jgen, "benefits", value.benefits());
    writeNonEmpty(jgen, "other", value.other());
    writeNonEmpty(jgen, "link", value.link());
    writeNonEmpty(jgen, "lastUpdated", value.lastUpdated());
    jgen.writeEndObject();
  }
}
