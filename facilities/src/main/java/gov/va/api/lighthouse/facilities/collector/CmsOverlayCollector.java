package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;
import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.CMS_OVERLAY_SERVICE_NAME_COVID_19;

import com.google.common.collect.Streams;
import gov.va.api.lighthouse.facilities.CmsOverlayEntity;
import gov.va.api.lighthouse.facilities.CmsOverlayHelper;
import gov.va.api.lighthouse.facilities.CmsOverlayRepository;
import gov.va.api.lighthouse.facilities.DatamartCmsOverlay;
import gov.va.api.lighthouse.facilities.DatamartFacility.OperatingStatus;
import gov.va.api.lighthouse.facilities.api.cms.DetailedService;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CmsOverlayCollector {
  private final CmsOverlayRepository cmsOverlayRepository;

  /** Method for determining whether Covid service is contained within detailed services. */
  public static boolean containsCovidService(List<DetailedService> detailedServices) {
    return detailedServices != null
        && detailedServices.parallelStream()
            .anyMatch(f -> f.name().equals(CMS_OVERLAY_SERVICE_NAME_COVID_19));
  }

  private static <K, V>
      Collector<AbstractMap.SimpleEntry<K, V>, ?, HashMap<K, V>> convertOverlayToMap() {
    return Collectors.toMap(
        AbstractMap.SimpleEntry::getKey,
        AbstractMap.SimpleEntry::getValue,
        (prev, next) -> next,
        HashMap::new);
  }

  /** Load and return map of CMS overlays for each facility id. */
  public HashMap<String, DatamartCmsOverlay> loadAndUpdateCmsOverlays() {
    HashMap<String, DatamartCmsOverlay> overlays =
        Streams.stream(cmsOverlayRepository.findAll())
            // .parallel()
            .map(this::makeOverlayFromEntity)
            .filter(Objects::nonNull)
            .collect(convertOverlayToMap());
    log.info(
        "Loaded {} overlays from {} db entities", overlays.size(), cmsOverlayRepository.count());
    return overlays;
  }

  private AbstractMap.SimpleEntry<String, DatamartCmsOverlay> makeOverlayFromEntity(
      CmsOverlayEntity cmsOverlayEntity) {
    DatamartCmsOverlay overlay;
    try {
      overlay =
          DatamartCmsOverlay.builder()
              .operatingStatus(
                  CmsOverlayHelper.getOperatingStatus(cmsOverlayEntity.cmsOperatingStatus()))
              .detailedServices(
                  cmsOverlayEntity.cmsServices() != null
                      ? // updateServiceUrlPaths(
                      //  cmsOverlayEntity.id().toIdString(),
                      CmsOverlayHelper.getDetailedServices(cmsOverlayEntity.cmsServices()) // )
                      : null)
              .build();
      // Save updates made to overlay with Covid services
      final OperatingStatus operatingStatus = overlay.operatingStatus();
      final List<DetailedService> detailedServices = overlay.detailedServices();
      if (containsCovidService(detailedServices)) {
        cmsOverlayRepository.save(
            CmsOverlayEntity.builder()
                .id(cmsOverlayEntity.id())
                .cmsOperatingStatus(CmsOverlayHelper.serializeOperatingStatus(operatingStatus))
                .cmsServices(CmsOverlayHelper.serializeDetailedServices(detailedServices))
                .build());
        log.info(
            "CMS overlay updated for {} facility", sanitize(cmsOverlayEntity.id().toIdString()));
      }
    } catch (Exception e) {
      log.warn(
          "Could not create CmsOverlay from CmsOverlayEntity with id {}",
          cmsOverlayEntity.id().toIdString());
      return null;
    }
    return new AbstractMap.SimpleEntry<>(cmsOverlayEntity.id().toIdString(), overlay);
  }
}
