package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.DetailedServiceHours;
import lombok.SneakyThrows;

public class DetailedServiceHoursSerializer extends NonEmptySerializer<DetailedServiceHours> {

  public DetailedServiceHoursSerializer() {
    this(null);
  }

  public DetailedServiceHoursSerializer(Class<DetailedServiceHours> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(
      DetailedServiceHours value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "monday", value.monday());
    writeNonEmpty(jgen, "tuesday", value.tuesday());
    writeNonEmpty(jgen, "wednesday", value.wednesday());
    writeNonEmpty(jgen, "thursday", value.thursday());
    writeNonEmpty(jgen, "friday", value.friday());
    writeNonEmpty(jgen, "saturday", value.saturday());
    writeNonEmpty(jgen, "sunday", value.sunday());
    jgen.writeEndObject();
  }
}
