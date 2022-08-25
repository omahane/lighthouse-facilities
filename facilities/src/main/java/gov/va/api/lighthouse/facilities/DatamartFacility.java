package gov.va.api.lighthouse.facilities;

import static org.apache.commons.lang3.StringUtils.capitalize;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import gov.va.api.lighthouse.facilities.api.ServiceType;
import gov.va.api.lighthouse.facilities.deserializers.DatamartAddressDeserializer;
import gov.va.api.lighthouse.facilities.deserializers.DatamartFacilityAttributesDeserializer;
import gov.va.api.lighthouse.facilities.deserializers.DatamartHoursDeserializer;
import gov.va.api.lighthouse.facilities.deserializers.DatamartOperatingStatusDeserializer;
import gov.va.api.lighthouse.facilities.deserializers.DatamartPatientSatisfactionDeserializer;
import gov.va.api.lighthouse.facilities.deserializers.DatamartPhoneDeserializer;
import gov.va.api.lighthouse.facilities.deserializers.DatamartSatisfactionDeserializer;
import gov.va.api.lighthouse.facilities.deserializers.DatamartServicesDeserializer;
import gov.va.api.lighthouse.facilities.deserializers.DatamartWaitTimesDeserializer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DatamartFacility {
  @NotNull String id;

  @NotNull Type type;

  @Valid @NotNull FacilityAttributes attributes;

  public enum ActiveStatus {
    A,
    T
  }

  public enum BenefitsService implements ServiceType {
    ApplyingForBenefits,
    BurialClaimAssistance,
    DisabilityClaimAssistance,
    eBenefitsRegistrationAssistance,
    EducationAndCareerCounseling,
    EducationClaimAssistance,
    FamilyMemberClaimAssistance,
    HomelessAssistance,
    InsuranceClaimAssistanceAndFinancialCounseling,
    IntegratedDisabilityEvaluationSystemAssistance,
    Pensions,
    PreDischargeClaimAssistance,
    TransitionAssistance,
    UpdatingDirectDepositInformation,
    VAHomeLoanAssistance,
    VocationalRehabilitationAndEmploymentAssistance
  }

  public enum FacilityType {
    va_benefits_facility,
    va_cemetery,
    va_health_facility,
    vet_center
  }

  public enum HealthService implements ServiceType {
    @JsonProperty("adaptiveSports")
    AdaptiveSports,
    @JsonProperty("addiction")
    Addiction,
    @JsonProperty("adviceNurse")
    AdviceNurse,
    @JsonProperty("allergy")
    Allergy,
    @JsonProperty("amputation")
    Amputation,
    @JsonProperty("anesthesia")
    Anesthesia,
    @JsonProperty("audiology")
    Audiology,
    @JsonProperty("bariatricSurgery")
    BariatricSurgery,
    @JsonProperty("billing")
    Billing,
    @JsonProperty("vision")
    Vision,
    @JsonProperty("cancer")
    Cancer,
    @JsonProperty("cardiology")
    Cardiology,
    @JsonProperty("cardiovascularSurgery")
    CardiovascularSurgery,
    @JsonProperty("caregiverSupport")
    CaregiverSupport,
    @JsonProperty("cashier")
    Cashier,
    @JsonProperty("chiropractic")
    Chiropractic,
    @JsonProperty("colonSurgery")
    ColonSurgery,
    @JsonProperty("communityEngagement")
    CommunityEngagement,
    @JsonProperty("complementaryHealth")
    ComplementaryHealth,
    @JsonProperty("familyCounseling")
    FamilyCounseling,
    @JsonProperty("covid19Vaccine")
    Covid19Vaccine,
    @JsonProperty("criticalCare")
    CriticalCare,
    @JsonProperty("dental")
    Dental,
    // DentalServices is a V0 holdover
    DentalServices,
    @JsonProperty("dermatology")
    Dermatology,
    @JsonProperty("diabetic")
    Diabetic,
    @JsonProperty("emergencyCare")
    EmergencyCare,
    @JsonProperty("endocrinology")
    Endocrinology,
    @JsonProperty("gastroenterology")
    Gastroenterology,
    @JsonProperty("genomicMedicine")
    GenomicMedicine,
    @JsonProperty("geriatrics")
    Geriatrics,
    @JsonProperty("griefCounseling")
    GriefCounseling,
    @JsonProperty("gynecology")
    Gynecology,
    @JsonProperty("hematology")
    Hematology,
    @JsonProperty("hiv")
    Hiv,
    @JsonProperty("homeless")
    Homeless,
    @JsonProperty("hospitalMedicine")
    HospitalMedicine,
    @JsonProperty("infectiousDisease")
    InfectiousDisease,
    @JsonProperty("internalMedicine")
    InternalMedicine,
    @JsonProperty("domesticAbuseSupport")
    DomesticAbuseSupport,
    @JsonProperty("laboratory")
    Laboratory,
    @JsonProperty("lgbtq")
    Lgbtq,
    @JsonProperty("medicalRecords")
    MedicalRecords,
    @JsonProperty("mentalHealth")
    MentalHealth,
    // MentalHealthCare is a V0 holdover
    MentalHealthCare,
    @JsonProperty("militarySexualTrauma")
    MilitarySexualTrauma,
    @JsonProperty("minorityCare")
    MinorityCare,
    @JsonProperty("weightManagement")
    WeightManagement,
    @JsonProperty("myHealtheVetCoordinator")
    MyHealtheVetCoordinator,
    @JsonProperty("nephrology")
    Nephrology,
    @JsonProperty("neurology")
    Neurology,
    @JsonProperty("neurosurgery")
    Neurosurgery,
    @JsonProperty("nutrition")
    Nutrition,
    @JsonProperty("ophthalmology")
    Ophthalmology,
    @JsonProperty("optometry")
    Optometry,
    @JsonProperty("orthopedics")
    Orthopedics,
    @JsonProperty("otolaryngology")
    Otolaryngology,
    @JsonProperty("outpatientSurgery")
    OutpatientSurgery,
    @JsonProperty("painManagement")
    PainManagement,
    @JsonProperty("hospice")
    Hospice,
    @JsonProperty("patientAdvocates")
    PatientAdvocates,
    @JsonProperty("pharmacy")
    Pharmacy,
    @JsonProperty("physicalMedicine")
    PhysicalMedicine,
    @JsonProperty("physicalTherapy")
    PhysicalTherapy,
    @JsonProperty("plasticSurgery")
    PlasticSurgery,
    @JsonProperty("podiatry")
    Podiatry,
    @JsonProperty("polytrauma")
    Polytrauma,
    @JsonProperty("primaryCare")
    PrimaryCare,
    @JsonProperty("psychiatry")
    Psychiatry,
    @JsonProperty("psychology")
    Psychology,
    @JsonProperty("ptsd")
    Ptsd,
    @JsonProperty("pulmonaryMedicine")
    PulmonaryMedicine,
    @JsonProperty("radiationOncology")
    RadiationOncology,
    @JsonProperty("radiology")
    Radiology,
    @JsonProperty("recreationTherapy")
    RecreationTherapy,
    @JsonProperty("registerForCare")
    RegisterForCare,
    @JsonProperty("registryExams")
    RegistryExams,
    @JsonProperty("rehabilitation")
    Rehabilitation,
    @JsonProperty("prosthetics")
    Prosthetics,
    @JsonProperty("transitionCounseling")
    TransitionCounseling,
    @JsonProperty("rheumatology")
    Rheumatology,
    @JsonProperty("sleepMedicine")
    SleepMedicine,
    @JsonProperty("smoking")
    Smoking,
    @JsonProperty("socialWork")
    SocialWork,
    // SpecialtyCare is a V0 holdover. V1 contains specific instances of its specialized care.
    SpecialtyCare,
    @JsonProperty("spinalInjury")
    SpinalInjury,
    @JsonProperty("suicidePrevention")
    SuicidePrevention,
    @JsonProperty("surgery")
    Surgery,
    @JsonProperty("surgicalOncology")
    SurgicalOncology,
    @JsonProperty("telehealth")
    Telehealth,
    @JsonProperty("thoracicSurgery")
    ThoracicSurgery,
    @JsonProperty("transplantSurgery")
    TransplantSurgery,
    @JsonProperty("travelReimbursement")
    TravelReimbursement,
    @JsonProperty("urgentCare")
    UrgentCare,
    @JsonProperty("urology")
    Urology,
    @JsonProperty("vascularSurgery")
    VascularSurgery,
    @JsonProperty("veteranConnections")
    VeteranConnections,
    @JsonProperty("employmentPrograms")
    EmploymentPrograms,
    @JsonProperty("mobility")
    Mobility,
    @JsonProperty("wholeHealth")
    WholeHealth,
    @JsonProperty("womensHealth")
    WomensHealth,
    @JsonProperty("workshops")
    Workshops,
    @JsonProperty("wound")
    Wound;

    /** Ensure that Jackson can create HealthService enum regardless of capitalization. */
    @JsonCreator
    public static HealthService fromString(String name) {
      return "COVID-19 vaccines".equalsIgnoreCase(name)
          ? HealthService.Covid19Vaccine
          : "MentalHealthCare".equalsIgnoreCase(name)
              ? HealthService.MentalHealth
              : "DentalServices".equalsIgnoreCase(name)
                  ? HealthService.Dental
                  : valueOf(capitalize(name));
    }
  }

  public enum OtherService implements ServiceType {
    OnlineScheduling
  }

  public enum Type {
    va_facilities
  }

  public enum OperatingStatusCode {
    NORMAL,
    NOTICE,
    LIMITED,
    CLOSED
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonDeserialize(using = DatamartAddressDeserializer.class)
  public static final class Address {
    @JsonProperty("address_1")
    String address1;

    @JsonProperty("address_2")
    String address2;

    @JsonProperty("address_3")
    String address3;

    String zip;

    String city;

    String state;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class Addresses {
    @Valid Address mailing;

    @Valid Address physical;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonPropertyOrder({
    "name",
    "facility_type",
    "classification",
    "website",
    "lat",
    "long",
    "time_zone",
    "address",
    "phone",
    "hours",
    "operational_hours_special_instructions",
    "services",
    "satisfaction",
    "wait_times",
    "mobile",
    "active_status",
    "operating_status",
    "detailed_services",
    "visn"
  })
  @JsonDeserialize(using = DatamartFacilityAttributesDeserializer.class)
  public static final class FacilityAttributes {
    @NotNull String name;

    @NotNull
    @JsonProperty("facility_type")
    FacilityType facilityType;

    String classification;

    String website;

    @NotNull
    @JsonProperty("lat")
    BigDecimal latitude;

    @NotNull
    @JsonProperty("long")
    BigDecimal longitude;

    @JsonProperty("time_zone")
    String timeZone;

    @Valid Addresses address;

    @Valid Phone phone;

    @Valid Hours hours;

    @JsonProperty("operational_hours_special_instructions")
    String operationalHoursSpecialInstructions;

    @Valid Services services;

    @Valid Satisfaction satisfaction;

    @Valid
    @JsonProperty("wait_times")
    WaitTimes waitTimes;

    Boolean mobile;

    @JsonProperty("active_status")
    ActiveStatus activeStatus;

    @Valid
    @NotNull
    @JsonProperty(value = "operating_status", required = true)
    OperatingStatus operatingStatus;

    @JsonProperty(value = "detailed_services")
    List<@Valid DatamartDetailedService> detailedServices;

    String visn;

    public static final class FacilityAttributesBuilder {
      @JsonProperty("operationalHoursSpecialInstructions")
      public FacilityAttributes.FacilityAttributesBuilder instructions(String val) {
        return operationalHoursSpecialInstructions(val);
      }
    }
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonDeserialize(using = DatamartHoursDeserializer.class)
  public static final class Hours {
    String sunday;

    String monday;

    String tuesday;

    String wednesday;

    String thursday;

    String friday;

    String saturday;

    public static final class HoursBuilder {
      @JsonProperty("Friday")
      public Hours.HoursBuilder fri(String val) {
        return friday(val);
      }

      @JsonProperty("Monday")
      public Hours.HoursBuilder mon(String val) {
        return monday(val);
      }

      @JsonProperty("Saturday")
      public Hours.HoursBuilder sat(String val) {
        return saturday(val);
      }

      @JsonProperty("Sunday")
      public Hours.HoursBuilder sun(String val) {
        return sunday(val);
      }

      @JsonProperty("Thursday")
      public Hours.HoursBuilder thurs(String val) {
        return thursday(val);
      }

      @JsonProperty("Tuesday")
      public Hours.HoursBuilder tues(String val) {
        return tuesday(val);
      }

      @JsonProperty("Wednesday")
      public Hours.HoursBuilder wed(String val) {
        return wednesday(val);
      }
    }
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonDeserialize(using = DatamartOperatingStatusDeserializer.class)
  public static final class OperatingStatus {
    @NotNull
    @JsonProperty(required = true)
    OperatingStatusCode code;

    @JsonProperty(value = "additional_info", required = false)
    @Size(max = 300)
    String additionalInfo;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonDeserialize(using = DatamartPatientSatisfactionDeserializer.class)
  public static final class PatientSatisfaction {
    @JsonProperty("primary_care_urgent")
    BigDecimal primaryCareUrgent;

    @JsonProperty("primary_care_routine")
    BigDecimal primaryCareRoutine;

    @JsonProperty("specialty_care_urgent")
    BigDecimal specialtyCareUrgent;

    @JsonProperty("specialty_care_routine")
    BigDecimal specialtyCareRoutine;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class PatientWaitTime {
    @NotNull HealthService service;

    @JsonProperty("new")
    BigDecimal newPatientWaitTime;

    @JsonProperty("established")
    BigDecimal establishedPatientWaitTime;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonDeserialize(using = DatamartPhoneDeserializer.class)
  public static final class Phone {
    String fax;

    String main;

    String pharmacy;

    @JsonProperty("after_hours")
    String afterHours;

    @JsonProperty("patient_advocate")
    String patientAdvocate;

    @JsonProperty("mental_health_clinic")
    String mentalHealthClinic;

    @JsonProperty("enrollment_coordinator")
    String enrollmentCoordinator;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonDeserialize(using = DatamartSatisfactionDeserializer.class)
  public static final class Satisfaction {
    @Valid PatientSatisfaction health;

    @JsonProperty("effective_date")
    LocalDate effectiveDate;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonDeserialize(using = DatamartServicesDeserializer.class)
  public static final class Services {
    List<OtherService> other;

    List<HealthService> health;

    List<BenefitsService> benefits;

    @JsonProperty("last_updated")
    LocalDate lastUpdated;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonDeserialize(using = DatamartWaitTimesDeserializer.class)
  public static final class WaitTimes {
    @Valid List<PatientWaitTime> health;

    @JsonProperty("effective_date")
    LocalDate effectiveDate;
  }
}
