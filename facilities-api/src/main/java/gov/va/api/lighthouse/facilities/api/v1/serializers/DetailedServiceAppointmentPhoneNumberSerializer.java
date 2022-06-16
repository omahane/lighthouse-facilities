package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.AppointmentPhoneNumber;
import lombok.SneakyThrows;

public class DetailedServiceAppointmentPhoneNumberSerializer
    extends NonEmptySerializer<AppointmentPhoneNumber> {

  public DetailedServiceAppointmentPhoneNumberSerializer() {
    this(null);
  }

  public DetailedServiceAppointmentPhoneNumberSerializer(Class<AppointmentPhoneNumber> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(
      AppointmentPhoneNumber value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "extension", value.extension());
    writeNonEmpty(jgen, "label", value.label());
    writeNonEmpty(jgen, "number", value.number());
    writeNonEmpty(jgen, "type", value.type());
    jgen.writeEndObject();
  }
}
