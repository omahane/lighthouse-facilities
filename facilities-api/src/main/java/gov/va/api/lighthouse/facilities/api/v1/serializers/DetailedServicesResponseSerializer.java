package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.DetailedServicesResponse;
import lombok.SneakyThrows;

public class DetailedServicesResponseSerializer
    extends NonEmptySerializer<DetailedServicesResponse> {

  public DetailedServicesResponseSerializer() {
    this(null);
  }

  public DetailedServicesResponseSerializer(Class<DetailedServicesResponse> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(
      DetailedServicesResponse value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "data", value.data());
    writeNonEmpty(jgen, "links", value.links());
    writeNonEmpty(jgen, "meta", value.meta());
    jgen.writeEndObject();
  }
}
