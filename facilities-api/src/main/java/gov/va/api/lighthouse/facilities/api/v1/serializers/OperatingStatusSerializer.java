package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.Facility.OperatingStatus;
import lombok.SneakyThrows;

public class OperatingStatusSerializer extends NonEmptySerializer<OperatingStatus> {

  public OperatingStatusSerializer() {
    this(null);
  }

  public OperatingStatusSerializer(Class<OperatingStatus> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(OperatingStatus value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "code", value.code());
    writeNonEmpty(jgen, "additionalInfo", value.additionalInfo());
    jgen.writeEndObject();
  }
}
