package gov.va.api.lighthouse.facilities.api.v1;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import gov.va.api.lighthouse.facilities.api.v1.serializers.CmsOverlaySerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.Valid;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

@Data
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
@JsonSerialize(using = CmsOverlaySerializer.class)
@Schema(description = "Data provided by CMS to Facilities to be applied on top of known data.")
public class CmsOverlay implements CanBeEmpty {
  @JsonAlias("operating_status")
  @Valid
  Facility.OperatingStatus operatingStatus;

  @JsonAlias("detailed_services")
  List<@Valid DetailedService> detailedServices;

  /** Empty elements will be omitted from JSON serialization. */
  @JsonIgnore
  public boolean isEmpty() {
    return (operatingStatus() == null || operatingStatus().isEmpty())
        && ObjectUtils.isEmpty(detailedServices());
  }
}
