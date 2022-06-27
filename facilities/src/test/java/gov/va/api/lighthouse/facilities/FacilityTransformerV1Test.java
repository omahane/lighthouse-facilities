package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.CMS_OVERLAY_SERVICE_NAME_COVID_19;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.va.api.lighthouse.facilities.api.v1.Facility;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class FacilityTransformerV1Test extends BaseFacilityTransformerTest {
  private DatamartFacility datamartFacility() {
    return datamartFacility(
        List.of(
            DatamartFacility.BenefitsService.EducationClaimAssistance,
            DatamartFacility.BenefitsService.FamilyMemberClaimAssistance),
        List.of(
            DatamartFacility.HealthService.PrimaryCare,
            DatamartFacility.HealthService.UrgentCare,
            DatamartFacility.HealthService.EmergencyCare),
        List.of(DatamartFacility.OtherService.OnlineScheduling),
        List.of(
            DatamartFacility.HealthService.Covid19Vaccine,
            DatamartFacility.HealthService.Cardiology),
        true);
  }

  private DatamartFacility datamartFacility(
      @NonNull List<DatamartFacility.BenefitsService> benefitsForServices,
      @NonNull List<DatamartFacility.HealthService> healthForServices,
      @NonNull List<DatamartFacility.OtherService> otherForServices,
      @NonNull List<DatamartFacility.HealthService> healthForDetailedServices,
      boolean isActive) {
    return DatamartFacility.builder()
        .id("vha_123GA")
        .type(DatamartFacility.Type.va_facilities)
        .attributes(
            DatamartFacility.FacilityAttributes.builder()
                .facilityType(DatamartFacility.FacilityType.va_health_facility)
                .address(
                    DatamartFacility.Addresses.builder()
                        .mailing(
                            DatamartFacility.Address.builder()
                                .address1("505 N John Rodes Blvd")
                                .city("Melbourne")
                                .state("FL")
                                .zip("32934")
                                .build())
                        .physical(
                            DatamartFacility.Address.builder()
                                .address1("505 N John Rodes Blvd")
                                .city("Melbourne")
                                .state("FL")
                                .zip("32934")
                                .build())
                        .build())
                .hours(
                    DatamartFacility.Hours.builder()
                        .sunday("Closed")
                        .monday("9AM-5PM")
                        .tuesday("9AM-5PM")
                        .wednesday("9AM-5PM")
                        .thursday("9AM-5PM")
                        .friday("9AM-5PM")
                        .saturday("Closed")
                        .build())
                .latitude(BigDecimal.valueOf(99.99))
                .longitude(BigDecimal.valueOf(123.45))
                .name("test_name")
                .phone(
                    DatamartFacility.Phone.builder()
                        .main("202-555-1212")
                        .healthConnect("202-555-1213")
                        .pharmacy("202-555-1213")
                        .patientAdvocate("202-555-1214")
                        .fax("202-555-1215")
                        .afterHours("202-555-1216")
                        .mentalHealthClinic("202-555-1217")
                        .enrollmentCoordinator("202-555-1218")
                        .build())
                .website("http://test.facilities.website.gov")
                .classification("test_classification")
                .timeZone("America/New_York")
                .mobile(false)
                .services(
                    DatamartFacility.Services.builder()
                        .benefits(benefitsForServices)
                        .other(otherForServices)
                        .health(healthForServices)
                        .lastUpdated(LocalDate.parse("2018-01-01"))
                        .build())
                .activeStatus(DatamartFacility.ActiveStatus.A)
                .visn("20")
                .satisfaction(
                    DatamartFacility.Satisfaction.builder()
                        .health(
                            DatamartFacility.PatientSatisfaction.builder()
                                .primaryCareRoutine(BigDecimal.valueOf(0.85))
                                .primaryCareUrgent(BigDecimal.valueOf(0.86))
                                .specialtyCareRoutine(BigDecimal.valueOf(0.87))
                                .specialtyCareUrgent(BigDecimal.valueOf(0.88))
                                .build())
                        .effectiveDate(LocalDate.parse("2018-02-01"))
                        .build())
                .waitTimes(
                    DatamartFacility.WaitTimes.builder()
                        .health(
                            List.of(
                                DatamartFacility.PatientWaitTime.builder()
                                    .service(DatamartFacility.HealthService.Cardiology)
                                    .establishedPatientWaitTime(BigDecimal.valueOf(5))
                                    .newPatientWaitTime(BigDecimal.valueOf(10))
                                    .build(),
                                DatamartFacility.PatientWaitTime.builder()
                                    .service(DatamartFacility.HealthService.Covid19Vaccine)
                                    .establishedPatientWaitTime(BigDecimal.valueOf(4))
                                    .newPatientWaitTime(BigDecimal.valueOf(9))
                                    .build()))
                        .effectiveDate(LocalDate.parse("2018-03-05"))
                        .build())
                .operatingStatus(
                    DatamartFacility.OperatingStatus.builder()
                        .code(DatamartFacility.OperatingStatusCode.NORMAL)
                        .additionalInfo("additional operating status info")
                        .supplementalStatuses(
                            List.of(
                                DatamartFacility.SupplementalStatus.builder()
                                    .id("COVID_HIGH")
                                    .label("This is a high Covid level")
                                    .build(),
                                DatamartFacility.SupplementalStatus.builder()
                                    .id("EBOLA_LOW")
                                    .label("This is a low Ebola level")
                                    .build()))
                        .build())
                .detailedServices(getHealthDetailedServices(healthForDetailedServices, isActive))
                .operationalHoursSpecialInstructions(
                    "Vet center 1 is available. | Vet center 2 is available. | Vet center 3 is available.")
                .build())
        .build();
  }

  @Test
  public void datamartFacilityRoundtrip() {
    DatamartFacility datamartFacility = datamartFacility();
    Facility facility = FacilityTransformerV1.toFacility(datamartFacility);
    assertThat(datamartFacility).hasFieldOrProperty("attributes.detailedServices");
    assertThatThrownBy(() -> assertThat(facility).hasFieldOrProperty("attributes.detailedServices"))
        .isInstanceOf(AssertionError.class);
    assertThat(datamartFacility).hasFieldOrProperty("attributes.waitTimes");
    assertThatThrownBy(() -> assertThat(facility).hasFieldOrProperty("attributes.waitTimes"))
        .isInstanceOf(AssertionError.class);
    assertThat(FacilityTransformerV1.toVersionAgnostic(facility))
        .usingRecursiveComparison()
        .ignoringFields("attributes.detailedServices", "attributes.waitTimes")
        .isEqualTo(datamartFacility);
  }

  private Facility facility() {
    return facility(
        List.of(
            Facility.BenefitsService.EducationClaimAssistance,
            Facility.BenefitsService.FamilyMemberClaimAssistance),
        List.of(
            Facility.HealthService.PrimaryCare,
            Facility.HealthService.UrgentCare,
            Facility.HealthService.EmergencyCare),
        List.of(Facility.OtherService.OnlineScheduling));
  }

  private Facility facility(
      @NonNull List<Facility.BenefitsService> benefitsForServices,
      @NonNull List<Facility.HealthService> healthForServices,
      @NonNull List<Facility.OtherService> otherForServices) {
    return Facility.builder()
        .id("vha_123GA")
        .type(Facility.Type.va_facilities)
        .attributes(
            Facility.FacilityAttributes.builder()
                .facilityType(Facility.FacilityType.va_health_facility)
                .address(
                    Facility.Addresses.builder()
                        .mailing(
                            Facility.Address.builder()
                                .address1("505 N John Rodes Blvd")
                                .city("Melbourne")
                                .state("FL")
                                .zip("32934")
                                .build())
                        .physical(
                            Facility.Address.builder()
                                .address1("505 N John Rodes Blvd")
                                .city("Melbourne")
                                .state("FL")
                                .zip("32934")
                                .build())
                        .build())
                .hours(
                    Facility.Hours.builder()
                        .sunday("Closed")
                        .monday("9AM-5PM")
                        .tuesday("9AM-5PM")
                        .wednesday("9AM-5PM")
                        .thursday("9AM-5PM")
                        .friday("9AM-5PM")
                        .saturday("Closed")
                        .build())
                .latitude(BigDecimal.valueOf(99.99))
                .longitude(BigDecimal.valueOf(123.45))
                .name("test_name")
                .phone(
                    Facility.Phone.builder()
                        .main("202-555-1212")
                        .healthConnect("202-555-1213")
                        .pharmacy("202-555-1213")
                        .patientAdvocate("202-555-1214")
                        .fax("202-555-1215")
                        .afterHours("202-555-1216")
                        .mentalHealthClinic("202-555-1217")
                        .enrollmentCoordinator("202-555-1218")
                        .build())
                .website("http://test.facilities.website.gov")
                .classification("test_classification")
                .timeZone("America/New_York")
                .mobile(false)
                .services(
                    Facility.Services.builder()
                        .benefits(benefitsForServices)
                        .other(otherForServices)
                        .health(healthForServices)
                        .lastUpdated(LocalDate.parse("2018-01-01"))
                        .build())
                .activeStatus(Facility.ActiveStatus.A)
                .visn("20")
                .satisfaction(
                    Facility.Satisfaction.builder()
                        .health(
                            Facility.PatientSatisfaction.builder()
                                .primaryCareRoutine(BigDecimal.valueOf(0.85))
                                .primaryCareUrgent(BigDecimal.valueOf(0.86))
                                .specialtyCareRoutine(BigDecimal.valueOf(0.87))
                                .specialtyCareUrgent(BigDecimal.valueOf(0.88))
                                .build())
                        .effectiveDate(LocalDate.parse("2018-02-01"))
                        .build())
                .operatingStatus(
                    Facility.OperatingStatus.builder()
                        .code(Facility.OperatingStatusCode.NORMAL)
                        .additionalInfo("additional operating status info")
                        .supplementalStatuses(
                            List.of(
                                Facility.SupplementalStatus.builder()
                                    .id("COVID_HIGH")
                                    .label("This is a high Covid level")
                                    .build(),
                                Facility.SupplementalStatus.builder()
                                    .id("EBOLA_LOW")
                                    .label("This is a low Ebola level")
                                    .build()))
                        .build())
                .operationalHoursSpecialInstructions(
                    new ArrayList<String>() {
                      {
                        add("Vet center 1 is available.");
                        add("Vet center 2 is available.");
                        add("Vet center 3 is available.");
                      }
                    })
                .build())
        .build();
  }

  @Test
  public void facilityRoundtrip() {
    Facility facility = facility();
    assertThat(FacilityTransformerV1.toFacility(FacilityTransformerV1.toVersionAgnostic(facility)))
        .usingRecursiveComparison()
        .isEqualTo(facility);
  }

  private DatamartDetailedService getHealthDetailedService(
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

  private List<DatamartDetailedService> getHealthDetailedServices(
      @NonNull List<DatamartFacility.HealthService> healthServices, boolean isActive) {
    return healthServices.stream()
        .map(
            hs -> {
              return getHealthDetailedService(hs, isActive);
            })
        .collect(Collectors.toList());
  }

  @Test
  @SneakyThrows
  void healthServiceRoundTripUsingServiceId() {
    for (String json :
        List.of(
            "\"audiology\"",
            "\"cardiology\"",
            "\"caregiverSupport\"",
            "\"covid19Vaccine\"",
            "\"dental\"",
            "\"dermatology\"",
            "\"emergencyCare\"",
            "\"gastroenterology\"",
            "\"gynecology\"",
            "\"mentalHealth\"",
            "\"ophthalmology\"",
            "\"optometry\"",
            "\"orthopedics\"",
            "\"nutrition\"",
            "\"podiatry\"",
            "\"primaryCare\"",
            "\"specialtyCare\"",
            "\"urgentCare\"",
            "\"urology\"",
            "\"womensHealth\"")) {
      // Convert to FAPI V1 Health Service
      gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService healthServiceV1 =
          convertToHealthServiceV1(json);
      // Convert to Datamart Health Service
      String jsonHealthService = convertToJson(healthServiceV1);
      DatamartFacility.HealthService datamartHealthService =
          convertToDatamartHealthService(jsonHealthService);
      // Convert to FAPI V0 Health Service
      jsonHealthService = convertToJson(datamartHealthService);
      gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService healthServiceV0 =
          convertToHealthServiceV0(jsonHealthService);
      // Convert back to FAPI V1 Health Service and compare beginning to end
      jsonHealthService = convertToJson(healthServiceV0);
      gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService healthService =
          convertToHealthServiceV1(jsonHealthService);
      assertThat(healthService).isEqualTo(healthServiceV1);
    }
  }

  @Test
  @SneakyThrows
  void healthServiceRoundTripUsingServiceName() {
    for (String json :
        List.of(
            "\"Audiology\"",
            "\"Cardiology\"",
            "\"CaregiverSupport\"",
            "\"Covid19Vaccine\"",
            "\"DentalServices\"",
            "\"Dermatology\"",
            "\"EmergencyCare\"",
            "\"Gastroenterology\"",
            "\"Gynecology\"",
            "\"MentalHealthCare\"",
            "\"Ophthalmology\"",
            "\"Optometry\"",
            "\"Orthopedics\"",
            "\"Nutrition\"",
            "\"Podiatry\"",
            "\"PrimaryCare\"",
            "\"SpecialtyCare\"",
            "\"UrgentCare\"",
            "\"Urology\"",
            "\"WomensHealth\"")) {
      // Convert to FAPI V1 Health Service
      gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService healthServiceV1 =
          convertToHealthServiceV1(json);
      // Convert to Datamart Health Service
      String jsonHealthService = convertToJson(healthServiceV1);
      DatamartFacility.HealthService datamartHealthService =
          convertToDatamartHealthService(jsonHealthService);
      // Convert to FAPI V0 Health Service
      jsonHealthService = convertToJson(datamartHealthService);
      gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService healthServiceV0 =
          convertToHealthServiceV0(jsonHealthService);
      // Convert back to FAPI V1 Health Service and compare beginning to end
      jsonHealthService = convertToJson(healthServiceV0);
      gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService healthService =
          convertToHealthServiceV1(jsonHealthService);
      assertThat(healthService).isEqualTo(healthServiceV1);
    }
  }

  @Test
  public void losslessFacilityVisitorRoundtrip() {
    Facility facility = facility();
    assertThat(
            FacilityTransformerV1.toFacility(
                FacilityTransformerV0.toVersionAgnostic(
                    FacilityTransformerV0.toFacility(
                        FacilityTransformerV1.toVersionAgnostic(facility)))))
        .usingRecursiveComparison()
        .isEqualTo(facility);
  }

  @Test
  public void nonLosslessFacilityVisitorRoundtrip() {
    Facility facilityWithWholeHealth =
        facility(
            emptyList(),
            List.of(
                Facility.HealthService.PrimaryCare,
                Facility.HealthService.UrgentCare,
                Facility.HealthService.EmergencyCare,
                Facility.HealthService.MentalHealth,
                Facility.HealthService.Dental,
                Facility.HealthService.WholeHealth),
            emptyList());
    Facility facilityWithoutWholeHealth =
        facility(
            emptyList(),
            List.of(
                Facility.HealthService.PrimaryCare,
                Facility.HealthService.UrgentCare,
                Facility.HealthService.EmergencyCare,
                Facility.HealthService.MentalHealth,
                Facility.HealthService.Dental),
            emptyList());
    assertThat(
            FacilityTransformerV1.toFacility(
                FacilityTransformerV0.toVersionAgnostic(
                    FacilityTransformerV0.toFacility(
                        FacilityTransformerV1.toVersionAgnostic(facilityWithWholeHealth)))))
        .usingRecursiveComparison()
        .isEqualTo(facilityWithoutWholeHealth);
  }

  @Test
  @SneakyThrows
  public void nullArgs() {
    assertThrows(NullPointerException.class, () -> FacilityTransformerV1.toFacility(null));
    assertThrows(NullPointerException.class, () -> FacilityTransformerV1.toVersionAgnostic(null));
    final Method transformDatmartFacilityBenefitsServiceMethod =
        FacilityTransformerV1.class.getDeclaredMethod(
            "transformFacilityBenefitsService", DatamartFacility.BenefitsService.class);
    transformDatmartFacilityBenefitsServiceMethod.setAccessible(true);
    DatamartFacility.BenefitsService nullBenefits = null;
    assertThatThrownBy(
            () -> transformDatmartFacilityBenefitsServiceMethod.invoke(null, nullBenefits))
        .isInstanceOf(InvocationTargetException.class)
        .hasCause(
            new NullPointerException(
                "datamartFacilityBenefitsService is marked non-null but is null"));
    final Method transformFacilityBenefitsServiceMethod =
        FacilityTransformerV1.class.getDeclaredMethod(
            "transformFacilityBenefitsService", Facility.BenefitsService.class);
    transformFacilityBenefitsServiceMethod.setAccessible(true);
    Facility.BenefitsService nullBenefitsV1 = null;
    assertThatThrownBy(() -> transformFacilityBenefitsServiceMethod.invoke(null, nullBenefitsV1))
        .isInstanceOf(InvocationTargetException.class)
        .hasCause(
            new NullPointerException("facilityBenefitsService is marked non-null but is null"));
    final Method transformDatmartFacilityHealthServiceMethod =
        FacilityTransformerV1.class.getDeclaredMethod(
            "transformFacilityHealthService", DatamartFacility.HealthService.class);
    transformDatmartFacilityHealthServiceMethod.setAccessible(true);
    DatamartFacility.HealthService nullHealth = null;
    assertThatThrownBy(() -> transformDatmartFacilityHealthServiceMethod.invoke(null, nullHealth))
        .isInstanceOf(InvocationTargetException.class)
        .hasCause(
            new NullPointerException(
                "datamartFacilityHealthService is marked non-null but is null"));
    final Method transformFacilityHealthServiceMethod =
        FacilityTransformerV1.class.getDeclaredMethod(
            "transformFacilityHealthService", Facility.HealthService.class);
    transformFacilityHealthServiceMethod.setAccessible(true);
    Facility.HealthService nullHealthV1 = null;
    assertThatThrownBy(() -> transformFacilityHealthServiceMethod.invoke(null, nullHealthV1))
        .isInstanceOf(InvocationTargetException.class)
        .hasCause(new NullPointerException("facilityHealthService is marked non-null but is null"));
    final Method transformDatmartFacilityServicesMethod =
        FacilityTransformerV1.class.getDeclaredMethod(
            "transformFacilityServices", DatamartFacility.Services.class);
    transformDatmartFacilityServicesMethod.setAccessible(true);
    DatamartFacility.Services nullServices = null;
    assertThat(transformDatmartFacilityServicesMethod.invoke(null, nullServices))
        .usingRecursiveComparison()
        .isEqualTo(Facility.Services.builder().build());
    final Method transformFacilityServicesMethod =
        FacilityTransformerV1.class.getDeclaredMethod(
            "transformFacilityServices", Facility.Services.class);
    transformFacilityServicesMethod.setAccessible(true);
    Facility.Services nullServicesV1 = null;
    assertThat(transformFacilityServicesMethod.invoke(null, nullServicesV1))
        .usingRecursiveComparison()
        .isEqualTo(DatamartFacility.Services.builder().build());
    final Method transformDatmartFacilitySatisfactionMethod =
        FacilityTransformerV1.class.getDeclaredMethod(
            "transformFacilitySatisfaction", DatamartFacility.Satisfaction.class);
    transformDatmartFacilitySatisfactionMethod.setAccessible(true);
    DatamartFacility.Satisfaction nullSatisfaction = null;
    assertThat(transformDatmartFacilitySatisfactionMethod.invoke(null, nullSatisfaction))
        .usingRecursiveComparison()
        .isEqualTo(Facility.Satisfaction.builder().build());
    final Method transformFacilitySatisfactionMethod =
        FacilityTransformerV1.class.getDeclaredMethod(
            "transformFacilitySatisfaction", Facility.Satisfaction.class);
    transformFacilitySatisfactionMethod.setAccessible(true);
    Facility.Satisfaction nullSatisfactionV1 = null;
    assertThat(transformFacilitySatisfactionMethod.invoke(null, nullSatisfactionV1))
        .usingRecursiveComparison()
        .isEqualTo(DatamartFacility.Satisfaction.builder().build());
    final Method transformDatmartFacilityPhoneMethod =
        FacilityTransformerV1.class.getDeclaredMethod(
            "transformFacilityPhone", DatamartFacility.Phone.class);
    transformDatmartFacilityPhoneMethod.setAccessible(true);
    DatamartFacility.Satisfaction nullPhone = null;
    assertThat(transformDatmartFacilityPhoneMethod.invoke(null, nullPhone))
        .usingRecursiveComparison()
        .isEqualTo(Facility.Phone.builder().build());
    final Method transformFacilityPhoneMethod =
        FacilityTransformerV1.class.getDeclaredMethod(
            "transformFacilityPhone", Facility.Phone.class);
    transformFacilityPhoneMethod.setAccessible(true);
    Facility.Satisfaction nullPhoneV1 = null;
    assertThat(transformFacilityPhoneMethod.invoke(null, nullPhoneV1))
        .usingRecursiveComparison()
        .isEqualTo(DatamartFacility.Phone.builder().build());
    final Method transformDatmartFacilityHoursMethod =
        FacilityTransformerV1.class.getDeclaredMethod(
            "transformFacilityHours", DatamartFacility.Hours.class);
    transformDatmartFacilityHoursMethod.setAccessible(true);
    DatamartFacility.Hours nullHours = null;
    assertThat(transformDatmartFacilityHoursMethod.invoke(null, nullHours))
        .usingRecursiveComparison()
        .isEqualTo(Facility.Hours.builder().build());
    final Method transformFacilityHoursMethod =
        FacilityTransformerV1.class.getDeclaredMethod(
            "transformFacilityHours", Facility.Hours.class);
    transformFacilityHoursMethod.setAccessible(true);
    Facility.Hours nullHoursV1 = null;
    assertThat(transformFacilityHoursMethod.invoke(null, nullHoursV1))
        .usingRecursiveComparison()
        .isEqualTo(DatamartFacility.Hours.builder().build());
    final Method transformDatmartFacilityAddressMethod =
        FacilityTransformerV1.class.getDeclaredMethod(
            "transformFacilityAddress", DatamartFacility.Address.class);
    transformDatmartFacilityAddressMethod.setAccessible(true);
    DatamartFacility.Address nullAddress = null;
    assertThat(transformDatmartFacilityAddressMethod.invoke(null, nullAddress))
        .usingRecursiveComparison()
        .isEqualTo(Facility.Address.builder().build());
    final Method transformFacilityAddressMethod =
        FacilityTransformerV1.class.getDeclaredMethod(
            "transformFacilityAddress", Facility.Address.class);
    transformFacilityAddressMethod.setAccessible(true);
    Facility.Addresses nullAddressV1 = null;
    assertThat(transformFacilityAddressMethod.invoke(null, nullAddressV1))
        .usingRecursiveComparison()
        .isEqualTo(DatamartFacility.Address.builder().build());
    final Method transformDatmartFacilityAddressesMethod =
        FacilityTransformerV1.class.getDeclaredMethod(
            "transformFacilityAddresses", DatamartFacility.Addresses.class);
    transformDatmartFacilityAddressesMethod.setAccessible(true);
    DatamartFacility.Addresses nullAddresses = null;
    assertThat(transformDatmartFacilityAddressesMethod.invoke(null, nullAddresses))
        .usingRecursiveComparison()
        .isEqualTo(Facility.Addresses.builder().build());
    final Method transformFacilityAddressesMethod =
        FacilityTransformerV1.class.getDeclaredMethod(
            "transformFacilityAddresses", Facility.Addresses.class);
    transformFacilityAddressesMethod.setAccessible(true);
    Facility.Addresses nullAddressesV1 = null;
    assertThat(transformFacilityAddressesMethod.invoke(null, nullAddressesV1))
        .usingRecursiveComparison()
        .isEqualTo(DatamartFacility.Addresses.builder().build());
  }

  @Test
  void toFacilityOperatingStatus() {
    DatamartFacility.OperatingStatus os =
        DatamartFacility.OperatingStatus.builder().code(null).build();
    Facility.OperatingStatus actual = FacilityTransformerV1.toFacilityOperatingStatus(os);
    assertThat(actual.code()).isNull();
    actual = FacilityTransformerV1.toFacilityOperatingStatus(null);
    assertThat(actual).isNull();
  }

  @Test
  void toVersionAgnosticFacilityOperatingStatus() {
    Facility.OperatingStatus os = Facility.OperatingStatus.builder().code(null).build();
    DatamartFacility.OperatingStatus actual =
        FacilityTransformerV1.toVersionAgnosticFacilityOperatingStatus(os);
    assertThat(actual.code()).isNull();
    actual = FacilityTransformerV1.toVersionAgnosticFacilityOperatingStatus(null);
    assertThat(actual).isNull();
  }

  @Test
  void toVersionAgnosticFacilityOperationalHoursSpecialInstructions() {
    assertThat(
            FacilityTransformerV1.toVersionAgnosticFacilityOperationalHoursSpecialInstructions(
                null))
        .isNull();
  }

  @Test
  public void transformDatamartFacility() {
    Facility expected = facility();
    DatamartFacility datamartFacility = datamartFacility();
    assertThat(FacilityTransformerV1.toFacility(datamartFacility))
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  void transformDatamartFacilitySatisfaction() {
    final Method transformFacilitySatisfactionMethod =
        FacilityTransformerV1.class.getDeclaredMethod(
            "transformFacilitySatisfaction", DatamartFacility.Satisfaction.class);
    transformFacilitySatisfactionMethod.setAccessible(true);
    Facility.Satisfaction actual =
        (Facility.Satisfaction)
            transformFacilitySatisfactionMethod.invoke(
                null,
                DatamartFacility.Satisfaction.builder()
                    .health(
                        DatamartFacility.PatientSatisfaction.builder()
                            .primaryCareUrgent(BigDecimal.valueOf(1.5))
                            .primaryCareUrgent(BigDecimal.valueOf(1.6))
                            .specialtyCareUrgent(BigDecimal.valueOf(2.3))
                            .specialtyCareRoutine(BigDecimal.valueOf(3.6))
                            .build())
                    .build());
    DatamartFacility.Satisfaction expected =
        DatamartFacility.Satisfaction.builder()
            .health(
                DatamartFacility.PatientSatisfaction.builder()
                    .primaryCareUrgent(BigDecimal.valueOf(1.5))
                    .primaryCareUrgent(BigDecimal.valueOf(1.6))
                    .specialtyCareUrgent(BigDecimal.valueOf(2.3))
                    .specialtyCareRoutine(BigDecimal.valueOf(3.6))
                    .build())
            .build();
    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    actual =
        (Facility.Satisfaction)
            transformFacilitySatisfactionMethod.invoke(
                null, DatamartFacility.Satisfaction.builder().health(null).build());
    assertThat(actual.health()).isNull();
  }

  @Test
  @SneakyThrows
  void transformDatamartFacilityServices() {
    final Method transformFacilityServicesMethod =
        FacilityTransformerV1.class.getDeclaredMethod(
            "transformFacilityServices", DatamartFacility.Services.class);
    transformFacilityServicesMethod.setAccessible(true);
    DatamartFacility.Services ds =
        DatamartFacility.Services.builder().health(null).benefits(null).other(null).build();
    Facility.Services actual = (Facility.Services) transformFacilityServicesMethod.invoke(null, ds);
    assertThat(actual.health()).isNull();
    assertThat(actual.benefits()).isNull();
    assertThat(actual.other()).isNull();
  }

  @Test
  public void transformEmptyFacility() {
    Facility facility = Facility.builder().build();
    DatamartFacility datamartFacility = DatamartFacility.builder().build();
    assertThat(FacilityTransformerV1.toVersionAgnostic(facility))
        .usingRecursiveComparison()
        .isEqualTo(datamartFacility);
    assertThat(FacilityTransformerV1.toFacility(datamartFacility))
        .usingRecursiveComparison()
        .isEqualTo(facility);
  }

  @Test
  public void transformFacility() {
    DatamartFacility expected = datamartFacility();
    Facility facility = facility();
    assertThat(expected).hasFieldOrProperty("attributes.detailedServices");
    assertThatThrownBy(() -> assertThat(facility).hasFieldOrProperty("attributes.detailedServices"))
        .isInstanceOf(AssertionError.class);
    assertThat(expected).hasFieldOrProperty("attributes.waitTimes");
    assertThatThrownBy(() -> assertThat(facility).hasFieldOrProperty("attributes.waitTimes"))
        .isInstanceOf(AssertionError.class);
    assertThat(FacilityTransformerV1.toVersionAgnostic(facility))
        .usingRecursiveComparison()
        .ignoringFields("attributes.detailedServices", "attributes.waitTimes")
        .isEqualTo(expected);
  }

  @Test
  public void transformFacilityOperatingStatus() {
    assertThat(
            FacilityTransformerV1.toFacilityOperatingStatus(
                DatamartFacility.OperatingStatus.builder()
                    .code(DatamartFacility.OperatingStatusCode.NORMAL)
                    .additionalInfo("additional info")
                    .build(),
                DatamartFacility.ActiveStatus.A))
        .usingRecursiveComparison()
        .isEqualTo(
            Facility.OperatingStatus.builder()
                .code(Facility.OperatingStatusCode.NORMAL)
                .additionalInfo("additional info")
                .build());
    assertThat(
            FacilityTransformerV1.toFacilityOperatingStatus(
                DatamartFacility.OperatingStatus.builder()
                    .code(DatamartFacility.OperatingStatusCode.NORMAL)
                    .additionalInfo("additional info")
                    .build(),
                null))
        .usingRecursiveComparison()
        .isEqualTo(
            Facility.OperatingStatus.builder()
                .code(Facility.OperatingStatusCode.NORMAL)
                .additionalInfo("additional info")
                .build());
    assertThat(
            FacilityTransformerV1.toFacilityOperatingStatus(null, DatamartFacility.ActiveStatus.A))
        .usingRecursiveComparison()
        .isEqualTo(
            Facility.OperatingStatus.builder().code(Facility.OperatingStatusCode.NORMAL).build());
  }

  @Test
  @SneakyThrows
  void transformFacilityPatientWaitTime() {
    final Method transformFacilityPatientWaitTimeMethod =
        FacilityTransformerV1.class.getDeclaredMethod(
            "transformFacilityPatientWaitTime", Facility.PatientWaitTime.class);
    transformFacilityPatientWaitTimeMethod.setAccessible(true);
    DatamartFacility.PatientWaitTime actual =
        (DatamartFacility.PatientWaitTime)
            transformFacilityPatientWaitTimeMethod.invoke(
                null,
                Facility.PatientWaitTime.builder()
                    .service(Facility.HealthService.Cardiology)
                    .build());
    DatamartFacility.PatientWaitTime expected =
        DatamartFacility.PatientWaitTime.builder()
            .service(DatamartFacility.HealthService.Cardiology)
            .build();
    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    actual =
        (DatamartFacility.PatientWaitTime)
            transformFacilityPatientWaitTimeMethod.invoke(
                null, Facility.PatientWaitTime.builder().service(null).build());
    expected = DatamartFacility.PatientWaitTime.builder().service(null).build();
    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  void transformFacilitySatisfaction() {
    final Method transformFacilitySatisfactionMethod =
        FacilityTransformerV1.class.getDeclaredMethod(
            "transformFacilitySatisfaction", Facility.Satisfaction.class);
    transformFacilitySatisfactionMethod.setAccessible(true);
    DatamartFacility.Satisfaction actual =
        (DatamartFacility.Satisfaction)
            transformFacilitySatisfactionMethod.invoke(
                null,
                Facility.Satisfaction.builder()
                    .health(
                        Facility.PatientSatisfaction.builder()
                            .primaryCareUrgent(BigDecimal.valueOf(1.5))
                            .primaryCareUrgent(BigDecimal.valueOf(1.6))
                            .specialtyCareUrgent(BigDecimal.valueOf(2.3))
                            .specialtyCareRoutine(BigDecimal.valueOf(3.6))
                            .build())
                    .build());
    DatamartFacility.Satisfaction expected =
        DatamartFacility.Satisfaction.builder()
            .health(
                DatamartFacility.PatientSatisfaction.builder()
                    .primaryCareUrgent(BigDecimal.valueOf(1.5))
                    .primaryCareUrgent(BigDecimal.valueOf(1.6))
                    .specialtyCareUrgent(BigDecimal.valueOf(2.3))
                    .specialtyCareRoutine(BigDecimal.valueOf(3.6))
                    .build())
            .build();
    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    actual =
        (DatamartFacility.Satisfaction)
            transformFacilitySatisfactionMethod.invoke(
                null, Facility.Satisfaction.builder().health(null).build());
    expected = DatamartFacility.Satisfaction.builder().health(null).build();
    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  void transformFacilityServices() {
    final Method transformFacilityServicesMethod =
        FacilityTransformerV1.class.getDeclaredMethod(
            "transformFacilityServices", Facility.Services.class);
    transformFacilityServicesMethod.setAccessible(true);
    Facility.Services fs =
        Facility.Services.builder().health(null).benefits(null).other(null).build();
    DatamartFacility.Services actual =
        (DatamartFacility.Services) transformFacilityServicesMethod.invoke(null, fs);
    assertThat(actual.health()).isNull();
    assertThat(actual.benefits()).isNull();
    assertThat(actual.other()).isNull();
  }

  @Test
  public void transformFacilityWithEmptyAttributes() {
    Facility facility =
        Facility.builder().id("vha_123GA").type(Facility.Type.va_facilities).build();
    DatamartFacility datamartFacility =
        DatamartFacility.builder()
            .id("vha_123GA")
            .type(DatamartFacility.Type.va_facilities)
            .build();
    assertThat(FacilityTransformerV1.toVersionAgnostic(facility))
        .usingRecursiveComparison()
        .isEqualTo(datamartFacility);
    assertThat(FacilityTransformerV1.toFacility(datamartFacility))
        .usingRecursiveComparison()
        .isEqualTo(facility);
  }

  @Test
  void transformOperationalHoursSpecialInstructions() {
    assertThat(FacilityTransformerV1.transformOperationalHoursSpecialInstructions(null)).isNull();
  }
}
