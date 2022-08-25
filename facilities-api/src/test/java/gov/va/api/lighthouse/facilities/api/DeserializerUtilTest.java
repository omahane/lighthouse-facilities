package gov.va.api.lighthouse.facilities.api;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class DeserializerUtilTest {
  @Test
  @SneakyThrows
  void activeStatus() {
    JsonNode activeStatus =
        DeserializerUtil.getActiveStatus(generateNode("{\"active_status\":\"A\"}"));
    assertThat(activeStatus).isNotNull();
    assertThat(createMapper().convertValue(activeStatus, String.class)).isEqualTo("A");
    activeStatus = DeserializerUtil.getActiveStatus(generateNode("{\"activeStatus\":\"A\"}"));
    assertThat(activeStatus).isNotNull();
    assertThat(createMapper().convertValue(activeStatus, String.class)).isEqualTo("A");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getActiveStatus(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void additionalHoursInfo() {
    JsonNode additionalHoursInfo =
        DeserializerUtil.getAdditionalHoursInfo(
            generateNode("{\"additional_hours_info\":\"additional hours info\"}"));
    assertThat(additionalHoursInfo).isNotNull();
    assertThat(createMapper().convertValue(additionalHoursInfo, String.class))
        .isEqualTo("additional hours info");
    additionalHoursInfo =
        DeserializerUtil.getAdditionalHoursInfo(
            generateNode("{\"additionalHoursInfo\":\"additional hours info\"}"));
    assertThat(additionalHoursInfo).isNotNull();
    assertThat(createMapper().convertValue(additionalHoursInfo, String.class))
        .isEqualTo("additional hours info");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getAdditionalHoursInfo(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void additionalInfo() {
    JsonNode additionalInfo =
        DeserializerUtil.getAdditionalInfo(
            generateNode("{\"additional_info\":\"additional info\"}"));
    assertThat(additionalInfo).isNotNull();
    assertThat(createMapper().convertValue(additionalInfo, String.class))
        .isEqualTo("additional info");
    additionalInfo =
        DeserializerUtil.getAdditionalInfo(
            generateNode("{\"additionalInfo\":\"additional info\"}"));
    assertThat(additionalInfo).isNotNull();
    assertThat(createMapper().convertValue(additionalInfo, String.class))
        .isEqualTo("additional info");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getAdditionalInfo(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void address1() {
    JsonNode address1 =
        DeserializerUtil.getAddress1(
            generateNode("{\"address_1\":\"50 Irving Street, Northwest\"}"));
    assertThat(address1).isNotNull();
    assertThat(createMapper().convertValue(address1, String.class))
        .isEqualTo("50 Irving Street, Northwest");
    address1 =
        DeserializerUtil.getAddress1(
            generateNode("{\"address1\":\"50 Irving Street, Northwest\"}"));
    assertThat(address1).isNotNull();
    assertThat(createMapper().convertValue(address1, String.class))
        .isEqualTo("50 Irving Street, Northwest");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getAddress1(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void address2() {
    JsonNode address2 = DeserializerUtil.getAddress2(generateNode("{\"address_2\":\"Bldg 2\"}"));
    assertThat(address2).isNotNull();
    assertThat(createMapper().convertValue(address2, String.class)).isEqualTo("Bldg 2");
    address2 = DeserializerUtil.getAddress2(generateNode("{\"address2\":\"Bldg 2\"}"));
    assertThat(address2).isNotNull();
    assertThat(createMapper().convertValue(address2, String.class)).isEqualTo("Bldg 2");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getAddress2(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void address3() {
    JsonNode address3 = DeserializerUtil.getAddress3(generateNode("{\"address_3\":\"Suite 7\"}"));
    assertThat(address3).isNotNull();
    assertThat(createMapper().convertValue(address3, String.class)).isEqualTo("Suite 7");
    address3 = DeserializerUtil.getAddress3(generateNode("{\"address3\":\"Suite 7\"}"));
    assertThat(address3).isNotNull();
    assertThat(createMapper().convertValue(address3, String.class)).isEqualTo("Suite 7");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getAddress3(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void addressLine1() {
    JsonNode addressLine1 =
        DeserializerUtil.getAddressLine1(
            generateNode("{\"address_line1\":\"50 Irving Street, Northwest\"}"));
    assertThat(addressLine1).isNotNull();
    assertThat(createMapper().convertValue(addressLine1, String.class))
        .isEqualTo("50 Irving Street, Northwest");
    addressLine1 =
        DeserializerUtil.getAddressLine1(
            generateNode("{\"addressLine1\":\"50 Irving Street, Northwest\"}"));
    assertThat(addressLine1).isNotNull();
    assertThat(createMapper().convertValue(addressLine1, String.class))
        .isEqualTo("50 Irving Street, Northwest");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getAddressLine1(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void addressLine2() {
    JsonNode addressLine2 =
        DeserializerUtil.getAddressLine2(generateNode("{\"address_line2\":\"Bldg 2\"}"));
    assertThat(addressLine2).isNotNull();
    assertThat(createMapper().convertValue(addressLine2, String.class)).isEqualTo("Bldg 2");
    addressLine2 = DeserializerUtil.getAddressLine2(generateNode("{\"addressLine2\":\"Bldg 2\"}"));
    assertThat(addressLine2).isNotNull();
    assertThat(createMapper().convertValue(addressLine2, String.class)).isEqualTo("Bldg 2");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getAddressLine2(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void afterHours() {
    JsonNode afterHours =
        DeserializerUtil.getAfterHours(generateNode("{\"after_hours\":\"202-555-1212\"}"));
    assertThat(afterHours).isNotNull();
    assertThat(createMapper().convertValue(afterHours, String.class)).isEqualTo("202-555-1212");
    afterHours = DeserializerUtil.getAfterHours(generateNode("{\"afterHours\":\"202-555-1212\"}"));
    assertThat(afterHours).isNotNull();
    assertThat(createMapper().convertValue(afterHours, String.class)).isEqualTo("202-555-1212");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getAfterHours(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void appointmentLeadin() {
    JsonNode appointmentLeadin =
        DeserializerUtil.getAppointmentLeadin(
            generateNode(
                "{\"appointment_leadin\":\"Your VA health care team will contact you if you...more text\"}"));
    assertThat(appointmentLeadin).isNotNull();
    assertThat(createMapper().convertValue(appointmentLeadin, String.class))
        .isEqualTo("Your VA health care team will contact you if you...more text");
    appointmentLeadin =
        DeserializerUtil.getAppointmentLeadin(
            generateNode(
                "{\"appointmentLeadin\":\"Your VA health care team will contact you if you...more text\"}"));
    assertThat(appointmentLeadin).isNotNull();
    assertThat(createMapper().convertValue(appointmentLeadin, String.class))
        .isEqualTo("Your VA health care team will contact you if you...more text");
    appointmentLeadin =
        DeserializerUtil.getAppointmentLeadin(
            generateNode(
                "{\"appointmentLeadIn\":\"Your VA health care team will contact you if you...more text\"}"));
    assertThat(appointmentLeadin).isNotNull();
    assertThat(createMapper().convertValue(appointmentLeadin, String.class))
        .isEqualTo("Your VA health care team will contact you if you...more text");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getAppointmentLeadin(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void buildingNameNumber() {
    JsonNode bldNameNumber =
        DeserializerUtil.getBuildingNameNumber(
            generateNode("{\"building_name_number\":\"Baxter Building\"}"));
    assertThat(bldNameNumber).isNotNull();
    assertThat(createMapper().convertValue(bldNameNumber, String.class))
        .isEqualTo("Baxter Building");
    bldNameNumber =
        DeserializerUtil.getBuildingNameNumber(
            generateNode("{\"buildingNameNumber\":\"Baxter Building\"}"));
    assertThat(bldNameNumber).isNotNull();
    assertThat(createMapper().convertValue(bldNameNumber, String.class))
        .isEqualTo("Baxter Building");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getBuildingNameNumber(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void clinicName() {
    JsonNode clinicName =
        DeserializerUtil.getClinicName(generateNode("{\"clinic_name\":\"Baxter Clinic\"}"));
    assertThat(clinicName).isNotNull();
    assertThat(createMapper().convertValue(clinicName, String.class)).isEqualTo("Baxter Clinic");
    clinicName = DeserializerUtil.getClinicName(generateNode("{\"clinicName\":\"Baxter Clinic\"}"));
    assertThat(clinicName).isNotNull();
    assertThat(createMapper().convertValue(clinicName, String.class)).isEqualTo("Baxter Clinic");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getClinicName(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void countryCode() {
    JsonNode countryCode =
        DeserializerUtil.getCountryCode(generateNode("{\"country_code\":\"US\"}"));
    assertThat(countryCode).isNotNull();
    assertThat(createMapper().convertValue(countryCode, String.class)).isEqualTo("US");
    countryCode = DeserializerUtil.getCountryCode(generateNode("{\"countryCode\":\"US\"}"));
    assertThat(countryCode).isNotNull();
    assertThat(createMapper().convertValue(countryCode, String.class)).isEqualTo("US");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getCountryCode(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void detailedServices() {
    JsonNode detailedServices =
        DeserializerUtil.getDetailedServices(generateNode("{\"detailed_services\":\"[]\"}"));
    assertThat(detailedServices).isNotNull();
    assertThat(
            createMapper()
                .convertValue(
                    detailedServices,
                    gov.va.api.lighthouse.facilities.api.v0.DetailedService.class))
        .usingRecursiveComparison()
        .isEqualTo(
            gov.va.api.lighthouse.facilities.api.v0.DetailedService.builder()
                .serviceId(gov.va.api.lighthouse.facilities.api.v0.DetailedService.INVALID_SVC_ID)
                .phoneNumbers(emptyList())
                .serviceLocations(emptyList())
                .build());
    detailedServices =
        DeserializerUtil.getDetailedServices(generateNode("{\"detailedServices\":\"[]\"}"));
    assertThat(detailedServices).isNotNull();
    assertThat(
            createMapper()
                .convertValue(
                    detailedServices,
                    gov.va.api.lighthouse.facilities.api.v0.DetailedService.class))
        .usingRecursiveComparison()
        .isEqualTo(
            gov.va.api.lighthouse.facilities.api.v0.DetailedService.builder()
                .serviceId(gov.va.api.lighthouse.facilities.api.v0.DetailedService.INVALID_SVC_ID)
                .phoneNumbers(emptyList())
                .serviceLocations(emptyList())
                .build());
    detailedServices =
        DeserializerUtil.getDetailedServices(generateNode("{\"detailed_services\":\"[]\"}"));
    assertThat(detailedServices).isNotNull();
    assertThat(
            createMapper()
                .convertValue(
                    detailedServices,
                    gov.va.api.lighthouse.facilities.api.v1.DetailedService.class))
        .usingRecursiveComparison()
        .isEqualTo(
            gov.va.api.lighthouse.facilities.api.v1.DetailedService.builder()
                .serviceId(gov.va.api.lighthouse.facilities.api.v1.DetailedService.INVALID_SVC_ID)
                .phoneNumbers(emptyList())
                .serviceLocations(emptyList())
                .build());
    detailedServices =
        DeserializerUtil.getDetailedServices(generateNode("{\"detailedServices\":\"[]\"}"));
    assertThat(detailedServices).isNotNull();
    assertThat(
            createMapper()
                .convertValue(
                    detailedServices,
                    gov.va.api.lighthouse.facilities.api.v1.DetailedService.class))
        .usingRecursiveComparison()
        .isEqualTo(
            gov.va.api.lighthouse.facilities.api.v1.DetailedService.builder()
                .serviceId(gov.va.api.lighthouse.facilities.api.v1.DetailedService.INVALID_SVC_ID)
                .phoneNumbers(emptyList())
                .serviceLocations(emptyList())
                .build());
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getDetailedServices(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void effectiveDate() {
    JsonNode effectiveDate =
        DeserializerUtil.getEffectiveDate(generateNode("{\"effective_date\":\"2022-02-20\"}"));
    assertThat(effectiveDate).isNotNull();
    assertThat(createMapper().convertValue(effectiveDate, LocalDate.class)).isEqualTo("2022-02-20");
    effectiveDate =
        DeserializerUtil.getEffectiveDate(generateNode("{\"effectiveDate\":\"2022-02-20\"}"));
    assertThat(effectiveDate).isNotNull();
    assertThat(createMapper().convertValue(effectiveDate, LocalDate.class)).isEqualTo("2022-02-20");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getEffectiveDate(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void emailAddress() {
    JsonNode emailAddress =
        DeserializerUtil.getEmailAddress(generateNode("{\"email_address\":\"georgea@va.gov\"}"));
    assertThat(emailAddress).isNotNull();
    assertThat(createMapper().convertValue(emailAddress, String.class)).isEqualTo("georgea@va.gov");
    emailAddress =
        DeserializerUtil.getEmailAddress(generateNode("{\"emailAddress\":\"georgea@va.gov\"}"));
    assertThat(emailAddress).isNotNull();
    assertThat(createMapper().convertValue(emailAddress, String.class)).isEqualTo("georgea@va.gov");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getEmailAddress(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void emailContacts() {
    JsonNode emailContacts =
        DeserializerUtil.getEmailContacts(
            generateNode(
                "{\"email_contacts\" : "
                    + "{"
                    + "\"email_address\" : \"georgea@va.gov\","
                    + "\"email_label\" : \"George Anderson\""
                    + "}"
                    + "}"));
    assertThat(emailContacts).isNotNull();
    assertThat(
            createMapper()
                .convertValue(
                    emailContacts,
                    gov.va.api.lighthouse.facilities.api.v0.DetailedService
                        .DetailedServiceEmailContact.class))
        .usingRecursiveComparison()
        .isEqualTo(
            gov.va.api.lighthouse.facilities.api.v0.DetailedService.DetailedServiceEmailContact
                .builder()
                .emailAddress("georgea@va.gov")
                .emailLabel("George Anderson")
                .build());
    emailContacts =
        DeserializerUtil.getEmailContacts(
            generateNode(
                "{\"emailContacts\" : "
                    + "{"
                    + "\"emailAddress\" : \"georgea@va.gov\","
                    + "\"emailLabel\" : \"George Anderson\""
                    + "}"
                    + "}"));
    assertThat(emailContacts).isNotNull();
    assertThat(
            createMapper()
                .convertValue(
                    emailContacts,
                    gov.va.api.lighthouse.facilities.api.v1.DetailedService
                        .DetailedServiceEmailContact.class))
        .usingRecursiveComparison()
        .isEqualTo(
            gov.va.api.lighthouse.facilities.api.v1.DetailedService.DetailedServiceEmailContact
                .builder()
                .emailAddress("georgea@va.gov")
                .emailLabel("George Anderson")
                .build());
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getEmailContacts(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void emailLabel() {
    JsonNode emailLabel =
        DeserializerUtil.getEmailLabel(generateNode("{\"email_label\":\"George Anderson\"}"));
    assertThat(emailLabel).isNotNull();
    assertThat(createMapper().convertValue(emailLabel, String.class)).isEqualTo("George Anderson");
    emailLabel =
        DeserializerUtil.getEmailLabel(generateNode("{\"emailLabel\":\"George Anderson\"}"));
    assertThat(emailLabel).isNotNull();
    assertThat(createMapper().convertValue(emailLabel, String.class)).isEqualTo("George Anderson");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getEmailLabel(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void enrollmentCoordinator() {
    JsonNode enrollmentCoordinator =
        DeserializerUtil.getEnrollmentCoordinator(
            generateNode("{\"enrollment_coordinator\":\"321-637-3527\"}"));
    assertThat(enrollmentCoordinator).isNotNull();
    assertThat(createMapper().convertValue(enrollmentCoordinator, String.class))
        .isEqualTo("321-637-3527");
    enrollmentCoordinator =
        DeserializerUtil.getEnrollmentCoordinator(
            generateNode("{\"enrollmentCoordinator\":\"321-637-3527\"}"));
    assertThat(enrollmentCoordinator).isNotNull();
    assertThat(createMapper().convertValue(enrollmentCoordinator, String.class))
        .isEqualTo("321-637-3527");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getEnrollmentCoordinator(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void facilityDescription() {
    JsonNode facilityDescription =
        DeserializerUtil.getFacilityDescription(
            generateNode("{\"description_facility\":\"I'm a facility service!\"}"));
    assertThat(facilityDescription).isNotNull();
    assertThat(createMapper().convertValue(facilityDescription, String.class))
        .isEqualTo("I'm a facility service!");
    facilityDescription =
        DeserializerUtil.getFacilityDescription(
            generateNode("{\"descriptionFacility\":\"I'm a facility service!\"}"));
    assertThat(facilityDescription).isNotNull();
    assertThat(createMapper().convertValue(facilityDescription, String.class))
        .isEqualTo("I'm a facility service!");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getFacilityDescription(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void facilityServiceHours() {
    JsonNode facilityServiceHours =
        DeserializerUtil.getFacilityServiceHours(
            generateNode(
                "{\"facility_service_hours\":{"
                    + "\"Friday\": \"830AM-430PM\","
                    + "\"Monday\": \"830AM-700PM\","
                    + "\"Sunday\": \"Closed\","
                    + "\"Tuesday\": \"830AM-700PM\","
                    + "\"Saturday\": \"Closed\","
                    + "\"Thursday\": \"830AM-600PM\","
                    + "\"Wednesday\": \"ANY STRING b\""
                    + "}}"));
    assertThat(facilityServiceHours).isNotNull();
    assertThat(
            createMapper()
                .convertValue(
                    facilityServiceHours,
                    gov.va.api.lighthouse.facilities.api.v0.DetailedService.DetailedServiceHours
                        .class))
        .isEqualTo(
            gov.va.api.lighthouse.facilities.api.v0.DetailedService.DetailedServiceHours.builder()
                .monday("830AM-700PM")
                .tuesday("830AM-700PM")
                .wednesday("ANY STRING b")
                .thursday("830AM-600PM")
                .friday("830AM-430PM")
                .saturday("Closed")
                .sunday("Closed")
                .build());
    facilityServiceHours =
        DeserializerUtil.getFacilityServiceHours(
            generateNode(
                "{\"facilityServiceHours\":{"
                    + "\"friday\": \"830AM-430PM\","
                    + "\"monday\": \"830AM-700PM\","
                    + "\"sunday\": \"Closed\","
                    + "\"tuesday\": \"830AM-700PM\","
                    + "\"saturday\": \"Closed\","
                    + "\"thursday\": \"830AM-600PM\","
                    + "\"wednesday\": \"ANY STRING b\""
                    + "}}"));
    assertThat(facilityServiceHours).isNotNull();
    assertThat(
            createMapper()
                .convertValue(
                    facilityServiceHours,
                    gov.va.api.lighthouse.facilities.api.v1.DetailedService.DetailedServiceHours
                        .class))
        .isEqualTo(
            gov.va.api.lighthouse.facilities.api.v1.DetailedService.DetailedServiceHours.builder()
                .monday("830AM-700PM")
                .tuesday("830AM-700PM")
                .wednesday("ANY STRING b")
                .thursday("830AM-600PM")
                .friday("830AM-430PM")
                .saturday("Closed")
                .sunday("Closed")
                .build());
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getFacilityServiceHours(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void facilityType() {
    JsonNode facilityType =
        DeserializerUtil.getFacilityType(
            generateNode("{\"facility_type\":\"va_health_facility\"}"));
    assertThat(facilityType).isNotNull();
    assertThat(
            createMapper()
                .convertValue(
                    facilityType,
                    gov.va.api.lighthouse.facilities.api.v0.Facility.FacilityType.class))
        .isEqualTo(
            gov.va.api.lighthouse.facilities.api.v0.Facility.FacilityType.va_health_facility);
    facilityType =
        DeserializerUtil.getFacilityType(generateNode("{\"facilityType\":\"va_health_facility\"}"));
    assertThat(facilityType).isNotNull();
    assertThat(
            createMapper()
                .convertValue(
                    facilityType,
                    gov.va.api.lighthouse.facilities.api.v1.Facility.FacilityType.class))
        .isEqualTo(
            gov.va.api.lighthouse.facilities.api.v1.Facility.FacilityType.va_health_facility);
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getFacilityType(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void fridayHours() {
    JsonNode fridayHours =
        DeserializerUtil.getFridayHours(generateNode("{\"Friday\":\"830AM-700PM\"}"));
    assertThat(fridayHours).isNotNull();
    assertThat(createMapper().convertValue(fridayHours, String.class)).isEqualTo("830AM-700PM");
    fridayHours = DeserializerUtil.getFridayHours(generateNode("{\"friday\":\"830AM-700PM\"}"));
    assertThat(fridayHours).isNotNull();
    assertThat(createMapper().convertValue(fridayHours, String.class)).isEqualTo("830AM-700PM");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getFridayHours(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @SneakyThrows
  private JsonNode generateNode(@NonNull String json) {
    return createMapper().readTree(json);
  }

  @Test
  @SneakyThrows
  void lastUpdated() {
    JsonNode lastUpdated =
        DeserializerUtil.getLastUpdated(generateNode("{\"last_updated\":\"2022-03-02\"}"));
    assertThat(lastUpdated).isNotNull();
    assertThat(createMapper().convertValue(lastUpdated, String.class)).isEqualTo("2022-03-02");
    lastUpdated = DeserializerUtil.getLastUpdated(generateNode("{\"lastUpdated\":\"2022-03-02\"}"));
    assertThat(lastUpdated).isNotNull();
    assertThat(createMapper().convertValue(lastUpdated, String.class)).isEqualTo("2022-03-02");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getLastUpdated(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void mentalHealthClinic() {
    JsonNode mentalHealthClinic =
        DeserializerUtil.getMentalHealthClinic(
            generateNode("{\"mental_health_clinic\":\"321-637-3788\"}"));
    assertThat(mentalHealthClinic).isNotNull();
    assertThat(createMapper().convertValue(mentalHealthClinic, String.class))
        .isEqualTo("321-637-3788");
    mentalHealthClinic =
        DeserializerUtil.getMentalHealthClinic(
            generateNode("{\"mentalHealthClinic\":\"321-637-3788\"}"));
    assertThat(mentalHealthClinic).isNotNull();
    assertThat(createMapper().convertValue(mentalHealthClinic, String.class))
        .isEqualTo("321-637-3788");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getMentalHealthClinic(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void mondayHours() {
    JsonNode mondayHours =
        DeserializerUtil.getMondayHours(generateNode("{\"Monday\":\"830AM-700PM\"}"));
    assertThat(mondayHours).isNotNull();
    assertThat(createMapper().convertValue(mondayHours, String.class)).isEqualTo("830AM-700PM");
    mondayHours = DeserializerUtil.getMondayHours(generateNode("{\"monday\":\"830AM-700PM\"}"));
    assertThat(mondayHours).isNotNull();
    assertThat(createMapper().convertValue(mondayHours, String.class)).isEqualTo("830AM-700PM");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getMondayHours(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void onlineSchedulingAvailable() {
    JsonNode onlineSchedulingAvailable =
        DeserializerUtil.getOnlineSchedulingAvailable(
            generateNode("{\"online_scheduling_available\":\"True\"}"));
    assertThat(onlineSchedulingAvailable).isNotNull();
    assertThat(createMapper().convertValue(onlineSchedulingAvailable, Boolean.class))
        .isEqualTo(true);
    onlineSchedulingAvailable =
        DeserializerUtil.getOnlineSchedulingAvailable(
            generateNode("{\"onlineSchedulingAvailable\":\"True\"}"));
    assertThat(onlineSchedulingAvailable).isNotNull();
    assertThat(createMapper().convertValue(onlineSchedulingAvailable, Boolean.class))
        .isEqualTo(true);
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getOnlineSchedulingAvailable(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void operationalHoursSpecialInstructions() {
    JsonNode operationalHoursSpecialInstructions =
        DeserializerUtil.getOperationalHoursSpecialInstructions(
            generateNode(
                "{\"operational_hours_special_instructions\":\"Normal business hours are Monday through Friday, 8:00 a.m. to 4:30 p.m.\"}"));
    assertThat(operationalHoursSpecialInstructions).isNotNull();
    assertThat(createMapper().convertValue(operationalHoursSpecialInstructions, String.class))
        .isEqualTo("Normal business hours are Monday through Friday, 8:00 a.m. to 4:30 p.m.");
    operationalHoursSpecialInstructions =
        DeserializerUtil.getOperationalHoursSpecialInstructions(
            generateNode(
                "{\"operationalHoursSpecialInstructions\":\"Normal business hours are Monday through Friday, 8:00 a.m. to 4:30 p.m.\"}"));
    assertThat(operationalHoursSpecialInstructions).isNotNull();
    assertThat(createMapper().convertValue(operationalHoursSpecialInstructions, String.class))
        .isEqualTo("Normal business hours are Monday through Friday, 8:00 a.m. to 4:30 p.m.");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getOperationalHoursSpecialInstructions(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void opertingStatus() {
    JsonNode opertingStatus =
        DeserializerUtil.getOpertingStatus(
            generateNode(
                "{\"operating_status\": {"
                    + "\"code\" : \"NORMAL\","
                    + "\"additional_info\" : \"test additional info\""
                    + "}}"));
    assertThat(opertingStatus).isNotNull();
    assertThat(
            createMapper()
                .convertValue(
                    opertingStatus,
                    gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatus.class))
        .usingRecursiveComparison()
        .isEqualTo(
            gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatus.builder()
                .code(gov.va.api.lighthouse.facilities.api.v0.Facility.OperatingStatusCode.NORMAL)
                .additionalInfo("test additional info")
                .build());
    opertingStatus =
        DeserializerUtil.getOpertingStatus(
            generateNode(
                "{\"operatingStatus\": {"
                    + "\"code\" : \"NORMAL\","
                    + "\"additionalInfo\" : \"test additional info\""
                    + "}}"));
    assertThat(opertingStatus).isNotNull();
    assertThat(
            createMapper()
                .convertValue(
                    opertingStatus,
                    gov.va.api.lighthouse.facilities.api.v1.Facility.OperatingStatus.class))
        .usingRecursiveComparison()
        .isEqualTo(
            gov.va.api.lighthouse.facilities.api.v1.Facility.OperatingStatus.builder()
                .code(gov.va.api.lighthouse.facilities.api.v1.Facility.OperatingStatusCode.NORMAL)
                .additionalInfo("test additional info")
                .build());
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getOpertingStatus(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void patientAdvocate() {
    JsonNode patientAdvocate =
        DeserializerUtil.getPatientAdvocate(
            generateNode("{\"patient_advocate\":\"818-895-9564\"}"));
    assertThat(patientAdvocate).isNotNull();
    assertThat(createMapper().convertValue(patientAdvocate, String.class))
        .isEqualTo("818-895-9564");
    patientAdvocate =
        DeserializerUtil.getPatientAdvocate(generateNode("{\"patientAdvocate\":\"818-895-9564\"}"));
    assertThat(patientAdvocate).isNotNull();
    assertThat(createMapper().convertValue(patientAdvocate, String.class))
        .isEqualTo("818-895-9564");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getPatientAdvocate(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void phoneNumbers() {
    JsonNode phoneNumbers =
        DeserializerUtil.getPhoneNumbers(
            generateNode(
                "{\"appointment_phones\":"
                    + "{"
                    + "\"extension\": \"123\","
                    + "\"label\": \"Main phone\","
                    + "\"number\": \"555-555-1212\","
                    + "\"type\": \"tel\""
                    + "}"
                    + "}"));
    assertThat(phoneNumbers).isNotNull();
    assertThat(
            createMapper()
                .convertValue(
                    phoneNumbers,
                    gov.va.api.lighthouse.facilities.api.v0.DetailedService.AppointmentPhoneNumber
                        .class))
        .usingRecursiveComparison()
        .isEqualTo(
            gov.va.api.lighthouse.facilities.api.v0.DetailedService.AppointmentPhoneNumber.builder()
                .extension("123")
                .label("Main phone")
                .number("555-555-1212")
                .type("tel")
                .build());
    phoneNumbers =
        DeserializerUtil.getPhoneNumbers(
            generateNode(
                "{\"appointmentPhones\":"
                    + "{"
                    + "\"extension\": \"123\","
                    + "\"label\": \"Main phone\","
                    + "\"number\": \"555-555-1212\","
                    + "\"type\": \"tel\""
                    + "}"
                    + "}"));
    assertThat(phoneNumbers).isNotNull();
    assertThat(
            createMapper()
                .convertValue(
                    phoneNumbers,
                    gov.va.api.lighthouse.facilities.api.v1.DetailedService.AppointmentPhoneNumber
                        .class))
        .usingDefaultComparator()
        .isEqualTo(
            gov.va.api.lighthouse.facilities.api.v1.DetailedService.AppointmentPhoneNumber.builder()
                .extension("123")
                .label("Main phone")
                .number("555-555-1212")
                .type("tel")
                .build());
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getPhoneNumbers(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void primaryCareRoutine() {
    JsonNode primaryCareRoutine =
        DeserializerUtil.getPrimaryCareRoutine(generateNode("{\"primary_care_routine\":\"0.83\"}"));
    assertThat(primaryCareRoutine).isNotNull();
    assertThat(createMapper().convertValue(primaryCareRoutine, BigDecimal.class))
        .isEqualTo(BigDecimal.valueOf(0.83));
    primaryCareRoutine =
        DeserializerUtil.getPrimaryCareRoutine(generateNode("{\"primaryCareRoutine\":\"0.83\"}"));
    assertThat(primaryCareRoutine).isNotNull();
    assertThat(createMapper().convertValue(primaryCareRoutine, BigDecimal.class))
        .isEqualTo(BigDecimal.valueOf(0.83));
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getPrimaryCareRoutine(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void primaryCareUrgent() {
    JsonNode primaryCareUrgent =
        DeserializerUtil.getPrimaryCareUrgent(generateNode("{\"primary_care_urgent\":\"0.83\"}"));
    assertThat(primaryCareUrgent).isNotNull();
    assertThat(createMapper().convertValue(primaryCareUrgent, BigDecimal.class))
        .isEqualTo(BigDecimal.valueOf(0.83));
    primaryCareUrgent =
        DeserializerUtil.getPrimaryCareUrgent(generateNode("{\"primaryCareUrgent\":\"0.83\"}"));
    assertThat(primaryCareUrgent).isNotNull();
    assertThat(createMapper().convertValue(primaryCareUrgent, BigDecimal.class))
        .isEqualTo(BigDecimal.valueOf(0.83));
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getPrimaryCareUrgent(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void referralRequired() {
    JsonNode referralRequired =
        DeserializerUtil.getReferralRequired(generateNode("{\"referral_required\":\"False\"}"));
    assertThat(referralRequired).isNotNull();
    assertThat(createMapper().convertValue(referralRequired, Boolean.class)).isEqualTo(false);
    referralRequired =
        DeserializerUtil.getReferralRequired(generateNode("{\"referralRequired\":\"False\"}"));
    assertThat(referralRequired).isNotNull();
    assertThat(createMapper().convertValue(referralRequired, Boolean.class)).isEqualTo(false);
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getReferralRequired(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void saturdayHours() {
    JsonNode saturdayHours =
        DeserializerUtil.getSaturdayHours(generateNode("{\"Saturday\":\"Closed\"}"));
    assertThat(saturdayHours).isNotNull();
    assertThat(createMapper().convertValue(saturdayHours, String.class)).isEqualTo("Closed");
    saturdayHours = DeserializerUtil.getSaturdayHours(generateNode("{\"saturday\":\"Closed\"}"));
    assertThat(saturdayHours).isNotNull();
    assertThat(createMapper().convertValue(saturdayHours, String.class)).isEqualTo("Closed");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getSaturdayHours(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void serviceLocationAddress() {
    JsonNode serviceLocationAddress =
        DeserializerUtil.getServiceLocationAddress(
            generateNode(
                "{\"service_location_address\":{"
                    + "\"building_name_number\" : \"Baxter Building\","
                    + "\"clinic_name\" : \"Baxter Clinic\","
                    + "\"wing_floor_or_room_number\" : \"Wing East\","
                    + "\"address_line1\" : \"122 Main St.\","
                    + "\"address_line2\" : \"West Side Apt# 227\","
                    + "\"city\" : \"Rochester\","
                    + "\"state\" : \"NY\","
                    + "\"zip_code\" : \"14623-1345\","
                    + "\"country_code\" : \"US\""
                    + "}}"));
    assertThat(serviceLocationAddress).isNotNull();
    assertThat(
            createMapper()
                .convertValue(
                    serviceLocationAddress,
                    gov.va.api.lighthouse.facilities.api.v0.DetailedService.DetailedServiceAddress
                        .class))
        .usingRecursiveComparison()
        .isEqualTo(
            gov.va.api.lighthouse.facilities.api.v0.DetailedService.DetailedServiceAddress.builder()
                .buildingNameNumber("Baxter Building")
                .clinicName("Baxter Clinic")
                .wingFloorOrRoomNumber("Wing East")
                .address1("122 Main St.")
                .address2("West Side Apt# 227")
                .city("Rochester")
                .state("NY")
                .zipCode("14623-1345")
                .countryCode("US")
                .build());
    serviceLocationAddress =
        DeserializerUtil.getServiceLocationAddress(
            generateNode(
                "{\"serviceLocationAddress\":{"
                    + "\"buildingNameNumber\" : \"Baxter Building\","
                    + "\"clinicName\" : \"Baxter Clinic\","
                    + "\"wingFloorOrRoomNumber\" : \"Wing East\","
                    + "\"addressLine1\" : \"122 Main St.\","
                    + "\"addressLine2\" : \"West Side Apt# 227\","
                    + "\"city\" : \"Rochester\","
                    + "\"state\" : \"NY\","
                    + "\"zipCode\" : \"14623-1345\","
                    + "\"countryCode\" : \"US\""
                    + "}}"));
    assertThat(serviceLocationAddress).isNotNull();
    assertThat(
            createMapper()
                .convertValue(
                    serviceLocationAddress,
                    gov.va.api.lighthouse.facilities.api.v1.DetailedService.DetailedServiceAddress
                        .class))
        .usingRecursiveComparison()
        .isEqualTo(
            gov.va.api.lighthouse.facilities.api.v1.DetailedService.DetailedServiceAddress.builder()
                .buildingNameNumber("Baxter Building")
                .clinicName("Baxter Clinic")
                .wingFloorOrRoomNumber("Wing East")
                .address1("122 Main St.")
                .address2("West Side Apt# 227")
                .city("Rochester")
                .state("NY")
                .zipCode("14623-1345")
                .countryCode("US")
                .build());
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getServiceLocationAddress(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void serviceLocations() {
    JsonNode serviceLocations =
        DeserializerUtil.getServiceLocations(
            generateNode(
                "{\"service_locations\" : "
                    + "{"
                    + "\"service_location_address\" : {"
                    + "\"building_name_number\" : \"Baxter Building\","
                    + "\"clinic_name\" : \"Baxter Clinic\","
                    + "\"wing_floor_or_room_number\" : \"Wing East\","
                    + "\"address_line1\" : \"122 Main St.\","
                    + "\"address_line2\" : \"West Side Apt# 227\","
                    + "\"city\" : \"Rochester\","
                    + "\"state\" : \"NY\","
                    + "\"zip_code\" : \"14623-1345\","
                    + "\"country_code\" : \"US\""
                    + "},"
                    + "\"appointment_phones\" : ["
                    + "{"
                    + "\"extension\" : \"567\","
                    + "\"label\" : \"Alt phone\","
                    + "\"number\" : \"556-565-1119\","
                    + "\"type\" : \"tel\""
                    + "}"
                    + "],"
                    + "\"email_contacts\" : ["
                    + "{"
                    + "\"email_address\" : \"georgea@va.gov\","
                    + "\"email_label\" : \"George Anderson\""
                    + "}"
                    + "],"
                    + "\"facility_service_hours\" : {"
                    + "\"Monday\" : \"8:30AM-7:00PM\","
                    + "\"Tuesday\" : \"8:30AM-7:00PM\","
                    + "\"Wednesday\" : \"8:30AM-7:00PM\","
                    + "\"Thursday\" : \"8:30AM-7:00PM\","
                    + "\"Friday\" : \"8:30AM-7:00PM\","
                    + "\"Saturday\" : \"8:30AM-7:00PM\","
                    + "\"Sunday\" : \"CLOSED\""
                    + "},"
                    + "\"additional_hours_info\" : \"additional hours info\""
                    + "}"
                    + "}"));
    assertThat(serviceLocations).isNotNull();
    assertThat(
            createMapper()
                .convertValue(
                    serviceLocations,
                    gov.va.api.lighthouse.facilities.api.v0.DetailedService.DetailedServiceLocation
                        .class))
        .usingRecursiveComparison()
        .isEqualTo(
            gov.va.api.lighthouse.facilities.api.v0.DetailedService.DetailedServiceLocation
                .builder()
                .serviceLocationAddress(
                    gov.va.api.lighthouse.facilities.api.v0.DetailedService.DetailedServiceAddress
                        .builder()
                        .buildingNameNumber("Baxter Building")
                        .clinicName("Baxter Clinic")
                        .wingFloorOrRoomNumber("Wing East")
                        .address1("122 Main St.")
                        .address2("West Side Apt# 227")
                        .city("Rochester")
                        .state("NY")
                        .zipCode("14623-1345")
                        .countryCode("US")
                        .build())
                .appointmentPhoneNumbers(
                    List.of(
                        gov.va.api.lighthouse.facilities.api.v0.DetailedService
                            .AppointmentPhoneNumber.builder()
                            .extension("567")
                            .label("Alt phone")
                            .number("556-565-1119")
                            .type("tel")
                            .build()))
                .emailContacts(
                    List.of(
                        gov.va.api.lighthouse.facilities.api.v0.DetailedService
                            .DetailedServiceEmailContact.builder()
                            .emailAddress("georgea@va.gov")
                            .emailLabel("George Anderson")
                            .build()))
                .facilityServiceHours(
                    gov.va.api.lighthouse.facilities.api.v0.DetailedService.DetailedServiceHours
                        .builder()
                        .monday("8:30AM-7:00PM")
                        .tuesday("8:30AM-7:00PM")
                        .wednesday("8:30AM-7:00PM")
                        .thursday("8:30AM-7:00PM")
                        .friday("8:30AM-7:00PM")
                        .saturday("8:30AM-7:00PM")
                        .sunday("CLOSED")
                        .build())
                .additionalHoursInfo("additional hours info")
                .build());
    serviceLocations =
        DeserializerUtil.getServiceLocations(
            generateNode(
                "{\"serviceLocations\" : "
                    + "{"
                    + "\"serviceLocationAddress\" : {"
                    + "\"buildingNameNumber\" : \"Baxter Building\","
                    + "\"clinicName\" : \"Baxter Clinic\","
                    + "\"wingFloorOrRoomNumber\" : \"Wing East\","
                    + "\"addressLine1\" : \"122 Main St.\","
                    + "\"addressLine2\" : \"West Side Apt# 227\","
                    + "\"city\" : \"Rochester\","
                    + "\"state\" : \"NY\","
                    + "\"zipCode\" : \"14623-1345\","
                    + "\"countryCode\" : \"US\""
                    + "},"
                    + "\"appointmentPhones\" : ["
                    + "{"
                    + "\"extension\" : \"567\","
                    + "\"label\" : \"Alt phone\","
                    + "\"number\" : \"556-565-1119\","
                    + "\"type\" : \"tel\""
                    + "}"
                    + "],"
                    + "\"emailContacts\" : ["
                    + "{"
                    + "\"emailAddress\" : \"georgea@va.gov\","
                    + "\"emailLabel\" : \"George Anderson\""
                    + "}"
                    + "],"
                    + "\"facilityServiceHours\" : {"
                    + "\"monday\" : \"8:30AM-7:00PM\","
                    + "\"tuesday\" : \"8:30AM-7:00PM\","
                    + "\"wednesday\" : \"8:30AM-7:00PM\","
                    + "\"thursday\" : \"8:30AM-7:00PM\","
                    + "\"friday\" : \"8:30AM-7:00PM\","
                    + "\"saturday\" : \"8:30AM-7:00PM\","
                    + "\"sunday\" : \"CLOSED\""
                    + "},"
                    + "\"additionalHoursInfo\" : \"additional hours info\""
                    + "}"
                    + "}"));
    assertThat(serviceLocations).isNotNull();
    assertThat(
            createMapper()
                .convertValue(
                    serviceLocations,
                    gov.va.api.lighthouse.facilities.api.v1.DetailedService.DetailedServiceLocation
                        .class))
        .usingRecursiveComparison()
        .isEqualTo(
            gov.va.api.lighthouse.facilities.api.v1.DetailedService.DetailedServiceLocation
                .builder()
                .serviceLocationAddress(
                    gov.va.api.lighthouse.facilities.api.v1.DetailedService.DetailedServiceAddress
                        .builder()
                        .buildingNameNumber("Baxter Building")
                        .clinicName("Baxter Clinic")
                        .wingFloorOrRoomNumber("Wing East")
                        .address1("122 Main St.")
                        .address2("West Side Apt# 227")
                        .city("Rochester")
                        .state("NY")
                        .zipCode("14623-1345")
                        .countryCode("US")
                        .build())
                .appointmentPhoneNumbers(
                    List.of(
                        gov.va.api.lighthouse.facilities.api.v1.DetailedService
                            .AppointmentPhoneNumber.builder()
                            .extension("567")
                            .label("Alt phone")
                            .number("556-565-1119")
                            .type("tel")
                            .build()))
                .emailContacts(
                    List.of(
                        gov.va.api.lighthouse.facilities.api.v1.DetailedService
                            .DetailedServiceEmailContact.builder()
                            .emailAddress("georgea@va.gov")
                            .emailLabel("George Anderson")
                            .build()))
                .facilityServiceHours(
                    gov.va.api.lighthouse.facilities.api.v1.DetailedService.DetailedServiceHours
                        .builder()
                        .monday("8:30AM-7:00PM")
                        .tuesday("8:30AM-7:00PM")
                        .wednesday("8:30AM-7:00PM")
                        .thursday("8:30AM-7:00PM")
                        .friday("8:30AM-7:00PM")
                        .saturday("8:30AM-7:00PM")
                        .sunday("CLOSED")
                        .build())
                .additionalHoursInfo("additional hours info")
                .build());
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getServiceLocations(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void specialtyCareRoutine() {
    JsonNode specialtyCareRoutine =
        DeserializerUtil.getSpecialtyCareRoutine(
            generateNode("{\"specialty_care_routine\":\"0.83\"}"));
    assertThat(specialtyCareRoutine).isNotNull();
    assertThat(createMapper().convertValue(specialtyCareRoutine, BigDecimal.class))
        .isEqualTo(BigDecimal.valueOf(0.83));
    specialtyCareRoutine =
        DeserializerUtil.getSpecialtyCareRoutine(
            generateNode("{\"specialtyCareRoutine\":\"0.83\"}"));
    assertThat(specialtyCareRoutine).isNotNull();
    assertThat(createMapper().convertValue(specialtyCareRoutine, BigDecimal.class))
        .isEqualTo(BigDecimal.valueOf(0.83));
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getSpecialtyCareRoutine(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void specialtyCareUrgent() {
    JsonNode specialtyCareUrgent =
        DeserializerUtil.getSpecialtyCareUrgent(
            generateNode("{\"specialty_care_urgent\":\"0.83\"}"));
    assertThat(specialtyCareUrgent).isNotNull();
    assertThat(createMapper().convertValue(specialtyCareUrgent, BigDecimal.class))
        .isEqualTo(BigDecimal.valueOf(0.83));
    specialtyCareUrgent =
        DeserializerUtil.getSpecialtyCareUrgent(generateNode("{\"specialtyCareUrgent\":\"0.83\"}"));
    assertThat(specialtyCareUrgent).isNotNull();
    assertThat(createMapper().convertValue(specialtyCareUrgent, BigDecimal.class))
        .isEqualTo(BigDecimal.valueOf(0.83));
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getSpecialtyCareUrgent(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void sundayHours() {
    JsonNode sundayHours = DeserializerUtil.getSundayHours(generateNode("{\"Sunday\":\"Closed\"}"));
    assertThat(sundayHours).isNotNull();
    assertThat(createMapper().convertValue(sundayHours, String.class)).isEqualTo("Closed");
    sundayHours = DeserializerUtil.getSundayHours(generateNode("{\"sunday\":\"Closed\"}"));
    assertThat(sundayHours).isNotNull();
    assertThat(createMapper().convertValue(sundayHours, String.class)).isEqualTo("Closed");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getSundayHours(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void thursdayHours() {
    JsonNode thursdayHours =
        DeserializerUtil.getThursdayHours(generateNode("{\"Thursday\":\"830AM-700PM\"}"));
    assertThat(thursdayHours).isNotNull();
    assertThat(createMapper().convertValue(thursdayHours, String.class)).isEqualTo("830AM-700PM");
    thursdayHours =
        DeserializerUtil.getThursdayHours(generateNode("{\"thursday\":\"830AM-700PM\"}"));
    assertThat(thursdayHours).isNotNull();
    assertThat(createMapper().convertValue(thursdayHours, String.class)).isEqualTo("830AM-700PM");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getThursdayHours(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void timeZone() {
    JsonNode timeZone =
        DeserializerUtil.getTimeZone(generateNode("{\"time_zone\":\"America/New_York\"}"));
    assertThat(timeZone).isNotNull();
    assertThat(createMapper().convertValue(timeZone, String.class)).isEqualTo("America/New_York");
    timeZone = DeserializerUtil.getTimeZone(generateNode("{\"timeZone\":\"America/New_York\"}"));
    assertThat(timeZone).isNotNull();
    assertThat(createMapper().convertValue(timeZone, String.class)).isEqualTo("America/New_York");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getTimeZone(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void tuesdayHours() {
    JsonNode tuesdayHours =
        DeserializerUtil.getTuesdayHours(generateNode("{\"Tuesday\":\"830AM-700PM\"}"));
    assertThat(tuesdayHours).isNotNull();
    assertThat(createMapper().convertValue(tuesdayHours, String.class)).isEqualTo("830AM-700PM");
    tuesdayHours = DeserializerUtil.getTuesdayHours(generateNode("{\"tuesday\":\"830AM-700PM\"}"));
    assertThat(tuesdayHours).isNotNull();
    assertThat(createMapper().convertValue(tuesdayHours, String.class)).isEqualTo("830AM-700PM");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getTuesdayHours(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void waitTimes() {
    JsonNode waitTimes =
        DeserializerUtil.getWaitTimes(
            generateNode(
                "{\"wait_times\" : {"
                    + "\"health\" : ["
                    + "{"
                    + "\"service\" : \"Dermatology\","
                    + "\"new\" : 3.714285,"
                    + "\"established\" : 0.0"
                    + "},"
                    + "{"
                    + "\"service\" : \"PrimaryCare\","
                    + "\"new\" : 13.727272,"
                    + "\"established\" : 10.392441"
                    + "},"
                    + "{"
                    + "\"service\" : \"SpecialtyCare\","
                    + "\"new\" : 5.222222,"
                    + "\"established\" : 0.0"
                    + "},"
                    + "{"
                    + "\"service\" : \"MentalHealthCare\","
                    + "\"new\" : 5.75,"
                    + "\"established\" : 2.634703"
                    + "}"
                    + "],"
                    + "\"effective_date\" : \"2022-02-24\""
                    + "}}"));
    assertThat(waitTimes).isNotNull();
    assertThat(
            createMapper()
                .convertValue(
                    waitTimes, gov.va.api.lighthouse.facilities.api.v0.Facility.WaitTimes.class))
        .usingRecursiveComparison()
        .isEqualTo(
            gov.va.api.lighthouse.facilities.api.v0.Facility.WaitTimes.builder()
                .health(
                    List.of(
                        gov.va.api.lighthouse.facilities.api.v0.Facility.PatientWaitTime.builder()
                            .service(
                                gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService
                                    .Dermatology)
                            .newPatientWaitTime(BigDecimal.valueOf(3.714285))
                            .establishedPatientWaitTime(BigDecimal.valueOf(0.0))
                            .build(),
                        gov.va.api.lighthouse.facilities.api.v0.Facility.PatientWaitTime.builder()
                            .service(
                                gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService
                                    .PrimaryCare)
                            .newPatientWaitTime(BigDecimal.valueOf(13.727272))
                            .establishedPatientWaitTime(BigDecimal.valueOf(10.392441))
                            .build(),
                        gov.va.api.lighthouse.facilities.api.v0.Facility.PatientWaitTime.builder()
                            .service(
                                gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService
                                    .SpecialtyCare)
                            .newPatientWaitTime(BigDecimal.valueOf(5.222222))
                            .establishedPatientWaitTime(BigDecimal.valueOf(0.0))
                            .build(),
                        gov.va.api.lighthouse.facilities.api.v0.Facility.PatientWaitTime.builder()
                            .service(
                                gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService
                                    .MentalHealthCare)
                            .newPatientWaitTime(BigDecimal.valueOf(5.75))
                            .establishedPatientWaitTime(BigDecimal.valueOf(2.634703))
                            .build()))
                .effectiveDate(LocalDate.parse("2022-02-24"))
                .build());
    waitTimes =
        DeserializerUtil.getWaitTimes(
            generateNode(
                "{\"waitTimes\" : {"
                    + "\"health\" : ["
                    + "{"
                    + "\"service\" : \"Dermatology\","
                    + "\"new\" : 3.714285,"
                    + "\"established\" : 0.0"
                    + "},"
                    + "{"
                    + "\"service\" : \"PrimaryCare\","
                    + "\"new\" : 13.727272,"
                    + "\"established\" : 10.392441"
                    + "},"
                    + "{"
                    + "\"service\" : \"Dental\","
                    + "\"new\" : 5.222222,"
                    + "\"established\" : 0.0"
                    + "},"
                    + "{"
                    + "\"service\" : \"MentalHealth\","
                    + "\"new\" : 5.75,"
                    + "\"established\" : 2.634703"
                    + "}"
                    + "],"
                    + "\"effectiveDate\" : \"2022-02-24\""
                    + "}}"));
    assertThat(waitTimes).isNotNull();
    assertThat(
            createMapper()
                .convertValue(
                    waitTimes, gov.va.api.lighthouse.facilities.api.v1.Facility.WaitTimes.class))
        .usingRecursiveComparison()
        .isEqualTo(
            gov.va.api.lighthouse.facilities.api.v1.Facility.WaitTimes.builder()
                .health(
                    List.of(
                        gov.va.api.lighthouse.facilities.api.v1.Facility.PatientWaitTime.builder()
                            .service(
                                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService
                                    .Dermatology)
                            .newPatientWaitTime(BigDecimal.valueOf(3.714285))
                            .establishedPatientWaitTime(BigDecimal.valueOf(0.0))
                            .build(),
                        gov.va.api.lighthouse.facilities.api.v1.Facility.PatientWaitTime.builder()
                            .service(
                                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService
                                    .PrimaryCare)
                            .newPatientWaitTime(BigDecimal.valueOf(13.727272))
                            .establishedPatientWaitTime(BigDecimal.valueOf(10.392441))
                            .build(),
                        gov.va.api.lighthouse.facilities.api.v1.Facility.PatientWaitTime.builder()
                            .service(
                                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService
                                    .Dental)
                            .newPatientWaitTime(BigDecimal.valueOf(5.222222))
                            .establishedPatientWaitTime(BigDecimal.valueOf(0.0))
                            .build(),
                        gov.va.api.lighthouse.facilities.api.v1.Facility.PatientWaitTime.builder()
                            .service(
                                gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService
                                    .MentalHealth)
                            .newPatientWaitTime(BigDecimal.valueOf(5.75))
                            .establishedPatientWaitTime(BigDecimal.valueOf(2.634703))
                            .build()))
                .effectiveDate(LocalDate.parse("2022-02-24"))
                .build());
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getWaitTimes(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void walkInsAccepted() {
    JsonNode walkInsAccepted =
        DeserializerUtil.getWalkInsAccepted(generateNode("{\"walk_ins_accepted\":\"True\"}"));
    assertThat(walkInsAccepted).isNotNull();
    assertThat(createMapper().convertValue(walkInsAccepted, Boolean.class)).isEqualTo(true);
    walkInsAccepted =
        DeserializerUtil.getWalkInsAccepted(generateNode("{\"walkInsAccepted\":\"True\"}"));
    assertThat(walkInsAccepted).isNotNull();
    assertThat(createMapper().convertValue(walkInsAccepted, Boolean.class)).isEqualTo(true);
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getWalkInsAccepted(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void wednesdayHours() {
    JsonNode wednesdayHours =
        DeserializerUtil.getWednesdayHours(generateNode("{\"Wednesday\":\"830AM-700PM\"}"));
    assertThat(wednesdayHours).isNotNull();
    assertThat(createMapper().convertValue(wednesdayHours, String.class)).isEqualTo("830AM-700PM");
    wednesdayHours =
        DeserializerUtil.getWednesdayHours(generateNode("{\"wednesday\":\"830AM-700PM\"}"));
    assertThat(wednesdayHours).isNotNull();
    assertThat(createMapper().convertValue(wednesdayHours, String.class)).isEqualTo("830AM-700PM");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getWednesdayHours(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void wingFloorOrRoomNumber() {
    JsonNode wingFloorOrRoomNumber =
        DeserializerUtil.getWingFloorOrRoomNumber(
            generateNode("{\"wing_floor_or_room_number\":\"Wing East\"}"));
    assertThat(wingFloorOrRoomNumber).isNotNull();
    assertThat(createMapper().convertValue(wingFloorOrRoomNumber, String.class))
        .isEqualTo("Wing East");
    wingFloorOrRoomNumber =
        DeserializerUtil.getWingFloorOrRoomNumber(
            generateNode("{\"wingFloorOrRoomNumber\":\"Wing East\"}"));
    assertThat(wingFloorOrRoomNumber).isNotNull();
    assertThat(createMapper().convertValue(wingFloorOrRoomNumber, String.class))
        .isEqualTo("Wing East");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getWingFloorOrRoomNumber(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }

  @Test
  @SneakyThrows
  void zipCode() {
    JsonNode zipCode = DeserializerUtil.getZipCode(generateNode("{\"zip_code\":\"14623-1345\"}"));
    assertThat(zipCode).isNotNull();
    assertThat(createMapper().convertValue(zipCode, String.class)).isEqualTo("14623-1345");
    zipCode = DeserializerUtil.getZipCode(generateNode("{\"zipCode\":\"14623-1345\"}"));
    assertThat(zipCode).isNotNull();
    assertThat(createMapper().convertValue(zipCode, String.class)).isEqualTo("14623-1345");
    // Exceptions
    assertThatThrownBy(() -> DeserializerUtil.getZipCode(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("node is marked non-null but is null");
  }
}
