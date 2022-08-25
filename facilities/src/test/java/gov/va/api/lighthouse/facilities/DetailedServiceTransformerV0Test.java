package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class DetailedServiceTransformerV0Test {
  @Test
  void datamartDetailedServiceWithEmptyAttributesRoundTrip() {
    DatamartDetailedService datamartDetailedService =
        DatamartDetailedServicesTestUtils.datamartDetailedServiceWithEmptyAttributes();
    assertThat(
            DetailedServiceTransformerV0.toVersionAgnosticDetailedService(
                DetailedServiceTransformerV0.toDetailedService(datamartDetailedService)))
        .usingRecursiveComparison()
        .isEqualTo(datamartDetailedService);
  }

  @Test
  void datamartDetailedServiceWithNullAttributesRoundTrip() {
    DatamartDetailedService datamartDetailedService =
        DatamartDetailedServicesTestUtils.datamartDetailedServiceWithNullAttributes();
    assertThat(
            DetailedServiceTransformerV0.toVersionAgnosticDetailedService(
                DetailedServiceTransformerV0.toDetailedService(datamartDetailedService)))
        .usingRecursiveComparison()
        .isEqualTo(datamartDetailedService);
  }

  @Test
  public void roundTripTransformation() {
    List<DatamartDetailedService> datamartDetailedServices =
        DatamartDetailedServicesTestUtils.datamartDetailedServices(true);
    assertThat(
            DetailedServiceTransformerV0.toVersionAgnosticDetailedServices(
                DetailedServiceTransformerV0.toDetailedServices(datamartDetailedServices)))
        .containsAll(datamartDetailedServices);
  }

  @Test
  void toDetailedServiceNullArgs() {
    assertThrows(
        NullPointerException.class, () -> DetailedServiceTransformerV0.toDetailedService(null));
  }

  @Test
  void toDetailedServicesEmptyArg() {
    assertThat(DetailedServiceTransformerV0.toDetailedServices(new ArrayList<>())).isEmpty();
  }

  @Test
  void toDetailedServicesNullArgs() {
    assertThat(DetailedServiceTransformerV0.toDetailedServices(null)).isEqualTo(null);
  }

  @Test
  void toVersionAgnosticDetailedServiceNullArgs() {
    assertThrows(
        NullPointerException.class,
        () -> DetailedServiceTransformerV0.toVersionAgnosticDetailedService(null));
  }

  @Test
  void toVersionAgnosticDetailedServicesEmptyArg() {
    assertThat(DetailedServiceTransformerV0.toVersionAgnosticDetailedServices(new ArrayList<>()))
        .isEmpty();
  }

  @Test
  void toVersionAgnosticDetailedServicesNullArgs() {
    assertThat(DetailedServiceTransformerV0.toVersionAgnosticDetailedServices(null))
        .isEqualTo(null);
  }
}
