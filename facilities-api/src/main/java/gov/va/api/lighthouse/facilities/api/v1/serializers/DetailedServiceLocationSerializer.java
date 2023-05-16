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
    writeNonEmpty(jgen, "officeName", value.officeName());
    writeNonEmpty(jgen, "serviceAddress", value.serviceAddress());
    writeNonEmpty(jgen, "phones", value.phoneNumbers());
    writeNonEmpty(jgen, "emailContacts", value.emailContacts());
    writeNonEmpty(jgen, "serviceHours", value.serviceHours());
    writeNonEmpty(jgen, "additionalHoursInfo", value.additionalHoursInfo());
    writeNonEmpty(jgen, "onlineSchedulingAvailable", value.onlineSchedulingAvailable());
    writeNonEmpty(jgen, "referralRequired", value.referralRequired());
    writeNonEmpty(jgen, "walkInsAccepted", value.walkInsAccepted());
    jgen.writeEndObject();
  }
}
