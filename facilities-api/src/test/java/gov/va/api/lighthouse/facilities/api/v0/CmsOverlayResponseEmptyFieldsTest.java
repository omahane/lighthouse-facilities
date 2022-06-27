package gov.va.api.lighthouse.facilities.api.v0;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.TestUtils.getExpectedJson;
import static gov.va.api.lighthouse.facilities.api.v0.DetailedServiceUtils.getDetailedService;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class CmsOverlayResponseEmptyFieldsTest {
  @Test
  @SneakyThrows
  void allFieldsEmpty() {
    // Null out the response fields
    String jsonEmptyOverlayResponse =
        getExpectedJson("v0/CmsOverlayResponse/responseWithNullFields.json");
    CmsOverlayResponse emptyOverlayResponse = CmsOverlayResponse.builder().overlay(null).build();
    assertThat(
            createMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(emptyOverlayResponse))
        .isEqualTo(jsonEmptyOverlayResponse);
  }

  private List<DetailedService> getDetailedServices() {
    return List.of(
        getDetailedService(Facility.HealthService.Cardiology),
        getDetailedService(Facility.HealthService.CaregiverSupport),
        getDetailedService(Facility.HealthService.EmergencyCare));
  }

  @Test
  @SneakyThrows
  void overlayResponseOverlayWithEmptyOpStatus() {
    String jsonOverlayWithEmptyOperatingStatus =
        getExpectedJson(
            "v0/CmsOverlayResponse/responseWithOverlayDetailedServicesNullOpStatus.json");
    // Null operating status
    CmsOverlayResponse responseWithOVerlayDetailedServicesEmptyOperatingStatus =
        CmsOverlayResponse.builder()
            .overlay(
                CmsOverlay.builder()
                    .operatingStatus(null)
                    .detailedServices(getDetailedServices())
                    .build())
            .build();
    assertThat(
            createMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(responseWithOVerlayDetailedServicesEmptyOperatingStatus))
        .isEqualToIgnoringWhitespace(jsonOverlayWithEmptyOperatingStatus);
  }

  @Test
  @SneakyThrows
  void overlayResponseWithEmptyOverlay() {
    String jsonResponseWithEmptyOverlay =
        getExpectedJson("v0/CmsOverlayResponse/responseWithOverlayNullFields.json");
    // Null out the overlay fields
    CmsOverlayResponse responseWithEmptyOverlay =
        CmsOverlayResponse.builder()
            .overlay(CmsOverlay.builder().operatingStatus(null).detailedServices(null).build())
            .build();
    assertThat(
            createMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(responseWithEmptyOverlay))
        .isEqualTo(jsonResponseWithEmptyOverlay);
    // Overlay with empty detailed service list
    jsonResponseWithEmptyOverlay =
        getExpectedJson("v0/CmsOverlayResponse/responseWithOverlayNullOpStatusEmptyOverlay.json");
    responseWithEmptyOverlay =
        CmsOverlayResponse.builder()
            .overlay(
                CmsOverlay.builder().operatingStatus(null).detailedServices(emptyList()).build())
            .build();
    assertThat(
            createMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(responseWithEmptyOverlay))
        .isEqualTo(jsonResponseWithEmptyOverlay);
  }
}
