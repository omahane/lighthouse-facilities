package gov.va.api.lighthouse.facilities.api.pssg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(value = Include.NON_EMPTY, content = Include.NON_EMPTY)
public class BandResult {
  String stationNumber;
  int fromMinutes;
  int toMinutes;
  double minLatitude;
  double minLongitude;
  double maxLatitude;
  double maxLongitude;
  String monthYear;
  String band;
  int version;
}
