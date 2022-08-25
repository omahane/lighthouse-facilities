package gov.va.api.lighthouse.facilities;

import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.DatamartFacility.HealthService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class CmsOverlayHelperTest {
  @SneakyThrows
  private void roundTripDetailedHealthService(HealthService healthService) {
    roundTripDetailedHealthServices(List.of(healthService));
  }

  @SneakyThrows
  private void roundTripDetailedHealthServices(List<HealthService> healthServices) {
    List<DatamartDetailedService> detailedServices =
        healthServices.stream()
            .map(
                hs ->
                    DatamartDetailedService.builder()
                        .serviceId(uncapitalize(hs.name()))
                        .name(hs.name())
                        .build())
            .collect(Collectors.toList());
    assertThat(
            CmsOverlayHelper.getDetailedServices(
                CmsOverlayHelper.serializeDetailedServices(detailedServices)))
        .usingRecursiveComparison()
        .isEqualTo(detailedServices);
  }

  @Test
  @SneakyThrows
  void roundTripDetailedServiceAudiologyHealthService() {
    roundTripDetailedHealthService(HealthService.Audiology);
  }

  @Test
  @SneakyThrows
  void roundTripDetailedServiceCardiologyHealthService() {
    roundTripDetailedHealthService(HealthService.Cardiology);
  }

  @Test
  @SneakyThrows
  void roundTripDetailedServiceCaregiverSupportHealthService() {
    roundTripDetailedHealthService(HealthService.CaregiverSupport);
  }

  @Test
  @SneakyThrows
  void roundTripDetailedServiceCovid19VaccineHealthService() {
    roundTripDetailedHealthService(HealthService.Covid19Vaccine);
  }

  @Test
  @SneakyThrows
  void roundTripDetailedServiceDentalHealthService() {
    roundTripDetailedHealthService(HealthService.Dental);
  }

  @Test
  @SneakyThrows
  void roundTripDetailedServiceDermatologyHealthService() {
    roundTripDetailedHealthService(HealthService.Dermatology);
  }

  @Test
  @SneakyThrows
  void roundTripDetailedServiceEmergencyCareHealthService() {
    roundTripDetailedHealthService(HealthService.EmergencyCare);
  }

  @Test
  @SneakyThrows
  void roundTripDetailedServiceGastroenterologyHealthService() {
    roundTripDetailedHealthService(HealthService.Gastroenterology);
  }

  @Test
  @SneakyThrows
  void roundTripDetailedServiceGynecologyHealthService() {
    roundTripDetailedHealthService(HealthService.Gynecology);
  }

  @Test
  @SneakyThrows
  void roundTripDetailedServiceMentalHealthService() {
    roundTripDetailedHealthService(HealthService.MentalHealth);
  }

  @Test
  @SneakyThrows
  void roundTripDetailedServiceNutritionHealthService() {
    roundTripDetailedHealthService(HealthService.Nutrition);
  }

  @Test
  @SneakyThrows
  void roundTripDetailedServiceOphthalmologyHealthService() {
    roundTripDetailedHealthService(HealthService.Ophthalmology);
  }

  @Test
  @SneakyThrows
  void roundTripDetailedServiceOptometryHealthService() {
    roundTripDetailedHealthService(HealthService.Optometry);
  }

  @Test
  @SneakyThrows
  void roundTripDetailedServiceOrthopedicsHealthService() {
    roundTripDetailedHealthService(HealthService.Orthopedics);
  }

  @Test
  @SneakyThrows
  void roundTripDetailedServicePodiatryHealthService() {
    roundTripDetailedHealthService(HealthService.Podiatry);
  }

  @Test
  @SneakyThrows
  void roundTripDetailedServicePrimaryCareHealthService() {
    roundTripDetailedHealthService(HealthService.PrimaryCare);
  }

  @Test
  @SneakyThrows
  void roundTripDetailedServiceSpecialtyCareHealthService() {
    roundTripDetailedHealthService(HealthService.SpecialtyCare);
  }

  @Test
  @SneakyThrows
  void roundTripDetailedServiceUrgentCareHealthService() {
    roundTripDetailedHealthService(HealthService.UrgentCare);
  }

  @Test
  @SneakyThrows
  void roundTripDetailedServiceUrologyHealthService() {
    roundTripDetailedHealthService(HealthService.Urology);
  }

  @Test
  @SneakyThrows
  void roundTripDetailedServiceWomensHealthHealthService() {
    roundTripDetailedHealthService(HealthService.WomensHealth);
  }

  @Test
  @SneakyThrows
  void roundTripDetailedServices() {
    roundTripDetailedHealthServices(
        List.of(
            HealthService.Audiology,
            HealthService.Cardiology,
            HealthService.CaregiverSupport,
            HealthService.Covid19Vaccine,
            HealthService.Dental,
            HealthService.Dermatology,
            HealthService.EmergencyCare,
            HealthService.Gastroenterology,
            HealthService.Gynecology,
            HealthService.MentalHealth,
            HealthService.Ophthalmology,
            HealthService.Optometry,
            HealthService.Orthopedics,
            HealthService.Nutrition,
            HealthService.Podiatry,
            HealthService.PrimaryCare,
            HealthService.SpecialtyCare,
            HealthService.UrgentCare,
            HealthService.Urology,
            HealthService.WomensHealth));
  }
}
