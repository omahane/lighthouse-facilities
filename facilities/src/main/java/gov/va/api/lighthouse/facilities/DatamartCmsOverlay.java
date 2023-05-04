package gov.va.api.lighthouse.facilities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.va.api.lighthouse.facilities.DatamartFacility.OperatingStatus;
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
public class DatamartCmsOverlay {
  @Valid Core core;

  @Valid
  @JsonProperty("operating_status")
  OperatingStatus operatingStatus;

  @JsonProperty("detailed_services")
  List<@Valid DatamartDetailedService> detailedServices;

  @JsonProperty("system")
  HealthCareSystem healthCareSystem;

  public boolean containsHealthConnectPhoneNumber() {
    return ObjectUtils.isNotEmpty(healthCareSystem())
        && StringUtils.isNotEmpty(healthCareSystem().healthConnectPhone());
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
  public static class HealthCareSystem {
    String name;

    String url;

    @JsonProperty("covid_url")
    String covidUrl;

    @JsonProperty("va_health_connect_phone")
    String healthConnectPhone;
  }

  @Data
  @Builder
  @JsonAutoDetect(fieldVisibility = Visibility.ANY)
  public static class Core {
    @JsonProperty("facility_url")
    String facilityUrl;
  }
}
