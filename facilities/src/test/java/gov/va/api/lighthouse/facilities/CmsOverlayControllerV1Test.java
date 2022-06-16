package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.CMS_OVERLAY_SERVICE_NAME_COVID_19;
import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.updateServiceUrlPaths;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import gov.va.api.lighthouse.facilities.api.v1.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v1.CmsOverlayResponse;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService;
import gov.va.api.lighthouse.facilities.api.v1.DetailedServiceResponse;
import gov.va.api.lighthouse.facilities.api.v1.DetailedServicesResponse;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import gov.va.api.lighthouse.facilities.api.v1.PageLinks;
import gov.va.api.lighthouse.facilities.api.v1.Pagination;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class CmsOverlayControllerV1Test {
  @Mock FacilityRepository mockFacilityRepository;

  @Mock CmsOverlayRepository mockCmsOverlayRepository;

  CmsOverlayControllerV1 controller() {
    return CmsOverlayControllerV1.builder()
        .facilityRepository(mockFacilityRepository)
        .cmsOverlayRepository(mockCmsOverlayRepository)
        .baseUrl("http://foo/")
        .basePath("bp")
        .build();
  }

  private List<DatamartDetailedService> detailedServices(boolean isActive) {
    return List.of(
        getCardiologyDetailedService(isActive),
        getCovid19DetailedService(isActive),
        getUrologyDetailedService(isActive));
  }

  @Test
  public void exceptions() {
    var id = "vha_041";
    var pk = FacilityEntity.Pk.fromIdString(id);
    var page = 1;
    var perPage = 10;
    when(mockCmsOverlayRepository.findById(pk)).thenThrow(new NullPointerException("oh noes"));
    assertThatThrownBy(() -> controller().getExistingOverlayEntity(pk))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("oh noes");
    assertThatThrownBy(() -> controller().getDetailedServices(id, page, perPage))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("oh noes");
    when(mockFacilityRepository.findById(pk)).thenThrow(new NullPointerException("oh noes"));
    assertThatThrownBy(
            () -> controller().saveOverlay(id, CmsOverlayTransformerV1.toCmsOverlay(overlay())))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("oh noes");
    assertThatThrownBy(() -> controller().getDetailedServices("vha_000", page, perPage))
        .isInstanceOf(ExceptionsUtils.NotFound.class)
        .hasMessage("The record identified by vha_000 could not be found");
  }

  private DatamartDetailedService getCardiologyDetailedService(boolean isActive) {
    return DatamartDetailedService.builder()
        .name(DatamartFacility.HealthService.Cardiology.name())
        .active(isActive)
        .changed(null)
        .descriptionFacility(null)
        .appointmentLeadIn("Your VA health care team will contact you if you...more text")
        .onlineSchedulingAvailable("True")
        .path("replaceable path here")
        .phoneNumbers(
            List.of(
                DatamartDetailedService.AppointmentPhoneNumber.builder()
                    .extension("123")
                    .label("Main phone")
                    .number("555-555-1234")
                    .type("tel")
                    .build()))
        .referralRequired("True")
        .walkInsAccepted("False")
        .serviceLocations(
            List.of(
                DatamartDetailedService.DetailedServiceLocation.builder()
                    .serviceLocationAddress(
                        DatamartDetailedService.DetailedServiceAddress.builder()
                            .buildingNameNumber("Baxter Building")
                            .clinicName("Baxter Clinic")
                            .wingFloorOrRoomNumber("Wing East")
                            .address1("122 Main St.")
                            .address2(null)
                            .city("Rochester")
                            .state("NY")
                            .zipCode("14623-1345")
                            .countryCode("US")
                            .build())
                    .appointmentPhoneNumbers(
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
                    .facilityServiceHours(
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

  private DatamartDetailedService getCovid19DetailedService(boolean isActive) {
    return DatamartDetailedService.builder()
        .name(CMS_OVERLAY_SERVICE_NAME_COVID_19)
        .active(isActive)
        .changed(null)
        .descriptionFacility(null)
        .appointmentLeadIn("Your VA health care team will contact you if you...more text")
        .onlineSchedulingAvailable("True")
        .path("replaceable path here")
        .phoneNumbers(
            List.of(
                DatamartDetailedService.AppointmentPhoneNumber.builder()
                    .extension("123")
                    .label("Main phone")
                    .number("555-555-1212")
                    .type("tel")
                    .build()))
        .referralRequired("True")
        .walkInsAccepted("False")
        .serviceLocations(
            List.of(
                DatamartDetailedService.DetailedServiceLocation.builder()
                    .serviceLocationAddress(
                        DatamartDetailedService.DetailedServiceAddress.builder()
                            .buildingNameNumber("Baxter Building")
                            .clinicName("Baxter Clinic")
                            .wingFloorOrRoomNumber("Wing East")
                            .address1("122 Main St.")
                            .address2(null)
                            .city("Rochester")
                            .state("NY")
                            .zipCode("14623-1345")
                            .countryCode("US")
                            .build())
                    .appointmentPhoneNumbers(
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
                    .facilityServiceHours(
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

  @Test
  @SneakyThrows
  public void getDetailedService() {
    DatamartCmsOverlay overlay = overlay();
    var facilityId = "vha_402";
    var pk = FacilityEntity.Pk.fromIdString(facilityId);
    var serviceId = CMS_OVERLAY_SERVICE_NAME_COVID_19;
    CmsOverlayEntity cmsOverlayEntity =
        CmsOverlayEntity.builder()
            .id(pk)
            .cmsOperatingStatus(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.operatingStatus()))
            .cmsServices(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.detailedServices()))
            .build();
    when(mockCmsOverlayRepository.findById(pk)).thenReturn(Optional.of(cmsOverlayEntity));
    assertThat(controller().getDetailedService(facilityId, serviceId))
        .usingRecursiveComparison()
        .isEqualTo(
            ResponseEntity.ok(
                DetailedServiceResponse.builder()
                    .data(
                        DetailedServiceTransformerV1.toDetailedService(
                            getCovid19DetailedService(false)))
                    .build()));

    assertThatThrownBy(() -> controller().getDetailedService("vha_000", "cardiology"))
        .isInstanceOf(ExceptionsUtils.NotFound.class)
        .hasMessage("The record identified by vha_000 could not be found");
  }

  @Test
  @SneakyThrows
  public void getDetailedServices() {
    DatamartCmsOverlay overlay = overlay();
    var facilityId = "vha_402";
    var pk = FacilityEntity.Pk.fromIdString(facilityId);
    var page = 1;
    var perPage = 1;
    CmsOverlayEntity cmsOverlayEntity =
        CmsOverlayEntity.builder()
            .id(pk)
            .cmsOperatingStatus(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.operatingStatus()))
            .cmsServices(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.detailedServices()))
            .build();
    when(mockCmsOverlayRepository.findById(pk)).thenReturn(Optional.of(cmsOverlayEntity));
    // Obtain first page of detailed services - cardiology detailed service
    ResponseEntity<DetailedServicesResponse> test =
        controller().getDetailedServices(facilityId, page, perPage);
    assertThat(controller().getDetailedServices(facilityId, page, perPage))
        .usingRecursiveComparison()
        .isEqualTo(
            ResponseEntity.ok(
                DetailedServicesResponse.builder()
                    .data(
                        DetailedServiceTransformerV1.toDetailedServices(
                            List.of(getCardiologyDetailedService(false))))
                    .links(
                        PageLinks.builder()
                            .self("http://foo/bp/v1/facilities/vha_402/services?page=1&per_page=1")
                            .first("http://foo/bp/v1/facilities/vha_402/services?page=1&per_page=1")
                            .prev(null)
                            .next("http://foo/bp/v1/facilities/vha_402/services?page=2&per_page=1")
                            .last("http://foo/bp/v1/facilities/vha_402/services?page=3&per_page=1")
                            .build())
                    .meta(
                        DetailedServicesResponse.DetailedServicesMetadata.builder()
                            .pagination(
                                Pagination.builder()
                                    .currentPage(1)
                                    .entriesPerPage(1)
                                    .totalPages(3)
                                    .totalEntries(3)
                                    .build())
                            .build())
                    .build()));
    // Obtain second page of detailed services - covid-19 detailed service
    page = 2;
    assertThat(controller().getDetailedServices(facilityId, page, perPage))
        .usingRecursiveComparison()
        .isEqualTo(
            ResponseEntity.ok(
                DetailedServicesResponse.builder()
                    .data(
                        DetailedServiceTransformerV1.toDetailedServices(
                            List.of(getCovid19DetailedService(false))))
                    .links(
                        PageLinks.builder()
                            .self("http://foo/bp/v1/facilities/vha_402/services?page=2&per_page=1")
                            .first("http://foo/bp/v1/facilities/vha_402/services?page=1&per_page=1")
                            .prev("http://foo/bp/v1/facilities/vha_402/services?page=1&per_page=1")
                            .next("http://foo/bp/v1/facilities/vha_402/services?page=3&per_page=1")
                            .last("http://foo/bp/v1/facilities/vha_402/services?page=3&per_page=1")
                            .build())
                    .meta(
                        DetailedServicesResponse.DetailedServicesMetadata.builder()
                            .pagination(
                                Pagination.builder()
                                    .currentPage(2)
                                    .entriesPerPage(1)
                                    .totalPages(3)
                                    .totalEntries(3)
                                    .build())
                            .build())
                    .build()));
    // Obtain third and final page of detailed services - urology detailed service
    page = 3;
    assertThat(controller().getDetailedServices(facilityId, page, perPage))
        .usingRecursiveComparison()
        .isEqualTo(
            ResponseEntity.ok(
                DetailedServicesResponse.builder()
                    .data(
                        DetailedServiceTransformerV1.toDetailedServices(
                            List.of(getUrologyDetailedService(false))))
                    .links(
                        PageLinks.builder()
                            .self("http://foo/bp/v1/facilities/vha_402/services?page=3&per_page=1")
                            .first("http://foo/bp/v1/facilities/vha_402/services?page=1&per_page=1")
                            .prev("http://foo/bp/v1/facilities/vha_402/services?page=2&per_page=1")
                            .next(null)
                            .last("http://foo/bp/v1/facilities/vha_402/services?page=3&per_page=1")
                            .build())
                    .meta(
                        DetailedServicesResponse.DetailedServicesMetadata.builder()
                            .pagination(
                                Pagination.builder()
                                    .currentPage(3)
                                    .entriesPerPage(1)
                                    .totalPages(3)
                                    .totalEntries(3)
                                    .build())
                            .build())
                    .build()));
  }

  @Test
  @SneakyThrows
  void getExistingOverlay() {
    DatamartCmsOverlay overlay = overlay();
    var pk = FacilityEntity.Pk.fromIdString("vha_402");
    CmsOverlayEntity cmsOverlayEntity =
        CmsOverlayEntity.builder()
            .id(pk)
            .cmsOperatingStatus(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.operatingStatus()))
            .cmsServices(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.detailedServices()))
            .healthCareSystem(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.healthCareSystem()))
            .build();
    when(mockCmsOverlayRepository.findById(pk)).thenReturn(Optional.of(cmsOverlayEntity));
    // active will ALWAYS be false when retrieving from the database, the fact the overlay
    // exists means that active was true at the time of insertion
    for (DatamartDetailedService d : overlay.detailedServices()) {
      d.active(false);
    }
    ResponseEntity<CmsOverlayResponse> response = controller().getOverlay("vha_402");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(CmsOverlayTransformerV1.toVersionAgnostic(response.getBody().overlay()))
        .isEqualTo(overlay);
  }

  @Test
  void getNonExistingOverlay() {
    assertThatThrownBy(() -> controller().getOverlay("vha_041"))
        .isInstanceOf(ExceptionsUtils.NotFound.class)
        .hasMessage("The record identified by vha_041 could not be found");
  }

  private DatamartDetailedService getUrologyDetailedService(boolean isActive) {
    return DatamartDetailedService.builder()
        .name(DatamartFacility.HealthService.Urology.name())
        .active(isActive)
        .changed(null)
        .descriptionFacility(null)
        .appointmentLeadIn("Your VA health care team will contact you if you...more text")
        .onlineSchedulingAvailable("True")
        .path("replaceable path here")
        .phoneNumbers(
            List.of(
                DatamartDetailedService.AppointmentPhoneNumber.builder()
                    .extension("123")
                    .label("Main phone")
                    .number("555-867-5309")
                    .type("tel")
                    .build()))
        .referralRequired("True")
        .walkInsAccepted("False")
        .serviceLocations(
            List.of(
                DatamartDetailedService.DetailedServiceLocation.builder()
                    .serviceLocationAddress(
                        DatamartDetailedService.DetailedServiceAddress.builder()
                            .buildingNameNumber("Baxter Building")
                            .clinicName("Baxter Clinic")
                            .wingFloorOrRoomNumber("Wing East")
                            .address1("122 Main St.")
                            .address2(null)
                            .city("Rochester")
                            .state("NY")
                            .zipCode("14623-1345")
                            .countryCode("US")
                            .build())
                    .appointmentPhoneNumbers(
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
                    .facilityServiceHours(
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

  private DatamartCmsOverlay overlay() {
    return DatamartCmsOverlay.builder()
        .operatingStatus(
            DatamartFacility.OperatingStatus.builder()
                .code(DatamartFacility.OperatingStatusCode.NOTICE)
                .additionalInfo("i need attention")
                .build())
        .detailedServices(detailedServices(true))
        .build();
  }

  @Test
  @SneakyThrows
  void updateFacilityWithOverlayData() {
    DatamartCmsOverlay overlay = overlay();
    overlay.operatingStatus(
        DatamartFacility.OperatingStatus.builder()
            .code(DatamartFacility.OperatingStatusCode.CLOSED)
            .additionalInfo("i need attention")
            .build());
    var pk = FacilityEntity.Pk.fromIdString("vha_402");
    Facility f =
        Facility.builder()
            .id("vha_402")
            .attributes(Facility.FacilityAttributes.builder().website("va.gov").build())
            .build();
    FacilityEntity facilityEntity =
        FacilityEntity.builder()
            .id(pk)
            .services(new HashSet<>())
            .facility(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(FacilityTransformerV1.toVersionAgnostic(f)))
            .build();
    when(mockFacilityRepository.findById(pk)).thenReturn(Optional.of(facilityEntity));
    CmsOverlayEntity cmsOverlayEntity =
        CmsOverlayEntity.builder()
            .id(pk)
            .cmsOperatingStatus(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.operatingStatus()))
            .cmsServices(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.detailedServices()))
            .build();
    when(mockCmsOverlayRepository.findById(pk)).thenReturn(Optional.of(cmsOverlayEntity));
    controller().saveOverlay("vha_402", CmsOverlayTransformerV1.toCmsOverlay(overlay));
    DatamartCmsOverlay updatedCovidPathOverlay = overlay();
    List<DatamartDetailedService> datamartDetailedServices =
        updatedCovidPathOverlay.detailedServices();
    updateServiceUrlPaths("vha_402", datamartDetailedServices);
    updatedCovidPathOverlay.detailedServices(datamartDetailedServices);
    // active will ALWAYS be false when retrieving from the database, the fact the overlay
    // exists means that active was true at the time of insertion
    for (DatamartDetailedService d : datamartDetailedServices) {
      d.active(false);
    }
    // Verify that facility is updated with detailed services from overlay
    FacilityEntity updatedFacilityEntity = mockFacilityRepository.findById(pk).get();
    Facility facility =
        FacilityTransformerV1.toFacility(
            DatamartFacilitiesJacksonConfig.createMapper()
                .readValue(updatedFacilityEntity.facility(), DatamartFacility.class));
    assertThat(facility.attributes().activeStatus()).isEqualTo(Facility.ActiveStatus.T);
    assertThat(facility.attributes().operatingStatus())
        .usingRecursiveComparison()
        .isEqualTo(CmsOverlayTransformerV1.toCmsOverlay(overlay).operatingStatus());
  }

  @Test
  @SneakyThrows
  void updateHealthCareSystem() {
    DatamartCmsOverlay overlay = overlay();
    var pk = FacilityEntity.Pk.fromIdString("vha_402");
    CmsOverlayEntity cmsOverlayEntity =
        CmsOverlayEntity.builder()
            .id(pk)
            .cmsOperatingStatus(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.operatingStatus()))
            .cmsServices(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.detailedServices()))
            .healthCareSystem(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.healthCareSystem()))
            .build();
    when(mockCmsOverlayRepository.findById(pk)).thenReturn(Optional.of(cmsOverlayEntity));
    controller().saveOverlay(pk.toIdString(), CmsOverlayTransformerV1.toCmsOverlay(overlay));
    var response = controller().getOverlay(pk.toIdString());
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().overlay().healthCareSystem())
        .isEqualTo(CmsOverlayTransformerV1.toCmsOverlay(overlay()).healthCareSystem());
  }

  @Test
  @SneakyThrows
  void updateIsAcceptedForKnownStation() {
    Facility f =
        Facility.builder()
            .id("vha_402")
            .attributes(Facility.FacilityAttributes.builder().website("va.gov").build())
            .build();
    var pk = FacilityEntity.Pk.fromIdString("vha_402");
    FacilityEntity entity =
        FacilityEntity.builder()
            .id(pk)
            .facility(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(FacilityTransformerV1.toVersionAgnostic(f)))
            .build();
    when(mockFacilityRepository.findById(pk)).thenReturn(Optional.of(entity));
    DatamartCmsOverlay overlay = overlay();
    ResponseEntity<Void> response =
        controller().saveOverlay("vha_402", CmsOverlayTransformerV1.toCmsOverlay(overlay));
    Set<String> detailedServices = new HashSet<>();
    for (DatamartDetailedService service : overlay.detailedServices()) {
      if (service.active()) {
        detailedServices.add(service.name());
      }
    }
    entity.cmsOperatingStatus(
        DatamartFacilitiesJacksonConfig.createMapper()
            .writeValueAsString(overlay.operatingStatus()));
    entity.overlayServices(detailedServices);
    verify(mockFacilityRepository).save(entity);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void updateIsSkippedForUnknownStation() {
    var pk = FacilityEntity.Pk.fromIdString("vha_666");
    when(mockFacilityRepository.findById(pk)).thenReturn(Optional.empty());
    ResponseEntity<Void> response =
        controller().saveOverlay("vha_666", CmsOverlayTransformerV1.toCmsOverlay(overlay()));
    verifyNoMoreInteractions(mockFacilityRepository);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
  }

  @Test
  @SneakyThrows
  void updateOverlayWithNoExistingServices() {
    DatamartCmsOverlay overlay = overlay();
    var pk = FacilityEntity.Pk.fromIdString("vha_402");
    CmsOverlayEntity cmsOverlayEntity =
        CmsOverlayEntity.builder()
            .id(pk)
            .cmsOperatingStatus(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.operatingStatus()))
            .cmsServices(null)
            .build();
    when(mockCmsOverlayRepository.findById(pk)).thenReturn(Optional.of(cmsOverlayEntity));
    List<DatamartDetailedService> detailedServices =
        List.of(
            DatamartDetailedService.builder().name("additional service1").active(true).build(),
            DatamartDetailedService.builder().name("additional service2").active(true).build());
    overlay.detailedServices(detailedServices);
    controller().saveOverlay("vha_402", CmsOverlayTransformerV1.toCmsOverlay(overlay));
    DatamartCmsOverlay updatedCovidPathOverlay = overlay();
    List<DatamartDetailedService> datamartDetailedServices =
        updatedCovidPathOverlay.detailedServices();
    updateServiceUrlPaths("vha_402", datamartDetailedServices);
    updatedCovidPathOverlay.detailedServices(datamartDetailedServices);
    // active will ALWAYS be false when retrieving from the database, the fact the overlay
    // exists means that active was true at the time of insertion
    for (DatamartDetailedService d : detailedServices) {
      d.active(false);
    }
    ResponseEntity<CmsOverlayResponse> response = controller().getOverlay("vha_402");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(
            DetailedServiceTransformerV1.toVersionAgnosticDetailedServices(
                response.getBody().overlay().detailedServices()))
        .containsAll(detailedServices);
  }

  @Test
  @SneakyThrows
  void updateWithExistingOverlay() {
    DatamartCmsOverlay overlay = overlay();
    var pk = FacilityEntity.Pk.fromIdString("vha_402");
    CmsOverlayEntity cmsOverlayEntity =
        CmsOverlayEntity.builder()
            .id(pk)
            .cmsOperatingStatus(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.operatingStatus()))
            .cmsServices(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.detailedServices()))
            .healthCareSystem(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(overlay.healthCareSystem()))
            .build();
    when(mockCmsOverlayRepository.findById(pk)).thenReturn(Optional.of(cmsOverlayEntity));
    List<DatamartDetailedService> additionalServices =
        List.of(
            DatamartDetailedService.builder().name("additional service1").active(true).build(),
            DatamartDetailedService.builder().name("additional service2").active(true).build());
    overlay.detailedServices(additionalServices);
    controller().saveOverlay("vha_402", CmsOverlayTransformerV1.toCmsOverlay(overlay));
    DatamartCmsOverlay updatedCovidPathOverlay = overlay();
    List<DatamartDetailedService> datamartDetailedServices =
        updatedCovidPathOverlay.detailedServices();
    updateServiceUrlPaths("vha_402", datamartDetailedServices);
    updatedCovidPathOverlay.detailedServices(datamartDetailedServices);
    List<DatamartDetailedService> combinedServices =
        Streams.stream(
                Iterables.concat(updatedCovidPathOverlay.detailedServices(), additionalServices))
            .toList();
    // active will ALWAYS be false when retrieving from the database, the fact the overlay
    // exists means that active was true at the time of insertion
    for (DatamartDetailedService d : combinedServices) {
      d.active(false);
    }
    ResponseEntity<CmsOverlayResponse> response = controller().getOverlay("vha_402");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(
            DetailedServiceTransformerV1.toVersionAgnosticDetailedServices(
                response.getBody().overlay().detailedServices()))
        .containsAll(combinedServices);
  }

  @Test
  @SneakyThrows
  void verifyServicePathUpdated() {
    Facility f =
        Facility.builder()
            .id("vha_402")
            .attributes(Facility.FacilityAttributes.builder().website("va.gov").build())
            .build();
    var pk = FacilityEntity.Pk.fromIdString("vha_402");
    FacilityEntity entity =
        FacilityEntity.builder()
            .id(pk)
            .facility(
                DatamartFacilitiesJacksonConfig.createMapper()
                    .writeValueAsString(FacilityTransformerV1.toVersionAgnostic(f)))
            .build();
    when(mockFacilityRepository.findById(pk)).thenReturn(Optional.of(entity));
    DatamartCmsOverlay overlay = overlay();
    for (DatamartDetailedService d : overlay.detailedServices()) {
      if (d.name().equals(CMS_OVERLAY_SERVICE_NAME_COVID_19)) {
        assertThat(d.path()).isEqualTo("replaceable path here");
      }
    }
    CmsOverlay cmsOverlay = CmsOverlayTransformerV1.toCmsOverlay(overlay);
    controller().saveOverlay("vha_402", cmsOverlay);
    for (DetailedService d : cmsOverlay.detailedServices()) {
      if (d.name().equals(CMS_OVERLAY_SERVICE_NAME_COVID_19)) {
        assertThat(d.path())
            .isEqualTo("https://www.va.gov/maine-health-care/programs/covid-19-vaccines/");
      }
    }
  }
}
