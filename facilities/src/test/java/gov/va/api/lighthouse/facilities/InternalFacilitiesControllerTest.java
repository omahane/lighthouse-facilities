package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.DatamartFacility.FacilityType.va_health_facility;
import static gov.va.api.lighthouse.facilities.DatamartFacility.FacilityType.vet_center;
import static gov.va.api.lighthouse.facilities.InternalFacilitiesController.SPECIAL_INSTRUCTION_OLD_1;
import static gov.va.api.lighthouse.facilities.InternalFacilitiesController.SPECIAL_INSTRUCTION_OLD_2;
import static gov.va.api.lighthouse.facilities.InternalFacilitiesController.SPECIAL_INSTRUCTION_OLD_3;
import static gov.va.api.lighthouse.facilities.InternalFacilitiesController.SPECIAL_INSTRUCTION_UPDATED_1;
import static gov.va.api.lighthouse.facilities.InternalFacilitiesController.SPECIAL_INSTRUCTION_UPDATED_2;
import static gov.va.api.lighthouse.facilities.InternalFacilitiesController.SPECIAL_INSTRUCTION_UPDATED_3;
import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildLinkerUrlV1;
import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildTypedServiceLink;
import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.CMS_OVERLAY_SERVICE_NAME_COVID_19;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.DatamartFacility.Address;
import gov.va.api.lighthouse.facilities.DatamartFacility.Addresses;
import gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService;
import gov.va.api.lighthouse.facilities.DatamartFacility.FacilityAttributes;
import gov.va.api.lighthouse.facilities.DatamartFacility.FacilityType;
import gov.va.api.lighthouse.facilities.DatamartFacility.HealthService;
import gov.va.api.lighthouse.facilities.DatamartFacility.OtherService;
import gov.va.api.lighthouse.facilities.DatamartFacility.Service;
import gov.va.api.lighthouse.facilities.DatamartFacility.Services;
import gov.va.api.lighthouse.facilities.api.TypedService;
import gov.va.api.lighthouse.facilities.api.v0.ReloadResponse;
import gov.va.api.lighthouse.facilities.collector.AccessToCareCollector;
import gov.va.api.lighthouse.facilities.collector.AccessToPwtCollector;
import gov.va.api.lighthouse.facilities.collector.CmsOverlayCollector;
import gov.va.api.lighthouse.facilities.collector.FacilitiesCollector;
import gov.va.api.lighthouse.facilities.collector.InsecureRestTemplateProvider;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class InternalFacilitiesControllerTest {
  @Autowired FacilityRepository facilityRepository;

  @Autowired FacilityServicesRepository facilityServicesRepository;

  @Autowired CmsOverlayRepository overlayRepository;

  FacilitiesCollector mockCollector;

  CmsOverlayCollector mockCmsOverlayCollector;

  private static DatamartCmsOverlay.Core _core() {
    return DatamartCmsOverlay.Core.builder()
        .facilityUrl("https://www.va.gov/phoenix-health-care/locations/payson-va-clinic")
        .build();
  }

  private static DatamartFacility _facility(
      String id,
      String state,
      String zip,
      double latitude,
      double longitude,
      List<gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService> health) {
    return FacilityTransformerV0.toVersionAgnostic(
        gov.va.api.lighthouse.facilities.api.v0.Facility.builder()
            .id(id)
            .attributes(
                gov.va.api.lighthouse.facilities.api.v0.Facility.FacilityAttributes.builder()
                    .address(
                        gov.va.api.lighthouse.facilities.api.v0.Facility.Addresses.builder()
                            .physical(
                                gov.va.api.lighthouse.facilities.api.v0.Facility.Address.builder()
                                    .state(state)
                                    .zip(zip)
                                    .build())
                            .build())
                    .latitude(BigDecimal.valueOf(latitude))
                    .longitude(BigDecimal.valueOf(longitude))
                    .services(
                        gov.va.api.lighthouse.facilities.api.v0.Facility.Services.builder()
                            .health(health)
                            .build())
                    .mobile(false)
                    .facilityType(
                        gov.va.api.lighthouse.facilities.api.v0.Facility.FacilityType.va_cemetery)
                    .build())
            .build());
  }

  private static DatamartFacility _facilityV1(
      String id,
      String state,
      String zip,
      double latitude,
      double longitude,
      List<
              gov.va.api.lighthouse.facilities.api.v1.Facility.Service<
                  gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService>>
          health) {
    return FacilityTransformerV1.toVersionAgnostic(
        gov.va.api.lighthouse.facilities.api.v1.Facility.builder()
            .id(id)
            .attributes(
                gov.va.api.lighthouse.facilities.api.v1.Facility.FacilityAttributes.builder()
                    .address(
                        gov.va.api.lighthouse.facilities.api.v1.Facility.Addresses.builder()
                            .physical(
                                gov.va.api.lighthouse.facilities.api.v1.Facility.Address.builder()
                                    .state(state)
                                    .zip(zip)
                                    .build())
                            .build())
                    .latitude(BigDecimal.valueOf(latitude))
                    .longitude(BigDecimal.valueOf(longitude))
                    .services(
                        gov.va.api.lighthouse.facilities.api.v1.Facility.Services.builder()
                            .health(health)
                            .build())
                    .mobile(false)
                    .facilityType(
                        gov.va.api.lighthouse.facilities.api.v1.Facility.FacilityType.va_cemetery)
                    .build())
            .build());
  }

  private static DatamartCmsOverlay _overlay() {
    return DatamartCmsOverlay.builder()
        .core(_core())
        .operatingStatus(_overlay_operating_status())
        .detailedServices(_overlay_detailed_services())
        .healthCareSystem(_overlay_health_care_system())
        .build();
  }

  private static List<DatamartDetailedService> _overlay_detailed_services() {
    return List.of(_overlay_detailed_services(HealthService.Covid19Vaccine, true));
  }

  private static DatamartDetailedService _overlay_detailed_services(
      @NonNull HealthService healthService, boolean isActive) {
    return DatamartDetailedService.builder()
        .active(isActive)
        .serviceInfo(
            DatamartDetailedService.ServiceInfo.builder()
                .serviceId(healthService.serviceId())
                .name(
                    HealthService.Covid19Vaccine.equals(healthService)
                        ? CMS_OVERLAY_SERVICE_NAME_COVID_19
                        : healthService.name())
                .serviceType(healthService.serviceType())
                .build())
        .appointmentLeadIn("Your VA health care team will contact you if you...more text")
        .phoneNumbers(
            List.of(
                DatamartDetailedService.AppointmentPhoneNumber.builder()
                    .extension("123")
                    .label("Main phone")
                    .number("555-555-1212")
                    .type("tel")
                    .build()))
        .serviceLocations(
            List.of(
                DatamartDetailedService.DetailedServiceLocation.builder()
                    .officeName("ENT Clinic")
                    .onlineSchedulingAvailable("True")
                    .referralRequired("True")
                    .walkInsAccepted("False")
                    .serviceAddress(
                        DatamartDetailedService.DetailedServiceAddress.builder()
                            .buildingNameNumber("Baxter Building")
                            .wingFloorOrRoomNumber("Wing East")
                            .address1("122 Main St.")
                            .address2(null)
                            .city("Rochester")
                            .state("NY")
                            .zipCode("14623-1345")
                            .countryCode("US")
                            .build())
                    .phoneNumbers(
                        List.of(
                            DatamartDetailedService.AppointmentPhoneNumber.builder()
                                .extension("567")
                                .label("Alt phone")
                                .number("556-565-1119")
                                .type("tel")
                                .build()))
                    .emailContacts(
                        List.of(
                            DatamartDetailedService.DetailedServiceEmailContact.builder()
                                .emailAddress("georgea@va.gov")
                                .emailLabel("George Anderson")
                                .build()))
                    .serviceHours(
                        DatamartDetailedService.DetailedServiceHours.builder()
                            .monday("8:30AM-7:00PM")
                            .tuesday("8:30AM-7:00PM")
                            .wednesday("8:30AM-7:00PM")
                            .thursday("8:30AM-7:00PM")
                            .friday("8:30AM-7:00PM")
                            .saturday("8:30AM-7:00PM")
                            .sunday("CLOSED")
                            .build())
                    .additionalHoursInfo("Please call for an appointment outside...")
                    .build()))
        .build();
  }

  private static DatamartCmsOverlay.HealthCareSystem _overlay_health_care_system() {
    return DatamartCmsOverlay.HealthCareSystem.builder()
        .name("Example Health Care System Name")
        .url("https://www.va.gov/example/locations/facility")
        .covidUrl("https://www.va.gov/example/programs/covid-19-vaccine")
        .healthConnectPhone("123-456-7890 x123")
        .build();
  }

  private static DatamartFacility.OperatingStatus _overlay_operating_status() {
    return DatamartFacility.OperatingStatus.builder()
        .code(DatamartFacility.OperatingStatusCode.LIMITED)
        .additionalInfo("Limited")
        .build();
  }

  private InternalFacilitiesController _controller() {
    return InternalFacilitiesController.builder()
        .collector(mockCollector)
        .facilityRepository(facilityRepository)
        .facilityServicesRepository(facilityServicesRepository)
        .cmsOverlayRepository(overlayRepository)
        .build();
  }

  private FacilitiesCollector _facilitiesCollector() {
    return new FacilitiesCollector(
        mock(InsecureRestTemplateProvider.class),
        mock(JdbcTemplate.class),
        mockCmsOverlayCollector,
        mock(AccessToCareCollector.class),
        mock(AccessToPwtCollector.class),
        "cemeteriesBaseUrl");
  }

  private FacilityEntity _facilityEntity(DatamartFacility fac) {
    return _facilityEntity(fac, null);
  }

  @SneakyThrows
  private FacilityEntity _facilityEntity(DatamartFacility fac, DatamartCmsOverlay overlay) {
    String operatingStatusString = null;
    Set<String> cmsServicesNames = new HashSet<>();
    String cmsServicesString = null;
    if (overlay != null) {
      operatingStatusString =
          overlay.operatingStatus() == null
              ? null
              : JacksonConfig.createMapper().writeValueAsString(overlay.operatingStatus());
      cmsServicesString =
          overlay.detailedServices() == null
              ? null
              : JacksonConfig.createMapper().writeValueAsString(overlay.detailedServices());
      if (overlay.detailedServices() != null) {
        for (DatamartDetailedService service : overlay.detailedServices()) {
          if (service.active()) {
            cmsServicesNames.add(capitalize(service.serviceInfo().serviceId()));
          }
        }
      }
    }
    return InternalFacilitiesController.populate(
        FacilityEntity.builder()
            .id(FacilityEntity.Pk.fromIdString(fac.id()))
            .cmsOperatingStatus(operatingStatusString)
            .overlayServices(cmsServicesNames)
            .cmsServices(cmsServicesString)
            .lastUpdated(Instant.now())
            .build(),
        fac);
  }

  private DatamartFacility.OperatingStatus _operatingStatus(
      DatamartFacility.OperatingStatusCode code) {
    return DatamartFacility.OperatingStatus.builder().code(code).build();
  }

  @SneakyThrows
  private CmsOverlayEntity _overlayEntity(DatamartCmsOverlay overlay, String id) {
    return CmsOverlayEntity.builder()
        .id(FacilityEntity.Pk.fromIdString(id))
        .core(
            overlay.core() == null
                ? null
                : JacksonConfig.createMapper().writeValueAsString(overlay.core()))
        .cmsOperatingStatus(
            overlay.operatingStatus() == null
                ? null
                : JacksonConfig.createMapper().writeValueAsString(overlay.operatingStatus()))
        .cmsServices(
            overlay.detailedServices() == null || overlay.detailedServices().isEmpty()
                ? null
                : JacksonConfig.createMapper().writeValueAsString(overlay.detailedServices()))
        .healthCareSystem(
            overlay.healthCareSystem() == null
                ? null
                : JacksonConfig.createMapper().writeValueAsString(overlay.healthCareSystem()))
        .build();
  }

  @Test
  @SneakyThrows
  void collect_createUpdate() {
    final var linkerUrl = buildLinkerUrlV1("http://foo/", "bar");
    final DatamartFacility f1 =
        _facility(
            "vha_f1",
            "FL",
            "South",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.MentalHealthCare));
    final DatamartFacility f2 =
        _facility(
            "vha_f2",
            "NEAT",
            "32934",
            5.6,
            6.7,
            List.of(gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.UrgentCare));
    List<DatamartFacility> datamartFacilities = List.of(f1, f2);
    final DatamartFacility f2Old =
        _facility(
            "vha_f2",
            "NO",
            "666",
            9.0,
            9.1,
            List.of(gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.SpecialtyCare));
    // Setup facility
    facilityRepository.save(_facilityEntity(f2Old));
    // Setup facility services
    setupFacilityServices(f2Old.id(), linkerUrl, f2Old.attributes().services().benefits());
    setupFacilityServices(f2Old.id(), linkerUrl, f2Old.attributes().services().health());
    setupFacilityServices(f2Old.id(), linkerUrl, f2Old.attributes().services().other());

    when(mockCollector.collectFacilities(true)).thenReturn(datamartFacilities);
    ReloadResponse response = _controller().reload().getBody();
    assertThat(response.facilitiesCreated()).isEqualTo(List.of("vha_f1"));
    assertThat(response.facilitiesUpdated()).isEqualTo(List.of("vha_f2"));
    RecursiveComparisonConfiguration comparisonConfig =
        RecursiveComparisonConfiguration.builder()
            .withIgnoredFields("version", "lastUpdated")
            .build();
    assertThat(facilityRepository.findAll())
        .usingRecursiveFieldByFieldElementComparator(comparisonConfig)
        .containsExactlyInAnyOrder(_facilityEntity(f1), _facilityEntity(f2));
  }

  @Test
  @SneakyThrows
  void collect_doNotInferOperatingStatus() {
    DatamartFacility facility = _facility("vha_f1", "FL", "32934", 91.4, 181.4, List.of());
    DatamartCmsOverlay overlay = _overlay();
    facility
        .attributes()
        .operatingStatus(_operatingStatus(DatamartFacility.OperatingStatusCode.CLOSED));
    facility.attributes().activeStatus(DatamartFacility.ActiveStatus.A);
    overlay.operatingStatus().code(DatamartFacility.OperatingStatusCode.CLOSED);
    HashMap<String, DatamartCmsOverlay> overlays = new HashMap<>();
    overlays.put(facility.id(), overlay);
    when(mockCmsOverlayCollector.loadAndUpdateCmsOverlays()).thenReturn(overlays);
    FacilitiesCollector facilitiesCollector = _facilitiesCollector();
    // If facility operating status is not null, and the overlay operating status matches the
    // facility operating status,
    // then active status should not be used and facility operating status should not change
    assertThat(facility.attributes().operatingStatus().code())
        .isEqualTo(DatamartFacility.OperatingStatusCode.CLOSED);
    facilitiesCollector.updateOperatingAndActiveStatusFromCmsOverlay(List.of(facility));
    assertThat(facility.attributes().operatingStatus().code())
        .isEqualTo(DatamartFacility.OperatingStatusCode.CLOSED);
    assertThat(facility.attributes().activeStatus).isEqualTo(DatamartFacility.ActiveStatus.T);
  }

  @Test
  @SneakyThrows
  void collect_healthConnectNullHealthCareSystem() {
    DatamartFacility facility = _facility("vha_f1", "FL", "32934", 91.4, 181.4, List.of());
    DatamartCmsOverlay overlay = _overlay();
    overlay.healthCareSystem(null);
    HashMap<String, DatamartCmsOverlay> overlays = new HashMap<>();
    overlays.put(facility.id(), overlay);
    when(mockCmsOverlayCollector.loadAndUpdateCmsOverlays()).thenReturn(overlays);
    FacilitiesCollector facilitiesCollector = _facilitiesCollector();
    assertThat(facility.attributes().phone().healthConnect()).isNull();
    assertDoesNotThrow(
        () -> facilitiesCollector.updateOperatingAndActiveStatusFromCmsOverlay(List.of(facility)));
    assertThat(facility.attributes().activeStatus).isEqualTo(DatamartFacility.ActiveStatus.A);
    assertThat(facility.attributes().phone().healthConnect()).isNull();
  }

  @Test
  @SneakyThrows
  void collect_healthConnectNullHealthCareSystemAndPhone() {
    DatamartFacility facility = _facility("vha_f1", "FL", "32934", 91.4, 181.4, List.of());
    DatamartCmsOverlay overlay = _overlay();
    overlay.healthCareSystem(null);
    facility.attributes().phone(null);
    HashMap<String, DatamartCmsOverlay> overlays = new HashMap<>();
    overlays.put(facility.id(), overlay);
    when(mockCmsOverlayCollector.loadAndUpdateCmsOverlays()).thenReturn(overlays);
    FacilitiesCollector facilitiesCollector = _facilitiesCollector();
    assertThat(facility.attributes().phone()).isNull();
    assertDoesNotThrow(
        () -> facilitiesCollector.updateOperatingAndActiveStatusFromCmsOverlay(List.of(facility)));
    assertThat(facility.attributes().activeStatus()).isEqualTo(DatamartFacility.ActiveStatus.A);
    assertThat(facility.attributes().phone()).isNull();
  }

  @Test
  @SneakyThrows
  void collect_healthConnectNullPhone() {
    DatamartFacility facility = _facility("vha_f1", "FL", "32934", 91.4, 181.4, List.of());
    facility.attributes().phone(null);
    HashMap<String, DatamartCmsOverlay> overlays = new HashMap<>();
    overlays.put(facility.id(), _overlay());
    when(mockCmsOverlayCollector.loadAndUpdateCmsOverlays()).thenReturn(overlays);
    FacilitiesCollector facilitiesCollector = _facilitiesCollector();
    assertThat(facility.attributes().phone()).isNull();
    assertDoesNotThrow(
        () -> facilitiesCollector.updateOperatingAndActiveStatusFromCmsOverlay(List.of(facility)));
    assertThat(facility.attributes().activeStatus()).isEqualTo(DatamartFacility.ActiveStatus.A);
    assertThat(facility.attributes().phone().healthConnect())
        .isEqualTo(_overlay().healthCareSystem().healthConnectPhone());
  }

  @Test
  @SneakyThrows
  void collect_invalidLatLong() {
    final var linkerUrl = buildLinkerUrlV1("http://foo/", "bar");
    final var facilityId = "vha_f1";
    DatamartFacility f1 =
        _facility(
            facilityId,
            "FL",
            "999",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.MentalHealthCare));
    DatamartFacility f1V1 =
        _facilityV1(
            facilityId,
            "FL",
            "South",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.Service
                    .<gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService>builder()
                    .serviceType(
                        gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealth)
                    .name(
                        gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealth
                            .name())
                    .link(
                        buildTypedServiceLink(
                            linkerUrl,
                            facilityId,
                            gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService
                                .MentalHealth.serviceId()))
                    .build()));
    f1.attributes().latitude(null);
    f1.attributes().longitude(null);
    f1V1.attributes().latitude(null);
    f1V1.attributes().longitude(null);
    when(mockCollector.collectFacilities(true)).thenReturn(List.of(f1));
    ReloadResponse response = _controller().reload().getBody();
    assertThat(response.problems())
        .isEqualTo(List.of(ReloadResponse.Problem.of("vha_f1", "Missing coordinates")));
  }

  @Test
  @SneakyThrows
  void collect_missing() {
    final var linkerUrl = buildLinkerUrlV1("http://foo/", "bar");
    final DatamartFacility f1 =
        _facility(
            "vha_f1",
            "FL",
            "South",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.MentalHealthCare));
    final DatamartFacility f1Old =
        _facility(
            "vha_f1",
            "NO",
            "666",
            9.0,
            9.1,
            List.of(gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.SpecialtyCare));
    final DatamartFacility f2Old =
        _facility(
            "vha_f2",
            "NO",
            "666",
            9.0,
            9.1,
            List.of(gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.SpecialtyCare));
    final DatamartFacility f3Old =
        _facility(
            "vha_f3",
            "NO",
            "666",
            9.0,
            9.1,
            List.of(gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.SpecialtyCare));
    final DatamartFacility f4Old =
        _facility(
            "vha_f4",
            "NO",
            "666",
            9.0,
            9.1,
            List.of(gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.SpecialtyCare));
    // Setup facility
    facilityRepository.save(_facilityEntity(f1Old));
    facilityRepository.save(_facilityEntity(f2Old));
    facilityRepository.save(_facilityEntity(f3Old));
    facilityRepository.save(_facilityEntity(f4Old));
    // Setup facility services
    setupFacilityServices(f1Old.id(), linkerUrl, f1Old.attributes().services().benefits());
    setupFacilityServices(f1Old.id(), linkerUrl, f1Old.attributes().services().health());
    setupFacilityServices(f1Old.id(), linkerUrl, f1Old.attributes().services().other());
    setupFacilityServices(f2Old.id(), linkerUrl, f2Old.attributes().services().benefits());
    setupFacilityServices(f2Old.id(), linkerUrl, f2Old.attributes().services().health());
    setupFacilityServices(f2Old.id(), linkerUrl, f2Old.attributes().services().other());
    setupFacilityServices(f3Old.id(), linkerUrl, f3Old.attributes().services().benefits());
    setupFacilityServices(f3Old.id(), linkerUrl, f3Old.attributes().services().health());
    setupFacilityServices(f3Old.id(), linkerUrl, f3Old.attributes().services().other());
    setupFacilityServices(f4Old.id(), linkerUrl, f4Old.attributes().services().benefits());
    setupFacilityServices(f4Old.id(), linkerUrl, f4Old.attributes().services().health());
    setupFacilityServices(f4Old.id(), linkerUrl, f4Old.attributes().services().other());

    when(mockCollector.collectFacilities(true)).thenReturn(List.of(f1));
    ReloadResponse response = _controller().reload().getBody();
    assertThat(response.facilitiesUpdated()).isEqualTo(List.of("vha_f1"));
    assertThat(response.facilitiesMissing()).isEqualTo(List.of("vha_f2", "vha_f3", "vha_f4"));
    List<FacilityEntity> findAll = ImmutableList.copyOf(facilityRepository.findAll());
    assertThat(findAll).hasSize(4);
    assertThat(findAll.get(0).missingTimestamp()).isNull();
    assertThat(findAll.get(0).services())
        .isEqualTo(
            Set.of(
                JacksonConfig.createMapper()
                    .writeValueAsString(
                        Service.<HealthService>builder()
                            .serviceType(HealthService.MentalHealth)
                            .name(HealthService.MentalHealth.name())
                            .build())));
    assertThat(findAll.get(1).missingTimestamp()).isNotNull();
    assertThat(findAll.get(2).missingTimestamp()).isNotNull();
    assertThat(findAll.get(3).missingTimestamp()).isNotNull();
  }

  @Test
  @SneakyThrows
  void collect_missingComesBack() {
    final var linkerUrl = buildLinkerUrlV1("http://foo/", "bar");
    final DatamartFacility f1 =
        _facility(
            "vha_f1",
            "FL",
            "South",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.MentalHealthCare));
    final DatamartFacility f1Old =
        _facility(
            "vha_f1",
            "NO",
            "666",
            9.0,
            9.1,
            List.of(gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.SpecialtyCare));
    // Setup facility
    facilityRepository.save(_facilityEntity(f1Old).missingTimestamp(Instant.now().toEpochMilli()));
    // Setup facility services
    setupFacilityServices(f1Old.id(), linkerUrl, f1Old.attributes().services().benefits());
    setupFacilityServices(f1Old.id(), linkerUrl, f1Old.attributes().services().health());
    setupFacilityServices(f1Old.id(), linkerUrl, f1Old.attributes().services().other());

    when(mockCollector.collectFacilities(true)).thenReturn(List.of(f1));
    ReloadResponse response = _controller().reload().getBody();
    assertThat(response.facilitiesUpdated()).isEqualTo(List.of("vha_f1"));
    FacilityEntity result = Iterables.getOnlyElement(facilityRepository.findAll());
    assertThat(result.missingTimestamp()).isNull();
    assertThat(result.services())
        .isEqualTo(
            Set.of(
                JacksonConfig.createMapper()
                    .writeValueAsString(
                        Service.<HealthService>builder()
                            .serviceType(HealthService.MentalHealth)
                            .name(HealthService.MentalHealth.name())
                            .build())));
  }

  @Test
  @SneakyThrows
  void collect_missingTimestampPreserved() {
    final var linkerUrl = buildLinkerUrlV1("http://foo/", "bar");
    final DatamartFacility f1Old =
        _facility(
            "vha_f1",
            "NO",
            "666",
            9.0,
            9.1,
            List.of(gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.SpecialtyCare));
    long early = Instant.now().minusSeconds(60).toEpochMilli();
    // Setup facility
    facilityRepository.save(_facilityEntity(f1Old).missingTimestamp(early));
    // Setup facility services
    setupFacilityServices(f1Old.id(), linkerUrl, f1Old.attributes().services().benefits());
    setupFacilityServices(f1Old.id(), linkerUrl, f1Old.attributes().services().health());
    setupFacilityServices(f1Old.id(), linkerUrl, f1Old.attributes().services().other());

    when(mockCollector.collectFacilities(true)).thenReturn(emptyList());
    ReloadResponse response = _controller().reload().getBody();
    assertThat(response.facilitiesMissing()).isEqualTo(List.of("vha_f1"));
    FacilityEntity result = Iterables.getOnlyElement(facilityRepository.findAll());
    assertThat(result.missingTimestamp()).isEqualTo(early);
  }

  @Test
  @SneakyThrows
  void collect_noStateOrZip() {
    final var linkerUrl = buildLinkerUrlV1("http://foo/", "bar");
    final var facilityId = "vha_f1";
    DatamartFacility f1 =
        _facility(
            facilityId,
            "FL",
            "South",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.MentalHealthCare));
    DatamartFacility f1V1 =
        _facilityV1(
            facilityId,
            "FL",
            "South",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.Service
                    .<gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService>builder()
                    .serviceType(
                        gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealth)
                    .name(
                        gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealth
                            .name())
                    .link(
                        buildTypedServiceLink(
                            linkerUrl,
                            facilityId,
                            gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService
                                .MentalHealth.serviceId()))
                    .build()));
    f1.attributes().address().physical().state(null);
    f1.attributes().address().physical().zip(null);
    f1.attributes().latitude(BigDecimal.valueOf(91.4));
    f1.attributes().longitude(BigDecimal.valueOf(181.4));
    f1V1.attributes().address().physical().state(null);
    f1V1.attributes().address().physical().zip(null);
    f1V1.attributes().latitude(BigDecimal.valueOf(91.4));
    f1V1.attributes().longitude(BigDecimal.valueOf(181.4));
    when(mockCollector.collectFacilities(true)).thenReturn(List.of(f1));
    ReloadResponse response = _controller().reload().getBody();
    assertThat(response.facilitiesCreated()).isEqualTo(List.of("vha_f1"));
    assertThat(response.problems())
        .isEqualTo(
            List.of(
                ReloadResponse.Problem.of("vha_f1", "Missing or invalid physical address zip"),
                ReloadResponse.Problem.of("vha_f1", "Missing physical address state"),
                ReloadResponse.Problem.of("vha_f1", "Missing physical address city"),
                ReloadResponse.Problem.of("vha_f1", "Missing physical address street information"),
                ReloadResponse.Problem.of("vha_f1", "Missing or invalid mailing address zip"),
                ReloadResponse.Problem.of("vha_f1", "Missing mailing address state"),
                ReloadResponse.Problem.of("vha_f1", "Missing mailing address city"),
                ReloadResponse.Problem.of("vha_f1", "Missing mailing address street information"),
                ReloadResponse.Problem.of("vha_f1", "Missing main phone number"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Monday"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Tuesday"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Wednesday"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Thursday"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Friday"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Saturday"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Sunday"),
                ReloadResponse.Problem.of("vha_f1", "Missing classification"),
                ReloadResponse.Problem.of("vha_f1", "Missing or invalid location latitude"),
                ReloadResponse.Problem.of("vha_f1", "Missing or invalid location longitude")));
  }

  @Test
  @SneakyThrows
  void collect_noVISN() {
    DatamartFacility f1 = _facility("vha_f1", "FL", "32934", 91.4, 181.4, List.of());
    DatamartFacility f1V1 = _facilityV1("vha_f1", "FL", "32934", 91.4, 181.4, List.of());
    f1.attributes().facilityType(va_health_facility);
    f1V1.attributes().facilityType(FacilityType.va_health_facility);
    when(mockCollector.collectFacilities(true)).thenReturn(List.of(f1));
    ReloadResponse responseHealth = _controller().reload().getBody();
    assertThat(responseHealth.facilitiesCreated()).isEqualTo(List.of("vha_f1"));
    assertThat(responseHealth.problems())
        .contains(ReloadResponse.Problem.of("vha_f1", "Missing VISN"));
    DatamartFacility f2 = _facility("vc_f1", "FL", "32934", 91.4, 181.4, List.of());
    DatamartFacility f2V1 = _facilityV1("vc_f1", "FL", "32934", 91.4, 181.4, List.of());
    f2.attributes().facilityType(vet_center);
    f2V1.attributes().facilityType(FacilityType.vet_center);
    when(mockCollector.collectFacilities(true)).thenReturn(List.of(f2));
    ReloadResponse responseVetCenter = _controller().reload().getBody();
    assertThat(responseVetCenter.facilitiesCreated()).isEqualTo(List.of("vc_f1"));
    assertThat(responseVetCenter.problems())
        .contains(ReloadResponse.Problem.of("vc_f1", "Missing VISN"));
  }

  @Test
  @SneakyThrows
  void collect_operatingStatusWithOverlay() {
    DatamartFacility facility = _facility("vha_f1", "FL", "32934", 91.4, 181.4, List.of());
    DatamartCmsOverlay overlay = _overlay();
    facility.attributes().operatingStatus(null);
    facility.attributes().activeStatus(DatamartFacility.ActiveStatus.A);
    overlay.operatingStatus().code(DatamartFacility.OperatingStatusCode.CLOSED);
    HashMap<String, DatamartCmsOverlay> overlays = new HashMap<>();
    overlays.put(facility.id(), overlay);
    when(mockCmsOverlayCollector.loadAndUpdateCmsOverlays()).thenReturn(overlays);
    FacilitiesCollector facilitiesCollector = _facilitiesCollector();
    // If facility operating status is null, but overlay operating status is not null, then active
    // status should not be
    // used, instead overlay operating status should be used
    assertThat(facility.attributes().operatingStatus()).isNull();
    facilitiesCollector.updateOperatingAndActiveStatusFromCmsOverlay(List.of(facility));
    assertThat(facility.attributes().activeStatus()).isEqualTo(DatamartFacility.ActiveStatus.T);
    assertThat(facility.attributes().operatingStatus().code())
        .isEqualTo(overlay.operatingStatus().code());
  }

  @Test
  @SneakyThrows
  void collect_populateOperatingStatusFromNull() {
    DatamartFacility facility = _facility("vha_f1", "FL", "32934", 91.4, 181.4, List.of());
    DatamartCmsOverlay overlay = _overlay();
    facility.attributes().operatingStatus(null);
    facility.attributes().activeStatus(DatamartFacility.ActiveStatus.A);
    overlay.operatingStatus(null);
    HashMap<String, DatamartCmsOverlay> overlays = new HashMap<>();
    overlays.put(facility.id(), overlay);
    when(mockCmsOverlayCollector.loadAndUpdateCmsOverlays()).thenReturn(overlays);
    FacilitiesCollector facilitiesCollector = _facilitiesCollector();
    // If facility operating status is null, and overlay operating status is null and therefore
    // cannot be used to
    // populate facility operating status, then active status should be used to infer facility
    // operating status
    assertThat(facility.attributes().operatingStatus()).isNull();
    facilitiesCollector.updateOperatingAndActiveStatusFromCmsOverlay(List.of(facility));
    assertThat(facility.attributes().activeStatus()).isEqualTo(DatamartFacility.ActiveStatus.A);
    assertThat(facility.attributes().operatingStatus().code())
        .isEqualTo(DatamartFacility.OperatingStatusCode.NORMAL);
  }

  @Test
  @SneakyThrows
  void collect_setHealthConnectPhoneNumber() {
    DatamartFacility facility = _facility("vha_f1", "FL", "32934", 91.4, 181.4, List.of());
    CmsOverlayCollector mockCmsOverlayCollector = mock(CmsOverlayCollector.class);
    HashMap<String, DatamartCmsOverlay> overlays = new HashMap<>();
    overlays.put(facility.id(), _overlay());
    when(mockCmsOverlayCollector.loadAndUpdateCmsOverlays()).thenReturn(overlays);
    FacilitiesCollector facilitiesCollector =
        new FacilitiesCollector(
            mock(InsecureRestTemplateProvider.class),
            mock(JdbcTemplate.class),
            mockCmsOverlayCollector,
            mock(AccessToCareCollector.class),
            mock(AccessToPwtCollector.class),
            "cemeteriesBaseUrl");
    assertThat(facility.attributes().phone().healthConnect()).isNull();
    facilitiesCollector.updateOperatingAndActiveStatusFromCmsOverlay(List.of(facility));
    assertThat(facility.attributes().activeStatus()).isEqualTo(DatamartFacility.ActiveStatus.A);
    assertThat(facility.attributes().phone().healthConnect())
        .isEqualTo(_overlay().healthCareSystem().healthConnectPhone());
  }

  @Test
  @SneakyThrows
  void collect_updateActiveStatusFromNullToA() {
    DatamartFacility facility = _facility("vha_f1", "FL", "32934", 91.4, 181.4, List.of());
    DatamartCmsOverlay overlay = _overlay();
    overlay.operatingStatus(_operatingStatus(DatamartFacility.OperatingStatusCode.NORMAL));
    facility.attributes().operatingStatus(_operatingStatus(null));
    facility.attributes().activeStatus(null);
    HashMap<String, DatamartCmsOverlay> overlays = new HashMap<>();
    overlays.put(facility.id(), overlay);
    when(mockCmsOverlayCollector.loadAndUpdateCmsOverlays()).thenReturn(overlays);
    FacilitiesCollector facilitiesCollector = _facilitiesCollector();
    assertThat(facility.attributes().activeStatus()).isEqualTo(null);
    facilitiesCollector.updateOperatingAndActiveStatusFromCmsOverlay(List.of(facility));
    assertThat(facility.attributes.operatingStatus().code())
        .isEqualTo(DatamartFacility.OperatingStatusCode.NORMAL);
    assertThat(facility.attributes().activeStatus()).isEqualTo(DatamartFacility.ActiveStatus.A);
  }

  @Test
  @SneakyThrows
  void collect_updateActiveStatusFromNullToT() {
    DatamartFacility facility = _facility("vha_f1", "FL", "32934", 91.4, 181.4, List.of());
    DatamartCmsOverlay overlay = _overlay();
    overlay.operatingStatus(_operatingStatus(DatamartFacility.OperatingStatusCode.CLOSED));
    facility.attributes().operatingStatus(_operatingStatus(null));
    facility.attributes().activeStatus(null);
    HashMap<String, DatamartCmsOverlay> overlays = new HashMap<>();
    overlays.put(facility.id(), overlay);
    when(mockCmsOverlayCollector.loadAndUpdateCmsOverlays()).thenReturn(overlays);
    FacilitiesCollector facilitiesCollector = _facilitiesCollector();
    assertThat(facility.attributes().activeStatus()).isEqualTo(null);
    facilitiesCollector.updateOperatingAndActiveStatusFromCmsOverlay(List.of(facility));
    assertThat(facility.attributes.operatingStatus().code())
        .isEqualTo(DatamartFacility.OperatingStatusCode.CLOSED);
    assertThat(facility.attributes().activeStatus()).isEqualTo(DatamartFacility.ActiveStatus.T);
  }

  @Test
  @SneakyThrows
  void collect_updateActiveStatusToA() {
    DatamartFacility facility = _facility("vha_f1", "FL", "32934", 91.4, 181.4, List.of());
    DatamartCmsOverlay overlay = _overlay();
    overlay.operatingStatus(_operatingStatus(DatamartFacility.OperatingStatusCode.NORMAL));
    facility.attributes().operatingStatus(_operatingStatus(null));
    facility.attributes().activeStatus(DatamartFacility.ActiveStatus.T);
    HashMap<String, DatamartCmsOverlay> overlays = new HashMap<>();
    overlays.put(facility.id(), overlay);
    when(mockCmsOverlayCollector.loadAndUpdateCmsOverlays()).thenReturn(overlays);
    FacilitiesCollector facilitiesCollector = _facilitiesCollector();
    assertThat(facility.attributes().activeStatus()).isEqualTo(DatamartFacility.ActiveStatus.T);
    facilitiesCollector.updateOperatingAndActiveStatusFromCmsOverlay(List.of(facility));
    assertThat(facility.attributes.operatingStatus().code())
        .isEqualTo(DatamartFacility.OperatingStatusCode.NORMAL);
    assertThat(facility.attributes().activeStatus()).isEqualTo(DatamartFacility.ActiveStatus.A);
  }

  @Test
  @SneakyThrows
  void collect_updateActiveStatusToT() {
    DatamartFacility facility = _facility("vha_f1", "FL", "32934", 91.4, 181.4, List.of());
    DatamartCmsOverlay overlay = _overlay();
    overlay.operatingStatus(_operatingStatus(DatamartFacility.OperatingStatusCode.CLOSED));
    facility.attributes().operatingStatus(_operatingStatus(null));
    facility.attributes().activeStatus(DatamartFacility.ActiveStatus.A);
    HashMap<String, DatamartCmsOverlay> overlays = new HashMap<>();
    overlays.put(facility.id(), overlay);
    when(mockCmsOverlayCollector.loadAndUpdateCmsOverlays()).thenReturn(overlays);
    FacilitiesCollector facilitiesCollector = _facilitiesCollector();
    assertThat(facility.attributes().activeStatus()).isEqualTo(DatamartFacility.ActiveStatus.A);
    facilitiesCollector.updateOperatingAndActiveStatusFromCmsOverlay(List.of(facility));
    assertThat(facility.attributes.operatingStatus().code())
        .isEqualTo(DatamartFacility.OperatingStatusCode.CLOSED);
    assertThat(facility.attributes().activeStatus()).isEqualTo(DatamartFacility.ActiveStatus.T);
  }

  @Test
  @SneakyThrows
  void collect_updateOperatingStatusToClosed() {
    DatamartFacility facility = _facility("vha_f1", "FL", "32934", 91.4, 181.4, List.of());
    DatamartCmsOverlay overlay = _overlay();
    overlay.operatingStatus(null);
    facility
        .attributes()
        .operatingStatus(_operatingStatus(DatamartFacility.OperatingStatusCode.NORMAL));
    facility.attributes().activeStatus(DatamartFacility.ActiveStatus.T);
    HashMap<String, DatamartCmsOverlay> overlays = new HashMap<>();
    overlays.put(facility.id(), overlay);
    when(mockCmsOverlayCollector.loadAndUpdateCmsOverlays()).thenReturn(overlays);
    FacilitiesCollector facilitiesCollector = _facilitiesCollector();
    // If facility's operating status is NORMAL, the overlay's operating status is null, and the
    // facility's ActiveStatus
    // is T, then the ActiveStatus should be used to change the operating status to CLOSED
    assertThat(facility.attributes().operatingStatus().code)
        .isEqualTo(DatamartFacility.OperatingStatusCode.NORMAL);
    assertThat(facility.attributes().activeStatus()).isEqualTo(DatamartFacility.ActiveStatus.T);
    facilitiesCollector.updateOperatingAndActiveStatusFromCmsOverlay(List.of(facility));
    assertThat(facility.attributes.operatingStatus().code())
        .isEqualTo(DatamartFacility.OperatingStatusCode.CLOSED);
    assertThat(facility.attributes().activeStatus()).isEqualTo(DatamartFacility.ActiveStatus.T);
  }

  @Test
  @SneakyThrows
  void collect_updateOperatingStatusToNormal() {
    DatamartFacility facility = _facility("vha_f1", "FL", "32934", 91.4, 181.4, List.of());
    DatamartCmsOverlay overlay = _overlay();
    overlay.operatingStatus(null);
    facility
        .attributes()
        .operatingStatus(_operatingStatus(DatamartFacility.OperatingStatusCode.CLOSED));
    facility.attributes().activeStatus(DatamartFacility.ActiveStatus.A);
    HashMap<String, DatamartCmsOverlay> overlays = new HashMap<>();
    overlays.put(facility.id(), overlay);
    when(mockCmsOverlayCollector.loadAndUpdateCmsOverlays()).thenReturn(overlays);
    FacilitiesCollector facilitiesCollector = _facilitiesCollector();
    // If facility's operating status is NORMAL, the overlay's operating status is null, and the
    // facility's ActiveStatus
    // is T, then the ActiveStatus should be used to change the operating status to CLOSED
    assertThat(facility.attributes().operatingStatus().code)
        .isEqualTo(DatamartFacility.OperatingStatusCode.CLOSED);
    assertThat(facility.attributes().activeStatus()).isEqualTo(DatamartFacility.ActiveStatus.A);
    facilitiesCollector.updateOperatingAndActiveStatusFromCmsOverlay(List.of(facility));
    assertThat(facility.attributes.operatingStatus().code())
        .isEqualTo(DatamartFacility.OperatingStatusCode.NORMAL);
    assertThat(facility.attributes().activeStatus()).isEqualTo(DatamartFacility.ActiveStatus.A);
  }

  @Test
  void deleteFacilityByIdWithOverlay() {
    final var linkerUrl = buildLinkerUrlV1("http://foo/", "bar");
    final DatamartFacility f =
        _facility(
            "vha_f1",
            "FL",
            "South",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.MentalHealthCare));
    // Setup facility
    facilityRepository.save(_facilityEntity(f, _overlay()));
    // Setup facility services
    setupFacilityServices(f.id(), linkerUrl, f.attributes().services().benefits());
    setupFacilityServices(f.id(), linkerUrl, f.attributes().services().health());
    setupFacilityServices(f.id(), linkerUrl, f.attributes().services().other());
    // Setup overlay services
    overlayRepository.save(_overlayEntity(_overlay(), "vha_f1"));
    ResponseEntity<String> response = _controller().deleteFacilityById("vha_f1");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(facilityRepository.findAll()).isEmpty();
    assertThat(overlayRepository.findAll())
        .usingRecursiveComparison()
        .isEqualTo((List.of(_overlayEntity(_overlay(), "vha_f1"))));
  }

  @Test
  void deleteFacilityByIdWithoutOverlay() {
    final var linkerUrl = buildLinkerUrlV1("http://foo/", "bar");
    final DatamartFacility f =
        _facility(
            "vha_f1",
            "FL",
            "South",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.MentalHealthCare));
    // Setup facility
    facilityRepository.save(_facilityEntity(f));
    // Setup facility services
    setupFacilityServices(f.id(), linkerUrl, f.attributes().services().benefits());
    setupFacilityServices(f.id(), linkerUrl, f.attributes().services().health());
    setupFacilityServices(f.id(), linkerUrl, f.attributes().services().other());

    ResponseEntity<String> response = _controller().deleteFacilityById("vha_f1");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(facilityRepository.findAll()).isEmpty();
  }

  @Test
  void deleteFacilityOverlayById() {
    final var linkerUrl = buildLinkerUrlV1("http://foo/", "bar");
    final DatamartFacility f =
        _facility(
            "vha_f1",
            "FL",
            "South",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.MentalHealthCare));
    ResponseEntity<Void> response = null;
    // Setup facility
    facilityRepository.save(_facilityEntity(f, _overlay()));
    // Setup facility services
    setupFacilityServices(f.id(), linkerUrl, f.attributes().services().benefits());
    setupFacilityServices(f.id(), linkerUrl, f.attributes().services().health());
    setupFacilityServices(f.id(), linkerUrl, f.attributes().services().other());
    // Setup overlay services
    overlayRepository.save(_overlayEntity(_overlay(), "vha_f1"));
    response = _controller().deleteCmsOverlayById("vha_f1", "operating_status");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    RecursiveComparisonConfiguration facilityEntityCompConfig =
        RecursiveComparisonConfiguration.builder()
            .withIgnoredFields("version", "lastUpdated")
            .build();
    assertThat(facilityRepository.findAll())
        .usingRecursiveFieldByFieldElementComparator(facilityEntityCompConfig)
        .containsOnly(
            _facilityEntity(
                f,
                DatamartCmsOverlay.builder()
                    .detailedServices(_overlay_detailed_services())
                    .build()));
    assertThat(overlayRepository.findAll())
        .usingRecursiveComparison()
        .isEqualTo(
            List.of(
                _overlayEntity(
                    DatamartCmsOverlay.builder()
                        .detailedServices(_overlay_detailed_services())
                        .healthCareSystem(_overlay_health_care_system())
                        .core(_core())
                        .build(),
                    "vha_f1")));
    overlayRepository.deleteAll();
    overlayRepository.save(_overlayEntity(_overlay(), "vha_f1"));
    response = _controller().deleteCmsOverlayById("vha_f1", "detailed_services");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(facilityRepository.findAll())
        .usingRecursiveFieldByFieldElementComparator(facilityEntityCompConfig)
        .containsOnly(
            _facilityEntity(
                f,
                DatamartCmsOverlay.builder().operatingStatus(_overlay_operating_status()).build()));
    assertThat(overlayRepository.findAll())
        .usingRecursiveComparison()
        .isEqualTo(
            List.of(
                _overlayEntity(
                    DatamartCmsOverlay.builder()
                        .operatingStatus(_overlay_operating_status())
                        .healthCareSystem(_overlay_health_care_system())
                        .core(_core())
                        .build(),
                    "vha_f1")));
    overlayRepository.deleteAll();
    overlayRepository.save(_overlayEntity(_overlay(), "vha_f1"));
    response = _controller().deleteCmsOverlayById("vha_f1", "system");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(overlayRepository.findAll())
        .usingRecursiveComparison()
        .isEqualTo(
            List.of(
                _overlayEntity(
                    DatamartCmsOverlay.builder()
                        .operatingStatus(_overlay_operating_status())
                        .detailedServices(_overlay_detailed_services())
                        .core(_core())
                        .build(),
                    "vha_f1")));
    overlayRepository.deleteAll();
    // Test deleting system node when system node does not exist
    DatamartCmsOverlay datamartCmsOverlay = _overlay();
    datamartCmsOverlay.healthCareSystem(null);
    overlayRepository.save(_overlayEntity(datamartCmsOverlay, "vha_f1"));
    response = _controller().deleteCmsOverlayById("vha_f1", "system");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    // Deleting core node
    overlayRepository.deleteAll();
    overlayRepository.save(_overlayEntity(_overlay(), "vha_f1"));
    response = _controller().deleteCmsOverlayById("vha_f1", "core");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(overlayRepository.findAll())
        .usingRecursiveComparison()
        .isEqualTo(
            List.of(
                _overlayEntity(
                    DatamartCmsOverlay.builder()
                        .operatingStatus(_overlay_operating_status())
                        .detailedServices(_overlay_detailed_services())
                        .healthCareSystem(_overlay_health_care_system())
                        .build(),
                    "vha_f1")));
    overlayRepository.deleteAll();
    // Test deleting system node when core node does not exist
    datamartCmsOverlay = _overlay();
    datamartCmsOverlay.core(null);
    overlayRepository.save(_overlayEntity(datamartCmsOverlay, "vha_f1"));
    response = _controller().deleteCmsOverlayById("vha_f1", "core");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    overlayRepository.deleteAll();
    overlayRepository.save(_overlayEntity(_overlay(), "vha_f1"));
    assertThatThrownBy(() -> _controller().deleteCmsOverlayById("vha_f1", "invalid field"))
        .isInstanceOf(ExceptionsUtils.NotFound.class)
        .hasMessage("The record identified by invalid field could not be found");
    overlayRepository.deleteAll();
    overlayRepository.save(_overlayEntity(_overlay(), "vha_f1"));
    response = _controller().deleteCmsOverlayById("vha_f1", null);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(facilityRepository.findAll())
        .usingRecursiveFieldByFieldElementComparator(facilityEntityCompConfig)
        .containsOnly(_facilityEntity(f, DatamartCmsOverlay.builder().build()));
    assertThat(overlayRepository.findAll()).isEmpty();
  }

  @Test
  void deleteFacilityOverlayMissingNode() {
    overlayRepository.save(_overlayEntity(_overlay(), "vha_f1"));
    assertThat(_controller().deleteCmsOverlayById("vha_f1", "operating_status").getStatusCode())
        .isEqualTo(HttpStatus.OK);
    assertThat(_controller().deleteCmsOverlayById("vha_f1", "operating_status").getStatusCode())
        .isEqualTo(HttpStatus.ACCEPTED);
    overlayRepository.save(_overlayEntity(_overlay(), "vha_f1"));
    assertThat(_controller().deleteCmsOverlayById("vha_f1", "detailed_services").getStatusCode())
        .isEqualTo(HttpStatus.OK);
    assertThat(_controller().deleteCmsOverlayById("vha_f1", "detailed_services").getStatusCode())
        .isEqualTo(HttpStatus.ACCEPTED);
  }

  @Test
  void deleteFacilityOverlayNotFound() {
    assertThat(_controller().deleteCmsOverlayById("vha_f1", null).getStatusCode())
        .isEqualTo(HttpStatus.ACCEPTED);
    assertThat(_controller().deleteCmsOverlayById("vha_f1", "operating_status").getStatusCode())
        .isEqualTo(HttpStatus.ACCEPTED);
    assertThat(_controller().deleteCmsOverlayById("vha_f1", "detailed_services").getStatusCode())
        .isEqualTo(HttpStatus.ACCEPTED);
  }

  @Test
  void deleteFacilityOverlayUnrecognizedNode() {
    overlayRepository.save(_overlayEntity(_overlay(), "vha_f1"));
    assertThatThrownBy(() -> _controller().deleteCmsOverlayById("vha_f1", "foo"))
        .isInstanceOf(ExceptionsUtils.NotFound.class)
        .hasMessage("The record identified by foo could not be found");
  }

  @Test
  void deleteFacilityOverlayWithCovid19Vaccine() {
    final var linkerUrl = buildLinkerUrlV1("http://foo/", "bar");
    final DatamartFacility f =
        _facility(
            "vha_f1",
            "FL",
            "South",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.MentalHealthCare,
                gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.Covid19Vaccine));
    ResponseEntity<Void> response = null;
    // Setup facility
    facilityRepository.save(_facilityEntity(f, _overlay()));
    // Setup facility services
    setupFacilityServices(f.id(), linkerUrl, f.attributes().services().benefits());
    setupFacilityServices(f.id(), linkerUrl, f.attributes().services().health());
    setupFacilityServices(f.id(), linkerUrl, f.attributes().services().other());
    // Setup overlay services
    overlayRepository.save(_overlayEntity(_overlay(), "vha_f1"));
    response = _controller().deleteCmsOverlayById("vha_f1", "operating_status");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    RecursiveComparisonConfiguration facilityEntityCompConfig =
        RecursiveComparisonConfiguration.builder()
            .withIgnoredFields("version", "lastUpdated")
            .build();
    assertThat(facilityRepository.findAll())
        .usingRecursiveFieldByFieldElementComparator(facilityEntityCompConfig)
        .containsOnly(
            _facilityEntity(
                f,
                DatamartCmsOverlay.builder()
                    .detailedServices(_overlay_detailed_services())
                    .build()));
    assertThat(overlayRepository.findAll())
        .usingRecursiveComparison()
        .isEqualTo(
            List.of(
                _overlayEntity(
                    DatamartCmsOverlay.builder()
                        .detailedServices(_overlay_detailed_services())
                        .healthCareSystem(_overlay_health_care_system())
                        .core(_core())
                        .build(),
                    "vha_f1")));
    overlayRepository.deleteAll();
    overlayRepository.save(_overlayEntity(_overlay(), "vha_f1"));
    response = _controller().deleteCmsOverlayById("vha_f1", "detailed_services");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    // Covid19Vaccine should be remove from facility health services list
    assertThat(facilityRepository.findAll())
        .usingRecursiveFieldByFieldElementComparator(facilityEntityCompConfig)
        .containsOnly(
            _facilityEntity(
                _facility(
                    "vha_f1",
                    "FL",
                    "South",
                    1.2,
                    3.4,
                    List.of(
                        gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService
                            .MentalHealthCare)),
                DatamartCmsOverlay.builder().operatingStatus(_overlay_operating_status()).build()));
    assertThat(overlayRepository.findAll())
        .usingRecursiveComparison()
        .isEqualTo(
            List.of(
                _overlayEntity(
                    DatamartCmsOverlay.builder()
                        .operatingStatus(_overlay_operating_status())
                        .healthCareSystem(_overlay_health_care_system())
                        .core(_core())
                        .build(),
                    "vha_f1")));
    overlayRepository.deleteAll();
    overlayRepository.save(_overlayEntity(_overlay(), "vha_f1"));
    response = _controller().deleteCmsOverlayById("vha_f1", null);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    // Covid19Vaccine should be remove from facility health services list
    assertThat(facilityRepository.findAll())
        .usingRecursiveFieldByFieldElementComparator(facilityEntityCompConfig)
        .containsOnly(
            _facilityEntity(
                _facility(
                    "vha_f1",
                    "FL",
                    "South",
                    1.2,
                    3.4,
                    List.of(
                        gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService
                            .MentalHealthCare)),
                DatamartCmsOverlay.builder().operatingStatus(null).build()));
    assertThat(overlayRepository.findAll()).isEmpty();
  }

  @Test
  void deleteNonExistingFacilityByIdReturnsAccepted() {
    assertThat(_controller().deleteFacilityById("vha_f1").getStatusCodeValue()).isEqualTo(202);
  }

  @Test
  void deleteOverlayHealthCareSystem() {
    CmsOverlayEntity cmsOverlayEntity = _overlayEntity(_overlay(), "vha_f1");
    overlayRepository.save(cmsOverlayEntity);
    assertThat(overlayRepository.findAll())
        .usingRecursiveComparison()
        .isEqualTo(List.of(cmsOverlayEntity));
    _controller().deleteCmsOverlayById("vha_f1", "system");
    cmsOverlayEntity.healthCareSystem(null);
    assertThat(overlayRepository.findAll())
        .usingRecursiveComparison()
        .isEqualTo(List.of(cmsOverlayEntity));
  }

  @Test
  @SneakyThrows
  void duplicateFacility_invalidDuplicate() {
    final var linkerUrl = buildLinkerUrlV1("http://foo/", "bar");
    final DatamartFacility f1 =
        _facility(
            "vha_f1",
            "FL",
            "32934",
            100.00,
            100.00,
            List.of(
                gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.MentalHealthCare));
    final DatamartFacility f2 =
        _facility(
            "vha_f2",
            "FL",
            "32934",
            50.00,
            50.00,
            List.of(
                gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.MentalHealthCare));
    final DatamartFacility f1Duplicate =
        _facility(
            "vha_f1dup",
            "FL",
            "32934",
            102.00,
            102.00,
            List.of(
                gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.MentalHealthCare));
    final DatamartFacility f2Duplicate =
        _facility(
            "vha_f2dup",
            "FL",
            "32934",
            52.00,
            52.00,
            List.of(
                gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.MentalHealthCare));
    ReloadResponse response = ReloadResponse.start();
    // Setup facility
    facilityRepository.save(_facilityEntity(f1));
    facilityRepository.save(_facilityEntity(f2));
    // Setup facility services
    setupFacilityServices(f1.id(), linkerUrl, f1.attributes().services().benefits());
    setupFacilityServices(f1.id(), linkerUrl, f1.attributes().services().health());
    setupFacilityServices(f1.id(), linkerUrl, f1.attributes().services().other());
    setupFacilityServices(f2.id(), linkerUrl, f2.attributes().services().benefits());
    setupFacilityServices(f2.id(), linkerUrl, f2.attributes().services().health());
    setupFacilityServices(f2.id(), linkerUrl, f2.attributes().services().other());

    assertThat(facilityRepository.findById(FacilityEntity.Pk.fromIdString("vha_f1"))).isNotNull();
    assertThat(facilityRepository.findById(FacilityEntity.Pk.fromIdString("vha_f2"))).isNotNull();
    _controller()
        .updateAndSave(
            response,
            FacilityEntity.builder().id(FacilityEntity.Pk.fromIdString("vha_f1dup")).build(),
            f1Duplicate);
    assertThat(
            response.problems().stream()
                .filter(
                    f ->
                        f.facilityId().equalsIgnoreCase("vha_f1dup")
                            && f.description().equalsIgnoreCase("Duplicate Facilities"))
                .toList())
        .isEmpty();
    _controller()
        .updateAndSave(
            response,
            FacilityEntity.builder().id(FacilityEntity.Pk.fromIdString("vha_f2dup")).build(),
            f2Duplicate);
    assertThat(
            response.problems().stream()
                .filter(
                    f ->
                        f.facilityId().equalsIgnoreCase("vha_f2dup")
                            && f.description().equalsIgnoreCase("Duplicate Facilities"))
                .toList())
        .isEmpty();
  }

  @Test
  @SneakyThrows
  void duplicateFacility_validDuplicate() {
    final var linkerUrl = buildLinkerUrlV1("http://foo/", "bar");
    final DatamartFacility f1 =
        _facility(
            "vha_f1",
            "FL",
            "32934",
            100.00,
            100.00,
            List.of(
                gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.MentalHealthCare));
    final DatamartFacility f2 =
        _facility(
            "vha_f2",
            "FL",
            "32934",
            50.00,
            50.00,
            List.of(
                gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.MentalHealthCare));
    final DatamartFacility f1Duplicate =
        _facility(
            "vha_f1dup",
            "FL",
            "32934",
            100.0002,
            100.0002,
            List.of(
                gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.MentalHealthCare));
    final DatamartFacility f2Duplicate =
        _facility(
            "vha_f2dup",
            "FL",
            "32934",
            50.0002,
            50.0002,
            List.of(
                gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.MentalHealthCare));
    // Setup facility
    facilityRepository.save(_facilityEntity(f1));
    facilityRepository.save(_facilityEntity(f2));
    // Setup facility services
    setupFacilityServices(f1.id(), linkerUrl, f1.attributes().services().benefits());
    setupFacilityServices(f1.id(), linkerUrl, f1.attributes().services().health());
    setupFacilityServices(f1.id(), linkerUrl, f1.attributes().services().other());
    setupFacilityServices(f2.id(), linkerUrl, f2.attributes().services().benefits());
    setupFacilityServices(f2.id(), linkerUrl, f2.attributes().services().health());
    setupFacilityServices(f2.id(), linkerUrl, f2.attributes().services().other());

    assertThat(facilityRepository.findById(FacilityEntity.Pk.fromIdString("vha_f1"))).isNotNull();
    assertThat(facilityRepository.findById(FacilityEntity.Pk.fromIdString("vha_f2"))).isNotNull();
    ReloadResponse response = ReloadResponse.start();
    _controller()
        .updateAndSave(
            response,
            FacilityEntity.builder().id(FacilityEntity.Pk.fromIdString("vha_f1dup")).build(),
            f1Duplicate);
    assertThat(
            response.problems().stream()
                .filter(
                    f ->
                        f.facilityId().equalsIgnoreCase("vha_f1dup")
                            && f.description().equalsIgnoreCase("Duplicate Facilities"))
                .toList())
        .isNotEmpty();
    _controller()
        .updateAndSave(
            response,
            FacilityEntity.builder().id(FacilityEntity.Pk.fromIdString("vha_f2dup")).build(),
            f2Duplicate);
    assertThat(
            response.problems().stream()
                .filter(
                    f ->
                        f.facilityId().equalsIgnoreCase("vha_f2dup")
                            && f.description().equalsIgnoreCase("Duplicate Facilities"))
                .toList())
        .isNotEmpty();
  }

  @Test
  @SneakyThrows
  public void entityById() {
    var invalidIdWithNoSeparator = "vha123";
    var invalidIdWithNoStationNumber = "vha_";
    InternalFacilitiesController controller = InternalFacilitiesController.builder().build();
    Method cmsOverlayEntityByIdMethod =
        InternalFacilitiesController.class.getDeclaredMethod("cmsOverlayEntityById", String.class);
    cmsOverlayEntityByIdMethod.setAccessible(true);
    assertThat(cmsOverlayEntityByIdMethod.invoke(controller, invalidIdWithNoSeparator))
        .isEqualTo(Optional.empty());
    assertThat(cmsOverlayEntityByIdMethod.invoke(controller, invalidIdWithNoStationNumber))
        .isEqualTo(Optional.empty());
    Method facilityEntityByIdMethod =
        InternalFacilitiesController.class.getDeclaredMethod("facilityEntityById", String.class);
    facilityEntityByIdMethod.setAccessible(true);
    assertThat(facilityEntityByIdMethod.invoke(controller, invalidIdWithNoSeparator))
        .isEqualTo(Optional.empty());
    assertThat(facilityEntityByIdMethod.invoke(controller, invalidIdWithNoStationNumber))
        .isEqualTo(Optional.empty());
  }

  @Test
  @SneakyThrows
  void exceptions() {
    Method processMissingFacilityMethod =
        InternalFacilitiesController.class.getDeclaredMethod(
            "processMissingFacility", ReloadResponse.class, FacilityEntity.Pk.class);
    processMissingFacilityMethod.setAccessible(true);
    var response = ReloadResponse.builder().build();
    var pk = FacilityEntity.Pk.fromIdString("vha_402");
    FacilityRepository mockFacilityRepository = mock(FacilityRepository.class);
    when(mockFacilityRepository.findById(pk)).thenThrow(new NullPointerException("oh noes"));
    assertThatThrownBy(() -> processMissingFacilityMethod.invoke(_controller(), response, pk))
        .isInstanceOf(InvocationTargetException.class)
        .hasCause(new IllegalStateException());
  }

  @Test
  @SneakyThrows
  public void isHoursNull() {
    InternalFacilitiesController controller = InternalFacilitiesController.builder().build();
    Method isHoursNullMethod =
        InternalFacilitiesController.class.getDeclaredMethod("isHoursNull", DatamartFacility.class);
    isHoursNullMethod.setAccessible(true);
    assertThat(
            isHoursNullMethod.invoke(
                controller,
                DatamartFacility.builder()
                    .attributes(FacilityAttributes.builder().build())
                    .build()))
        .isEqualTo(Boolean.TRUE);
    assertThat(
            isHoursNullMethod.invoke(
                controller,
                DatamartFacility.builder()
                    .attributes(
                        FacilityAttributes.builder()
                            .hours(DatamartFacility.Hours.builder().build())
                            .build())
                    .build()))
        .isEqualTo(Boolean.FALSE);
  }

  @Test
  @SneakyThrows
  public void isMobileCenter() {
    InternalFacilitiesController controller = InternalFacilitiesController.builder().build();
    Method isMobileCeneterMethod =
        InternalFacilitiesController.class.getDeclaredMethod(
            "isMobileCenter", FacilityEntity.class);
    isMobileCeneterMethod.setAccessible(true);
    assertThat(isMobileCeneterMethod.invoke(controller, FacilityEntity.builder().build()))
        .isEqualTo(Boolean.FALSE);
    assertThat(
            isMobileCeneterMethod.invoke(
                controller,
                FacilityEntity.builder()
                    .id(FacilityEntity.Pk.fromIdString("vha_123"))
                    .mobile(true)
                    .build()))
        .isEqualTo(Boolean.TRUE);
    assertThat(
            isMobileCeneterMethod.invoke(
                controller,
                FacilityEntity.builder()
                    .id(FacilityEntity.Pk.fromIdString("vha_123MVC").stationNumber("123MVC"))
                    .build()))
        .isEqualTo(Boolean.FALSE);
    assertThat(
            isMobileCeneterMethod.invoke(
                controller,
                FacilityEntity.builder()
                    .id(FacilityEntity.Pk.fromIdString("vha_123MVC").stationNumber("123MVC"))
                    .mobile(true)
                    .build()))
        .isEqualTo(Boolean.TRUE);
  }

  @Test
  public void populateException() {
    FacilityEntity mockEntity = mock(FacilityEntity.class);
    when(mockEntity.id()).thenReturn(null);
    DatamartFacility mockDatamartFacility = mock(DatamartFacility.class);
    when(mockDatamartFacility.attributes()).thenThrow(new NullPointerException("oh noes"));
    assertThrows(
        IllegalArgumentException.class,
        () -> InternalFacilitiesController.populate(mockEntity, mockDatamartFacility));
    FacilityRepository mockRepo = mock(FacilityRepository.class);
    when(mockRepo.findAll()).thenThrow(new NullPointerException("oh noes"));
    assertDoesNotThrow(
        () -> InternalFacilitiesController.builder().facilityRepository(mockRepo).build());
  }

  @Test
  @SneakyThrows
  public void processException() {
    final InternalFacilitiesController controller =
        InternalFacilitiesController.builder()
            .facilityRepository(mock(FacilityRepository.class))
            .build();
    Method processMethod =
        InternalFacilitiesController.class.getDeclaredMethod(
            "process", ReloadResponse.class, List.class);
    processMethod.setAccessible(true);
    ReloadResponse reloadResponseProc = ReloadResponse.start();
    DatamartFacility datamartFacilityWithInvalidId =
        DatamartFacility.builder().id("invalid-id").build();
    final ResponseEntity actualResponse =
        (ResponseEntity)
            processMethod.invoke(
                controller, reloadResponseProc, List.of(datamartFacilityWithInvalidId));
    assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(actualResponse.getBody()).isInstanceOf(ReloadResponse.class);
    assertThat(((ReloadResponse) actualResponse.getBody()).problems())
        .usingRecursiveComparison()
        .isEqualTo(List.of(ReloadResponse.Problem.of("invalid-id", "Cannot parse ID")));
  }

  @Test
  @SneakyThrows
  public void saveAsMissingException() {
    Method saveAsMissingMethod =
        InternalFacilitiesController.class.getDeclaredMethod(
            "saveAsMissing", ReloadResponse.class, FacilityEntity.class);
    saveAsMissingMethod.setAccessible(true);
    final FacilityEntity entityEx =
        FacilityEntity.builder().id(FacilityEntity.Pk.fromIdString("vha_123")).build();
    FacilityRepository mockRepo = mock(FacilityRepository.class);
    when(mockRepo.save(entityEx)).thenThrow(new NullPointerException("oh noes"));
    final InternalFacilitiesController controllerEx =
        InternalFacilitiesController.builder().facilityRepository(mockRepo).build();
    final ReloadResponse reloadResponseEx = ReloadResponse.start();
    assertThatThrownBy(() -> saveAsMissingMethod.invoke(controllerEx, reloadResponseEx, entityEx))
        .isInstanceOf(InvocationTargetException.class)
        .hasCause(new NullPointerException("oh noes"));
    assertThat(reloadResponseEx.problems())
        .usingRecursiveComparison()
        .isEqualTo(
            List.of(
                ReloadResponse.Problem.of(
                    "vha_123", "Failed to mark facility as missing: oh noes")));
  }

  @Test
  void servicesOf() {
    assertThat(
            InternalFacilitiesController.serviceTypesOf(
                DatamartFacility.builder()
                    .attributes(FacilityAttributes.builder().build())
                    .build()))
        .isEmpty();
    assertThat(
            InternalFacilitiesController.serviceTypesOf(
                DatamartFacility.builder()
                    .attributes(
                        FacilityAttributes.builder().services(Services.builder().build()).build())
                    .build()))
        .isEmpty();
    assertThat(
            InternalFacilitiesController.serviceTypesOf(
                DatamartFacility.builder()
                    .attributes(
                        FacilityAttributes.builder()
                            .services(
                                Services.builder()
                                    .health(List.of())
                                    .benefits(List.of())
                                    .other(List.of())
                                    .build())
                            .build())
                    .build()))
        .isEmpty();
    assertThat(
            InternalFacilitiesController.serviceTypesOf(
                DatamartFacility.builder()
                    .attributes(
                        FacilityAttributes.builder()
                            .services(
                                Services.builder()
                                    .health(
                                        List.of(
                                            Service.<HealthService>builder()
                                                .serviceType(HealthService.PrimaryCare)
                                                .name(HealthService.PrimaryCare.name())
                                                .build(),
                                            Service.<HealthService>builder()
                                                .serviceType(HealthService.MentalHealth)
                                                .name(HealthService.MentalHealth.name())
                                                .build()))
                                    .benefits(
                                        List.of(
                                            Service.<BenefitsService>builder()
                                                .serviceType(BenefitsService.ApplyingForBenefits)
                                                .name(BenefitsService.ApplyingForBenefits.name())
                                                .build(),
                                            Service.<BenefitsService>builder()
                                                .serviceType(BenefitsService.BurialClaimAssistance)
                                                .name(BenefitsService.BurialClaimAssistance.name())
                                                .build()))
                                    .other(
                                        List.of(
                                            Service.<OtherService>builder()
                                                .serviceType(OtherService.OnlineScheduling)
                                                .name(OtherService.OnlineScheduling.name())
                                                .build()))
                                    .build())
                            .build())
                    .build()))
        .containsExactlyInAnyOrder(
            Service.<HealthService>builder()
                .serviceType(HealthService.PrimaryCare)
                .name(HealthService.PrimaryCare.name())
                .build(),
            Service.<HealthService>builder()
                .serviceType(HealthService.MentalHealth)
                .name(HealthService.MentalHealth.name())
                .build(),
            Service.<BenefitsService>builder()
                .serviceType(BenefitsService.ApplyingForBenefits)
                .name(BenefitsService.ApplyingForBenefits.name())
                .build(),
            Service.<BenefitsService>builder()
                .serviceType(BenefitsService.BurialClaimAssistance)
                .name(BenefitsService.BurialClaimAssistance.name())
                .build(),
            Service.<OtherService>builder()
                .serviceType(OtherService.OnlineScheduling)
                .name(OtherService.OnlineScheduling.name())
                .build());
  }

  @BeforeEach
  void setup() {
    mockCollector = mock(FacilitiesCollector.class);
    mockCmsOverlayCollector = mock(CmsOverlayCollector.class);
  }

  private <T extends TypedService> void setupFacilityServices(
      @NonNull String facilityId, @NonNull String linkerUrl, List<Service<T>> facilityServices) {
    if (ObjectUtils.isNotEmpty(facilityServices)) {
      facilityServices.stream()
          .forEach(
              fs ->
                  facilityServicesRepository.save(
                      FacilitySamples.defaultSamples(linkerUrl)
                          .facilityServicesEntity(facilityId, fs)));
    }
  }

  @Test
  void stateOf() {
    // No address
    assertThat(
            InternalFacilitiesController.stateOf(
                DatamartFacility.builder()
                    .attributes(FacilityAttributes.builder().build())
                    .build()))
        .isNull();
    // No physical or mailing
    assertThat(
            InternalFacilitiesController.stateOf(
                DatamartFacility.builder()
                    .attributes(
                        FacilityAttributes.builder().address(Addresses.builder().build()).build())
                    .build()))
        .isNull();
    // No Physical zip
    assertThat(
            InternalFacilitiesController.stateOf(
                DatamartFacility.builder()
                    .attributes(
                        FacilityAttributes.builder()
                            .address(
                                Addresses.builder().physical(Address.builder().build()).build())
                            .build())
                    .build()))
        .isNull();
    // Physical zip
    assertThat(
            InternalFacilitiesController.stateOf(
                DatamartFacility.builder()
                    .attributes(
                        FacilityAttributes.builder()
                            .address(
                                Addresses.builder()
                                    .physical(Address.builder().state("FL").build())
                                    .build())
                            .build())
                    .build()))
        .isEqualTo("FL");
  }

  @Test
  void updateAndSave_error() {
    final var linkerUrl = buildLinkerUrlV1("http://foo/", "bar");
    final var facilityId = "vha_f1";
    FacilityRepository repo = mock(FacilityRepository.class);
    when(repo.save(any(FacilityEntity.class))).thenThrow(new RuntimeException("oh noez"));
    InternalFacilitiesController controller =
        InternalFacilitiesController.builder().facilityRepository(repo).build();
    DatamartFacility f1 =
        _facility(
            facilityId,
            "CO",
            "5319",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.MentalHealthCare));
    DatamartFacility f1V1 =
        _facilityV1(
            facilityId,
            "FL",
            "South",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.Service
                    .<gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService>builder()
                    .serviceType(
                        gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealth)
                    .name(
                        gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealth
                            .name())
                    .link(
                        buildTypedServiceLink(
                            linkerUrl,
                            facilityId,
                            gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService
                                .MentalHealth.serviceId()))
                    .build()));
    f1.attributes().address().mailing(Address.builder().zip("12345-56").build());
    f1V1.attributes().address().mailing(Address.builder().zip("12345-56").build());
    ReloadResponse response = ReloadResponse.start();
    assertThrows(
        RuntimeException.class,
        () ->
            controller.updateAndSave(
                response,
                FacilityEntity.builder().id(FacilityEntity.Pk.fromIdString("vha_f1")).build(),
                f1));
    assertThat(response.problems())
        .isEqualTo(
            List.of(
                ReloadResponse.Problem.of("vha_f1", "Missing or invalid physical address zip"),
                ReloadResponse.Problem.of("vha_f1", "Missing physical address city"),
                ReloadResponse.Problem.of("vha_f1", "Missing physical address street information"),
                ReloadResponse.Problem.of("vha_f1", "Missing or invalid mailing address zip"),
                ReloadResponse.Problem.of("vha_f1", "Missing mailing address state"),
                ReloadResponse.Problem.of("vha_f1", "Missing mailing address city"),
                ReloadResponse.Problem.of("vha_f1", "Missing mailing address street information"),
                ReloadResponse.Problem.of("vha_f1", "Missing main phone number"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Monday"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Tuesday"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Wednesday"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Thursday"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Friday"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Saturday"),
                ReloadResponse.Problem.of("vha_f1", "Missing hours Sunday"),
                ReloadResponse.Problem.of("vha_f1", "Missing classification"),
                ReloadResponse.Problem.of("vha_f1", "Failed to save record: oh noez")));
  }

  @Test
  void updateAndSave_replaceInstructions() {
    final var linkerUrl = buildLinkerUrlV1("http://foo/", "bar");
    DatamartFacility f1 =
        _facility(
            "vha_f1",
            "FL",
            "32934",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.MentalHealthCare));
    DatamartFacility f2 =
        _facility(
            "vha_f2",
            "FL",
            "32934",
            5.6,
            6.7,
            List.of(gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.UrgentCare));
    DatamartFacility f3 =
        _facility(
            "vha_f3",
            "FL",
            "32934",
            5.6,
            6.7,
            List.of(gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.UrgentCare));
    DatamartFacility f1V1 =
        _facilityV1(
            "vha_f1",
            "FL",
            "South",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.Service
                    .<gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService>builder()
                    .serviceType(
                        gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealth)
                    .name(
                        gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealth
                            .name())
                    .link(
                        buildTypedServiceLink(
                            linkerUrl,
                            "vha_f1",
                            gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService
                                .MentalHealth.serviceId()))
                    .build()));
    DatamartFacility f2V1 =
        _facilityV1(
            "vha_f2",
            "NEAT",
            "32934",
            5.6,
            6.7,
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.Service
                    .<gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService>builder()
                    .serviceType(
                        gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.UrgentCare)
                    .name(
                        gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.UrgentCare
                            .name())
                    .link(
                        buildTypedServiceLink(
                            linkerUrl,
                            "vha_f2",
                            gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService
                                .UrgentCare.serviceId()))
                    .build()));
    DatamartFacility f3V1 =
        _facilityV1(
            "vha_f3",
            "FL",
            "32934",
            5.6,
            6.7,
            List.of(
                gov.va.api.lighthouse.facilities.api.v1.Facility.Service
                    .<gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService>builder()
                    .serviceType(
                        gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.UrgentCare)
                    .name(
                        gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.UrgentCare
                            .name())
                    .link(
                        buildTypedServiceLink(
                            linkerUrl,
                            "vha_f3",
                            gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService
                                .UrgentCare.serviceId()))
                    .build()));
    f1.attributes().operationalHoursSpecialInstructions(SPECIAL_INSTRUCTION_OLD_1);
    f2.attributes().operationalHoursSpecialInstructions(SPECIAL_INSTRUCTION_OLD_2);
    f3.attributes().operationalHoursSpecialInstructions(SPECIAL_INSTRUCTION_OLD_3);
    f1V1.attributes().operationalHoursSpecialInstructions(SPECIAL_INSTRUCTION_OLD_1);
    f2V1.attributes().operationalHoursSpecialInstructions(SPECIAL_INSTRUCTION_OLD_2);
    f3V1.attributes().operationalHoursSpecialInstructions(SPECIAL_INSTRUCTION_OLD_3);
    ReloadResponse response = ReloadResponse.start();
    _controller()
        .updateAndSave(
            response,
            FacilityEntity.builder().id(FacilityEntity.Pk.fromIdString("vha_f1")).build(),
            f1);
    assertThat(f1.attributes().operationalHoursSpecialInstructions())
        .isEqualTo(SPECIAL_INSTRUCTION_UPDATED_1);
    assertThat(
            facilityRepository.findById(FacilityEntity.Pk.fromIdString("vha_f1")).get().facility())
        .contains(SPECIAL_INSTRUCTION_UPDATED_1);
    _controller()
        .updateAndSave(
            response,
            FacilityEntity.builder().id(FacilityEntity.Pk.fromIdString("vha_f2")).build(),
            f2);
    assertThat(f2.attributes().operationalHoursSpecialInstructions())
        .isEqualTo(SPECIAL_INSTRUCTION_UPDATED_2);
    assertThat(
            facilityRepository.findById(FacilityEntity.Pk.fromIdString("vha_f2")).get().facility())
        .contains(SPECIAL_INSTRUCTION_UPDATED_2);
    _controller()
        .updateAndSave(
            response,
            FacilityEntity.builder().id(FacilityEntity.Pk.fromIdString("vha_f3")).build(),
            f3);
    assertThat(f3.attributes().operationalHoursSpecialInstructions())
        .isEqualTo(SPECIAL_INSTRUCTION_UPDATED_3);
    assertThat(
            facilityRepository.findById(FacilityEntity.Pk.fromIdString("vha_f3")).get().facility())
        .contains(SPECIAL_INSTRUCTION_UPDATED_3);
  }

  @Test
  @SneakyThrows
  public void updateFacilityException() {
    final InternalFacilitiesController controller = InternalFacilitiesController.builder().build();
    Method updateFacilityMethod =
        InternalFacilitiesController.class.getDeclaredMethod(
            "updateFacility", ReloadResponse.class, DatamartFacility.class);
    updateFacilityMethod.setAccessible(true);
    final ReloadResponse reloadResponse = ReloadResponse.start();
    DatamartFacility datamartFacilityWithInvalidId =
        DatamartFacility.builder().id("invalid-id").build();
    updateFacilityMethod.invoke(controller, reloadResponse, datamartFacilityWithInvalidId);
    assertThat(reloadResponse.problems())
        .usingRecursiveComparison()
        .isEqualTo(List.of(ReloadResponse.Problem.of("invalid-id", "Cannot parse ID")));
  }

  @Test
  @SneakyThrows
  void upload() {
    DatamartFacility f1 =
        _facility(
            "vha_f91",
            "FU",
            "South",
            1.2,
            3.4,
            List.of(
                gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.MentalHealthCare));
    DatamartFacility f2 =
        _facility(
            "vha_f92",
            "NEAT",
            "32934",
            5.6,
            6.7,
            List.of(gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.UrgentCare));
    List<DatamartFacility> collectedFacilities = List.of(f1, f2);
    _controller().upload(collectedFacilities);
    RecursiveComparisonConfiguration comparisonConfig =
        RecursiveComparisonConfiguration.builder()
            .withIgnoredFields("version", "lastUpdated")
            .build();
    assertThat(facilityRepository.findAll())
        .usingRecursiveFieldByFieldElementComparator(comparisonConfig)
        .containsExactlyInAnyOrder(_facilityEntity(f1), _facilityEntity(f2));
  }

  @Test
  void zipOf() {
    // No address
    assertThat(
            InternalFacilitiesController.zipOf(
                DatamartFacility.builder()
                    .attributes(FacilityAttributes.builder().build())
                    .build()))
        .isNull();
    // No physical or mailing
    assertThat(
            InternalFacilitiesController.zipOf(
                DatamartFacility.builder()
                    .attributes(
                        FacilityAttributes.builder().address(Addresses.builder().build()).build())
                    .build()))
        .isNull();
    // No Physical zip
    assertThat(
            InternalFacilitiesController.zipOf(
                DatamartFacility.builder()
                    .attributes(
                        FacilityAttributes.builder()
                            .address(
                                Addresses.builder().physical(Address.builder().build()).build())
                            .build())
                    .build()))
        .isNull();
    // Physical zip
    assertThat(
            InternalFacilitiesController.zipOf(
                DatamartFacility.builder()
                    .attributes(
                        FacilityAttributes.builder()
                            .address(
                                Addresses.builder()
                                    .physical(Address.builder().zip("12345").build())
                                    .build())
                            .build())
                    .build()))
        .isEqualTo("12345");
    // Physical zip that is long
    assertThat(
            InternalFacilitiesController.zipOf(
                DatamartFacility.builder()
                    .attributes(
                        FacilityAttributes.builder()
                            .address(
                                Addresses.builder()
                                    .physical(Address.builder().zip("12345-9876").build())
                                    .build())
                            .build())
                    .build()))
        .isEqualTo("12345");
  }
}
