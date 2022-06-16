package gov.va.api.lighthouse.facilities.api.v1.serializers;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import gov.va.api.lighthouse.facilities.api.v1.CanBeEmpty;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;

public abstract class NonEmptySerializer<T extends CanBeEmpty> extends StdSerializer<T> {

  public NonEmptySerializer(Class<T> t) {
    super(t);
  }

  private static boolean isNotBlankString(Object value) {
    return value instanceof String && isNotBlank((String) value);
  }

  private static boolean isNotEmptyObject(Object value) {
    return (value instanceof CanBeEmpty && !((CanBeEmpty) value).isEmpty())
        || (!(value instanceof CanBeEmpty) && !(value instanceof String));
  }

  private static Object trim(Object value) {
    if (value instanceof String) {
      return ((String) value).trim();
    }
    return value;
  }

  @SneakyThrows
  protected static void writeNonEmpty(JsonGenerator jgen, String fieldName, Object value) {
    if (ObjectUtils.isNotEmpty(value)) {
      if (isNotBlankString(value) || isNotEmptyObject(value)) {
        jgen.writeObjectField(fieldName, trim(value));
      }
    }
  }

  @Override
  public boolean isEmpty(SerializerProvider provider, T value) {
    return value == null || value.isEmpty();
  }
}
