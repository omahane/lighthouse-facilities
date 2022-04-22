package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.updateServiceUrlPaths;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;

public abstract class BaseCmsOverlayController {
  /** Filter out unrecognized datamart detailed services from overlay. */
  @SneakyThrows
  protected DatamartCmsOverlay filterOutUnrecognizedServicesFromOverlay(
      @NonNull DatamartCmsOverlay overlay) {
    if (ObjectUtils.isNotEmpty(overlay.detailedServices())) {
      overlay.detailedServices(
          overlay.detailedServices().parallelStream()
              .filter(ds -> isRecognizedServiceId(ds.serviceInfo().serviceId()))
              .collect(Collectors.toList()));
    }
    return overlay;
  }

  @SneakyThrows
  protected List<DatamartDetailedService> findServicesToSave(
      CmsOverlayEntity cmsOverlayEntity,
      String id,
      List<DatamartDetailedService> detailedServices,
      ObjectMapper mapper) {
    final List<DatamartDetailedService> ds =
        (detailedServices == null) ? Collections.emptyList() : detailedServices;
    final List<String> overlayServiceIds =
        ds.parallelStream().map(dds -> dds.serviceInfo().serviceId()).collect(Collectors.toList());
    // Detailed services represented in pre-serviceInfo block format that have unrecognized service
    // names will have null serviceInfo block when deserialized.
    final List<DatamartDetailedService> currentDetailedServices =
        cmsOverlayEntity.cmsServices() == null
            ? Collections.emptyList()
            : List.of(
                    mapper.readValue(
                        cmsOverlayEntity.cmsServices(), DatamartDetailedService[].class))
                .parallelStream()
                .filter(dds -> dds.serviceInfo() != null)
                .collect(Collectors.toList());
    final List<DatamartDetailedService> finalDetailedServices = new ArrayList<>();
    finalDetailedServices.addAll(
        currentDetailedServices.parallelStream()
            .filter(
                currentDetailedService ->
                    !overlayServiceIds.contains(currentDetailedService.serviceInfo().serviceId()))
            .collect(Collectors.toList()));
    finalDetailedServices.addAll(
        ds.parallelStream().filter(d -> d.active()).collect(Collectors.toList()));
    updateServiceUrlPaths(id, finalDetailedServices);
    finalDetailedServices.sort(Comparator.comparing(dds -> dds.serviceInfo().serviceId()));
    return finalDetailedServices;
  }

  protected List<DatamartDetailedService> getActiveServicesFromOverlay(
      String id, List<DatamartDetailedService> detailedServices) {
    final List<DatamartDetailedService> activeServices = new ArrayList<>();
    if (detailedServices != null) {
      activeServices.addAll(
          detailedServices.parallelStream().filter(d -> d.active()).collect(Collectors.toList()));
    }
    updateServiceUrlPaths(id, activeServices);
    activeServices.sort(Comparator.comparing(dds -> dds.serviceInfo().serviceId()));
    return activeServices;
  }

  protected abstract Optional<CmsOverlayEntity> getExistingOverlayEntity(FacilityEntity.Pk pk);

  @SneakyThrows
  protected DatamartDetailedService getOverlayDetailedService(
      @NonNull String facilityId, @NonNull String serviceId) {
    List<DatamartDetailedService> detailedServices =
        getOverlayDetailedServices(facilityId).parallelStream()
            .filter(ds -> ds.serviceInfo().serviceId().equals(serviceId))
            .collect(Collectors.toList());
    return detailedServices.isEmpty() ? null : detailedServices.get(0);
  }

  @SneakyThrows
  protected List<DatamartDetailedService> getOverlayDetailedServices(@NonNull String facilityId) {
    FacilityEntity.Pk pk = FacilityEntity.Pk.fromIdString(facilityId);
    Optional<CmsOverlayEntity> existingOverlayEntity = getExistingOverlayEntity(pk);
    if (!existingOverlayEntity.isPresent()) {
      throw new ExceptionsUtils.NotFound(facilityId);
    }
    return CmsOverlayHelper.getDetailedServices(existingOverlayEntity.get().cmsServices());
  }

  /** Determine whether specified service id matches that for service. */
  protected abstract boolean isRecognizedServiceId(String serviceId);
}
