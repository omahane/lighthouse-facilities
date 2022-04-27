package gov.va.api.lighthouse.facilities.api.v0;

import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import gov.va.api.lighthouse.facilities.api.ServiceType;
import gov.va.api.lighthouse.facilities.api.v0.Facility.BenefitsService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OtherService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class ServiceTypeTest {
  private static final int ONE = 1;

  @Test
  @SneakyThrows
  void benefitsFromString() {
    List<gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService> benefitsV1 =
        List.of(
            gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService.ApplyingForBenefits,
            gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService.BurialClaimAssistance,
            gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService
                .DisabilityClaimAssistance,
            gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService
                .eBenefitsRegistrationAssistance,
            gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService
                .EducationAndCareerCounseling,
            gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService
                .EducationClaimAssistance,
            gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService
                .FamilyMemberClaimAssistance,
            gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService.HomelessAssistance,
            gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService
                .InsuranceClaimAssistanceAndFinancialCounseling,
            gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService
                .IntegratedDisabilityEvaluationSystemAssistance,
            gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService.Pensions,
            gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService
                .PreDischargeClaimAssistance,
            gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService.TransitionAssistance,
            gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService
                .UpdatingDirectDepositInformation,
            gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService.VAHomeLoanAssistance,
            gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService
                .VocationalRehabilitationAndEmploymentAssistance);
    List<BenefitsService> benefitsV0 =
        List.of(
            BenefitsService.ApplyingForBenefits,
            BenefitsService.BurialClaimAssistance,
            BenefitsService.DisabilityClaimAssistance,
            BenefitsService.eBenefitsRegistrationAssistance,
            BenefitsService.EducationAndCareerCounseling,
            BenefitsService.EducationClaimAssistance,
            BenefitsService.FamilyMemberClaimAssistance,
            BenefitsService.HomelessAssistance,
            BenefitsService.InsuranceClaimAssistanceAndFinancialCounseling,
            BenefitsService.IntegratedDisabilityEvaluationSystemAssistance,
            BenefitsService.Pensions,
            BenefitsService.PreDischargeClaimAssistance,
            BenefitsService.TransitionAssistance,
            BenefitsService.UpdatingDirectDepositInformation,
            BenefitsService.VAHomeLoanAssistance,
            BenefitsService.VocationalRehabilitationAndEmploymentAssistance);
    benefitsV1.parallelStream()
        .forEach(
            bs -> assertThat(benefitsV0.contains(BenefitsService.fromString(bs.name()))).isTrue());
    benefitsV1.parallelStream()
        .forEach(
            bs ->
                assertThat(benefitsV0.contains(BenefitsService.fromString(uncapitalize(bs.name()))))
                    .isTrue());
    assertThatThrownBy(() -> BenefitsService.fromString("No Such Name"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "No enum constant gov.va.api.lighthouse.facilities.api.v0.Facility.BenefitsService.No Such Name");
  }

  private int countOfServiceTypesWithMatchingServiceId(
      @NonNull List<ServiceType> serviceTypes, @NonNull String serviceId) {
    int count = 0;
    for (final ServiceType st : serviceTypes) {
      if (st.serviceId().equals(serviceId)) {
        count++;
      }
    }
    ;
    return count;
  }

  @Test
  @SneakyThrows
  void healthFromString() {
    List<gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService> healthV1 =
        List.of(
            gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Audiology,
            gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Cardiology,
            gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.CaregiverSupport,
            gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Covid19Vaccine,
            gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Dental,
            gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Dermatology,
            gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.EmergencyCare,
            gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Gastroenterology,
            gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Gynecology,
            gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealth,
            gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Ophthalmology,
            gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Optometry,
            gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Orthopedics,
            gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Nutrition,
            gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Podiatry,
            gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.PrimaryCare,
            gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.SpecialtyCare,
            gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.UrgentCare,
            gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Urology,
            gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.WomensHealth);
    List<HealthService> healthV0 =
        List.of(
            HealthService.Audiology,
            HealthService.Cardiology,
            HealthService.CaregiverSupport,
            HealthService.Covid19Vaccine,
            HealthService.DentalServices,
            HealthService.Dermatology,
            HealthService.EmergencyCare,
            HealthService.Gastroenterology,
            HealthService.Gynecology,
            HealthService.MentalHealthCare,
            HealthService.Ophthalmology,
            HealthService.Optometry,
            HealthService.Orthopedics,
            HealthService.Nutrition,
            HealthService.Podiatry,
            HealthService.PrimaryCare,
            HealthService.SpecialtyCare,
            HealthService.UrgentCare,
            HealthService.Urology,
            HealthService.WomensHealth);
    healthV1.parallelStream()
        .forEach(hs -> assertThat(healthV0.contains(HealthService.fromString(hs.name()))).isTrue());
    healthV1.parallelStream()
        .forEach(
            hs ->
                assertThat(healthV0.contains(HealthService.fromString(uncapitalize(hs.name()))))
                    .isTrue());
    assertThat(HealthService.fromString("COVID-19 vaccines"))
        .isEqualTo(HealthService.Covid19Vaccine);
    assertThatThrownBy(() -> HealthService.fromString("No Such Name"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "No enum constant gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.No Such Name");
  }

  @Test
  @SneakyThrows
  void isRecognizedServiceId() {
    Arrays.stream(BenefitsService.values())
        .parallel()
        .forEach(bs -> assertThat(BenefitsService.isRecognizedServiceId(bs.serviceId())).isTrue());
    Arrays.stream(HealthService.values())
        .parallel()
        .forEach(hs -> assertThat(HealthService.isRecognizedServiceId(hs.serviceId())).isTrue());
    Arrays.stream(OtherService.values())
        .parallel()
        .forEach(os -> assertThat(OtherService.isRecognizedServiceId(os.serviceId())).isTrue());
    assertThat(BenefitsService.isRecognizedServiceId("noSuchId")).isFalse();
    assertThat(HealthService.isRecognizedServiceId("noSuchId")).isFalse();
    assertThat(OtherService.isRecognizedServiceId("noSuchId")).isFalse();
    assertThat(BenefitsService.isRecognizedServiceId("INVALID_ID")).isFalse();
    assertThat(HealthService.isRecognizedServiceId("INVALID_ID")).isFalse();
    assertThat(OtherService.isRecognizedServiceId("INVALID_ID")).isFalse();
  }

  @Test
  @SneakyThrows
  void isRecognizedServiceName() {
    Arrays.stream(BenefitsService.values())
        .parallel()
        .forEach(bs -> assertThat(BenefitsService.isRecognizedServiceName(bs.name())).isTrue());
    Arrays.stream(HealthService.values())
        .parallel()
        .forEach(hs -> assertThat(HealthService.isRecognizedServiceName(hs.name())).isTrue());
    Arrays.stream(HealthService.values())
        .parallel()
        .forEach(
            hs ->
                assertThat(HealthService.isRecognizedServiceName(uncapitalize(hs.name())))
                    .isTrue());
    Arrays.stream(OtherService.values())
        .parallel()
        .forEach(os -> assertThat(OtherService.isRecognizedServiceName(os.name())).isTrue());
    assertThat(HealthService.isRecognizedCovid19ServiceName("COVID-19 vaccines")).isTrue();
    assertThat(
            HealthService.isRecognizedCovid19ServiceName(
                uncapitalize(HealthService.Covid19Vaccine.name())))
        .isTrue();
    assertThat(BenefitsService.isRecognizedServiceName("No Such Name")).isFalse();
    assertThat(HealthService.isRecognizedServiceName("No Such Name")).isFalse();
    assertThat(OtherService.isRecognizedServiceName("No Such Name")).isFalse();
    assertThat(HealthService.isRecognizedCovid19ServiceName("No Such Name")).isFalse();
  }

  @Test
  @SneakyThrows
  void otherValueOf() {
    List<gov.va.api.lighthouse.facilities.api.v1.Facility.OtherService> otherV1 =
        List.of(gov.va.api.lighthouse.facilities.api.v1.Facility.OtherService.OnlineScheduling);
    List<OtherService> otherV0 = List.of(OtherService.OnlineScheduling);
    otherV1.parallelStream()
        .forEach(os -> assertThat(otherV0.contains(OtherService.valueOf(os.name()))).isTrue());
    otherV1.parallelStream()
        .forEach(
            os ->
                assertThatThrownBy(() -> OtherService.valueOf(uncapitalize(os.name())))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(
                        "No enum constant gov.va.api.lighthouse.facilities.api.v0.Facility.OtherService.onlineScheduling"));
    assertThatThrownBy(() -> OtherService.valueOf("NoSuchName"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "No enum constant gov.va.api.lighthouse.facilities.api.v0.Facility.OtherService.NoSuchName");
  }

  @Test
  @SneakyThrows
  void uniqueServiceIds() {
    final ArrayList<ServiceType> allServiceTypes = new ArrayList<>();
    allServiceTypes.addAll(List.of(BenefitsService.values()));
    allServiceTypes.addAll(List.of(HealthService.values()));
    allServiceTypes.addAll(List.of(OtherService.values()));

    /* Confirm uniqueness of service type value across all service types. */
    Arrays.stream(BenefitsService.values())
        .parallel()
        .forEach(
            bs -> {
              assertThat(countOfServiceTypesWithMatchingServiceId(allServiceTypes, bs.serviceId()))
                  .isEqualTo(ONE);
            });
    Arrays.stream(HealthService.values())
        .parallel()
        .forEach(
            hs -> {
              assertThat(countOfServiceTypesWithMatchingServiceId(allServiceTypes, hs.serviceId()))
                  .isEqualTo(ONE);
            });
    Arrays.stream(OtherService.values())
        .parallel()
        .forEach(
            os -> {
              assertThat(countOfServiceTypesWithMatchingServiceId(allServiceTypes, os.serviceId()))
                  .isEqualTo(ONE);
            });
  }
}
