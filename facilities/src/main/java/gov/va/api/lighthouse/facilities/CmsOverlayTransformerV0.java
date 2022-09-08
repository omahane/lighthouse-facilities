package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.DetailedServiceTransformerV0.toDetailedServices;
import static gov.va.api.lighthouse.facilities.DetailedServiceTransformerV0.toVersionAgnosticDetailedServices;
import static gov.va.api.lighthouse.facilities.FacilityTransformerV0.toFacilityOperatingStatus;
import static gov.va.api.lighthouse.facilities.FacilityTransformerV0.toVersionAgnosticFacilityOperatingStatus;

import gov.va.api.lighthouse.facilities.api.v0.CmsOverlay;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class CmsOverlayTransformerV0 {
  /** Transform version agnostic CMS overlay to V0 CMS overlay. */
  public static CmsOverlay toCmsOverlay(
      @NonNull DatamartCmsOverlay dc, @NonNull ServiceNameAggregatorV0 serviceNameAggregator) {
    return CmsOverlay.builder()
        .operatingStatus(toFacilityOperatingStatus(dc.operatingStatus()))
        .detailedServices(toDetailedServices(dc.detailedServices(), serviceNameAggregator))
        .healthCareSystem(toHeatlhCareSystem(dc.healthCareSystem()))
        .build();
  }

  /**
   * Transform DatamartCmsOverlay.HealthCareSystem object to version 0 CmsOverlay.HealthCareSystem
   * object
   */
  public static CmsOverlay.HealthCareSystem toHeatlhCareSystem(
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

  /** Transform V0 CMS overlay to version agnostic CMS overlay. */
  public static DatamartCmsOverlay toVersionAgnostic(CmsOverlay overlay) {
    return DatamartCmsOverlay.builder()
        .operatingStatus(toVersionAgnosticFacilityOperatingStatus(overlay.operatingStatus()))
        .detailedServices(toVersionAgnosticDetailedServices(overlay.detailedServices()))
        .healthCareSystem(toVersionAgnosticHeatlhCareSystem(overlay.healthCareSystem()))
        .build();
  }

  /**
   * Transform version 0 CmsOverlay.HealthCareSystem object to DatamartCmsOverlay.HealthCareSystem
   * object
   */
  public static DatamartCmsOverlay.HealthCareSystem toVersionAgnosticHeatlhCareSystem(
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
