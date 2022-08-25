package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.FacilityTransformerV0.toFacilityOperatingStatus;
import static gov.va.api.lighthouse.facilities.FacilityTransformerV0.toVersionAgnosticFacilityOperatingStatus;

import gov.va.api.lighthouse.facilities.api.v0.CmsOverlay;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class CmsOverlayTransformerV0 {
  /** Transform version agnostic CMS overlay to V0 CMS overlay. */
  public static CmsOverlay toCmsOverlay(DatamartCmsOverlay dc) {
    return CmsOverlay.builder()
        .operatingStatus(toFacilityOperatingStatus(dc.operatingStatus()))
        .detailedServices(DetailedServiceTransformerV0.toDetailedServices(dc.detailedServices()))
        .build();
  }

  /** Transform V0 CMS overlay to version agnostic CMS overlay. */
  public static DatamartCmsOverlay toVersionAgnostic(CmsOverlay overlay) {
    return DatamartCmsOverlay.builder()
        .operatingStatus(toVersionAgnosticFacilityOperatingStatus(overlay.operatingStatus()))
        .detailedServices(
            DetailedServiceTransformerV0.toVersionAgnosticDetailedServices(
                overlay.detailedServices()))
        .build();
  }
}
