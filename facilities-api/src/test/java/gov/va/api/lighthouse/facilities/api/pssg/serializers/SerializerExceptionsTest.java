package gov.va.api.lighthouse.facilities.api.pssg.serializers;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class SerializerExceptionsTest {

  @SneakyThrows
  private <T, U extends StdSerializer<T>> void assertNpeThrown(
      T value, U serializer, JsonGenerator jgen, SerializerProvider provider, String expectedMsg) {
    assertThatThrownBy(() -> serializer.serialize(value, jgen, provider))
        .isInstanceOf(NullPointerException.class)
        .hasMessage(expectedMsg);
  }

  @Test
  @SneakyThrows
  void bandUpdateResponseExceptions() {
    assertNpeThrown(
        null,
        new BandUpdateResponseSerializer(),
        mock(JsonGenerator.class),
        mock(SerializerProvider.class),
        "Cannot invoke \"gov.va.api.lighthouse.facilities.api.pssg.BandUpdateResponse.bandsCreated()\" because \"bandUpdateResponse\" is null");
  }
}
