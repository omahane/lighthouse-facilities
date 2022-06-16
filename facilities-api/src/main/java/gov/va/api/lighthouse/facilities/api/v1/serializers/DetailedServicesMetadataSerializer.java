package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.DetailedServicesResponse.DetailedServicesMetadata;
import lombok.SneakyThrows;

public class DetailedServicesMetadataSerializer
    extends NonEmptySerializer<DetailedServicesMetadata> {

  public DetailedServicesMetadataSerializer() {
    this(null);
  }

  public DetailedServicesMetadataSerializer(Class<DetailedServicesMetadata> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(
      DetailedServicesMetadata value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "pagination", value.pagination());
    jgen.writeEndObject();
  }
}
