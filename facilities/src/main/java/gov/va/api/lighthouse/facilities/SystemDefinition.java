package gov.va.api.lighthouse.facilities;

import gov.va.api.health.sentinel.Environment;
import gov.va.api.health.sentinel.SentinelProperties;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SystemDefinition {
  private Service lab() {
    return Service.builder()
        .url(SentinelProperties.optionUrl("facilities", "https://blue.lab.lighthouse.va.gov"))
        .apiPath(SentinelProperties.optionApiPath("facilities", "/va_facilities/"))
        .build();
  }

  private Service local() {
    return Service.builder()
        .url(SentinelProperties.optionUrl("facilities", "http://localhost:8085"))
        .apiPath(SentinelProperties.optionApiPath("facilities", "/"))
        .build();
  }

  private Service production() {
    return Service.builder()
        .url(
            SentinelProperties.optionUrl("facilities", "https://blue.production.lighthouse.va.gov"))
        .apiPath(SentinelProperties.optionApiPath("facilities", "/va_facilities/"))
        .build();
  }

  private Service qa() {
    return Service.builder()
        .url(SentinelProperties.optionUrl("facilities", "https://blue.qa.lighthouse.va.gov"))
        .apiPath(SentinelProperties.optionApiPath("facilities", "/va_facilities/"))
        .build();
  }

  private Service staging() {
    return Service.builder()
        .url(SentinelProperties.optionUrl("facilities", "https://blue.staging.lighthouse.va.gov"))
        .apiPath(SentinelProperties.optionApiPath("facilities", "/va_facilities/"))
        .build();
  }

  private Service stagingLab() {
    return Service.builder()
        .url(
            SentinelProperties.optionUrl(
                "facilities", "https://blue.staging-lab.lighthouse.va.gov"))
        .apiPath(SentinelProperties.optionApiPath("facilities", "/va_facilities/"))
        .build();
  }

  /** Return url for each environment. */
  public Service systemDefinition() {
    switch (Environment.get()) {
      case LOCAL:
        return local();
      case QA:
        return qa();
      case STAGING:
        return staging();
      case PROD:
        return production();
      case STAGING_LAB:
        return stagingLab();
      case LAB:
        return lab();
      default:
        throw new IllegalArgumentException(
            "Unsupported sentinel environment: " + Environment.get());
    }
  }

  @Value
  @Builder
  public static class Service {
    @NonNull String url;
    @NonNull String apiPath;
  }
}
