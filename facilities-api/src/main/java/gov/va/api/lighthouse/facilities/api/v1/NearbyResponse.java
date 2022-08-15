package gov.va.api.lighthouse.facilities.api.v1;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import gov.va.api.lighthouse.facilities.api.v1.serializers.MetaSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.NearbyAttributesSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.NearbyResponseSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.NearbySerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

@Value
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
@JsonSerialize(using = NearbyResponseSerializer.class)
@JsonPropertyOrder({"data", "meta"})
@Schema(
    description =
        "Response which contains minimum and maximum time it takes " + "to reach facility.",
    nullable = true)
public final class NearbyResponse implements CanBeEmpty {
  List<@Valid @NotNull Nearby> data;

  @Schema(nullable = true)
  Meta meta;

  /** Empty elements will be omitted from JSON serialization. */
  @Override
  @JsonIgnore
  public boolean isEmpty() {
    return ObjectUtils.isEmpty(data()) && (meta() == null || meta().isEmpty());
  }

  public enum Type {
    @JsonProperty("nearby_facility")
    NearbyFacility
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
  @JsonSerialize(using = NearbyAttributesSerializer.class)
  @Schema(nullable = true)
  public static final class NearbyAttributes implements CanBeEmpty {
    @NotNull
    @Schema(description = "Minimum time to reach facility.", example = "10")
    Integer minTime;

    @NotNull
    @Schema(description = "Maximum time to reach facility.", example = "20")
    Integer maxTime;

    /** Empty elements will be omitted from JSON serialization. */
    @Override
    @JsonIgnore
    public boolean isEmpty() {
      return ObjectUtils.isEmpty(minTime()) && ObjectUtils.isEmpty(maxTime());
    }
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
  @JsonSerialize(using = MetaSerializer.class)
  @Schema(
      description = "JSON API-compliant object containing metadata about this response.",
      nullable = true)
  public static final class Meta implements CanBeEmpty {
    @Schema(
        description =
            "Version of the drive time band " + "data set used to generate this response.",
        example = "APR2021",
        nullable = true)
    String bandVersion;

    /** Empty elements will be omitted from JSON serialization. */
    @Override
    @JsonIgnore
    public boolean isEmpty() {
      return StringUtils.isBlank(bandVersion());
    }
  }

  @Value
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
  @JsonSerialize(using = NearbySerializer.class)
  @Schema(
      description = "JSON API-compliant object describing a nearby VA facility.",
      nullable = true)
  public static final class Nearby implements CanBeEmpty {
    @Schema(description = "Identifier for facility.", example = "vha_688")
    @NotNull
    String id;

    @Schema(description = "Type of facility.", example = "va_health_facility")
    @NotNull
    Type type;

    @Valid @NotNull NearbyAttributes attributes;

    /** Empty elements will be omitted from JSON serialization. */
    @Override
    @JsonIgnore
    public boolean isEmpty() {
      return StringUtils.isBlank(id())
          && ObjectUtils.isEmpty(type())
          && (attributes() == null || attributes().isEmpty());
    }
  }
}
