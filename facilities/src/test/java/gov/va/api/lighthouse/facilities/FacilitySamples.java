package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.DatamartFacilitiesJacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.FacilityServicesUtils.populate;
import static gov.va.api.lighthouse.facilities.FacilityTransformerV0.toVersionAgnosticFacilityBenefitsService;
import static gov.va.api.lighthouse.facilities.FacilityTransformerV0.toVersionAgnosticFacilityHealthService;
import static gov.va.api.lighthouse.facilities.FacilityTransformerV0.toVersionAgnosticFacilityOtherService;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.DatamartFacility.Service;
import gov.va.api.lighthouse.facilities.DatamartFacility.Service.Source;
import gov.va.api.lighthouse.facilities.api.ServiceType;
import gov.va.api.lighthouse.facilities.api.TypedService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;

public class FacilitySamples {

  private static final ObjectMapper DATAMART_MAPPER = createMapper();

  private final Map<String, gov.va.api.lighthouse.facilities.api.v0.Facility> facilities;

  private final Map<String, gov.va.api.lighthouse.facilities.api.v1.Facility> facilitiesV1;

  private final List<DatamartFacility> datamartFacilities;

  @SneakyThrows
  @Builder
  FacilitySamples(@NonNull List<String> resources, @NonNull String linkerUrl) {
    datamartFacilities =
        resources.stream()
            .map(r -> getClass().getResourceAsStream(r))
            .map(
                in ->
                    DatamartFacilitiesJacksonConfig.quietlyMap(
                        DATAMART_MAPPER, in, DatamartFacility.class))
            .collect(Collectors.toList());
    facilities =
        datamartFacilities.stream()
            .map(df -> FacilityTransformerV0.toFacility(df))
            .collect(
                Collectors.toMap(
                    gov.va.api.lighthouse.facilities.api.v0.Facility::id, Function.identity()));
    facilitiesV1 =
        datamartFacilities.stream()
            .map(
                df ->
                    FacilityTransformerV1.toFacility(
                        df, linkerUrl, List.of("ATC", "CMS", "DST", "internal", "BISL")))
            .collect(
                Collectors.toMap(
                    gov.va.api.lighthouse.facilities.api.v1.Facility::id, Function.identity()));
  }

  static FacilitySamples defaultSamples(@NonNull String linkerUrl) {
    return FacilitySamples.builder()
        .resources(List.of("/vha_691GB.json", "/vha_740GA.json", "/vha_757.json"))
        .linkerUrl(linkerUrl)
        .build();
  }

  gov.va.api.lighthouse.facilities.api.v0.Facility facility(String id) {
    var f = facilities.get(id);
    assertThat(f).describedAs(id).isNotNull();
    return f;
  }

  /** Obtain entity using V0 facility transformer. Parent id not populated. */
  FacilityEntity facilityEntity(@NonNull String id) {
    DatamartFacility df = FacilityTransformerV0.toVersionAgnostic(facility(id));
    df.attributes().services().health().stream().forEach(hs -> hs.source(Source.ATC));
    return InternalFacilitiesController.populate(
        FacilityEntity.builder()
            .id(FacilityEntity.Pk.fromIdString(id))
            .lastUpdated(Instant.now())
            .build(),
        df);
  }

  /** Obtain entity using V1 facility transformer, which will populate parent id. */
  FacilityEntity facilityEntityV1(String id) {
    DatamartFacility df = FacilityTransformerV1.toVersionAgnostic(facilityV1(id));
    df.attributes().services().health().stream().forEach(hs -> hs.source(Source.ATC));
    return InternalFacilitiesController.populate(
        FacilityEntity.builder()
            .id(FacilityEntity.Pk.fromIdString(id))
            .lastUpdated(Instant.now())
            .build(),
        df);
  }

  @SneakyThrows
  <U extends ServiceType, V extends TypedService> FacilityServicesEntity facilityServicesEntity(
      @NonNull String facilityId, @NonNull U facilityService) {
    return facilityServicesEntity(facilityId, facilityService, Source.ATC);
  }

  @SneakyThrows
  <U extends ServiceType, V extends TypedService> FacilityServicesEntity facilityServicesEntity(
      @NonNull String facilityId, @NonNull U facilityService, @NonNull Source source) {
    DatamartFacility.Service<? extends TypedService> datamartFacilityService =
        facilityService instanceof gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService
            ? DatamartFacility.Service.<DatamartFacility.HealthService>builder()
                .serviceType(
                    toVersionAgnosticFacilityHealthService(
                            (gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService)
                                facilityService)
                        .serviceType())
                .build()
            : facilityService
                    instanceof gov.va.api.lighthouse.facilities.api.v0.Facility.BenefitsService
                ? DatamartFacility.Service.<DatamartFacility.BenefitsService>builder()
                    .serviceType(
                        toVersionAgnosticFacilityBenefitsService(
                                (gov.va.api.lighthouse.facilities.api.v0.Facility.BenefitsService)
                                    facilityService)
                            .serviceType())
                    .build()
                : facilityService
                        instanceof gov.va.api.lighthouse.facilities.api.v0.Facility.OtherService
                    ? DatamartFacility.Service.<DatamartFacility.OtherService>builder()
                        .serviceType(
                            toVersionAgnosticFacilityOtherService(
                                    (gov.va.api.lighthouse.facilities.api.v0.Facility.OtherService)
                                        facilityService)
                                .serviceType())
                        .build()
                    : null;
    if (datamartFacilityService != null) {
      datamartFacilityService.serviceId(facilityService.serviceId());
      datamartFacilityService.source(source);
    }
    FacilityEntity facilityEntity =
        FacilityEntity.builder()
            .id(FacilityEntity.Pk.fromIdString(facilityId))
            .lastUpdated(Instant.now())
            .build();
    return populate(
        FacilityServicesEntity.builder()
            .id(
                FacilityServicesEntity.Pk.of(
                    facilityEntity.id().type(),
                    facilityId,
                    DATAMART_MAPPER.writeValueAsString(datamartFacilityService)))
            .build(),
        datamartFacilityService);
  }

  @SneakyThrows
  <V extends TypedService> FacilityServicesEntity facilityServicesEntity(
      @NonNull String facilityId, @NonNull Service<V> datamartHealthService) {
    FacilityEntity facilityEntity =
        FacilityEntity.builder()
            .id(FacilityEntity.Pk.fromIdString(facilityId))
            .lastUpdated(Instant.now())
            .build();
    return populate(
        FacilityServicesEntity.builder()
            .id(
                FacilityServicesEntity.Pk.of(
                    facilityEntity.id().type(),
                    facilityId,
                    DATAMART_MAPPER.writeValueAsString(datamartHealthService)))
            .build(),
        datamartHealthService);
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
}
