package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getFridayHours;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getMondayHours;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getSaturdayHours;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getSundayHours;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getThursdayHours;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getTuesdayHours;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getWednesdayHours;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import gov.va.api.lighthouse.facilities.DatamartFacility.Hours;
import lombok.SneakyThrows;

public class DatamartHoursDeserializer extends StdDeserializer<Hours> {
  public DatamartHoursDeserializer() {
    this(null);
  }

  public DatamartHoursDeserializer(Class<Hours> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public Hours deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
    ObjectCodec oc = jsonParser.getCodec();
    JsonNode node = oc.readTree(jsonParser);

    // Read values using snake_case or camelCase representations
    JsonNode mondayHoursNode = getMondayHours(node);
    JsonNode tuesdayHoursNode = getTuesdayHours(node);
    JsonNode wednesdayHoursNode = getWednesdayHours(node);
    JsonNode thursdayHoursNode = getThursdayHours(node);
    JsonNode fridayHoursNode = getFridayHours(node);
    JsonNode saturdayHoursNode = getSaturdayHours(node);
    JsonNode sundayHoursNode = getSundayHours(node);

    return Hours.builder()
        .monday(
            mondayHoursNode != null
                ? createMapper().convertValue(mondayHoursNode, String.class)
                : null)
        .tuesday(
            tuesdayHoursNode != null
                ? createMapper().convertValue(tuesdayHoursNode, String.class)
                : null)
        .wednesday(
            wednesdayHoursNode != null
                ? createMapper().convertValue(wednesdayHoursNode, String.class)
                : null)
        .thursday(
            thursdayHoursNode != null
                ? createMapper().convertValue(thursdayHoursNode, String.class)
                : null)
        .friday(
            fridayHoursNode != null
                ? createMapper().convertValue(fridayHoursNode, String.class)
                : null)
        .saturday(
            saturdayHoursNode != null
                ? createMapper().convertValue(saturdayHoursNode, String.class)
                : null)
        .sunday(
            sundayHoursNode != null
                ? createMapper().convertValue(sundayHoursNode, String.class)
                : null)
        .build();
  }
}
