package gov.va.api.lighthouse.facilities;

import gov.va.api.lighthouse.facilities.api.TypedService;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FacilityServicesUtils {
  /** Populate source and service id for specified facility services entity. */
  public static <T extends TypedService> FacilityServicesEntity populate(
      @NonNull FacilityServicesEntity facilityServicesRecord,
      @NonNull DatamartFacility.Service<T> facilityService) {
    facilityServicesRecord.serviceId(facilityService.serviceId());
    facilityServicesRecord.source(facilityService.source());
    return facilityServicesRecord;
  }
}
