package gov.va.api.lighthouse.facilities.api.v0;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.Valid;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Schema(description = "JSON API response containing expanded details for a service.")
@JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
public class DetailedServiceResponse {
  @Schema(description = "Object containing data on service details.")
  @Valid
  DetailedService data;
}
