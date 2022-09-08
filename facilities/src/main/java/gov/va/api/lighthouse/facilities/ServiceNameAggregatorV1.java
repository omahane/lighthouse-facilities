package gov.va.api.lighthouse.facilities;

import static org.apache.commons.lang3.StringUtils.uncapitalize;

import com.google.common.collect.ImmutableMap;
import gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService;
import gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService;
import gov.va.api.lighthouse.facilities.api.v1.Facility.OtherService;
import gov.va.api.lighthouse.facilities.collector.AccessToCareMapper;
import gov.va.api.lighthouse.facilities.collector.CmsOverlayMapper;
import gov.va.api.lighthouse.facilities.collector.IsAtcAware;
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
public class ServiceNameAggregatorV1 {
  private final AccessToCareMapper atcToCareMapper;

  private final CmsOverlayMapper cmsOverlayMapper;

  private ServiceNameAggregate serviceNameAggregate;

  /** Aggregate CMS and ATC service name data based on established ruleset. */
  public ServiceNameAggregatorV1(
      @Autowired @NonNull AccessToCareMapper atcToCareMapper,
      @Autowired @NonNull CmsOverlayMapper cmsOverlayMapper) {
    this.atcToCareMapper = atcToCareMapper;
    this.cmsOverlayMapper = cmsOverlayMapper;
    reloadAll();
  }

  /** Refresh and reload mapping helpers and service name mappings. */
  public void reloadAll() {
    if (atcToCareMapper.reload() && cmsOverlayMapper.reload()) {
      reloadMapping();
    }
  }

  /** Refresh and reload service name mappings. */
  public void reloadMapping() {
    serviceNameAggregate =
        new ServiceNameAggregate(cmsOverlayMapper.serviceIds(), atcToCareMapper.serviceIds());
  }

  public ServiceNameAggregate serviceNameAggregate() {
    return serviceNameAggregate;
  }

  public class ServiceNameAggregate implements IsAtcAware {
    private final Map<String, String> serviceIdToServiceNameMapping;

    private final Map<String, String> reverseLookup;

    public ServiceNameAggregate(
        @NonNull Set<String> cmsServiceIds, @NonNull Set<String> atcServiceIds) {
      this.serviceIdToServiceNameMapping = populateAggregteMappings(cmsServiceIds, atcServiceIds);
      this.reverseLookup = populateReverseLookup(serviceIdToServiceNameMapping);
    }

    /** Method used to determine if given service name is recognized. */
    public boolean isRecognizedServiceName(String serviceName) {
      return serviceName != null
          && (reverseLookup.containsKey(serviceName)
              || HealthService.isRecognizedEnumOrCovidService(serviceName)
              || BenefitsService.isRecognizedServiceEnum(serviceName)
              || OtherService.isRecognizedServiceEnum(serviceName));
    }

    /** Method used to populate aggregated service name for given service id. */
    private void populateAggregateMapping(
        @NonNull Map<String, String> serviceIdToServiceNameAggregateMapping,
        @NonNull Set<String> serviceIds) {
      serviceIds.stream()
          .forEach(
              svcId -> {
                final Optional<String> atcServiceName =
                    atcToCareMapper.serviceNameForServiceId(svcId);
                final Optional<String> cmsServiceName =
                    cmsOverlayMapper.serviceNameForServiceId(svcId);
                if (cmsServiceName.isPresent()) {
                  serviceIdToServiceNameAggregateMapping.put(svcId, cmsServiceName.get());
                } else if (atcServiceName.isPresent()) {
                  serviceIdToServiceNameAggregateMapping.put(
                      svcId, HEALTH_SERVICES.get(atcServiceName.get()).name());
                  log.error(
                      "Service for {} not found in CMS, but present in ATC. "
                          + "CMS no longer source of truth for {}",
                      svcId,
                      svcId);
                }
              });
    }

    /**
     * Service name populated based on following rules:
     * <li>If given service shows active from CMS but not in ATC. Service name should be populated
     *     from CMS. (No data manipulation needed)
     * <li>If given service shows active from ATC, but not in CMS. In unlikely event that this
     *     should occur, service name should be populated from ATC. CMS is the recognized source of
     *     truth.
     * <li>If given service is populated in both ATC and CMS. Service name should be populated from
     *     CMS data. (No data manipulation needed)
     */
    private Map<String, String> populateAggregteMappings(
        @NonNull Set<String> cmsServiceIds, @NonNull Set<String> atcServiceIds) {
      final Map<String, String> serviceIdToServiceNameAggregateMapping =
          new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
      populateAggregateMapping(serviceIdToServiceNameAggregateMapping, cmsServiceIds);
      populateAggregateMapping(serviceIdToServiceNameAggregateMapping, atcServiceIds);
      return ImmutableMap.copyOf(serviceIdToServiceNameAggregateMapping);
    }

    /** Method used to populate reverse lookup mapping. */
    private Map<String, String> populateReverseLookup(
        @NonNull Map<String, String> serviceIdToServiceNameMapping) {
      final Map<String, String> serviceNameToServiceIdMapping =
          new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
      serviceIdToServiceNameMapping.entrySet().stream()
          .forEach(es -> serviceNameToServiceIdMapping.put(es.getValue(), es.getKey()));
      return ImmutableMap.copyOf(serviceNameToServiceIdMapping);
    }

    /** Method used to obtain service id for a given service name. */
    public Optional<String> serviceId(String serviceName) {
      return Optional.ofNullable(reverseLookup.get(serviceName));
    }

    /** Method used to obtain aggregated service name for given service id. */
    public String serviceName(String serviceId) {
      final String svcId = serviceId == null ? null : uncapitalize(serviceId);
      return serviceIdToServiceNameMapping.containsKey(svcId)
          ? serviceIdToServiceNameMapping.get(svcId)
          : HealthService.isRecognizedServiceId(svcId)
              ? HealthService.fromServiceId(svcId).get().name()
              : BenefitsService.isRecognizedServiceId(svcId)
                  ? BenefitsService.fromServiceId(svcId).get().name()
                  : OtherService.isRecognizedServiceId(svcId)
                      ? OtherService.fromServiceId(svcId).get().name()
                      : null;
    }
  }
}
