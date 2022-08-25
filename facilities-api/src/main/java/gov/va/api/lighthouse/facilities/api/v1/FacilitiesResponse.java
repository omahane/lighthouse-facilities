package gov.va.api.lighthouse.facilities.api.v1;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import gov.va.api.lighthouse.facilities.api.v1.serializers.DistanceSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.FacilitiesMetadataSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.FacilitiesResponseSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.ObjectUtils;

@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
@JsonSerialize(using = FacilitiesResponseSerializer.class)
public final class FacilitiesResponse implements CanBeEmpty {
  List<@Valid Facility> data;

  @Valid @NotNull PageLinks links;

  @Valid @NotNull FacilitiesMetadata meta;

  /** Empty elements will be omitted from JSON serialization. */
  @JsonIgnore
  public boolean isEmpty() {
    return ObjectUtils.isEmpty(data())
        && (links() == null || links().isEmpty())
        && (meta() == null || meta().isEmpty());
  }

  @Value
  @Builder
  @Schema
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
  @JsonSerialize(using = DistanceSerializer.class)
  public static final class Distance implements CanBeEmpty {
    @NotNull String id;

    @NotNull BigDecimal distance;

    /** Empty elements will be omitted from JSON serialization. */
    @JsonIgnore
    public boolean isEmpty() {
      return isBlank(id()) && ObjectUtils.isEmpty(distance());
    }
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
  @JsonSerialize(using = FacilitiesMetadataSerializer.class)
  public static final class FacilitiesMetadata implements CanBeEmpty {
    @Valid @NotNull Pagination pagination;

    List<@Valid @NotNull Distance> distances;

    /** Empty elements will be omitted from JSON serialization. */
    @JsonIgnore
    public boolean isEmpty() {
      return (pagination() == null || pagination().isEmpty()) && ObjectUtils.isEmpty(distances());
    }
  }
}
