package gov.va.api.lighthouse.facilities.tests.v0;

import static gov.va.api.lighthouse.facilities.tests.FacilitiesRequest.facilitiesRequest;
import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;

import gov.va.api.lighthouse.facilities.api.v0.FacilitiesIdsResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RequiresFacilitiesExtension.class)
public class IdsIT {
  @Test
  void idsByType() {
    final String emptyRequest = "v0/ids";
    facilitiesRequest("application/json", emptyRequest, 200)
        .expectValid(FacilitiesIdsResponse.class);
    facilitiesRequest(null, emptyRequest, 200).expectValid(FacilitiesIdsResponse.class);

    final String request = emptyRequest + "?type=" + systemDefinition().ids().type();
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesIdsResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesIdsResponse.class);
  }
}
