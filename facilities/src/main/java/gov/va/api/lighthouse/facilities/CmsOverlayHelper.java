package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.DatamartCmsOverlay.HealthCareSystem;
import static gov.va.api.lighthouse.facilities.DatamartFacilitiesJacksonConfig.createMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.DatamartCmsOverlay.Core;
import gov.va.api.lighthouse.facilities.DatamartFacility.OperatingStatus;
import java.util.List;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

/** Helper class for CMS overlay operating status and detailed services. */
@UtilityClass
public final class CmsOverlayHelper {
  private static final ObjectMapper DATAMART_MAPPER = createMapper();

  /** Obtain DatamarOverlay core from JSON string. */
  @SneakyThrows
  public static Core getCore(String core) {
    return (core == null) ? null : DATAMART_MAPPER.readValue(core, Core.class);
  }

  /** Obtain list of detailed services from JSON string. */
  @SneakyThrows
  public static List<DatamartDetailedService> getDetailedServices(String detailedServices) {
    return (detailedServices == null)
        ? List.of()
        : List.of(DATAMART_MAPPER.readValue(detailedServices, DatamartDetailedService[].class))
            .parallelStream()
            .filter(ds -> ds.serviceInfo() != null)
            .collect(Collectors.toList());
  }

  /** Obtain DatamartFacility health care system from JSON string. */
  @SneakyThrows
  public static HealthCareSystem getHealthCareSystem(String healthCareSystem) {
    return (healthCareSystem == null)
        ? null
        : DATAMART_MAPPER.readValue(healthCareSystem, HealthCareSystem.class);
  }

  /** Obtain DatamartFacility operating status from JSON string. */
  @SneakyThrows
  public static OperatingStatus getOperatingStatus(String operatingStatus) {
    return (operatingStatus == null)
        ? null
        : DATAMART_MAPPER.readValue(operatingStatus, OperatingStatus.class);
  }

  /** Obtain JSON string representation of DatamartOverlay core. */
  @SneakyThrows
  public static String serializeCore(Core core) {
    return (core == null) ? null : DATAMART_MAPPER.writeValueAsString(core);
  }

  /** Obtain JSON string representation of detailed service list. */
  @SneakyThrows
  public static String serializeDetailedServices(List<DatamartDetailedService> detailedServices) {
    return (detailedServices == null || detailedServices.isEmpty())
        ? null
        : DATAMART_MAPPER.writeValueAsString(detailedServices);
  }

  /** Obtain JSON string representation of DatamartFacility health care system. */
  @SneakyThrows
  public static String serializeHealthCareSystem(HealthCareSystem healthCareSystem) {
    return (healthCareSystem == null) ? null : DATAMART_MAPPER.writeValueAsString(healthCareSystem);
  }

  /** Obtain JSON string representation of DatamartFacility operating status. */
  @SneakyThrows
  public static String serializeOperatingStatus(OperatingStatus operatingStatus) {
    return (operatingStatus == null) ? null : DATAMART_MAPPER.writeValueAsString(operatingStatus);
  }
}
