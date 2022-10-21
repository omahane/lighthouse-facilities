package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.DetailedServiceTransformerV0.toDetailedServices;
import static gov.va.api.lighthouse.facilities.DetailedServiceTransformerV0.toVersionAgnosticDetailedServices;
import static gov.va.api.lighthouse.facilities.FacilityTransformerV0.toFacilityOperatingStatus;
import static gov.va.api.lighthouse.facilities.FacilityTransformerV0.toVersionAgnosticFacilityOperatingStatus;

import gov.va.api.lighthouse.facilities.api.v0.CmsOverlay;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class CmsOverlayTransformerV0 {
  /** Transform version agnostic CMS overlay to V0 CMS overlay. */
  public static CmsOverlay toCmsOverlay(DatamartCmsOverlay dc) {
    return CmsOverlay.builder()
        .core(toCore(dc.core()))
        .operatingStatus(toFacilityOperatingStatus(dc.operatingStatus()))
        .detailedServices(toDetailedServices(dc.detailedServices()))
        .healthCareSystem(toHeatlhCareSystem(dc.healthCareSystem()))
        .build();
  }

  /** Transform DatamartCmsOverlay.Core to version 0 CmsOverlay.Core. */
  public static CmsOverlay.Core toCore(DatamartCmsOverlay.Core core) {
    return (core != null)
        ? CmsOverlay.Core.builder().facilityUrl(core.facilityUrl()).build()
        : null;
  }

  /** Transform version agnostic HealthCareSystem object to version 0 object. */
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
        .core(toVersionAgnosticCore(overlay.core()))
        .operatingStatus(toVersionAgnosticFacilityOperatingStatus(overlay.operatingStatus()))
        .detailedServices(toVersionAgnosticDetailedServices(overlay.detailedServices()))
        .healthCareSystem(toVersionAgnosticHeatlhCareSystem(overlay.healthCareSystem()))
        .build();
  }

  /** Transform version 0 CmsOverlay.Core to DatamartCmsOverlay.Core. */
  public static DatamartCmsOverlay.Core toVersionAgnosticCore(CmsOverlay.Core core) {
    return (core != null)
        ? DatamartCmsOverlay.Core.builder().facilityUrl(core.facilityUrl()).build()
        : null;
  }

  /** Transform version 0 HealthCareSystem object to version agnostic object. */
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
