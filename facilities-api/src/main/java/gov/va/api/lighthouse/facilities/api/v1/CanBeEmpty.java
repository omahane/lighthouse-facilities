package gov.va.api.lighthouse.facilities.api.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface CanBeEmpty {
  /** Empty elements will be omitted from JSON serialization. */
  @JsonIgnore
  boolean isEmpty();
}
