package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.ServiceInfo;
import lombok.SneakyThrows;

public class DetailedServiceInfoSerializer extends NonEmptySerializer<ServiceInfo> {

  public DetailedServiceInfoSerializer() {
    this(null);
  }

  public DetailedServiceInfoSerializer(Class<ServiceInfo> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(ServiceInfo value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "name", value.name());
    writeNonEmpty(jgen, "serviceId", value.serviceId());
    writeNonEmpty(jgen, "serviceType", value.serviceType());
    jgen.writeEndObject();
  }
}
