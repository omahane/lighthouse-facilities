package gov.va.api.lighthouse.facilities.collector;

import lombok.experimental.UtilityClass;

@UtilityClass
final class CollectorExceptions {
  static final class BenefitsCollectorException extends CollectorException {
    public BenefitsCollectorException(Throwable cause) {
      super(cause);
    }
  }

  static final class CemeteriesCollectorException extends CollectorException {
    public CemeteriesCollectorException(Throwable cause) {
      super(cause);
    }
  }

  static class CollectorException extends RuntimeException {
    CollectorException(Throwable cause) {
      super(cause);
    }
  }

  static final class HealthsCollectorException extends CollectorException {
    public HealthsCollectorException(Throwable cause) {
      super(cause);
    }
  }

  static final class StateCemeteriesCollectorException extends CollectorException {
    public StateCemeteriesCollectorException(Throwable cause) {
      super(cause);
    }
  }

  static final class VetCentersCollectorException extends CollectorException {
    public VetCentersCollectorException(Throwable cause) {
      super(cause);
    }
  }
}
