package gov.va.api.lighthouse.facilities.collector;

import static org.apache.commons.lang3.StringUtils.length;

import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
final class LocalDateHelper {
  public static LocalDate sliceToDate(String slice) {
    return length(slice) <= 9 ? null : LocalDate.parse(slice.substring(0, 10));
  }
}
