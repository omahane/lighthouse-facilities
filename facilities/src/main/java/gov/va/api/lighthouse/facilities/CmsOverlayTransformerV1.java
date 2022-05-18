package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.DetailedServiceTransformerV1.toDetailedServices;
import static gov.va.api.lighthouse.facilities.DetailedServiceTransformerV1.toVersionAgnosticDetailedServices;
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
        .detailedServices(toDetailedServices(dc.detailedServices()))
        .healthCareSystem(transformHealthCareSystem(dc.healthCareSystem()))
        .build();
  }

  /** Transform V1 CMS overlay to version agnostic CMS overlay. */
  public static DatamartCmsOverlay toVersionAgnostic(CmsOverlay overlay) {
    return DatamartCmsOverlay.builder()
        .operatingStatus(toVersionAgnosticFacilityOperatingStatus(overlay.operatingStatus()))
        .detailedServices(toVersionAgnosticDetailedServices(overlay.detailedServices()))
        .healthCareSystem(transformHealthCareSystem(overlay.healthCareSystem()))
        .build();
  }

  /**
   * Transform DatamartCmsOverlay.HealthCareSystem object to version 0 CmsOverlay.HealthCareSystem
   * object
   */
  public static CmsOverlay.HealthCareSystem transformHealthCareSystem(
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
  public static DatamartCmsOverlay.HealthCareSystem transformHealthCareSystem(
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
