package gov.va.api.lighthouse.facilities.api.pssg.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import gov.va.api.lighthouse.facilities.api.pssg.BandUpdateResponse;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;

public class BandUpdateResponseSerializer extends StdSerializer<BandUpdateResponse> {
  public BandUpdateResponseSerializer() {
    this(null);
  }

  public BandUpdateResponseSerializer(Class<BandUpdateResponse> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(
      BandUpdateResponse bandUpdateResponse, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "bandsCreated", bandUpdateResponse.bandsCreated());
    writeNonEmpty(jgen, "bandsUpdated", bandUpdateResponse.bandsUpdated());
    jgen.writeEndObject();
  }

  @SneakyThrows
  private void writeNonEmpty(JsonGenerator jgen, String fieldName, Object value) {
    if (ObjectUtils.isNotEmpty(value)) {
      jgen.writeObjectField(fieldName, value);
    }
  }
}
