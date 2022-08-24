package gov.va.api.lighthouse.facilities.api.v1;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class DetailedServiceResponseEmptyFieldsTest {

  @Test
  @SneakyThrows
  void isEmpty() {
    // Empty
    assertThat(DetailedServiceResponse.builder().build().isEmpty()).isTrue();
    // Not empty
    assertThat(
            DetailedServiceResponse.builder()
                .data(
                    DetailedService.builder()
                        .serviceInfo(
                            DetailedService.ServiceInfo.builder()
                                .serviceId(Facility.HealthService.Cardiology.serviceId())
                                .serviceType(Facility.HealthService.Cardiology.serviceType())
                                .build())
                        .build())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            DetailedServiceResponse.builder()
                .data(
                    DetailedService.builder()
                        .serviceInfo(
                            DetailedService.ServiceInfo.builder()
                                .serviceId(Facility.HealthService.Cardiology.serviceId())
                                .name("test")
                                .serviceType(Facility.HealthService.Cardiology.serviceType())
                                .build())
                        .build())
                .build()
                .isEmpty())
        .isFalse();
  }
}
