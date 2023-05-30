package gov.va.api.lighthouse.facilities.tests.v1;

import static gov.va.api.lighthouse.facilities.tests.FacilitiesRequest.facilitiesRequest;
import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;

import gov.va.api.lighthouse.facilities.api.v1.FacilityReadResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RequiresFacilitiesExtension.class)
public class ReadIT {
  private static String readPath() {
    return "v1/facilities/" + systemDefinition().ids().facility();
  }

  @Test
  void readById_json() {
    facilitiesRequest("application/json", readPath(), 200).expectValid(FacilityReadResponse.class);
  }

  @Test
  void readById_noAccept() {
    // default to application/json
    facilitiesRequest(null, readPath(), 200).expectValid(FacilityReadResponse.class);
  }
}
