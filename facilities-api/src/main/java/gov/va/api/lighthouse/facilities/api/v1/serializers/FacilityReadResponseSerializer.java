package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.FacilityReadResponse;
import lombok.SneakyThrows;

public class FacilityReadResponseSerializer extends NonEmptySerializer<FacilityReadResponse> {

  public FacilityReadResponseSerializer() {
    this(null);
  }

  public FacilityReadResponseSerializer(Class<FacilityReadResponse> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(
      FacilityReadResponse value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "data", value.facility());
    jgen.writeEndObject();
  }
}
