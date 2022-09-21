package gov.va.api.lighthouse.facilities.collector;

import static com.google.common.base.Preconditions.checkState;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService;
import static gov.va.api.lighthouse.facilities.DatamartFacility.Service;
import static gov.va.api.lighthouse.facilities.api.UrlFormatHelper.withTrailingSlash;
import static gov.va.api.lighthouse.facilities.collector.CsvLoader.loadWebsites;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import gov.va.api.lighthouse.facilities.DatamartCmsOverlay;
import gov.va.api.lighthouse.facilities.DatamartFacility;
import gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService;
import gov.va.api.lighthouse.facilities.DatamartFacility.OtherService;
import gov.va.api.lighthouse.facilities.DatamartFacility.Services;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/** Version agnostic facilities collector. */
@Slf4j
@Component
public class FacilitiesCollector {
  private static final String WEBSITES_CSV_RESOURCE_NAME = "websites.csv";

  private static final String CSC_STATIONS_RESOURCE_NAME = "csc_stations.txt";

  private static final String ORTHO_STATIONS_RESOURCE_NAME = "ortho_stations.txt";

  protected final InsecureRestTemplateProvider insecureRestTemplateProvider;

  protected final JdbcTemplate jdbcTemplate;

  protected final String cemeteriesBaseUrl;

  private final CmsOverlayCollector cmsOverlayCollector;

  private final AccessToCareCollector accessToCareCollector;

  private final AccessToPwtCollector accessToPwtCollector;

  /** Primary facilities collector constructor. */
  public FacilitiesCollector(
      @Autowired @NonNull InsecureRestTemplateProvider insecureRestTemplateProvider,
      @Autowired @NonNull JdbcTemplate jdbcTemplate,
      @Autowired @NonNull CmsOverlayCollector cmsOverlayCollector,
      @Autowired @NonNull AccessToCareCollector accessToCareCollector,
      @Autowired @NonNull AccessToPwtCollector accessToPwtCollector,
      @Value("${cemeteries.url}") String cemeteriesBaseUrl) {
    this.insecureRestTemplateProvider = insecureRestTemplateProvider;
    this.jdbcTemplate = jdbcTemplate;
    this.cemeteriesBaseUrl = withTrailingSlash(cemeteriesBaseUrl);
    this.cmsOverlayCollector = cmsOverlayCollector;
    this.accessToCareCollector = accessToCareCollector;
    this.accessToPwtCollector = accessToPwtCollector;
  }

  /** Returns list of vha facilities contained in a file. */
  @SneakyThrows
  public static ArrayList<String> loadFacilitiesFromResource(String resourceName) {
    final Stopwatch totalWatch = Stopwatch.createStarted();
    ArrayList<String> facilities = new ArrayList<>();
    try (BufferedReader reader =
        new BufferedReader(
            new InputStreamReader(
                new ClassPathResource(resourceName).getInputStream(), StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        facilities.add("vha_" + line);
      }
    }
    log.info(
        "Loading caregiver support facilities took {} millis for {} entries",
        totalWatch.stop().elapsed(TimeUnit.MILLISECONDS),
        facilities.size());
    checkState(!facilities.isEmpty(), "No caregiver support entries");
    return facilities;
  }

  @SneakyThrows
  static VastEntity toVastEntity(ResultSet rs) {
    return VastEntity.builder()
        .vetCenter(rs.getBoolean("VCTR2"))
        .mobileVetCenter(rs.getBoolean("MVCTR"))
        .latitude(rs.getBigDecimal("LAT"))
        .longitude(rs.getBigDecimal("LON"))
        .stationNumber(rs.getString("STA_NO"))
        .stationName(rs.getString("STATIONNAME"))
        .abbreviation(rs.getString("S_ABBR"))
        .cocClassificationId(rs.getString("COCCLASSIFICATIONID"))
        .address1(rs.getString("ADDRESS1"))
        .address2(rs.getString("ADDRESS2"))
        .address3(rs.getString("ADDRESS3"))
        .city(rs.getString("CITY"))
        .state(rs.getString("ST"))
        .zip(rs.getString("zip"))
        .zip4(rs.getString("ZIP4"))
        .monday(rs.getString("MONDAY"))
        .tuesday(rs.getString("TUESDAY"))
        .wednesday(rs.getString("WEDNESDAY"))
        .thursday(rs.getString("THURSDAY"))
        .friday(rs.getString("FRIDAY"))
        .saturday(rs.getString("SATURDAY"))
        .sunday(rs.getString("SUNDAY"))
        .staPhone(rs.getString("STA_PHONE"))
        .staFax(rs.getString("STA_FAX"))
        .afterHoursPhone(rs.getString("AFTERHOURSPHONE"))
        .patientAdvocatePhone(rs.getString("PATIENTADVOCATEPHONE"))
        .enrollmentCoordinatorPhone(rs.getString("ENROLLMENTCOORDINATORPHONE"))
        .pharmacyPhone(rs.getString("PHARMACYPHONE"))
        .pod(rs.getString("POD"))
        .mobile(rs.getBoolean("MOBILE"))
        .visn(rs.getString("VISN"))
        .lastUpdated(
            Optional.ofNullable(rs.getTimestamp("LASTUPDATED"))
                .map(t -> t.toInstant())
                .orElse(null))
        .operationalHoursSpecialInstructions(rs.getString("OPERATIONALHOURSSPECIALINSTRUCTIONS"))
        .build();
  }

  /** Collect datamart facilities. */
  @SneakyThrows
  public List<DatamartFacility> collectFacilities(boolean reloadAtcAndCmsServiceNameMappings) {
    if (!reloadAtcAndCmsServiceNameMappings
        || (accessToCareCollector.reload() && cmsOverlayCollector.reload())) {
      Map<String, String> websites;
      Collection<VastEntity> vastEntities;
      ArrayList<String> cscFacilities;
      ArrayList<String> orthoFacilities;
      try {
        websites = loadWebsites(WEBSITES_CSV_RESOURCE_NAME);
        vastEntities = loadVast();
        cscFacilities = loadFacilitiesFromResource(CSC_STATIONS_RESOURCE_NAME);
        orthoFacilities = loadFacilitiesFromResource(ORTHO_STATIONS_RESOURCE_NAME);
      } catch (Exception e) {
        throw new CollectorExceptions.CollectorException(e);
      }

      Collection<DatamartFacility> healths =
          HealthsCollector.builder()
              .accessToCareCollector(accessToCareCollector)
              .accessToPwtCollector(accessToPwtCollector)
              .cscFacilities(cscFacilities)
              .orthoFacilities(orthoFacilities)
              .jdbcTemplate(jdbcTemplate)
              .insecureRestTemplate(insecureRestTemplateProvider.restTemplate())
              .vastEntities(vastEntities)
              .websites(websites)
              .build()
              .collect();
      Collection<DatamartFacility> stateCems =
          StateCemeteriesCollector.builder()
              .baseUrl(cemeteriesBaseUrl)
              .insecureRestTemplate(insecureRestTemplateProvider.restTemplate())
              .websites(websites)
              .build()
              .collect();
      Collection<DatamartFacility> vetCenters =
          VetCentersCollector.builder()
              .vastEntities(vastEntities)
              .websites(websites)
              .build()
              .collect();
      Collection<DatamartFacility> benefits =
          BenefitsCollector.builder()
              .websites(websites)
              .jdbcTemplate(jdbcTemplate)
              .build()
              .collect();
      Collection<DatamartFacility> cemeteries =
          CemeteriesCollector.builder()
              .baseUrl(cemeteriesBaseUrl)
              .insecureRestTemplate(insecureRestTemplateProvider.restTemplate())
              .websites(websites)
              .jdbcTemplate(jdbcTemplate)
              .build()
              .collect();
      log.info(
          "Collected V0: Health {},  Benefits {},  Vet centers {}, "
              + "Non-national cemeteries {}, Cemeteries {}",
          healths.size(),
          benefits.size(),
          vetCenters.size(),
          stateCems.size(),
          cemeteries.size());
      List<DatamartFacility> datamartFacilities =
          Streams.stream(Iterables.concat(benefits, cemeteries, healths, stateCems, vetCenters))
              .sorted((left, right) -> left.id().compareToIgnoreCase(right.id()))
              .collect(toList());
      updateOperatingStatusFromCmsOverlay(datamartFacilities);
      updateServicesFromCmsOverlay(datamartFacilities);
      cmsOverlayCollector.updateCmsServicesWithAtcWaitTimes(datamartFacilities);
      return datamartFacilities;
    }
    return Collections.emptyList();
  }

  private List<VastEntity> loadVast() {
    Stopwatch watch = Stopwatch.createStarted();
    List<VastEntity> entities =
        ImmutableList.copyOf(
            jdbcTemplate.query(
                "SELECT "
                    + "VCTR2,"
                    + "MVCTR,"
                    + "LAT,"
                    + "LON,"
                    + "STA_NO,"
                    + "STATIONNAME,"
                    + "S_ABBR,"
                    + "COCCLASSIFICATIONID,"
                    + "ADDRESS1,"
                    + "ADDRESS2,"
                    + "ADDRESS3,"
                    + "CITY,"
                    + "ST,"
                    + "ZIP,"
                    + "ZIP4,"
                    + "MONDAY,"
                    + "TUESDAY,"
                    + "WEDNESDAY,"
                    + "THURSDAY,"
                    + "FRIDAY,"
                    + "SATURDAY,"
                    + "SUNDAY,"
                    + "OPERATIONALHOURSSPECIALINSTRUCTIONS,"
                    + "STA_PHONE,"
                    + "STA_FAX,"
                    + "AFTERHOURSPHONE,"
                    + "PATIENTADVOCATEPHONE,"
                    + "ENROLLMENTCOORDINATORPHONE,"
                    + "PHARMACYPHONE,"
                    + "POD,"
                    + "MOBILE,"
                    + "VISN,"
                    + "LASTUPDATED"
                    + " FROM App.Vast",
                (rs, rowNum) -> toVastEntity(rs)));
    log.info(
        "Loading VAST took {} millis for {} entries",
        watch.stop().elapsed(TimeUnit.MILLISECONDS),
        entities.size());
    checkState(!entities.isEmpty(), "No App.Vast entries");
    return entities;
  }

  /** Updates facility based on CMS Overlay data. * */
  @SneakyThrows
  public void updateOperatingStatusFromCmsOverlay(List<DatamartFacility> datamartFacilities) {
    HashMap<String, DatamartCmsOverlay> cmsOverlays;
    try {
      cmsOverlays = cmsOverlayCollector.loadAndUpdateCmsOverlays();
    } catch (Exception e) {
      throw new CollectorExceptions.CollectorException(e);
    }
    for (DatamartFacility datamartFacility : datamartFacilities) {
      if (cmsOverlays.containsKey(datamartFacility.id())) {
        DatamartCmsOverlay cmsOverlay = cmsOverlays.get(datamartFacility.id());
        datamartFacility.attributes().operatingStatus(cmsOverlay.operatingStatus());
        datamartFacility.attributes().detailedServices(cmsOverlay.detailedServices());

        if (cmsOverlay.healthCareSystem() != null) {
          if (cmsOverlay.healthCareSystem().healthConnectPhone() != null) {
            if (datamartFacility.attributes().phone() != null) {
              datamartFacility
                  .attributes()
                  .phone()
                  .healthConnect(cmsOverlay.healthCareSystem().healthConnectPhone());
            } else {
              datamartFacility
                  .attributes()
                  .phone(
                      DatamartFacility.Phone.builder()
                          .healthConnect(cmsOverlay.healthCareSystem().healthConnectPhone())
                          .build());
            }
          }
        }
      } else {
        log.warn("No cms overlay for facility: {}", datamartFacility.id());
      }
      if (datamartFacility.attributes() != null) {
        if (datamartFacility.attributes().operatingStatus() == null) {
          datamartFacility
              .attributes()
              .operatingStatus(
                  datamartFacility.attributes().activeStatus() == DatamartFacility.ActiveStatus.T
                      ? DatamartFacility.OperatingStatus.builder()
                          .code(DatamartFacility.OperatingStatusCode.CLOSED)
                          .build()
                      : DatamartFacility.OperatingStatus.builder()
                          .code(DatamartFacility.OperatingStatusCode.NORMAL)
                          .build());
        }
      }
    }
  }

  private void updateServicesFromCmsOverlay(List<DatamartFacility> datamartFacilities) {
    Map<String, Services> facilityCmsServicesMap;
    try {
      facilityCmsServicesMap = cmsOverlayCollector.getCmsServices();
    } catch (Exception e) {
      throw new CollectorExceptions.CollectorException(e);
    }

    datamartFacilities.stream()
        .filter(df -> facilityCmsServicesMap.containsKey(df.id()))
        .forEach(
            df -> {
              if (df.attributes().services() == null) {
                df.attributes().services(Services.builder().build());
              }

              if (ObjectUtils.isNotEmpty(facilityCmsServicesMap.get(df.id()).benefits())) {
                Set<Service<BenefitsService>> facilityBenefitsServicesSet =
                    facilityCmsServicesMap.get(df.id()).benefits().stream().collect(toSet());
                if (df.attributes().services().benefits() != null) {
                  // Add CMS sourced equivalent for existing facility benefits services
                  facilityBenefitsServicesSet.addAll(
                      cmsOverlayCollector.getCmsSourcedBenefitsServices(
                          df.attributes().services().benefits()));
                  // Add all existing facility benefits services
                  facilityBenefitsServicesSet.addAll(df.attributes().services().benefits());
                }

                List<Service<BenefitsService>> facilityBenefitsServices =
                    facilityBenefitsServicesSet.stream().collect(toList());
                Collections.sort(facilityBenefitsServices);
                df.attributes().services().benefits(facilityBenefitsServices);
              }

              if (ObjectUtils.isNotEmpty(facilityCmsServicesMap.get(df.id()).health())) {
                Set<Service<HealthService>> facilityHealthServicesSet =
                    facilityCmsServicesMap.get(df.id()).health().stream().collect(toSet());
                if (df.attributes().services().health() != null) {
                  // Add CMS sourced equivalent for existing facility health services
                  facilityHealthServicesSet.addAll(
                      cmsOverlayCollector.getCmsSourcedHealthServices(
                          df.attributes().services().health()));
                  // Add all existing facility health services
                  facilityHealthServicesSet.addAll(df.attributes().services().health());
                }

                List<Service<HealthService>> facilityHealthServices =
                    facilityHealthServicesSet.stream().collect(toList());
                Collections.sort(facilityHealthServices);
                df.attributes().services().health(facilityHealthServices);
              }

              if (ObjectUtils.isNotEmpty(facilityCmsServicesMap.get(df.id()).other())) {
                Set<Service<OtherService>> facilityOtherServicesSet =
                    facilityCmsServicesMap.get(df.id()).other().stream().collect(toSet());
                if (df.attributes().services().other() != null) {
                  // Add CMS sourced equivalent for existing facility other services
                  facilityOtherServicesSet.addAll(
                      cmsOverlayCollector.getCmsSourcedOtherServices(
                          df.attributes().services().other()));
                  // Add all existing facility other services
                  facilityOtherServicesSet.addAll(df.attributes().services().other());
                }

                List<Service<OtherService>> facilityOtherServices =
                    facilityOtherServicesSet.stream().collect(toList());
                Collections.sort(facilityOtherServices);
                df.attributes().services().other(facilityOtherServices);
              }
            });
  }
}
