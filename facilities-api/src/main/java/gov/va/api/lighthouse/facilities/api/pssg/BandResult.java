package gov.va.api.lighthouse.facilities.api.pssg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
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
