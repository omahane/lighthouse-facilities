package gov.va.api.lighthouse.facilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.DatamartFacility.OperatingStatus;
import gov.va.api.lighthouse.facilities.api.cms.DetailedService;
import java.util.List;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

/** Helper class for CMS overlay operating status and detailed services. */
@UtilityClass
public final class CmsOverlayHelper {

  private static final ObjectMapper DATAMART_MAPPER =
      DatamartFacilitiesJacksonConfig.createMapper();

  /** Obtain list of detailed services from JSON string. */
  @SneakyThrows
  public static List<DetailedService> getDetailedServices(String detailedServices) {
    return (detailedServices == null)
        ? List.of()
        : List.of(DATAMART_MAPPER.readValue(detailedServices, DetailedService[].class));
  }

  /** Obtain DatamartFacility operating status from JSON string. */
  @SneakyThrows
  public static OperatingStatus getOperatingStatus(String operatingStatus) {
    return (operatingStatus == null)
        ? null
        : DATAMART_MAPPER.readValue(operatingStatus, OperatingStatus.class);
  }

  /** Obtain JSON string representation of detailed service list. */
  @SneakyThrows
  public static String serializeDetailedServices(List<DetailedService> detailedServices) {
    return (detailedServices == null || detailedServices.isEmpty())
        ? null
        : DATAMART_MAPPER.writeValueAsString(detailedServices);
  }

  /** Obtain JSON string representation of DatamartFacility operating status. */
  @SneakyThrows
  public static String serializeOperatingStatus(OperatingStatus operatingStatus) {
    return (operatingStatus == null) ? null : DATAMART_MAPPER.writeValueAsString(operatingStatus);
  }
}
