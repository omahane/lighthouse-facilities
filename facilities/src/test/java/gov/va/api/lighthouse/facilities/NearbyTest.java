package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildLinkerUrlV0;
import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildLinkerUrlV1;
import static gov.va.api.lighthouse.facilities.api.v0.Facility.BenefitsService.ApplyingForBenefits;
import static gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.PrimaryCare;
import static gov.va.api.lighthouse.facilities.api.v0.NearbyResponse.Type.NearbyFacility;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.DatamartFacility.Service.Source;
import gov.va.api.lighthouse.facilities.api.TypedService;
import gov.va.api.lighthouse.facilities.api.pssg.PathEncoder;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.NearbyResponse;
import gov.va.api.lighthouse.facilities.collector.InsecureRestTemplateProvider;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class NearbyTest {
  @Autowired FacilityRepository facilityRepository;

  @Autowired FacilityServicesRepository facilityServicesRepository;

  @Autowired DriveTimeBandRepository driveTimeBandRepository;

  private RestTemplate mockRestTemplate;

  private NearbyControllerV0 _controller() {
    InsecureRestTemplateProvider restTemplateProvider = mock(InsecureRestTemplateProvider.class);
    when(restTemplateProvider.restTemplate()).thenReturn(mockRestTemplate);
    return NearbyControllerV0.builder()
        .facilityRepository(facilityRepository)
        .driveTimeBandRepository(driveTimeBandRepository)
        .build();
  }

  @SneakyThrows
  private DriveTimeBandEntity _deprecatedPssgDriveTimeBandEntity(PssgDriveTimeBand band) {
    List<List<Double>> flatRings =
        band.geometry().rings().stream().flatMap(r -> r.stream()).collect(toList());
    return DriveTimeBandEntity.builder()
        .id(
            DriveTimeBandEntity.Pk.of(
                band.attributes().stationNumber(),
                band.attributes().fromBreak(),
                band.attributes().toBreak()))
        .minLongitude(flatRings.stream().mapToDouble(c -> c.get(0)).min().orElseThrow())
        .maxLongitude(flatRings.stream().mapToDouble(c -> c.get(0)).max().orElseThrow())
        .minLatitude(flatRings.stream().mapToDouble(c -> c.get(1)).min().orElseThrow())
        .maxLatitude(flatRings.stream().mapToDouble(c -> c.get(1)).max().orElseThrow())
        .band(JacksonConfig.createMapper().writeValueAsString(band))
        .build();
  }

  private PssgDriveTimeBand _diamondBand(
      String stationNumber, int fromMinutes, int toMinutes, int offset) {
    return PssgDriveTimeBand.builder()
        .attributes(
            PssgDriveTimeBand.Attributes.builder()
                .stationNumber(stationNumber)
                .fromBreak(fromMinutes)
                .toBreak(toMinutes)
                .build())
        .geometry(
            PssgDriveTimeBand.Geometry.builder()
                .rings(
                    List.of(
                        List.of(
                            PssgDriveTimeBand.coord(offset, offset + 2),
                            PssgDriveTimeBand.coord(offset + 1, offset),
                            PssgDriveTimeBand.coord(offset, offset - 2),
                            PssgDriveTimeBand.coord(offset - 1, offset))))
                .build())
        .build();
  }

  @SneakyThrows
  private DriveTimeBandEntity _entity(PssgDriveTimeBand band) {
    List<List<Double>> flatRings =
        band.geometry().rings().stream().flatMap(r -> r.stream()).collect(toList());
    return DriveTimeBandEntity.builder()
        .id(
            DriveTimeBandEntity.Pk.of(
                band.attributes().stationNumber(),
                band.attributes().fromBreak(),
                band.attributes().toBreak()))
        .minLongitude(flatRings.stream().mapToDouble(c -> c.get(0)).min().orElseThrow())
        .maxLongitude(flatRings.stream().mapToDouble(c -> c.get(0)).max().orElseThrow())
        .minLatitude(flatRings.stream().mapToDouble(c -> c.get(1)).min().orElseThrow())
        .maxLatitude(flatRings.stream().mapToDouble(c -> c.get(1)).max().orElseThrow())
        .band(PathEncoder.create().encodeToBase64(band))
        .build();
  }

  private Facility _facilityBenefits(String id) {
    return Facility.builder()
        .id(id)
        .attributes(
            Facility.FacilityAttributes.builder()
                .latitude(BigDecimal.ONE)
                .longitude(BigDecimal.ONE)
                .services(
                    Facility.Services.builder().benefits(List.of(ApplyingForBenefits)).build())
                .build())
        .build();
  }

  private FacilityEntity _facilityEntity(DatamartFacility datamartFacility) {
    return InternalFacilitiesController.populate(
        FacilityEntity.builder()
            .id(FacilityEntity.Pk.fromIdString(datamartFacility.id()))
            .lastUpdated(Instant.now())
            .build(),
        datamartFacility);
  }

  private DatamartFacility _facilityHealth(String id) {
    DatamartFacility facility =
        FacilityTransformerV0.toVersionAgnostic(
            Facility.builder()
                .id(id)
                .attributes(
                    Facility.FacilityAttributes.builder()
                        .latitude(BigDecimal.ONE)
                        .longitude(BigDecimal.ONE)
                        .services(Facility.Services.builder().health(List.of(PrimaryCare)).build())
                        .build())
                .build());
    facility.attributes.services().health().stream().forEach(hs -> hs.source(Source.ATC));
    return facility;
  }

  @Test
  @SneakyThrows
  void addressNotSupported() {
    when(mockRestTemplate.exchange(
            startsWith("http://bing"),
            eq(HttpMethod.GET),
            Mockito.any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(
            ResponseEntity.of(
                Optional.of(
                    JacksonConfig.createMapper()
                        .writeValueAsString(
                            BingResponse.builder()
                                .resourceSets(
                                    List.of(
                                        BingResponse.ResourceSet.builder().build(),
                                        BingResponse.ResourceSet.builder()
                                            .resources(
                                                List.of(
                                                    BingResponse.Resource.builder().build(),
                                                    BingResponse.Resource.builder()
                                                        .resourcePoint(
                                                            BingResponse.Point.builder().build())
                                                        .build(),
                                                    BingResponse.Resource.builder()
                                                        .resourcePoint(
                                                            BingResponse.Point.builder()
                                                                .coordinates(
                                                                    List.of(BigDecimal.ZERO))
                                                                .build())
                                                        .build(),
                                                    BingResponse.Resource.builder()
                                                        .resourcePoint(
                                                            BingResponse.Point.builder()
                                                                .coordinates(
                                                                    List.of(
                                                                        new BigDecimal("-0.1"),
                                                                        new BigDecimal("0.1")))
                                                                .build())
                                                        .build()))
                                            .build()))
                                .build()))));
    final var linkerUrl = buildLinkerUrlV1("http://foo/", "bar");
    final var vha666FacilityId = "vha_666";
    final var vha777FacilityId = "vha_777";
    final DatamartFacility vha666Facility = _facilityHealth(vha666FacilityId);
    final DatamartFacility vha777Facility = _facilityHealth(vha777FacilityId);
    // Setup facility
    facilityRepository.save(_facilityEntity(vha666Facility));
    facilityRepository.save(_facilityEntity(vha777Facility));
    // Setup facility services
    setupFacilityServices(
        vha666FacilityId, linkerUrl, vha666Facility.attributes().services().benefits());
    setupFacilityServices(
        vha666FacilityId, linkerUrl, vha666Facility.attributes().services().health());
    setupFacilityServices(
        vha666FacilityId, linkerUrl, vha666Facility.attributes().services().other());
    setupFacilityServices(
        vha777FacilityId, linkerUrl, vha777Facility.attributes().services().benefits());
    setupFacilityServices(
        vha777FacilityId, linkerUrl, vha777Facility.attributes().services().health());
    setupFacilityServices(
        vha777FacilityId, linkerUrl, vha777Facility.attributes().services().other());
    // Setup drive time band
    driveTimeBandRepository.save(_entity(_diamondBand("666", 0, 10, 0)));
    driveTimeBandRepository.save(_entity(_diamondBand("777", 80, 90, 5)));
    assertThatThrownBy(
            () ->
                _controller()
                    .nearbyAddressNotSupported(
                        "505 N John Rodes Blvd", "Melbourne", "FL", "32934", null, null))
        .isInstanceOf(ExceptionsUtilsV0.BingException.class)
        .hasMessage("Bing error: Search by address is not supported, please use [lat,lng]");
  }

  @Test
  void addressNotSupported_bingException() {
    when(mockRestTemplate.exchange(
            startsWith("http://bing"),
            eq(HttpMethod.GET),
            Mockito.any(HttpEntity.class),
            eq(String.class)))
        .thenThrow(new IllegalStateException("Google instead?"));
    assertThatThrownBy(
            () ->
                _controller()
                    .nearbyAddressNotSupported(
                        "505 N John Rodes Blvd", "Melbourne", "FL", "32934", null, null))
        .isInstanceOf(ExceptionsUtilsV0.BingException.class)
        .hasMessage("Bing error: Search by address is not supported, please use [lat,lng]");
  }

  @Test
  @SneakyThrows
  void addressNotSupported_bingNoResults() {
    when(mockRestTemplate.exchange(
            startsWith("http://bing"),
            eq(HttpMethod.GET),
            Mockito.any(HttpEntity.class),
            eq(String.class)))
        .thenReturn(
            ResponseEntity.of(
                Optional.of(
                    JacksonConfig.createMapper()
                        .writeValueAsString(
                            BingResponse.builder()
                                .resourceSets(
                                    List.of(
                                        BingResponse.ResourceSet.builder().build(),
                                        BingResponse.ResourceSet.builder()
                                            .resources(
                                                List.of(
                                                    BingResponse.Resource.builder().build(),
                                                    BingResponse.Resource.builder()
                                                        .resourcePoint(
                                                            BingResponse.Point.builder().build())
                                                        .build(),
                                                    BingResponse.Resource.builder()
                                                        .resourcePoint(
                                                            BingResponse.Point.builder()
                                                                .coordinates(
                                                                    List.of(BigDecimal.ZERO))
                                                                .build())
                                                        .build()))
                                            .build()))
                                .build()))));
    assertThatThrownBy(
            () ->
                _controller()
                    .nearbyAddressNotSupported(
                        "505 N John Rodes Blvd", "Melbourne", "FL", "32934", null, null))
        .isInstanceOf(ExceptionsUtilsV0.BingException.class)
        .hasMessage("Bing error: Search by address is not supported, please use [lat,lng]");
  }

  @Test
  void empty() {
    final var baseUrl = "http://foo/";
    final var basePath = "bp";
    final var linkerUrl = buildLinkerUrlV0(baseUrl, basePath);
    final var facilityId = "vha_757";
    final DatamartFacility facility = _facilityHealth(facilityId);
    // Setup facility
    facilityRepository.save(FacilitySamples.defaultSamples(linkerUrl).facilityEntity(facilityId));
    // Setup facility services
    setupFacilityServices(facilityId, linkerUrl, facility.attributes().services().benefits());
    setupFacilityServices(facilityId, linkerUrl, facility.attributes().services().health());
    setupFacilityServices(facilityId, linkerUrl, facility.attributes().services().other());
    NearbyResponse response =
        _controller().nearbyLatLong(BigDecimal.ZERO, BigDecimal.ZERO, null, null);
    assertThat(response)
        .isEqualTo(
            NearbyResponse.builder()
                .data(emptyList())
                .meta(NearbyResponse.Meta.builder().bandVersion("Unknown").build())
                .build());
  }

  @Test
  void filterMaxDriveTime() {
    final var linkerUrl = buildLinkerUrlV1("http://foo/", "bar");
    final var vha666FacilityId = "vha_666";
    final var vha777FacilityId = "vha_777";
    final DatamartFacility vha666Facility = _facilityHealth(vha666FacilityId);
    final DatamartFacility vha777Facility = _facilityHealth(vha777FacilityId);
    // Setup facility
    facilityRepository.save(_facilityEntity(vha666Facility));
    facilityRepository.save(_facilityEntity(vha777Facility));
    // Setup facility services
    setupFacilityServices(
        vha666FacilityId, linkerUrl, vha666Facility.attributes().services().benefits());
    setupFacilityServices(
        vha666FacilityId, linkerUrl, vha666Facility.attributes().services().health());
    setupFacilityServices(
        vha666FacilityId, linkerUrl, vha666Facility.attributes().services().other());
    setupFacilityServices(
        vha777FacilityId, linkerUrl, vha777Facility.attributes().services().benefits());
    setupFacilityServices(
        vha777FacilityId, linkerUrl, vha777Facility.attributes().services().health());
    setupFacilityServices(
        vha777FacilityId, linkerUrl, vha777Facility.attributes().services().other());
    // Setup drive time band
    driveTimeBandRepository.save(_entity(_diamondBand("666", 50, 60, 0)));
    driveTimeBandRepository.save(_entity(_diamondBand("777", 80, 90, 5)));
    NearbyResponse response =
        _controller().nearbyLatLong(BigDecimal.ZERO, BigDecimal.ZERO, null, 50);
    assertThat(response)
        .isEqualTo(
            NearbyResponse.builder()
                .data(emptyList())
                .meta(NearbyResponse.Meta.builder().bandVersion("Unknown").build())
                .build());
  }

  @Test
  void filterServices() {
    final var linkerUrl = buildLinkerUrlV1("http://foo/", "bar");
    final var vha666FacilityId = "vha_666";
    final var vha777FacilityId = "vha_777";
    final DatamartFacility vha666Facility = _facilityHealth(vha666FacilityId);
    final DatamartFacility vha777Facility = _facilityHealth(vha777FacilityId);
    // Setup facility
    facilityRepository.save(_facilityEntity(vha666Facility));
    facilityRepository.save(_facilityEntity(vha777Facility));
    // Setup facility services
    setupFacilityServices(
        vha666FacilityId, linkerUrl, vha666Facility.attributes().services().benefits());
    setupFacilityServices(
        vha666FacilityId, linkerUrl, vha666Facility.attributes().services().health());
    setupFacilityServices(
        vha666FacilityId, linkerUrl, vha666Facility.attributes().services().other());
    setupFacilityServices(
        vha777FacilityId, linkerUrl, vha777Facility.attributes().services().benefits());
    setupFacilityServices(
        vha777FacilityId, linkerUrl, vha777Facility.attributes().services().health());
    setupFacilityServices(
        vha777FacilityId, linkerUrl, vha777Facility.attributes().services().other());
    // Setup drive time band
    driveTimeBandRepository.save(_entity(_diamondBand("666", 0, 10, 0)));
    driveTimeBandRepository.save(_entity(_diamondBand("777", 80, 90, 5)));
    NearbyResponse response =
        _controller().nearbyLatLong(BigDecimal.ZERO, BigDecimal.ZERO, List.of("primarycare"), null);
    assertThat(response)
        .isEqualTo(
            NearbyResponse.builder()
                .data(
                    List.of(
                        NearbyResponse.Nearby.builder()
                            .id(vha666FacilityId)
                            .type(NearbyResponse.Type.NearbyFacility)
                            .attributes(
                                NearbyResponse.NearbyAttributes.builder()
                                    .minTime(0)
                                    .maxTime(10)
                                    .build())
                            .build()))
                .meta(NearbyResponse.Meta.builder().bandVersion("Unknown").build())
                .build());
  }

  @Test
  void hit() {
    final var linkerUrl = buildLinkerUrlV1("http://foo/", "bar");
    final var vha666FacilityId = "vha_666";
    final var vha777FacilityId = "vha_777";
    final DatamartFacility vha666Facility = _facilityHealth(vha666FacilityId);
    final DatamartFacility vha777Facility = _facilityHealth(vha777FacilityId);
    // Setup facility
    facilityRepository.save(_facilityEntity(vha666Facility));
    facilityRepository.save(_facilityEntity(vha777Facility));
    // Setup facility services
    setupFacilityServices(
        vha666FacilityId, linkerUrl, vha666Facility.attributes().services().benefits());
    setupFacilityServices(
        vha666FacilityId, linkerUrl, vha666Facility.attributes().services().health());
    setupFacilityServices(
        vha666FacilityId, linkerUrl, vha666Facility.attributes().services().other());
    setupFacilityServices(
        vha777FacilityId, linkerUrl, vha777Facility.attributes().services().benefits());
    setupFacilityServices(
        vha777FacilityId, linkerUrl, vha777Facility.attributes().services().health());
    setupFacilityServices(
        vha777FacilityId, linkerUrl, vha777Facility.attributes().services().other());
    // Setup drive time band
    driveTimeBandRepository.save(_entity(_diamondBand("666", 0, 10, 0)));
    driveTimeBandRepository.save(_entity(_diamondBand("777", 80, 90, 5)));
    NearbyResponse response =
        _controller().nearbyLatLong(BigDecimal.ZERO, BigDecimal.ZERO, null, null);
    assertThat(response).isEqualTo(hitVha666());
  }

  NearbyResponse hitVha666() {
    return NearbyResponse.builder()
        .data(
            List.of(
                NearbyResponse.Nearby.builder()
                    .id("vha_666")
                    .type(NearbyFacility)
                    .attributes(
                        NearbyResponse.NearbyAttributes.builder().minTime(0).maxTime(10).build())
                    .build()))
        .meta(NearbyResponse.Meta.builder().bandVersion("Unknown").build())
        .build();
  }

  @Test
  void hitWithDeprecatedPssgDriveBands() {
    final var linkerUrl = buildLinkerUrlV1("http://foo/", "bar");
    final var vha666FacilityId = "vha_666";
    final var vha777FacilityId = "vha_777";
    final DatamartFacility vha666Facility = _facilityHealth(vha666FacilityId);
    final DatamartFacility vha777Facility = _facilityHealth(vha777FacilityId);
    // Setup facility
    facilityRepository.save(_facilityEntity(vha666Facility));
    facilityRepository.save(_facilityEntity(vha777Facility));
    // Setup facility services
    setupFacilityServices(
        vha666FacilityId, linkerUrl, vha666Facility.attributes().services().benefits());
    setupFacilityServices(
        vha666FacilityId, linkerUrl, vha666Facility.attributes().services().health());
    setupFacilityServices(
        vha666FacilityId, linkerUrl, vha666Facility.attributes().services().other());
    setupFacilityServices(
        vha777FacilityId, linkerUrl, vha777Facility.attributes().services().benefits());
    setupFacilityServices(
        vha777FacilityId, linkerUrl, vha777Facility.attributes().services().health());
    setupFacilityServices(
        vha777FacilityId, linkerUrl, vha777Facility.attributes().services().other());
    // Setup drive time band
    driveTimeBandRepository.save(_deprecatedPssgDriveTimeBandEntity(_diamondBand("666", 0, 10, 0)));
    driveTimeBandRepository.save(
        _deprecatedPssgDriveTimeBandEntity(_diamondBand("777", 80, 90, 5)));
    NearbyResponse response =
        _controller().nearbyLatLong(BigDecimal.ZERO, BigDecimal.ZERO, null, null);
    assertThat(response).isEqualTo(hitVha666());
  }

  @BeforeEach
  void setup() {
    mockRestTemplate = mock(RestTemplate.class);
  }

  private <T extends TypedService> void setupFacilityServices(
      @NonNull String facilityId,
      @NonNull String linkerUrl,
      List<DatamartFacility.Service<T>> facilityServices) {
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
