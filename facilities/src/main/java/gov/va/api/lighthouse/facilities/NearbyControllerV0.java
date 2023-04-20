package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.ControllersV0.validateServices;
import static gov.va.api.lighthouse.facilities.DatamartFacilitiesJacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.NearbyUtils.NearbyId;
import static gov.va.api.lighthouse.facilities.NearbyUtils.intersections;
import static gov.va.api.lighthouse.facilities.NearbyUtils.validateDriveTime;
import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import gov.va.api.lighthouse.facilities.DatamartFacility.HealthService;
import gov.va.api.lighthouse.facilities.DatamartFacility.Service.Source;
import gov.va.api.lighthouse.facilities.api.ServiceType;
import gov.va.api.lighthouse.facilities.api.v0.NearbyResponse;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(value = "/v0/nearby")
@Slf4j
public class NearbyControllerV0 {
  private static final ObjectMapper DATAMART_MAPPER = createMapper();

  private final FacilityRepository facilityRepository;

  private final DriveTimeBandRepository driveTimeBandRepository;

  private final List<String> serviceSources;

  @Builder
  NearbyControllerV0(
      @Autowired FacilityRepository facilityRepository,
      @Autowired DriveTimeBandRepository driveTimeBandRepository) {
    this.facilityRepository = facilityRepository;
    this.driveTimeBandRepository = driveTimeBandRepository;
    this.serviceSources =
        List.of(
            Source.ATC.toString(),
            Source.DST.toString(),
            Source.BISL.toString(),
            Source.internal.toString());
  }

  private Set<String> buildServiceFilterStrings(Set<ServiceType> services) {
    Set<String> serviceStrings = new HashSet<>();
    services.stream()
        .forEach(
            serviceType -> {
              try {
                if (serviceType.serviceId().equals(HealthService.Covid19Vaccine.serviceId())) {
                  String service =
                      DATAMART_MAPPER.writeValueAsString(
                          DatamartFacility.Service.builder()
                              .serviceId(serviceType.serviceId())
                              .name(serviceType.name())
                              .source(Source.CMS)
                              .build());
                  serviceStrings.add(service);
                } else {
                  serviceSources.stream()
                      .forEach(
                          source -> {
                            try {
                              String service =
                                  DATAMART_MAPPER.writeValueAsString(
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
                }
              } catch (final JsonProcessingException ex) {
                throw new RuntimeException(ex);
              }
            });
    return serviceStrings;
  }

  private String getMonthYearFromBandIds(List<NearbyId> ids) {
    String monthYear;
    if (!ids.isEmpty() && driveTimeBandRepository.findById(ids.get(0).bandId).isPresent()) {
      monthYear = driveTimeBandRepository.findById(ids.get(0).bandId).get().monthYear();
    } else {
      monthYear = driveTimeBandRepository.getDefaultBandVersion();
    }
    if (monthYear == null) {
      monthYear = "Unknown";
    }
    return monthYear;
  }

  /** Nearby facilities by address - No longer supported. */
  @GetMapping(
      produces = "application/json",
      params = {"street_address", "city", "state", "zip", "!lat", "!lng"})
  void nearbyAddress(
      @RequestParam(value = "street_address") String street,
      @RequestParam(value = "city") String city,
      @RequestParam(value = "state") String state,
      @RequestParam(value = "zip") String zip,
      @RequestParam(value = "services[]", required = false) List<String> services,
      @RequestParam(value = "drive_time", required = false) Integer maxDriveTime) {
    throw new ExceptionsUtilsV0.BingException(
        "Search by address is not supported, please use [lat,lng]");
  }

  private NearbyResponse.Nearby nearbyFacility(@NonNull NearbyId entity) {
    return NearbyResponse.Nearby.builder()
        .id(entity.facilityId())
        .type(NearbyResponse.Type.NearbyFacility)
        .attributes(
            NearbyResponse.NearbyAttributes.builder()
                .minTime(entity.bandId().fromMinutes())
                .maxTime(entity.bandId().toMinutes())
                .build())
        .build();
  }

  @SneakyThrows
  private List<NearbyId> nearbyIds(
      @NonNull BigDecimal longitude,
      @NonNull BigDecimal latitude,
      List<String> rawServices,
      Integer rawMaxDriveTime) {
    Set<ServiceType> services = validateServices(rawServices);
    Set<String> serviceStrings = buildServiceFilterStrings(services);
    Integer maxDriveTime = validateDriveTime(rawMaxDriveTime);
    log.info(
        "Searching near {},{} within {} minutes with {} services",
        longitude.doubleValue(),
        latitude.doubleValue(),
        maxDriveTime,
        services.size());
    var timer = Stopwatch.createStarted();
    List<DriveTimeBandEntity> maybeBands =
        driveTimeBandRepository.findAll(
            DriveTimeBandRepository.MinMaxSpecification.builder()
                .longitude(longitude)
                .latitude(latitude)
                .maxDriveTime(maxDriveTime)
                .build());
    log.info("{} bands found in {} ms", maybeBands.size(), timer.elapsed(TimeUnit.MILLISECONDS));
    Map<String, DriveTimeBandEntity> bandsByStation =
        intersections(longitude, latitude, maybeBands);
    List<FacilityEntity> facilityEntities =
        facilityRepository.findAll(
            FacilityRepository.StationNumbersSpecification.builder()
                .stationNumbers(bandsByStation.keySet())
                .facilityType(FacilityEntity.Type.vha)
                .services(serviceStrings)
                .build());
    return facilityEntities.stream()
        .map(
            e ->
                NearbyId.builder()
                    .bandId(bandsByStation.get(e.id().stationNumber()).id())
                    .facilityId(e.id().toIdString())
                    .build())
        .sorted(Comparator.comparingInt(left -> left.bandId().toMinutes()))
        .collect(toList());
  }

  /** Nearby facilities by coordinates. */
  @GetMapping(
      produces = "application/json",
      params = {"lat", "lng"})
  NearbyResponse nearbyLatLong(
      @RequestParam(value = "lat") BigDecimal latitude,
      @RequestParam(value = "lng") BigDecimal longitude,
      @RequestParam(value = "services[]", required = false) List<String> services,
      @RequestParam(value = "drive_time", required = false) Integer maxDriveTime) {
    List<NearbyId> ids = nearbyIds(longitude, latitude, services, maxDriveTime);
    return NearbyResponse.builder()
        .data(ids.stream().map(this::nearbyFacility).collect(toList()))
        .meta(NearbyResponse.Meta.builder().bandVersion(getMonthYearFromBandIds(ids)).build())
        .build();
  }
}
