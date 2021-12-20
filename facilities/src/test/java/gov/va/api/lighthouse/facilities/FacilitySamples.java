package gov.va.api.lighthouse.facilities;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.Builder;
import lombok.SneakyThrows;

public class FacilitySamples {
  private final Map<String, gov.va.api.lighthouse.facilities.api.v0.Facility> facilities;

  private final Map<String, gov.va.api.lighthouse.facilities.api.v1.Facility> facilitiesV1;

  @SneakyThrows
  @Builder
  FacilitySamples(List<String> resources) {
    var mapper = FacilitiesJacksonConfigV0.createMapper();
    var mapperV1 = FacilitiesJacksonConfigV1.createMapper();
    facilities =
        resources.stream()
            .map(r -> getClass().getResourceAsStream(r))
            .map(
                in ->
                    FacilitiesJacksonConfigV0.quietlyMap(
                        mapper,
                        in,
                        gov.va.api.lighthouse.facilities.api.v0.FacilityReadResponse.class))
            .map(gov.va.api.lighthouse.facilities.api.v0.FacilityReadResponse::facility)
            .collect(
                toMap(gov.va.api.lighthouse.facilities.api.v0.Facility::id, Function.identity()));
    facilitiesV1 =
        resources.stream()
            .map(r -> getClass().getResourceAsStream(r))
            .map(
                in ->
                    FacilitiesJacksonConfigV1.quietlyMap(
                        mapperV1,
                        in,
                        gov.va.api.lighthouse.facilities.api.v1.FacilityReadResponse.class))
            .map(gov.va.api.lighthouse.facilities.api.v1.FacilityReadResponse::facility)
            .collect(
                toMap(gov.va.api.lighthouse.facilities.api.v1.Facility::id, Function.identity()));
  }

  static FacilitySamples defaultSamples() {
    return FacilitySamples.builder()
        .resources(List.of("/vha_691GB.json", "/vha_740GA.json", "/vha_757.json"))
        .build();
  }

  gov.va.api.lighthouse.facilities.api.v0.Facility facility(String id) {
    var f = facilities.get(id);
    assertThat(f).describedAs(id).isNotNull();
    return f;
  }

  FacilityEntity facilityEntity(String id) {
    return InternalFacilitiesController.populate(
        FacilityEntity.builder()
            .id(FacilityEntity.Pk.fromIdString(id))
            .lastUpdated(Instant.now())
            .build(),
        FacilityTransformerV0.toVersionAgnostic(facility(id)));
  }

  gov.va.api.lighthouse.facilities.api.v1.Facility facilityV1(String id) {
    var fV1 = facilitiesV1.get(id);
    assertThat(fV1).describedAs(id).isNotNull();
    return fV1;
  }

  gov.va.api.lighthouse.facilities.api.v0.GeoFacility geoFacility(String id) {
    var f = facilities.get(id);
    assertThat(f).describedAs(id).isNotNull();
    return GeoFacilityTransformerV0.builder().facility(f).build().toGeoFacility();
  }

  gov.va.api.lighthouse.facilities.api.v1.GeoFacility geoFacilityV1(String id) {
    var fV1 = facilitiesV1.get(id);
    assertThat(fV1).describedAs(id).isNotNull();
    return GeoFacilityTransformerV1.builder().facility(fV1).build().toGeoFacility();
  }
}
