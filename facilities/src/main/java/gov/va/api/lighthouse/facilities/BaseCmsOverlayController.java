package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.updateServiceUrlPaths;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.api.cms.DetailedService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.SneakyThrows;

public abstract class BaseCmsOverlayController {
  @SneakyThrows
  protected List<DetailedService> findServicesToSave(
      CmsOverlayEntity cmsOverlayEntity,
      String id,
      List<DetailedService> detailedServices,
      ObjectMapper mapper) {
    final List<DetailedService> ds =
        (detailedServices == null) ? Collections.emptyList() : detailedServices;
    List<DetailedService> currentDetailedServices =
        cmsOverlayEntity.cmsServices() == null
            ? Collections.emptyList()
            : List.of(mapper.readValue(cmsOverlayEntity.cmsServices(), DetailedService[].class));
    final List<String> overlayServiceNames =
        ds.stream().map(DetailedService::name).collect(Collectors.toList());
    final List<DetailedService> finalDetailedServices = new ArrayList<>();
    finalDetailedServices.addAll(
        currentDetailedServices.parallelStream()
            .filter(
                currentDetailedService ->
                    !overlayServiceNames.contains(currentDetailedService.name()))
            .collect(Collectors.toList()));
    finalDetailedServices.addAll(
        ds.parallelStream().filter(d -> d.active()).collect(Collectors.toList()));
    updateServiceUrlPaths(id, finalDetailedServices);
    finalDetailedServices.sort(Comparator.comparing(DetailedService::name));
    return finalDetailedServices;
  }

  protected List<DetailedService> getActiveServicesFromOverlay(
      String id, List<DetailedService> detailedServices) {
    final List<DetailedService> activeServices = new ArrayList<>();
    if (detailedServices != null) {
      activeServices.addAll(
          detailedServices.parallelStream().filter(d -> d.active()).collect(Collectors.toList()));
    }
    updateServiceUrlPaths(id, activeServices);
    activeServices.sort(Comparator.comparing(DetailedService::name));
    return activeServices;
  }

  protected abstract Optional<CmsOverlayEntity> getExistingOverlayEntity(FacilityEntity.Pk pk);

  @SneakyThrows
  protected List<DetailedService> getOverlayDetailedServices(String id) {
    FacilityEntity.Pk pk = FacilityEntity.Pk.fromIdString(id);
    Optional<CmsOverlayEntity> existingOverlayEntity = getExistingOverlayEntity(pk);
    if (!existingOverlayEntity.isPresent()) {
      throw new ExceptionsUtils.NotFound(id);
    }
    CmsOverlayEntity cmsOverlayEntity = existingOverlayEntity.get();
    return CmsOverlayHelper.getDetailedServices(cmsOverlayEntity.cmsServices());
  }
}
