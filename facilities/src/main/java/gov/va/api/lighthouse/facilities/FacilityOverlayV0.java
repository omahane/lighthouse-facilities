package gov.va.api.lighthouse.facilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.Facility.ActiveStatus;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatus;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatusCode;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Builder
@Value
@Slf4j
public class FacilityOverlayV0 implements Function<HasFacilityPayload, Facility> {

  private static final ObjectMapper DATAMART_MAPPER =
      DatamartFacilitiesJacksonConfig.createMapper();

  private static void applyCmsOverlayServices(Facility facility, Set<String> overlayServices) {
    if (overlayServices == null) {
      log.warn("CMS Overlay for facility {} is missing CMS Services", facility.id());
    } else {
      boolean needToSort = false;
      for (String overlayService : overlayServices) {
        if ("Covid19Vaccine".equalsIgnoreCase(overlayService)) {
          if (facility.attributes().services().health() != null) {
            facility.attributes().services().health().add(Facility.HealthService.Covid19Vaccine);
          } else {
            facility.attributes().services().health(List.of(Facility.HealthService.Covid19Vaccine));
          }
          needToSort = true;
          break;
        }
      }
      // re-sort the health services list with the newly added field(s)
      if (needToSort && facility.attributes().services().health().size() > 1) {
        Collections.sort(
            facility.attributes().services().health(),
            (left, right) -> left.name().compareToIgnoreCase(right.name()));
      }
    }
  }

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
            DATAMART_MAPPER.readValue(entity.facility(), DatamartFacility.class));

    if (facility.attributes().operatingStatus() == null) {
      facility
          .attributes()
          .operatingStatus(
              determineOperatingStatusFromActiveStatus(facility.attributes().activeStatus()));
    }

    if (entity.overlayServices() != null) {
      applyCmsOverlayServices(facility, entity.overlayServices());
    }

    return facility;
  }
}
