package gov.va.api.lighthouse.facilities.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

public class TypeOfServiceTest {
  @Test
  void fromString() {
    // Valid types of service
    Arrays.stream(TypeOfService.values())
        .parallel()
        .forEach(
            tos -> {
              assertThat(TypeOfService.fromString(tos.name()))
                  .isEqualTo(TypeOfService.valueOf(tos.name()));
            });
    Arrays.stream(TypeOfService.values())
        .parallel()
        .forEach(
            tos -> {
              assertThat(TypeOfService.fromString(StringUtils.uncapitalize(tos.name())))
                  .isEqualTo(TypeOfService.valueOf(tos.name()));
            });
    // Invalid type of service
    assertThatThrownBy(() -> TypeOfService.fromString("foo"))
        .isInstanceOf(Exception.class)
        .hasMessage("Unrecognized service type: foo");
  }

  @Test
  void isRecognizedTypeOfService() {
    // Valid types of service
    Arrays.stream(TypeOfService.values())
        .parallel()
        .forEach(
            tos -> {
              assertThat(TypeOfService.isRecognizedTypeOfService(tos.name())).isTrue();
            });
    Arrays.stream(TypeOfService.values())
        .parallel()
        .forEach(
            tos -> {
              assertThat(
                      TypeOfService.isRecognizedTypeOfService(StringUtils.uncapitalize(tos.name())))
                  .isTrue();
            });
    // Invalid type of service
    assertThat(TypeOfService.isRecognizedTypeOfService("foo")).isFalse();
  }
}
