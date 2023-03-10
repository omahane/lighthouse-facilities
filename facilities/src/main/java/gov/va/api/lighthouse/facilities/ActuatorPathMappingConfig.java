package gov.va.api.lighthouse.facilities;

import java.util.stream.Stream;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ActuatorPathMappingConfig implements WebMvcConfigurer {

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    Stream.of("/actuator/health", "/actuator/health/liveness", "/actuator/health/readiness")
        .forEach(
            healthCheck -> {
              registry.addViewController("/v0" + healthCheck).setViewName(healthCheck);
              registry.addViewController("/v1" + healthCheck).setViewName(healthCheck);
            });
  }
}
