package gov.va.api.lighthouse.facilities.collector;

import java.util.Optional;
import java.util.Set;
import lombok.NonNull;

public interface ServiceDataMapper {
  Set<String> serviceIds();

  Optional<String> serviceNameForServiceId(@NonNull String serviceId);
}
