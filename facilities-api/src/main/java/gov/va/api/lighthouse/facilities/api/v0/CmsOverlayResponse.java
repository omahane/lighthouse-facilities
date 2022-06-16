package gov.va.api.lighthouse.facilities.api.v0;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
public class CmsOverlayResponse {
  @Builder.Default CmsOverlay overlay = null;
}
