package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.DatamartFacilitiesJacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.FacilityOverlayHelper.filterOutInvalidDetailedServices;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.function.BiFunction;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;

@Builder
@Value
public class FacilityOverlayV0
    implements BiFunction<HasFacilityPayload, ServiceNameAggregatorV0, Facility> {

  private static final ObjectMapper DATAMART_MAPPER = createMapper();

  @Override
  @SneakyThrows
  public Facility apply(
      @NonNull HasFacilityPayload entity, @NonNull ServiceNameAggregatorV0 serviceNameAggregator) {
    Facility facility =
        FacilityTransformerV0.toFacility(
            filterOutInvalidDetailedServices(
                DATAMART_MAPPER.readValue(entity.facility(), DatamartFacility.class)),
            serviceNameAggregator);
    return facility;
  }
}
