package gov.va.api.lighthouse.facilities.api.v1;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import gov.va.api.lighthouse.facilities.api.v1.serializers.PaginationSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.ObjectUtils;

@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
@JsonSerialize(using = PaginationSerializer.class)
@JsonPropertyOrder({"currentPage", "perPage", "totalPages", "totalEntries"})
@Schema(
    description =
        "Pagination data reflecting response that has been seperated into discrete pages.")
public final class Pagination implements CanBeEmpty {
  @NotNull
  @Schema(description = "Current page of response.", example = "1")
  Integer currentPage;

  @NotNull
  @Schema(description = "Number of results per page.", example = "20")
  @JsonProperty("perPage")
  Integer entriesPerPage;

  @NotNull
  @Schema(description = "Total number of pages matching this query.", example = "250")
  Integer totalPages;

  @NotNull
  @Schema(description = "Total number of entries matching this query.", example = "2162")
  Integer totalEntries;

  /** Empty elements will be omitted from JSON serialization. */
  @Override
  @JsonIgnore
  public boolean isEmpty() {
    return ObjectUtils.isEmpty(currentPage())
        && ObjectUtils.isEmpty(entriesPerPage())
        && ObjectUtils.isEmpty(totalPages())
        && ObjectUtils.isEmpty(totalEntries());
  }
}
