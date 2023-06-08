package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class DatamartFacilityTest {

  @Test
  void exceptions() {
    // Benefits Service
    assertThatThrownBy(() -> DatamartFacility.BenefitsService.fromString(null))
        .isInstanceOf(NullPointerException.class);
    // Health Service
    assertThatThrownBy(() -> DatamartFacility.HealthService.fromString(null))
        .isInstanceOf(NullPointerException.class);
    // Other Service
    assertThatThrownBy(() -> DatamartFacility.OtherService.fromString(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void fromString() {
    // Benefits Service
    assertThat(DatamartFacility.BenefitsService.fromString("Pensions"))
        .isEqualTo(DatamartFacility.BenefitsService.Pensions);
    assertThat(DatamartFacility.BenefitsService.fromString("pensions"))
        .isEqualTo(DatamartFacility.BenefitsService.Pensions);

    // Health Service
    assertThat(DatamartFacility.HealthService.fromString("Cardiology"))
        .isEqualTo(DatamartFacility.HealthService.Cardiology);
    assertThat(DatamartFacility.HealthService.fromString("cardiology"))
        .isEqualTo(DatamartFacility.HealthService.Cardiology);

    // Other Service
    assertThat(DatamartFacility.OtherService.fromString("OnlineScheduling"))
        .isEqualTo(DatamartFacility.OtherService.OnlineScheduling);
    assertThat(DatamartFacility.OtherService.fromString("onlineScheduling"))
        .isEqualTo(DatamartFacility.OtherService.OnlineScheduling);
  }
}
