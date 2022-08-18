package gov.va.api.lighthouse.facilities.api;

import static gov.va.api.lighthouse.facilities.api.UrlFormatHelper.withTrailingSlash;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServiceLinkBuilder {
  /** Build base link url used for versioned API calls. */
  private static String buildLinkerUrl(
      @NonNull String baseUrl, @NonNull String basePath, @NonNull String version) {
    String url = withTrailingSlash(baseUrl);
    String path = basePath.replaceAll("/$", "");
    path = path.isEmpty() ? path : withTrailingSlash(path);
    return withTrailingSlash(url + path + version);
  }

  /** Build base link url used for V0 API calls. */
  public static String buildLinkerUrlV0(@NonNull String baseUrl, @NonNull String basePath) {
    return buildLinkerUrl(baseUrl, basePath, "v0");
  }

  /** Build base link url used for V1 API calls. */
  public static String buildLinkerUrlV1(@NonNull String baseUrl, @NonNull String basePath) {
    return buildLinkerUrl(baseUrl, basePath, "v1");
  }

  /**
   * Build link to obtain collection of services for facility. Example:
   * https://api.va.gov/services/va_facilities/v1/facilities/vha_402/services.
   */
  public static String buildServicesLink(@NonNull String linkerUrl, @NonNull String facilityId) {
    StringBuilder linkUrl = new StringBuilder();
    linkUrl.append(withTrailingSlash(linkerUrl.trim()));
    linkUrl.append("facilities/").append(facilityId.trim()).append("/services");
    return linkUrl.toString();
  }

  /**
   * Build link to obtain specific facility service. Example:
   * https://api.va.gov/services/va_facilities/v1/facilities/vha_402/services/cardiology.
   */
  public static String buildTypedServiceLink(
      @NonNull String linkerUrl, @NonNull String facilityId, @NonNull String serviceId) {
    StringBuilder linkUrl = new StringBuilder();
    linkUrl.append(buildServicesLink(linkerUrl, facilityId)).append("/").append(serviceId.trim());
    return linkUrl.toString();
  }
}
