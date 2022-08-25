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
  @Schema(example = "Feature")
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
  @Schema(nullable = true)
  public static final class Geometry {
    @Schema(example = "Point")
    @NotNull
    GeometryType type;

    @Schema(example = "[-77.0367761, 38.9004181]", nullable = true)
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
  public static final class Properties {
    @Schema(example = "vha_688")
    @NotNull
    String id;

    @Schema(example = "Washington VA Medical Center", nullable = true)
    String name;

    @NotNull
    @JsonProperty("facility_type")
    Facility.FacilityType facilityType;

    @Schema(example = "VA Medical Center (VAMC)", nullable = true)
    String classification;

    @Schema(example = "http://www.washingtondc.va.gov", nullable = true)
    String website;

    @Schema(description = "Facility time zone", format = "String", example = "America/New_York")
    @JsonProperty("time_zone")
    String timeZone;

    @Schema(nullable = true)
    @Valid
    Facility.Addresses address;

    @Schema(nullable = true)
    @Valid
    Facility.Phone phone;

    @Schema(nullable = true)
    @Valid
    Facility.Hours hours;

    @Schema(
        example = "Administrative hours are Monday-Friday 8:00 a.m. to 4:30 p.m.",
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

    @Schema(nullable = true)
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
