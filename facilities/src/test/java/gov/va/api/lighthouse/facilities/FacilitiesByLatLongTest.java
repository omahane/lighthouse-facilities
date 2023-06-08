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
import java.math.BigDecimal;
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
public class FacilitiesByLatLongTest {
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
  void geoFacilities() {
    final var vha691GbFacilityId = "vha_691GB";
    final var vha740GaFacilityId = "vha_740GA";
    final var vha757FacilityId = "vha_757";
    final GeoFacility vha691GbFacility =
        FacilitySamples.defaultSamples(linkerUrl).geoFacility(vha691GbFacilityId);
    final GeoFacility vha740GaFacility =
        FacilitySamples.defaultSamples(linkerUrl).geoFacility(vha740GaFacilityId);
    final GeoFacility vha757Facility =
        FacilitySamples.defaultSamples(linkerUrl).geoFacility(vha757FacilityId);
    // Setup facility
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha691GbFacilityId));
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha740GaFacilityId));
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha757FacilityId));
    // Setup facility services
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.properties().services().benefits());
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.properties().services().health());
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.properties().services().other());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.properties().services().benefits());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.properties().services().health());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.properties().services().other());
    setupFacilityServices(vha757FacilityId, vha757Facility.properties().services().benefits());
    setupFacilityServices(vha757FacilityId, vha757Facility.properties().services().health());
    setupFacilityServices(vha757FacilityId, vha757Facility.properties().services().other());
    // Query for facilities without constraining to a specified radius
    assertThat(
            controller(baseUrl, basePath)
                .geoFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    null,
                    "HEALTH",
                    List.of("primarycare"),
                    false,
                    1,
                    10))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(List.of(vha757Facility, vha740GaFacility, vha691GbFacility))
                .build());
    // Query for facilities within a 75 mile radius of (35.4423637, -119.77646693)
    assertThat(
            controller(baseUrl, basePath)
                .geoFacilitiesByLatLong(
                    new BigDecimal("35.4423637"),
                    new BigDecimal("-119.77646693"),
                    new BigDecimal("75"),
                    null,
                    "HEALTH",
                    List.of("primarycare"),
                    false,
                    1,
                    10))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(List.of(vha691GbFacility))
                .build());
    // Query for facilities within a 50 mile radius of (29.112464, -80.7015994)
    assertThat(
            controller(baseUrl, basePath)
                .geoFacilitiesByLatLong(
                    new BigDecimal("29.112464"),
                    new BigDecimal("-80.7015994"),
                    new BigDecimal("50"),
                    null,
                    "HEALTH",
                    List.of("primarycare"),
                    false,
                    1,
                    10))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(emptyList())
                .build());
  }

  @Test
  void geoFacilities_ids() {
    final var vha691GbFacilityId = "vha_691GB";
    final var vha740GaFacilityId = "vha_740GA";
    final var vha757FacilityId = "vha_757";
    final GeoFacility vha691GbFacility =
        FacilitySamples.defaultSamples(linkerUrl).geoFacility(vha691GbFacilityId);
    final GeoFacility vha740GaFacility =
        FacilitySamples.defaultSamples(linkerUrl).geoFacility(vha740GaFacilityId);
    final GeoFacility vha757Facility =
        FacilitySamples.defaultSamples(linkerUrl).geoFacility(vha757FacilityId);
    // Setup facility
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha691GbFacilityId));
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha740GaFacilityId));
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha757FacilityId));
    // Setup facility services
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.properties().services().benefits());
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.properties().services().health());
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.properties().services().other());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.properties().services().benefits());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.properties().services().health());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.properties().services().other());
    setupFacilityServices(vha757FacilityId, vha757Facility.properties().services().benefits());
    setupFacilityServices(vha757FacilityId, vha757Facility.properties().services().health());
    setupFacilityServices(vha757FacilityId, vha757Facility.properties().services().other());
    assertThat(
            controller(baseUrl, basePath)
                .geoFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    "x,,xxx,,,,vha_757,vha_757,vha_757,xxxx,x",
                    "HEALTH",
                    List.of("primarycare"),
                    false,
                    1,
                    10))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(List.of(vha757Facility))
                .build());
  }

  @Test
  void geoFacilities_invalidRadius() {
    // Query for facilities constrained to within a negative radius
    assertThrows(
        ExceptionsUtils.InvalidParameter.class,
        () ->
            controller(baseUrl, basePath)
                .geoFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    new BigDecimal("-10"),
                    "x,,xxx,,,,vha_757,vha_757,vha_757,xxxx,x",
                    "HEALTH",
                    List.of("primarycare"),
                    false,
                    1,
                    1));
  }

  @Test
  void geoFacilities_radiusOnly() {
    final var vha691GbFacilityId = "vha_691GB";
    final var vha740GaFacilityId = "vha_740GA";
    final var vha757FacilityId = "vha_757";
    final GeoFacility vha691GbFacility =
        FacilitySamples.defaultSamples(linkerUrl).geoFacility(vha691GbFacilityId);
    final GeoFacility vha740GaFacility =
        FacilitySamples.defaultSamples(linkerUrl).geoFacility(vha740GaFacilityId);
    final GeoFacility vha757Facility =
        FacilitySamples.defaultSamples(linkerUrl).geoFacility(vha757FacilityId);
    // Setup facility
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha691GbFacilityId));
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha740GaFacilityId));
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha757FacilityId));
    // Setup facility services
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.properties().services().benefits());
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.properties().services().health());
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.properties().services().other());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.properties().services().benefits());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.properties().services().health());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.properties().services().other());
    setupFacilityServices(vha757FacilityId, vha757Facility.properties().services().benefits());
    setupFacilityServices(vha757FacilityId, vha757Facility.properties().services().health());
    setupFacilityServices(vha757FacilityId, vha757Facility.properties().services().other());
    // Query for facilities within a 2500 mile radius of (28.112464, -80.7015994)
    assertThat(
            controller(baseUrl, basePath)
                .geoFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    new BigDecimal("2500"),
                    null,
                    null,
                    emptyList(),
                    null,
                    1,
                    10))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(List.of(vha757Facility, vha740GaFacility, vha691GbFacility))
                .build());
    // Query for facilities within a 2000 mile radius of (28.112464, -80.7015994)
    assertThat(
            controller(baseUrl, basePath)
                .geoFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    new BigDecimal("2000"),
                    null,
                    null,
                    emptyList(),
                    null,
                    1,
                    10))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(List.of(vha757Facility, vha740GaFacility))
                .build());
    // Query for facilities within a 1000 mile radius of (28.112464, -80.7015994)
    assertThat(
            controller(baseUrl, basePath)
                .geoFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    new BigDecimal("1000"),
                    null,
                    null,
                    emptyList(),
                    null,
                    1,
                    10))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(List.of(vha757Facility))
                .build());
    // Query for facilities within a 500 mile radius of (28.112464, -80.7015994)
    assertThat(
            controller(baseUrl, basePath)
                .geoFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    new BigDecimal("500"),
                    null,
                    null,
                    emptyList(),
                    null,
                    1,
                    10))
        .isEqualTo(
            GeoFacilitiesResponse.builder()
                .type(GeoFacilitiesResponse.Type.FeatureCollection)
                .features(emptyList())
                .build());
  }

  @Test
  void json_ids() {
    final var vha691GbFacilityId = "vha_691GB";
    final var vha740GaFacilityId = "vha_740GA";
    final var vha757FacilityId = "vha_757";
    final Facility vha691GbFacility =
        FacilitySamples.defaultSamples(linkerUrl).facility(vha691GbFacilityId);
    final Facility vha740GaFacility =
        FacilitySamples.defaultSamples(linkerUrl).facility(vha740GaFacilityId);
    final Facility vha757Facility =
        FacilitySamples.defaultSamples(linkerUrl).facility(vha757FacilityId);
    // Setup facility
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha691GbFacilityId));
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha740GaFacilityId));
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha757FacilityId));
    // Setup facility services
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.attributes().services().benefits());
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.attributes().services().health());
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.attributes().services().other());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.attributes().services().benefits());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.attributes().services().health());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.attributes().services().other());
    setupFacilityServices(vha757FacilityId, vha757Facility.attributes().services().benefits());
    setupFacilityServices(vha757FacilityId, vha757Facility.attributes().services().health());
    setupFacilityServices(vha757FacilityId, vha757Facility.attributes().services().other());
    assertThat(
            controller(baseUrl, basePath)
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    "x,,xxx,,,,vha_757,vha_757,vha_757,xxxx,x",
                    null,
                    null,
                    null,
                    1,
                    10)
                .data())
        .isEqualTo(List.of(vha757Facility));
  }

  @Test
  void json_invalidRadius() {
    // Query for facilities constrained to within a negative radius
    assertThrows(
        ExceptionsUtils.InvalidParameter.class,
        () ->
            controller(baseUrl, basePath)
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    new BigDecimal("-10"),
                    "x,,xxx,,,,vha_757,vha_757,vha_757,xxxx,x",
                    "HEALTH",
                    List.of("primarycare"),
                    false,
                    1,
                    1));
  }

  @Test
  void json_invalidService() {
    assertThrows(
        ExceptionsUtils.InvalidParameter.class,
        () ->
            controller(baseUrl, basePath)
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    null,
                    null,
                    List.of("unknown"),
                    null,
                    1,
                    1));
  }

  @Test
  void json_invalidType() {
    assertThrows(
        ExceptionsUtils.InvalidParameter.class,
        () ->
            controller(baseUrl, basePath)
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    null,
                    "xxx",
                    null,
                    null,
                    1,
                    1));
  }

  @Test
  void json_noFilter() {
    final var vha691GbFacilityId = "vha_691GB";
    final var vha740GaFacilityId = "vha_740GA";
    final var vha757FacilityId = "vha_757";
    final Facility vha691GbFacility =
        FacilitySamples.defaultSamples(linkerUrl).facility(vha691GbFacilityId);
    final Facility vha740GaFacility =
        FacilitySamples.defaultSamples(linkerUrl).facility(vha740GaFacilityId);
    final Facility vha757Facility =
        FacilitySamples.defaultSamples(linkerUrl).facility(vha757FacilityId);
    // Setup facility
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha691GbFacilityId));
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha740GaFacilityId));
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha757FacilityId));
    // Setup facility services
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.attributes().services().benefits());
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.attributes().services().health());
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.attributes().services().other());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.attributes().services().benefits());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.attributes().services().health());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.attributes().services().other());
    setupFacilityServices(vha757FacilityId, vha757Facility.attributes().services().benefits());
    setupFacilityServices(vha757FacilityId, vha757Facility.attributes().services().health());
    setupFacilityServices(vha757FacilityId, vha757Facility.attributes().services().other());
    assertThat(
            controller(baseUrl, basePath)
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    null,
                    null,
                    null,
                    null,
                    1,
                    10)
                .data())
        .isEqualTo(List.of(vha757Facility, vha740GaFacility, vha691GbFacility));
  }

  @Test
  void json_perPageZero() {
    final var vha691GbFacilityId = "vha_691GB";
    final var vha740GaFacilityId = "vha_740GA";
    final var vha757FacilityId = "vha_757";
    final Facility vha691GbFacility =
        FacilitySamples.defaultSamples(linkerUrl).facility(vha691GbFacilityId);
    final Facility vha740GaFacility =
        FacilitySamples.defaultSamples(linkerUrl).facility(vha740GaFacilityId);
    final Facility vha757Facility =
        FacilitySamples.defaultSamples(linkerUrl).facility(vha757FacilityId);
    // Setup facility
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha691GbFacilityId));
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha740GaFacilityId));
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha757FacilityId));
    // Setup facility services
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.attributes().services().benefits());
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.attributes().services().health());
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.attributes().services().other());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.attributes().services().benefits());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.attributes().services().health());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.attributes().services().other());
    setupFacilityServices(vha757FacilityId, vha757Facility.attributes().services().benefits());
    setupFacilityServices(vha757FacilityId, vha757Facility.attributes().services().health());
    setupFacilityServices(vha757FacilityId, vha757Facility.attributes().services().other());
    // Query for facilities without constraining to a specified radius
    assertThat(
            controller(baseUrl, basePath)
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    null,
                    null,
                    null,
                    null,
                    100,
                    0))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(emptyList())
                .links(
                    PageLinks.builder()
                        .self(
                            "http://foo/v0/facilities?lat=28.112464&long=-80.7015994&page=100&per_page=0")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(100)
                                .entriesPerPage(0)
                                .totalPages(0)
                                .totalEntries(3)
                                .build())
                        .distances(emptyList())
                        .build())
                .build());
    // Query for facilities within a 75 mile radius of (27.1745479800001, -97.6667188)
    assertThat(
            controller(baseUrl, basePath)
                .jsonFacilitiesByLatLong(
                    new BigDecimal("27.1745479800001"),
                    new BigDecimal("-97.6667188"),
                    new BigDecimal("75"),
                    null,
                    null,
                    null,
                    null,
                    100,
                    0))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(emptyList())
                .links(
                    PageLinks.builder()
                        .self(
                            "http://foo/v0/facilities?lat=27.1745479800001&long=-97.6667188&radius=75&page=100&per_page=0")
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
                        .distances(emptyList())
                        .build())
                .build());
    // Query for facilities within a 50 mile radius of (29.112464, -80.7015994)
    assertThat(
            controller(baseUrl, basePath)
                .jsonFacilitiesByLatLong(
                    new BigDecimal("29.112464"),
                    new BigDecimal("-80.7015994"),
                    new BigDecimal("50"),
                    null,
                    null,
                    null,
                    null,
                    100,
                    0))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(emptyList())
                .links(
                    PageLinks.builder()
                        .self(
                            "http://foo/v0/facilities?lat=29.112464&long=-80.7015994&radius=50&page=100&per_page=0")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(100)
                                .entriesPerPage(0)
                                .totalPages(0)
                                .totalEntries(0)
                                .build())
                        .distances(emptyList())
                        .build())
                .build());
  }

  @Test
  void json_radiusOnly() {
    final var vha691GbFacilityId = "vha_691GB";
    final var vha740GaFacilityId = "vha_740GA";
    final var vha757FacilityId = "vha_757";
    final Facility vha691GbFacility =
        FacilitySamples.defaultSamples(linkerUrl).facility(vha691GbFacilityId);
    final Facility vha740GaFacility =
        FacilitySamples.defaultSamples(linkerUrl).facility(vha740GaFacilityId);
    final Facility vha757Facility =
        FacilitySamples.defaultSamples(linkerUrl).facility(vha757FacilityId);
    // Setup facility
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha691GbFacilityId));
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha740GaFacilityId));
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha757FacilityId));
    // Setup facility services
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.attributes().services().benefits());
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.attributes().services().health());
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.attributes().services().other());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.attributes().services().benefits());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.attributes().services().health());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.attributes().services().other());
    setupFacilityServices(vha757FacilityId, vha757Facility.attributes().services().benefits());
    setupFacilityServices(vha757FacilityId, vha757Facility.attributes().services().health());
    setupFacilityServices(vha757FacilityId, vha757Facility.attributes().services().other());
    // Query for facilities within a 2500 mile radius of (28.112464, -80.7015994)
    assertThat(
            controller(baseUrl, basePath)
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    new BigDecimal("2500"),
                    null,
                    null,
                    emptyList(),
                    null,
                    1,
                    10)
                .data())
        .isEqualTo(List.of(vha757Facility, vha740GaFacility, vha691GbFacility));
    // Query for facilities within a 2000 mile radius of (28.112464, -80.7015994)
    assertThat(
            controller(baseUrl, basePath)
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    new BigDecimal("2000"),
                    null,
                    null,
                    emptyList(),
                    null,
                    1,
                    10)
                .data())
        .isEqualTo(List.of(vha757Facility, vha740GaFacility));
    // Query for facilities within a 1000 mile radius of (28.112464, -80.7015994)
    assertThat(
            controller(baseUrl, basePath)
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    new BigDecimal("1000"),
                    null,
                    null,
                    emptyList(),
                    null,
                    1,
                    10)
                .data())
        .isEqualTo(List.of(vha757Facility));
    // Query for facilities within a 500 mile radius of (28.112464, -80.7015994)
    assertThat(
            controller(baseUrl, basePath)
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    new BigDecimal("500"),
                    null,
                    null,
                    emptyList(),
                    null,
                    1,
                    10)
                .data())
        .isEqualTo(emptyList());
  }

  @Test
  void json_serviceOnly() {
    final var vha691GbFacilityId = "vha_691GB";
    final var vha740GaFacilityId = "vha_740GA";
    final var vha757FacilityId = "vha_757";
    final Facility vha691GbFacility =
        FacilitySamples.defaultSamples(linkerUrl).facility(vha691GbFacilityId);
    final Facility vha740GaFacility =
        FacilitySamples.defaultSamples(linkerUrl).facility(vha740GaFacilityId);
    final Facility vha757Facility =
        FacilitySamples.defaultSamples(linkerUrl).facility(vha757FacilityId);
    // Setup facility
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha691GbFacilityId));
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha740GaFacilityId));
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha757FacilityId));
    // Setup facility services
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.attributes().services().benefits());
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.attributes().services().health());
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.attributes().services().other());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.attributes().services().benefits());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.attributes().services().health());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.attributes().services().other());
    setupFacilityServices(vha757FacilityId, vha757Facility.attributes().services().benefits());
    setupFacilityServices(vha757FacilityId, vha757Facility.attributes().services().health());
    setupFacilityServices(vha757FacilityId, vha757Facility.attributes().services().other());
    assertThat(
            controller(baseUrl, basePath)
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    null,
                    null,
                    List.of("primarycare"),
                    null,
                    1,
                    10)
                .data())
        .isEqualTo(List.of(vha757Facility, vha740GaFacility, vha691GbFacility));
  }

  @Test
  void json_typeAndService() {
    final var vha691GbFacilityId = "vha_691GB";
    final var vha740GaFacilityId = "vha_740GA";
    final var vha757FacilityId = "vha_757";
    final Facility vha691GbFacility =
        FacilitySamples.defaultSamples(linkerUrl).facility(vha691GbFacilityId);
    final Facility vha740GaFacility =
        FacilitySamples.defaultSamples(linkerUrl).facility(vha740GaFacilityId);
    final Facility vha757Facility =
        FacilitySamples.defaultSamples(linkerUrl).facility(vha757FacilityId);
    // Setup facility
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha691GbFacilityId));
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha740GaFacilityId));
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha757FacilityId));
    // Setup facility services
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.attributes().services().benefits());
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.attributes().services().health());
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.attributes().services().other());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.attributes().services().benefits());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.attributes().services().health());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.attributes().services().other());
    setupFacilityServices(vha757FacilityId, vha757Facility.attributes().services().benefits());
    setupFacilityServices(vha757FacilityId, vha757Facility.attributes().services().health());
    setupFacilityServices(vha757FacilityId, vha757Facility.attributes().services().other());
    String linkBase =
        "http://foo/v0/facilities?lat=28.112464&long=-80.7015994&services%5B%5D=primarycare&type=HEALTH";
    assertThat(
            controller(baseUrl, basePath)
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    null,
                    "HEALTH",
                    List.of("primarycare"),
                    null,
                    1,
                    10))
        .isEqualTo(
            FacilitiesResponse.builder()
                .data(List.of(vha757Facility, vha740GaFacility, vha691GbFacility))
                .links(
                    PageLinks.builder()
                        .self(linkBase + "&page=1&per_page=10")
                        .first(linkBase + "&page=1&per_page=10")
                        .last(linkBase + "&page=1&per_page=10")
                        .build())
                .meta(
                    FacilitiesResponse.FacilitiesMetadata.builder()
                        .pagination(
                            Pagination.builder()
                                .currentPage(1)
                                .entriesPerPage(10)
                                .totalPages(1)
                                .totalEntries(3)
                                .build())
                        .distances(
                            List.of(
                                FacilitiesResponse.Distance.builder()
                                    .id("vha_757")
                                    .distance(new BigDecimal("829.69"))
                                    .build(),
                                FacilitiesResponse.Distance.builder()
                                    .id("vha_740GA")
                                    .distance(new BigDecimal("1050.77"))
                                    .build(),
                                FacilitiesResponse.Distance.builder()
                                    .id("vha_691GB")
                                    .distance(new BigDecimal("2333.84"))
                                    .build()))
                        .build())
                .build());
  }

  @Test
  void json_typeOnly() {
    final var vha691GbFacilityId = "vha_691GB";
    final var vha740GaFacilityId = "vha_740GA";
    final var vha757FacilityId = "vha_757";
    final Facility vha691GbFacility =
        FacilitySamples.defaultSamples(linkerUrl).facility(vha691GbFacilityId);
    final Facility vha740GaFacility =
        FacilitySamples.defaultSamples(linkerUrl).facility(vha740GaFacilityId);
    final Facility vha757Facility =
        FacilitySamples.defaultSamples(linkerUrl).facility(vha757FacilityId);
    // Setup facility
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha691GbFacilityId));
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha740GaFacilityId));
    repo.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(vha757FacilityId));
    // Setup facility services
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.attributes().services().benefits());
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.attributes().services().health());
    setupFacilityServices(vha691GbFacilityId, vha691GbFacility.attributes().services().other());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.attributes().services().benefits());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.attributes().services().health());
    setupFacilityServices(vha740GaFacilityId, vha740GaFacility.attributes().services().other());
    setupFacilityServices(vha757FacilityId, vha757Facility.attributes().services().benefits());
    setupFacilityServices(vha757FacilityId, vha757Facility.attributes().services().health());
    setupFacilityServices(vha757FacilityId, vha757Facility.attributes().services().other());
    assertThat(
            controller(baseUrl, basePath)
                .jsonFacilitiesByLatLong(
                    new BigDecimal("28.112464"),
                    new BigDecimal("-80.7015994"),
                    null,
                    null,
                    "HEALTH",
                    emptyList(),
                    null,
                    1,
                    10)
                .data())
        .isEqualTo(List.of(vha757Facility, vha740GaFacility, vha691GbFacility));
  }

  @BeforeEach
  void setup() {
    baseUrl = "http://foo/";
    basePath = "";
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
