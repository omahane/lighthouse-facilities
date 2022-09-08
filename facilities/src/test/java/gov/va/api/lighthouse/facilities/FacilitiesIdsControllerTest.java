package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildLinkerUrlV0;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.lighthouse.facilities.ServiceNameAggregatorV0.ServiceNameAggregate;
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

  private ServiceNameAggregatorV0 mockServiceNameAggregator;

  private ServiceNameAggregate mockServiceNameAggregate;

  private FacilitiesControllerV0 controller(
      @NonNull String baseUrl,
      @NonNull String basePath,
      @NonNull ServiceNameAggregatorV0 serviceNameAggregator) {
    return FacilitiesControllerV0.builder()
        .facilityRepository(repo)
        .baseUrl(baseUrl)
        .basePath(basePath)
        .serviceNameAggregator(serviceNameAggregator)
        .build();
  }

  @Test
  void facilityIdsByType() {
    repo.save(
        FacilitySamples.defaultSamples(linkerUrl, mockServiceNameAggregator)
            .facilityEntity("vha_757"));
    assertThat(controller(baseUrl, basePath, mockServiceNameAggregator).facilityIdsByType("health"))
        .isEqualTo(FacilitiesIdsResponse.builder().data(Arrays.asList("vha_757")).build());
  }

  @Test
  void invalidType() {
    assertThrows(
        ExceptionsUtils.InvalidParameter.class,
        () -> controller(baseUrl, basePath, mockServiceNameAggregator).facilityIdsByType("xxx"));
  }

  @Test
  void nullReturnAll() {
    repo.save(
        FacilitySamples.defaultSamples(linkerUrl, mockServiceNameAggregator)
            .facilityEntity("vha_757"));
    assertThat(controller(baseUrl, basePath, mockServiceNameAggregator).facilityIdsByType(""))
        .isEqualTo(FacilitiesIdsResponse.builder().data(Arrays.asList("vha_757")).build());
  }

  @BeforeEach
  void setup() {
    baseUrl = "http://foo/";
    basePath = "bp";
    linkerUrl = buildLinkerUrlV0(baseUrl, basePath);
    mockServiceNameAggregate = mock(ServiceNameAggregate.class);
    mockServiceNameAggregator = mock(ServiceNameAggregatorV0.class);
    when(mockServiceNameAggregator.serviceNameAggregate()).thenReturn(mockServiceNameAggregate);
  }

  @Test
  void validEmptyReturn() {
    repo.save(
        FacilitySamples.defaultSamples(linkerUrl, mockServiceNameAggregator)
            .facilityEntity("vha_757"));
    assertThat(
            controller(baseUrl, basePath, mockServiceNameAggregator).facilityIdsByType("benefits"))
        .isEqualTo(FacilitiesIdsResponse.builder().data(Collections.emptyList()).build());
  }
}
