package gov.va.api.lighthouse.facilities.api;

import static org.apache.commons.lang3.StringUtils.capitalize;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import lombok.SneakyThrows;

public enum TypeOfService {
  @JsonProperty("benefits")
  Benefits,
  @JsonProperty("health")
  Health,
  @JsonProperty("other")
  Other;

  /** Ensure that Jackson can create ServiceType enum regardless of capitalization. */
  @JsonCreator
  @SneakyThrows
  public static TypeOfService fromString(String name) {
    if (isRecognizedTypeOfService(name)) {
      return valueOf(capitalize(name));
    } else {
      throw new Exception(String.format("Unrecognized service type: %s", name));
    }
  }

  /** Method used to determine whether name matches string representation of enum value. */
  public static boolean isRecognizedTypeOfService(String name) {
    return name != null
        && Arrays.stream(values())
            .parallel()
            .map(tos -> tos.name())
            .anyMatch(tosName -> tosName.equalsIgnoreCase(name));
  }
}
