package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.DatamartFacilitiesJacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.FacilityOverlayHelper.filterOutInvalidDetailedServices;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import gov.va.api.lighthouse.facilities.api.v1.Facility.ActiveStatus;
import gov.va.api.lighthouse.facilities.api.v1.Facility.OperatingStatus;
import gov.va.api.lighthouse.facilities.api.v1.Facility.OperatingStatusCode;
import java.util.function.Function;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;

@Builder
@Value
public class FacilityOverlayV1 implements Function<HasFacilityPayload, Facility> {
  private static final ObjectMapper DATAMART_MAPPER = createMapper();

  private static OperatingStatus determineOperatingStatusFromActiveStatus(
      ActiveStatus activeStatus) {
    return OperatingStatus.builder()
        .code(
            activeStatus == ActiveStatus.T
                ? OperatingStatusCode.CLOSED
                : OperatingStatusCode.NORMAL)
        .build();
  }

  @Override
  @SneakyThrows
  public Facility apply(HasFacilityPayload entity) {
    Facility facility =
        FacilityTransformerV1.toFacility(
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
}
