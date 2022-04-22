package gov.va.api.lighthouse.facilities.api;

import static org.apache.commons.lang3.StringUtils.capitalize;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum TypeOfService {
  @JsonProperty("benefits")
  Benefits,
  @JsonProperty("health")
  Health,
  @JsonProperty("other")
  Other;

  /** Ensure that Jackson can create ServiceType enum regardless of capitalization. */
  @JsonCreator
  public static TypeOfService fromString(String name) {
    return valueOf(capitalize(name));
  }
}
