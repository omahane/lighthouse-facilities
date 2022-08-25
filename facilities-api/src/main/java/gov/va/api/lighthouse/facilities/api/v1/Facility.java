package gov.va.api.lighthouse.facilities.api.v1;

import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.isBlank;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import gov.va.api.lighthouse.facilities.api.ServiceType;
import gov.va.api.lighthouse.facilities.api.v1.serializers.AddressSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.AddressesSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.FacilityAttributesSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.FacilitySerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.HoursSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.OperatingStatusSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.PatientSatisfactionSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.PatientWaitTimeSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.PhoneSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.SatisfactionSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.ServicesSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.WaitTimesSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

@Data
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
@JsonSerialize(using = FacilitySerializer.class)
@Schema(description = "JSON API-compliant object describing a VA facility")
public final class Facility implements CanBeEmpty {
  @Schema(example = "vha_688")
  @NotNull
  String id;

  @NotNull Type type;

  @Valid @NotNull FacilityAttributes attributes;

  /** Empty elements will be omitted from JSON serialization. */
  @JsonIgnore
  public boolean isEmpty() {
    return isBlank(id())
        && ObjectUtils.isEmpty(type())
        && (attributes() == null || attributes().isEmpty());
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
  @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
  @JsonSerialize(using = AddressSerializer.class)
  @Schema(nullable = true)
  public static final class Address implements CanBeEmpty {
    @Schema(example = "50 Irving Street, Northwest", nullable = true)
    String address1;

    @Schema(example = "Bldg 2", nullable = true)
    String address2;

    @Schema(example = "Suite 7", nullable = true)
    String address3;

    @Schema(example = "20422-0001", nullable = true)
    String zip;

    @Schema(example = "Washington", nullable = true)
    String city;

    @Schema(example = "DC", nullable = true)
    String state;

    /** Empty elements will be omitted from JSON serialization. */
    @JsonIgnore
    public boolean isEmpty() {
      return isBlank(address1())
          && isBlank(address2())
          && isBlank(address3())
          && isBlank(zip())
          && isBlank(city())
          && isBlank(state());
    }
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
  @JsonSerialize(using = AddressesSerializer.class)
  @Schema(nullable = true)
  public static final class Addresses implements CanBeEmpty {
    @Schema(nullable = true)
    @Valid
    Address mailing;

    @Schema(nullable = true)
    @Valid
    Address physical;

    /** Empty elements will be omitted from JSON serialization. */
    @JsonIgnore
    public boolean isEmpty() {
      return (mailing() == null || mailing().isEmpty())
          && (physical() == null || physical().isEmpty());
    }
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
  @JsonSerialize(using = FacilityAttributesSerializer.class)
  @JsonPropertyOrder({
    "name",
    "facilityType",
    "classification",
    "website",
    "lat",
    "long",
    "timeZone",
    "address",
    "phone",
    "hours",
    "operationalHoursSpecialInstructions",
    "services",
    "satisfaction",
    "waitTimes",
    "mobile",
    "operatingStatus",
    "detailedServices",
    "visn"
  })
  @Schema(nullable = true)
  public static final class FacilityAttributes implements CanBeEmpty {
    @NotNull
    @Schema(example = "Washington VA Medical Center")
    String name;

    @NotNull
    @Schema(example = "va_health_facility")
    FacilityType facilityType;

    @Schema(example = "VA Medical Center (VAMC)", nullable = true)
    String classification;

    @Schema(example = "http://www.washingtondc.va.gov", nullable = true)
    String website;

    @NotNull
    @Schema(description = "Facility latitude", format = "float", example = "38.9311137")
    @JsonProperty("lat")
    BigDecimal latitude;

    @NotNull
    @Schema(description = "Facility longitude", format = "float", example = "-77.0109110499999")
    @JsonProperty("long")
    BigDecimal longitude;

    @Schema(description = "Facility time zone", format = "String", example = "America/New_York")
    String timeZone;

    @Schema(nullable = true)
    @Valid
    Addresses address;

    @Schema(nullable = true)
    @Valid
    Phone phone;

    @Schema(nullable = true)
    @Valid
    Hours hours;

    @Schema(
        example =
            "[\"More hours are available for some services.\","
                + "\"If you need to talk to someone, call the Vet Center at 1-877-927-8387.\","
                + "\"Vet Center hours are dependent upon outreach assignments.\" ]",
        nullable = true)
    List<String> operationalHoursSpecialInstructions;

    @Schema(nullable = true)
    @Valid
    Services services;

    @Schema(nullable = true)
    @Valid
    Satisfaction satisfaction;

    @Valid
    @Schema(example = "10", nullable = true)
    WaitTimes waitTimes;

    @Schema(example = "false", nullable = true)
    Boolean mobile;

    @Valid
    @NotNull
    @JsonProperty(required = true)
    @Schema(example = "NORMAL")
    OperatingStatus operatingStatus;

    @Schema(example = "20", nullable = true)
    String visn;

    /** Empty elements will be omitted from JSON serialization. */
    @JsonIgnore
    public boolean isEmpty() {
      return isBlank(name())
          && ObjectUtils.isEmpty(facilityType())
          && isBlank(classification())
          && isBlank(website())
          && ObjectUtils.isEmpty(latitude())
          && ObjectUtils.isEmpty(longitude())
          && isBlank(timeZone())
          && (address() == null || address().isEmpty())
          && (phone() == null || phone().isEmpty())
          && (hours() == null || hours().isEmpty())
          && ObjectUtils.isEmpty(operationalHoursSpecialInstructions())
          && (services() == null || services().isEmpty())
          && (satisfaction() == null || satisfaction().isEmpty())
          && (waitTimes() == null || waitTimes().isEmpty())
          && ObjectUtils.isEmpty(mobile())
          && ObjectUtils.isEmpty(operatingStatus())
          && isBlank(visn());
    }

    public static final class FacilityAttributesBuilder {
      public FacilityAttributesBuilder instructions(List<String> val) {
        return operationalHoursSpecialInstructions(val);
      }
    }
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
  @JsonSerialize(using = HoursSerializer.class)
  @JsonPropertyOrder({"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"})
  @Schema(
      description =
          "Standard hours of operation. Currently formatted as descriptive text suitable for "
              + "display, with no guarantee of a standard parseable format. "
              + "Hours of operation may vary due to holidays or other events.",
      nullable = true)
  public static final class Hours implements CanBeEmpty {
    @Schema(example = "9AM-5PM", nullable = true)
    String monday;

    @Schema(example = "9AM-5PM", nullable = true)
    String tuesday;

    @Schema(example = "9AM-5PM", nullable = true)
    String wednesday;

    @Schema(example = "9AM-5PM", nullable = true)
    String thursday;

    @Schema(example = "9AM-5PM", nullable = true)
    String friday;

    @Schema(example = "Closed", nullable = true)
    String saturday;

    @Schema(example = "Closed", nullable = true)
    String sunday;

    /** Empty elements will be omitted from JSON serialization. */
    @JsonIgnore
    public boolean isEmpty() {
      return isBlank(monday())
          && isBlank(tuesday())
          && isBlank(wednesday())
          && isBlank(thursday())
          && isBlank(friday())
          && isBlank(saturday())
          && isBlank(sunday());
    }
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
  @JsonSerialize(using = OperatingStatusSerializer.class)
  @Schema(
      description =
          "Current status of facility operations."
              + " The overall status of the facility, which can be:"
              + " Normal Hours and Services,"
              + " Facility Notice,"
              + " Limited Hours and/or Services,"
              + " or Closed."
              + " This field replaces active_status.",
      nullable = true)
  public static final class OperatingStatus implements CanBeEmpty {
    @NotNull
    @JsonProperty(required = true)
    @Schema(
        example = "NORMAL",
        description =
            "Status codes indicate normal hours/services,"
                + " limited hours/services, closed operations,"
                + " or published facility notices for visitors.",
        nullable = true)
    OperatingStatusCode code;

    @JsonProperty(required = false)
    @Size(max = 300)
    @Schema(
        description =
            "Details of facility notices for visitors,"
                + " such as messages about parking lot closures or"
                + " floor visitation information.",
        nullable = true)
    String additionalInfo;

    /** Empty elements will be omitted from JSON serialization. */
    @JsonIgnore
    public boolean isEmpty() {
      return ObjectUtils.isEmpty(code()) && isBlank(additionalInfo());
    }
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
  @JsonSerialize(using = PatientSatisfactionSerializer.class)
  @Schema(
      description = "Veteran-reported satisfaction scores for health care services",
      nullable = true)
  public static final class PatientSatisfaction implements CanBeEmpty {
    @Schema(
        example = "0.85",
        format = "float",
        description =
            "% of Veterans who say they usually or always get an appointment when "
                + "they need care right away at a primary care location.",
        nullable = true)
    BigDecimal primaryCareUrgent;

    @Schema(
        example = "0.85",
        format = "float",
        description =
            "% of Veterans who say they usually or always get an appointment when "
                + "they need it at a primary care location.",
        nullable = true)
    BigDecimal primaryCareRoutine;

    @Schema(
        example = "0.85",
        format = "float",
        description =
            "% of Veterans who say they usually or always get an appointment when "
                + "they need care right away at a specialty location.",
        nullable = true)
    BigDecimal specialtyCareUrgent;

    @Schema(
        example = "0.85",
        format = "float",
        description =
            "% of Veterans who say they usually or always get an appointment when "
                + "they need it at a specialty location.",
        nullable = true)
    BigDecimal specialtyCareRoutine;

    /** Empty elements will be omitted from JSON serialization. */
    @JsonIgnore
    public boolean isEmpty() {
      return ObjectUtils.isEmpty(primaryCareUrgent())
          && ObjectUtils.isEmpty(primaryCareRoutine())
          && ObjectUtils.isEmpty(specialtyCareUrgent())
          && ObjectUtils.isEmpty(specialtyCareRoutine());
    }
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
  @JsonSerialize(using = PatientWaitTimeSerializer.class)
  @Schema(
      description =
          "Expected wait times for new and established patients for a given health care service",
      nullable = true)
  public static final class PatientWaitTime implements CanBeEmpty {
    @NotNull HealthService service;

    @Schema(
        example = "10",
        description =
            "Average number of days a Veteran who hasn't been to this location has to wait "
                + "for a non-urgent appointment.",
        nullable = true)
    @JsonProperty("new")
    BigDecimal newPatientWaitTime;

    @Schema(
        example = "5",
        description =
            "Average number of days a patient who has already been to this location has to wait "
                + "for a non-urgent appointment.",
        nullable = true)
    @JsonProperty("established")
    BigDecimal establishedPatientWaitTime;

    /** Empty elements will be omitted from JSON serialization. */
    @JsonIgnore
    public boolean isEmpty() {
      return ObjectUtils.isEmpty(service())
          && ObjectUtils.isEmpty(newPatientWaitTime())
          && ObjectUtils.isEmpty(establishedPatientWaitTime());
    }
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
  @JsonSerialize(using = PhoneSerializer.class)
  @Schema(nullable = true)
  public static final class Phone implements CanBeEmpty {
    @Schema(example = "202-555-1212", nullable = true)
    String fax;

    @Schema(example = "202-555-1212", nullable = true)
    String main;

    @Schema(example = "202-555-1212", nullable = true)
    String pharmacy;

    @Schema(example = "202-555-1212", nullable = true)
    String afterHours;

    @Schema(example = "202-555-1212", nullable = true)
    String patientAdvocate;

    @Schema(example = "202-555-1212", nullable = true)
    String mentalHealthClinic;

    @Schema(example = "202-555-1212", nullable = true)
    String enrollmentCoordinator;

    /** Empty elements will be omitted from JSON serialization. */
    @JsonIgnore
    public boolean isEmpty() {
      return isBlank(fax())
          && isBlank(main())
          && isBlank(pharmacy())
          && isBlank(afterHours())
          && isBlank(patientAdvocate())
          && isBlank(mentalHealthClinic())
          && isBlank(enrollmentCoordinator());
    }
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
  @JsonSerialize(using = SatisfactionSerializer.class)
  @Schema(nullable = true)
  public static final class Satisfaction implements CanBeEmpty {
    @Schema(nullable = true)
    @Valid
    PatientSatisfaction health;

    @Schema(example = "2018-01-01", nullable = true)
    LocalDate effectiveDate;

    /** Empty elements will be omitted from JSON serialization. */
    @JsonIgnore
    public boolean isEmpty() {
      return (health() == null || health().isEmpty()) && ObjectUtils.isEmpty(effectiveDate());
    }
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
  @JsonSerialize(using = ServicesSerializer.class)
  @Schema(nullable = true)
  public static final class Services implements CanBeEmpty {
    @Schema(nullable = true)
    List<OtherService> other;

    @Schema(nullable = true)
    List<HealthService> health;

    @Schema(nullable = true)
    List<BenefitsService> benefits;

    @Schema(example = "2018-01-01", nullable = true)
    LocalDate lastUpdated;

    /** Empty elements will be omitted from JSON serialization. */
    @JsonIgnore
    public boolean isEmpty() {
      return ObjectUtils.isEmpty(other())
          && ObjectUtils.isEmpty(health())
          && ObjectUtils.isEmpty(benefits())
          && ObjectUtils.isEmpty(lastUpdated());
    }
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
  @JsonSerialize(using = WaitTimesSerializer.class)
  @Schema(nullable = true)
  public static final class WaitTimes implements CanBeEmpty {
    @Schema(nullable = true)
    List<@Valid PatientWaitTime> health;

    @Schema(example = "2018-01-01", nullable = true)
    LocalDate effectiveDate;

    /** Empty elements will be omitted from JSON serialization. */
    @JsonIgnore
    public boolean isEmpty() {
      return ObjectUtils.isEmpty(health()) && ObjectUtils.isEmpty(effectiveDate());
    }
  }
}
