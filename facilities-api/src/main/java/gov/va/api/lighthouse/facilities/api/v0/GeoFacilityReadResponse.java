package gov.va.api.lighthouse.facilities.api.v0;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
@Schema(description = "Response that contains GeoFacility data.")
public final class GeoFacilityReadResponse {
  @NotNull GeoFacility.Type type;

  @Valid @NotNull GeoFacility.Geometry geometry;

  @Valid @NotNull GeoFacility.Properties properties;

  /** Create GeoFacilityReadResponse with the same type, geometry, and properties. */
  public static GeoFacilityReadResponse of(@NonNull GeoFacility facility) {
    return GeoFacilityReadResponse.builder()
        .type(facility.type())
        .geometry(facility.geometry())
        .properties(facility.properties())
        .build();
  }
}
