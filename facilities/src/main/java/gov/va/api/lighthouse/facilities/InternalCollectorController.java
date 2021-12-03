package gov.va.api.lighthouse.facilities;

import gov.va.api.health.autoconfig.logging.Loggable;
import gov.va.api.lighthouse.facilities.collector.FacilitiesCollector;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Loggable
@RestController
@RequestMapping(value = "/internal/collector", produces = "application/json")
public class InternalCollectorController {
  private final JdbcTemplate jdbc;

  private final FacilitiesCollector collector;

  @Builder
  InternalCollectorController(
      @Autowired JdbcTemplate jdbc, @Autowired FacilitiesCollector collector) {
    this.jdbc = jdbc;
    this.collector = collector;
  }

  @SneakyThrows
  private List<Map<String, String>> allResults(String sql) {
    return jdbc.query(
        sql,
        (resultSet) -> {
          List<Map<String, String>> results = new ArrayList<>();
          ResultSetMetaData rsmd = resultSet.getMetaData();
          int columnCount = rsmd.getColumnCount();
          while (resultSet.next()) {
            Map<String, String> map = new LinkedHashMap<>();
            for (int i = 1; i <= columnCount; i++) {
              map.put(rsmd.getColumnName(i), resultSet.getString(i));
            }
            results.add(map);
          }
          return results;
        });
  }

  @GetMapping(value = "/facilities")
  List<DatamartFacility> collectFacilities() {
    return collector.collectFacilities();
  }

  @GetMapping(value = "/mental-health-contact")
  List<Map<String, String>> mentalHealthContacts() {
    try {
      return allResults("SELECT * FROM App.VHA_Mental_Health_Contact_Info");
    } catch (Exception ex) {
      throw new CdwException(ex);
    }
  }

  @GetMapping(value = "/nca")
  List<Map<String, String>> nca() {
    try {
      return allResults("SELECT * FROM App.FacilityLocator_NCA");
    } catch (Exception ex) {
      throw new CdwException(ex);
    }
  }

  @GetMapping(value = "/stop-code")
  List<Map<String, String>> stopCodes() {
    try {
      return allResults("SELECT * FROM App.VSSC_ClinicalServices");
    } catch (Exception ex) {
      throw new CdwException(ex);
    }
  }

  @GetMapping(value = "/vast")
  List<Map<String, String>> vast() {
    try {
      return allResults("SELECT * FROM App.Vast");
    } catch (Exception ex) {
      throw new CdwException(ex);
    }
  }

  @GetMapping(value = "/vba")
  List<Map<String, String>> vba() {
    try {
      return allResults("SELECT * FROM App.FacilityLocator_VBA");
    } catch (Exception ex) {
      throw new CdwException(ex);
    }
  }

  static final class CdwException extends RuntimeException {
    public CdwException(Throwable cause) {
      super(cause);
    }
  }
}
