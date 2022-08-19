package gov.va.api.lighthouse.facilities.collector;

import com.google.common.collect.ImmutableMap;
import gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService;
import gov.va.api.lighthouse.facilities.DatamartFacility.HealthService;
import gov.va.api.lighthouse.facilities.DatamartFacility.OtherService;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Service name data aggregator. */
@Slf4j
@Component
public class ServiceNameAggregator {
  private final AccessToCareMapper atcToCareMapper;

  private final CmsOverlayMapper cmsOverlayMapper;

  private final Map<String, String> serviceIdToServiceNameAggregate;

  /** Aggregate CMS and ATC service name data based on established ruleset. */
  public ServiceNameAggregator(
      @Autowired @NonNull AccessToCareMapper atcToCareMapper,
      @Autowired @NonNull CmsOverlayMapper cmsOverlayMapper) {
    this.atcToCareMapper = atcToCareMapper;
    this.cmsOverlayMapper = cmsOverlayMapper;
    serviceIdToServiceNameAggregate = formAggregate();
  }

  /**
   * Service name populated based on following rules:
   * <li>If given service shows active from CMS but not in ATC. Service name should be populated
   *     from CMS. (No data manipulation needed)
   * <li>If given service shows active from ATC, but not in CMS. In unlikely event that this should
   *     occur, service name should be populated from ATC. CMS is the recognized source of truth.
   * <li>If given service is populated in both ATC and CMS. Service name should be populated from
   *     CMS data. (No data manipulation needed)
   */
  private Map<String, String> formAggregate() {
    final Map<String, String> serviceIdToServiceName = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    populateAggregateMapping(serviceIdToServiceName, cmsOverlayMapper.serviceIds());
    populateAggregateMapping(serviceIdToServiceName, atcToCareMapper.serviceIds());
    return ImmutableMap.copyOf(serviceIdToServiceName);
  }

  private void populateAggregateMapping(
      @NonNull Map<String, String> serviceIdToServiceName, @NonNull Set<String> serviceIds) {
    serviceIds.stream()
        .forEach(
            svcId -> {
              final Optional<String> atcServiceName =
                  atcToCareMapper.serviceNameForServiceId(svcId);
              final Optional<String> cmsServiceName =
                  cmsOverlayMapper.serviceNameForServiceId(svcId);
              if (cmsServiceName.isPresent()) {
                serviceIdToServiceName.put(svcId, cmsServiceName.get());
              } else if (atcServiceName.isPresent()) {
                serviceIdToServiceName.put(svcId, atcServiceName.get());
                log.error(
                    "Service for {} not found in CMS, but present in ATC. "
                        + "CMS no longer source of truth for {}",
                    svcId,
                    svcId);
              }
            });
  }

  /** Method used to obtain aggregated service name for given service id. */
  public String serviceName(String serviceId) {
    return serviceIdToServiceNameAggregate.containsKey(serviceId)
        ? serviceIdToServiceNameAggregate.get(serviceId)
        : HealthService.isRecognizedServiceId(serviceId)
            ? HealthService.fromServiceId(serviceId).get().name()
            : BenefitsService.isRecognizedServiceId(serviceId)
                ? BenefitsService.fromServiceId(serviceId).get().name()
                : OtherService.isRecognizedServiceId(serviceId)
                    ? OtherService.fromServiceId(serviceId).get().name()
                    : null;
  }
}
