package gov.va.api.lighthouse.facilities.api.v0;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.Valid;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
@Schema(description = "Data provided by CMS to Facilities to be applied on top of known data.")
public class CmsOverlay {
  @Valid
  @JsonProperty("operating_status")
  @JsonAlias("operatingStatus")
  Facility.OperatingStatus operatingStatus;

  @JsonProperty("detailed_services")
  @JsonAlias("detailedServices")
  List<@Valid DetailedService> detailedServices;

  @Valid
  @JsonProperty("system")
  HealthCareSystem healthCareSystem;

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
  @Schema(description = "Describes the Health Care System for a facility.", nullable = true)
  public static final class HealthCareSystem {
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
    @JsonProperty("covid_url")
    @JsonAlias("covidUrl")
    String covidUrl;

    @Schema(
        description = "VA Health connect phone number",
        example = "555-555-5555 x123",
        nullable = true)
    @JsonProperty("va_health_connect_phone")
    @JsonAlias("vaHealthConnectPhone")
    String healthConnectPhone;
  }
}
