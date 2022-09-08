package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.Covid19Vaccine;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import com.google.common.collect.ImmutableMap;
import gov.va.api.lighthouse.facilities.collector.AccessToCareMapper;
import gov.va.api.lighthouse.facilities.collector.CmsOverlayMapper;
import gov.va.api.lighthouse.facilities.collector.IsAtcAware;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceNameAggregatorV0 {
  private final AccessToCareMapper atcToCareMapper;

  private final CmsOverlayMapper cmsOverlayMapper;

  private ServiceNameAggregate serviceNameAggregate;

  /** Aggregate CMS and ATC service name data based on established ruleset. */
  public ServiceNameAggregatorV0(
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
    serviceNameAggregate = new ServiceNameAggregate(atcToCareMapper.serviceIds());
  }

  public ServiceNameAggregate serviceNameAggregate() {
    return serviceNameAggregate;
  }

  public class ServiceNameAggregate implements IsAtcAware {
    private final Map<String, String> serviceIdToServiceNameMapping;

    private final Map<String, String> reverseLookup;

    public ServiceNameAggregate(@NonNull Set<String> atcServiceIds) {
      this.serviceIdToServiceNameMapping = populateAggregteMappings(atcServiceIds);
      this.reverseLookup = populateReverseLookup(serviceIdToServiceNameMapping);
    }

    /** Method used to determine if given service name is recognized. */
    public boolean isRecognizedServiceName(String serviceName) {
      return serviceName != null
          && (reverseLookup.containsKey(serviceName)
              || gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService
                  .isRecognizedEnumOrCovidService(serviceName)
              || gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService
                  .isRecognizedEnumOrCovidService(serviceName)
              || gov.va.api.lighthouse.facilities.api.v0.Facility.BenefitsService
                  .isRecognizedServiceEnum(serviceName)
              || gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService
                  .isRecognizedServiceEnum(serviceName)
              || gov.va.api.lighthouse.facilities.api.v0.Facility.OtherService
                  .isRecognizedServiceEnum(serviceName)
              || gov.va.api.lighthouse.facilities.api.v1.Facility.OtherService
                  .isRecognizedServiceEnum(serviceName));
    }

    /**
     * Populate service name mappings using ATC as source of truth. Covid-19 only service for which
     * CMS is source of truth for V0 services.
     */
    private Map<String, String> populateAggregteMappings(@NonNull Set<String> atcServiceIds) {
      final Map<String, String> serviceIdToServiceNameAggregateMapping =
          new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
      // Populate mapping with ATC service names
      atcServiceIds.stream()
          .forEach(
              svcId -> {
                final Optional<String> atcServiceName =
                    atcToCareMapper.serviceNameForServiceId(svcId);
                if (atcServiceName.isPresent()) {
                  serviceIdToServiceNameAggregateMapping.put(
                      svcId, HEALTH_SERVICES.get(atcServiceName.get()).name());
                }
              });
      // Populate mapping with CMS service name for Covid-19 service
      final Optional<String> covid19SvcName =
          cmsOverlayMapper.serviceNameForServiceId(Covid19Vaccine.serviceId());
      if (covid19SvcName.isPresent()) {
        serviceIdToServiceNameAggregateMapping.put(
            Covid19Vaccine.serviceId(), covid19SvcName.get());
      }
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

    /**
     * Method used to obtain aggregated service name for given service id. For the moment, V1
     * services are being allowed to be uploaded to the V0 endpoint.
     */
    public String serviceName(String serviceId) {
      final String svcId = serviceId == null ? null : uncapitalize(serviceId);
      return serviceIdToServiceNameMapping.containsKey(svcId)
          ? serviceIdToServiceNameMapping.get(svcId)
          : gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.isRecognizedServiceId(
                  svcId)
              ? gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.fromServiceId(svcId)
                  .get()
                  .name()
              : gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService
                      .isRecognizedServiceId(svcId)
                  ? gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.fromServiceId(
                          svcId)
                      .get()
                      .name()
                  : gov.va.api.lighthouse.facilities.api.v0.Facility.BenefitsService
                          .isRecognizedServiceId(svcId)
                      ? gov.va.api.lighthouse.facilities.api.v0.Facility.BenefitsService
                          .fromServiceId(svcId)
                          .get()
                          .name()
                      : gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService
                              .isRecognizedServiceId(svcId)
                          ? gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService
                              .fromServiceId(svcId)
                              .get()
                              .name()
                          : gov.va.api.lighthouse.facilities.api.v0.Facility.OtherService
                                  .isRecognizedServiceId(svcId)
                              ? gov.va.api.lighthouse.facilities.api.v0.Facility.OtherService
                                  .fromServiceId(svcId)
                                  .get()
                                  .name()
                              : gov.va.api.lighthouse.facilities.api.v1.Facility.OtherService
                                      .isRecognizedServiceId(svcId)
                                  ? gov.va.api.lighthouse.facilities.api.v1.Facility.OtherService
                                      .fromServiceId(svcId)
                                      .get()
                                      .name()
                                  : null;
    }
  }
}
