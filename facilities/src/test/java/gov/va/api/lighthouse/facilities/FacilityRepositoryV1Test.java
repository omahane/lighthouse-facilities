package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildLinkerUrlV1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class FacilityRepositoryV1Test {
  @Autowired private FacilityRepository repo;

  private String baseUrl;

  private String basePath;

  private String linkerUrl;

  @Test
  void bbox_nullParameter() {
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity("vha_757"));
    assertThatThrownBy(
            () ->
                FacilityRepository.BoundingBoxSpecification.builder()
                    .maxLatitude(null)
                    .maxLongitude(null)
                    .minLatitude(null)
                    .minLongitude(null)
                    .build())
        .isInstanceOf(NullPointerException.class);
  }

  private FacilitiesControllerV1 controller() {
    return FacilitiesControllerV1.builder()
        .facilityRepository(repo)
        .baseUrl("http://foo/")
        .basePath("bp")
        .serviceSources(List.of("ATC", "CMS", "DST", "internal", "BISL"))
        .build();
  }

  @BeforeEach
  void setup() {
    baseUrl = "http://foo/";
    basePath = "bp";
    linkerUrl = buildLinkerUrlV1(baseUrl, basePath);
  }

  @Test
  void state_nullParamater() {
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity("vha_757"));
    assertThatThrownBy(() -> FacilityRepository.StateSpecification.builder().state(null).build())
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void visn() {
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity("vha_757"));
    assertThat(
            controller()
                .jsonFacilities(
                    null, null, null, null, null, null, null, null, null, null, "10", 1, 10)
                .data())
        .isEqualTo(List.of(FacilitySamples.defaultSamples(linkerUrl).facilityV1("vha_757")));
  }

  @Test
  void visn_nullParameter() {
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity("vha_757"));
    assertThatThrownBy(() -> FacilityRepository.VisnSpecification.builder().visn(null).build())
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void zip_nullParameter() {
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity("vha_757"));
    assertThatThrownBy(() -> FacilityRepository.ZipSpecification.builder().zip(null).build())
        .isInstanceOf(NullPointerException.class);
  }
}
