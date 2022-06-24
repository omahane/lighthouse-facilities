package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.CMS_OVERLAY_SERVICE_NAME_COVID_19;
import static gov.va.api.lighthouse.facilities.collector.CovidServiceUpdater.updateServiceUrlPaths;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import java.util.ArrayList;
import java.util.Arrays;
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
    if (!isValidService(serviceId)) {
      throw new ExceptionsUtils.InvalidParameter("service_id", serviceId);
    }
    List<DatamartDetailedService> detailedServices =
        getOverlayDetailedServices(facilityId).parallelStream()
            .filter(ds -> ds.name().equalsIgnoreCase(serviceId))
            .collect(Collectors.toList());
    if (detailedServices.isEmpty()) {
      throw new ExceptionsUtils.NotFound(facilityId, serviceId);
    }
    return detailedServices.get(0);
  }

  @SneakyThrows
  protected List<DatamartDetailedService> getOverlayDetailedServices(@NonNull String facilityId) {
    FacilityEntity.Pk pk;
    try {
      pk = FacilityEntity.Pk.fromIdString(facilityId);
    } catch (IllegalArgumentException e) {
      throw new ExceptionsUtils.InvalidParameter("facility_id", facilityId);
    }
    Optional<CmsOverlayEntity> existingOverlayEntity = getExistingOverlayEntity(pk);
    if (!existingOverlayEntity.isPresent()) {
      throw new ExceptionsUtils.NotFound(facilityId);
    }
    return CmsOverlayHelper.getDetailedServices(existingOverlayEntity.get().cmsServices());
  }

  protected boolean isValidService(String serviceId) {
    if (serviceId.equalsIgnoreCase(CMS_OVERLAY_SERVICE_NAME_COVID_19)
        || serviceId.equalsIgnoreCase(CMS_OVERLAY_SERVICE_NAME_COVID_19.replace(" ", "%20"))) {
      return true;
    }
    return Arrays.stream(Facility.HealthService.values())
        .anyMatch(e -> e.name().equalsIgnoreCase(serviceId));
  }
}
