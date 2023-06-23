package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.CmsOverlayFixture.overlay;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class InternalCmsOverlayControllerTest {
  @Autowired CmsOverlayRepository cmsOverlayRepository;

  private Map<String, DatamartCmsOverlay> allOverlays(boolean isActive) {
    final Map<String, DatamartCmsOverlay> overlays = new HashMap<>();
    for (int stationNumber = 100; stationNumber <= 110; stationNumber++) {
      overlays.put("vha_" + stationNumber, overlay(isActive));
    }
    return overlays;
  }

  private InternalCmsOverlayController internalController() {
    return InternalCmsOverlayController.builder()
        .cmsOverlayRepository(cmsOverlayRepository)
        .build();
  }

  @Test
  void uploadOverlaysAndGetAll() {
    // Upload CMS overlays
    assertThat(internalController().upload(allOverlays(true)))
        .usingRecursiveComparison()
        .isEqualTo(ResponseEntity.ok().build());
    // Get all CMS overlays
    assertThat(internalController().all())
        .usingRecursiveComparison()
        .isEqualTo(ResponseEntity.of(Optional.of(allOverlays(false))));
  }
}
