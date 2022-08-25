package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getActiveStatus;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getDetailedServices;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getFacilityType;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getOperationalHoursSpecialInstructions;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getOpertingStatus;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getTimeZone;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getWaitTimes;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import gov.va.api.lighthouse.facilities.DatamartDetailedService;
import gov.va.api.lighthouse.facilities.DatamartFacility.ActiveStatus;
import gov.va.api.lighthouse.facilities.DatamartFacility.Addresses;
import gov.va.api.lighthouse.facilities.DatamartFacility.FacilityAttributes;
import gov.va.api.lighthouse.facilities.DatamartFacility.FacilityType;
import gov.va.api.lighthouse.facilities.DatamartFacility.Hours;
import gov.va.api.lighthouse.facilities.DatamartFacility.OperatingStatus;
import gov.va.api.lighthouse.facilities.DatamartFacility.Phone;
import gov.va.api.lighthouse.facilities.DatamartFacility.Satisfaction;
import gov.va.api.lighthouse.facilities.DatamartFacility.Services;
import gov.va.api.lighthouse.facilities.DatamartFacility.WaitTimes;
import java.math.BigDecimal;
import java.util.List;
import lombok.SneakyThrows;

public class DatamartFacilityAttributesDeserializer
    extends BaseListDeserializer<FacilityAttributes> {
  public DatamartFacilityAttributesDeserializer() {
    this(null);
  }

  public DatamartFacilityAttributesDeserializer(Class<FacilityAttributes> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public FacilityAttributes deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) {
    ObjectCodec oc = jsonParser.getCodec();
    JsonNode node = oc.readTree(jsonParser);

    // Read values using snake_case or camelCase representations
    JsonNode nameNode = node.get("name");
    JsonNode facilityTypeNode = getFacilityType(node);
    JsonNode classificationNode = node.get("classification");
    JsonNode websiteNode = node.get("website");
    JsonNode latitudeNode = node.get("lat");
    JsonNode longitudeNode = node.get("long");
    JsonNode timeZoneNode = getTimeZone(node);
    JsonNode addressNode = node.get("address");
    JsonNode phoneNode = node.get("phone");
    JsonNode hoursNode = node.get("hours");
    JsonNode operationalHoursSpecialInstructionsNode = getOperationalHoursSpecialInstructions(node);
    JsonNode servicesNode = node.get("services");
    JsonNode satisfactionNode = node.get("satisfaction");
    JsonNode waitTimesNode = getWaitTimes(node);
    JsonNode mobileNode = node.get("mobile");
    JsonNode activeStatusNode = getActiveStatus(node);
    JsonNode operatingStatusNode = getOpertingStatus(node);
    JsonNode detailedServicesNode = getDetailedServices(node);
    JsonNode visnNode = node.get("visn");

    TypeReference<List<DatamartDetailedService>> detailedServicesRef = new TypeReference<>() {};

    return FacilityAttributes.builder()
        .name(nameNode != null ? createMapper().convertValue(nameNode, String.class) : null)
        .facilityType(
            facilityTypeNode != null
                ? createMapper().convertValue(facilityTypeNode, FacilityType.class)
                : null)
        .classification(
            classificationNode != null
                ? createMapper().convertValue(classificationNode, String.class)
                : null)
        .website(
            websiteNode != null ? createMapper().convertValue(websiteNode, String.class) : null)
        .latitude(
            latitudeNode != null
                ? createMapper().convertValue(latitudeNode, BigDecimal.class)
                : null)
        .longitude(
            longitudeNode != null
                ? createMapper().convertValue(longitudeNode, BigDecimal.class)
                : null)
        .timeZone(
            timeZoneNode != null ? createMapper().convertValue(timeZoneNode, String.class) : null)
        .address(
            addressNode != null ? createMapper().convertValue(addressNode, Addresses.class) : null)
        .phone(phoneNode != null ? createMapper().convertValue(phoneNode, Phone.class) : null)
        .hours(hoursNode != null ? createMapper().convertValue(hoursNode, Hours.class) : null)
        .operationalHoursSpecialInstructions(
            operationalHoursSpecialInstructionsNode != null
                ? createMapper().convertValue(operationalHoursSpecialInstructionsNode, String.class)
                : null)
        .services(
            servicesNode != null ? createMapper().convertValue(servicesNode, Services.class) : null)
        .satisfaction(
            satisfactionNode != null
                ? createMapper().convertValue(satisfactionNode, Satisfaction.class)
                : null)
        .waitTimes(
            waitTimesNode != null
                ? createMapper().convertValue(waitTimesNode, WaitTimes.class)
                : null)
        .mobile(mobileNode != null ? createMapper().convertValue(mobileNode, Boolean.class) : null)
        .activeStatus(
            activeStatusNode != null
                ? createMapper().convertValue(activeStatusNode, ActiveStatus.class)
                : null)
        .operatingStatus(
            operatingStatusNode != null
                ? createMapper().convertValue(operatingStatusNode, OperatingStatus.class)
                : null)
        .detailedServices(
            detailedServicesNode != null
                ? filterOutInvalidDetailedServices(
                    createMapper().convertValue(detailedServicesNode, detailedServicesRef))
                : null)
        .visn(visnNode != null ? createMapper().convertValue(visnNode, String.class) : null)
        .build();
  }
}
