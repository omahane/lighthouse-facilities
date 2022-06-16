package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.ReloadResponse.Timing;
import lombok.SneakyThrows;

public class ReloadResponseTimingSerializer extends NonEmptySerializer<Timing> {

  public ReloadResponseTimingSerializer() {
    this(null);
  }

  public ReloadResponseTimingSerializer(Class<Timing> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(Timing value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "start", value.start());
    writeNonEmpty(jgen, "completeCollection", value.completeCollection());
    writeNonEmpty(jgen, "complete", value.complete());
    writeNonEmpty(jgen, "totalDuration", value.totalDuration());
    jgen.writeEndObject();
  }
}
