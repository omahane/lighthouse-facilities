package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildLinkerUrlV0;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.lighthouse.facilities.ServiceNameAggregatorV0.ServiceNameAggregate;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import java.util.List;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class FacilitiesByVisnTest {
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
  void geoFacilities() {
    repo.save(
        FacilitySamples.defaultSamples(linkerUrl, mockServiceNameAggregator)
            .facilityEntity("vha_757"));
    assertThat(
            controller(baseUrl, basePath, mockServiceNameAggregator)
                .geoFacilitiesByVisn("10", 1, 1))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(
                    List.of(
                        FacilitySamples.defaultSamples(linkerUrl, mockServiceNameAggregator)
                            .geoFacility("vha_757")))
                .build());
  }

  @Test
  void json_searchVisn() {
    repo.save(
        FacilitySamples.defaultSamples(linkerUrl, mockServiceNameAggregator)
            .facilityEntity("vha_757"));
    assertThat(
            controller(baseUrl, basePath, mockServiceNameAggregator)
                .jsonFacilitiesByVisn("10", 1, 1)
                .data())
        .isEqualTo(
            List.of(
                FacilitySamples.defaultSamples(linkerUrl, mockServiceNameAggregator)
                    .facility("vha_757")));
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
}
