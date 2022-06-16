package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.ReloadResponse.Problem;
import lombok.SneakyThrows;

public class ReloadResponseProblemSerializer extends NonEmptySerializer<Problem> {

  public ReloadResponseProblemSerializer() {
    this(null);
  }

  public ReloadResponseProblemSerializer(Class<Problem> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(Problem value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "facilityId", value.facilityId());
    writeNonEmpty(jgen, "description", value.description());
    writeNonEmpty(jgen, "data", value.data());
    jgen.writeEndObject();
  }
}
