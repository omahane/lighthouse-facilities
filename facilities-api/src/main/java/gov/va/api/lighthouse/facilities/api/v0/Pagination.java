package gov.va.api.lighthouse.facilities.api.v0;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
public final class Pagination {
  @NotNull
  @Schema(example = "1")
  @JsonProperty("current_page")
  Integer currentPage;

  @NotNull
  @Schema(example = "10")
  @JsonProperty("per_page")
  Integer entriesPerPage;

  @NotNull
  @Schema(example = "217")
  @JsonProperty("total_pages")
  Integer totalPages;

  @NotNull
  @Schema(example = "2162")
  @JsonProperty("total_entries")
  Integer totalEntries;
}
