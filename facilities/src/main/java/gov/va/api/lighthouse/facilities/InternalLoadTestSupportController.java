package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.ControllersV0.validateFacilityType;
import static gov.va.api.lighthouse.facilities.FacilityEntity.Pk.fromIdString;
import static gov.va.api.lighthouse.facilities.NearbyUtils.Coordinates;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import com.google.common.collect.Streams;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@Validated
@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "/internal/management/loadtest/support")
public class InternalLoadTestSupportController {

  static final String CSV_NEARBY_ADDRESS = "randomNearbyAddresses.csv";

  static final String CSV_NEARBY_LATLON_FOR_FACILITY_TYPE = "randomNearbyLatLonForFacilityType.csv";

  static final String CSV_BBOX_FOR_FACILITY_TYPE = "randomBoundingBoxForFacilityType.csv";

  static final String CSV_DETAILED_SERVICE_FOR_FACILITY =
      "randomDetailedServiceIdForFacilities.csv";

  static final String CSV_FACILITY_ID_STRING_FOR_FACILITY_TYPE =
      "randomFacilityIdStringsForFacilityType.csv";

  static final String CSV_FACILITY_IDS = "randomFacilityIds.csv";

  static final String CSV_FACILITY_TYPES = "randomFacilityTypes.csv";

  static final String CSV_LATLON_FOR_FACILITY_TYPE = "randomLatLonsForFacilityType.csv";

  static final String CSV_STATES_FOR_FACILITY_TYPE = "randomStatesForFacilityType.csv";

  static final String CSV_VISN_FOR_FACILITY_TYPE = "randomVisnForFacilityType.csv";

  static final String CSV_ZIPCODES_FOR_FACILITY_TYPE = "randomZipCodesForFacilityType.csv";

  private static final String[] ALPHABET = {
    "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
    "t", "u", "v", "w", "x", "y", "z"
  };

  private static final List<Integer> DRIVE_TIME_VALUES =
      List.of(10, 20, 30, 40, 50, 60, 70, 80, 90);

  private static final String SPACE = "%20";

  private static final SecureRandom _randomNum = new SecureRandom();

  private FacilityRepository facilityRepository;

  private CmsOverlayRepository cmsOverlayRepository;

  private List<String> cities(int numIterations) {
    final List<String> randomCities = new ArrayList<>();
    for (int idx = 0; idx < numIterations; idx++) {
      randomCities.add(generateRandomWord());
    }
    return randomCities;
  }

  private List<DatamartDetailedService> detailedServicesForFacility(FacilityEntity.@NonNull Pk pk) {
    final Optional<CmsOverlayEntity> cmsEntity = cmsOverlayRepository.findById(pk);
    if (cmsEntity.isPresent()) {
      return CmsOverlayHelper.getDetailedServices(cmsEntity.get().cmsServices());
    }
    return Collections.emptyList();
  }

  /** Utility method for exporting random input to CSV. */
  @SneakyThrows
  private ResponseEntity<Resource> exportToCsv(
      @NonNull String[] csvHeader,
      @NonNull List<List<String>> csvBody,
      @NonNull String csvFilename) {
    ByteArrayInputStream byteArrayInputStream;
    try (ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVPrinter csvPrinter =
            new CSVPrinter(
                new PrintWriter(
                    new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))),
                CSVFormat.DEFAULT.withHeader(csvHeader).withIgnoreEmptyLines().withTrim())) {
      for (final List<String> record : csvBody) {
        csvPrinter.printRecord(record);
      }
      csvPrinter.flush();
      byteArrayInputStream = new ByteArrayInputStream(out.toByteArray());
    } catch (final IOException e) {
      throw new RuntimeException(e.getMessage());
    }
    final InputStreamResource fileInputStream = new InputStreamResource(byteArrayInputStream);
    final HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + csvFilename);
    headers.set(HttpHeaders.CONTENT_TYPE, "text/csv");
    return new ResponseEntity<>(fileInputStream, headers, HttpStatus.OK);
  }

  private List<Coordinates> facilityCoordinates(FacilityEntity.Type facilityType) {
    return Streams.stream(facilityRepository.findAll())
        .filter(entity -> facilityType == null || entity.id().type() == facilityType)
        .map(
            entity ->
                Coordinates.builder()
                    .latitude(BigDecimal.valueOf(entity.latitude()))
                    .longitude(BigDecimal.valueOf(entity.longitude()))
                    .build())
        .collect(Collectors.toList());
  }

  private List<String> facilityIds(FacilityEntity.Type facilityType) {
    return facilityRepository.findAllIds().stream()
        .filter(id -> facilityType == null || id.type() == facilityType)
        .map(id -> id.toIdString())
        .collect(toList());
  }

  private List<String> facilityTypes() {
    return Arrays.asList("benefits", "cemetery", "health", "vet_center");
  }

  private String generateRandomPhrase(int numWords) {
    final StringBuilder phrase = new StringBuilder();
    for (int idx = 0; idx < numWords; idx++) {
      phrase.append(generateRandomWord());
      if (idx < (numWords - 1)) {
        phrase.append(SPACE);
      }
    }
    return phrase.toString();
  }

  private String generateRandomWord() {
    final StringBuilder word = new StringBuilder();
    final int wordLen = _randomNum.nextInt(24) + 1;
    for (int idx = 0; idx < wordLen; idx++) {
      final String letter = ALPHABET[_randomNum.nextInt(ALPHABET.length - 1)];
      word.append(idx == 0 ? letter.toUpperCase() : letter);
    }
    return word.toString();
  }

  @GetMapping(value = "/nearby/address")
  ResponseEntity<Resource> nearbyAddress(@RequestParam(value = "iterations") int numIterations) {
    final List<String> streetAddresses = streetAddresses(numIterations);
    final List<String> cities = cities(numIterations);
    final List<String> states = states(null);
    final List<String> zipcodes = zipCodes(null);
    // Create list of random street addresses for number of iterations
    final List<List<String>> csvBody = new ArrayList<>();
    if (isNotEmpty(streetAddresses)
        && isNotEmpty(cities)
        && isNotEmpty(states)
        && isNotEmpty(zipcodes)) {
      for (; csvBody.size() < numIterations; ) {
        csvBody.add(
            Arrays.asList(
                streetAddresses.get(_randomNum.nextInt(streetAddresses.size())),
                cities.get(_randomNum.nextInt(cities.size())),
                states.get(_randomNum.nextInt(states.size())),
                zipcodes.get(_randomNum.nextInt(zipcodes.size())),
                Integer.toString(_randomNum.nextInt(100))));
      }
    }
    final String[] csvHeader = {"street_address", "city", "state", "zip", "drive_time"};
    return exportToCsv(csvHeader, csvBody, "randomNearbyAddresses.csv");
  }

  @GetMapping(value = "/nearby/latlon")
  ResponseEntity<Resource> nearbyLatLonForFacilityType(
      @RequestParam(value = "iterations") int numIterations,
      @RequestParam(value = "type", required = false) String type) {
    // Obtain facility coordinates for facility type
    final List<Coordinates> coordinates =
        facilityCoordinates(type != null ? validateFacilityType(type) : null);
    // Create list of random lat, lon, and drive_time for facility type and number of iterations
    final List<List<String>> csvBody = new ArrayList<>();
    if (isNotEmpty(coordinates)) {
      for (; csvBody.size() < numIterations; ) {
        // Export facility_type, lat, lon, drive_time as CSV
        final Coordinates coordinate = coordinates.get(_randomNum.nextInt(coordinates.size()));
        final BigDecimal randomOffset = BigDecimal.valueOf(_randomNum.nextDouble(2.0D));
        final int randomDriveTime =
            DRIVE_TIME_VALUES.get(_randomNum.nextInt(DRIVE_TIME_VALUES.size()));
        csvBody.add(
            Arrays.asList(
                type,
                coordinate.latitude().add(randomOffset).toPlainString(),
                coordinate.longitude().add(randomOffset).toPlainString(),
                Integer.toString(randomDriveTime)));
      }
    }
    final String[] csvHeader = {"facility_type", "lat", "lng", "drive_time"};
    return exportToCsv(csvHeader, csvBody, CSV_NEARBY_LATLON_FOR_FACILITY_TYPE);
  }

  /** Generate CSV of random bounding boxes for facility type and number of iterations. */
  @GetMapping(value = "/facility/bbox")
  ResponseEntity<Resource> randomBoundingBoxForFacilityType(
      @RequestParam(value = "iterations") int numIterations,
      @RequestParam(value = "type", required = false) String type) {
    // Obtain facility coordinates for facility type
    final List<Coordinates> coordinates =
        facilityCoordinates(type != null ? validateFacilityType(type) : null);
    // Create list of random bounding box bounds for facility type and number of iterations
    final List<List<String>> csvBody = new ArrayList<>();
    if (isNotEmpty(coordinates)) {
      for (; csvBody.size() < numIterations; ) {
        // Export facility_type, minLon, minLat, maxLon, maxLat as CSV
        final Coordinates coordinate = coordinates.get(_randomNum.nextInt(coordinates.size()));
        final BigDecimal randomOffset = BigDecimal.valueOf(_randomNum.nextDouble(2.5D));
        csvBody.add(
            Arrays.asList(
                type,
                coordinate.longitude().subtract(randomOffset).toPlainString(),
                coordinate.latitude().subtract(randomOffset).toPlainString(),
                coordinate.longitude().add(randomOffset).toPlainString(),
                coordinate.latitude().add(randomOffset).toPlainString()));
      }
    }
    final String[] csvHeader = {"facility_type", "minLon", "minLat", "maxLon", "maxLat"};
    return exportToCsv(csvHeader, csvBody, CSV_BBOX_FOR_FACILITY_TYPE);
  }

  /** Generate CSV of random detailed service IDs for facility types and number of iterations. */
  @GetMapping(value = "/facility/service")
  ResponseEntity<Resource> randomDetailedServiceForFacility(
      @RequestParam(value = "iterations") int numIterations,
      @RequestParam(value = "type", required = false) String type) {
    // Obtain list of all facility ids for type
    final List<String> facilityIds = facilityIds(type != null ? validateFacilityType(type) : null);
    // Create list of random service ids for facilities of specified type and number of iterations
    final List<List<String>> csvBody = new ArrayList<>();
    if (isNotEmpty(facilityIds)) {
      for (; csvBody.size() < numIterations; ) {
        // Obtain random facility
        final String facilityId = facilityIds.get(_randomNum.nextInt(facilityIds.size()));
        // Obtain random service ID for facility
        final List<DatamartDetailedService> detailedServices =
            detailedServicesForFacility(fromIdString(facilityId));
        if (!detailedServices.isEmpty()) {
          // Export facility_id, service_id as CSV
          csvBody.add(
              Arrays.asList(
                  facilityId,
                  detailedServices
                      .get(_randomNum.nextInt(detailedServices.size()))
                      .serviceInfo()
                      .serviceId()));
        }
      }
    }
    final String[] csvHeader = {"facility_id", "service_id"};
    return exportToCsv(csvHeader, csvBody, CSV_DETAILED_SERVICE_FOR_FACILITY);
  }

  /** Generate CSV of random facility ID strings for facility type and number of iterations. */
  @GetMapping(value = "/facility/ids")
  ResponseEntity<Resource> randomFacilityIdStringForFacilityType(
      @RequestParam(value = "iterations") int numIterations,
      @RequestParam(value = "type", required = false) String type) {
    // Obtain facility IDs for facility type
    final List<String> idsForType = facilityIds(type != null ? validateFacilityType(type) : null);
    // Create list of random facility ID strings for facility type and number of iterations
    final List<List<String>> csvBody = new ArrayList<>();
    if (isNotEmpty(idsForType)) {
      for (; csvBody.size() < numIterations; ) {
        // Export facility_type, facility_id as CSV
        final StringBuilder facilityIds = new StringBuilder();
        final int randomLen = _randomNum.nextInt(idsForType.size());
        for (; facilityIds.length() < randomLen; ) {
          facilityIds.append(idsForType.get(_randomNum.nextInt(idsForType.size())));
          if (facilityIds.length() < (randomLen - 1)) {
            facilityIds.append(",");
          }
        }
        csvBody.add(Arrays.asList(type, facilityIds.toString()));
      }
    }
    final String[] csvHeader = {"facility_type", "facility_ids"};
    return exportToCsv(csvHeader, csvBody, CSV_FACILITY_ID_STRING_FOR_FACILITY_TYPE);
  }

  /** Generate CSV of random facility ids. */
  @GetMapping(value = "/ids")
  ResponseEntity<Resource> randomFacilityIds(
      @RequestParam(value = "iterations") int numIterations,
      @RequestParam(value = "type", required = false) String type) {
    // Obtain list of all facility ids for type
    final List<String> facilityIds = facilityIds(type != null ? validateFacilityType(type) : null);
    // Create list of random facility ids for type and number of iterations
    final List<List<String>> csvBody = new ArrayList<>();
    if (isNotEmpty(facilityIds)) {
      for (int idx = 0; idx < numIterations; idx++) {
        csvBody.add(Arrays.asList(facilityIds.get(_randomNum.nextInt(facilityIds.size()))));
      }
    }
    final String[] csvHeader = {"facility_id"};
    return exportToCsv(csvHeader, csvBody, CSV_FACILITY_IDS);
  }

  /** Generate CSV of random facility types. */
  @GetMapping(value = "/types")
  ResponseEntity<Resource> randomFacilityTypes(
      @RequestParam(value = "iterations") int numIterations) {
    // Obtain list of all facility types
    final List<String> facilityTypes = facilityTypes();
    // Create list of random facility types for number of iterations
    final List<List<String>> csvBody = new ArrayList<>();
    if (isNotEmpty(facilityTypes)) {
      for (int idx = 0; idx < numIterations; idx++) {
        csvBody.add(Arrays.asList(facilityTypes.get(_randomNum.nextInt(facilityTypes.size()))));
      }
    }
    final String[] csvHeader = {"facility_type"};
    return exportToCsv(csvHeader, csvBody, CSV_FACILITY_TYPES);
  }

  /** Generate CSV of random facility coordinates for facility type and number of iterations. */
  @GetMapping(value = "/facility/latlon")
  ResponseEntity<Resource> randomLatLonForFacilityType(
      @RequestParam(value = "iterations") int numIterations,
      @RequestParam(value = "type", required = false) String type) {
    // Obtain facility coordinates for facility type
    final List<Coordinates> coordinates =
        facilityCoordinates(type != null ? validateFacilityType(type) : null);
    // Create list of random lat, lons, and radius for facility type and number of iterations
    final List<List<String>> csvBody = new ArrayList<>();
    if (isNotEmpty(coordinates)) {
      for (; csvBody.size() < numIterations; ) {
        // Export facility_type, lat, lon, radius as CSV
        final Coordinates coordinate = coordinates.get(_randomNum.nextInt(coordinates.size()));
        final BigDecimal randomOffset = BigDecimal.valueOf(_randomNum.nextDouble(2.0D));
        final BigDecimal randomRadius = BigDecimal.valueOf(_randomNum.nextDouble(100.0D));
        csvBody.add(
            Arrays.asList(
                type,
                coordinate.latitude().add(randomOffset).toPlainString(),
                coordinate.longitude().add(randomOffset).toPlainString(),
                randomRadius.toPlainString()));
      }
    }
    final String[] csvHeader = {"facility_type", "lat", "lon", "radius"};
    return exportToCsv(csvHeader, csvBody, CSV_LATLON_FOR_FACILITY_TYPE);
  }

  /** Generate CSV of random state abbreviations for facility type and number of iterations. */
  @GetMapping(value = "/facility/states")
  ResponseEntity<Resource> randomStatesForFacilityType(
      @RequestParam(value = "iterations") int numIterations,
      @RequestParam(value = "type", required = false) String type) {
    // Obtain states for facility type
    final List<String> statesForType = states(type != null ? validateFacilityType(type) : null);
    // Create list of random states for facility type and number of iterations
    final List<List<String>> csvBody = new ArrayList<>();
    if (isNotEmpty(statesForType)) {
      for (; csvBody.size() < numIterations; ) {
        // Export facility_type, state as CSV
        csvBody.add(
            Arrays.asList(type, statesForType.get(_randomNum.nextInt(statesForType.size()))));
      }
    }
    final String[] csvHeader = {"facility_type", "state"};
    return exportToCsv(csvHeader, csvBody, CSV_STATES_FOR_FACILITY_TYPE);
  }

  /** Generate CSV of random VISN values for facility type and number of iterations. */
  @GetMapping(value = "/facility/visn")
  ResponseEntity<Resource> randomVisnForFacilityType(
      @RequestParam(value = "iterations") int numIterations,
      @RequestParam(value = "type", required = false) String type) {
    // Obtain VISN values for facility type
    final List<String> visnForType = visn(type != null ? validateFacilityType(type) : null);
    // Create list of random VISN values for facility type and number of iterations
    final List<List<String>> csvBody = new ArrayList<>();
    if (isNotEmpty(visnForType)) {
      for (; csvBody.size() < numIterations; ) {
        // Export facility_type, VISN as CSV
        csvBody.add(Arrays.asList(type, visnForType.get(_randomNum.nextInt(visnForType.size()))));
      }
    }
    final String[] csvHeader = {"facility_type", "visn"};
    return exportToCsv(csvHeader, csvBody, CSV_VISN_FOR_FACILITY_TYPE);
  }

  /** Generate CSV of random zipcodes for facility type and number of iterations. */
  @GetMapping(value = "/facility/zip")
  ResponseEntity<Resource> randomZipCodesForFacilityType(
      @RequestParam(value = "iterations") int numIterations,
      @RequestParam(value = "type", required = false) String type) {
    // Obtain zip codes for facility type
    final List<String> zipcodesForType = zipCodes(type != null ? validateFacilityType(type) : null);
    // Create list of random zip codes for facility type and number of iterations
    final List<List<String>> csvBody = new ArrayList<>();
    if (isNotEmpty(zipcodesForType)) {
      for (; csvBody.size() < numIterations; ) {
        // Export facility_type, zip as CSV
        csvBody.add(
            Arrays.asList(type, zipcodesForType.get(_randomNum.nextInt(zipcodesForType.size()))));
      }
    }
    final String[] csvHeader = {"facility_type", "zip"};
    return exportToCsv(csvHeader, csvBody, CSV_ZIPCODES_FOR_FACILITY_TYPE);
  }

  private List<String> states(FacilityEntity.Type facilityType) {
    return Streams.stream(facilityRepository.findAll())
        .filter(entity -> facilityType == null || entity.id().type() == facilityType)
        .map(FacilityEntity::state)
        .collect(toList());
  }

  private String streetAddress() {
    final StringBuilder streetAddress = new StringBuilder();
    streetAddress.append(_randomNum.nextInt(10000) + 1999);
    streetAddress.append(SPACE);
    streetAddress.append(generateRandomPhrase(_randomNum.nextInt(9) + 1));
    return streetAddress.toString();
  }

  private List<String> streetAddresses(int numIterations) {
    final List<String> streetAddresses = new ArrayList<>();
    for (int idx = 0; idx < numIterations; idx++) {
      streetAddresses.add(streetAddress());
    }
    return streetAddresses;
  }

  private List<String> visn(FacilityEntity.Type facilityType) {
    return Streams.stream(facilityRepository.findAll())
        .filter(entity -> facilityType == null || entity.id().type() == facilityType)
        .map(FacilityEntity::visn)
        .collect(toList());
  }

  private List<String> zipCodes(FacilityEntity.Type facilityType) {
    return Streams.stream(facilityRepository.findAll())
        .filter(entity -> facilityType == null || entity.id().type() == facilityType)
        .map(FacilityEntity::zip)
        .collect(toList());
  }
}
