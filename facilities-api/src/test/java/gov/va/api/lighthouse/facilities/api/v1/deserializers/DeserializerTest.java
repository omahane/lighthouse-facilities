package gov.va.api.lighthouse.facilities.api.v1.deserializers;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static gov.va.api.lighthouse.facilities.api.v1.DetailedService.INVALID_SVC_ID;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.v1.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService;
import gov.va.api.lighthouse.facilities.api.v1.DetailedServicesResponse;
import gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService;
import gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService;
import gov.va.api.lighthouse.facilities.api.v1.Facility.OtherService;
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
        "{\"detailedServices\":[" + "{\"name\":\"Pensions\"}" + "]}", CmsOverlay.class, overlay);
    assertJson(
        "{\"detailedServices\":[" + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\"}" + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":[" + "{\"name\":\"Pensions\",\"appointmentPhones\":[]}" + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointmentPhones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"name\":\"Pensions\",\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointmentPhones\":[],\"serviceLocations\":[]}"
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
        "{\"name\":\"Pensions\",\"appointmentPhones\":[]}", DetailedService.class, detailedService);
    assertJson(
        "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointmentPhones\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"name\":\"Pensions\",\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DetailedService.class,
        detailedService);
  }

  @Test
  @SneakyThrows
  void deserializeBenefitsDetailedServicesResponse() {
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
        "{\"data\":[" + "{\"name\":\"Pensions\",\"appointmentPhones\":[]}" + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointmentPhones\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"name\":\"Pensions\",\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
  }

  @Test
  @SneakyThrows
  void deserializeCmsOverlayWithInvalidDetailedServices() {
    CmsOverlay overlay = CmsOverlay.builder().detailedServices(emptyList()).build();
    assertJson(
        "{\"detailedServices\":[" + "{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\"}" + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":[" + "{\"serviceId\":\"foo\",\"name\":\"bar\"}" + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":[" + "{\"name\":\"baz\",\"appointmentPhones\":[]}" + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"appointmentPhones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"name\":\"baz\",\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceId\":\"foo\",\"name\":\"bar\",\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
  }

  @Test
  @SneakyThrows
  void deserializeCmsOverlayWithMixedDetailedServices() {
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
                        .serviceId(uncapitalize(HealthService.Smoking.name()))
                        .name(HealthService.Smoking.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailedServices\":["
            + "{\"name\":\"Pensions\"},"
            + "{\"name\":\"Smoking\"},"
            + "{\"name\":\"foo\"}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\"},"
            + "{\"serviceId\":\"smoking\",\"name\":\"Smoking\"},"
            + "{\"serviceId\":\"foo\",\"name\":\"Pensions\"},"
            + "{\"serviceId\":\"bar\",\"name\":\"baz\"}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"name\":\"Pensions\",\"appointmentPhones\":[]},"
            + "{\"name\":\"Smoking\",\"appointmentPhones\":[]},"
            + "{\"name\":\"foo\",\"appointmentPhones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointmentPhones\":[]},"
            + "{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"appointmentPhones\":[]},"
            + "{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"appointmentPhones\":[]},"
            + "{\"serviceId\":\"bar\",\"name\":\"baz\",\"appointmentPhones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"name\":\"Pensions\",\"appointmentPhones\":[],\"serviceLocations\":[]},"
            + "{\"name\":\"Smoking\",\"appointmentPhones\":[],\"serviceLocations\":[]},"
            + "{\"name\":\"foo\",\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointmentPhones\":[],\"serviceLocations\":[]},"
            + "{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"appointmentPhones\":[],\"serviceLocations\":[]},"
            + "{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"appointmentPhones\":[],\"serviceLocations\":[]},"
            + "{\"serviceId\":\"bar\",\"name\":\"baz\",\"appointmentPhones\":[],\"serviceLocations\":[]}"
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
        "{\"data\":[" + "{\"name\":\"baz\",\"appointmentPhones\":[]}" + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"appointmentPhones\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"name\":\"baz\",\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceId\":\"foo\",\"name\":\"bar\",\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
  }

  @Test
  @SneakyThrows
  void deserializeDetailedServicesResponseWithMixedDetailedServices() {
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
                        .serviceId(uncapitalize(HealthService.Smoking.name()))
                        .name(HealthService.Smoking.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"data\":["
            + "{\"name\":\"Pensions\"},"
            + "{\"name\":\"Smoking\"},"
            + "{\"name\":\"foo\"}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\"},"
            + "{\"serviceId\":\"smoking\",\"name\":\"Smoking\"},"
            + "{\"serviceId\":\"foo\",\"name\":\"Pensions\"},"
            + "{\"serviceId\":\"bar\",\"name\":\"baz\"}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"name\":\"Pensions\",\"appointmentPhones\":[]},"
            + "{\"name\":\"Smoking\",\"appointmentPhones\":[]},"
            + "{\"name\":\"foo\",\"appointmentPhones\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointmentPhones\":[]},"
            + "{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"appointmentPhones\":[]},"
            + "{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"appointmentPhones\":[]},"
            + "{\"serviceId\":\"bar\",\"name\":\"baz\",\"appointmentPhones\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"name\":\"Pensions\",\"appointmentPhones\":[],\"serviceLocations\":[]},"
            + "{\"name\":\"Smoking\",\"appointmentPhones\":[],\"serviceLocations\":[]},"
            + "{\"name\":\"foo\",\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"appointmentPhones\":[],\"serviceLocations\":[]},"
            + "{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"appointmentPhones\":[],\"serviceLocations\":[]},"
            + "{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"appointmentPhones\":[],\"serviceLocations\":[]},"
            + "{\"serviceId\":\"bar\",\"name\":\"baz\",\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
  }

  @Test
  @SneakyThrows
  void deserializeHealthCmsOverlay() {
    CmsOverlay overlay =
        CmsOverlay.builder()
            .detailedServices(
                List.of(
                    DetailedService.builder()
                        .serviceId(uncapitalize(HealthService.Dental.name()))
                        .name(HealthService.Dental.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailedServices\":[" + "{\"name\":\"Dental\"}" + "]}", CmsOverlay.class, overlay);
    assertJson(
        "{\"detailedServices\":[" + "{\"serviceId\":\"dental\",\"name\":\"Dental\"}" + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":[" + "{\"name\":\"Dental\",\"appointmentPhones\":[]}" + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceId\":\"dental\",\"name\":\"Dental\",\"appointmentPhones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"name\":\"Dental\",\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceId\":\"dental\",\"name\":\"Dental\",\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
  }

  @Test
  @SneakyThrows
  void deserializeHealthDetailedService() {
    DetailedService detailedService =
        DetailedService.builder()
            .serviceId(uncapitalize(HealthService.Dental.name()))
            .name(HealthService.Dental.name())
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson("{\"name\":\"Dental\"}", DetailedService.class, detailedService);
    assertJson(
        "{\"serviceId\":\"dental\",\"name\":\"Dental\"}", DetailedService.class, detailedService);
    assertJson(
        "{\"name\":\"Dental\",\"appointmentPhones\":[]}", DetailedService.class, detailedService);
    assertJson(
        "{\"serviceId\":\"dental\",\"name\":\"Dental\",\"appointmentPhones\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"name\":\"Dental\",\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"dental\",\"name\":\"Dental\",\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DetailedService.class,
        detailedService);
  }

  @Test
  @SneakyThrows
  void deserializeHealthDetailedServicesResponse() {
    DetailedServicesResponse response =
        DetailedServicesResponse.builder()
            .data(
                List.of(
                    DetailedService.builder()
                        .serviceId(uncapitalize(HealthService.Dental.name()))
                        .name(HealthService.Dental.name())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"data\":[" + "{\"name\":\"Dental\"}" + "]}", DetailedServicesResponse.class, response);
    assertJson(
        "{\"data\":[" + "{\"serviceId\":\"dental\",\"name\":\"Dental\"}" + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":[" + "{\"name\":\"Dental\",\"appointmentPhones\":[]}" + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceId\":\"dental\",\"name\":\"Dental\",\"appointmentPhones\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"name\":\"Dental\",\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceId\":\"dental\",\"name\":\"Dental\",\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
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
    invalidService.name("Smoking");
    assertJson(
        "{\"serviceId\":\"bar\",\"name\":\"Smoking\"}", DetailedService.class, invalidService);
  }

  @Test
  @SneakyThrows
  void deserializeOtherCmsOverlay() {
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
        "{\"detailedServices\":[" + "{\"name\":\"OnlineScheduling\"}" + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\"}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"name\":\"OnlineScheduling\",\"appointmentPhones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"appointmentPhones\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"name\":\"OnlineScheduling\",\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        CmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailedServices\":["
            + "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"appointmentPhones\":[],\"serviceLocations\":[]}"
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
        "{\"name\":\"OnlineScheduling\",\"appointmentPhones\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"appointmentPhones\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"name\":\"OnlineScheduling\",\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"appointmentPhones\":[],\"serviceLocations\":[]}",
        DetailedService.class,
        detailedService);
  }

  @Test
  @SneakyThrows
  void deserializeOtherDetailedServicesResponse() {
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
        "{\"data\":[" + "{\"name\":\"OnlineScheduling\",\"appointmentPhones\":[]}" + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"appointmentPhones\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"name\":\"OnlineScheduling\",\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
    assertJson(
        "{\"data\":["
            + "{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"appointmentPhones\":[],\"serviceLocations\":[]}"
            + "]}",
        DetailedServicesResponse.class,
        response);
  }
}
