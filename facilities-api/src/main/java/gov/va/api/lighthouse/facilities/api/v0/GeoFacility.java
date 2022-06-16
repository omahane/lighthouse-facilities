package gov.va.api.lighthouse.facilities.api.v0;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
@Schema(description = "GeoJSON-complaint Feature object describing a VA Facility")
public final class GeoFacility {
  @Schema(description = "Top level category describing the type of facility.", example = "Feature")
  @NotNull
  Type type;

  @Valid @NotNull Geometry geometry;

  @Valid @NotNull Properties properties;

  public enum GeometryType {
    Point
  }

  public enum Type {
    Feature
  }

  @Value
  @Builder
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
  @Schema(
      description = "Geometric data giving the physical location of a VA Facility.",
      nullable = true)
  public static final class Geometry {
    @Schema(description = "Describes the type of geometric data provided.", example = "Point")
    @NotNull
    GeometryType type;

    @Schema(
        description = "The latitude and longitude of the Facility's physical location.",
        example = "[-77.0367761, 38.9004181]",
        nullable = true)
    @Size(min = 2, max = 2)
    List<BigDecimal> coordinates;
  }

  @Value
  @Builder
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
  @JsonPropertyOrder({
    "id",
    "name",
    "facility_type",
    "classification",
    "website",
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
    "visn"
  })
  @Schema(description = "Details describing a facility.", nullable = true)
  public static final class Properties {
    @Schema(description = "Identifier representing the Facility.", example = "vha_688")
    @NotNull
    String id;

    @Schema(
        description = "Name associated with given facility.",
        example = "Washington VA Medical Center")
    String name;

    @NotNull
    @JsonProperty("facility_type")
    @Schema(
        description =
            "One of facility top-level type categories (e.g.) "
                + "health, benefits, cemetery and vet center.",
        example = "va_health_facility")
    Facility.FacilityType facilityType;

    @Schema(
        description = "Subtype of facility which can further be used to describe facility.",
        example = "VA Medical Center (VAMC)",
        nullable = true)
    String classification;

    @Schema(
        description = "Web address of facility.",
        example = "http://www.washingtondc.va.gov",
        nullable = true)
    String website;

    @Schema(description = "Facility time zone", format = "String", example = "America/New_York")
    @JsonProperty("time_zone")
    String timeZone;

    @Schema(description = "Collection of addresses associated with a facility.", nullable = true)
    @Valid
    Facility.Addresses address;

    @Schema(
        description = "Phone number contact for facility.",
        example = "1-800-827-1000",
        nullable = true)
    @Valid
    Facility.Phone phone;

    @Schema(
        description = "Operating hours for facility.",
        example = "\"monday\": \"9:30AM-4:00PM\",",
        nullable = true)
    @Valid
    Facility.Hours hours;

    @Schema(
        description = "Additional information about facility operating hours.",
        example =
            "[\"More hours are available for some services.\","
                + "\"If you need to talk to someone, call the Vet Center at 1-877-927-8387.\","
                + "\"Vet Center hours are dependent upon outreach assignments.\" ]",
        nullable = true)
    @JsonProperty("operational_hours_special_instructions")
    String operationalHoursSpecialInstructions;

    @Schema(nullable = true)
    @Valid
    Facility.Services services;

    @Schema(nullable = true)
    @Valid
    Facility.Satisfaction satisfaction;

    @Valid
    @Schema(nullable = true)
    @JsonProperty("wait_times")
    Facility.WaitTimes waitTimes;

    @Schema(example = "false", nullable = true)
    Boolean mobile;

    @Schema(
        description = "This field is deprecated and replaced with \"operating_status\".",
        nullable = true)
    @JsonProperty("active_status")
    Facility.ActiveStatus activeStatus;

    @Valid
    @NotNull
    @JsonProperty(value = "operating_status", required = true)
    Facility.OperatingStatus operatingStatus;

    @JsonProperty(value = "detailed_services")
    @Schema(nullable = true)
    List<@Valid DetailedService> detailedServices;

    @Schema(example = "20", nullable = true)
    String visn;
  }
}
