package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.FacilitiesResponse;
import lombok.SneakyThrows;

public class FacilitiesResponseSerializer extends NonEmptySerializer<FacilitiesResponse> {

  public FacilitiesResponseSerializer() {
    this(null);
  }

  public FacilitiesResponseSerializer(Class<FacilitiesResponse> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(FacilitiesResponse value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "data", value.data());
    writeNonEmpty(jgen, "links", value.links());
    writeNonEmpty(jgen, "meta", value.meta());
    jgen.writeEndObject();
  }
}
