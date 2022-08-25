package gov.va.api.lighthouse.facilities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import gov.va.api.lighthouse.facilities.DatamartFacility.OperatingStatus;
import gov.va.api.lighthouse.facilities.deserializers.DatamartCmsOverlayDeserializer;
import java.util.List;
import javax.validation.Valid;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonDeserialize(using = DatamartCmsOverlayDeserializer.class)
public class DatamartCmsOverlay {
  @Valid
  @JsonProperty("operating_status")
  OperatingStatus operatingStatus;

  @JsonProperty("detailed_services")
  List<@Valid DatamartDetailedService> detailedServices;
}
