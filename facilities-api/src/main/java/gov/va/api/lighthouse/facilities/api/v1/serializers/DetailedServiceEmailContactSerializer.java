package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.DetailedServiceEmailContact;
import lombok.SneakyThrows;

public class DetailedServiceEmailContactSerializer
    extends NonEmptySerializer<DetailedServiceEmailContact> {

  public DetailedServiceEmailContactSerializer() {
    this(null);
  }

  public DetailedServiceEmailContactSerializer(Class<DetailedServiceEmailContact> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(
      DetailedServiceEmailContact value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "emailAddress", value.emailAddress());
    writeNonEmpty(jgen, "emailLabel", value.emailLabel());
    jgen.writeEndObject();
  }
}
