package gov.va.api.lighthouse.facilities;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;
import static gov.va.api.lighthouse.facilities.DatamartDetailedService.INVALID_SVC_ID;
import static gov.va.api.lighthouse.facilities.DatamartFacility.FacilityType.va_benefits_facility;
import static gov.va.api.lighthouse.facilities.DatamartFacility.FacilityType.va_cemetery;
import static gov.va.api.lighthouse.facilities.DatamartFacility.FacilityType.va_health_facility;
import static gov.va.api.lighthouse.facilities.DatamartFacility.FacilityType.vet_center;
import static gov.va.api.lighthouse.facilities.collector.Transformers.allBlank;
import static gov.va.api.lighthouse.facilities.collector.Transformers.isBlank;
import static java.util.stream.Collectors.toCollection;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import gov.va.api.health.autoconfig.logging.Loggable;
import gov.va.api.lighthouse.facilities.DatamartFacility.Address;
import gov.va.api.lighthouse.facilities.DatamartFacility.Addresses;
import gov.va.api.lighthouse.facilities.DatamartFacility.FacilityAttributes;
import gov.va.api.lighthouse.facilities.DatamartFacility.Services;
import gov.va.api.lighthouse.facilities.api.ServiceType;
import gov.va.api.lighthouse.facilities.api.v0.ReloadResponse;
import gov.va.api.lighthouse.facilities.collector.FacilitiesCollector;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@Validated
@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "/internal/management", produces = "application/json")
public class InternalFacilitiesController {
  static final String SPECIAL_INSTRUCTION_OLD_1 =
      "Expanded or Nontraditional hours are available for some services on a routine and "
          + "or requested basis. Please call our main phone number for details.";

  static final String SPECIAL_INSTRUCTION_UPDATED_1 =
      "More hours are available for some services. To learn more, call our main phone number.";

  static final String SPECIAL_INSTRUCTION_OLD_2 =
      "Vet Center after hours assistance is "
          + "available by calling 1-877-WAR-VETS (1-877-927-8387).";

  static final String SPECIAL_INSTRUCTION_UPDATED_2 =
      "If you need to talk to someone "
          + "or get advice right away, call the Vet Center anytime at 1-877-WAR-VETS "
          + "(1-877-927-8387).";

  static final String SPECIAL_INSTRUCTION_OLD_3 =
      "Administrative hours are Monday-Friday 8:00 a.m. to 4:30 p.m.";

  static final String SPECIAL_INSTRUCTION_UPDATED_3 =
      "Normal business hours are Monday through Friday, 8:00 a.m. to 4:30 p.m.";

  private static final String ZIP_REGEX = "^[0-9]{5}(-[0-9]{4})?$";

  private static final Pattern ZIP_PATTERN = Pattern.compile(ZIP_REGEX);

  private static final ObjectMapper DATAMART_MAPPER =
      DatamartFacilitiesJacksonConfig.createMapper();

  private final FacilitiesCollector collector;

  private final CmsOverlayRepository cmsOverlayRepository;

  private final FacilityRepository facilityRepository;

  // Max distance in miles where two facilities are considered to be duplicates
  private final Double duplicateFacilityOverlapRange = 0.02;

  private final Set<FacilityEntity> facilityEntities = new HashSet<>();

  private static Optional<Address> addressMailing(DatamartFacility datamartFacility) {
    return addresses(datamartFacility).map(a -> a.mailing());
  }

  private static Optional<Address> addressPhysical(DatamartFacility datamartFacility) {
    return addresses(datamartFacility).map(a -> a.physical());
  }

  private static Optional<Addresses> addresses(DatamartFacility datamartFacility) {
    return attributes(datamartFacility).map(a -> a.address());
  }

  private static Optional<FacilityAttributes> attributes(DatamartFacility datamartFacility) {
    return Optional.ofNullable(datamartFacility.attributes());
  }

  private static boolean isHoursNull(DatamartFacility datamartFacility) {
    return datamartFacility.attributes().hours() == null;
  }

  private static Boolean isMobileCenter(FacilityEntity facility) {
    return Optional.ofNullable(facility.mobile()).orElse(false);
  }

  /** Populate the given record with facility data _EXCEPT_ of the PK. */
  @SneakyThrows
  static FacilityEntity populate(FacilityEntity record, DatamartFacility datamartFacility) {
    checkArgument(record.id() != null);
    record.latitude(datamartFacility.attributes().latitude().doubleValue());
    record.longitude(datamartFacility.attributes().longitude().doubleValue());
    record.state(stateOf(datamartFacility));
    record.zip(zipOf(datamartFacility));
    record.servicesFromServiceTypes(serviceTypesOf(datamartFacility));
    record.facility(DATAMART_MAPPER.writeValueAsString(datamartFacility));
    record.visn(datamartFacility.attributes().visn());
    record.mobile(datamartFacility.attributes().mobile());
    return record;
  }

  /**
   * Determine the total collection of service types by combining health, benefits, and other
   * services types. This is guaranteed to return a non-null, but potentially empty collection.
   */
  static Set<ServiceType> serviceTypesOf(DatamartFacility datamartFacility) {
    var services = datamartFacility.attributes().services();
    if (services == null) {
      return Set.of();
    }
    var allServices = new HashSet<ServiceType>();
    if (services.health() != null) {
      allServices.addAll(services.health());
    }
    if (services.benefits() != null) {
      allServices.addAll(services.benefits());
    }
    if (services.other() != null) {
      allServices.addAll(services.other());
    }
    return allServices;
  }

  private static Optional<Services> services(DatamartFacility datamartFacility) {
    return attributes(datamartFacility).map(a -> a.services());
  }

  /** Determine the state if available in a physical address, otherwise return null. */
  static String stateOf(DatamartFacility datamartFacility) {
    if (datamartFacility.attributes().address() != null
        && datamartFacility.attributes().address().physical() != null
        && isNotBlank(datamartFacility.attributes().address().physical().state())) {
      return datamartFacility.attributes().address().physical().state();
    }
    return null;
  }

  /** Determine the 5 digit zip if available in a physical address, otherwise return null. */
  static String zipOf(DatamartFacility datamartFacility) {
    if (datamartFacility.attributes().address() != null
        && datamartFacility.attributes().address().physical() != null
        && isNotBlank(datamartFacility.attributes().address().physical().zip())) {
      /* We only store the destination portion of the zip code, we do not store the route. */
      return datamartFacility
          .attributes()
          .address()
          .physical()
          .zip()
          .substring(
              0, Math.min(5, datamartFacility.attributes().address().physical().zip().length()));
    }
    return null;
  }

  /** Check for invalid service ids in existing CMS overlays. */
  @GetMapping(value = "/facilities/detailedServices/overlay/check-for-invalid-service-ids")
  ResponseEntity<String> checkForInvalidServiceIdsInCmsOverlay() {
    boolean noErrors = true;
    StringBuilder responseBody = new StringBuilder();
    try {
      log.info("Checking service ids of all overlay detailed services.");
      Streams.stream(cmsOverlayRepository.findAll())
          .parallel()
          .forEach(
              c -> {
                String responseMsg;
                try {
                  List<DatamartDetailedService> updatedCmsServices =
                      CmsOverlayHelper.getDetailedServices(c.cmsServices());
                  if (containsInvalidServiceId(updatedCmsServices)) {
                    String serviceNamesWithInvalidServiceIds =
                        updatedCmsServices.stream()
                            .filter(ds -> ds.serviceId().equals(INVALID_SVC_ID))
                            .map(ds -> ds.name())
                            .collect(Collectors.joining(", "));
                    responseMsg =
                        String.format(
                            "Invalid service id(s) found for %s existing detailed services in "
                                + "overlay with %s operating status code for the following "
                                + "service names: %s",
                            c.id().toIdString(),
                            CmsOverlayHelper.getOperatingStatus(c.cmsOperatingStatus()).code(),
                            serviceNamesWithInvalidServiceIds);
                    log.error(responseMsg);
                    responseBody.append(responseMsg).append("\n");
                  }
                } catch (Exception ex) {
                  log.error(
                      "Failed to check service id for detailed services associated with {}: {}",
                      c.id().toIdString(),
                      ex.getMessage());
                  responseBody
                      .append("Failed to check service id ")
                      .append("for detailed services associated with ")
                      .append(c.id().toIdString())
                      .append(":\n")
                      .append(ex.getMessage())
                      .append("\n");
                }
              });
    } catch (Exception e) {
      noErrors = false;
      log.error("Failed to check service id for all overlay detailed services: {}", e.getMessage());
      responseBody
          .append("Failed to check service id for all overlay detailed services:\n")
          .append(e.getMessage())
          .append("\n");
    }
    if (noErrors) {
      if (responseBody.isEmpty()) {
        responseBody
            .append("No failures to perform invalid service id check encountered.\n")
            .append("No invalid service ids identified for existing overlays.\n");
      }
      String msg = "Completed invalid service id check for all overlay detailed services!";
      log.info(msg);
      responseBody.append(msg).append("\n");
      return ResponseEntity.ok().body(responseBody.toString());
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody.toString());
  }

  /** Check for invalid service ids in facility attributes. */
  @GetMapping(value = "/facilities/detailedServices/check-for-invalid-service-ids")
  ResponseEntity<String> checkForInvalidServiceIdsInFacilityAttributes() {
    boolean noErrors = true;
    StringBuilder responseBody = new StringBuilder();
    try {
      log.info("Checking service ids of all facility detailed services.");
      Streams.stream(facilityRepository.findAll())
          .parallel()
          .forEach(
              f -> {
                String responseMsg;
                try {
                  DatamartFacility facility =
                      DATAMART_MAPPER.readValue(f.facility(), DatamartFacility.class);
                  DatamartFacility.OperatingStatus operatingStatus =
                      facility.attributes().operatingStatus();
                  List<DatamartDetailedService> facilityDetailedServices =
                      facility.attributes().detailedServices();
                  if (containsInvalidServiceId(facilityDetailedServices)) {
                    String serviceNamesWithInvalidServiceIds =
                        facilityDetailedServices.stream()
                            .filter(ds -> ds.serviceId().equals(INVALID_SVC_ID))
                            .map(ds -> ds.name())
                            .collect(Collectors.joining(", "));
                    responseMsg =
                        String.format(
                            "Invalid service id(s) found for %s existing detailed services in "
                                + "facility attributes with %s operating status code for the "
                                + "following service names: %s",
                            f.id().toIdString(),
                            operatingStatus.code,
                            serviceNamesWithInvalidServiceIds);
                    log.error(responseMsg);
                    responseBody.append(responseMsg).append("\n");
                  }
                } catch (Exception ex) {
                  log.error(
                      "Failed to check service id for detailed services associated with {}: {}",
                      f.id().toIdString(),
                      ex.getMessage());
                  responseBody
                      .append("Failed to check service id ")
                      .append("for detailed services associated with ")
                      .append(f.id().toIdString())
                      .append(":\n")
                      .append(ex.getMessage())
                      .append("\n");
                }
              });
    } catch (Exception e) {
      noErrors = false;
      log.error(
          "Failed to check service ids for all facility detailed services: {}", e.getMessage());
      responseBody
          .append("Failed to check service ids for all facility detailed services:\n")
          .append(e.getMessage())
          .append("\n");
    }
    if (noErrors) {
      if (responseBody.isEmpty()) {
        responseBody
            .append("No failures to perform invalid service id check encountered.\n")
            .append("No invalid service ids identified for existing facility attributes.\n");
      }
      String msg = "Completed invalid service id check for all facility detailed services!";
      log.info(msg);
      responseBody.append(msg).append("\n");
      return ResponseEntity.ok().body(responseBody.toString());
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody.toString());
  }

  private Optional<CmsOverlayEntity> cmsOverlayEntityById(String id) {
    FacilityEntity.Pk pk = null;
    try {
      pk = FacilityEntity.Pk.fromIdString(id);
    } catch (IllegalArgumentException ex) {
      return Optional.empty();
    }
    return cmsOverlayRepository.findById(pk);
  }

  private boolean containsInvalidServiceId(List<DatamartDetailedService> detailedServices) {
    return ObjectUtils.isNotEmpty(detailedServices)
        && detailedServices.stream()
            .parallel()
            .anyMatch(ds -> ds.serviceId().equals(INVALID_SVC_ID));
  }

  /**
   * Delete an overlay if thisNodeOnly is not specified or partial overlay identified by
   * thisNodeOnly.
   */
  @DeleteMapping(value = {"/facilities/{id}/cms-overlay", "/facilities/{id}/cms-overlay/{node}"})
  ResponseEntity<Void> deleteCmsOverlayById(
      @PathVariable("id") String id,
      @PathVariable(value = "node", required = false) String thisNodeOnly) {
    CmsOverlayEntity overlayEntity = cmsOverlayEntityById(id).orElse(null);
    if (overlayEntity == null) {
      log.info("CmsOverlay {} does not exist, ignoring request.", sanitize(id));
      return ResponseEntity.accepted().build();
    }
    if (thisNodeOnly == null) {
      log.info("Deleting cms overlay for id: {}", sanitize(id));
      overlayEntity.cmsOperatingStatus(null);
      overlayEntity.cmsServices(null);
    } else if (thisNodeOnly.equalsIgnoreCase("operating_status")) {
      if (overlayEntity.cmsOperatingStatus() == null) {
        log.info("CmsOverlay {} does not have an operating_status, ignoring request", sanitize(id));
        return ResponseEntity.accepted().build();
      }
      log.info("Deleting operating_status node from overlay for id: {}", sanitize(id));
      overlayEntity.cmsOperatingStatus(null);
    } else if (thisNodeOnly.equalsIgnoreCase("detailed_services")) {
      if (overlayEntity.cmsServices() == null) {
        log.info("CmsOverlay {} does not have detailed_services, ignoring request", sanitize(id));
        return ResponseEntity.accepted().build();
      }
      log.info("Deleting detailed_services node from overlay for id: {}", sanitize(id));
      overlayEntity.cmsServices(null);
    } else {
      log.info("CmsOverlay field {} does not exist.", sanitize(thisNodeOnly));
      throw new ExceptionsUtils.NotFound(thisNodeOnly);
    }
    if (overlayEntity.cmsOperatingStatus() == null && overlayEntity.cmsServices() == null) {
      cmsOverlayRepository.delete(overlayEntity);
    } else {
      cmsOverlayRepository.save(overlayEntity);
    }
    FacilityEntity facilityEntity = facilityEntityById(id).orElse(null);
    if (facilityEntity != null) {
      facilityEntity
          .cmsOperatingStatus(overlayEntity.cmsOperatingStatus())
          .cmsServices(overlayEntity.cmsServices());
      if (overlayEntity.cmsServices() == null) {
        facilityEntity.overlayServices(new HashSet<>());
      }
      facilityRepository.save(facilityEntity);
    }
    return ResponseEntity.ok().build();
  }

  @DeleteMapping(value = "/facilities/{id}")
  ResponseEntity<String> deleteFacilityById(@PathVariable("id") String id) {
    Optional<FacilityEntity> entity = facilityEntityById(id);
    if (entity.isEmpty()) {
      log.info("Facility {} does not exist, ignoring request.", sanitize(id));
      return ResponseEntity.accepted().build();
    }
    log.info("Deleting facility {}", sanitize(id));
    facilityRepository.delete(entity.get());
    return ResponseEntity.ok().build();
  }

  // Checks a facility to make sure it is not within 0.02 miles of another
  private List<String> detectDuplicateFacilities(FacilityEntity newFacility) {
    List<String> duplicateIds = new ArrayList<>();
    getAllFacilities().stream()
        .filter(f -> f.id().type() == newFacility.id().type())
        .filter(f -> !f.id().stationNumber().equals(newFacility.id().stationNumber()))
        .filter(f -> !isMobileCenter(f) && !isMobileCenter(newFacility))
        .filter(
            f ->
                FacilityUtils.haversine(newFacility, f.longitude(), f.latitude())
                    <= duplicateFacilityOverlapRange)
        .map(f -> f.id().toIdString())
        .forEachOrdered(duplicateIds::add);
    return duplicateIds;
  }

  private Optional<FacilityEntity> facilityEntityById(String id) {
    FacilityEntity.Pk pk = null;
    try {
      pk = FacilityEntity.Pk.fromIdString(id);
    } catch (IllegalArgumentException ex) {
      return Optional.empty();
    }
    return facilityRepository.findById(pk);
  }

  private String findAndReplaceOperationalHoursSpecialInstructions(String instructions) {
    if (instructions == null) {
      return null;
    } else {
      // Look through the instructions for specific substrings and replace them if necessary
      if (instructions.contains(SPECIAL_INSTRUCTION_OLD_1)) {
        instructions =
            instructions.replace(SPECIAL_INSTRUCTION_OLD_1, SPECIAL_INSTRUCTION_UPDATED_1);
      }
      if (instructions.contains(SPECIAL_INSTRUCTION_OLD_2)) {
        instructions =
            instructions.replace(SPECIAL_INSTRUCTION_OLD_2, SPECIAL_INSTRUCTION_UPDATED_2);
      }
      if (instructions.contains(SPECIAL_INSTRUCTION_OLD_3)) {
        instructions =
            instructions.replace(SPECIAL_INSTRUCTION_OLD_3, SPECIAL_INSTRUCTION_UPDATED_3);
      }
    }
    return instructions;
  }

  private Set<FacilityEntity> getAllFacilities() {
    if (facilityEntities.isEmpty()) {
      facilityRepository.findAll().forEach(facilityEntities::add);
    }
    return facilityEntities;
  }

  private Set<FacilityEntity.Pk> missingIds(List<DatamartFacility> collectedFacilities) {
    Set<FacilityEntity.Pk> newIds =
        collectedFacilities.stream()
            .map(df -> FacilityEntity.Pk.optionalFromIdString(df.id()).orElse(null))
            .filter(Objects::nonNull)
            .collect(toCollection(LinkedHashSet::new));
    Set<FacilityEntity.Pk> oldIds = new LinkedHashSet<>(facilityRepository.findAllIds());
    return ImmutableSet.copyOf(Sets.difference(oldIds, newIds));
  }

  @GetMapping(value = "/populate-cms-overlay-table")
  void populateCmsOverlayTable() {
    // parallel stream all facilities response
    // build entity for cms_overlay table
    // operating status AND/OR detailed services exist add to entity otherwise it will just be null
    // save entity
    // done after all processing completes
    boolean noErrors = true;
    try {
      log.warn("Attempting to save all facility overlay info to cms_overlay table.");
      Streams.stream(facilityRepository.findAll())
          .parallel()
          .filter(f -> f.cmsOperatingStatus() != null || f.cmsServices() != null)
          .forEach(
              f ->
                  cmsOverlayRepository.save(
                      CmsOverlayEntity.builder()
                          .id(f.id())
                          .cmsOperatingStatus(f.cmsOperatingStatus())
                          .cmsServices(f.cmsServices())
                          .build()));
    } catch (Exception e) {
      noErrors = false;
      log.error(
          "Failed to save all facility overlay info to cms_overlay table. {}", e.getMessage());
    }
    if (noErrors) {
      log.warn("Completed saving all facility overlay info to cms_overlay table!");
    }
  }

  private ResponseEntity<ReloadResponse> process(
      ReloadResponse response, List<DatamartFacility> collectedFacilities) {
    response.timing().markCompleteCollection();
    log.info("Facilities collected: {}", collectedFacilities.size());
    try {
      collectedFacilities.parallelStream().forEach(f -> updateFacility(response, f));
      for (FacilityEntity.Pk missingId : missingIds(collectedFacilities)) {
        processMissingFacility(response, missingId);
      }
    } catch (Exception e) {
      log.error("Failed to process facilities: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    } finally {
      response.timing().markComplete();
    }
    return ResponseEntity.ok(response);
  }

  @SneakyThrows
  private void processMissingFacility(ReloadResponse response, FacilityEntity.Pk id) {
    Optional<FacilityEntity> optEntity = facilityRepository.findById(id);
    checkState(optEntity.isPresent());
    FacilityEntity entity = optEntity.get();
    Instant now = response.timing().completeCollection();
    if (entity.missingTimestamp() == null) {
      entity.missingTimestamp(now.toEpochMilli());
    }
    if (now.toEpochMilli() - entity.missingTimestamp() <= TimeUnit.HOURS.toMillis(24)) {
      saveAsMissing(response, entity);
      return;
    }
    facilityRepository.delete(entity);
  }

  @GetMapping(value = "/reload")
  ResponseEntity<ReloadResponse> reload() {
    var response = ReloadResponse.start();
    var collectedFacilities = collector.collectFacilities();
    response.totalFacilities(collectedFacilities.size());
    return process(response, collectedFacilities);
  }

  @SneakyThrows
  private void saveAsMissing(ReloadResponse response, FacilityEntity entity) {
    FacilityEntity.Pk id = entity.id();
    try {
      response.facilitiesMissing().add(id.toIdString());
      log.warn("Marking facility {} as missing.", id.toIdString());
      facilityRepository.save(entity);
      return;
    } catch (Exception e) {
      log.error("Failed to mark facility {} as missing: {}", id.toIdString(), e.getMessage());
      response
          .problems()
          .add(
              ReloadResponse.Problem.of(
                  id.toIdString(), "Failed to mark facility as missing: " + e.getMessage()));
      throw e;
    }
  }

  /**
   * WARNING: This method will overwrite existing detailed services in CMS overlay with those
   * containing a matching serviceId found within facility attributes. Engineer discretion is
   * advised.
   */
  @GetMapping(value = "/facilities/detailedServices/transfer-to-cms-overlay-table")
  ResponseEntity<String> updateAllDetailedServiceIdAndTransferToCmsOverlay() {
    boolean noErrors = true;
    StringBuilder responseBody = new StringBuilder();
    try {
      log.warn(
          "Attempting to update and transfer all facility detailed services to cms_overlay table.");
      Streams.stream(facilityRepository.findAll())
          .parallel()
          .forEach(
              f ->
                  responseBody.append(
                      updateDetailedServiceIdAndTransferToCmsOverlay(f.facility())));
    } catch (Exception e) {
      noErrors = false;
      log.error(
          "Failed to update and transfer all facility detailed services to cms_overlay table: {}",
          e.getMessage());
      responseBody
          .append("Failed to update and transfer all facility detailed services ")
          .append("to cms_overlay table:\n")
          .append(e.getMessage())
          .append("\n");
    }
    if (noErrors) {
      String msg =
          "Completed update and transfer of all facility detailed services to cms_overlay table!";
      log.warn(msg);
      responseBody.append(msg).append("\n");
      return ResponseEntity.ok().body(responseBody.toString());
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody.toString());
  }

  @SneakyThrows
  void updateAndSave(
      ReloadResponse response, FacilityEntity record, DatamartFacility datamartFacility) {
    datamartFacility
        .attributes()
        .operationalHoursSpecialInstructions(
            findAndReplaceOperationalHoursSpecialInstructions(
                datamartFacility.attributes().operationalHoursSpecialInstructions()));
    populate(record, datamartFacility);
    record.missingTimestamp(null);
    record.lastUpdated(response.timing().completeCollection());
    /*
     * Determine if there is something wrong with the record, but it is still usable.
     */
    List<String> duplicateFacilities = detectDuplicateFacilities(record);
    Collections.sort(duplicateFacilities);
    if (!duplicateFacilities.isEmpty()) {
      response
          .problems()
          .add(
              ReloadResponse.Problem.of(
                  datamartFacility.id(),
                  "Duplicate Facilities",
                  String.join(";", duplicateFacilities)));
    }
    if (isBlank(record.zip()) || !ZIP_PATTERN.matcher(record.zip()).matches()) {
      response
          .problems()
          .add(
              ReloadResponse.Problem.of(
                  datamartFacility.id(), "Missing or invalid physical address zip"));
    }
    if (isBlank(record.state())) {
      response
          .problems()
          .add(ReloadResponse.Problem.of(datamartFacility.id(), "Missing physical address state"));
    }
    if (isBlank(addressPhysical(datamartFacility).map(a -> a.city()))) {
      response
          .problems()
          .add(ReloadResponse.Problem.of(datamartFacility.id(), "Missing physical address city"));
    }
    if (allBlank(
        addressPhysical(datamartFacility).map(a -> a.address1()),
        addressPhysical(datamartFacility).map(a -> a.address2()),
        addressPhysical(datamartFacility).map(a -> a.address3()))) {
      response
          .problems()
          .add(
              ReloadResponse.Problem.of(
                  datamartFacility.id(), "Missing physical address street information"));
    }
    // Mailing addresses only exist for cemeteries
    if (datamartFacility.attributes().facilityType() == va_cemetery) {
      if (isBlank(addressMailing(datamartFacility).map(a -> a.zip()))
          || !ZIP_PATTERN
              .matcher(datamartFacility.attributes().address().mailing().zip())
              .matches()) {
        response
            .problems()
            .add(
                ReloadResponse.Problem.of(
                    datamartFacility.id(), "Missing or invalid mailing address zip"));
      }
      if (isBlank(addressMailing(datamartFacility).map(a -> a.state()))) {
        response
            .problems()
            .add(ReloadResponse.Problem.of(datamartFacility.id(), "Missing mailing address state"));
      }
      if (isBlank(addressMailing(datamartFacility).map(a -> a.city()))) {
        response
            .problems()
            .add(ReloadResponse.Problem.of(datamartFacility.id(), "Missing mailing address city"));
      }
      if (allBlank(
          addressMailing(datamartFacility).map(a -> a.address1()),
          addressMailing(datamartFacility).map(a -> a.address2()),
          addressMailing(datamartFacility).map(a -> a.address3()))) {
        response
            .problems()
            .add(
                ReloadResponse.Problem.of(
                    datamartFacility.id(), "Missing mailing address street information"));
      }
    }
    if (datamartFacility.attributes().phone() == null
        || isBlank(datamartFacility.attributes().phone().main())) {
      response
          .problems()
          .add(ReloadResponse.Problem.of(datamartFacility.id(), "Missing main phone number"));
    }
    if (isHoursNull(datamartFacility) || isBlank(datamartFacility.attributes().hours().monday())) {
      response
          .problems()
          .add(ReloadResponse.Problem.of(datamartFacility.id(), "Missing hours Monday"));
    }
    if (isHoursNull(datamartFacility) || isBlank(datamartFacility.attributes().hours().tuesday())) {
      response
          .problems()
          .add(ReloadResponse.Problem.of(datamartFacility.id(), "Missing hours Tuesday"));
    }
    if (isHoursNull(datamartFacility)
        || isBlank(datamartFacility.attributes().hours().wednesday())) {
      response
          .problems()
          .add(ReloadResponse.Problem.of(datamartFacility.id(), "Missing hours Wednesday"));
    }
    if (isHoursNull(datamartFacility)
        || isBlank(datamartFacility.attributes().hours().thursday())) {
      response
          .problems()
          .add(ReloadResponse.Problem.of(datamartFacility.id(), "Missing hours Thursday"));
    }
    if (isHoursNull(datamartFacility) || isBlank(datamartFacility.attributes().hours().friday())) {
      response
          .problems()
          .add(ReloadResponse.Problem.of(datamartFacility.id(), "Missing hours Friday"));
    }
    if (isHoursNull(datamartFacility)
        || isBlank(datamartFacility.attributes().hours().saturday())) {
      response
          .problems()
          .add(ReloadResponse.Problem.of(datamartFacility.id(), "Missing hours Saturday"));
    }
    if (isHoursNull(datamartFacility) || isBlank(datamartFacility.attributes().hours().sunday())) {
      response
          .problems()
          .add(ReloadResponse.Problem.of(datamartFacility.id(), "Missing hours Sunday"));
    }
    // Currently classification is not populated for vet centers
    if (datamartFacility.attributes().facilityType() != vet_center
        && isBlank(datamartFacility.attributes().classification())) {
      response
          .problems()
          .add(ReloadResponse.Problem.of(datamartFacility.id(), "Missing classification"));
    }
    if (record.latitude() > 90 || record.latitude() < -90) {
      response
          .problems()
          .add(
              ReloadResponse.Problem.of(
                  datamartFacility.id(), "Missing or invalid location latitude"));
    }
    if (record.longitude() > 180 || record.longitude() < -180) {
      response
          .problems()
          .add(
              ReloadResponse.Problem.of(
                  datamartFacility.id(), "Missing or invalid location longitude"));
    }
    if ((datamartFacility.attributes().facilityType() == va_benefits_facility)
        && isBlank(services(datamartFacility).map(s -> s.benefits()))) {
      response.problems().add(ReloadResponse.Problem.of(datamartFacility.id(), "Missing services"));
    }
    if ((datamartFacility.attributes().facilityType() == va_health_facility)
        && isBlank(services(datamartFacility).map(s -> s.health()))) {
      response.problems().add(ReloadResponse.Problem.of(datamartFacility.id(), "Missing services"));
    }
    if ((datamartFacility.attributes().facilityType() == va_health_facility
            || datamartFacility.attributes().facilityType() == vet_center)
        && isBlank(record.visn())) {
      response.problems().add(ReloadResponse.Problem.of(datamartFacility.id(), "Missing VISN"));
    }
    try {
      facilityRepository.save(record);
    } catch (Exception e) {
      log.error("Failed to save facility record {}: {}", record.id(), e.getMessage());
      log.error("{}", record);
      response
          .problems()
          .add(
              ReloadResponse.Problem.of(
                  datamartFacility.id(), "Failed to save record: " + e.getMessage()));
      throw e;
    }
  }

  /**
   * WARNING: This method will overwrite existing detailed services in CMS overlay with those
   * containing a matching serviceId found within facility attributes. Engineer discretion is
   * advised.
   */
  @SneakyThrows
  private String updateDetailedServiceIdAndTransferToCmsOverlay(String facilityJson) {
    String responseMsg;
    StringBuilder responseBody = new StringBuilder();
    DatamartFacility df = DATAMART_MAPPER.readValue(facilityJson, DatamartFacility.class);
    DatamartFacility.OperatingStatus operatingStatus = df.attributes().operatingStatus();
    List<DatamartDetailedService> facilityDetailedServices = df.attributes().detailedServices();
    if (operatingStatus != null) {
      // Merge facility detailed services with those of existing overlay
      Set<DatamartDetailedService> mergedDetailedServices =
          new TreeSet<>(
              new Comparator<DatamartDetailedService>() {
                @Override
                public int compare(DatamartDetailedService dds1, DatamartDetailedService dds2) {
                  return dds1.serviceId().compareTo(dds2.serviceId());
                }
              });
      if (isNotEmpty(facilityDetailedServices)) {
        responseMsg =
            String.format(
                "Updating service id for detailed services associated with facility %s", df.id());
        log.warn(responseMsg);
        responseBody.append(responseMsg).append("\n");
        if (containsInvalidServiceId(facilityDetailedServices)) {
          responseMsg =
              String.format(
                  "Invalid service id(s) found for %s existing detailed services "
                      + "in facility attributes",
                  df.id());
          log.error(responseMsg);
          responseBody.append(responseMsg).append("\n");
        }
        mergedDetailedServices.addAll(facilityDetailedServices);
      }
      Optional<CmsOverlayEntity> existingOverlay =
          cmsOverlayRepository.findById(FacilityEntity.Pk.fromIdString(df.id()));
      if (existingOverlay.isPresent()) {
        List<DatamartDetailedService> updatedCmsServices =
            CmsOverlayHelper.getDetailedServices(existingOverlay.get().cmsServices());
        if (isNotEmpty(updatedCmsServices)) {
          responseMsg =
              String.format(
                  "Updating service id for existing %s CMS overlay detailed services", df.id());
          log.warn(responseMsg);
          responseBody.append(responseMsg).append("\n");
        }
        if (containsInvalidServiceId(updatedCmsServices)) {
          responseMsg =
              String.format(
                  "Invalid service id(s) found for %s existing CMS overlay detailed services",
                  df.id());
          log.error(responseMsg);
          responseBody.append(responseMsg).append("\n");
        }
        mergedDetailedServices.addAll(updatedCmsServices);
      }
      cmsOverlayRepository.save(
          CmsOverlayEntity.builder()
              .id(FacilityEntity.Pk.fromIdString(df.id()))
              .cmsOperatingStatus(DATAMART_MAPPER.writeValueAsString(operatingStatus))
              .cmsServices(
                  CmsOverlayHelper.serializeDetailedServices(
                      mergedDetailedServices.stream().toList()))
              .build());
    }
    return responseBody.toString();
  }

  private void updateFacility(ReloadResponse response, DatamartFacility datamartFacility) {
    FacilityEntity.Pk pk;
    try {
      pk = FacilityEntity.Pk.fromIdString(datamartFacility.id());
    } catch (IllegalArgumentException e) {
      log.error("Cannot process facility {}, ID not understood", datamartFacility.id(), e);
      response.problems().add(ReloadResponse.Problem.of(datamartFacility.id(), "Cannot parse ID"));
      return;
    }
    if (datamartFacility.attributes().latitude() == null
        || datamartFacility.attributes().longitude() == null) {
      log.error(
          "Cannot process facility {}, latitude and/or longitude is null", datamartFacility.id());
      response
          .problems()
          .add(ReloadResponse.Problem.of(datamartFacility.id(), "Missing coordinates"));
      return;
    }
    var existing = facilityRepository.findById(pk);
    if (existing.isPresent()) {
      response.facilitiesUpdated().add(datamartFacility.id());
      log.warn("Updating facility {}", datamartFacility.id());
      updateAndSave(response, existing.get(), datamartFacility);
      return;
    }
    response.facilitiesCreated().add(datamartFacility.id());
    log.warn("Creating new facility {}", datamartFacility.id());
    updateAndSave(response, FacilityEntity.builder().id(pk).build(), datamartFacility);
  }

  /** Update service id for existing CMS overlays. */
  @GetMapping(value = "/facilities/detailedServices/update-exisiting-overlay-service-ids")
  ResponseEntity<String> updateServiceIdForExistingCmsOverlays() {
    boolean noErrors = true;
    StringBuilder responseBody = new StringBuilder();
    try {
      log.warn(
          "Attempting to update service id for existing detailed services contained "
              + "in overlay.");
      Streams.stream(cmsOverlayRepository.findAll())
          .parallel()
          .forEach(
              c -> {
                String responseMsg;
                try {
                  if (StringUtils.isNotBlank(c.cmsServices())) {
                    responseMsg =
                        String.format(
                            "Updating service id for detailed services associated with overlay %s",
                            c.id().toIdString());
                    log.warn(responseMsg);
                    responseBody.append(responseMsg).append("\n");
                    List<DatamartDetailedService> updatedCmsServices =
                        CmsOverlayHelper.getDetailedServices(c.cmsServices());
                    if (containsInvalidServiceId(updatedCmsServices)) {
                      responseMsg =
                          String.format(
                              "Invalid service id(s) found for %s existing detailed services "
                                  + "in overlay",
                              c.id().toIdString());
                      log.error(responseMsg);
                      responseBody.append(responseMsg).append("\n");
                    }
                    cmsOverlayRepository.save(
                        c.cmsServices(
                            CmsOverlayHelper.serializeDetailedServices(updatedCmsServices)));
                  }
                } catch (Exception ex) {
                  log.error(
                      "Failed to update service id for {} existing detailed services "
                          + "in overlay: {}",
                      c.id().toIdString(),
                      ex.getMessage());
                  responseBody
                      .append("Failed to update service id for ")
                      .append(c.id().toIdString())
                      .append(" existing detailed services in overlay:\n")
                      .append(ex.getMessage())
                      .append("\n");
                }
              });
    } catch (Exception e) {
      noErrors = false;
      log.error(
          "Failed to update service id for existing detailed services in overlay: {}",
          e.getMessage());
      responseBody
          .append("Failed to update service id for existing detailed services in overlay:\n")
          .append(e.getMessage())
          .append("\n");
    }
    if (noErrors) {
      if (responseBody.isEmpty()) {
        responseBody.append(
            "No failures to perform service id update encountered for existing overlays.\n");
      }
      String msg = "Completed service id update for existing detailed services in overlay!";
      log.warn(msg);
      responseBody.append(msg).append("\n");
      return ResponseEntity.ok().body(responseBody.toString());
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody.toString());
  }

  @PostMapping(value = "/reload")
  @Loggable(arguments = false)
  ResponseEntity<ReloadResponse> upload(@RequestBody List<DatamartFacility> collectedFacilities) {
    var response = ReloadResponse.start();
    return process(response, collectedFacilities);
  }
}
