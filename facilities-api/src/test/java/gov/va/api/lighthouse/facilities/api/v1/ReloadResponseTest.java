package gov.va.api.lighthouse.facilities.api.v1;

import static gov.va.api.lighthouse.facilities.api.TestUtils.getExpectedJson;
import static gov.va.api.lighthouse.facilities.api.v1.SerializerUtil.createMapper;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class ReloadResponseTest {
  @Test
  @SneakyThrows
  void emptyProblem() {
    // Empty
    assertThat(ReloadResponse.Problem.builder().build().isEmpty());
    String blank = "   ";
    assertThat(ReloadResponse.Problem.builder().description(blank).build().isEmpty()).isTrue();
    assertThat(ReloadResponse.Problem.builder().facilityId(blank).build().isEmpty()).isTrue();
    assertThat(ReloadResponse.Problem.builder().data(blank).build().isEmpty()).isTrue();
    // Not empty
    String notBlank = "test";
    assertThat(ReloadResponse.Problem.builder().description(notBlank).build().isEmpty()).isFalse();
    assertThat(ReloadResponse.Problem.builder().facilityId(notBlank).build().isEmpty()).isFalse();
    assertThat(ReloadResponse.Problem.builder().data(notBlank).build().isEmpty()).isFalse();
  }

  @Test
  @SneakyThrows
  void emptyTiming() {
    // Empty
    assertThat(ReloadResponse.Timing.builder().build().isEmpty()).isTrue();
    // Not empty
    assertThat(ReloadResponse.Timing.builder().start(Instant.now()).build().isEmpty()).isFalse();
    assertThat(ReloadResponse.Timing.builder().complete(Instant.now()).build().isEmpty()).isFalse();
    assertThat(ReloadResponse.Timing.builder().completeCollection(Instant.now()).build().isEmpty())
        .isFalse();
    assertThat(ReloadResponse.Timing.builder().totalDuration(Duration.ZERO).build().isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void isEmpty() {
    // Empty
    assertThat(ReloadResponse.builder().build().isEmpty()).isTrue();
    assertThat(ReloadResponse.builder().facilitiesCreated(emptyList()).build().isEmpty()).isTrue();
    assertThat(ReloadResponse.builder().facilitiesMissing(emptyList()).build().isEmpty()).isTrue();
    assertThat(ReloadResponse.builder().facilitiesRemoved(emptyList()).build().isEmpty()).isTrue();
    assertThat(ReloadResponse.builder().facilitiesRevived(emptyList()).build().isEmpty()).isTrue();
    assertThat(ReloadResponse.builder().facilitiesUpdated(emptyList()).build().isEmpty()).isTrue();
    assertThat(ReloadResponse.builder().problems(emptyList()).build().isEmpty()).isTrue();
    assertThat(
            ReloadResponse.builder()
                .timing(ReloadResponse.Timing.builder().build())
                .build()
                .isEmpty())
        .isTrue();
    // Not empty
    assertThat(ReloadResponse.builder().facilitiesCreated(List.of("vha_402")).build().isEmpty())
        .isFalse();
    assertThat(ReloadResponse.builder().facilitiesMissing(List.of("vha_402")).build().isEmpty())
        .isFalse();
    assertThat(ReloadResponse.builder().facilitiesRemoved(List.of("vha_402")).build().isEmpty())
        .isFalse();
    assertThat(ReloadResponse.builder().facilitiesRevived(List.of("vha_402")).build().isEmpty())
        .isFalse();
    assertThat(ReloadResponse.builder().facilitiesUpdated(List.of("vha_402")).build().isEmpty())
        .isFalse();
    assertThat(
            ReloadResponse.builder()
                .problems(List.of(ReloadResponse.Problem.builder().description("test").build()))
                .build()
                .isEmpty())
        .isFalse();
    assertThat(
            ReloadResponse.builder()
                .timing(ReloadResponse.Timing.builder().start(Instant.now()).build())
                .build()
                .isEmpty())
        .isFalse();
    assertThat(ReloadResponse.builder().totalFacilities(BigInteger.TEN).build().isEmpty())
        .isFalse();
  }

  @Test
  @SneakyThrows
  void reloadMarkCompleted() {
    final long MILLIS = 250;
    Instant start = Instant.now();
    ReloadResponse response =
        ReloadResponse.builder()
            .timing(ReloadResponse.Timing.builder().start(start).build())
            .facilitiesUpdated(List.of("Update facility"))
            .facilitiesRevived(List.of("Revived facility"))
            .facilitiesCreated(List.of("Created facility"))
            .facilitiesMissing(List.of("Missing facility"))
            .facilitiesRemoved(List.of("Removed facility"))
            .problems(List.of(ReloadResponse.Problem.of("vha_689", "Problem statement")))
            .build();
    Thread.sleep(MILLIS);
    response.timing().markComplete();
    Instant finish = Instant.now();
    assertThat(response.timing.complete).isBetween(start.plusMillis(MILLIS), finish);
    assertThat(response.timing.totalDuration)
        .isBetween(Duration.ofMillis(MILLIS), Duration.ofMillis(MILLIS + 50));
    start = Instant.now();
    Thread.sleep(MILLIS);
    response.timing.markCompleteCollection();
    finish = Instant.now();
    assertThat(response.timing.completeCollection).isBetween(start.plusMillis(MILLIS), finish);
  }

  @Test
  @SneakyThrows
  void responseWithEmptyFields() {
    // Null out fields for reload response
    String jsonEmptyReloadResponse =
        getExpectedJson("v1/ReloadResponse/responseWithEmptyFields.json");
    ReloadResponse emptyPageLinks =
        ReloadResponse.builder()
            .problems(null)
            .facilitiesCreated(null)
            .facilitiesMissing(null)
            .facilitiesRemoved(null)
            .facilitiesRevived(null)
            .facilitiesUpdated(null)
            .timing(null)
            .build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyPageLinks))
        .isEqualTo(jsonEmptyReloadResponse);
    // Reload response with empty fields
    emptyPageLinks =
        ReloadResponse.builder()
            .problems(emptyList())
            .facilitiesCreated(emptyList())
            .facilitiesMissing(emptyList())
            .facilitiesRemoved(emptyList())
            .facilitiesRevived(emptyList())
            .facilitiesUpdated(emptyList())
            .timing(null)
            .build();
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(emptyPageLinks))
        .isEqualTo(jsonEmptyReloadResponse);
  }

  @Test
  void startReloadResponse() {
    Instant start = Instant.now();
    ReloadResponse response = ReloadResponse.start();
    assertThat(response.timing.start).isBetween(start, start.plusMillis(50));
    assertThat(response.facilitiesUpdated).isEqualTo(List.of());
    assertThat(response.facilitiesRevived).isEqualTo(List.of());
    assertThat(response.facilitiesCreated).isEqualTo(List.of());
    assertThat(response.facilitiesMissing).isEqualTo(List.of());
    assertThat(response.facilitiesRemoved).isEqualTo(List.of());
    assertThat(response.problems).isEqualTo(List.of());
  }
}
