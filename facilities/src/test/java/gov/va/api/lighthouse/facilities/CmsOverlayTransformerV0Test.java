package gov.va.api.lighthouse.facilities;

import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.v0.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v0.DetailedService;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.junit.jupiter.api.Test;

public class CmsOverlayTransformerV0Test {
  @Test
  public void cmsOverlayRoundtrip() {
    // Lossless transformation
    CmsOverlay covidOnlyOverlay = overlay(List.of(Facility.HealthService.Covid19Vaccine));
    assertThat(
            CmsOverlayTransformerV0.toCmsOverlay(
                CmsOverlayTransformerV0.toVersionAgnostic(covidOnlyOverlay)))
        .usingRecursiveComparison()
        .isEqualTo(covidOnlyOverlay);
    // Non-lossless transformation
    CmsOverlay overlayWithMoreThanCovidServices = overlay();
    assertThat(
            CmsOverlayTransformerV0.toCmsOverlay(
                CmsOverlayTransformerV0.toVersionAgnostic(overlayWithMoreThanCovidServices)))
        .usingRecursiveComparison()
        .isEqualTo(covidOnlyOverlay);
  }

  @Test
  public void cmsOverlayVisitorRoundtrip() {
    // Lossless transformation
    CmsOverlay covidOnlyOverlay = overlay(List.of(Facility.HealthService.Covid19Vaccine));
    assertThat(
            CmsOverlayTransformerV0.toCmsOverlay(
                CmsOverlayTransformerV1.toVersionAgnostic(
                    CmsOverlayTransformerV1.toCmsOverlay(
                        CmsOverlayTransformerV0.toVersionAgnostic(covidOnlyOverlay)))))
        .usingRecursiveComparison()
        .isEqualTo(covidOnlyOverlay);
    // Non-lossless transformation
    CmsOverlay overlayWithMoreThanCovidServices = overlay();
    assertThat(
            CmsOverlayTransformerV0.toCmsOverlay(
                CmsOverlayTransformerV1.toVersionAgnostic(
                    CmsOverlayTransformerV1.toCmsOverlay(
                        CmsOverlayTransformerV0.toVersionAgnostic(
                            overlayWithMoreThanCovidServices)))))
        .usingRecursiveComparison()
        .isEqualTo(covidOnlyOverlay);
  }

  private DatamartCmsOverlay datamartCmsOverlay() {
    return datamartCmsOverlay(
        List.of(
            DatamartFacility.HealthService.Covid19Vaccine,
            DatamartFacility.HealthService.Cardiology));
  }

  private DatamartCmsOverlay datamartCmsOverlay(
      List<DatamartFacility.HealthService> healthServices) {
    return DatamartCmsOverlay.builder()
        .operatingStatus(
            DatamartFacility.OperatingStatus.builder()
                .code(DatamartFacility.OperatingStatusCode.NORMAL)
                .additionalInfo("additional operating status info")
                .build())
        .detailedServices(
            healthServices != null ? getDatamartDetailedServices(healthServices, true) : null)
        .build();
  }

  @Test
  public void datamartCmsOverlayRoundtrip() {
    // Lossless transformation
    DatamartCmsOverlay covidOnlyDatamartCmsOverlay =
        datamartCmsOverlay(List.of(DatamartFacility.HealthService.Covid19Vaccine));
    assertThat(
            CmsOverlayTransformerV0.toVersionAgnostic(
                CmsOverlayTransformerV0.toCmsOverlay(covidOnlyDatamartCmsOverlay)))
        .usingRecursiveComparison()
        .isEqualTo(covidOnlyDatamartCmsOverlay);
    // Non-lossless transformation
    DatamartCmsOverlay datamartCmsOverlayWithMoreThanCovidService = datamartCmsOverlay();
    assertThat(
            CmsOverlayTransformerV0.toVersionAgnostic(
                CmsOverlayTransformerV0.toCmsOverlay(datamartCmsOverlayWithMoreThanCovidService)))
        .usingRecursiveComparison()
        .isEqualTo(covidOnlyDatamartCmsOverlay);
  }

  private DatamartDetailedService getDatamartDetailedService(
      @NonNull DatamartFacility.HealthService healthService, boolean isActive) {
    return DatamartDetailedService.builder()
        .active(isActive)
        .name(
            healthService.name().equals(DatamartFacility.HealthService.Covid19Vaccine.name())
                ? "COVID-19 vaccines"
                : healthService.name())
        .serviceId(uncapitalize(healthService.name()))
        .path("https://path/to/service/goodness")
        .phoneNumbers(
            List.of(
                DatamartDetailedService.AppointmentPhoneNumber.builder()
                    .number("937-268-6511")
                    .label("Main phone")
                    .type("tel")
                    .extension("71234")
                    .build(),
                DatamartDetailedService.AppointmentPhoneNumber.builder()
                    .number("321-213-4253")
                    .label("After hours phone")
                    .type("tel")
                    .extension("12345")
                    .build()))
        .walkInsAccepted("true")
        .referralRequired("false")
        .appointmentLeadIn(
            "Your VA health care team will contact you if you???re eligible to get a vaccine "
                + "during this time. As the supply of vaccine increases, we'll work with our care "
                + "teams to let Veterans know their options.")
        .descriptionFacility("facility description")
        .onlineSchedulingAvailable("true")
        .serviceLocations(
            List.of(
                DatamartDetailedService.DetailedServiceLocation.builder()
                    .additionalHoursInfo(
                        "Location hours times may vary depending on staff availability")
                    .facilityServiceHours(
                        DatamartDetailedService.DetailedServiceHours.builder()
                            .sunday("Closed")
                            .monday("9AM-5PM")
                            .tuesday("9AM-5PM")
                            .wednesday("9AM-5PM")
                            .thursday("9AM-5PM")
                            .friday("9AM-5PM")
                            .saturday("Closed")
                            .build())
                    .emailContacts(
                        List.of(
                            DatamartDetailedService.DetailedServiceEmailContact.builder()
                                .emailAddress("georgea@va.gov")
                                .emailLabel("George Anderson")
                                .build(),
                            DatamartDetailedService.DetailedServiceEmailContact.builder()
                                .emailAddress("john.doe@va.gov")
                                .emailLabel("John Doe")
                                .build(),
                            DatamartDetailedService.DetailedServiceEmailContact.builder()
                                .emailAddress("jane.doe@va.gov")
                                .emailLabel("Jane Doe")
                                .build()))
                    .appointmentPhoneNumbers(
                        List.of(
                            DatamartDetailedService.AppointmentPhoneNumber.builder()
                                .number("932-934-6731")
                                .type("tel")
                                .label("Main Phone")
                                .extension("3245")
                                .build(),
                            DatamartDetailedService.AppointmentPhoneNumber.builder()
                                .number("956-862-6651")
                                .type("mobile")
                                .label("Mobile phone")
                                .build()))
                    .serviceLocationAddress(
                        DatamartDetailedService.DetailedServiceAddress.builder()
                            .address1("50 Irving Street, Northwest")
                            .buildingNameNumber("Baxter Building")
                            .city("Washington")
                            .state("DC")
                            .zipCode("20422-0001")
                            .countryCode("US")
                            .clinicName("Baxter Clinic")
                            .wingFloorOrRoomNumber("Wing East")
                            .build())
                    .build()))
        .changed("2021-02-04T22:36:49+00:00")
        .build();
  }

  private List<DatamartDetailedService> getDatamartDetailedServices(
      @NonNull List<DatamartFacility.HealthService> healthServices, boolean isActive) {
    return healthServices.stream()
        .map(
            hs -> {
              return getDatamartDetailedService(hs, isActive);
            })
        .collect(Collectors.toList());
  }

  private DetailedService getDetailedService(
      @NonNull Facility.HealthService healthService, boolean isActive) {
    return DetailedService.builder()
        .active(isActive)
        .name(
            healthService.name().equals(Facility.HealthService.Covid19Vaccine.name())
                ? "COVID-19 vaccines"
                : healthService.name())
        .serviceId(uncapitalize(healthService.name()))
        .path("https://path/to/service/goodness")
        .phoneNumbers(
            List.of(
                DetailedService.AppointmentPhoneNumber.builder()
                    .number("937-268-6511")
                    .label("Main phone")
                    .type("tel")
                    .extension("71234")
                    .build(),
                DetailedService.AppointmentPhoneNumber.builder()
                    .number("321-213-4253")
                    .label("After hours phone")
                    .type("tel")
                    .extension("12345")
                    .build()))
        .walkInsAccepted("true")
        .referralRequired("false")
        .appointmentLeadIn(
            "Your VA health care team will contact you if you???re eligible to get a vaccine "
                + "during this time. As the supply of vaccine increases, we'll work with our care "
                + "teams to let Veterans know their options.")
        .descriptionFacility("facility description")
        .onlineSchedulingAvailable("true")
        .serviceLocations(
            List.of(
                DetailedService.DetailedServiceLocation.builder()
                    .additionalHoursInfo(
                        "Location hours times may vary depending on staff availability")
                    .facilityServiceHours(
                        DetailedService.DetailedServiceHours.builder()
                            .sunday("Closed")
                            .monday("9AM-5PM")
                            .tuesday("9AM-5PM")
                            .wednesday("9AM-5PM")
                            .thursday("9AM-5PM")
                            .friday("9AM-5PM")
                            .saturday("Closed")
                            .build())
                    .emailContacts(
                        List.of(
                            DetailedService.DetailedServiceEmailContact.builder()
                                .emailAddress("georgea@va.gov")
                                .emailLabel("George Anderson")
                                .build(),
                            DetailedService.DetailedServiceEmailContact.builder()
                                .emailAddress("john.doe@va.gov")
                                .emailLabel("John Doe")
                                .build(),
                            DetailedService.DetailedServiceEmailContact.builder()
                                .emailAddress("jane.doe@va.gov")
                                .emailLabel("Jane Doe")
                                .build()))
                    .appointmentPhoneNumbers(
                        List.of(
                            DetailedService.AppointmentPhoneNumber.builder()
                                .number("932-934-6731")
                                .type("tel")
                                .label("Main Phone")
                                .extension("3245")
                                .build(),
                            DetailedService.AppointmentPhoneNumber.builder()
                                .number("956-862-6651")
                                .type("mobile")
                                .label("Mobile phone")
                                .build()))
                    .serviceLocationAddress(
                        DetailedService.DetailedServiceAddress.builder()
                            .address1("50 Irving Street, Northwest")
                            .buildingNameNumber("Baxter Building")
                            .city("Washington")
                            .state("DC")
                            .zipCode("20422-0001")
                            .countryCode("US")
                            .clinicName("Baxter Clinic")
                            .wingFloorOrRoomNumber("Wing East")
                            .build())
                    .build()))
        .changed("2021-02-04T22:36:49+00:00")
        .build();
  }

  private List<DetailedService> getDetailedServices(
      @NonNull List<Facility.HealthService> healthServices, boolean isActive) {
    return healthServices.stream()
        .map(
            hs -> {
              return getDetailedService(hs, isActive);
            })
        .collect(Collectors.toList());
  }

  private CmsOverlay overlay() {
    return overlay(
        List.of(Facility.HealthService.Covid19Vaccine, Facility.HealthService.Cardiology));
  }

  private CmsOverlay overlay(List<Facility.HealthService> healthServices) {
    return CmsOverlay.builder()
        .operatingStatus(
            Facility.OperatingStatus.builder()
                .code(Facility.OperatingStatusCode.NORMAL)
                .additionalInfo("additional operating status info")
                .build())
        .detailedServices(healthServices != null ? getDetailedServices(healthServices, true) : null)
        .build();
  }

  @Test
  public void transformCmsOverlay() {
    DatamartCmsOverlay expected = datamartCmsOverlay();
    CmsOverlay overlay = overlay();
    assertThat(CmsOverlayTransformerV0.toVersionAgnostic(overlay))
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

  @Test
  public void transformCmsOverlayWithEmptyDetailedServices() {
    CmsOverlay overlay =
        CmsOverlay.builder()
            .operatingStatus(
                Facility.OperatingStatus.builder()
                    .code(Facility.OperatingStatusCode.NORMAL)
                    .additionalInfo("additional operating status info")
                    .build())
            .build();
    DatamartCmsOverlay datamartCmsOverlay =
        DatamartCmsOverlay.builder()
            .operatingStatus(
                DatamartFacility.OperatingStatus.builder()
                    .code(DatamartFacility.OperatingStatusCode.NORMAL)
                    .additionalInfo("additional operating status info")
                    .build())
            .build();
    assertThat(CmsOverlayTransformerV0.toVersionAgnostic(overlay))
        .usingRecursiveComparison()
        .isEqualTo(datamartCmsOverlay);
    assertThat(CmsOverlayTransformerV0.toCmsOverlay(datamartCmsOverlay))
        .usingRecursiveComparison()
        .isEqualTo(overlay);
  }

  @Test
  public void transformDatamartCmsOverlay() {
    // All non-Covid19 detailed services filtered out for V0
    CmsOverlay expected = overlay(List.of(Facility.HealthService.Covid19Vaccine));
    DatamartCmsOverlay datamartCmsOverlay = datamartCmsOverlay();
    assertThat(CmsOverlayTransformerV0.toCmsOverlay(datamartCmsOverlay))
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

  @Test
  public void transformEmptyCmsOverlay() {
    CmsOverlay overlay = CmsOverlay.builder().build();
    DatamartCmsOverlay datamartCmsOverlay = DatamartCmsOverlay.builder().build();
    assertThat(CmsOverlayTransformerV0.toVersionAgnostic(overlay))
        .usingRecursiveComparison()
        .isEqualTo(datamartCmsOverlay);
    assertThat(CmsOverlayTransformerV0.toCmsOverlay(datamartCmsOverlay))
        .usingRecursiveComparison()
        .isEqualTo(overlay);
  }
}
