package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.FacilityTransformerV1.toFacilityOperatingStatus;
import static gov.va.api.lighthouse.facilities.FacilityTransformerV1.toVersionAgnosticFacilityOperatingStatus;

import gov.va.api.lighthouse.facilities.api.v1.CmsOverlay;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CmsOverlayTransformerV1 {
  /** Transform version agnostic CMS overlay to V1 CMS overlay. */
  public static CmsOverlay toCmsOverlay(DatamartCmsOverlay dc) {
    return CmsOverlay.builder()
        .operatingStatus(toFacilityOperatingStatus(dc.operatingStatus()))
        .detailedServices(DetailedServiceTransformerV1.toDetailedServices(dc.detailedServices()))
        .build();
  }

  /** Transform V1 CMS overlay to version agnostic CMS overlay. */
  public static DatamartCmsOverlay toVersionAgnostic(CmsOverlay overlay) {
    return DatamartCmsOverlay.builder()
        .operatingStatus(toVersionAgnosticFacilityOperatingStatus(overlay.operatingStatus()))
        .detailedServices(
            DetailedServiceTransformerV1.toVersionAgnosticDetailedServices(
                overlay.detailedServices()))
        .build();
  }
}
