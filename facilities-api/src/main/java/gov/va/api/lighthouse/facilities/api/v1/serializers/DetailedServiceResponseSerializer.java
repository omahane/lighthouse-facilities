package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.DetailedServiceResponse;
import lombok.SneakyThrows;

public class DetailedServiceResponseSerializer extends NonEmptySerializer<DetailedServiceResponse> {

  public DetailedServiceResponseSerializer() {
    this(null);
  }

  public DetailedServiceResponseSerializer(Class<DetailedServiceResponse> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(
      DetailedServiceResponse value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "data", value.data());
    jgen.writeEndObject();
  }
}
