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
@Schema(
    description =
        "Pagination data reflecting response that has been seperated into discrete pages.")
public final class Pagination {
  @NotNull
  @Schema(description = "Current page of response.", example = "1")
  @JsonProperty("current_page")
  Integer currentPage;

  @NotNull
  @Schema(description = "Number of results per page.", example = "20")
  @JsonProperty("per_page")
  Integer entriesPerPage;

  @NotNull
  @Schema(description = "Total number of pages matching this query.", example = "250")
  @JsonProperty("total_pages")
  Integer totalPages;

  @NotNull
  @Schema(description = "Total number of entries matching this query.", example = "2162")
  @JsonProperty("total_entries")
  Integer totalEntries;
}
