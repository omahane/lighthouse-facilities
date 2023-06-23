package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.CMS_OVERLAY_SERVICE_NAME_COVID_19;

import java.util.List;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CmsOverlayFixture {

  public static DatamartCmsOverlay.Core core() {
    return DatamartCmsOverlay.Core.builder()
        .facilityUrl("https://www.va.gov/phoenix-health-care/locations/payson-va-clinic")
        .build();
  }

  public static DatamartCmsOverlay overlay() {
    return overlay(true);
  }

  public static DatamartCmsOverlay overlay(boolean isActive) {
    return DatamartCmsOverlay.builder()
        .core(core())
        .operatingStatus(overlayOperatingStatus())
        .detailedServices(overlayDetailedServices(isActive))
        .healthCareSystem(overlayHealthCareSystem())
        .build();
  }

  public static List<DatamartDetailedService> overlayDetailedServices() {
    return overlayDetailedServices(true);
  }

  public static List<DatamartDetailedService> overlayDetailedServices(boolean isActive) {
    return List.of(
        overlayDetailedServices(DatamartFacility.HealthService.Covid19Vaccine, isActive));
  }

  private static DatamartDetailedService overlayDetailedServices(
      @NonNull DatamartFacility.HealthService healthService, boolean isActive) {
    return DatamartDetailedService.builder()
        .active(isActive)
        .serviceInfo(
            DatamartDetailedService.ServiceInfo.builder()
                .serviceId(healthService.serviceId())
                .name(
                    DatamartFacility.HealthService.Covid19Vaccine.equals(healthService)
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

  public static DatamartCmsOverlay.HealthCareSystem overlayHealthCareSystem() {
    return DatamartCmsOverlay.HealthCareSystem.builder()
        .name("Example Health Care System Name")
        .url("https://www.va.gov/example/locations/facility")
        .covidUrl("https://www.va.gov/example/programs/covid-19-vaccine")
        .healthConnectPhone("123-456-7890 x123")
        .build();
  }

  public static DatamartFacility.OperatingStatus overlayOperatingStatus() {
    return DatamartFacility.OperatingStatus.builder()
        .code(DatamartFacility.OperatingStatusCode.LIMITED)
        .additionalInfo("Limited")
        .build();
  }
}
