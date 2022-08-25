package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Properties;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.io.ByteArrayResource;

public class HomeControllerTest {
  @Test
  void metadata() {
    Properties properties = new Properties();
    properties.put("version", "3.14");
    assertThat(
            HomeController.builder()
                .buildProperties(new BuildProperties(properties))
                .basePath("/bp")
                .build()
                .metadata())
        .isEqualTo(
            HomeController.Metadata.builder()
                .meta(
                    HomeController.Versions.builder()
                        .versions(
                            List.of(
                                HomeController.Version.builder()
                                    .version("3.14")
                                    .internalOnly(false)
                                    .status("Current Version")
                                    .path("/bp/docs/v0/api")
                                    .healthcheck("/bp/actuator/health")
                                    .build()))
                        .build())
                .build());
  }

  @Test
  @SneakyThrows
  void openapiJson() {
    assertThat(
            HomeController.builder()
                .openapi(new ByteArrayResource("{}".getBytes()))
                .basePath("")
                .build()
                .openapiJson())
        .isEqualTo("{}");
  }
}
