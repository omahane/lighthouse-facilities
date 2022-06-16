package gov.va.api.lighthouse.facilities.api;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class TestUtils {
  @SneakyThrows
  public static String getExpectedJson(String jsonFile) {
    ClassLoader classLoader = TestUtils.class.getClassLoader();
    URL resource = classLoader.getResource(jsonFile);
    if (resource == null) {
      throw new IllegalArgumentException("File not found! " + jsonFile);
    }
    File file = new File(resource.toURI());
    return Files.readAllLines(file.toPath(), StandardCharsets.UTF_8).stream()
        .collect(Collectors.joining(System.lineSeparator()));
  }
}
