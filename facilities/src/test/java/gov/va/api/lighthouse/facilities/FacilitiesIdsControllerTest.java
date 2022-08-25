package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildLinkerUrlV0;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.va.api.lighthouse.facilities.api.v0.FacilitiesIdsResponse;
import java.util.Arrays;
import java.util.Collections;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class FacilitiesIdsControllerTest {
  @Autowired private FacilityRepository repo;

  private String baseUrl;

  private String basePath;

  private String linkerUrl;

  private FacilitiesControllerV0 controller(@NonNull String baseUrl, @NonNull String basePath) {
    return FacilitiesControllerV0.builder()
        .facilityRepository(repo)
        .baseUrl(baseUrl)
        .basePath(basePath)
        .build();
  }

  @Test
  void facilityIdsByType() {
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity("vha_757"));
    assertThat(controller(baseUrl, basePath).facilityIdsByType("health"))
        .isEqualTo(FacilitiesIdsResponse.builder().data(Arrays.asList("vha_757")).build());
  }

  @Test
  void invalidType() {
    assertThrows(
        ExceptionsUtils.InvalidParameter.class,
        () -> controller(baseUrl, basePath).facilityIdsByType("xxx"));
  }

  @Test
  void nullReturnAll() {
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity("vha_757"));
    assertThat(controller(baseUrl, basePath).facilityIdsByType(""))
        .isEqualTo(FacilitiesIdsResponse.builder().data(Arrays.asList("vha_757")).build());
  }

  @BeforeEach
  void setup() {
    baseUrl = "http://foo/";
    basePath = "bp";
    linkerUrl = buildLinkerUrlV0(baseUrl, basePath);
  }

  @Test
  void validEmptyReturn() {
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity("vha_757"));
    assertThat(controller(baseUrl, basePath).facilityIdsByType("benefits"))
        .isEqualTo(FacilitiesIdsResponse.builder().data(Collections.emptyList()).build());
  }
}
