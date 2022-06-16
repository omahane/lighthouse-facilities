package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.FacilitiesResponse.Distance;
import lombok.SneakyThrows;

public class DistanceSerializer extends NonEmptySerializer<Distance> {

  public DistanceSerializer() {
    this(null);
  }

  public DistanceSerializer(Class<Distance> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(Distance value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "id", value.id());
    writeNonEmpty(jgen, "distance", value.distance());
    jgen.writeEndObject();
  }
}
