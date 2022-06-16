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

public abstract class BaseCmsOverlayController {
  @SneakyThrows
  protected List<DatamartDetailedService> findServicesToSave(
      CmsOverlayEntity cmsOverlayEntity,
      String id,
      List<DatamartDetailedService> detailedServices,
      ObjectMapper mapper) {
    final List<DatamartDetailedService> ds =
        (detailedServices == null) ? Collections.emptyList() : detailedServices;
    List<DatamartDetailedService> currentDetailedServices =
        cmsOverlayEntity.cmsServices() == null
            ? Collections.emptyList()
            : List.of(
                mapper.readValue(cmsOverlayEntity.cmsServices(), DatamartDetailedService[].class));
    final List<String> overlayServiceNames =
        ds.stream().map(DatamartDetailedService::name).collect(Collectors.toList());
    final List<DatamartDetailedService> finalDetailedServices = new ArrayList<>();
    finalDetailedServices.addAll(
        currentDetailedServices.parallelStream()
            .filter(
                currentDetailedService ->
                    !overlayServiceNames.contains(currentDetailedService.name()))
            .collect(Collectors.toList()));
    finalDetailedServices.addAll(
        ds.parallelStream().filter(d -> d.active()).collect(Collectors.toList()));
    updateServiceUrlPaths(id, finalDetailedServices);
    finalDetailedServices.sort(Comparator.comparing(DatamartDetailedService::name));
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
    activeServices.sort(Comparator.comparing(DatamartDetailedService::name));
    return activeServices;
  }

  protected abstract Optional<CmsOverlayEntity> getExistingOverlayEntity(FacilityEntity.Pk pk);

  @SneakyThrows
  protected DatamartDetailedService getOverlayDetailedService(
      @NonNull String facilityId, @NonNull String serviceId) {
    List<DatamartDetailedService> detailedServices =
        getOverlayDetailedServices(facilityId).parallelStream()
            .filter(ds -> ds.name().equalsIgnoreCase(serviceId))
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
}
