package gov.va.api.lighthouse.facilities.api.v1;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import gov.va.api.lighthouse.facilities.api.v1.serializers.CmsOverlaySerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.Valid;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
@JsonSerialize(using = CmsOverlaySerializer.class)
@Schema(description = "Data provided by CMS to Facilities to be applied on top of known data.")
public class CmsOverlay implements CanBeEmpty {

  @Valid Core core;

  @JsonAlias("operating_status")
  @Valid
  Facility.OperatingStatus operatingStatus;

  @JsonAlias("detailed_services")
  List<@Valid DetailedService> detailedServices;

  @JsonProperty("system")
  HealthCareSystem healthCareSystem;

  /** Empty elements will be omitted from JSON serialization. */
  @Override
  @JsonIgnore
  public boolean isEmpty() {
    return (core() == null || core().isEmpty())
        && (operatingStatus() == null || operatingStatus().isEmpty())
        && ObjectUtils.isEmpty(detailedServices())
        && (healthCareSystem() == null || healthCareSystem().isEmpty());
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
  @Schema(description = "Describes the Health Care System for a facility.", nullable = true)
  public static final class HealthCareSystem implements CanBeEmpty {
    @Schema(
        description = "Health care system name",
        example = "VA Pittsburgh health care",
        nullable = true)
    String name;

    @Schema(
        description = "Health care system website url",
        example = "https://www.va.gov/pittsburgh-health-care/",
        nullable = true)
    String url;

    @Schema(
        description = "Health care system website url for covid-19 services",
        example = "https://www.va.gov/pittsburgh-health-care/programs/covid-19-vaccines/",
        nullable = true)
    @JsonProperty("covidUrl")
    @JsonAlias("covid_url")
    String covidUrl;

    @Schema(
        description = "VA Health connect phone number",
        example = "555-555-5555 x123",
        nullable = true)
    @JsonProperty("vaHealthConnectPhone")
    @JsonAlias("va_health_connect_phone")
    String healthConnectPhone;

    /** Empty elements will be omitted from JSON serialization. */
    @Override
    @JsonIgnore
    public boolean isEmpty() {
      return StringUtils.isEmpty(name())
          && StringUtils.isEmpty(url())
          && StringUtils.isEmpty(covidUrl())
          && StringUtils.isEmpty(healthConnectPhone());
    }
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
  @Schema(description = "Contain information on core facility fields", nullable = true)
  public static final class Core implements CanBeEmpty {
    @Schema(
        description = "Facility url",
        example = "https://www.va.gov/phoenix-health-care/locations/payson-va-clinic",
        nullable = true)
    @JsonAlias("facility_url")
    String facilityUrl;

    /** Empty elements will be omitted from JSON serialization. */
    @Override
    public boolean isEmpty() {
      return isBlank(facilityUrl);
    }
  }
}
