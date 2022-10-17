package gov.va.api.lighthouse.facilities.api.pssg;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Offset.offset;

import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand.Attributes;
import gov.va.api.lighthouse.facilities.api.pssg.PssgDriveTimeBand.Geometry;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class PathEncoderTest {
  @Test
  @SneakyThrows
  void exceptions() {
    Method check = PathEncoder.class.getDeclaredMethod("check", boolean.class, String.class);
    check.setAccessible(true);
    assertThatThrownBy(() -> check.invoke(PathEncoder.create(), false, "Test Message"))
        .isInstanceOf(InvocationTargetException.class)
        .hasCause(new PathEncoder.PathEncodingException("Test Message"));
    Method decompress = PathEncoder.class.getDeclaredMethod("decompress", byte[].class);
    decompress.setAccessible(true);
    byte[] nullData = null;
    assertThatThrownBy(() -> decompress.invoke(PathEncoder.create(), nullData))
        .isInstanceOf(InvocationTargetException.class)
        .hasCause(
            new NullPointerException(
                "Cannot read the array length because \"compressedData\" is null"));
  }

  @Test
  void roundTrip() {
    List<List<Double>> ring1 = PssgDriveTimeBand.newRing(6);
    ring1.add(PssgDriveTimeBand.coord(1.111, -1.111));
    ring1.add(PssgDriveTimeBand.coord(1.111, -4.444));
    ring1.add(PssgDriveTimeBand.coord(2.222, -4.444));
    ring1.add(PssgDriveTimeBand.coord(4.444, -4.444));
    ring1.add(PssgDriveTimeBand.coord(4.444, -1.111));
    ring1.add(PssgDriveTimeBand.coord(2.222, -1.111));
    List<List<Double>> ring2 = PssgDriveTimeBand.newRing(6);
    ring2.add(PssgDriveTimeBand.coord(10.111, -10.111));
    ring2.add(PssgDriveTimeBand.coord(10.111, -40.444));
    ring2.add(PssgDriveTimeBand.coord(20.222, -40.444));
    ring2.add(PssgDriveTimeBand.coord(40.444, -40.444));
    ring2.add(PssgDriveTimeBand.coord(40.444, -10.111));
    ring2.add(PssgDriveTimeBand.coord(20.222, -10.111));
    List<List<List<Double>>> rings = PssgDriveTimeBand.newListOfRings();
    rings.add(ring1);
    rings.add(ring2);
    var band =
        PssgDriveTimeBand.builder()
            .attributes(Attributes.builder().stationNumber("No1").fromBreak(10).toBreak(20).build())
            .geometry(Geometry.builder().rings(rings).build())
            .build();
    String path64 = PathEncoder.create().encodeToBase64(band);
    log.info("{}", path64.length());
    log.info(path64);
    Optional<Path2D> path = PathEncoder.create().decodeFromBase64(path64);
    if (path.isPresent()) {
      var iter = path.get().getPathIterator(null);
      double[] coords = new double[6];
      assertThat(iter.currentSegment(coords)).isEqualTo(PathIterator.SEG_MOVETO);
      assertThat(coords[0]).isCloseTo(1.111, offset(0.01));
      assertThat(coords[1]).isCloseTo(-1.111, offset(0.01));
      iter.next();
      assertThat(iter.currentSegment(coords)).isEqualTo(PathIterator.SEG_LINETO);
      assertThat(coords[0]).isCloseTo(1.111, offset(0.01));
      assertThat(coords[1]).isCloseTo(-4.444, offset(0.01));
      iter.next();
      assertThat(iter.currentSegment(coords)).isEqualTo(PathIterator.SEG_LINETO);
      assertThat(coords[0]).isCloseTo(2.222, offset(0.01));
      assertThat(coords[1]).isCloseTo(-4.444, offset(0.01));
      iter.next();
      assertThat(iter.currentSegment(coords)).isEqualTo(PathIterator.SEG_LINETO);
      assertThat(coords[0]).isCloseTo(4.444, offset(0.01));
      assertThat(coords[1]).isCloseTo(-4.444, offset(0.01));
      iter.next();
      assertThat(iter.currentSegment(coords)).isEqualTo(PathIterator.SEG_LINETO);
      assertThat(coords[0]).isCloseTo(4.444, offset(0.01));
      assertThat(coords[1]).isCloseTo(-1.111, offset(0.01));
      iter.next();
      assertThat(iter.currentSegment(coords)).isEqualTo(PathIterator.SEG_LINETO);
      assertThat(coords[0]).isCloseTo(2.222, offset(0.01));
      assertThat(coords[1]).isCloseTo(-1.111, offset(0.01));
      iter.next();
      assertThat(iter.currentSegment(coords)).isEqualTo(PathIterator.SEG_CLOSE);
      // No points on close
      iter.next();
      assertThat(iter.currentSegment(coords)).isEqualTo(PathIterator.SEG_MOVETO);
      assertThat(coords[0]).isCloseTo(10.111, offset(0.01));
      assertThat(coords[1]).isCloseTo(-10.111, offset(0.01));
      iter.next();
      assertThat(iter.currentSegment(coords)).isEqualTo(PathIterator.SEG_LINETO);
      assertThat(coords[0]).isCloseTo(10.111, offset(0.01));
      assertThat(coords[1]).isCloseTo(-40.444, offset(0.01));
      iter.next();
      assertThat(iter.currentSegment(coords)).isEqualTo(PathIterator.SEG_LINETO);
      assertThat(coords[0]).isCloseTo(20.222, offset(0.01));
      assertThat(coords[1]).isCloseTo(-40.444, offset(0.01));
      iter.next();
      assertThat(iter.currentSegment(coords)).isEqualTo(PathIterator.SEG_LINETO);
      assertThat(coords[0]).isCloseTo(40.444, offset(0.01));
      assertThat(coords[1]).isCloseTo(-40.444, offset(0.01));
      iter.next();
      assertThat(iter.currentSegment(coords)).isEqualTo(PathIterator.SEG_LINETO);
      assertThat(coords[0]).isCloseTo(40.444, offset(0.01));
      assertThat(coords[1]).isCloseTo(-10.111, offset(0.01));
      iter.next();
      assertThat(iter.currentSegment(coords)).isEqualTo(PathIterator.SEG_LINETO);
      assertThat(coords[0]).isCloseTo(20.222, offset(0.01));
      assertThat(coords[1]).isCloseTo(-10.111, offset(0.01));
      iter.next();
      assertThat(iter.currentSegment(coords)).isEqualTo(PathIterator.SEG_CLOSE);
    }
  }
}
