package gov.va.api.lighthouse.facilities.api.v1.serializers;

import static gov.va.api.lighthouse.facilities.api.v1.serializers.SerializerHelper.idStartsWith;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.Facility.WaitTimes;
import lombok.SneakyThrows;

public class WaitTimesSerializer extends NonEmptySerializer<WaitTimes> {

  public WaitTimesSerializer() {
    this(null);
  }

  public WaitTimesSerializer(Class<WaitTimes> t) {
    super(t);
  }

  private static boolean empty(WaitTimes value) {
    return value.health() == null && value.effectiveDate() == null;
  }

  @Override
  @SneakyThrows
  public void serialize(WaitTimes value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    if (!empty(value) || idStartsWith(jgen, "vha_")) {
      writeNonEmpty(jgen, "health", value.health());
      writeNonEmpty(jgen, "effectiveDate", value.effectiveDate());
    }
    jgen.writeEndObject();
  }
}
