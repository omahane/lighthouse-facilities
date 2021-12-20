package gov.va.api.lighthouse.facilities.api.cms;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import gov.va.api.lighthouse.facilities.api.v1.PageLinks;
import gov.va.api.lighthouse.facilities.api.v1.Pagination;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DetailedServicesResponse {
  List<@Valid DetailedService> data;

  @Valid @NotNull PageLinks links;

  @Valid @NotNull DetailedServicesMetadata meta;

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class DetailedServicesMetadata {
    @Valid @NotNull Pagination pagination;
  }
}
