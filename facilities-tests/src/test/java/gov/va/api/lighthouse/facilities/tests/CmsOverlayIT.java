package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentNotIn;
import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.CLIENT_KEY_DEFAULT;
import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.sentinel.Environment;
import gov.va.api.health.sentinel.ExpectedResponse;
import gov.va.api.lighthouse.facilities.api.cms.DetailedService;
import gov.va.api.lighthouse.facilities.api.v0.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v0.CmsOverlayResponse;
import gov.va.api.lighthouse.facilities.api.v0.Facility.ActiveStatus;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatus;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatusCode;
import gov.va.api.lighthouse.facilities.api.v0.FacilityReadResponse;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;
import java.time.Instant;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Slf4j
@ExtendWith(RequiresFacilitiesExtension.class)
public class CmsOverlayIT {
  private static final ObjectMapper MAPPER = JacksonConfig.createMapper();

  private static final String DETAILED_SERVICE_JSON_BODY =
      "{    \"detailed_services\":["
          + "{"
          + "\"name\":\"COVID-19 vaccines\","
          + "\"active\":true,"
          + "\"changed\": \"2021-02-04T22:36:49+00:00\","
          + "\"description_facility\":\"I'm a facility service!\","
          + "\"health_service_api_id\":\"12435\","
          + "\"appointment_leadin\":\"Your VA health care team will contact you if you...more text\","
          + "\"online_scheduling_available\": \"Unknown\","
          + "\"path\": \"\\/erie-health-care\\/locations\\/erie-va-medical-center\\/covid-19-vaccines\","
          + "\"appointment_phones\": [  "
          + "{"
          + "\"extension\": \"123\","
          + "\"label\": \"Main phone changed\","
          + "\"number\": \"555-555-1212\","
          + "\"type\": \"tel\"},  "
          + "{		  "
          + "\"extension\": null,"
          + "\"label\": \"Main Fax\","
          + "\"number\": \"444-444-1212\","
          + "\"type\": \"fax\"        }"
          + "],"
          + "\"referral_required\": \"False\","
          + "\"service_locations\": [   "
          + "{"
          + "\"additional_hours_info\": \"Please use call for an apt outside...\","
          + "\"email_contacts\": [   "
          + "{"
          + "\"email_address\": \"georgea@va.gov\","
          + "\"email_label\": \"George Anderson\"    },  "
          + "{"
          + "\"email_address\": \"confirmations@va.gov\","
          + "\"email_label\": \"Confirm your appointment\"}"
          + "],"
          + "\"facility_service_hours\": {"
          + "\"Friday\": \"830AM-430PM\","
          + "\"Monday\": \"830AM-700PM\","
          + "\"Sunday\": \"Closed\","
          + "\"Tuesday\": \"830AM-700PM\","
          + "\"Saturday\": \"Closed\","
          + "\"Thursday\": \"830AM-600PM\","
          + "\"Wednesday\": \"ANY STRING b\""
          + "},"
          + "\"appointment_phones\": ["
          + "{"
          + "\"extension\": \"123\","
          + "\"label\": \"Appointment phone\","
          + "\"number\": \"555-555-1212\","
          + "\"type\": \"tel\"  "
          + "},"
          + "{"
          + "\"extension\": null, "
          + "\"label\": \"TTY\","
          + "\"number\": \"222-222-1212\","
          + "\"type\": \"tty\"  "
          + "}"
          + "],"
          + "\"service_location_address\": {  "
          + "\"address_line1\": \"122 Main St.\",  "
          + "\"address_line2\": null,  "
          + "\"building_name_number\": \"Baxter Bulding\",  "
          + "\"clinic_name\": \"Baxter Clinic\",  "
          + "\"country_code\": \"US\",  "
          + "\"city\": \"Rochester\",  "
          + "\"state\": \"NY\",  "
          + "\"zip_code\": \"14623-1345\",  "
          + "\"wing_floor_or_room_number\": \"Wing East\""
          + "}"
          + "}"
          + "],"
          + "\"walk_ins_accepted\": \"True\""
          + "}  "
          + "]"
          + "}";

  @SneakyThrows
  private static void assertUpdate(
      OperatingStatusCode code, String message, ActiveStatus expectedActiveStatus) {
    var id = systemDefinition().ids().facility();
    log.info("Updating facility {} operating status to be {}", id, code);
    OperatingStatus op =
        OperatingStatus.builder().code(code).additionalInfo(message + " " + code).build();
    SystemDefinitions.Service svc = systemDefinition().facilities();
    ExpectedResponse.of(
            requestSpecification()
                .contentType("application/json")
                .body(MAPPER.writeValueAsString(CmsOverlay.builder().operatingStatus(op).build()))
                .request(
                    Method.POST, svc.urlWithApiPath() + "v0/facilities/" + id + "/cms-overlay"))
        .expect(200);
    var facility =
        ExpectedResponse.of(
                requestSpecification()
                    .request(Method.GET, svc.urlWithApiPath() + "v0/facilities/" + id))
            .expect(200)
            .expectValid(FacilityReadResponse.class);
    assertThat(facility.facility().attributes().operatingStatus()).isEqualTo(op);
    assertThat(facility.facility().attributes().activeStatus()).isEqualTo(expectedActiveStatus);
  }

  @BeforeAll
  static void assumeEnvironment() {
    // CMS overlay tests alter data, but do not infinitely create more
    // These can run in lower environments, but not SLA'd environments
    assumeEnvironmentNotIn(Environment.LAB, Environment.PROD);
  }

  private static RequestSpecification requestSpecification() {
    SystemDefinitions.Service svc = systemDefinition().facilities();
    return RestAssured.given().baseUri(svc.url()).port(svc.port()).relaxedHTTPSValidation();
  }

  private static RequestSpecification requestSpecificationInternal() {
    SystemDefinitions.Service svcInternal = systemDefinition().facilitiesInternal();
    return RestAssured.given()
        .baseUri(svcInternal.url())
        .port(svcInternal.port())
        .relaxedHTTPSValidation()
        .header("client-key", System.getProperty("client-key", CLIENT_KEY_DEFAULT));
  }

  @Test
  void canApplyOverlay() {
    var message = getClass().getSimpleName() + " " + Instant.now();
    assertUpdate(OperatingStatusCode.CLOSED, message, ActiveStatus.T);
    assertUpdate(OperatingStatusCode.LIMITED, message, ActiveStatus.A);
    assertUpdate(OperatingStatusCode.NOTICE, message, ActiveStatus.A);
    assertUpdate(OperatingStatusCode.NORMAL, message, ActiveStatus.A);
  }

  @Test
  @SneakyThrows
  void deleteOverlayAndFacility() {
    var id = systemDefinition().ids().facility();
    SystemDefinitions.Service svc = systemDefinition().facilities();
    SystemDefinitions.Service svcInternal = systemDefinition().facilitiesInternal();
    // Create detailed service for facility then remove it
    ExpectedResponse.of(
            requestSpecification()
                .contentType("application/json")
                .body(DETAILED_SERVICE_JSON_BODY)
                .request(
                    Method.POST, svc.urlWithApiPath() + "v0/facilities/" + id + "/cms-overlay"))
        .expect(200);
    ExpectedResponse.of(
            requestSpecificationInternal()
                .request(
                    Method.DELETE,
                    svcInternal.urlWithApiPath()
                        + "internal/management/facilities/"
                        + id
                        + "/cms-overlay"))
        .expect(200);
    ExpectedResponse.of(
            requestSpecificationInternal()
                .request(
                    Method.DELETE,
                    svcInternal.urlWithApiPath() + "internal/management/facilities/" + id))
        .expect(200);
    // Call reload since we deleted the facility
    ExpectedResponse.of(
            requestSpecificationInternal()
                .request(Method.GET, svcInternal.urlWithApiPath() + "internal/management/reload"))
        .expect(200);
  }

  private List<DetailedService> detailedServices() {
    return List.of(
        DetailedService.builder()
            .name("COVID-19 vaccines")
            .descriptionFacility("I'm a facility service!")
            .appointmentLeadIn("Your VA health care team will contact you if you...more text")
            .onlineSchedulingAvailable("Unknown")
            .phoneNumbers(
                List.of(
                    DetailedService.AppointmentPhoneNumber.builder()
                        .extension("123")
                        .label("Main phone changed")
                        .number("555-555-1212")
                        .type("tel")
                        .build(),
                    DetailedService.AppointmentPhoneNumber.builder()
                        .label("Main Fax")
                        .number("444-444-1212")
                        .type("fax")
                        .build()))
            .referralRequired("False")
            .serviceLocations(
                List.of(
                    DetailedService.DetailedServiceLocation.builder()
                        .additionalHoursInfo("Please use call for an apt outside...")
                        .emailContacts(
                            List.of(
                                DetailedService.DetailedServiceEmailContact.builder()
                                    .emailAddress("georgea@va.gov")
                                    .emailLabel("George Anderson")
                                    .build(),
                                DetailedService.DetailedServiceEmailContact.builder()
                                    .emailAddress("confirmations@va.gov")
                                    .emailLabel("Confirm your appointment")
                                    .build()))
                        .facilityServiceHours(
                            DetailedService.DetailedServiceHours.builder()
                                .monday("830AM-700PM")
                                .tuesday("830AM-700PM")
                                .wednesday("ANY STRING b")
                                .thursday("830AM-600PM")
                                .friday("830AM-430PM")
                                .saturday("Closed")
                                .sunday("Closed")
                                .build())
                        .appointmentPhoneNumbers(
                            List.of(
                                DetailedService.AppointmentPhoneNumber.builder()
                                    .extension("123")
                                    .label("Appointment phone")
                                    .number("555-555-1212")
                                    .type("tel")
                                    .build(),
                                DetailedService.AppointmentPhoneNumber.builder()
                                    .label("TTY")
                                    .number("222-222-1212")
                                    .type("tty")
                                    .build()))
                        .serviceLocationAddress(
                            DetailedService.DetailedServiceAddress.builder()
                                .address1("122 Main St.")
                                .state("NY")
                                .buildingNameNumber("Baxter Bulding")
                                .clinicName("Baxter Clinic")
                                .countryCode("US")
                                .city("Rochester")
                                .zipCode("14623-1345")
                                .wingFloorOrRoomNumber("Wing East")
                                .build())
                        .build()))
            .walkInsAccepted("True")
            .build());
  }

  @Test
  @SneakyThrows
  void getDetailedServicesErrorStatuses() {
    var id = SystemDefinitions.systemDefinition().ids().facility();
    SystemDefinitions.Service svc = systemDefinition().facilities();
    // ==== V0 CMS Overlays ====
    // 400 - Bad Request
    // Note: Performing a GET request to /v0/facilities/%/services through Postman produces an
    //       HTTP 400 error as expected.
    ExpectedResponse.of(
            requestSpecification()
                .contentType("application/json")
                .request(Method.GET, svc.urlWithApiPath() + "v0/facilities/%/services"))
        .expect(500);
    // 404 - Facility Not Found
    ExpectedResponse.of(
            requestSpecification()
                .request(
                    Method.GET, svc.urlWithApiPath() + "v0/facilities/{id}/services", "vba_1234"))
        .expect(404);
    // 406 - Request Format Unacceptable
    ExpectedResponse.of(
            requestSpecification()
                .accept("application/xml")
                .request(
                    Method.GET, svc.urlWithApiPath() + "v0/facilities/{id}/services", "vha_558GA"))
        .expect(406);

    // ==== V1 CMS Overlays ====
    // 400 - Bad Request
    // Note: Performing a GET request to /v1/facilities/%/services through Postman produces an
    //       HTTP 400 error as expected.
    ExpectedResponse.of(
            requestSpecification()
                .request(Method.GET, svc.urlWithApiPath() + "v1/facilities/%/services"))
        .expect(500);
    // 404 - Facility Not Found
    ExpectedResponse.of(
            requestSpecification()
                .request(
                    Method.GET, svc.urlWithApiPath() + "v1/facilities/{id}/services", "vba_1234"))
        .expect(404);
    // 406 - Request Format Unavailable
    ExpectedResponse.of(
            requestSpecification()
                .accept("application/xml")
                .request(
                    Method.GET, svc.urlWithApiPath() + "v1/facilities/{id}/services", "vha_558GA"))
        .expect(406);
  }

  @Test
  @SneakyThrows
  void multiOverlayUpdate() {
    OperatingStatus ops =
        OperatingStatus.builder()
            .code(OperatingStatusCode.NOTICE)
            .additionalInfo("Update1")
            .build();
    var id = systemDefinition().ids().facility();
    SystemDefinitions.Service svc = systemDefinition().facilities();
    SystemDefinitions.Service svcInternal = systemDefinition().facilitiesInternal();
    // make sure the overlay doesn't exist is cleaned up before running the rest of the test
    ExpectedResponse.of(
        requestSpecificationInternal()
            .request(
                Method.DELETE,
                svcInternal.urlWithApiPath() + "internal/management/cms-overlay/" + id));
    ExpectedResponse.of(
        requestSpecificationInternal()
            .request(
                Method.DELETE,
                svcInternal.urlWithApiPath()
                    + "internal/management/facilities/"
                    + id
                    + "/cms-overlay"));
    ExpectedResponse.of(
            requestSpecificationInternal()
                .request(Method.GET, svcInternal.urlWithApiPath() + "internal/management/reload"))
        .expect(200);
    // Attempt to get an overlay that does not exist
    ExpectedResponse.of(
            requestSpecification()
                .contentType("application/json")
                .request(Method.GET, svc.urlWithApiPath() + "v0/facilities/" + id + "/cms-overlay"))
        .expect(404);
    // Create an overlay
    ExpectedResponse.of(
            requestSpecification()
                .contentType("application/json")
                .body(MAPPER.writeValueAsString(CmsOverlay.builder().operatingStatus(ops).build()))
                .request(
                    Method.POST, svc.urlWithApiPath() + "v0/facilities/" + id + "/cms-overlay"))
        .expect(200);
    // Retrieve the overlay
    var cmsOverlay =
        ExpectedResponse.of(
                requestSpecification()
                    .request(
                        Method.GET, svc.urlWithApiPath() + "v0/facilities/" + id + "/cms-overlay"))
            .expect(200)
            .expectValid(CmsOverlayResponse.class);
    assertThat(cmsOverlay.overlay().operatingStatus()).isEqualTo(ops);
    assertThat(cmsOverlay.overlay().detailedServices()).isEmpty();
    var facility =
        ExpectedResponse.of(
                requestSpecification()
                    .request(Method.GET, svc.urlWithApiPath() + "v0/facilities/" + id))
            .expect(200)
            .expectValid(FacilityReadResponse.class)
            .facility();
    assertThat(facility.attributes().operatingStatus()).isEqualTo(ops);
    assertThat(facility.attributes().detailedServices()).isNull();
    ExpectedResponse.of(
            requestSpecification()
                .contentType("application/json")
                .body(DETAILED_SERVICE_JSON_BODY)
                .request(
                    Method.POST, svc.urlWithApiPath() + "v0/facilities/" + id + "/cms-overlay"))
        .expect(200);
    cmsOverlay =
        ExpectedResponse.of(
                requestSpecification()
                    .request(
                        Method.GET, svc.urlWithApiPath() + "v0/facilities/" + id + "/cms-overlay"))
            .expect(200)
            .expectValid(CmsOverlayResponse.class);
    assertThat(cmsOverlay.overlay().operatingStatus()).isEqualTo(ops);
    assertThat(cmsOverlay.overlay().detailedServices())
        .usingRecursiveComparison()
        .isEqualTo(detailedServices());
    facility =
        ExpectedResponse.of(
                requestSpecification()
                    .request(Method.GET, svc.urlWithApiPath() + "v0/facilities/" + id))
            .expect(200)
            .expectValid(FacilityReadResponse.class)
            .facility();
    assertThat(facility.attributes().operatingStatus()).isEqualTo(ops);
    assertThat(facility.attributes().detailedServices())
        .usingRecursiveComparison()
        .isEqualTo(detailedServices());
  }

  @Test
  @SneakyThrows
  void saveForUnknownFacility() {
    var id = "vba_NOPE";
    log.info("Updating invalid facility {} with cmsOverlay", id);
    OperatingStatus ops =
        OperatingStatus.builder().code(OperatingStatusCode.NOTICE).additionalInfo("Shrug").build();
    SystemDefinitions.Service svc = systemDefinition().facilities();
    ExpectedResponse.of(
            requestSpecification()
                .contentType("application/json")
                .body(MAPPER.writeValueAsString(CmsOverlay.builder().operatingStatus(ops).build()))
                .request(
                    Method.POST, svc.urlWithApiPath() + "v0/facilities/" + id + "/cms-overlay"))
        .expect(202);
    var cmsOverlay =
        ExpectedResponse.of(
                requestSpecification()
                    .request(
                        Method.GET, svc.urlWithApiPath() + "v0/facilities/" + id + "/cms-overlay"))
            .expect(200)
            .expectValid(CmsOverlayResponse.class);
    assertThat(cmsOverlay.overlay().operatingStatus()).isEqualTo(ops);
  }

  @Test
  @SneakyThrows
  void validation() {
    var id = SystemDefinitions.systemDefinition().ids().facility();
    StringBuilder longMessage = new StringBuilder();
    for (int i = 1; i <= 301; i++) {
      longMessage.append(i % 10);
    }
    log.info("Updating facility {} with invalid operating status", id);
    OperatingStatus op =
        OperatingStatus.builder()
            .code(OperatingStatusCode.CLOSED)
            .additionalInfo(longMessage.toString())
            .build();
    SystemDefinitions.Service svc = systemDefinition().facilities();
    ExpectedResponse.of(
            requestSpecification()
                .contentType("application/json")
                .body(MAPPER.writeValueAsString(CmsOverlay.builder().operatingStatus(op).build()))
                .request(
                    Method.POST, svc.urlWithApiPath() + "v0/facilities/" + id + "/cms-overlay"))
        .expect(400);
  }
}
