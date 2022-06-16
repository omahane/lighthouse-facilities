package gov.va.api.lighthouse.facilities.api.v0;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

/** This is the response returned by the internal management API when reloading facilities data. */
@Builder
@Data
@JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
public class ReloadResponse {
  public List<String> facilitiesUpdated;

  public List<String> facilitiesRevived;

  public List<String> facilitiesCreated;

  public List<String> facilitiesMissing;

  public List<String> facilitiesRemoved;

  public List<Problem> problems;

  public Timing timing;

  int totalFacilities;

  /**
   * Create an instance that is has thread safe collections that can be added to when processing
   * records simultaneously and has timing initialized to start now.
   */
  public static ReloadResponse start() {
    return ReloadResponse.builder()
        .timing(Timing.builder().start(Instant.now()).build())
        .facilitiesUpdated(new CopyOnWriteArrayList<>())
        .facilitiesRevived(new CopyOnWriteArrayList<>())
        .facilitiesCreated(new CopyOnWriteArrayList<>())
        .facilitiesMissing(new CopyOnWriteArrayList<>())
        .facilitiesRemoved(new CopyOnWriteArrayList<>())
        .problems(new CopyOnWriteArrayList<>())
        .build();
  }

  @Builder
  @Value
  @AllArgsConstructor(staticName = "of")
  @JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
  public static final class Problem {
    String facilityId;

    String description;

    String data;

    public static Problem of(String facilityId, String description) {
      return Problem.of(facilityId, description, "");
    }
  }

  @Builder
  @Data
  @JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
  public static final class Timing {
    /** The time we started the reload process. */
    public Instant start;

    /**
     * The time after we started and completed the collection phase, but not yet started updating
     * the database.
     */
    public Instant completeCollection;

    /** The time completed all work (including the DB updates). */
    public Instant complete;

    /** The amount of time it took to perform the full reload cycle. */
    public Duration totalDuration;

    /** Set the 'complete' time to now and compute the 'totalDuration'. */
    public void markComplete() {
      complete = Instant.now();
      totalDuration = Duration.between(start, complete);
    }

    /** Set the 'completeCollection' time to now. */
    public void markCompleteCollection() {
      completeCollection = Instant.now();
    }
  }
}
