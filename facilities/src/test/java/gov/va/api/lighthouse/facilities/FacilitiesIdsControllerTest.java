package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.va.api.lighthouse.facilities.api.v0.FacilitiesIdsResponse;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class FacilitiesIdsControllerTest {
  @Autowired private FacilityRepository repo;

  private FacilitiesControllerV0 controller() {
    return FacilitiesControllerV0.builder()
        .facilityRepository(repo)
        .baseUrl("http://foo/")
        .basePath("bp")
        .build();
  }

  @Test
  void facilityIdsByType() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(controller().facilityIdsByType("health"))
        .isEqualTo(FacilitiesIdsResponse.builder().data(Arrays.asList("vha_757")).build());
  }

  @Test
  void invalidType() {
    assertThrows(
        ExceptionsUtils.InvalidParameter.class, () -> controller().facilityIdsByType("xxx"));
  }

  @Test
  void nullReturnAll() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(controller().facilityIdsByType(""))
        .isEqualTo(FacilitiesIdsResponse.builder().data(Arrays.asList("vha_757")).build());
  }

  @Test
  void validEmptyReturn() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(controller().facilityIdsByType("benefits"))
        .isEqualTo(FacilitiesIdsResponse.builder().data(Collections.emptyList()).build());
  }
}
