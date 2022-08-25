package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class FacilityRepositoryV1Test {
  @Autowired private FacilityRepository repo;

  @Test
  void bbox_nullParameter() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
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
        .build();
  }

  @Test
  void facilityType_nullParameter() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThatThrownBy(
            () -> FacilityRepository.FacilityTypeSpecification.builder().facilityType(null).build())
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void mobile_nullParameter() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThatThrownBy(() -> FacilityRepository.MobileSpecification.builder().mobile(null).build())
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void state_nullParamater() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThatThrownBy(() -> FacilityRepository.StateSpecification.builder().state(null).build())
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void visn() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(
            controller()
                .jsonFacilities(
                    null, null, null, null, null, null, null, null, null, null, "10", 1, 10)
                .data())
        .isEqualTo(List.of(FacilitySamples.defaultSamples().facilityV1("vha_757")));
  }

  @Test
  void visn_nullParameter() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThatThrownBy(() -> FacilityRepository.VisnSpecification.builder().visn(null).build())
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void zip_nullParameter() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThatThrownBy(() -> FacilityRepository.ZipSpecification.builder().zip(null).build())
        .isInstanceOf(NullPointerException.class);
  }
}
