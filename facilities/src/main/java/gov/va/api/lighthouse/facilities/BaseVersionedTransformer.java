package gov.va.api.lighthouse.facilities;

import java.util.Arrays;
import lombok.NonNull;

abstract class BaseVersionedTransformer {
  protected static boolean checkHealthServiceNameChange(
      @NonNull DatamartFacility.HealthService healthService) {
    return healthService.equals(DatamartFacility.HealthService.MentalHealth)
        || healthService.equals(DatamartFacility.HealthService.Dental);
  }

  protected static boolean checkHealthServiceNameChange(
      @NonNull gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService healthService) {
    return healthService.equals(
            gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.MentalHealthCare)
        || healthService.equals(
            gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.DentalServices);
  }

  protected static boolean containsValueOfName(@NonNull Enum<?>[] values, @NonNull String name) {
    return Arrays.stream(values).parallel().anyMatch(e -> e.name().equals(name));
  }
}
