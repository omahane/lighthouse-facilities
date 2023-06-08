package gov.va.api.lighthouse.facilitieslocaldb;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import com.google.common.base.Joiner;
import com.google.common.collect.Streams;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

@Value
@AllArgsConstructor(staticName = "of")
public final class Populaterator {
  Db db;

  private static String baseDir() {
    return System.getProperty("basedir", ".");
  }

  /** Populate local databases. */
  @SneakyThrows
  public static void main(String[] args) {
    if (Boolean.parseBoolean(System.getProperty("populator.h2", "true"))) {
      Populaterator.of(H2.builder().dbFile(baseDir() + "/target/mock-cdw").build()).populate();
    }
    if (Boolean.parseBoolean(System.getProperty("populator.sqlserver", "false"))) {
      Populaterator.of(
              SqlServer.builder()
                  .host("localhost")
                  .port("1533")
                  .user("SA")
                  .password("<YourStrong!Passw0rd>")
                  .collectorDatabase("fc")
                  .facilityDatabase("facility")
                  .build())
          .populate();
    }
  }

  @SneakyThrows
  private void bootstrap() {
    var conn = db.bootstrapConnection();
    if (conn.isEmpty()) {
      return;
    }
    for (var n : db.bootstrapDatabases()) {
      log("Creating database " + n);
      conn.get().prepareStatement("DROP DATABASE IF EXISTS " + n).execute();
      conn.get().prepareStatement("CREATE DATABASE " + n).execute();
    }
    conn.get().commit();
    conn.get().close();
  }

  @SneakyThrows
  private Connection createAppSchema(Connection connection) {
    log("Creating 'app' schema");
    connection.prepareStatement("CREATE SCHEMA APP").execute();
    return connection;
  }

  private void log(String msg) {
    System.out.println(db.name() + ": " + msg);
  }

  @SneakyThrows
  private void mentalHealthContacts(Connection connection) {
    log("Populating mental health contacts");
    connection
        .prepareStatement(
            "CREATE TABLE App.VHA_Mental_Health_Contact_Info ("
                + "ID VARCHAR(MAX),"
                + "Region FLOAT,"
                + "VISN VARCHAR(MAX),"
                + "AdminParent FLOAT,"
                + "StationNumber VARCHAR(MAX),"
                + "MHClinicPhone FLOAT,"
                + "MHPhone VARCHAR(MAX),"
                + "Extension FLOAT,"
                + "OfficialStationName VARCHAR(MAX),"
                + "\"POC Email\" VARCHAR(MAX),"
                + "Status VARCHAR(MAX),"
                + "Modified VARCHAR(MAX),"
                + "Created VARCHAR(MAX),"
                + "AddedToOutbox VARCHAR(MAX)"
                + ")")
        .execute();
    try (InputStreamReader reader =
        new FileReader(
            new File(baseDir() + "/src/test/resources/mental-health-contact.csv"),
            StandardCharsets.UTF_8)) {
      Iterable<CSVRecord> rows = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
      for (CSVRecord row : rows) {
        try (PreparedStatement statement =
            connection.prepareStatement(
                "INSERT INTO App.VHA_Mental_Health_Contact_Info ("
                    + "ID,"
                    + "Region,"
                    + "VISN,"
                    + "AdminParent,"
                    + "StationNumber,"
                    + "MHClinicPhone,"
                    + "MHPhone,"
                    + "Extension,"
                    + "OfficialStationName,"
                    + "\"POC Email\","
                    + "Status,"
                    + "Modified,"
                    + "Created,"
                    + "AddedToOutbox"
                    + ") VALUES ("
                    + IntStream.range(0, 14).mapToObj(v -> "?").collect(joining(","))
                    + ")")) {
          int paramIndex = 1;
          for (Object val : row) {
            statement.setObject(
                paramIndex, String.valueOf(val).equalsIgnoreCase("null") ? null : val);
            paramIndex++;
          }
          statement.execute();
        }
      }
    }
  }

  @SneakyThrows
  private void nca(Connection connection) {
    log("Populating National Cemeteries");
    try (InputStreamReader reader =
        new FileReader(
            new File(baseDir() + "/src/test/resources/nca.csv"), StandardCharsets.UTF_8)) {
      CSVParser parser = CSVFormat.DEFAULT.withHeader().withSkipHeaderRecord().parse(reader);
      List<String> headers = parser.getHeaderNames();
      connection
          .prepareStatement(
              "CREATE TABLE App.FacilityLocator_NCA ("
                  + headers.stream().map(h -> h + " VARCHAR(MAX)").collect(joining(","))
                  + ")")
          .execute();
      for (CSVRecord row : parser) {
        try (PreparedStatement statement =
            connection.prepareStatement(
                String.format(
                    "INSERT INTO App.FacilityLocator_NCA (%s) VALUES (%s)",
                    Joiner.on(",").join(headers),
                    headers.stream().map(h -> "?").collect(joining(","))))) {
          int paramIndex = 1;
          for (Object val : row) {
            statement.setObject(
                paramIndex, String.valueOf(val).equalsIgnoreCase("null") ? null : val);
            paramIndex++;
          }
          statement.execute();
        }
      }
    }
  }

  /** Populate the database. */
  @SneakyThrows
  void populate() {
    log("Populating " + db.name() + " database");
    bootstrap();
    log("Initializing facility collector database.");
    var connection = db.connection();
    createAppSchema(connection);
    mentalHealthContacts(connection);
    nca(connection);
    stopCodes(connection);
    vast(connection);
    vba(connection);
    connection.commit();
    connection.close();
    log("Finished " + db.name());
  }

  @SneakyThrows
  private void stopCodes(Connection connection) {
    log("Populating stop codes");
    connection
        .prepareStatement(
            "CREATE TABLE App.VSSC_ClinicalServices ("
                + "DIVISION_FCDMD VARCHAR(MAX),"
                + "CocClassification VARCHAR(MAX),"
                + "Sta6a VARCHAR(MAX),"
                + "PrimaryStopCode VARCHAR(MAX),"
                + "PrimaryStopCodeName VARCHAR(MAX),"
                + "NumberOfAppointments INT,"
                + "NumberOfAppointmentsLinkedToConsult INT,"
                + "NumberOfLocations INT,"
                + "AvgWaitTimeNew VARCHAR(MAX),"
                + "AvgWaitTimeNew_Appointments INT"
                + ")")
        .execute();
    try (InputStreamReader reader =
        new FileReader(
            new File(baseDir() + "/src/test/resources/clinical-services.csv"),
            StandardCharsets.UTF_8)) {
      CSVParser parser = CSVFormat.DEFAULT.withHeader().withSkipHeaderRecord().parse(reader);
      List<String> headers = parser.getHeaderNames();
      Streams.stream(parser)
          .parallel()
          .forEach(
              row -> {
                try (PreparedStatement statement =
                    connection.prepareStatement(
                        String.format(
                            "INSERT INTO App.VSSC_ClinicalServices (%s) VALUES (%s)",
                            Joiner.on(",").join(headers),
                            headers.stream().map(h -> "?").collect(joining(","))))) {
                  int paramIndex = 1;
                  for (Object val : row) {
                    statement.setObject(
                        paramIndex, String.valueOf(val).equalsIgnoreCase("null") ? null : val);
                    paramIndex++;
                  }
                  statement.execute();
                } catch (Throwable tr) {
                  throw new RuntimeException(tr);
                }
              });
    }
  }

  @SneakyThrows
  private void vast(Connection connection) {
    log("Populating VAST health facilities and vet centers");
    try (InputStreamReader reader =
        new FileReader(
            new File(baseDir() + "/src/test/resources/vast.csv"), StandardCharsets.UTF_8)) {
      CSVParser parser = CSVFormat.DEFAULT.withHeader().withSkipHeaderRecord().parse(reader);
      List<String> headers = parser.getHeaderNames();
      connection
          .prepareStatement(
              "CREATE TABLE App.Vast ("
                  + headers.stream()
                      .map(
                          h ->
                              StringUtils.equals(h, "LastUpdated")
                                  ? h + " smalldatetime"
                                  : h + " VARCHAR(MAX)")
                      .collect(joining(","))
                  + ")")
          .execute();
      for (CSVRecord row : parser) {
        try (PreparedStatement statement =
            connection.prepareStatement(
                "INSERT INTO App.Vast ("
                    + Joiner.on(",").join(headers)
                    + ") VALUES ("
                    + headers.stream().map(h -> "?").collect(joining(","))
                    + ")")) {
          int paramIndex = 1;
          for (Object val : row) {
            if (paramIndex == headers.stream().collect(toList()).indexOf("LastUpdated") + 1) {
              // will be overwritten afterward with current time
              statement.setObject(paramIndex, null);
            } else {
              statement.setObject(
                  paramIndex, String.valueOf(val).equalsIgnoreCase("null") ? null : val);
            }
            paramIndex++;
          }
          statement.execute();
        }
      }
    }
    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S").withZone(ZoneOffset.UTC);
    connection
        .prepareStatement(
            String.format("UPDATE App.Vast SET LastUpdated='%s'", formatter.format(Instant.now())))
        .execute();
  }

  @SneakyThrows
  private void vba(Connection connection) {
    log("Populating Benefit Centers");
    try (InputStreamReader reader =
        new FileReader(
            new File(baseDir() + "/src/test/resources/vba.csv"), StandardCharsets.UTF_8)) {
      CSVParser parser = CSVFormat.DEFAULT.withHeader().withSkipHeaderRecord().parse(reader);
      List<String> headers = parser.getHeaderNames();
      connection
          .prepareStatement(
              "CREATE TABLE App.FacilityLocator_VBA ("
                  + headers.stream().map(h -> h + " VARCHAR(MAX)").collect(joining(","))
                  + ")")
          .execute();
      for (CSVRecord row : parser) {
        try (PreparedStatement statement =
            connection.prepareStatement(
                String.format(
                    "INSERT INTO App.FacilityLocator_VBA (%s) VALUES (%s)",
                    Joiner.on(",").join(headers),
                    headers.stream().map(h -> "?").collect(joining(","))))) {
          int paramIndex = 1;
          for (Object val : row) {
            statement.setObject(
                paramIndex, String.valueOf(val).equalsIgnoreCase("null") ? null : val);
            paramIndex++;
          }
          statement.execute();
        }
      }
    }
  }

  interface Db {
    /** If a bootstrap connection is provided, databases will be (re)created. */
    Optional<Connection> bootstrapConnection();

    default List<String> bootstrapDatabases() {
      return List.of();
    }

    Connection connection();

    default String name() {
      return getClass().getSimpleName();
    }
  }

  @Value
  @Builder
  static final class H2 implements Db {
    String dbFile;

    @Override
    public Optional<Connection> bootstrapConnection() {
      return Optional.empty();
    }

    @Override
    @SneakyThrows
    public Connection connection() {
      new File(dbFile + ".mv.db").delete();
      new File(dbFile + ".trace.db").delete();
      return DriverManager.getConnection("jdbc:h2:" + dbFile, "sa", "sa");
    }
  }

  @Value
  @Builder
  static final class SqlServer implements Db {
    String host;

    String port;

    String user;

    String password;

    String collectorDatabase;

    String facilityDatabase;

    @Override
    @SneakyThrows
    public Optional<Connection> bootstrapConnection() {
      String bootstrapUrl =
          String.format(
              "jdbc:sqlserver://%s:%s;user=%S;password=%s;encrypt=true;trustServerCertificate=true",
              host, port, user, password);
      return Optional.of(DriverManager.getConnection(bootstrapUrl));
    }

    @Override
    public List<String> bootstrapDatabases() {
      return List.of(collectorDatabase, facilityDatabase);
    }

    @Override
    @SneakyThrows
    public Connection connection() {
      return DriverManager.getConnection(
          String.format(
              "jdbc:sqlserver://%s:%s;user=%S;password=%s;database=%s;encrypt=true;trustServerCertificate=true",
              host, port, user, password, collectorDatabase));
    }
  }
}
