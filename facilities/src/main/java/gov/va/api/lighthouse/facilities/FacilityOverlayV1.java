package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.DatamartFacilitiesJacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.FacilityOverlayHelper.filterOutInvalidDetailedServices;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import java.util.function.BiFunction;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;

@Data
@Builder
@Value
public class FacilityOverlayV1 implements BiFunction<HasFacilityPayload, String, Facility> {
  private static final ObjectMapper DATAMART_MAPPER = createMapper();

  private String linkerUrl;

  @Override
  @SneakyThrows
  public Facility apply(HasFacilityPayload entity, @NonNull String linkerUrl) {
    Facility facility =
        FacilityTransformerV1.toFacility(
            filterOutInvalidDetailedServices(
                DATAMART_MAPPER.readValue(entity.facility(), DatamartFacility.class)),
            linkerUrl);
    return facility;
  }
}
