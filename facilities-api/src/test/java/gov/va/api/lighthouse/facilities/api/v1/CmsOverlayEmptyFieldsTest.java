package gov.va.api.lighthouse.facilities.api.v1;

import static gov.va.api.lighthouse.facilities.api.TestUtils.getExpectedJson;
import static gov.va.api.lighthouse.facilities.api.v1.DetailedServiceUtils.getDetailedService;
import static gov.va.api.lighthouse.facilities.api.v1.SerializerUtil.createMapper;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class CmsOverlayEmptyFieldsTest {
  @Test
  @SneakyThrows
  void allFieldsEmpty() {
    String jsonEmptyOverlay = getExpectedJson("v1/CmsOverlay/overlayWithNullFields.json");
    // Null out the fields
    CmsOverlay emptyOverlay =
        CmsOverlay.builder().operatingStatus(null).detailedServices(null).build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyOverlay))
        .isEqualTo(jsonEmptyOverlay);
    // Empty detailed service list
    emptyOverlay = CmsOverlay.builder().operatingStatus(null).detailedServices(emptyList()).build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyOverlay))
        .isEqualTo(jsonEmptyOverlay);
  }

  @Test
  @SneakyThrows
  void emptyDetailedServices() {
    String jsonOverlayWithEmptyDetailedServices =
        getExpectedJson("v1/CmsOverlay/overlayWithOpStatusNullDetailedServices.json");
    // Null detailed services
    CmsOverlay overlayWithEmptyServices =
        CmsOverlay.builder().operatingStatus(getOperatingStatus()).detailedServices(null).build();
    assertThat(
            createMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(overlayWithEmptyServices))
        .isEqualTo(jsonOverlayWithEmptyDetailedServices);
    // Empty detailed services
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
        getExpectedJson("v1/CmsOverlay/overlayWithDetailedServicesNullOpStatus.json");
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

  @Test
  @SneakyThrows
  void isEmpty() {
    // Empty
    assertThat(CmsOverlay.builder().build().isEmpty()).isTrue();
    assertThat(
            CmsOverlay.builder()
                .operatingStatus(Facility.OperatingStatus.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    assertThat(CmsOverlay.builder().detailedServices(emptyList()).build().isEmpty()).isTrue();
    // Not empty
    assertThat(
            CmsOverlay.builder()
                .operatingStatus(
                    Facility.OperatingStatus.builder()
                        .code(Facility.OperatingStatusCode.NORMAL)
                        .build())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            CmsOverlay.builder()
                .detailedServices(
                    List.of(
                        DetailedService.builder()
                            .serviceInfo(
                                DetailedService.ServiceInfo.builder()
                                    .serviceId(Facility.HealthService.Cardiology.serviceId())
                                    .name(Facility.HealthService.Cardiology.name())
                                    .serviceType(Facility.HealthService.Cardiology.serviceType())
                                    .build())
                            .build()))
                .build()
                .isEmpty())
        .isFalse();
  }
}
