package gov.va.api.lighthouse.facilities.api.cms;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import javax.validation.Valid;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DetailedServiceResponse {
  @Valid DetailedService data;
}
