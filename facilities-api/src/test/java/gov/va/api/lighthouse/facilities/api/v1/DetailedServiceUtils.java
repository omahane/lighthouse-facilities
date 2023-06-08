package gov.va.api.lighthouse.facilities.api.v1;

import gov.va.api.lighthouse.facilities.api.TypedService;
import java.util.Arrays;
import java.util.List;
import lombok.NonNull;

public class DetailedServiceUtils {
  public static DetailedService getDetailedService(@NonNull TypedService serviceType) {
    return DetailedService.builder()
        .serviceInfo(
            DetailedService.ServiceInfo.builder()
                .serviceId(serviceType.serviceId())
                .name(serviceType.name())
                .serviceType(serviceType.serviceType())
                .build())
        .active(true)
        .appointmentLeadIn("Your VA health care team will contact you if you...more text")
        .path("replaceable path here")
        .phoneNumbers(
            List.of(
                DetailedService.AppointmentPhoneNumber.builder()
                    .extension("123")
                    .label("Main phone")
                    .number("555-555-1212")
                    .type("tel")
                    .build()))
        .serviceLocations(
            List.of(
                DetailedService.DetailedServiceLocation.builder()
                    .officeName("ENT Clinic")
                    .serviceAddress(
                        DetailedService.DetailedServiceAddress.builder()
                            .buildingNameNumber("Baxter Building")
                            .wingFloorOrRoomNumber("Wing East")
                            .address1("122 Main St.")
                            .address2("West Side Apt# 227")
                            .city("Rochester")
                            .state("NY")
                            .zipCode("14623-1345")
                            .countryCode("US")
                            .build())
                    .phoneNumbers(
                        List.of(
                            DetailedService.AppointmentPhoneNumber.builder()
                                .extension("567")
                                .label("Alt phone")
                                .number("556-565-1119")
                                .type("tel")
                                .build()))
                    .emailContacts(
                        List.of(
                            DetailedService.DetailedServiceEmailContact.builder()
                                .emailAddress("georgea@va.gov")
                                .emailLabel("George Anderson")
                                .build()))
                    .serviceHours(
                        DetailedService.DetailedServiceHours.builder()
                            .monday("8:30AM-7:00PM")
                            .tuesday("8:30AM-7:00PM")
                            .wednesday("8:30AM-7:00PM")
                            .thursday("8:30AM-7:00PM")
                            .friday("8:30AM-7:00PM")
                            .saturday("8:30AM-7:00PM")
                            .sunday("CLOSED")
                            .build())
                    .additionalHoursInfo("")
                    .referralRequired("True")
                    .walkInsAccepted("False")
                    .onlineSchedulingAvailable("True")
                    .build()))
        .build();
  }

  private static DetailedService.ServiceType getServiceTypeForServiceName(String serviceName) {
    return Arrays.stream(Facility.HealthService.values())
            .parallel()
            .anyMatch(hs -> hs.name().equals(serviceName))
        ? DetailedService.ServiceType.Health
        : Arrays.stream(Facility.BenefitsService.values())
                .parallel()
                .anyMatch(bs -> bs.name().equals(serviceName))
            ? DetailedService.ServiceType.Benefits
            : Arrays.stream(Facility.OtherService.values())
                    .parallel()
                    .anyMatch(os -> os.name().equals(serviceName))
                ? DetailedService.ServiceType.Other
                : // Default to Health service type
                DetailedService.ServiceType.Health;
  }
}
