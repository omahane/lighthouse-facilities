package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.FacilitiesResponse.FacilitiesMetadata;
import lombok.SneakyThrows;

public class FacilitiesMetadataSerializer extends NonEmptySerializer<FacilitiesMetadata> {

  public FacilitiesMetadataSerializer() {
    this(null);
  }

  public FacilitiesMetadataSerializer(Class<FacilitiesMetadata> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(FacilitiesMetadata value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "pagination", value.pagination());
    writeNonEmpty(jgen, "distances", value.distances());
    jgen.writeEndObject();
  }
}
