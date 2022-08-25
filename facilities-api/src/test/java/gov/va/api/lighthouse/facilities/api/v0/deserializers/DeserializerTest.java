package gov.va.api.lighthouse.facilities.api.v0.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.v0.DetailedService.INVALID_SVC_ID;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.v0.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v0.DetailedService;
import gov.va.api.lighthouse.facilities.api.v0.DetailedServicesResponse;
import gov.va.api.lighthouse.facilities.api.v0.Facility.BenefitsService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.FacilityAttributes;
import gov.va.api.lighthouse.facilities.api.v0.Facility.HealthService;
import gov.va.api.lighthouse.facilities.api.v0.Facility.OtherService;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacility.Properties;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class DeserializerTest {
  @SneakyThrows
  private <T> void assertJson(String json, Class<T> expectedClass, T expectedValue) {
    assertThat(createMapper().readValue(json, expectedClass))
        .usingRecursiveComparison()
        .isEqualTo(expectedValue);
  }

  @Test
  @SneakyThrows
  void deserializeBenefitsCmsOverlay() {
    // All non-Covid-19 detailed services are filtered out for V0
    CmsOverlay overlay =
        CmsOverlay.builder()
            .detailedServices(
                List.of(
                    DetailedService.builder()
                        .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                        .name(BenefitsService.Pensions.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"Pensions\"}" + "]}", CmsOverlay.class, overlay);
    assertJson(
        "{\"detailed_services\":[" + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\"}" + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"Pensions\",\"appointment_phones\":[]}" + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
  }

  @Test
  @SneakyThrows
  void deserializeBenefitsDetailedService() {
    DetailedService detailedService =
        DetailedService.builder()
            .serviceId(uncapitalize(BenefitsService.Pensions.name()))
            .name(BenefitsService.Pensions.name())
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson("{\"name\":\"Pensions\"}", DetailedService.class, detailedService);
    assertJson(
        "{\"serviceId\":\"pensions\",\"name\":\"Pensions\"}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"name\":\"Pensions\",\"appointment_phones\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]}",
        DetailedService.class,
        detailedService);
  }

  @Test
  @SneakyThrows
  void deserializeBenefitsDetailedServicesResponse() {
    // All non-Covid-19 detailed services are filtered out for V0
    DetailedServicesResponse response =
        DetailedServicesResponse.builder()
            .data(
                List.of(
                    DetailedService.builder()
                        .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                        .name(BenefitsService.Pensions.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"data\":[" + "{\"name\":\"Pensions\"}" + "]}", DetailedServicesResponse.class, response);
    assertJson(
        "{\"data\":[" + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\"}" + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":[" + "{\"name\":\"Pensions\",\"appointment_phones\":[]}" + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
  }

  @Test
  @SneakyThrows
  void deserializeBenefitsFacilityAttributes() {
    // All non-Covid-19 detailed services are filtered out for V0
    FacilityAttributes attributes =
        FacilityAttributes.builder()
            .detailedServices(
                List.of(
                    DetailedService.builder()
                        .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                        .name(BenefitsService.Pensions.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"Pensions\"}" + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":[" + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\"}" + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"Pensions\",\"appointment_phones\":[]}" + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
  }

  @Test
  @SneakyThrows
  void deserializeBenefitsGeoFacilityProperties() {
    // All non-Covid-19 detailed services are filtered out for V0
    Properties properties =
        Properties.builder()
            .detailedServices(
                List.of(
                    DetailedService.builder()
                        .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                        .name(BenefitsService.Pensions.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"Pensions\"}" + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":[" + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\"}" + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"Pensions\",\"appointment_phones\":[]}" + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[]}"
            + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        Properties.class,
        properties);
  }

  @Test
  @SneakyThrows
  void deserializeCmsOverlayWithInvalidDetailedServices() {
    CmsOverlay overlay = CmsOverlay.builder().detailedServices(emptyList()).build();
    assertJson(
        "{\"detailed_services\":[" + "{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\"}" + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":[" + "{\"serviceId\":\"foo\",\"name\":\"bar\"}" + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"baz\",\"appointment_phones\":[]}" + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"baz\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"foo\",\"name\":\"bar\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
  }

  @Test
  @SneakyThrows
  void deserializeCmsOverlayWithMixedDetailedServices() {
    // All non-Covid-19 detailed services are filtered out for V0
    CmsOverlay overlay =
        CmsOverlay.builder()
            .detailedServices(
                List.of(
                    DetailedService.builder()
                        .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                        .name(BenefitsService.Pensions.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build(),
                    DetailedService.builder()
                        .serviceId(uncapitalize(HealthService.Covid19Vaccine.name()))
                        .name(HealthService.Covid19Vaccine.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Pensions\"},"
            + "{\"name\":\"Covid19Vaccine\"},"
            + "{\"name\":\"Smoking\"},"
            + "{\"name\":\"foo\"}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\"},"
            + "{\"serviceId\":\"covid19Vaccine\",\"name\":\"Covid19Vaccine\"},"
            + "{\"serviceId\":\"smoking\",\"name\":\"Smoking\"},"
            + "{\"serviceId\":\"foo\",\"name\":\"Pensions\"},"
            + "{\"serviceId\":\"bar\",\"name\":\"baz\"}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Pensions\",\"appointment_phones\":[]},"
            + "{\"name\":\"Covid19Vaccine\",\"appointment_phones\":[]},"
            + "{\"name\":\"Smoking\",\"appointment_phones\":[]},"
            + "{\"name\":\"foo\",\"appointment_phones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[]},"
            + "{\"serviceId\":\"covid19Vaccine\",\"name\":\"Covid19Vaccine\",\"appointment_phones\":[]},"
            + "{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"appointment_phones\":[]},"
            + "{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"appointment_phones\":[]},"
            + "{\"serviceId\":\"bar\",\"name\":\"baz\",\"appointment_phones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"name\":\"Covid19Vaccine\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"name\":\"Smoking\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"name\":\"foo\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceId\":\"covid19Vaccine\",\"name\":\"Covid19Vaccine\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceId\":\"bar\",\"name\":\"baz\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
  }

  @Test
  @SneakyThrows
  void deserializeDetailedServicesResponseWithInvalidDetailedServices() {
    DetailedServicesResponse response =
        DetailedServicesResponse.builder().data(emptyList()).build();
    assertJson(
        "{\"data\":[" + "{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\"}" + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":[" + "{\"serviceId\":\"foo\",\"name\":\"bar\"}" + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":[" + "{\"name\":\"baz\",\"appointment_phones\":[]}" + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"name\":\"baz\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceId\":\"foo\",\"name\":\"bar\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
  }

  @Test
  @SneakyThrows
  void deserializeDetailedServicesResponseWithMixedDetailedServices() {
    // All non-Covid-19 detailed services are filtered out for V0
    DetailedServicesResponse response =
        DetailedServicesResponse.builder()
            .data(
                List.of(
                    DetailedService.builder()
                        .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                        .name(BenefitsService.Pensions.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build(),
                    DetailedService.builder()
                        .serviceId(uncapitalize(HealthService.Covid19Vaccine.name()))
                        .name(HealthService.Covid19Vaccine.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"data\":["
            + "{\"name\":\"Pensions\"},"
            + "{\"name\":\"Covid19Vaccine\"},"
            + "{\"name\":\"Smoking\"},"
            + "{\"name\":\"foo\"}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\"},"
            + "{\"serviceId\":\"covid19Vaccine\",\"name\":\"Covid19Vaccine\"},"
            + "{\"serviceId\":\"smoking\",\"name\":\"Smoking\"},"
            + "{\"serviceId\":\"foo\",\"name\":\"Pensions\"},"
            + "{\"serviceId\":\"bar\",\"name\":\"baz\"}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"name\":\"Pensions\",\"appointment_phones\":[]},"
            + "{\"name\":\"Covid19Vaccine\",\"appointment_phones\":[]},"
            + "{\"name\":\"Smoking\",\"appointment_phones\":[]},"
            + "{\"name\":\"foo\",\"appointment_phones\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[]},"
            + "{\"serviceId\":\"covid19Vaccine\",\"name\":\"Covid19Vaccine\",\"appointment_phones\":[]},"
            + "{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"appointment_phones\":[]},"
            + "{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"appointment_phones\":[]},"
            + "{\"serviceId\":\"bar\",\"name\":\"baz\",\"appointment_phones\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"name\":\"Covid19Vaccine\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"name\":\"Smoking\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"name\":\"foo\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceId\":\"covid19Vaccine\",\"name\":\"Covid19Vaccine\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceId\":\"bar\",\"name\":\"baz\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
  }

  @Test
  @SneakyThrows
  void deserializeFacilityAttributesWithInvalidDetailedServices() {
    FacilityAttributes attributes =
        FacilityAttributes.builder().detailedServices(emptyList()).build();
    assertJson(
        "{\"detailed_services\":[" + "{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\"}" + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":[" + "{\"serviceId\":\"foo\",\"name\":\"bar\"}" + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"baz\",\"appointment_phones\":[]}" + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"baz\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"foo\",\"name\":\"bar\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
  }

  @Test
  @SneakyThrows
  void deserializeFacilityAttributesWithMixedDetailedServices() {
    // All non-Covid-19 detailed services are filtered out for V0
    FacilityAttributes attributes =
        FacilityAttributes.builder()
            .detailedServices(
                List.of(
                    DetailedService.builder()
                        .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                        .name(BenefitsService.Pensions.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build(),
                    DetailedService.builder()
                        .serviceId(uncapitalize(HealthService.Covid19Vaccine.name()))
                        .name(HealthService.Covid19Vaccine.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Pensions\"},"
            + "{\"name\":\"Covid19Vaccine\"},"
            + "{\"name\":\"Smoking\"},"
            + "{\"name\":\"foo\"}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\"},"
            + "{\"serviceId\":\"covid19Vaccine\",\"name\":\"Covid19Vaccine\"},"
            + "{\"serviceId\":\"smoking\",\"name\":\"Smoking\"},"
            + "{\"serviceId\":\"foo\",\"name\":\"Pensions\"},"
            + "{\"serviceId\":\"bar\",\"name\":\"baz\"}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Pensions\",\"appointment_phones\":[]},"
            + "{\"name\":\"Covid19Vaccine\",\"appointment_phones\":[]},"
            + "{\"name\":\"Smoking\",\"appointment_phones\":[]},"
            + "{\"name\":\"foo\",\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[]},"
            + "{\"serviceId\":\"covid19Vaccine\",\"name\":\"Covid19Vaccine\",\"appointment_phones\":[]},"
            + "{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"appointment_phones\":[]},"
            + "{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"appointment_phones\":[]},"
            + "{\"serviceId\":\"bar\",\"name\":\"baz\",\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"name\":\"Covid19Vaccine\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"name\":\"Smoking\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"name\":\"foo\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceId\":\"covid19Vaccine\",\"name\":\"Covid19Vaccine\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceId\":\"bar\",\"name\":\"baz\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
  }

  @Test
  @SneakyThrows
  void deserializeGeoFacilityPropertiesWithInvalidDetailedServices() {
    Properties properties = Properties.builder().detailedServices(emptyList()).build();
    assertJson(
        "{\"detailed_services\":[" + "{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\"}" + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":[" + "{\"serviceId\":\"foo\",\"name\":\"bar\"}" + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"baz\",\"appointment_phones\":[]}" + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[]}"
            + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"baz\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"foo\",\"name\":\"bar\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        Properties.class,
        properties);
  }

  @Test
  @SneakyThrows
  void deserializeGeoFacilityPropertiesWithMixedDetailedServices() {
    // All non-Covid-19 detailed services are filtered out for V0
    Properties properties =
        Properties.builder()
            .detailedServices(
                List.of(
                    DetailedService.builder()
                        .serviceId(uncapitalize(BenefitsService.Pensions.name()))
                        .name(BenefitsService.Pensions.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build(),
                    DetailedService.builder()
                        .serviceId(uncapitalize(HealthService.Covid19Vaccine.name()))
                        .name(HealthService.Covid19Vaccine.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Pensions\"},"
            + "{\"name\":\"Covid19Vaccine\"},"
            + "{\"name\":\"Smoking\"},"
            + "{\"name\":\"foo\"}"
            + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\"},"
            + "{\"serviceId\":\"covid19Vaccine\",\"name\":\"Covid19Vaccine\"},"
            + "{\"serviceId\":\"smoking\",\"name\":\"Smoking\"},"
            + "{\"serviceId\":\"foo\",\"name\":\"Pensions\"},"
            + "{\"serviceId\":\"bar\",\"name\":\"baz\"}"
            + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Pensions\",\"appointment_phones\":[]},"
            + "{\"name\":\"Covid19Vaccine\",\"appointment_phones\":[]},"
            + "{\"name\":\"Smoking\",\"appointment_phones\":[]},"
            + "{\"name\":\"foo\",\"appointment_phones\":[]}"
            + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[]},"
            + "{\"serviceId\":\"covid19Vaccine\",\"name\":\"Covid19Vaccine\",\"appointment_phones\":[]},"
            + "{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"appointment_phones\":[]},"
            + "{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"appointment_phones\":[]},"
            + "{\"serviceId\":\"bar\",\"name\":\"baz\",\"appointment_phones\":[]}"
            + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"name\":\"Covid19Vaccine\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"name\":\"Smoking\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"name\":\"foo\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceId\":\"covid19Vaccine\",\"name\":\"Covid19Vaccine\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceId\":\"bar\",\"name\":\"baz\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        Properties.class,
        properties);
  }

  @Test
  @SneakyThrows
  void deserializeHealthCmsOverlay() {
    // All non-Covid-19 detailed services are filtered out for V0
    CmsOverlay overlay =
        CmsOverlay.builder()
            .detailedServices(
                List.of(
                    DetailedService.builder()
                        .serviceId(uncapitalize(HealthService.DentalServices.name()))
                        .name(HealthService.DentalServices.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"DentalServices\"}" + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"dentalServices\",\"name\":\"DentalServices\"}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"DentalServices\",\"appointment_phones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"dentalServices\",\"name\":\"DentalServices\",\"appointment_phones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"DentalServices\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"dentalServices\",\"name\":\"DentalServices\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);

    overlay =
        CmsOverlay.builder()
            .detailedServices(
                List.of(
                    DetailedService.builder()
                        .serviceId(uncapitalize(HealthService.Covid19Vaccine.name()))
                        .name(HealthService.Covid19Vaccine.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"Covid19Vaccine\"}" + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"covid19Vaccine\",\"name\":\"Covid19Vaccine\"}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Covid19Vaccine\",\"appointment_phones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"covid19Vaccine\",\"name\":\"Covid19Vaccine\",\"appointment_phones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Covid19Vaccine\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"covid19Vaccine\",\"name\":\"Covid19Vaccine\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
  }

  @Test
  @SneakyThrows
  void deserializeHealthDetailedService() {
    DetailedService detailedService =
        DetailedService.builder()
            .serviceId(uncapitalize(HealthService.DentalServices.name()))
            .name(HealthService.DentalServices.name())
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson("{\"name\":\"DentalServices\"}", DetailedService.class, detailedService);
    assertJson(
        "{\"serviceId\":\"dentalServices\",\"name\":\"DentalServices\"}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"name\":\"DentalServices\",\"appointment_phones\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"dentalServices\",\"name\":\"DentalServices\",\"appointment_phones\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"name\":\"DentalServices\",\"appointment_phones\":[],\"service_locations\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"dentalServices\",\"name\":\"DentalServices\",\"appointment_phones\":[],\"service_locations\":[]}",
        DetailedService.class,
        detailedService);
  }

  @Test
  @SneakyThrows
  void deserializeHealthDetailedServicesResponse() {
    // All non-Covid-19 detailed services are filtered out for V0
    DetailedServicesResponse response =
        DetailedServicesResponse.builder()
            .data(
                List.of(
                    DetailedService.builder()
                        .serviceId(uncapitalize(HealthService.DentalServices.name()))
                        .name(HealthService.DentalServices.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"data\":[" + "{\"name\":\"DentalServices\"}" + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":[" + "{\"serviceId\":\"dentalServices\",\"name\":\"DentalServices\"}" + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":[" + "{\"name\":\"DentalServices\",\"appointment_phones\":[]}" + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceId\":\"dentalServices\",\"name\":\"DentalServices\",\"appointment_phones\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"name\":\"DentalServices\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceId\":\"dentalServices\",\"name\":\"DentalServices\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);

    response =
        DetailedServicesResponse.builder()
            .data(
                List.of(
                    DetailedService.builder()
                        .serviceId(uncapitalize(HealthService.Covid19Vaccine.name()))
                        .name(HealthService.Covid19Vaccine.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"data\":[" + "{\"name\":\"Covid19Vaccine\"}" + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":[" + "{\"serviceId\":\"covid19Vaccine\",\"name\":\"Covid19Vaccine\"}" + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":[" + "{\"name\":\"Covid19Vaccine\",\"appointment_phones\":[]}" + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceId\":\"covid19Vaccine\",\"name\":\"Covid19Vaccine\",\"appointment_phones\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"name\":\"Covid19Vaccine\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceId\":\"covid19Vaccine\",\"name\":\"Covid19Vaccine\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
  }

  @Test
  @SneakyThrows
  void deserializeHealthFacilityAttributes() {
    // All non-Covid-19 detailed services are filtered out for V0
    FacilityAttributes attributes =
        FacilityAttributes.builder()
            .detailedServices(
                List.of(
                    DetailedService.builder()
                        .serviceId(uncapitalize(HealthService.DentalServices.name()))
                        .name(HealthService.DentalServices.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"DentalServices\"}" + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"dentalServices\",\"name\":\"DentalServices\"}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"DentalServices\",\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"dentalServices\",\"name\":\"DentalServices\",\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"DentalServices\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"dentalServices\",\"name\":\"DentalServices\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);

    attributes =
        FacilityAttributes.builder()
            .detailedServices(
                List.of(
                    DetailedService.builder()
                        .serviceId(uncapitalize(HealthService.Covid19Vaccine.name()))
                        .name(HealthService.Covid19Vaccine.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"Covid19Vaccine\"}" + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"covid19Vaccine\",\"name\":\"Covid19Vaccine\"}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Covid19Vaccine\",\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"covid19Vaccine\",\"name\":\"Covid19Vaccine\",\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Covid19Vaccine\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"covid19Vaccine\",\"name\":\"Covid19Vaccine\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
  }

  @Test
  @SneakyThrows
  void deserializeHealthGeoFacilityProperties() {
    // All non-Covid-19 detailed services are filtered out for V0
    Properties properties =
        Properties.builder()
            .detailedServices(
                List.of(
                    DetailedService.builder()
                        .serviceId(uncapitalize(HealthService.DentalServices.name()))
                        .name(HealthService.DentalServices.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"DentalServices\"}" + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"dentalServices\",\"name\":\"DentalServices\"}"
            + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"DentalServices\",\"appointment_phones\":[]}"
            + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"dentalServices\",\"name\":\"DentalServices\",\"appointment_phones\":[]}"
            + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"DentalServices\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"dentalServices\",\"name\":\"DentalServices\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        Properties.class,
        properties);

    properties =
        Properties.builder()
            .detailedServices(
                List.of(
                    DetailedService.builder()
                        .serviceId(uncapitalize(HealthService.Covid19Vaccine.name()))
                        .name(HealthService.Covid19Vaccine.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"Covid19Vaccine\"}" + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"covid19Vaccine\",\"name\":\"Covid19Vaccine\"}"
            + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Covid19Vaccine\",\"appointment_phones\":[]}"
            + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"covid19Vaccine\",\"name\":\"Covid19Vaccine\",\"appointment_phones\":[]}"
            + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"Covid19Vaccine\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"covid19Vaccine\",\"name\":\"Covid19Vaccine\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        Properties.class,
        properties);
  }

  @Test
  @SneakyThrows
  void deserializeInvalidDetailedService() {
    DetailedService invalidService =
        DetailedService.builder()
            .serviceId(INVALID_SVC_ID)
            .name("foo")
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson("{\"name\":\"foo\"}", DetailedService.class, invalidService);
    invalidService.name("OnlineScheduling");
    assertJson(
        "{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\"}",
        DetailedService.class,
        invalidService);
    invalidService.name("baz");
    assertJson("{\"serviceId\":\"foo\",\"name\":\"baz\"}", DetailedService.class, invalidService);
    assertJson(
        "{\"serviceId\":\"smoking\",\"name\":\"baz\"}", DetailedService.class, invalidService);
    invalidService.name("Smoking");
    assertJson("{\"name\":\"Smoking\"}", DetailedService.class, invalidService);
    assertJson(
        "{\"serviceId\":\"smoking\",\"name\":\"Smoking\"}", DetailedService.class, invalidService);
    assertJson(
        "{\"serviceId\":\"bar\",\"name\":\"Smoking\"}", DetailedService.class, invalidService);
  }

  @Test
  @SneakyThrows
  void deserializeOtherCmsOverlay() {
    // All non-Covid-19 detailed services are filtered out for V0
    CmsOverlay overlay =
        CmsOverlay.builder()
            .detailedServices(
                List.of(
                    DetailedService.builder()
                        .serviceId(uncapitalize(OtherService.OnlineScheduling.name()))
                        .name(OtherService.OnlineScheduling.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"OnlineScheduling\"}" + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\"}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"OnlineScheduling\",\"appointment_phones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"OnlineScheduling\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
  }

  @Test
  @SneakyThrows
  void deserializeOtherDetailedService() {
    DetailedService detailedService =
        DetailedService.builder()
            .serviceId(uncapitalize(OtherService.OnlineScheduling.name()))
            .name(OtherService.OnlineScheduling.name())
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson("{\"name\":\"OnlineScheduling\"}", DetailedService.class, detailedService);
    assertJson(
        "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\"}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"name\":\"OnlineScheduling\",\"appointment_phones\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"name\":\"OnlineScheduling\",\"appointment_phones\":[],\"service_locations\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[],\"service_locations\":[]}",
        DetailedService.class,
        detailedService);
  }

  @Test
  @SneakyThrows
  void deserializeOtherDetailedServicesResponse() {
    // All non-Covid-19 detailed services are filtered out for V0
    DetailedServicesResponse response =
        DetailedServicesResponse.builder()
            .data(
                List.of(
                    DetailedService.builder()
                        .serviceId(uncapitalize(OtherService.OnlineScheduling.name()))
                        .name(OtherService.OnlineScheduling.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"data\":[" + "{\"name\":\"OnlineScheduling\"}" + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":[" + "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\"}" + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":[" + "{\"name\":\"OnlineScheduling\",\"appointment_phones\":[]}" + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"name\":\"OnlineScheduling\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
  }

  @Test
  @SneakyThrows
  void deserializeOtherFacilityAttributes() {
    // All non-Covid-19 detailed services are filtered out for V0
    FacilityAttributes attributes =
        FacilityAttributes.builder()
            .detailedServices(
                List.of(
                    DetailedService.builder()
                        .serviceId(uncapitalize(OtherService.OnlineScheduling.name()))
                        .name(OtherService.OnlineScheduling.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"OnlineScheduling\"}" + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\"}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"OnlineScheduling\",\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"OnlineScheduling\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        FacilityAttributes.class,
        attributes);
  }

  @Test
  @SneakyThrows
  void deserializeOtherGeoFacilityProperties() {
    // All non-Covid-19 detailed services are filtered out for V0
    Properties properties =
        Properties.builder()
            .detailedServices(
                List.of(
                    DetailedService.builder()
                        .serviceId(uncapitalize(OtherService.OnlineScheduling.name()))
                        .name(OtherService.OnlineScheduling.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":[" + "{\"name\":\"OnlineScheduling\"}" + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\"}"
            + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"OnlineScheduling\",\"appointment_phones\":[]}"
            + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[]}"
            + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"name\":\"OnlineScheduling\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        Properties.class,
        properties);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        Properties.class,
        properties);
  }
}
