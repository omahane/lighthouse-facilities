package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.Facility.Hours;
import lombok.SneakyThrows;

public class HoursSerializer extends NonEmptySerializer<Hours> {

  public HoursSerializer() {
    this(null);
  }

  public HoursSerializer(Class<Hours> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(Hours value, JsonGenerator jgen, SerializerProvider provider) {
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
