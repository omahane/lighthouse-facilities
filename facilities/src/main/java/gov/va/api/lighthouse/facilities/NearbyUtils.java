package gov.va.api.lighthouse.facilities;

import static gov.va.api.health.autoconfig.logging.LogSanitizer.sanitize;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.pssg.PathEncoder;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NearbyUtils {
  static final DeprecatedPssgDriveTimeBandSupport deprecatedPssgDriveTimeBandSupport =
      new DeprecatedPssgDriveTimeBandSupport();

  static final Set<Integer> DRIVE_TIME_VALUES = Set.of(10, 20, 30, 40, 50, 60, 70, 80, 90);

  @SneakyThrows
  static Optional<DriveTimeBandEntity> firstIntersection(
      @NonNull Point2D point, List<DriveTimeBandEntity> entities) {
    Stopwatch timer = Stopwatch.createStarted();
    int count = 0;
    for (DriveTimeBandEntity entity : entities) {
      count++;
      Optional<Path2D> path2D = toPath(entity);
      if (path2D.isPresent() && path2D.get().contains(point)) {
        log.info(
            "Found {} intersection in {} ms, looked at {} of {} options",
            entity.id().stationNumber(),
            timer.elapsed(TimeUnit.MILLISECONDS),
            count,
            entities.size());
        return Optional.of(entity);
      }
      else {
        log.info("No path available for DTB {}", sanitize(entity.id().name()));
      }
    }
    log.info("No matches found in {} options", entities.size());
    return Optional.empty();
  }

  static Map<String, DriveTimeBandEntity> intersections(
      @NonNull BigDecimal longitude,
      @NonNull BigDecimal latitude,
      List<DriveTimeBandEntity> entities) {
    ListMultimap<String, DriveTimeBandEntity> bandsForStation = ArrayListMultimap.create();
    for (DriveTimeBandEntity e : entities) {
      bandsForStation.put(e.id().stationNumber(), e);
    }
    Point2D point = new Point2D.Double(longitude.doubleValue(), latitude.doubleValue());
    return bandsForStation.asMap().entrySet().parallelStream()
        .map(
            entry -> {
              List<DriveTimeBandEntity> sortedEntities =
                  entry.getValue().stream()
                      .sorted(Comparator.comparingInt(left -> left.id().fromMinutes()))
                      .collect(toList());
              return firstIntersection(point, sortedEntities).orElse(null);
            })
        .filter(Objects::nonNull)
        .collect(toMap(b -> b.id().stationNumber(), Function.identity()));
  }

  @SneakyThrows
  static Optional<Path2D> toPath(DriveTimeBandEntity entity) {
    if (deprecatedPssgDriveTimeBandSupport.isPssgDriveTimeBand(entity)) {
      return Optional.of(deprecatedPssgDriveTimeBandSupport.toPath(entity));
    }
    try {
      return PathEncoder.create().decodeFromBase64(entity.band());
    } catch (Exception e) {
      log.info("Failed to decode {}", entity.id());
      throw e;
    }
  }

  static Integer validateDriveTime(Integer val) {
    if (val != null && !DRIVE_TIME_VALUES.contains(val)) {
      throw new ExceptionsUtils.InvalidParameter("drive_time", val);
    }
    return val;
  }

  /**
   * This encapsulates the older support where PSSG drive time bands were stored directly as JSON.
   * They were big and slow, and we have our own serialization model now. But to keep supporting any
   * records that have not be converted yet, this class allows for a graceful transition. It can
   * deleted once all databases have been upgraded.
   */
  static final class DeprecatedPssgDriveTimeBandSupport {
    final ObjectMapper mapper = JacksonConfig.createMapper();

    boolean isPssgDriveTimeBand(DriveTimeBandEntity entity) {
      return entity.band().startsWith("{\"attributes");
    }

    @SneakyThrows
    Path2D toPath(DriveTimeBandEntity entity) {
      PssgDriveTimeBand asBand = mapper.readValue(entity.band(), PssgDriveTimeBand.class);
      List<List<List<Double>>> rings =
          asBand.geometry() == null ? Collections.emptyList() : asBand.geometry().rings();
      checkState(!rings.isEmpty());
      List<List<Double>> exteriorRing = rings.get(0);
      Path2D path2D = toPath2D(exteriorRing);
      for (int i = 1; i < rings.size(); i++) {
        List<List<Double>> interiorRing = rings.get(i);
        path2D.append(toPath2D(interiorRing), false);
      }
      return path2D;
    }

    Path2D toPath2D(List<List<Double>> coordinates) {
      checkArgument(!coordinates.isEmpty());
      Path2D shape = null;
      for (List<Double> c : coordinates) {
        if (shape == null) {
          shape = new Path2D.Double(Path2D.WIND_NON_ZERO);
          shape.moveTo(c.get(0), c.get(1));
        } else {
          shape.lineTo(c.get(0), c.get(1));
        }
      }
      shape.closePath();
      return shape;
    }
  }

  @Builder
  @lombok.Value
  static final class Coordinates {
    BigDecimal latitude;

    BigDecimal longitude;
  }

  @Builder
  @lombok.Value
  static final class NearbyId {
    public DriveTimeBandEntity.Pk bandId;

    String facilityId;
  }
}
