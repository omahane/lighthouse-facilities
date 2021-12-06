package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.cms.DetailedService;
import gov.va.api.lighthouse.facilities.api.v0.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CmsOverlayTransformerV0Test {
  @Test
  public void cmsOverlayRoundtrip() {
    CmsOverlay overlay = overlay();
    assertThat(
            CmsOverlayTransformerV0.toCmsOverlay(
                CmsOverlayTransformerV0.toVersionAgnostic(overlay)))
        .usingRecursiveComparison()
        .isEqualTo(overlay);
  }

  @Test
  public void cmsOverlayVisitorRoundtrip() {
    CmsOverlay overlay = overlay();
    assertThat(
            CmsOverlayTransformerV0.toCmsOverlay(
                CmsOverlayTransformerV1.toVersionAgnostic(
                    CmsOverlayTransformerV1.toCmsOverlay(
                        CmsOverlayTransformerV0.toVersionAgnostic(overlay)))))
        .usingRecursiveComparison()
        .isEqualTo(overlay);
  }

  private DatamartCmsOverlay datamartCmsOverlay() {
    return DatamartCmsOverlay.builder()
        .operatingStatus(
            DatamartFacility.OperatingStatus.builder()
                .code(DatamartFacility.OperatingStatusCode.NORMAL)
                .additionalInfo("additional operating status info")
                .build())
        .detailedServices(
            List.of(
                DetailedService.builder()
                    .active(true)
                    .name("COVID-19 vaccines")
                    .path("https://www.melbourne.va.gov/services/covid-19-vaccines.asp")
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
                    .build(),
                DetailedService.builder()
                    .active(true)
                    .name("Cardiology")
                    .path("https://www.melbourne.va.gov/services/cardiology.asp")
                    .phoneNumbers(
                        List.of(
                            DetailedService.AppointmentPhoneNumber.builder()
                                .number("924-268-4253")
                                .label("Main phone")
                                .type("tel")
                                .extension("71432")
                                .build(),
                            DetailedService.AppointmentPhoneNumber.builder()
                                .number("321-726-6526")
                                .label("After hours phone")
                                .type("tel")
                                .extension("17525")
                                .build()))
                    .walkInsAccepted("true")
                    .referralRequired("false")
                    .appointmentLeadIn(
                        "Do not consume caffeinated beverages 24 hours prior to your appointment.")
                    .descriptionFacility("cardiology facility description")
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
                                        .address1("2513 Irving Street, Northwest")
                                        .buildingNameNumber("Baxter Building")
                                        .city("Washington")
                                        .state("DC")
                                        .zipCode("20422-0001")
                                        .countryCode("US")
                                        .clinicName("Walter Read Medical Facility")
                                        .wingFloorOrRoomNumber("Wing East")
                                        .build())
                                .build()))
                    .changed("2021-02-04T22:36:49+00:00")
                    .build()))
        .build();
  }

  @Test
  public void datamartCmsOverlayRoundtrip() {
    DatamartCmsOverlay datamartCmsOverlay = datamartCmsOverlay();
    assertThat(
            CmsOverlayTransformerV0.toVersionAgnostic(
                CmsOverlayTransformerV0.toCmsOverlay(datamartCmsOverlay)))
        .usingRecursiveComparison()
        .isEqualTo(datamartCmsOverlay);
  }

  private CmsOverlay overlay() {
    return CmsOverlay.builder()
        .operatingStatus(
            Facility.OperatingStatus.builder()
                .code(Facility.OperatingStatusCode.NORMAL)
                .additionalInfo("additional operating status info")
                .build())
        .detailedServices(
            List.of(
                DetailedService.builder()
                    .active(true)
                    .name("COVID-19 vaccines")
                    .path("https://www.melbourne.va.gov/services/covid-19-vaccines.asp")
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
                    .build(),
                DetailedService.builder()
                    .active(true)
                    .name("Cardiology")
                    .path("https://www.melbourne.va.gov/services/cardiology.asp")
                    .phoneNumbers(
                        List.of(
                            DetailedService.AppointmentPhoneNumber.builder()
                                .number("924-268-4253")
                                .label("Main phone")
                                .type("tel")
                                .extension("71432")
                                .build(),
                            DetailedService.AppointmentPhoneNumber.builder()
                                .number("321-726-6526")
                                .label("After hours phone")
                                .type("tel")
                                .extension("17525")
                                .build()))
                    .walkInsAccepted("true")
                    .referralRequired("false")
                    .appointmentLeadIn(
                        "Do not consume caffeinated beverages 24 hours prior to your appointment.")
                    .descriptionFacility("cardiology facility description")
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
                                        .address1("2513 Irving Street, Northwest")
                                        .buildingNameNumber("Baxter Building")
                                        .city("Washington")
                                        .state("DC")
                                        .zipCode("20422-0001")
                                        .countryCode("US")
                                        .clinicName("Walter Read Medical Facility")
                                        .wingFloorOrRoomNumber("Wing East")
                                        .build())
                                .build()))
                    .changed("2021-02-04T22:36:49+00:00")
                    .build()))
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
    CmsOverlay expected = overlay();
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
