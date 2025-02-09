package gov.va.api.lighthouse.facilities;

import java.util.Arrays;
import lombok.NonNull;

abstract class BaseVersionedTransformer {
  protected static boolean checkHealthServiceNameChange(
      @NonNull DatamartFacility.Service<DatamartFacility.HealthService> healthService) {
    return healthService.serviceType().equals(DatamartFacility.HealthService.MentalHealth)
        || healthService.serviceType().equals(DatamartFacility.HealthService.Dental);
  }

  protected static boolean checkHealthServiceNameChange(
      @NonNull gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService healthService) {
    return healthService.equals(
            gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.MentalHealthCare)
        || healthService.equals(
            gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.DentalServices);
  }

  protected static boolean checkHealthServiceNameChange(
      @NonNull
          gov.va.api.lighthouse.facilities.api.v1.Facility.Service<
                  gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService>
              healthService) {
    return healthService
            .serviceType()
            .equals(gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealth)
        || healthService
            .serviceType()
            .equals(gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Dental);
  }

  protected static boolean containsValueOfName(@NonNull Enum<?>[] values, @NonNull String name) {
    return Arrays.stream(values).parallel().anyMatch(e -> e.name().equals(name));
  }
}
