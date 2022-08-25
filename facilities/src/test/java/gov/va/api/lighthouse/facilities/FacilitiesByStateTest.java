package gov.va.api.lighthouse.facilities;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.va.api.lighthouse.facilities.api.v0.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.PageLinks;
import gov.va.api.lighthouse.facilities.api.v0.Pagination;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class FacilitiesByStateTest {
  @Autowired private FacilityRepository repo;

  private FacilitiesControllerV0 controller() {
    return FacilitiesControllerV0.builder()
        .facilityRepository(repo)
        .baseUrl("http://foo/")
        .basePath("bp")
        .build();
  }

  @Test
  void geoFacilities() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(controller().geoFacilitiesByState("oh", "HEALTH", List.of("urology"), false, 1, 1))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(List.of(FacilitySamples.defaultSamples().geoFacility("vha_757")))
                .build());
  }

  @Test
  void json_invalidService() {
    assertThrows(
        ExceptionsUtils.InvalidParameter.class,
        () -> controller().jsonFacilitiesByState("FL", null, List.of("unknown"), null, 1, 1));
  }

  @Test
  void json_invalidType() {
    assertThrows(
        ExceptionsUtils.InvalidParameter.class,
        () -> controller().jsonFacilitiesByState("FL", "xxx", null, null, 1, 1));
  }

  @Test
  void json_noFilter() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(controller().jsonFacilitiesByState("oh", null, null, null, 1, 1).data())
        .isEqualTo(List.of(FacilitySamples.defaultSamples().facility("vha_757")));
  }

  @Test
  void json_serviceOnly() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(
            controller().jsonFacilitiesByState("oh", null, List.of("urology"), null, 1, 1).data())
        .isEqualTo(List.of(FacilitySamples.defaultSamples().facility("vha_757")));
  }

  @Test
  void json_typeAndService() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    String linkBase = "http://foo/bp/v0/facilities?services%5B%5D=primarycare&state=oh&type=HEALTH";
    assertThat(
            controller().jsonFacilitiesByState("oh", "HEALTH", List.of("primarycare"), null, 1, 1))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(List.of(FacilitySamples.defaultSamples().facility("vha_757")))
                .links(
                    PageLinks.builder()
                        .self(linkBase + "&page=1&per_page=1")
                        .first(linkBase + "&page=1&per_page=1")
                        .last(linkBase + "&page=1&per_page=1")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(1)
                                .entriesPerPage(1)
                                .totalPages(1)
                                .totalEntries(1)
                                .build())
                        .build())
                .build());
  }

  @Test
  void json_typeOnly() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(controller().jsonFacilitiesByState("oh", "HEALTH", emptyList(), null, 1, 1).data())
        .isEqualTo(List.of(FacilitySamples.defaultSamples().facility("vha_757")));
  }

  @Test
  void jsonperPageZero() {
    repo.save(FacilitySamples.defaultSamples().facilityEntity("vha_757"));
    assertThat(controller().jsonFacilitiesByState("oh", null, null, null, 100, 0))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(emptyList())
                .links(
                    PageLinks.builder()
                        .self("http://foo/bp/v0/facilities?state=oh&page=100&per_page=0")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(100)
                                .entriesPerPage(0)
                                .totalPages(0)
                                .totalEntries(1)
                                .build())
                        .build())
                .build());
  }
}
