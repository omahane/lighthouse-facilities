package gov.va.api.lighthouse.facilities;

import lombok.experimental.UtilityClass;

@UtilityClass
final class ExceptionsUtils {
  static final class InvalidParameter extends RuntimeException {
    InvalidParameter(String name, Object value) {
      super(String.format("'%s' is not a valid value for '%s'", value, name));
    }
  }

  static final class NotFound extends RuntimeException {
    public NotFound(String id) {
      super(String.format("The record identified by %s could not be found", id));
    }

    public NotFound(String id, Throwable cause) {
      super(String.format("The record identified by %s could not be found", id), cause);
    }
  }
}
