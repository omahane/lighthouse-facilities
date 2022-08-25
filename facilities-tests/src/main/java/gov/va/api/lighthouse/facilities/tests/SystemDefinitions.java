package gov.va.api.lighthouse.facilities.tests;

import gov.va.api.health.sentinel.Environment;
import gov.va.api.health.sentinel.SentinelProperties;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SystemDefinitions {
  static final String CLIENT_KEY_DEFAULT = "axolotl";

  private static Ids ids() {
    return Ids.builder()
        .facility("vba_322c")
        .facilityIdsCsv("nca_055,nca_s1001,vba_322c,vc_0101V,vha_402GA")
        .latitude("28.112464")
        .longitude("-80.7015994")
        .bbox("bbox[]=-81.47&bbox[]=27.48&bbox[]=-79.97&bbox[]=28.98")
        .zip("32934")
        .state("FL")
        .city("Melbourne")
        .streetAddress("505 N John Rodes Blvd")
        .type("health")
        .visn("8")
        .mobile(false)
        .build();
  }

  private static SystemDefinition lab() {
    String url = "https://blue.lab.lighthouse.va.gov";
    return SystemDefinition.builder()
        .facilities(serviceDefinition("facilities", url, 443, "/va_facilities/"))
        .facilitiesInternal(serviceDefinition("facilities-internal", url, 443, "/facilities/"))
        .ids(ids())
        .build();
  }

  private static SystemDefinition local() {
    String url = "http://localhost";
    return SystemDefinition.builder()
        .facilities(serviceDefinition("facilities", url, 8085, "/"))
        .facilitiesInternal(serviceDefinition("facilities-internal", url, 8085, "/"))
        .ids(ids())
        .build();
  }

  private static SystemDefinition production() {
    String url = "https://blue.production.lighthouse.va.gov";
    return SystemDefinition.builder()
        .facilities(serviceDefinition("facilities", url, 443, "/va_facilities/"))
        .facilitiesInternal(serviceDefinition("facilities-internal", url, 443, "/facilities/"))
        .ids(ids())
        .build();
  }

  private static SystemDefinition qa() {
    String url = "https://blue.qa.lighthouse.va.gov";
    return SystemDefinition.builder()
        .facilities(serviceDefinition("facilities", url, 443, "/va_facilities/"))
        .facilitiesInternal(serviceDefinition("facilities-internal", url, 443, "/facilities/"))
        .ids(ids())
        .build();
  }

  private static Service serviceDefinition(String name, String url, int port, String apiPath) {
    return Service.builder()
        .url(SentinelProperties.optionUrl(name, url))
        .port(port)
        .apiPath(SentinelProperties.optionApiPath(name, apiPath))
        .build();
  }

  private static SystemDefinition staging() {
    String url = "https://blue.staging.lighthouse.va.gov";
    return SystemDefinition.builder()
        .facilities(serviceDefinition("facilities", url, 443, "/va_facilities/"))
        .facilitiesInternal(serviceDefinition("facilities-internal", url, 443, "/facilities/"))
        .ids(ids())
        .build();
  }

  private static SystemDefinition stagingLab() {
    String url = "https://blue.staging-lab.lighthouse.va.gov";
    return SystemDefinition.builder()
        .facilities(serviceDefinition("facilities", url, 443, "/va_facilities/"))
        .facilitiesInternal(serviceDefinition("facilities-internal", url, 443, "/facilities/"))
        .ids(ids())
        .build();
  }

  /** Gets SystemDefinition based on Sentinel env. */
  public static SystemDefinition systemDefinition() {
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
  public static final class Ids {
    @NonNull String facility;

    @NonNull String facilityIdsCsv;

    @NonNull String latitude;

    @NonNull String longitude;

    @NonNull String bbox;

    @NonNull String zip;

    @NonNull String state;

    @NonNull String city;

    @NonNull String streetAddress;

    @NonNull String type;

    @NonNull String visn;

    @NonNull Boolean mobile;
  }

  @Value
  @Builder
  static final class Service {
    @NonNull String url;

    @NonNull Integer port;

    @NonNull String apiPath;

    String urlWithApiPath() {
      StringBuilder builder = new StringBuilder(url());
      if (!apiPath().startsWith("/")) {
        builder.append('/');
      }
      builder.append(apiPath());
      if (!apiPath.endsWith("/")) {
        builder.append('/');
      }
      return builder.toString();
    }
  }

  @Value
  @Builder
  public static final class SystemDefinition {
    @NonNull Service facilities;

    @NonNull Service facilitiesInternal;

    @NonNull Ids ids;
  }
}
