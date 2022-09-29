package gov.va.api.lighthouse.facilities;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import lombok.Builder;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeControllerV1 {
  private final Resource openapi;

  @Builder
  HomeControllerV1(
      @Value("classpath:/v1/openapi.json") Resource openapi,
      @Value("${facilities.base-path}") String basePath) {
    this.openapi = openapi;
  }

  @SneakyThrows
  private String openapiContent() {
    try (InputStream is = openapi.getInputStream()) {
      return StreamUtils.copyToString(is, Charset.defaultCharset());
    }
  }

  @SneakyThrows
  @GetMapping(
      value = {"/", "/docs/v1/api", "/v1/facilities/openapi.json"},
      produces = "application/json")
  Object openapiJson() {
    return openapiContent();
  }

  @Builder
  @lombok.Value
  static final class Versions {
    List<Version> versions;
  }

  @Builder
  @lombok.Value
  static final class Version {
    String version;

    @JsonProperty("internal_only")
    Boolean internalOnly;

    String status;

    String path;

    String healthcheck;
  }
}
