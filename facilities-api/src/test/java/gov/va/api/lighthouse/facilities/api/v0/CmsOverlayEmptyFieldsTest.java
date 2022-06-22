package gov.va.api.lighthouse.facilities.api.v0;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.TestUtils.getExpectedJson;
import static gov.va.api.lighthouse.facilities.api.v0.DetailedServiceUtils.getDetailedService;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class CmsOverlayEmptyFieldsTest {

  @Test
  @SneakyThrows
  void allFieldsEmpty() {
    // Null out the fields
    String jsonEmptyOverlay = getExpectedJson("v0/CmsOverlay/overlayWithNullFields.json");
    CmsOverlay emptyOverlay =
        CmsOverlay.builder().operatingStatus(null).detailedServices(null).build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyOverlay))
        .isEqualTo(jsonEmptyOverlay);
    // Empty detailed service list
    jsonEmptyOverlay =
        getExpectedJson("v0/CmsOverlay/overlayWithNullOpStatusEmptyDetailedServices.json");
    emptyOverlay = CmsOverlay.builder().operatingStatus(null).detailedServices(emptyList()).build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyOverlay))
        .isEqualTo(jsonEmptyOverlay);
  }

  @Test
  @SneakyThrows
  void emptyDetailedServices() {
    // Null detailed services
    String jsonOverlayWithEmptyDetailedServices =
        getExpectedJson("v0/CmsOverlay/overlayWithOpStatusNullDetailedServices.json");
    CmsOverlay overlayWithEmptyServices =
        CmsOverlay.builder().operatingStatus(getOperatingStatus()).detailedServices(null).build();
    assertThat(
            createMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(overlayWithEmptyServices))
        .isEqualTo(jsonOverlayWithEmptyDetailedServices);
    // Empty detailed services
    jsonOverlayWithEmptyDetailedServices =
        getExpectedJson("v0/CmsOverlay/overlayWithOpStatusEmptyDetailedServices.json");
    overlayWithEmptyServices =
        CmsOverlay.builder()
            .operatingStatus(getOperatingStatus())
            .detailedServices(emptyList())
            .build();
    assertThat(
            createMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(overlayWithEmptyServices))
        .isEqualTo(jsonOverlayWithEmptyDetailedServices);
  }

  @Test
  @SneakyThrows
  void emptyOperatingStatus() {
    String jsonOverlayWithEmptyOperatingStatus =
        getExpectedJson("v0/CmsOverlay/overlayWithDetailedServicesNullOpStatus.json");
    // Null operating status
    CmsOverlay overlayWithEmptyOperatingStatus =
        CmsOverlay.builder().operatingStatus(null).detailedServices(getDetailedServices()).build();
    assertThat(
            createMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(overlayWithEmptyOperatingStatus))
        .isEqualToIgnoringWhitespace(jsonOverlayWithEmptyOperatingStatus);
  }

  private List<DetailedService> getDetailedServices() {
    return List.of(
        getDetailedService(Facility.HealthService.Cardiology),
        getDetailedService(Facility.HealthService.CaregiverSupport),
        getDetailedService(Facility.HealthService.EmergencyCare));
  }

  private Facility.OperatingStatus getOperatingStatus() {
    return Facility.OperatingStatus.builder()
        .code(Facility.OperatingStatusCode.NORMAL)
        .additionalInfo("test additional info")
        .build();
  }
}
