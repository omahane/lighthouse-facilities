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
        .core(toCore(dc.core()))
        .operatingStatus(toFacilityOperatingStatus(dc.operatingStatus()))
        .detailedServices(toDetailedServices(dc.detailedServices()))
        .healthCareSystem(toHealthCareSystem(dc.healthCareSystem()))
        .build();
  }

  /** Transform DatamartCmsOverlay.Core to version 1 CmsOverlay.Core. */
  public static CmsOverlay.Core toCore(DatamartCmsOverlay.Core core) {
    return (core != null)
        ? CmsOverlay.Core.builder().facilityUrl(core.facilityUrl()).build()
        : null;
  }

  /**
   * Transform DatamartCmsOverlay.HealthCareSystem object to version 0 CmsOverlay.HealthCareSystem
   * object
   */
  public static CmsOverlay.HealthCareSystem toHealthCareSystem(
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

  /** Transform V1 CMS overlay to version agnostic CMS overlay. */
  public static DatamartCmsOverlay toVersionAgnostic(CmsOverlay overlay) {
    return DatamartCmsOverlay.builder()
        .core(toVersionAgnosticCore(overlay.core()))
        .operatingStatus(toVersionAgnosticFacilityOperatingStatus(overlay.operatingStatus()))
        .detailedServices(toVersionAgnosticDetailedServices(overlay.detailedServices()))
        .healthCareSystem(toVersionAgnosticHealthCareSystem(overlay.healthCareSystem()))
        .build();
  }

  /** Transform version 1 CmsOverlay.Core to DatamartCmsOverlay.Core. */
  public static DatamartCmsOverlay.Core toVersionAgnosticCore(CmsOverlay.Core core) {
    return (core != null)
        ? DatamartCmsOverlay.Core.builder().facilityUrl(core.facilityUrl()).build()
        : null;
  }

  /**
   * Transform version 0 CmsOverlay.HealthCareSystem object to DatamartCmsOverlay.HealthCareSystem
   * object
   */
  public static DatamartCmsOverlay.HealthCareSystem toVersionAgnosticHealthCareSystem(
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
