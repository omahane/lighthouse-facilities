package gov.va.api.lighthouse.facilities.api.pssg;

import java.awt.geom.Path2D;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

/**
 * The Path Encoder provides a very tight serialization mechanism for PSSG drive time band data. It
 * allows serialization of {@link PssgDriveTimeBand} that can be deserialized directly to Java
 * Path2D objects.
 *
 * <p>Serialization uses a binary collection of integer values.
 *
 * <pre>
 *   raw := {magic-token}{version}{number-rings}{ring ...}
 *   ring := {number-coords}{coord ...}
 *   coord := {scaled-longitude}{scaled-latitude}
 *   magic-token := {int} First byte used to verify this array is understood
 *   version := {int} Indicates the version of binary data format
 *   number-rings := {int} The number of ring structures
 *   number-coords := {int} The number of coordinate structures
 *   scaled-longitude := {int} Floating point value multiple by 10000
 *   scaled-latitude := {int} Floating point value multiple by 10000
 *   {int} := 4 bytes
 *
 *   Scaled values can be divided by 10000.0 to return to double with precision 5.
 * </pre>
 */
@AllArgsConstructor(staticName = "create")
public class PathEncoder {
  /**
   * The amount of space required in a ByteBuffer for an integer value. This is used to determine
   * the required buffer size.
   */
  private static int BYTES_PER_INT = 4;

  /** Random number to indicate the binary packet type. */
  private static int MAGIC_NUMBER = 37337;

  /**
   * The current version of encoding. Should a new information be needed, this version will be
   * incremented and backwards compatibility will be need to be maintained.
   */
  private static int VERSION = 1;

  /**
   * Scaling factor applied to floating point values to integers. Since lat long values range
   * between -180 and 180, there is no concern of integer overflow.
   */
  private static int SCALE = 100000;

  private static int scale(double value) {
    return (int) (value * SCALE);
  }

  private static double unscale(int scaledValue) {
    return (double) scaledValue / (double) SCALE;
  }

  private void check(boolean condition, String message) {
    if (!condition) {
      throw new PathEncodingException(message);
    }
  }

  private byte[] compress(byte[] data) {
    Deflater compressor = new Deflater();
    compressor.setInput(data);
    compressor.finish();
    byte[] compressed = new byte[data.length];
    int size = compressor.deflate(compressed);
    return Arrays.copyOf(compressed, size);
  }

  private Path2D decode(byte[] compressPath) {
    byte[] pathData = decompress(compressPath);
    ByteBuffer buffer = ByteBuffer.wrap(pathData);
    check(buffer.getInt() == MAGIC_NUMBER, "Unknown magic number");
    check(buffer.getInt() == VERSION, "Unsupported version");
    /*
     * In the future, should a new format be required, the existing version MUST continue to be
     * supported. Database entries will already exist and we will need to keep supporting them.
     * Should that day come, this is where we'd switch deserializers on version number.
     */
    int numberOfRings = buffer.getInt();
    check(numberOfRings > 0, "Number of rings");
    Path2D path = new Path2D.Double();
    for (int ringNumber = 0; ringNumber < numberOfRings; ringNumber++) {
      int numberOfCoordinates = buffer.getInt();
      check(
          numberOfCoordinates > 0,
          "Number of coordinates for ring " + ringNumber + ": " + numberOfCoordinates);
      for (int coordNumber = 0; coordNumber < numberOfCoordinates; coordNumber++) {
        int scaledLong = buffer.getInt();
        int scaledLat = buffer.getInt();
        double longitude = unscale(scaledLong);
        double latitude = unscale(scaledLat);
        if (coordNumber == 0) {
          path.moveTo(longitude, latitude);
        } else {
          path.lineTo(longitude, latitude);
        }
      }
      path.closePath();
    }
    return path;
  }

  /**
   * Decode a Path from Base 64 encoded binary representation created by {@link
   * #encodeToBase64(PssgDriveTimeBand)}.
   */
  public Path2D decodeFromBase64(@NonNull String path64) {
    return decode(Base64.getDecoder().decode(path64));
  }

  @SneakyThrows
  private byte[] decompress(byte[] compressedData) {
    /*
     * When deserializing, we have to estimate how much space we need. According to zlib, typical
     * compression is between 2:1 to 5:1. (https://zlib.net/zlib_tech.html) We'll choose 10:1.
     */
    byte[] uncompressed = new byte[compressedData.length * 10];
    Inflater uncompressor = new Inflater();
    uncompressor.setInput(compressedData);
    int size = uncompressor.inflate(uncompressed);
    uncompressor.end();
    return Arrays.copyOf(uncompressed, size);
  }

  private byte[] encode(@NonNull PssgDriveTimeBand band) {
    ByteBuffer buffer = ByteBuffer.allocate(sizeOf(band));
    buffer.putInt(MAGIC_NUMBER);
    buffer.putInt(VERSION);
    /*
     * Encoding only needs to support the latest version. Should a format change be required in the
     * future, it can replace this format encoding, but decoding will still need to handle both.
     */
    buffer.putInt(band.geometry().rings().size());
    band.geometry()
        .rings()
        .forEach(
            ring -> {
              buffer.putInt(ring.size());
              ring.forEach(
                  coords -> {
                    int scaledLong = scale(coords.get(0));
                    int scaledLat = scale(coords.get(1));
                    buffer.putInt(scaledLong);
                    buffer.putInt(scaledLat);
                  });
            });
    return compress(buffer.array());
  }

  /** Encode a drive time band into a base 64 binary string that can be decoded into a Path. */
  public String encodeToBase64(@NonNull PssgDriveTimeBand band) {
    return Base64.getEncoder().encodeToString(encode(band));
  }

  /** Compute the amount of uncompressed space that will be required to serialize the band. */
  private int sizeOf(PssgDriveTimeBand band) {
    int sizeOfRings = band.geometry().rings().stream().mapToInt(this::sizeOfRing).sum();
    // magic-token + version +  number-rings + sizeOfRings
    return BYTES_PER_INT + BYTES_PER_INT + BYTES_PER_INT + sizeOfRings;
  }

  private int sizeOfRing(List<List<Double>> ring) {
    // number-coords + (coords lat and long)
    return BYTES_PER_INT + BYTES_PER_INT * ring.size() * 2;
  }

  /** Should something go wrong ... you get this. */
  public static final class PathEncodingException extends RuntimeException {
    public PathEncodingException(String message) {
      super(message);
    }
  }
}
