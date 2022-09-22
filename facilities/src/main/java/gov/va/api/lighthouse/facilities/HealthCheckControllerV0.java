package gov.va.api.lighthouse.facilities;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.va.api.lighthouse.facilities.collector.InsecureRestTemplateProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Validated
@RestController
@RequestMapping(produces = "application/json")
public class HealthCheckControllerV0 {

  private final String healthActuatorEndpoint;

  private final RestTemplate restTemplate;

  @Builder
  HealthCheckControllerV0(
      @Value("${facilities.url}") String baseUrl,
      @Autowired InsecureRestTemplateProvider insecureRestTemplateProvider) {
    this.healthActuatorEndpoint = baseUrl + "/actuator/health";
    this.restTemplate = insecureRestTemplateProvider.restTemplate();
  }

  @GetMapping(value = "/v0/healthcheck")
  public HealthActuatorResponse getHealthActuatorEndpoint() {
    return restTemplate.getForObject(healthActuatorEndpoint, HealthActuatorResponse.class);
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  private static class HealthActuatorResponse {
    @JsonProperty("status")
    private String status;
  }
}
