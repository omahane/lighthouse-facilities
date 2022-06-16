package gov.va.api.lighthouse.facilities.api.v1;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import gov.va.api.lighthouse.facilities.api.v1.serializers.ReloadResponseProblemSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.ReloadResponseSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.ReloadResponseTimingSerializer;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import org.apache.commons.lang3.ObjectUtils;

/** This is the response returned by the internal management API when reloading facilities data. */
@Builder
@Data
@JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
@JsonSerialize(using = ReloadResponseSerializer.class)
public class ReloadResponse implements CanBeEmpty {
  public List<String> facilitiesUpdated;

  public List<String> facilitiesRevived;

  public List<String> facilitiesCreated;

  public List<String> facilitiesMissing;

  public List<String> facilitiesRemoved;

  public List<Problem> problems;

  public Timing timing;

  BigInteger totalFacilities;

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

  /** Empty elements will be omitted from JSON serialization. */
  @JsonIgnore
  public boolean isEmpty() {
    return ObjectUtils.isEmpty(facilitiesUpdated())
        && ObjectUtils.isEmpty(facilitiesRevived())
        && ObjectUtils.isEmpty(facilitiesCreated())
        && ObjectUtils.isEmpty(facilitiesMissing())
        && ObjectUtils.isEmpty(facilitiesRemoved())
        && ObjectUtils.isEmpty(problems())
        && (timing() == null || timing().isEmpty())
        && ObjectUtils.isEmpty(totalFacilities());
  }

  @Builder
  @Value
  @AllArgsConstructor(staticName = "of")
  @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
  @JsonSerialize(using = ReloadResponseProblemSerializer.class)
  public static final class Problem implements CanBeEmpty {
    String facilityId;

    String description;

    String data;

    public static Problem of(String facilityId, String description) {
      return Problem.of(facilityId, description, EMPTY);
    }

    /** Empty elements will be omitted from JSON serialization. */
    @JsonIgnore
    public boolean isEmpty() {
      return isBlank(facilityId()) && isBlank(description()) && isBlank(data());
    }
  }

  @Builder
  @Data
  @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
  @JsonSerialize(using = ReloadResponseTimingSerializer.class)
  public static final class Timing implements CanBeEmpty {
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

    /** Empty elements will be omitted from JSON serialization. */
    @JsonIgnore
    public boolean isEmpty() {
      return ObjectUtils.isEmpty(start())
          && ObjectUtils.isEmpty(completeCollection())
          && ObjectUtils.isEmpty(complete())
          && ObjectUtils.isEmpty(totalDuration());
    }

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
