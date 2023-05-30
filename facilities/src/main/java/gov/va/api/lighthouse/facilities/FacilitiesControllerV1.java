package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.ControllersV1.convertToDatamartServices;
import static gov.va.api.lighthouse.facilities.ControllersV1.page;
import static gov.va.api.lighthouse.facilities.ControllersV1.validateBoundingBox;
import static gov.va.api.lighthouse.facilities.ControllersV1.validateFacilityType;
import static gov.va.api.lighthouse.facilities.ControllersV1.validateLatLong;
import static gov.va.api.lighthouse.facilities.ControllersV1.validateServices;
import static gov.va.api.lighthouse.facilities.FacilitiesJacksonConfigV1.createMapper;
import static gov.va.api.lighthouse.facilities.FacilityUtils.distance;
import static gov.va.api.lighthouse.facilities.FacilityUtils.haversine;
import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildLinkerUrlV1;
import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.DatamartFacility.Service.Source;
import gov.va.api.lighthouse.facilities.api.ServiceType;
import gov.va.api.lighthouse.facilities.api.v1.FacilitiesIdsResponse;
import gov.va.api.lighthouse.facilities.api.v1.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import gov.va.api.lighthouse.facilities.api.v1.FacilityReadResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(value = "/v1")
public class FacilitiesControllerV1 {

  private static final ObjectMapper MAPPER_V1 = createMapper();

  private static FacilityOverlayV1 FACILITY_OVERLAY = FacilityOverlayV1.builder().build();

  private final FacilityRepository facilityRepository;

  private final String linkerUrl;

  private final List<String> serviceSources;

  @Builder
  FacilitiesControllerV1(
      @Autowired FacilityRepository facilityRepository,
      @Value("${facilities.url}") String baseUrl,
      @Value("${facilities.base-path}") String basePath,
      @Value("${facility-services-source-v1:}#{T(java.util.Collections).emptyList()}")
          List<String> serviceSources) {
    this.facilityRepository = facilityRepository;
    linkerUrl = buildLinkerUrlV1(baseUrl, basePath);
    this.serviceSources =
        serviceSources.stream()
            .filter(s -> EnumUtils.isValidEnum(Source.class, s))
            .collect(Collectors.toList());
  }

  @SneakyThrows
  private static Facility facility(
      @NonNull HasFacilityPayload entity, @NonNull String linkerUrl, List<String> serviceSources) {
    return FACILITY_OVERLAY.apply(entity, linkerUrl, serviceSources);
  }

  private static FacilityRepository.BoundingBoxSpecification getBoundingBoxSpec(
      List<BigDecimal> bbox) {
    validateBoundingBox(bbox);
    return bbox == null
        ? null
        : FacilityRepository.BoundingBoxSpecification.builder()
            .minLongitude(bbox.get(0).min(bbox.get(2)))
            .maxLongitude(bbox.get(0).max(bbox.get(2)))
            .minLatitude(bbox.get(1).min(bbox.get(3)))
            .maxLatitude(bbox.get(1).max(bbox.get(3)))
            .build();
  }

  static FacilityRepository.FacilityTypeSpecification getFacilityTypeSpec(String type) {
    FacilityEntity.Type validatedType = validateFacilityType(type);
    return type == null
        ? null
        : FacilityRepository.FacilityTypeSpecification.builder()
            .facilityType(validatedType)
            .build();
  }

  private static FacilityRepository.TypeServicesIdsSpecification getIdsSpec(String ids) {
    List<FacilityEntity.Pk> validIds;
    validIds = FacilityUtils.entityIds(ids);
    return validIds.isEmpty()
        ? null
        : FacilityRepository.TypeServicesIdsSpecification.builder().ids(validIds).build();
  }

  static FacilityRepository.MobileSpecification getMobileSpec(Boolean mobile) {
    return mobile == null
        ? null
        : FacilityRepository.MobileSpecification.builder().mobile(mobile).build();
  }

  static FacilityRepository.StateSpecification getStateSpec(String state) {
    return state == null
        ? null
        : FacilityRepository.StateSpecification.builder().state(state).build();
  }

  static FacilityRepository.VisnSpecification getVisnSpec(String visn) {
    return visn == null ? null : FacilityRepository.VisnSpecification.builder().visn(visn).build();
  }

  static FacilityRepository.ZipSpecification getZipSpec(String zip) {
    return zip == null ? null : FacilityRepository.ZipSpecification.builder().zip(zip).build();
  }

  /** Get all facilities as CSV. */
  @SneakyThrows
  @GetMapping(value = "/facilities", produces = "text/csv")
  String allCsv() {
    List<List<String>> rows =
        facilityRepository.findAllProjectedBy().stream()
            .parallel()
            .map(
                e ->
                    CsvTransformerV1.builder()
                        .facility(facility(e, linkerUrl, serviceSources))
                        .build()
                        .toRow())
            .collect(toList());
    StringBuilder sb = new StringBuilder();
    try (CSVPrinter printer =
        CSVFormat.DEFAULT
            .withHeader(CsvTransformerV1.HEADERS.stream().toArray(String[]::new))
            .print(sb)) {
      for (List<String> row : rows) {
        printer.printRecord(row);
      }
      return sb.toString();
    }
  }

  private FacilityEntity entityById(String id) {
    FacilityEntity.Pk pk = null;
    try {
      pk = FacilityEntity.Pk.fromIdString(id);
    } catch (IllegalArgumentException ex) {
      throw new ExceptionsUtils.NotFound(id, ex);
    }
    Optional<FacilityEntity> opt = facilityRepository.findById(pk);
    if (opt.isEmpty()) {
      throw new ExceptionsUtils.NotFound(id);
    }
    return opt.get();
  }

  /** Get all facility IDs as a list by Type. */
  @GetMapping(
      value = "/ids",
      produces = {"application/json"})
  FacilitiesIdsResponse facilityIdsByType(
      @RequestParam(value = "type", required = false) String type) {
    FacilityEntity.Type facilityType = validateFacilityType(type);
    return FacilitiesIdsResponse.builder()
        .data(
            facilityRepository.findAllIds().stream()
                .filter(id -> facilityType == null || id.type() == facilityType)
                .map(id -> id.toIdString())
                .collect(toList()))
        .build();
  }

  private List<FacilityEntity> filterByBoundingBox(
      List<BigDecimal> bbox, List<FacilityEntity> allEntities) {
    double centerLng = (bbox.get(0).doubleValue() + bbox.get(2).doubleValue()) / 2;
    double centerLat = (bbox.get(1).doubleValue() + bbox.get(3).doubleValue()) / 2;
    return allEntities.stream()
        .sorted(
            (left, right) ->
                Double.compare(
                    distance(left, centerLng, centerLat), distance(right, centerLng, centerLat)))
        .collect(toList());
  }

  private List<DistanceEntity> filterByLatLong(
      BigDecimal latitude, BigDecimal longitude, BigDecimal rad, List<FacilityEntity> entities) {
    double lng = longitude.doubleValue();
    double lat = latitude.doubleValue();
    Optional<BigDecimal> radius = Optional.ofNullable(rad);
    return entities.stream()
        .map(
            e ->
                DistanceEntity.builder()
                    .entity(e)
                    .distance(BigDecimal.valueOf(haversine(e, lng, lat)))
                    .linkerUrl(linkerUrl)
                    .serviceSources(serviceSources)
                    .build())
        .filter(
            radius.isPresent()
                ? de -> radius.get().compareTo(de.distance().abs()) >= 0
                : de -> true)
        .sorted((left, right) -> left.distance().compareTo(right.distance()))
        .collect(toList());
  }

  @SneakyThrows
  private Set<String> getServices(List<String> rawServices) {
    Set<ServiceType> datamartServices = convertToDatamartServices(validateServices(rawServices));
    Set<String> serviceStrings = new HashSet<>();
    datamartServices.stream()
        .forEach(
            serviceType -> {
              serviceSources.stream()
                  .forEach(
                      source -> {
                        try {
                          String service =
                              MAPPER_V1.writeValueAsString(
                                  DatamartFacility.Service.builder()
                                      .serviceId(serviceType.serviceId())
                                      .name(serviceType.name())
                                      .source(Source.valueOf(source))
                                      .build());
                          serviceStrings.add(service);

                        } catch (final JsonProcessingException ex) {
                          throw new RuntimeException(ex);
                        }
                      });
            });

    return serviceStrings;
  }

  @SneakyThrows
  @GetMapping(
      value = "/facilities",
      produces = {"application/json"})
  FacilitiesResponse jsonFacilities(
      @RequestParam(value = "bbox[]", required = false) List<BigDecimal> bbox,
      @RequestParam(value = "state", required = false) String state,
      @RequestParam(value = "zip", required = false) String zip,
      @RequestParam(value = "type", required = false) String rawType,
      @RequestParam(value = "lat", required = false) BigDecimal latitude,
      @RequestParam(value = "long", required = false) BigDecimal longitude,
      @RequestParam(value = "radius", required = false) BigDecimal radius,
      @RequestParam(value = "facilityIds", required = false) String ids,
      @RequestParam(value = "services[]", required = false) List<String> rawServices,
      @RequestParam(value = "mobile", required = false) Boolean mobile,
      @RequestParam(value = "visn", required = false) String visn,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "per_page", defaultValue = "10") @Min(0) int perPage) {
    validateLatLong(latitude, longitude, radius);
    FacilityRepository.FacilitySpecificationHelper spec =
        FacilityRepository.FacilitySpecificationHelper.builder()
            .boundingBox(getBoundingBoxSpec(bbox))
            .state(getStateSpec(state))
            .zip(getZipSpec(zip))
            .facilityType(getFacilityTypeSpec(rawType))
            .ids(getIdsSpec(ids))
            .services(getServices(rawServices))
            .mobile(getMobileSpec(mobile))
            .visn(getVisnSpec(visn))
            .build();
    List<FacilityEntity> entities = facilityRepository.findAll(spec);
    List<FacilitiesResponse.Distance> distances = null;
    List<Facility> facilitiesPage;
    if (bbox == null && latitude != null && longitude != null) {
      List<DistanceEntity> entitiesPage =
          page(filterByLatLong(latitude, longitude, radius, entities), page, perPage);
      distances =
          entitiesPage.stream()
              .map(
                  e ->
                      FacilitiesResponse.Distance.builder()
                          .id(e.facility().id())
                          .distance(e.distance().setScale(2, RoundingMode.HALF_EVEN))
                          .build())
              .collect(toList());
      facilitiesPage =
          entitiesPage.parallelStream().map(DistanceEntity::facility).collect(toList());
    } else {
      if (bbox != null && latitude == null && longitude == null) {
        entities = filterByBoundingBox(bbox, entities);
      } else {
        entities.sort(Comparator.comparing(e -> e.id().toIdString()));
      }
      facilitiesPage =
          page(entities, page, perPage).parallelStream()
              .map(e -> facility(e, linkerUrl, serviceSources))
              .collect(toList());
    }
    PageLinkerV1 linker =
        PageLinkerV1.builder()
            .url(linkerUrl + "facilities")
            .params(
                Parameters.builder()
                    .addAll("bbox[]", bbox)
                    .addIgnoreNull("state", state)
                    .addIgnoreNull("zip", zip)
                    .addIgnoreNull("type", rawType)
                    .addIgnoreNull("lat", latitude)
                    .addIgnoreNull("long", longitude)
                    .addIgnoreNull("radius", radius)
                    .addIgnoreNull("facilityIds", ids)
                    .addAll("services[]", rawServices)
                    .addIgnoreNull("mobile", mobile)
                    .addIgnoreNull("visn", visn)
                    .addIgnoreNull("page", page)
                    .addIgnoreNull("per_page", perPage)
                    .build())
            .totalEntries(entities.size())
            .build();
    return FacilitiesResponse.builder()
        .data(facilitiesPage)
        .links(linker.links())
        .meta(
            FacilitiesResponse.FacilitiesMetadata.builder()
                .pagination(linker.pagination())
                .distances(distances)
                .build())
        .build();
  }

  /** Read facility. */
  @GetMapping(value = "/facilities/{facilityId}", produces = "application/json")
  FacilityReadResponse readJson(@PathVariable("facilityId") String id) {
    return FacilityReadResponse.builder()
        .facility(facility(entityById(id), linkerUrl, serviceSources))
        .build();
  }

  @Data
  @Builder
  private static final class DistanceEntity {
    @NonNull final FacilityEntity entity;

    final BigDecimal distance;

    @NonNull final String linkerUrl;

    List<String> serviceSources;

    Facility facility;

    Facility facility() {

      if (facility == null) {
        facility = FacilitiesControllerV1.facility(entity, linkerUrl, serviceSources);
      }
      return facility;
    }
  }
}
