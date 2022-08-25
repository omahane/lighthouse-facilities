package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getPrimaryCareRoutine;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getPrimaryCareUrgent;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getSpecialtyCareRoutine;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getSpecialtyCareUrgent;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import gov.va.api.lighthouse.facilities.DatamartFacility.PatientSatisfaction;
import java.math.BigDecimal;
import lombok.SneakyThrows;

public class DatamartPatientSatisfactionDeserializer extends StdDeserializer<PatientSatisfaction> {
  public DatamartPatientSatisfactionDeserializer() {
    this(null);
  }

  public DatamartPatientSatisfactionDeserializer(Class<PatientSatisfaction> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public PatientSatisfaction deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) {
    ObjectCodec oc = jsonParser.getCodec();
    JsonNode node = oc.readTree(jsonParser);

    // Read values using snake_case or camelCase representations
    JsonNode primaryCareUrgentNode = getPrimaryCareUrgent(node);
    JsonNode specialtyCareUrgentNode = getSpecialtyCareUrgent(node);
    JsonNode primaryCareRoutineNode = getPrimaryCareRoutine(node);
    JsonNode specialtyCareRoutineNode = getSpecialtyCareRoutine(node);

    return PatientSatisfaction.builder()
        .primaryCareUrgent(
            primaryCareUrgentNode != null
                ? createMapper().convertValue(primaryCareUrgentNode, BigDecimal.class)
                : null)
        .specialtyCareUrgent(
            specialtyCareUrgentNode != null
                ? createMapper().convertValue(specialtyCareUrgentNode, BigDecimal.class)
                : null)
        .primaryCareRoutine(
            primaryCareRoutineNode != null
                ? createMapper().convertValue(primaryCareRoutineNode, BigDecimal.class)
                : null)
        .specialtyCareRoutine(
            specialtyCareRoutineNode != null
                ? createMapper().convertValue(specialtyCareRoutineNode, BigDecimal.class)
                : null)
        .build();
  }
}
