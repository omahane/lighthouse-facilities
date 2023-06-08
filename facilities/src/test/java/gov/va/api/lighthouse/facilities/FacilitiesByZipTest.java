package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildLinkerUrlV0;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.va.api.lighthouse.facilities.api.ServiceType;
import gov.va.api.lighthouse.facilities.api.v0.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacility;
import gov.va.api.lighthouse.facilities.api.v0.PageLinks;
import gov.va.api.lighthouse.facilities.api.v0.Pagination;
import java.util.List;
import lombok.NonNull;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class FacilitiesByZipTest {
  @Autowired private FacilityRepository repo;

  @Autowired private FacilityServicesRepository facilityServicesRepository;

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
  void geoFacilitiesByZip() {
    final var facilityId = "vha_757";
    final GeoFacility geoFacility =
        FacilitySamples.defaultSamples(linkerUrl).geoFacility(facilityId);
    // Setup facility
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(facilityId));
    // Setup facility services
    setupFacilityServices(facilityId, geoFacility.properties().services().benefits());
    setupFacilityServices(facilityId, geoFacility.properties().services().health());
    setupFacilityServices(facilityId, geoFacility.properties().services().other());
    assertThat(
            controller(baseUrl, basePath)
                .geoFacilitiesByZip("43219", "HEALTH", List.of("urology"), false, 1, 1))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(List.of(geoFacility))
                .build());
  }

  @Test
  void jsonFacilitiesByZip_invalidService() {
    assertThrows(
        ExceptionsUtils.InvalidParameter.class,
        () ->
            controller(baseUrl, basePath)
                .jsonFacilitiesByZip("33333", null, List.of("unknown"), null, 1, 1));
  }

  @Test
  void jsonFacilitiesByZip_invalidType() {
    assertThrows(
        ExceptionsUtils.InvalidParameter.class,
        () -> controller(baseUrl, basePath).jsonFacilitiesByZip("33333", "xxx", null, null, 1, 1));
  }

  @Test
  void jsonFacilitiesByZip_noFilter() {
    final var facilityId = "vha_757";
    final Facility facility = FacilitySamples.defaultSamples(linkerUrl).facility(facilityId);
    // Setup facility
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(facilityId));
    // Setup facility services
    setupFacilityServices(facilityId, facility.attributes().services().benefits());
    setupFacilityServices(facilityId, facility.attributes().services().health());
    setupFacilityServices(facilityId, facility.attributes().services().other());
    assertThat(
            controller(baseUrl, basePath)
                .jsonFacilitiesByZip("43219", null, null, null, 1, 1)
                .data())
        .isEqualTo(List.of(facility));
  }

  @Test
  void jsonFacilitiesByZip_perPageZero() {
    final var facilityId = "vha_757";
    final Facility facility = FacilitySamples.defaultSamples(linkerUrl).facility(facilityId);
    // Setup facility
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(facilityId));
    // Setup facility services
    setupFacilityServices(facilityId, facility.attributes().services().benefits());
    setupFacilityServices(facilityId, facility.attributes().services().health());
    setupFacilityServices(facilityId, facility.attributes().services().other());
    assertThat(controller(baseUrl, basePath).jsonFacilitiesByZip("43219", null, null, null, 100, 0))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(emptyList())
                .links(
                    PageLinks.builder()
                        .self("http://foo/bp/v0/facilities?zip=43219&page=100&per_page=0")
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

  @Test
  void jsonFacilitiesByZip_serviceOnly() {
    final var facilityId = "vha_757";
    final Facility facility = FacilitySamples.defaultSamples(linkerUrl).facility(facilityId);
    // Setup facility
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(facilityId));
    // Setup facility services
    setupFacilityServices(facilityId, facility.attributes().services().benefits());
    setupFacilityServices(facilityId, facility.attributes().services().health());
    setupFacilityServices(facilityId, facility.attributes().services().other());
    assertThat(
            controller(baseUrl, basePath)
                .jsonFacilitiesByZip("43219", null, List.of("urology"), null, 1, 1)
                .data())
        .isEqualTo(List.of(facility));
  }

  @Test
  void jsonFacilitiesByZip_typeAndService() {
    final var facilityId = "vha_757";
    final Facility facility = FacilitySamples.defaultSamples(linkerUrl).facility(facilityId);
    // Setup facility
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(facilityId));
    // Setup facility services
    setupFacilityServices(facilityId, facility.attributes().services().benefits());
    setupFacilityServices(facilityId, facility.attributes().services().health());
    setupFacilityServices(facilityId, facility.attributes().services().other());
    assertThat(
            controller(baseUrl, basePath)
                .jsonFacilitiesByZip("43219", "HEALTH", List.of("primarycare"), null, 1, 1))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(List.of(facility))
                .links(
                    PageLinks.builder()
                        .self(
                            "http://foo/bp/v0/facilities?services%5B%5D=primarycare&type=HEALTH&zip=43219&page=1&per_page=1")
                        .first(
                            "http://foo/bp/v0/facilities?services%5B%5D=primarycare&type=HEALTH&zip=43219&page=1&per_page=1")
                        .last(
                            "http://foo/bp/v0/facilities?services%5B%5D=primarycare&type=HEALTH&zip=43219&page=1&per_page=1")
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
  void jsonFacilitiesByZip_typeOnly() {
    final var facilityId = "vha_757";
    final Facility facility = FacilitySamples.defaultSamples(linkerUrl).facility(facilityId);
    // Setup facility
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(facilityId));
    // Setup facility services
    setupFacilityServices(facilityId, facility.attributes().services().benefits());
    setupFacilityServices(facilityId, facility.attributes().services().health());
    setupFacilityServices(facilityId, facility.attributes().services().other());
    assertThat(
            controller(baseUrl, basePath)
                .jsonFacilitiesByZip("43219", "HEALTH", emptyList(), null, 1, 1)
                .data())
        .isEqualTo(List.of(facility));
  }

  @BeforeEach
  void setup() {
    baseUrl = "http://foo/";
    basePath = "bp";
    linkerUrl = buildLinkerUrlV0(baseUrl, basePath);
  }

  private <T extends ServiceType> void setupFacilityServices(
      @NonNull String facilityId, List<T> facilityServices) {
    if (ObjectUtils.isNotEmpty(facilityServices)) {
      facilityServices.stream()
          .forEach(
              fs ->
                  facilityServicesRepository.save(
                      FacilitySamples.defaultSamples(linkerUrl)
                          .facilityServicesEntity(facilityId, fs)));
    }
  }
}
