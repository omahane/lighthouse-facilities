package gov.va.api.lighthouse.facilities.collector;

import java.util.Optional;
import java.util.Set;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AccessToCareMapper extends BaseAccessToCareHandler implements ServiceDataMapper {

  /** Access To Care service data loader. */
  public AccessToCareMapper(
      @Autowired InsecureRestTemplateProvider insecureRestTemplateProvider,
      @Value("${access-to-care.url}") String atcBaseUrl) {
    super(insecureRestTemplateProvider, atcBaseUrl);
  }

  @Override
  public Set<String> serviceIds() {
    return serviceIdToServiceNameMapping.keySet();
  }

  @Override
  public Optional<String> serviceNameForServiceId(@NonNull String serviceId) {
    // Obtain ATC service name from service id reverse lookup
    return Optional.ofNullable(serviceIdToServiceNameMapping.get(serviceId));
  }
}
