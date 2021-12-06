package gov.va.api.lighthouse.facilities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.va.api.lighthouse.facilities.DatamartFacility.OperatingStatus;
import gov.va.api.lighthouse.facilities.api.cms.DetailedService;
import java.util.List;
import javax.validation.Valid;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DatamartCmsOverlay {
  @Valid
  @JsonProperty("operating_status")
  OperatingStatus operatingStatus;

  @JsonProperty("detailed_services")
  List<@Valid DetailedService> detailedServices;
}
