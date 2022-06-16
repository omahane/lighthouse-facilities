package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.FacilitiesIdsResponse;
import lombok.SneakyThrows;

public class FacilitiesIdsResponseSerializer extends NonEmptySerializer<FacilitiesIdsResponse> {

  public FacilitiesIdsResponseSerializer() {
    this(null);
  }

  public FacilitiesIdsResponseSerializer(Class<FacilitiesIdsResponse> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(
      FacilitiesIdsResponse value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "data", value.data());
    jgen.writeEndObject();
  }
}
