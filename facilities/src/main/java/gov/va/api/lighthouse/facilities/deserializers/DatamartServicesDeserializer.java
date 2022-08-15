package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.lighthouse.facilities.DatamartFacilitiesJacksonConfig.createMapper;
import static java.util.Collections.emptyList;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService;
import gov.va.api.lighthouse.facilities.DatamartFacility.HealthService;
import gov.va.api.lighthouse.facilities.DatamartFacility.OtherService;
import gov.va.api.lighthouse.facilities.DatamartFacility.Service;
import gov.va.api.lighthouse.facilities.DatamartFacility.Services;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.SneakyThrows;

public class DatamartServicesDeserializer extends StdDeserializer<Services> {
  private static final ObjectMapper MAPPER = createMapper();

  public DatamartServicesDeserializer() {
    this(null);
  }

  public DatamartServicesDeserializer(Class<?> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public Services deserialize(JsonParser jp, DeserializationContext deserializationContext) {
    JsonNode node = jp.getCodec().readTree(jp);
    JsonNode benefitsNode = node.get("benefits");
    JsonNode healthNode = node.get("health");
    JsonNode otherNode = node.get("other");
    JsonNode lastUpdatedNode = node.get("last_updated");
    return Services.builder()
        .benefits(isBlank(benefitsNode) ? null : getBenefitsServices(benefitsNode))
        .health(isBlank(healthNode) ? null : getHealthServices(healthNode))
        .other(isBlank(otherNode) ? null : getOtherServices(otherNode))
        .lastUpdated(
            isBlank(lastUpdatedNode) ? null : MAPPER.convertValue(lastUpdatedNode, LocalDate.class))
        .build();
  }

  /** Obtain list of facility benefits services from JSON node. */
  private List<Service<BenefitsService>> getBenefitsServices(@NonNull JsonNode benefitsNode) {
    List<Service<BenefitsService>> benefitsServices = emptyList();
    if (isNonEmptyArray(benefitsNode)) {
      if (isServiceListInObjectFormat(benefitsNode)) {
        // Interpret list as list of benefits service object values.
        TypeReference<List<Service<BenefitsService>>> serviceObjList = new TypeReference<>() {};
        benefitsServices = MAPPER.convertValue(benefitsNode, serviceObjList);
      } else {
        // Interpret list as list of benefits services enum values. Convert to list of benefits
        // service object values.
        TypeReference<List<BenefitsService>> serviceEnumList = new TypeReference<>() {};
        benefitsServices =
            MAPPER.convertValue(benefitsNode, serviceEnumList).stream()
                .map(
                    bs ->
                        Service.<BenefitsService>builder().serviceType(bs).name(bs.name()).build())
                .collect(Collectors.toList());
      }
    }
    return benefitsServices;
  }

  /** Obtain list of facility health services from JSON node. */
  private List<Service<HealthService>> getHealthServices(@NonNull JsonNode healthNode) {
    List<Service<HealthService>> healthServices = emptyList();
    if (isNonEmptyArray(healthNode)) {
      if (isServiceListInObjectFormat(healthNode)) {
        // Interpret list as list of health service object values.
        TypeReference<List<Service<HealthService>>> serviceObjList = new TypeReference<>() {};
        healthServices = MAPPER.convertValue(healthNode, serviceObjList);
      } else {
        // Interpret list as list of health services enum values. Convert to list of health service
        // object values.
        TypeReference<List<HealthService>> serviceEnumList = new TypeReference<>() {};
        healthServices =
            MAPPER.convertValue(healthNode, serviceEnumList).stream()
                .map(hs -> Service.<HealthService>builder().serviceType(hs).name(hs.name()).build())
                .collect(Collectors.toList());
      }
    }
    return healthServices;
  }

  /** Obtain list of facility other services from JSON node. */
  private List<Service<OtherService>> getOtherServices(@NonNull JsonNode otherNode) {
    List<Service<OtherService>> otherServices = emptyList();
    if (isNonEmptyArray(otherNode)) {
      if (isServiceListInObjectFormat(otherNode)) {
        // Interpret list as list of other service object values.
        TypeReference<List<Service<OtherService>>> serviceObjList = new TypeReference<>() {};
        otherServices = MAPPER.convertValue(otherNode, serviceObjList);
      } else {
        // Interpret list as list of other services enum values. Convert to list of other service
        // object values.
        TypeReference<List<OtherService>> serviceEnumList = new TypeReference<>() {};
        otherServices =
            MAPPER.convertValue(otherNode, serviceEnumList).stream()
                .map(os -> Service.<OtherService>builder().serviceType(os).name(os.name()).build())
                .collect(Collectors.toList());
      }
    }
    return otherServices;
  }

  private boolean isBlank(JsonNode node) {
    return node == null || node.isNull();
  }

  private boolean isNonEmptyArray(@NonNull JsonNode node) {
    return !node.isEmpty() && node.isArray() && !node.toString().equals("[]");
  }

  private boolean isServiceListInObjectFormat(@NonNull JsonNode serviceNode) {
    for (final Iterator<JsonNode> nodeIter = serviceNode.iterator(); nodeIter.hasNext(); ) {
      JsonNode node = nodeIter.next();
      if (node.hasNonNull("serviceId")) {
        return true;
      }
    }
    return false;
  }
}
