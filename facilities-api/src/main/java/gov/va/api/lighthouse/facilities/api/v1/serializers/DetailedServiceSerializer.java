package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService;
import lombok.SneakyThrows;

public class DetailedServiceSerializer extends NonEmptySerializer<DetailedService> {

  public DetailedServiceSerializer() {
    this(null);
  }

  public DetailedServiceSerializer(Class<DetailedService> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(DetailedService value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "serviceInfo", value.serviceInfo());
    writeNonEmpty(jgen, "appointmentLeadIn", value.appointmentLeadIn());
    writeNonEmpty(jgen, "appointmentPhones", value.phoneNumbers());
    writeNonEmpty(jgen, "onlineSchedulingAvailable", value.onlineSchedulingAvailable());
    writeNonEmpty(jgen, "referralRequired", value.referralRequired());
    writeNonEmpty(jgen, "walkInsAccepted", value.walkInsAccepted());
    writeNonEmpty(jgen, "serviceLocations", value.serviceLocations());
    writeNonEmpty(jgen, "path", value.path());
    jgen.writeEndObject();
  }
}
