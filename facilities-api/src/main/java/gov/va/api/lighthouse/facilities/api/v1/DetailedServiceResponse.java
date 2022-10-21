package gov.va.api.lighthouse.facilities.api.v1;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import gov.va.api.lighthouse.facilities.api.v1.serializers.DetailedServiceResponseSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.Valid;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
@JsonSerialize(using = DetailedServiceResponseSerializer.class)
@Schema(description = "JSON API response containing expanded details for a service.")
public class DetailedServiceResponse implements CanBeEmpty {
  @Schema(description = "Object containing data on service details.")
  @Valid
  DetailedService data;

  /** Empty elements will be omitted from JSON serialization. */
  @Override
  @JsonIgnore
  public boolean isEmpty() {
    return data() == null || data().isEmpty();
  }
}
