package gov.va.api.lighthouse.facilities.api.v1;

import static gov.va.api.lighthouse.facilities.api.TestUtils.getExpectedJson;
import static gov.va.api.lighthouse.facilities.api.v1.DetailedServiceUtils.getDetailedService;
import static gov.va.api.lighthouse.facilities.api.v1.SerializerUtil.createMapper;
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
        getExpectedJson("v1/CmsOverlayResponse/responseWithNullFields.json");
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
  void isEmpty() {
    // Empty
    assertThat(CmsOverlayResponse.builder().build().isEmpty()).isTrue();
    assertThat(CmsOverlayResponse.builder().overlay(CmsOverlay.builder().build()).build().isEmpty())
        .isTrue();
    // Not empty
    assertThat(
            CmsOverlayResponse.builder()
                .overlay(
                    CmsOverlay.builder()
                        .detailedServices(
                            List.of(
                                DetailedService.builder()
                                    .serviceInfo(
                                        DetailedService.ServiceInfo.builder()
                                            .serviceId(
                                                Facility.HealthService.Cardiology.serviceId())
                                            .name(Facility.HealthService.Cardiology.name())
                                            .serviceType(
                                                Facility.HealthService.Cardiology.serviceType())
                                            .build())
                                    .build()))
                        .build())
                .build()
                .isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void overlayResponseOverlayWithEmptyOpStatus() {
    String jsonOverlayWithEmptyOperatingStatus =
        getExpectedJson(
            "v1/CmsOverlayResponse/responseWithOverlayDetailedServicesNullOpStatus.json");
    // Null operating status
    CmsOverlayResponse responseWithOverlayDetailedServicesEmptyOperatingStatus =
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
                .writeValueAsString(responseWithOverlayDetailedServicesEmptyOperatingStatus))
        .isEqualToIgnoringWhitespace(jsonOverlayWithEmptyOperatingStatus);
  }

  @Test
  @SneakyThrows
  void overlayResponseWithEmptyOverlay() {
    String jsonResponseWithEmptyOverlay =
        getExpectedJson("v1/CmsOverlayResponse/responseWithEmptyOverlay.json");
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
