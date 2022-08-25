package gov.va.api.lighthouse.facilities.api.v0.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getAppointmentLeadin;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getFacilityDescription;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getOnlineSchedulingAvailable;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getPhoneNumbers;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getReferralRequired;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getServiceLocations;
import static gov.va.api.lighthouse.facilities.api.DeserializerUtil.getWalkInsAccepted;
import static gov.va.api.lighthouse.facilities.api.v0.DetailedService.INVALID_SVC_ID;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import gov.va.api.lighthouse.facilities.api.v0.DetailedService;
import gov.va.api.lighthouse.facilities.api.v0.DetailedService.AppointmentPhoneNumber;
import gov.va.api.lighthouse.facilities.api.v0.DetailedService.DetailedServiceLocation;
import gov.va.api.lighthouse.facilities.api.v0.Facility.BenefitsService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OtherService;
import java.util.Arrays;
import java.util.List;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

public class DetailedServiceDeserializer extends StdDeserializer<DetailedService> {
  public DetailedServiceDeserializer() {
    this(null);
  }

  public DetailedServiceDeserializer(Class<DetailedService> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public DetailedService deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) {
    ObjectCodec oc = jsonParser.getCodec();
    JsonNode node = oc.readTree(jsonParser);

    // Read values using snake_case or camelCase representations
    JsonNode nameNode = node.get("name");
    JsonNode serviceIdNode = node.get("serviceId");
    JsonNode activeNode = node.get("active");
    JsonNode changedNode = node.get("changed");
    JsonNode descriptionFacilityNode = getFacilityDescription(node);
    JsonNode appointmentLeadInNode = getAppointmentLeadin(node);
    JsonNode onlineSchedulingAvailableNode = getOnlineSchedulingAvailable(node);
    JsonNode pathNode = node.get("path");
    JsonNode phoneNumbersNode = getPhoneNumbers(node);
    JsonNode referralRequiredNode = getReferralRequired(node);
    JsonNode serviceLocationsNode = getServiceLocations(node);
    JsonNode walkInsAcceptedNode = getWalkInsAccepted(node);

    String serviceName =
        nameNode != null ? createMapper().convertValue(nameNode, String.class) : null;
    String serviceId =
        serviceIdNode != null
                && isRecognizedServiceId(createMapper().convertValue(serviceIdNode, String.class))
            ? createMapper().convertValue(serviceIdNode, String.class)
            : // Attempt to construct service id from service name
            serviceIdNode == null && isRecognizedServiceName(serviceName)
                ? getServiceIdForRecognizedServiceName(serviceName)
                : INVALID_SVC_ID;

    TypeReference<List<AppointmentPhoneNumber>> appointmentNumbersRef = new TypeReference<>() {};
    TypeReference<List<DetailedServiceLocation>> serviceLocationsRef = new TypeReference<>() {};

    return DetailedService.builder()
        .serviceId(serviceId)
        .name(serviceName)
        .active(activeNode != null ? createMapper().convertValue(activeNode, Boolean.class) : false)
        .changed(
            changedNode != null ? createMapper().convertValue(changedNode, String.class) : null)
        .descriptionFacility(
            descriptionFacilityNode != null
                ? createMapper().convertValue(descriptionFacilityNode, String.class)
                : null)
        .appointmentLeadIn(
            appointmentLeadInNode != null
                ? createMapper().convertValue(appointmentLeadInNode, String.class)
                : null)
        .onlineSchedulingAvailable(
            onlineSchedulingAvailableNode != null
                ? createMapper().convertValue(onlineSchedulingAvailableNode, String.class)
                : null)
        .path(pathNode != null ? createMapper().convertValue(pathNode, String.class) : null)
        .phoneNumbers(
            phoneNumbersNode != null
                ? createMapper().convertValue(phoneNumbersNode, appointmentNumbersRef)
                : emptyList())
        .referralRequired(
            referralRequiredNode != null
                ? createMapper().convertValue(referralRequiredNode, String.class)
                : null)
        .serviceLocations(
            serviceLocationsNode != null
                ? createMapper().convertValue(serviceLocationsNode, serviceLocationsRef)
                : emptyList())
        .walkInsAccepted(
            walkInsAcceptedNode != null
                ? createMapper().convertValue(walkInsAcceptedNode, String.class)
                : null)
        .build();
  }

  private String getServiceIdForRecognizedServiceName(String name) {
    return uncapitalize(
        StringUtils.equals(name, "COVID-19 vaccines")
            ? HealthService.Covid19Vaccine.name()
            : Arrays.stream(HealthService.values())
                    .parallel()
                    .anyMatch(hs -> hs.name().equalsIgnoreCase(name))
                ? HealthService.fromString(name).name()
                : Arrays.stream(BenefitsService.values())
                        .parallel()
                        .anyMatch(bs -> bs.name().equalsIgnoreCase(name))
                    ? BenefitsService.valueOf(name).name()
                    : Arrays.stream(OtherService.values())
                            .parallel()
                            .anyMatch(os -> os.name().equalsIgnoreCase(name))
                        ? OtherService.valueOf(name).name()
                        : INVALID_SVC_ID);
  }

  private boolean isRecognizedServiceId(String serviceId) {
    return Arrays.stream(HealthService.values())
            .parallel()
            .anyMatch(hs -> hs.name().equalsIgnoreCase(serviceId))
        || Arrays.stream(BenefitsService.values())
            .parallel()
            .anyMatch(bs -> bs.name().equalsIgnoreCase(serviceId))
        || Arrays.stream(OtherService.values())
            .parallel()
            .anyMatch(os -> os.name().equalsIgnoreCase(serviceId));
  }

  private boolean isRecognizedServiceName(String name) {
    return StringUtils.equals(name, "COVID-19 vaccines") || isRecognizedServiceId(name);
  }
}
