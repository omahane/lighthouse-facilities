package gov.va.api.lighthouse.facilities.api.v0;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
@Schema(description = "Links for first page, next page, " + "previous and last page of response.")
public final class PageLinks {
  @Schema(
      description = "Link to related pages of response.",
      example = "https://api.va.gov/services/va_facilities/v0/facilities?id=vha_688",
      nullable = true)
  String related;

  @Schema(
      description = "Link to current page of response.",
      example =
          "https://api.va.gov/services/va_facilities/v0/facilities?type=health&page=1&per_page=1000",
      nullable = true)
  @NotNull
  String self;

  @Schema(
      description = "Link to first page of response.",
      example =
          "https://api.va.gov/services/va_facilities/v0/facilities?type=health&page=1&per_page=1000",
      nullable = true)
  String first;

  @Schema(
      description = "Link to previous page of response.",
      example =
          "https://api.va.gov/services/va_facilities/v0/facilities?type=health&page=1&per_page=1000",
      nullable = true)
  String prev;

  @Schema(
      description = "Link to next page of response.",
      example =
          "https://api.va.gov/services/va_facilities/v0/facilities?type=health&page=2&per_page=1000",
      nullable = true)
  String next;

  @Schema(
      description = "Link to last page of response.",
      example =
          "https://api.va.gov/services/va_facilities/v0/facilities?type=health&page=4&per_page=1000",
      nullable = true)
  String last;
}
