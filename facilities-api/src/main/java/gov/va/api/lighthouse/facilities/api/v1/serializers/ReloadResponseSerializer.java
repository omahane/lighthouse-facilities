package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.ReloadResponse;
import lombok.SneakyThrows;

public class ReloadResponseSerializer extends NonEmptySerializer<ReloadResponse> {

  public ReloadResponseSerializer() {
    this(null);
  }

  public ReloadResponseSerializer(Class<ReloadResponse> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(ReloadResponse value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "facilitiesUpdated", value.facilitiesUpdated());
    writeNonEmpty(jgen, "facilitiesRevived", value.facilitiesRevived());
    writeNonEmpty(jgen, "facilitiesCreated", value.facilitiesCreated());
    writeNonEmpty(jgen, "facilitiesMissing", value.facilitiesMissing());
    writeNonEmpty(jgen, "facilitiesRemoved", value.facilitiesRemoved());
    writeNonEmpty(jgen, "problems", value.problems());
    writeNonEmpty(jgen, "timing", value.timing());
    writeNonEmpty(jgen, "totalFacilities", value.totalFacilities());
    jgen.writeEndObject();
  }
}
