package gov.va.api.lighthouse.facilities.api.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import gov.va.api.lighthouse.facilities.api.v1.serializers.CmsOverlayResponseSerializer;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
@JsonSerialize(using = CmsOverlayResponseSerializer.class)
public class CmsOverlayResponse implements CanBeEmpty {
  @Builder.Default CmsOverlay overlay = null;

  /** Empty elements will be omitted from JSON serialization. */
  @JsonIgnore
  public boolean isEmpty() {
    return overlay() == null || overlay().isEmpty();
  }
}
