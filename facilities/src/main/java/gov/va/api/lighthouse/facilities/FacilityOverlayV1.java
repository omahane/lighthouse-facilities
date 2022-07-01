package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.DatamartFacilitiesJacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.FacilityOverlayHelper.filterOutInvalidDetailedServices;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import java.util.function.Function;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;

@Data
@Builder
public class FacilityOverlayV1 implements Function<HasFacilityPayload, Facility> {
  private static final ObjectMapper DATAMART_MAPPER = createMapper();

  private String linkerUrl;

  @Override
  @SneakyThrows
  public Facility apply(HasFacilityPayload entity) {
    DatamartFacility df = DATAMART_MAPPER.readValue(entity.facility(), DatamartFacility.class);
    Facility facility = FacilityTransformerV1.toFacility(filterOutInvalidDetailedServices(df));

    facility
        .attributes()
        .parent(FacilityTransformerV1.toFacilityParent(df.attributes().parentId(), linkerUrl));

    return facility;
  }
}
