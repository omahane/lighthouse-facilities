package gov.va.api.lighthouse.facilities.api.v0;

import java.util.List;
import lombok.NonNull;

public class DetailedServiceUtils {
  public static DetailedService getDetailedService(@NonNull Facility.HealthService healthService) {
    return DetailedService.builder()
        .serviceId(healthService.serviceId())
        .name(healthService.name())
        .active(true)
        .changed(null)
        .descriptionFacility("Most advanced healthcare facility nationally.")
        .appointmentLeadIn("Your VA health care team will contact you if you...more text")
        .onlineSchedulingAvailable("True")
        .path("replaceable path here")
        .phoneNumbers(
            List.of(
                DetailedService.AppointmentPhoneNumber.builder()
                    .extension("123")
                    .label("Main phone")
                    .number("555-555-1212")
                    .type("tel")
                    .build()))
        .referralRequired("True")
        .walkInsAccepted("False")
        .build();
  }
}
