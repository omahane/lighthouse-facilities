package gov.va.api.lighthouse.facilities;

import gov.va.api.lighthouse.facilities.api.v0.CmsOverlay;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class CmsOverlayTransformerV0 {
  /** Transform version agnostic CMS overlay to V0 CMS overlay. */
  public static CmsOverlay toCmsOverlay(DatamartCmsOverlay dc) {
    return CmsOverlay.builder()
        .operatingStatus(FacilityTransformerV0.toFacilityOperatingStatus(dc.operatingStatus()))
        .detailedServices(DetailedServiceTransformerV0.toDetailedServices(dc.detailedServices()))
        .healthCareSystem(transformHeatlhCareSystem(dc.healthCareSystem()))
        .build();
  }

  /** Transform V0 CMS overlay to version agnostic CMS overlay. */
  public static DatamartCmsOverlay toVersionAgnostic(CmsOverlay overlay) {
    return DatamartCmsOverlay.builder()
        .operatingStatus(
            FacilityTransformerV0.toVersionAgnosticFacilityOperatingStatus(
                overlay.operatingStatus()))
        .detailedServices(
            DetailedServiceTransformerV0.toVersionAgnosticDetailedServices(
                overlay.detailedServices()))
        .healthCareSystem(transformHeatlhCareSystem(overlay.healthCareSystem()))
        .build();
  }

  /**
   * Transform DatamartCmsOverlay.HealthCareSystem object to version 0 CmsOverlay.HealthCareSystem
   * object
   */
  public static CmsOverlay.HealthCareSystem transformHeatlhCareSystem(
      DatamartCmsOverlay.HealthCareSystem healthCareSystem) {
    return (healthCareSystem != null)
        ? CmsOverlay.HealthCareSystem.builder()
            .name(healthCareSystem.name())
            .url(healthCareSystem.url())
            .covidUrl(healthCareSystem.covidUrl())
            .healthConnectPhone(healthCareSystem.healthConnectPhone())
            .build()
        : null;
  }

  /**
   * Transform version 0 CmsOverlay.HealthCareSystem object to DatamartCmsOverlay.HealthCareSystem
   * object
   */
  public static DatamartCmsOverlay.HealthCareSystem transformHeatlhCareSystem(
      CmsOverlay.HealthCareSystem healthCareSystem) {
    return (healthCareSystem != null)
        ? DatamartCmsOverlay.HealthCareSystem.builder()
            .name(healthCareSystem.name())
            .url(healthCareSystem.url())
            .covidUrl(healthCareSystem.covidUrl())
            .healthConnectPhone(healthCareSystem.healthConnectPhone())
            .build()
        : null;
  }
}
