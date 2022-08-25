package gov.va.api.lighthouse.facilities.api.v1;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import gov.va.api.lighthouse.facilities.api.v1.serializers.PageLinksSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
@JsonSerialize(using = PageLinksSerializer.class)
public final class PageLinks implements CanBeEmpty {
  @Schema(description = "/services/va_facilities/v1/facilities?id=vha_688", nullable = true)
  String related;

  @NotNull String self;

  @Schema(nullable = true)
  String first;

  @Schema(nullable = true)
  String prev;

  @Schema(nullable = true)
  String next;

  @Schema(nullable = true)
  String last;

  /** Empty elements will be omitted from JSON serialization. */
  @JsonIgnore
  public boolean isEmpty() {
    return isBlank(related())
        && isBlank(self())
        && isBlank(first())
        && isBlank(prev())
        && isBlank(next())
        && isBlank(last());
  }
}
