package gov.va.api.lighthouse.facilities.api.v0;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import gov.va.api.lighthouse.facilities.api.v1.PageLinks;
import gov.va.api.lighthouse.facilities.api.v1.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
public class DetailedServicesResponse {
  List<@Valid DetailedService> data;

  @Valid @NotNull PageLinks links;

  @Valid @NotNull DetailedServicesMetadata meta;

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
  @Schema(
      description =
          "\t\n"
              + "JSON API-compliant object containing metadata about "
              + "detailed service response")
  public static final class DetailedServicesMetadata {
    @Schema(
        description =
            "Object containing pagination data reflecting response"
                + " that has been seperated into pages.")
    @Valid
    @NotNull
    Pagination pagination;
  }
}
