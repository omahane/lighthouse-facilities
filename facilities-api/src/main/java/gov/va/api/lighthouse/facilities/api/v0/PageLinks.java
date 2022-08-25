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
public final class PageLinks {
  @Schema(description = "/services/va_facilities/v0/facilities?id=vha_688", nullable = true)
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
}
