package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.CmsOverlayHelper.convertOverlayToMap;
import static gov.va.api.lighthouse.facilities.CmsOverlayHelper.makeOverlayFromEntity;
import static java.util.Collections.emptySet;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.capitalize;

import com.google.common.collect.Streams;
import gov.va.api.health.autoconfig.logging.Loggable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@Validated
@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(value = "/internal/management", produces = "application/json")
public class InternalCmsOverlayController {
  private CmsOverlayRepository cmsOverlayRepository;

  @GetMapping(value = "/overlays")
  ResponseEntity<Map<String, DatamartCmsOverlay>> all() {
    HashMap<String, DatamartCmsOverlay> overlays =
        Streams.stream(cmsOverlayRepository.findAll())
            .map(entity -> makeOverlayFromEntity(entity))
            .filter(Objects::nonNull)
            .collect(convertOverlayToMap());
    return ResponseEntity.of(Optional.of(overlays));
  }

  @SneakyThrows
  private Set<String> serviceIds(@NonNull List<DatamartDetailedService> datamartDetailedServices) {
    return datamartDetailedServices.stream()
        .map(dds -> capitalize(dds.serviceInfo().serviceId()))
        .collect(Collectors.toSet());
  }

  @PostMapping(value = "/overlays")
  @Loggable(arguments = false)
  ResponseEntity<Void> upload(@RequestBody Map<String, DatamartCmsOverlay> collectedOverlays) {
    collectedOverlays.entrySet().stream()
        .forEach(
            mapEntry -> {
              final String facilityId = mapEntry.getKey();
              final DatamartCmsOverlay overlay = mapEntry.getValue();
              final CmsOverlayEntity cmsOverlayEntity =
                  CmsOverlayEntity.builder()
                      .id(FacilityEntity.Pk.fromIdString(facilityId))
                      .core(CmsOverlayHelper.serializeCore(overlay.core()))
                      .cmsOperatingStatus(
                          CmsOverlayHelper.serializeOperatingStatus(overlay.operatingStatus()))
                      .cmsServices(
                          CmsOverlayHelper.serializeDetailedServices(overlay.detailedServices()))
                      .overlayServices(
                          isNotEmpty(overlay.detailedServices())
                              ? serviceIds(overlay.detailedServices())
                              : emptySet())
                      .healthCareSystem(
                          CmsOverlayHelper.serializeHealthCareSystem(overlay.healthCareSystem()))
                      .build();
              cmsOverlayRepository.save(cmsOverlayEntity);
            });
    return ResponseEntity.ok().build();
  }
}
