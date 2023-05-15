package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.api.v0.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v0.DetailedService;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.Facility.FacilityAttributes;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class FacilityOverlayV0Test {
  private static final ObjectMapper MAPPER_V0 = FacilitiesJacksonConfigV0.createMapper();

  private static final ObjectMapper DATAMART_MAPPER =
      DatamartFacilitiesJacksonConfig.createMapper();

  private void assertAttributes(
      List<Facility.HealthService> expectedHealthServices, FacilityEntity entity) {
    Facility facility = FacilityOverlayV0.builder().build().apply(entity);
    assertThat(facility.attributes().services().health()).isEqualTo(expectedHealthServices);
  }

  @Test
  void covid19VaccineIsPopulatedWhenAvailable() {
    assertAttributes(
        List.of(Facility.HealthService.Covid19Vaccine),
        entity(
            facility(
                Facility.Services.builder()
                    .health(List.of(Facility.HealthService.Covid19Vaccine))
                    .build()),
            overlay(true)));
    assertAttributes(null, entity(facility(Facility.Services.builder().build()), overlay(false)));
  }

  private DetailedService createDetailedService(boolean cmsServiceActiveValue) {
    return DetailedService.builder()
        .serviceId(Facility.HealthService.Covid19Vaccine.serviceId())
        .name("Covid19Vaccine")
        .active(cmsServiceActiveValue)
        .changed("2021-02-04T22:36:49+00:00")
        .descriptionFacility("Facility description for vaccine availability for COVID-19")
        .appointmentLeadIn("Your VA health care team will contact you if you...more text")
        .onlineSchedulingAvailable("True")
        .path("\\/erie-health-care\\/locations\\/erie-va-medical-center\\/covid-19-vaccines")
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

  @SneakyThrows
  private FacilityEntity entity(Facility facility, CmsOverlay overlay) {
    Set<String> detailedServices = null;
    if (overlay != null) {
      detailedServices = new HashSet<>();
      for (DetailedService service : overlay.detailedServices()) {
        if (service.active()) {
          detailedServices.add(service.name());
        }
      }
    }
    return FacilityEntity.builder()
        .facility(
            DATAMART_MAPPER.writeValueAsString(FacilityTransformerV0.toVersionAgnostic(facility)))
        .cmsOperatingStatus(
            overlay == null ? null : MAPPER_V0.writeValueAsString(overlay.operatingStatus()))
        .overlayServices(overlay == null ? null : detailedServices)
        .cmsServices(
            overlay == null ? null : MAPPER_V0.writeValueAsString(overlay.detailedServices()))
        .build();
  }

  private Facility facility(Facility.Services services) {
    return Facility.builder()
        .attributes(FacilityAttributes.builder().services(services).build())
        .build();
  }

  private CmsOverlay overlay(boolean cmsServiceActiveValue) {
    return CmsOverlay.builder()
        .detailedServices(List.of(createDetailedService(cmsServiceActiveValue)))
        .build();
  }
}
