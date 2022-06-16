package gov.va.api.lighthouse.facilities.api.v1.serializers;

import static gov.va.api.lighthouse.facilities.api.v1.serializers.SerializerHelper.idStartsWith;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.Facility.Satisfaction;
import lombok.SneakyThrows;

public class SatisfactionSerializer extends NonEmptySerializer<Satisfaction> {

  public SatisfactionSerializer() {
    this(null);
  }

  public SatisfactionSerializer(Class<Satisfaction> t) {
    super(t);
  }

  private static boolean empty(Satisfaction value) {
    return value.health() == null && value.effectiveDate() == null;
  }

  @Override
  @SneakyThrows
  public void serialize(Satisfaction value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    if (!empty(value) || idStartsWith(jgen, "vha_")) {
      writeNonEmpty(jgen, "health", value.health());
      writeNonEmpty(jgen, "effectiveDate", value.effectiveDate());
    }
    jgen.writeEndObject();
  }
}
