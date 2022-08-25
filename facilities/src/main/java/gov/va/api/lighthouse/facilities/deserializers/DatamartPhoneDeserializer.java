package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getAfterHours;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getEnrollmentCoordinator;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getMentalHealthClinic;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getPatientAdvocate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import gov.va.api.lighthouse.facilities.DatamartFacility.Phone;
import lombok.SneakyThrows;

public class DatamartPhoneDeserializer extends StdDeserializer<Phone> {
  public DatamartPhoneDeserializer() {
    this(null);
  }

  public DatamartPhoneDeserializer(Class<Phone> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public Phone deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
    ObjectCodec oc = jsonParser.getCodec();
    JsonNode node = oc.readTree(jsonParser);

    // Read values using snake_case or camelCase representations
    JsonNode faxNode = node.get("fax");
    JsonNode mainNode = node.get("main");
    JsonNode pharmacyNode = node.get("pharmacy");
    JsonNode afterHoursNode = getAfterHours(node);
    JsonNode patientAdvocateNode = getPatientAdvocate(node);
    JsonNode mentalHealthClinicNode = getMentalHealthClinic(node);
    JsonNode enrollmentCoordinator = getEnrollmentCoordinator(node);

    return Phone.builder()
        .fax(faxNode != null ? createMapper().convertValue(faxNode, String.class) : null)
        .main(mainNode != null ? createMapper().convertValue(mainNode, String.class) : null)
        .pharmacy(
            pharmacyNode != null ? createMapper().convertValue(pharmacyNode, String.class) : null)
        .afterHours(
            afterHoursNode != null
                ? createMapper().convertValue(afterHoursNode, String.class)
                : null)
        .patientAdvocate(
            patientAdvocateNode != null
                ? createMapper().convertValue(patientAdvocateNode, String.class)
                : null)
        .mentalHealthClinic(
            mentalHealthClinicNode != null
                ? createMapper().convertValue(mentalHealthClinicNode, String.class)
                : null)
        .enrollmentCoordinator(
            enrollmentCoordinator != null
                ? createMapper().convertValue(enrollmentCoordinator, String.class)
                : null)
        .build();
  }
}
