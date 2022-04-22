package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.DatamartFacilitiesJacksonConfig.createMapper;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.Facility.ActiveStatus;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatus;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatusCode;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;

@Builder
@Value
public class FacilityOverlayV0 implements Function<HasFacilityPayload, Facility> {
  private static final ObjectMapper DATAMART_MAPPER = createMapper();

  private static OperatingStatus determineOperatingStatusFromActiveStatus(
      ActiveStatus activeStatus) {
    if (activeStatus == ActiveStatus.T) {
      return OperatingStatus.builder().code(OperatingStatusCode.CLOSED).build();
    }
    return OperatingStatus.builder().code(OperatingStatusCode.NORMAL).build();
  }

  @Override
  @SneakyThrows
  public Facility apply(HasFacilityPayload entity) {
    Facility facility =
        FacilityTransformerV0.toFacility(
            filterOutInvalidDetailedServices(
                DATAMART_MAPPER.readValue(entity.facility(), DatamartFacility.class)));
    if (facility.attributes().operatingStatus() == null) {
      facility
          .attributes()
          .operatingStatus(
              determineOperatingStatusFromActiveStatus(facility.attributes().activeStatus()));
    }
    return facility;
  }

  /**
   * Detailed services represented in pre-serviceInfo block format that have unrecognized service
   * names will have null serviceInfo block when deserialized.
   */
  private DatamartFacility filterOutInvalidDetailedServices(
      @NonNull DatamartFacility datamartFacility) {
    if (isNotEmpty(datamartFacility.attributes().detailedServices())) {
      datamartFacility
          .attributes()
          .detailedServices(
              datamartFacility.attributes().detailedServices().parallelStream()
                  .filter(dds -> dds.serviceInfo() != null)
                  .collect(Collectors.toList()));
    }
    return datamartFacility;
  }
}
