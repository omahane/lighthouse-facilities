package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.DetailedServiceLocation;
import lombok.SneakyThrows;

public class DetailedServiceLocationSerializer extends NonEmptySerializer<DetailedServiceLocation> {

  public DetailedServiceLocationSerializer() {
    this(null);
  }

  public DetailedServiceLocationSerializer(Class<DetailedServiceLocation> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(
      DetailedServiceLocation value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "serviceLocationAddress", value.serviceLocationAddress());
    writeNonEmpty(jgen, "appointmentPhones", value.appointmentPhoneNumbers());
    writeNonEmpty(jgen, "emailContacts", value.emailContacts());
    writeNonEmpty(jgen, "facilityServiceHours", value.facilityServiceHours());
    writeNonEmpty(jgen, "additionalHoursInfo", value.additionalHoursInfo());
    jgen.writeEndObject();
  }
}
