package gov.va.api.lighthouse.facilities.api.v1;

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

@Data
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
@JsonSerialize(using = CmsOverlaySerializer.class)
@Schema(description = "Data provided by CMS to Facilities to be applied on top of known data.")
public class CmsOverlay implements CanBeEmpty {
  @JsonAlias("operating_status")
  @Valid
  Facility.OperatingStatus operatingStatus;

  @JsonAlias("detailed_services")
  List<@Valid DetailedService> detailedServices;

  @JsonProperty("system")
  HealthCareSystem healthCareSystem;

  /** Empty elements will be omitted from JSON serialization. */
  @JsonIgnore
  public boolean isEmpty() {
    return (operatingStatus() == null || operatingStatus().isEmpty())
        && ObjectUtils.isEmpty(detailedServices());
  }

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
    @JsonProperty("covidUrl")
    String covidUrl;

    @Schema(
        description = "VA Health connect phone number",
        example = "555-555-5555 x123",
        nullable = true)
    @JsonProperty("vaHealthConnectPhone")
    String healthConnectPhone;
  }
}
