package gov.va.api.lighthouse.facilities.api.v0;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
@Schema(
    description = "JSON API-compliant response object describing " + "one or more VA facilities")
public final class FacilitiesResponse {
  List<@Valid Facility> data;

  @Valid @NotNull PageLinks links;

  @Valid @NotNull FacilitiesMetadata meta;

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
  @Schema(
      description =
          "Distance to facility in miles "
              + "using decimal format. "
              + "Used when querying for facilities proximal to a location. ",
      example = "54.13")
  public static final class Distance {
    @Schema(description = "Identifier of facility.", example = "vc_0101V")
    @NotNull
    String id;

    @Schema(description = "Distance to facility in decimal format.", example = "54.13")
    @NotNull
    BigDecimal distance;
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
  @Schema(
      description =
          "Metadata representation for data in response. "
              + "This metadata includes paginated items "
              + "that allow user to see the current page, "
              + "objects perPage, total pages "
              + "and total entries.")
  public static final class FacilitiesMetadata {
    @Valid @NotNull Pagination pagination;

    List<@Valid @NotNull Distance> distances;
  }
}
