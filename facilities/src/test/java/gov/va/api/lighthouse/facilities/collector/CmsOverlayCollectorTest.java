package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.CMS_OVERLAY_SERVICE_NAME_COVID_19;
import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.lighthouse.facilities.CmsOverlayEntity;
import gov.va.api.lighthouse.facilities.CmsOverlayRepository;
import gov.va.api.lighthouse.facilities.DatamartCmsOverlay;
import gov.va.api.lighthouse.facilities.DatamartDetailedService;
import gov.va.api.lighthouse.facilities.DatamartFacilitiesJacksonConfig;
import gov.va.api.lighthouse.facilities.DatamartFacility;
import gov.va.api.lighthouse.facilities.FacilityEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class CmsOverlayCollectorTest {
  @Mock CmsOverlayRepository mockCmsOverlayRepository;

  @Test
  public void exceptions() {
    CmsOverlayEntity mockEntity = mock(CmsOverlayEntity.class);
    when(mockEntity.id()).thenReturn(FacilityEntity.Pk.fromIdString("vha_123"));
    when(mockEntity.cmsServices()).thenThrow(new NullPointerException("oh noes"));
    when(mockCmsOverlayRepository.findAll()).thenReturn(List.of(mockEntity));
    CmsOverlayCollector collector = new CmsOverlayCollector(mockCmsOverlayRepository);
    assertThat(collector.loadAndUpdateCmsOverlays()).isEqualTo(Collections.emptyMap());
  }

  @Test
  @SneakyThrows
  void loadCovidOverlay() {
    DatamartDetailedService covidService =
        DatamartDetailedService.builder()
            .name(CovidServiceUpdater.CMS_OVERLAY_SERVICE_NAME_COVID_19)
            .serviceId(uncapitalize(DatamartFacility.HealthService.Covid19Vaccine.name()))
            .active(true)
            .path("replace_this_path")
            .build();
    DatamartCmsOverlay overlay =
        DatamartCmsOverlay.builder()
            .operatingStatus(
                DatamartFacility.OperatingStatus.builder()
                    .code(DatamartFacility.OperatingStatusCode.NORMAL)
                    .build())
            .detailedServices(List.of(covidService))
            .build();
    var pk = FacilityEntity.Pk.fromIdString("vha_558GA");
    CmsOverlayEntity overlayEntity =
        CmsOverlayEntity.builder()
            .id(pk)
            .cmsOperatingStatus(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.operatingStatus()))
            .cmsServices(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.detailedServices()))
            .build();
    List<CmsOverlayEntity> mockOverlays = new ArrayList<CmsOverlayEntity>();
    IntStream.range(1, 5000)
        .forEachOrdered(
            n -> {
              CmsOverlayEntity entity =
                  CmsOverlayEntity.builder()
                      .id(FacilityEntity.Pk.fromIdString("vha_" + Integer.toString(n)))
                      .cmsOperatingStatus(overlayEntity.cmsOperatingStatus())
                      .cmsServices(overlayEntity.cmsServices())
                      .build();
              mockOverlays.add(entity);
            });
    mockOverlays.add(overlayEntity);
    InsecureRestTemplateProvider mockInsecureRestTemplateProvider =
        mock(InsecureRestTemplateProvider.class);
    JdbcTemplate mockTemplate = mock(JdbcTemplate.class);
    when(mockCmsOverlayRepository.findAll()).thenReturn(mockOverlays); // List.of(overlayEntity));
    CmsOverlayCollector cmsOverlayCollector = new CmsOverlayCollector(mockCmsOverlayRepository);
    HashMap<String, DatamartCmsOverlay> cmsOverlays =
        cmsOverlayCollector.loadAndUpdateCmsOverlays();
    // Verify loaded CMS overlay
    assertThat(cmsOverlays.isEmpty()).isFalse();
    DatamartDetailedService updatedCovidService =
        DatamartDetailedService.builder()
            .name(CMS_OVERLAY_SERVICE_NAME_COVID_19)
            .serviceId(uncapitalize(DatamartFacility.HealthService.Covid19Vaccine.name()))
            // .path("https://www.va.gov/durham-health-care/programs/covid-19-vaccines/")
            .path("replace_this_path")
            .build();
    DatamartCmsOverlay updatedOverlay =
        DatamartCmsOverlay.builder()
            .operatingStatus(overlay.operatingStatus())
            .detailedServices(List.of(updatedCovidService))
            .build();
    assertThat(cmsOverlays.get(pk.toIdString()))
        .usingRecursiveComparison()
        .isEqualTo(updatedOverlay);
  }

  @Test
  @SneakyThrows
  public void overlayWithNoDetailedServices() {
    var id = "vha_123GA";
    CmsOverlayEntity mockEntity = mock(CmsOverlayEntity.class);
    when(mockEntity.id()).thenReturn(FacilityEntity.Pk.fromIdString(id));
    when(mockEntity.cmsOperatingStatus())
        .thenReturn(
            DatamartFacilitiesJacksonConfig.createMapper()
                .writeValueAsString(
                    DatamartFacility.OperatingStatus.builder()
                        .code(DatamartFacility.OperatingStatusCode.NORMAL)
                        .build()));
    when(mockEntity.cmsServices()).thenReturn(null);
    when(mockCmsOverlayRepository.findAll()).thenReturn(List.of(mockEntity));
    HashMap<String, DatamartCmsOverlay> expectedOverlays = new HashMap<>();
    expectedOverlays.put(
        id,
        DatamartCmsOverlay.builder()
            .operatingStatus(
                DatamartFacility.OperatingStatus.builder()
                    .code(DatamartFacility.OperatingStatusCode.NORMAL)
                    .build())
            .build());
    CmsOverlayCollector collector = new CmsOverlayCollector(mockCmsOverlayRepository);
    assertThat(collector.loadAndUpdateCmsOverlays())
        .usingRecursiveComparison()
        .isEqualTo(expectedOverlays);
  }

  @Test
  @SneakyThrows
  void verifyContainsCovidService() {
    assertThat(
            new CmsOverlayCollector(mockCmsOverlayRepository)
                .containsCovidService(
                    List.of(
                        DatamartDetailedService.builder()
                            .name(CMS_OVERLAY_SERVICE_NAME_COVID_19)
                            .serviceId(
                                uncapitalize(DatamartFacility.HealthService.Covid19Vaccine.name()))
                            .build(),
                        DatamartDetailedService.builder()
                            .name(DatamartFacility.HealthService.Cardiology.name())
                            .serviceId(
                                uncapitalize(DatamartFacility.HealthService.Cardiology.name()))
                            .build(),
                        DatamartDetailedService.builder()
                            .name(DatamartFacility.HealthService.Dermatology.name())
                            .serviceId(
                                uncapitalize(DatamartFacility.HealthService.Dermatology.name()))
                            .build())))
        .isTrue();
  }

  @Test
  void verifyDoesNotContainCovidService() {
    assertThat(
            new CmsOverlayCollector(mockCmsOverlayRepository)
                .containsCovidService(
                    List.of(
                        DatamartDetailedService.builder()
                            .name(DatamartFacility.HealthService.Optometry.name())
                            .serviceId(
                                uncapitalize(DatamartFacility.HealthService.Optometry.name()))
                            .build(),
                        DatamartDetailedService.builder()
                            .name(DatamartFacility.HealthService.Cardiology.name())
                            .serviceId(
                                uncapitalize(DatamartFacility.HealthService.Cardiology.name()))
                            .build(),
                        DatamartDetailedService.builder()
                            .name(DatamartFacility.HealthService.Dermatology.name())
                            .serviceId(
                                uncapitalize(DatamartFacility.HealthService.Dermatology.name()))
                            .build())))
        .isFalse();
    assertThat(new CmsOverlayCollector(mockCmsOverlayRepository).containsCovidService(null))
        .isFalse();
  }
}
