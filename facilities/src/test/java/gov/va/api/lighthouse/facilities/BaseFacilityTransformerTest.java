package gov.va.api.lighthouse.facilities;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;

import lombok.SneakyThrows;

public abstract class BaseFacilityTransformerTest {
  @SneakyThrows
  protected DatamartFacility.HealthService convertToDatamartHealthService(String json) {
    return createMapper().readValue(json, DatamartFacility.HealthService.class);
  }

  @SneakyThrows
  protected gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService convertToHealthServiceV0(
      String json) {
    return createMapper()
        .readValue(json, gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService.class);
  }

  @SneakyThrows
  protected gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService convertToHealthServiceV1(
      String json) {
    return createMapper()
        .readValue(json, gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.class);
  }

  @SneakyThrows
  protected String convertToJson(
      gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService healthService) {
    return createMapper().writeValueAsString(healthService);
  }

  @SneakyThrows
  protected String convertToJson(
      gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService healthService) {
    return createMapper().writeValueAsString(healthService);
  }

  @SneakyThrows
  protected String convertToJson(DatamartFacility.HealthService healthService) {
    return createMapper().writeValueAsString(healthService);
  }
}
