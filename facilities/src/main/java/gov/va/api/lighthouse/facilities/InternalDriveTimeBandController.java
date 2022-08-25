package gov.va.api.lighthouse.facilities;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import gov.va.api.health.autoconfig.logging.Loggable;
import gov.va.api.lighthouse.facilities.api.pssg.BandResult;
import gov.va.api.lighthouse.facilities.api.pssg.BandUpdateResponse;
import gov.va.api.lighthouse.facilities.api.pssg.PathEncoder;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand;
import gov.va.api.lighthouse.facilities.api.pssg.PssgResponse;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@Validated
@RestController
@RequestMapping(value = "/internal/management/bands", produces = "application/json")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class InternalDriveTimeBandController {
  private final DriveTimeBandRepository repository;

  @GetMapping("/{name}")
  BandResult band(@PathVariable("name") String name) {
    return repository
        .findById(DriveTimeBandEntity.Pk.fromName(name))
        .map(
            result ->
                BandResult.builder()
                    .stationNumber(result.id().stationNumber())
                    .fromMinutes(result.id().fromMinutes())
                    .toMinutes(result.id().toMinutes())
                    .minLatitude(result.minLatitude())
                    .minLongitude(result.minLongitude())
                    .maxLatitude(result.maxLatitude())
                    .maxLongitude(result.maxLongitude())
                    .monthYear(result.monthYear())
                    .band(result.band())
                    .version(result.version() == null ? 0 : result.version())
                    .build())
        .orElseThrow(() -> new ExceptionsUtils.NotFound(name));
  }

  @GetMapping("/versions")
  List<String> bandVersions() {
    return repository.findAllBandVersions();
  }

  private Rectangle2D boundsOf(PssgDriveTimeBand band) {
    var rect = new AtomicReference<Rectangle2D>();
    band.geometry().rings().stream()
        .flatMap(r -> r.stream())
        .forEach(
            coord -> {
              double x = coord.get(PssgDriveTimeBand.INDEX_LONGITUDE);
              double y = coord.get(PssgDriveTimeBand.INDEX_LATITUDE);
              if (rect.get() == null) {
                rect.set(new Rectangle2D.Double(x, y, x, y));
              } else {
                rect.get().add(x, y);
              }
            });
    /* If there now rings for some reason, bounds are a point. */
    if (rect.get() == null) {
      return new Rectangle2D.Double();
    }
    return rect.get();
  }

  @GetMapping
  List<String> driveTimeBandIds() {
    return repository.findAllIds().stream().map(DriveTimeBandEntity.Pk::name).collect(toList());
  }

  @Loggable(arguments = false)
  @PostMapping(consumes = "application/json")
  BandUpdateResponse update(@RequestBody PssgResponse pssg) {
    List<PssgDriveTimeBand> bands = Optional.ofNullable(pssg.features()).orElse(emptyList());
    log.info("Updating {} bands", bands.size());
    BandUpdateResponse response =
        BandUpdateResponse.builder()
            .bandsCreated(new CopyOnWriteArrayList<>())
            .bandsUpdated(new CopyOnWriteArrayList<>())
            .build();
    bands.stream().forEach(f -> updateBand(f, response));
    return response;
  }

  @SneakyThrows
  private void updateBand(@NonNull PssgDriveTimeBand band, @NonNull BandUpdateResponse response) {
    var pk =
        DriveTimeBandEntity.Pk.of(
            band.attributes().stationNumber(),
            band.attributes().fromBreak(),
            band.attributes().toBreak());
    var entity = repository.findById(pk).orElse(null);
    if (entity == null) {
      entity = DriveTimeBandEntity.builder().id(pk).build();
      response.bandsCreated().add(pk.name());
    } else {
      response.bandsUpdated().add(pk.name());
    }

    var bounds = boundsOf(band);
    entity.minLongitude(bounds.getMinX());
    entity.minLatitude(bounds.getMinY());
    entity.maxLongitude(bounds.getMaxX());
    entity.maxLatitude(bounds.getMaxY());
    entity.monthYear(band.attributes().monthYear());
    entity.band(PathEncoder.create().encodeToBase64(band));
    repository.save(entity);
  }
}
