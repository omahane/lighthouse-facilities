package gov.va.api.lighthouse.facilities.tests.internal;

import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentIn;
import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.CLIENT_KEY_DEFAULT;
import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.sentinel.Environment;
import gov.va.api.health.sentinel.ExpectedResponse;
import gov.va.api.lighthouse.facilities.api.pssg.BandUpdateResponse;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand;
import gov.va.api.lighthouse.facilities.api.pssg.PssgResponse;
import gov.va.api.lighthouse.facilities.tests.SystemDefinitions;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DriveTimeBandManagementIT {
  private static final ObjectMapper MAPPER = JacksonConfig.createMapper();

  @BeforeAll
  static void assumeEnvironment() {
    // These tests invent data that will not be cleaned up
    // To avoid polluting the database, they should only run locally
    assumeEnvironmentIn(Environment.LOCAL);
  }

  private static RequestSpecification requestSpecification() {
    SystemDefinitions.Service svc = systemDefinition().facilitiesInternal();
    return RestAssured.given()
        .baseUri(svc.url())
        .port(svc.port())
        .relaxedHTTPSValidation()
        .header("client-key", System.getProperty("client-key", CLIENT_KEY_DEFAULT));
  }

  @Test
  @SneakyThrows
  void canUpdateRecords() {
    List<PssgDriveTimeBand> bands =
        MAPPER.readValue(
            getClass().getResource("/pssg-drive-time-bands-0.json"),
            new TypeReference<List<PssgDriveTimeBand>>() {});
    PssgDriveTimeBand favorite = Iterables.getOnlyElement(bands);
    favorite.attributes().stationNumber("tmp" + Instant.now().getEpochSecond());
    String name =
        favorite.attributes().stationNumber()
            + "-"
            + favorite.attributes().fromBreak()
            + "-"
            + favorite.attributes().toBreak();

    ExpectedResponse.of(
            requestSpecification().request(Method.GET, "internal/management/bands/{name}", name))
        .expect(404);
    var updateResponse =
        ExpectedResponse.of(
                requestSpecification()
                    .contentType("application/json")
                    .body(PssgResponse.builder().features(bands).build())
                    .request(Method.POST, "internal/management/bands"))
            .expect(200)
            .expectValid(BandUpdateResponse.class);
    assertThat(updateResponse.bandsUpdated()).isEmpty();
    assertThat(updateResponse.bandsCreated()).containsExactly(name);

    var actual =
        ExpectedResponse.of(
                requestSpecification()
                    .request(Method.GET, "internal/management/bands/{name}", name))
            .expect(200)
            .expectValid(Map.class);
    assertThat(actual.get("stationNumber")).isEqualTo(favorite.attributes().stationNumber());

    var allIds =
        ExpectedResponse.of(requestSpecification().request(Method.GET, "internal/management/bands"))
            .expect(200)
            .expectListOf(String.class);
    assertThat(allIds).contains(name);

    // Re-attempt update of same list of pssg bands
    updateResponse =
        ExpectedResponse.of(
                requestSpecification()
                    .contentType("application/json")
                    .body(PssgResponse.builder().features(bands).build())
                    .request(Method.POST, "internal/management/bands"))
            .expect(200)
            .expectValid(BandUpdateResponse.class);
    assertThat(updateResponse.bandsUpdated()).containsExactly(name);
    assertThat(updateResponse.bandsCreated()).isEmpty();
  }

  @Test
  @SneakyThrows
  public void testNullGeometry() {
    List<PssgDriveTimeBand> bands =
        MAPPER.readValue(
            getClass().getResource("/pssg-drive-time-bands-1.json"),
            new TypeReference<List<PssgDriveTimeBand>>() {});
    PssgDriveTimeBand favorite = Iterables.getOnlyElement(bands);
    favorite.attributes().stationNumber("tmp" + Instant.now().getEpochSecond());
    String name =
        favorite.attributes().stationNumber()
            + "-"
            + favorite.attributes().fromBreak()
            + "-"
            + favorite.attributes().toBreak();

    ExpectedResponse.of(
            requestSpecification().request(Method.GET, "internal/management/bands/{name}", name))
        .expect(404);
    var updateResponse =
        ExpectedResponse.of(
                requestSpecification()
                    .contentType("application/json")
                    .body(PssgResponse.builder().features(bands).build())
                    .request(Method.POST, "internal/management/bands"))
            .expect(200)
            .expectValid(BandUpdateResponse.class);
    assertThat(updateResponse.bandsUpdated()).isEmpty();
    assertThat(updateResponse.bandsCreated()).containsExactly(name);

    var actual =
        ExpectedResponse.of(
                requestSpecification()
                    .request(Method.GET, "internal/management/bands/{name}", name))
            .expect(200)
            .expectValid(Map.class);
    assertThat(actual.get("stationNumber")).isEqualTo(favorite.attributes().stationNumber());

    var allIds =
        ExpectedResponse.of(requestSpecification().request(Method.GET, "internal/management/bands"))
            .expect(200)
            .expectListOf(String.class);
    assertThat(allIds).contains(name);

    // Re-attempt update of same list of pssg bands
    updateResponse =
        ExpectedResponse.of(
                requestSpecification()
                    .contentType("application/json")
                    .body(PssgResponse.builder().features(bands).build())
                    .request(Method.POST, "internal/management/bands"))
            .expect(200)
            .expectValid(BandUpdateResponse.class);
    assertThat(updateResponse.bandsUpdated()).containsExactly(name);
    assertThat(updateResponse.bandsCreated()).isEmpty();
  }
}
