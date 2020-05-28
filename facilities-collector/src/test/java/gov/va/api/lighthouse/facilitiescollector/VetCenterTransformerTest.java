package gov.va.api.lighthouse.facilitiescollector;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import org.junit.Test;

public class VetCenterTransformerTest {
  @Test
  public void empty() {
    assertThat(
            VetCenterTransformer.builder()
                .vast(new VastEntity())
                .websites(emptyMap())
                .build()
                .toFacility())
        .isNull();

    assertThat(
            VetCenterTransformer.builder()
                .vast(VastEntity.builder().stationNumber("x").build())
                .websites(emptyMap())
                .build()
                .toFacility())
        .isEqualTo(Facility.builder().id("vc_x").type(Facility.Type.va_facilities).build());
  }
}
