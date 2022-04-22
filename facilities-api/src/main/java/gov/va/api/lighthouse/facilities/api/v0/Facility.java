package gov.va.api.lighthouse.facilities.api.v0;

import static org.apache.commons.lang3.StringUtils.capitalize;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import gov.va.api.lighthouse.facilities.api.ServiceType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
@Schema(description = "JSON API-compliant object describing a VA facility")
public final class Facility {
  @Schema(example = "vha_688")
  @NotNull
  String id;

  @NotNull Type type;

  @Valid @NotNull FacilityAttributes attributes;

  public enum ActiveStatus {
    A,
    T
  }

  public enum BenefitsService implements ServiceType {
    ApplyingForBenefits("applyingForBenefits"),
    BurialClaimAssistance("burialClaimAssistance"),
    DisabilityClaimAssistance("disabilityClaimAssistance"),
    eBenefitsRegistrationAssistance("eBenefitsRegistrationAssistance"),
    EducationAndCareerCounseling("educationAndCareerCounseling"),
    EducationClaimAssistance("educationClaimAssistance"),
    FamilyMemberClaimAssistance("familyMemberClaimAssistance"),
    HomelessAssistance("homelessAssistance"),
    InsuranceClaimAssistanceAndFinancialCounseling(
        "insuranceClaimAssistanceAndFinancialCounseling"),
    IntegratedDisabilityEvaluationSystemAssistance(
        "integratedDisabilityEvaluationSystemAssistance"),
    Pensions("pensions"),
    PreDischargeClaimAssistance("preDischargeClaimAssistance"),
    TransitionAssistance("transitionAssistance"),
    UpdatingDirectDepositInformation("updatingDirectDepositInformation"),
    VAHomeLoanAssistance("vaHomeLoanAssistance"),
    VocationalRehabilitationAndEmploymentAssistance(
        "vocationalRehabilitationAndEmploymentAssistance");

    private final String serviceId;

    BenefitsService(@NotNull String serviceId) {
      this.serviceId = serviceId;
    }

    /** Ensure that Jackson can create BenefitsService enum regardless of capitalization. */
    @JsonCreator
    public static BenefitsService fromString(String name) {
      return eBenefitsRegistrationAssistance.name().equalsIgnoreCase(name)
          ? eBenefitsRegistrationAssistance
          : valueOf(capitalize(name));
    }

    /** Determine whether specified service id represents benefits service. */
    public static boolean isRecognizedServiceId(String serviceId) {
      return Arrays.stream(values()).parallel().anyMatch(bs -> bs.serviceId().equals(serviceId));
    }

    /** Determine whether specified service name represents benefits service. */
    public static boolean isRecognizedServiceName(String serviceName) {
      return Arrays.stream(values()).parallel().anyMatch(bs -> bs.name().equals(serviceName));
    }

    public String serviceId() {
      return serviceId;
    }
  }

  public enum FacilityType {
    va_benefits_facility,
    va_cemetery,
    va_health_facility,
    vet_center
  }

  public enum HealthService implements ServiceType {
    Audiology("audiology"),
    Cardiology("cardiology"),
    CaregiverSupport("caregiverSupport"),
    Covid19Vaccine("covid19Vaccine"),
    DentalServices("dentalServices"),
    Dermatology("dermatology"),
    EmergencyCare("emergencyCare"),
    Gastroenterology("gastroenterology"),
    Gynecology("gynecology"),
    MentalHealthCare("mentalHealthCare"),
    Ophthalmology("ophthalmology"),
    Optometry("optometry"),
    Orthopedics("orthopedics"),
    Nutrition("nutrition"),
    Podiatry("podiatry"),
    PrimaryCare("primaryCare"),
    SpecialtyCare("specialtyCare"),
    UrgentCare("urgentCare"),
    Urology("urology"),
    WomensHealth("womensHealth");

    private final String serviceId;

    HealthService(@NotNull String serviceId) {
      this.serviceId = serviceId;
    }

    /** Ensure that Jackson can create HealthService enum regardless of capitalization. */
    @JsonCreator
    public static HealthService fromString(String name) {
      return "COVID-19 vaccines".equalsIgnoreCase(name)
          ? Covid19Vaccine
          : "mentalHealth".equalsIgnoreCase(name)
              ? MentalHealthCare
              : "dental".equalsIgnoreCase(name) ? DentalServices : valueOf(capitalize(name));
    }

    /** Determine whether specified service name represents Covid-19 health service. */
    public static boolean isRecognizedCovid19ServiceName(String serviceName) {
      return "COVID-19 vaccines".equals(serviceName)
          || Covid19Vaccine.name().equalsIgnoreCase(serviceName);
    }

    /** Determine whether specified service id represents health service. */
    public static boolean isRecognizedServiceId(String serviceId) {
      return Arrays.stream(values()).parallel().anyMatch(hs -> hs.serviceId().equals(serviceId));
    }

    /** Determine whether specified service name represents health service. */
    public static boolean isRecognizedServiceName(String serviceName) {
      return isRecognizedCovid19ServiceName(serviceName)
          || "dental".equalsIgnoreCase(serviceName)
          || "mentalHealth".equalsIgnoreCase(serviceName)
          || Arrays.stream(values())
              .parallel()
              .anyMatch(hs -> hs.name().equalsIgnoreCase(serviceName));
    }

    public String serviceId() {
      return serviceId;
    }
  }

  public enum OtherService implements ServiceType {
    OnlineScheduling("onlineScheduling");

    private final String serviceId;

    OtherService(@NotNull String serviceId) {
      this.serviceId = serviceId;
    }

    /** Determine whether specified service id represents other service. */
    public static boolean isRecognizedServiceId(String serviceId) {
      return Arrays.stream(values()).parallel().anyMatch(os -> os.serviceId().equals(serviceId));
    }

    /** Determine whether specified service name represents other service. */
    public static boolean isRecognizedServiceName(String serviceName) {
      return Arrays.stream(values()).parallel().anyMatch(os -> os.name().equals(serviceName));
    }

    public String serviceId() {
      return serviceId;
    }
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
  @JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
  @Schema(nullable = true)
  public static final class Address {
    @Schema(example = "50 Irving Street, Northwest", nullable = true)
    @JsonProperty("address_1")
    String address1;

    @Schema(example = "Bldg 2", nullable = true)
    @JsonProperty("address_2")
    String address2;

    @Schema(example = "Suite 7", nullable = true)
    @JsonProperty("address_3")
    String address3;

    @Schema(example = "20422-0001", nullable = true)
    String zip;

    @Schema(example = "Washington", nullable = true)
    String city;

    @Schema(example = "DC", nullable = true)
    String state;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
  @Schema(nullable = true)
  public static final class Addresses {
    @Schema(nullable = true)
    @Valid
    Address mailing;

    @Schema(nullable = true)
    @Valid
    Address physical;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
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
  @Schema(nullable = true)
  public static final class FacilityAttributes {
    @NotNull
    @Schema(example = "Washington VA Medical Center")
    String name;

    @NotNull
    @Schema(example = "va_health_facility")
    @JsonProperty("facility_type")
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
    @JsonProperty("time_zone")
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
        example = "Normal business hours are Monday through Friday, 8:00 a.m. to 4:30 p.m.",
        nullable = true)
    @JsonProperty("operational_hours_special_instructions")
    String operationalHoursSpecialInstructions;

    @Schema(nullable = true)
    @Valid
    Services services;

    @Schema(nullable = true)
    @Valid
    Satisfaction satisfaction;

    @Valid
    @Schema(example = "10", nullable = true)
    @JsonProperty("wait_times")
    WaitTimes waitTimes;

    @Schema(example = "false", nullable = true)
    Boolean mobile;

    @JsonProperty("active_status")
    @Schema(
        description = "This field is deprecated and replaced with \"operating_status\".",
        nullable = true)
    ActiveStatus activeStatus;

    @Valid
    @NotNull
    @JsonProperty(value = "operating_status", required = true)
    @Schema(example = "NORMAL")
    OperatingStatus operatingStatus;

    @JsonProperty(value = "detailed_services")
    @Schema(nullable = true)
    List<@Valid DetailedService> detailedServices;

    @Schema(example = "20", nullable = true)
    String visn;

    public static final class FacilityAttributesBuilder {
      @JsonProperty("operationalHoursSpecialInstructions")
      public FacilityAttributesBuilder instructions(String val) {
        return operationalHoursSpecialInstructions(val);
      }
    }
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
  @Schema(
      description =
          "Standard hours of operation. Currently formatted as descriptive text suitable for "
              + "display, with no guarantee of a standard parseable format. "
              + "Hours of operation may vary due to holidays or other events.",
      nullable = true)
  public static final class Hours {
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

    public static final class HoursBuilder {
      @JsonProperty("Friday")
      public HoursBuilder fri(String val) {
        return friday(val);
      }

      @JsonProperty("Monday")
      public HoursBuilder mon(String val) {
        return monday(val);
      }

      @JsonProperty("Saturday")
      public HoursBuilder sat(String val) {
        return saturday(val);
      }

      @JsonProperty("Sunday")
      public HoursBuilder sun(String val) {
        return sunday(val);
      }

      @JsonProperty("Thursday")
      public HoursBuilder thurs(String val) {
        return thursday(val);
      }

      @JsonProperty("Tuesday")
      public HoursBuilder tues(String val) {
        return tuesday(val);
      }

      @JsonProperty("Wednesday")
      public HoursBuilder wed(String val) {
        return wednesday(val);
      }
    }
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
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
  public static final class OperatingStatus {
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

    @JsonProperty(value = "additional_info", required = false)
    @JsonAlias("additionalInfo")
    @Size(max = 300)
    @Schema(
        description =
            "Details of facility notices for visitors,"
                + " such as messages about parking lot closures or"
                + " floor visitation information.",
        nullable = true)
    String additionalInfo;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
  @Schema(
      description = "Veteran-reported satisfaction scores for health care services",
      nullable = true)
  public static final class PatientSatisfaction {
    @Schema(
        example = "0.85",
        format = "float",
        description =
            "% of Veterans who say they usually or always get an appointment when "
                + "they need care right away at a primary care location.",
        nullable = true)
    @JsonProperty("primary_care_urgent")
    BigDecimal primaryCareUrgent;

    @Schema(
        example = "0.85",
        format = "float",
        description =
            "% of Veterans who say they usually or always get an appointment when "
                + "they need it at a primary care location.",
        nullable = true)
    @JsonProperty("primary_care_routine")
    BigDecimal primaryCareRoutine;

    @Schema(
        example = "0.85",
        format = "float",
        description =
            "% of Veterans who say they usually or always get an appointment when "
                + "they need care right away at a specialty location.",
        nullable = true)
    @JsonProperty("specialty_care_urgent")
    BigDecimal specialtyCareUrgent;

    @Schema(
        example = "0.85",
        format = "float",
        description =
            "% of Veterans who say they usually or always get an appointment when "
                + "they need it at a specialty location.",
        nullable = true)
    @JsonProperty("specialty_care_routine")
    BigDecimal specialtyCareRoutine;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
  @Schema(
      description =
          "Expected wait times for new and established patients for a given health care service",
      nullable = true)
  public static final class PatientWaitTime {
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
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
  @Schema(nullable = true)
  public static final class Phone {
    @Schema(example = "202-555-1212", nullable = true)
    String fax;

    @Schema(example = "202-555-1212", nullable = true)
    String main;

    @Schema(example = "202-555-1212", nullable = true)
    String pharmacy;

    @Schema(example = "202-555-1212", nullable = true)
    @JsonProperty("after_hours")
    String afterHours;

    @Schema(example = "202-555-1212", nullable = true)
    @JsonProperty("patient_advocate")
    String patientAdvocate;

    @Schema(example = "202-555-1212", nullable = true)
    @JsonProperty("mental_health_clinic")
    String mentalHealthClinic;

    @Schema(example = "202-555-1212", nullable = true)
    @JsonProperty("enrollment_coordinator")
    String enrollmentCoordinator;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
  @Schema(nullable = true)
  public static final class Satisfaction {
    @Schema(nullable = true)
    @Valid
    PatientSatisfaction health;

    @Schema(example = "2018-01-01", nullable = true)
    @JsonProperty("effective_date")
    LocalDate effectiveDate;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
  @Schema(nullable = true)
  public static final class Services {
    @Schema(nullable = true)
    List<OtherService> other;

    @Schema(nullable = true)
    List<HealthService> health;

    @Schema(nullable = true)
    List<BenefitsService> benefits;

    @Schema(example = "2018-01-01", nullable = true)
    @JsonProperty("last_updated")
    LocalDate lastUpdated;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
  @Schema(nullable = true)
  public static final class WaitTimes {
    @Schema(nullable = true)
    List<@Valid PatientWaitTime> health;

    @Schema(example = "2018-01-01", nullable = true)
    @JsonProperty("effective_date")
    LocalDate effectiveDate;
  }
}
