package gov.va.api.lighthouse.facilities;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService;
import gov.va.api.lighthouse.facilities.DatamartFacility.HealthService;
import gov.va.api.lighthouse.facilities.DatamartFacility.OtherService;
import gov.va.api.lighthouse.facilities.api.TypeOfService;
import gov.va.api.lighthouse.facilities.api.TypedService;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@JsonInclude()
@JsonIgnoreProperties(
    ignoreUnknown = true,
    value = {"active"},
    allowSetters = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({
  "serviceInfo",
  "description_facility",
  "appointment_leadin",
  "appointment_phones",
  "online_scheduling_available",
  "referral_required",
  "walk_ins_accepted",
  "service_locations"
})
public class DatamartDetailedService {
  @NonNull ServiceInfo serviceInfo;

  boolean active;

  @JsonIgnore String changed;

  @JsonProperty("description_facility")
  String descriptionFacility;

  @JsonProperty("appointment_leadin")
  String appointmentLeadIn;

  @JsonProperty("online_scheduling_available")
  String onlineSchedulingAvailable;

  String path;

  @JsonProperty("appointment_phones")
  List<AppointmentPhoneNumber> phoneNumbers;

  @JsonProperty("referral_required")
  String referralRequired;

  @JsonProperty("service_locations")
  List<DetailedServiceLocation> serviceLocations;

  @JsonProperty("walk_ins_accepted")
  String walkInsAccepted;

  private static boolean isRecognizedServiceName(String serviceName) {
    return isNotEmpty(serviceName)
        && (HealthService.isRecognizedServiceName(serviceName)
            || BenefitsService.isRecognizedServiceName(serviceName)
            || OtherService.isRecognizedServiceName(serviceName));
  }

  /** Provide backwards compatability with version 0 detailed services. */
  @JsonProperty("name")
  public DatamartDetailedService serviceName(String serviceName) {
    if (isRecognizedServiceName(serviceName)) {
      // Update service info based on recognized service name
      serviceInfo(
          serviceInfo() == null
              ? ServiceInfo.builder()
                  .serviceId(
                      HealthService.isRecognizedServiceName(serviceName)
                          ? HealthService.fromString(serviceName).serviceId()
                          : BenefitsService.isRecognizedServiceName(serviceName)
                              ? BenefitsService.fromString(serviceName).serviceId()
                              : OtherService.isRecognizedServiceName(serviceName)
                                  ? OtherService.fromString(serviceName).serviceId()
                                  : TypedService.INVALID_SVC_ID)
                  .name(serviceName)
                  .serviceType(
                      HealthService.isRecognizedServiceName(serviceName)
                          ? TypeOfService.Health
                          : BenefitsService.isRecognizedServiceName(serviceName)
                              ? TypeOfService.Benefits
                              : OtherService.isRecognizedServiceName(serviceName)
                                  ? TypeOfService.Other
                                  : TypeOfService.Health)
                  .build()
              : serviceInfo().name(serviceName));
    }
    return this;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_EMPTY)
  @JsonPropertyOrder({"name", "serviceId", "serviceType"})
  @Schema(description = "Service information.")
  public static final class ServiceInfo {
    @Schema(description = "Service id.", example = "covid19Vaccine")
    @NonNull
    String serviceId;

    @Schema(description = "Service name.", example = "COVID-19 vaccines", nullable = true)
    String name;

    @Schema(description = "Service type.", example = "Health")
    @NonNull
    TypeOfService serviceType;

    public static class ServiceInfoBuilder {
      private String serviceId;

      private String name;

      private TypeOfService serviceType;

      /**
       * Method used to set service info name and attempt to infer service id based on provided
       * service name.
       */
      public ServiceInfoBuilder name(String name) {
        this.name = name;
        if (StringUtils.isEmpty(serviceId)) {
          if (HealthService.isRecognizedServiceName(name)) {
            this.serviceId = HealthService.fromString(name).serviceId();
          } else if (BenefitsService.isRecognizedServiceName(name)) {
            this.serviceId = BenefitsService.fromString(name).serviceId();
          } else if (OtherService.isRecognizedServiceName(name)) {
            this.serviceId = OtherService.fromString(name).serviceId();
          }
        }
        return this;
      }

      /**
       * Method used to set service id and infer service name based on provided service id given it
       * is recognized as valid.
       */
      @SneakyThrows
      public ServiceInfoBuilder serviceId(String serviceId) {
        if (HealthService.isRecognizedServiceId(serviceId)) {
          this.serviceId = serviceId;
          if (StringUtils.isEmpty(name)) {
            Optional<HealthService> healthService = HealthService.fromServiceId(serviceId);
            if (healthService.isPresent()) {
              this.name = healthService.get().name();
            }
          }
        } else if (BenefitsService.isRecognizedServiceId(serviceId)) {
          this.serviceId = serviceId;
          if (StringUtils.isEmpty(name)) {
            Optional<BenefitsService> benefitsService = BenefitsService.fromServiceId(serviceId);
            if (benefitsService.isPresent()) {
              this.name = benefitsService.get().name();
            }
          }
        } else if (OtherService.isRecognizedServiceId(serviceId)) {
          this.serviceId = serviceId;
          if (StringUtils.isEmpty(name)) {
            Optional<OtherService> otherService = OtherService.fromServiceId(serviceId);
            if (otherService.isPresent()) {
              this.name = otherService.get().name();
            }
          }
        } else {
          throw new Exception(String.format("Unrecognized service id: %s", serviceId));
        }
        return this;
      }
    }
  }

  @Data
  @Builder
  @JsonInclude()
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonPropertyOrder({
    "building_name_number",
    "clinic_name",
    "wing_floor_or_room_number",
    "address_line1",
    "address_line2",
    "city",
    "state",
    "zip_code",
    "country_code"
  })
  public static final class DetailedServiceAddress {
    @JsonProperty("address_line1")
    String address1;

    @JsonProperty("address_line2")
    String address2;

    String state;

    @JsonProperty("building_name_number")
    String buildingNameNumber;

    @JsonProperty("clinic_name")
    String clinicName;

    @JsonProperty("country_code")
    String countryCode;

    String city;

    @JsonProperty("zip_code")
    String zipCode;

    @JsonProperty("wing_floor_or_room_number")
    String wingFloorOrRoomNumber;
  }

  @Data
  @Builder
  @JsonInclude()
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class AppointmentPhoneNumber {
    String extension;

    String label;

    String number;

    String type;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonPropertyOrder({
    "service_location_address",
    "appointment_phones",
    "email_contacts",
    "facility_service_hours",
    "additional_hours_info"
  })
  public static final class DetailedServiceLocation {
    @JsonProperty("additional_hours_info")
    String additionalHoursInfo;

    @JsonProperty("email_contacts")
    List<DetailedServiceEmailContact> emailContacts;

    @JsonProperty("facility_service_hours")
    @Valid
    DetailedServiceHours facilityServiceHours;

    @JsonProperty("appointment_phones")
    List<AppointmentPhoneNumber> appointmentPhoneNumbers;

    @JsonProperty("service_location_address")
    DetailedServiceAddress serviceLocationAddress;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class DetailedServiceEmailContact {
    @JsonProperty("email_address")
    String emailAddress;

    @JsonProperty("email_label")
    String emailLabel;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonPropertyOrder({"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"})
  public static final class DetailedServiceHours {
    @JsonProperty("Monday")
    String monday;

    @JsonProperty("Tuesday")
    String tuesday;

    @JsonProperty("Wednesday")
    String wednesday;

    @JsonProperty("Thursday")
    String thursday;

    @JsonProperty("Friday")
    String friday;

    @JsonProperty("Saturday")
    String saturday;

    @JsonProperty("Sunday")
    String sunday;
  }
}
