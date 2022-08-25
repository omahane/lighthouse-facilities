package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.CMS_OVERLAY_SERVICE_NAME_COVID_19;
import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.updateServiceUrlPaths;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.uncapitalize;
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
import gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService;
import gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService;
import gov.va.api.lighthouse.facilities.api.v1.Facility.OtherService;
import gov.va.api.lighthouse.facilities.api.v1.PageLinks;
import gov.va.api.lighthouse.facilities.api.v1.Pagination;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
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

  private DatamartCmsOverlay activeOverlay() {
    return overlay(
        List.of(HealthService.Cardiology, HealthService.Covid19Vaccine, HealthService.Urology),
        true);
  }

  CmsOverlayControllerV1 controller() {
    return CmsOverlayControllerV1.builder()
        .facilityRepository(mockFacilityRepository)
        .cmsOverlayRepository(mockCmsOverlayRepository)
        .baseUrl("http://foo/")
        .basePath("bp")
        .build();
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
    assertThatThrownBy(() -> controller().saveOverlay(id, activeOverlay()))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("oh noes");
  }

  private DatamartDetailedService getDatamartDetailedService(
      @NonNull HealthService healthService, boolean isActive) {
    return DatamartDetailedService.builder()
        .name(
            HealthService.Covid19Vaccine.equals(healthService)
                ? CMS_OVERLAY_SERVICE_NAME_COVID_19
                : healthService.name())
        .serviceId(uncapitalize(healthService.name()))
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

  private List<DatamartDetailedService> getDatamartDetailedServices(
      @NonNull List<HealthService> healthServices, boolean isActive) {
    return healthServices.stream()
        .map(
            hs -> {
              return getDatamartDetailedService(hs, isActive);
            })
        .collect(Collectors.toList());
  }

  @Test
  @SneakyThrows
  public void getDetailedService() {
    DatamartCmsOverlay overlay = activeOverlay();
    var facilityId = "vha_402";
    var pk = FacilityEntity.Pk.fromIdString(facilityId);
    var serviceId = uncapitalize(HealthService.Covid19Vaccine.name());
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
                            getDatamartDetailedService(HealthService.Covid19Vaccine, false)))
                    .build()));
  }

  @Test
  @SneakyThrows
  public void getDetailedServices() {
    DatamartCmsOverlay overlay = activeOverlay();
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
                            getDatamartDetailedServices(List.of(HealthService.Cardiology), false)))
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
                            getDatamartDetailedServices(
                                List.of(HealthService.Covid19Vaccine), false)))
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
                            getDatamartDetailedServices(List.of(HealthService.Urology), false)))
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
    DatamartCmsOverlay overlay = activeOverlay();
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

  private DatamartCmsOverlay overlay(
      @NonNull List<HealthService> healthServices, boolean isActive) {
    return DatamartCmsOverlay.builder()
        .operatingStatus(
            DatamartFacility.OperatingStatus.builder()
                .code(DatamartFacility.OperatingStatusCode.NOTICE)
                .additionalInfo("i need attention")
                .build())
        .detailedServices(getDatamartDetailedServices(healthServices, isActive))
        .build();
  }

  @Test
  @SneakyThrows
  void updateIsAcceptedForKnownStationUsingServiceId() {
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
    DatamartCmsOverlay overlay = activeOverlay();
    ResponseEntity<Void> response = controller().saveOverlay("vha_402", overlay);
    Set<String> detailedServices = new HashSet<>();
    for (DatamartDetailedService service : overlay.detailedServices()) {
      if (service.active()) {
        detailedServices.add(capitalize(service.serviceId()));
      }
    }
    // Test contained DetailedService is one of HealthService, BenefitsService, or OtherService
    assertThat(
            detailedServices.parallelStream()
                .filter(
                    ds ->
                        Arrays.stream(HealthService.values()).anyMatch(hs -> hs.name().equals(ds))
                            || Arrays.stream(BenefitsService.values())
                                .anyMatch(bs -> bs.name().equals(ds))
                            || Arrays.stream(OtherService.values())
                                .anyMatch(os -> os.name().equals(ds)))
                .collect(Collectors.toList()))
        .usingRecursiveComparison()
        .isEqualTo(detailedServices);
    entity.cmsOperatingStatus(
        DatamartFacilitiesJacksonConfig.createMapper()
            .writeValueAsString(overlay.operatingStatus()));
    entity.overlayServices(detailedServices);
    verify(mockFacilityRepository).save(entity);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  @SneakyThrows
  void updateIsAcceptedForKnownStationUsingServiceName() {
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
    DatamartCmsOverlay overlay = activeOverlay();
    ResponseEntity<Void> response = controller().saveOverlay("vha_402", overlay);
    Set<String> detailedServices = new HashSet<>();
    for (DatamartDetailedService service : overlay.detailedServices()) {
      if (service.active()) {
        if ("COVID-19 vaccines".equals(service.name())) {
          detailedServices.add(HealthService.Covid19Vaccine.name());
        } else {
          detailedServices.add(service.name());
        }
      }
    }
    // Test contained DetailedService is one of HealthService, BenefitsService, or OtherService
    assertThat(
            detailedServices.parallelStream()
                .filter(
                    ds ->
                        Arrays.stream(HealthService.values()).anyMatch(hs -> hs.name().equals(ds))
                            || Arrays.stream(BenefitsService.values())
                                .anyMatch(bs -> bs.name().equals(ds))
                            || Arrays.stream(OtherService.values())
                                .anyMatch(os -> os.name().equals(ds)))
                .collect(Collectors.toList()))
        .usingRecursiveComparison()
        .isEqualTo(detailedServices);
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
    ResponseEntity<Void> response = controller().saveOverlay("vha_666", activeOverlay());
    verifyNoMoreInteractions(mockFacilityRepository);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
  }

  @Test
  @SneakyThrows
  void updateWithExistingOverlay() {
    DatamartCmsOverlay overlay = activeOverlay();
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
            .build();
    when(mockCmsOverlayRepository.findById(pk)).thenReturn(Optional.of(cmsOverlayEntity));
    List<DatamartDetailedService> additionalServices =
        List.of(
            DatamartDetailedService.builder()
                .name(DatamartFacility.HealthService.CaregiverSupport.name())
                .serviceId(uncapitalize(DatamartFacility.HealthService.CaregiverSupport.name()))
                .active(true)
                .build(),
            DatamartDetailedService.builder()
                .name(DatamartFacility.HealthService.Ophthalmology.name())
                .serviceId(uncapitalize(DatamartFacility.HealthService.Ophthalmology.name()))
                .active(true)
                .build());
    overlay.detailedServices(additionalServices);
    ResponseEntity<Void> resp = controller().saveOverlay("vha_402", overlay);
    assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    DatamartCmsOverlay updatedCovidPathOverlay = activeOverlay();
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
    DatamartCmsOverlay overlay = activeOverlay();
    for (DatamartDetailedService d : overlay.detailedServices()) {
      if (d.name().equals(CMS_OVERLAY_SERVICE_NAME_COVID_19)) {
        assertThat(d.path()).isEqualTo("replaceable path here");
      }
    }
    ResponseEntity<Void> response = controller().saveOverlay("vha_402", overlay);
    CmsOverlay cmsOverlay = CmsOverlayTransformerV1.toCmsOverlay(overlay);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    for (DetailedService d : cmsOverlay.detailedServices()) {
      if (d.name().equals(CMS_OVERLAY_SERVICE_NAME_COVID_19)) {
        assertThat(d.path())
            .isEqualTo("https://www.va.gov/maine-health-care/programs/covid-19-vaccines/");
      }
    }
  }
}
