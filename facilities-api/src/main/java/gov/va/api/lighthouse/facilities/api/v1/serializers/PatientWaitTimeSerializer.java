package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.PatientWaitTime;
import lombok.SneakyThrows;

public class PatientWaitTimeSerializer extends NonEmptySerializer<PatientWaitTime> {

  public PatientWaitTimeSerializer() {
    this(null);
  }

  public PatientWaitTimeSerializer(Class<PatientWaitTime> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(PatientWaitTime value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "new", value.newPatientWaitTime());
    writeNonEmpty(jgen, "established", value.establishedPatientWaitTime());
    writeNonEmpty(jgen, "effectiveDate", value.effectiveDate());
    jgen.writeEndObject();
  }
}
