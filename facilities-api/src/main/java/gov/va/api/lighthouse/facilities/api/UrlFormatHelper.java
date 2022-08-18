package gov.va.api.lighthouse.facilities.api;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class UrlFormatHelper {
  public static String withTrailingSlash(@NonNull String url) {
    return url.endsWith("/") ? url : url + "/";
  }
}
