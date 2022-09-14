package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.lighthouse.facilities.DatamartFacility.FacilityType.va_health_facility;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.*;
import static gov.va.api.lighthouse.facilities.DatamartFacility.Type.va_facilities;
import static gov.va.api.lighthouse.facilities.collector.FacilitiesCollector.loadFacilitiesFromResource;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.google.common.collect.ArrayListMultimap;
import gov.va.api.lighthouse.facilities.DatamartFacility;
import gov.va.api.lighthouse.facilities.DatamartFacility.FacilityAttributes;
import gov.va.api.lighthouse.facilities.DatamartFacility.HealthService;
import gov.va.api.lighthouse.facilities.DatamartFacility.Service;
import gov.va.api.lighthouse.facilities.DatamartFacility.Service.Source;
import gov.va.api.lighthouse.facilities.DatamartFacility.Services;
import gov.va.api.lighthouse.facilities.api.v0.Facility.ActiveStatus;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class HealthTransformerTest {
  private static final String CSC_STATIONS_RESOURCE_NAME = "csc_stations.txt";

  private static final String ORTHO_STATIONS_RESOURCE_NAME = "ortho_stations.txt";

  @Test
  @SneakyThrows
  public void activeStatus() {
    Method activeStatusMethod = HealthTransformer.class.getDeclaredMethod("activeStatus", null);
    activeStatusMethod.setAccessible(true);
    HealthTransformer withActiveStatus =
        HealthTransformer.builder()
            .vast(VastEntity.builder().pod(ActiveStatus.A.name()).build())
            .accessToCareCollector(mock(AccessToCareCollector.class))
            .accessToPwtCollector(mock(AccessToPwtCollector.class))
            .mentalHealthPhoneNumbers(emptyMap())
            .stopCodesMap(ArrayListMultimap.create())
            .websites(emptyMap())
            .cscFacilities(new ArrayList<>())
            .orthoFacilities(new ArrayList<>())
            .build();
    assertThat(activeStatusMethod.invoke(withActiveStatus, null))
        .isEqualTo(DatamartFacility.ActiveStatus.A);
    HealthTransformer withInactiveStatus =
        HealthTransformer.builder()
            .vast(VastEntity.builder().pod(ActiveStatus.T.name()).build())
            .accessToCareCollector(mock(AccessToCareCollector.class))
            .accessToPwtCollector(mock(AccessToPwtCollector.class))
            .mentalHealthPhoneNumbers(emptyMap())
            .stopCodesMap(ArrayListMultimap.create())
            .websites(emptyMap())
            .cscFacilities(new ArrayList<>())
            .orthoFacilities(new ArrayList<>())
            .build();
    assertThat(activeStatusMethod.invoke(withInactiveStatus, null))
        .isEqualTo(DatamartFacility.ActiveStatus.T);
  }

  private void assertClassification(String classificationId, String featureCode, String expected) {
    assertThat(
            HealthTransformer.builder()
                .vast(
                    VastEntity.builder()
                        .cocClassificationId(classificationId)
                        .abbreviation(featureCode)
                        .build())
                .accessToCareCollector(mock(AccessToCareCollector.class))
                .accessToPwtCollector(mock(AccessToPwtCollector.class))
                .cscFacilities(new ArrayList<>())
                .orthoFacilities(new ArrayList<>())
                .mentalHealthPhoneNumbers(emptyMap())
                .stopCodesMap(ArrayListMultimap.create())
                .websites(emptyMap())
                .build()
                .classification())
        .isEqualTo(expected);
  }

  @Test
  void classification() {
    assertThat(
            HealthTransformer.builder()
                .vast(VastEntity.builder().build())
                .accessToCareCollector(mock(AccessToCareCollector.class))
                .accessToPwtCollector(mock(AccessToPwtCollector.class))
                .cscFacilities(new ArrayList<>())
                .orthoFacilities(new ArrayList<>())
                .mentalHealthPhoneNumbers(emptyMap())
                .stopCodesMap(ArrayListMultimap.create())
                .websites(emptyMap())
                .build()
                .classification())
        .isNull();
    assertClassification(null, null, null);
    assertClassification("1", null, "VA Medical Center (VAMC)");
    assertClassification("2", null, "Health Care Center (HCC)");
    assertClassification("3", null, "Multi-Specialty CBOC");
    assertClassification("4", null, "Primary Care CBOC");
    assertClassification("5", null, "Other Outpatient Services (OOS)");
    assertClassification("7", null, "Residential Care Site (MH RRTP/DRRTP) (Stand-Alone)");
    assertClassification("8", null, "Extended Care Site (Community Living Center) (Stand-Alone)");
    assertClassification("x", null, "x");
    assertClassification(null, "f", "f");
  }

  @Test
  void empty() {
    assertThat(
            HealthTransformer.builder()
                .vast(VastEntity.builder().build())
                .accessToCareCollector(mock(AccessToCareCollector.class))
                .accessToPwtCollector(mock(AccessToPwtCollector.class))
                .cscFacilities(new ArrayList<>())
                .orthoFacilities(new ArrayList<>())
                .mentalHealthPhoneNumbers(emptyMap())
                .stopCodesMap(ArrayListMultimap.create())
                .websites(emptyMap())
                .build()
                .toDatamartFacility())
        .isNull();
    ArrayListMultimap<String, StopCode> sc = ArrayListMultimap.create();
    sc.put("VHA_X", StopCode.builder().build());
    assertThat(
            HealthTransformer.builder()
                .vast(VastEntity.builder().stationNumber("x").build())
                .accessToCareCollector(mock(AccessToCareCollector.class))
                .accessToPwtCollector(mock(AccessToPwtCollector.class))
                .cscFacilities(new ArrayList<>())
                .orthoFacilities(new ArrayList<>())
                .mentalHealthPhoneNumbers(emptyMap())
                .stopCodesMap(sc)
                .websites(emptyMap())
                .build()
                .toDatamartFacility())
        .isEqualTo(DatamartFacility.builder().id("vha_x").type(va_facilities).build());
  }

  @Test
  void facilityWithCaregiverSupport() {
    var cscStationNumbers = new ArrayList<>(List.of("vha_123GA", "vha_321GA", "vha_789GA"));
    HealthTransformer hasCaregiverSupportAndStationNumber =
        HealthTransformer.builder()
            .vast(VastEntity.builder().stationNumber("123GA").build())
            .accessToCareCollector(mock(AccessToCareCollector.class))
            .accessToPwtCollector(mock(AccessToPwtCollector.class))
            .mentalHealthPhoneNumbers(emptyMap())
            .stopCodesMap(ArrayListMultimap.create())
            .websites(emptyMap())
            .cscFacilities(cscStationNumbers)
            .orthoFacilities(new ArrayList<>())
            .build();
    assertThat(hasCaregiverSupportAndStationNumber.hasCaregiverSupport()).isTrue();
    HealthTransformer hasCaregiverSupportWithNoStationNumber =
        HealthTransformer.builder()
            .vast(VastEntity.builder().build())
            .accessToCareCollector(mock(AccessToCareCollector.class))
            .accessToPwtCollector(mock(AccessToPwtCollector.class))
            .mentalHealthPhoneNumbers(emptyMap())
            .stopCodesMap(ArrayListMultimap.create())
            .websites(emptyMap())
            .cscFacilities(cscStationNumbers)
            .orthoFacilities(new ArrayList<>())
            .build();
    assertThat(hasCaregiverSupportWithNoStationNumber.hasCaregiverSupport()).isFalse();
    ArrayListMultimap<String, StopCode> sc = ArrayListMultimap.create();
    sc.put("VHA_689", StopCode.builder().build());
    assertThat(
            HealthTransformer.builder()
                .vast(VastEntity.builder().stationNumber("689").build())
                .accessToCareCollector(mock(AccessToCareCollector.class))
                .accessToPwtCollector(mock(AccessToPwtCollector.class))
                .cscFacilities(loadFacilitiesFromResource(CSC_STATIONS_RESOURCE_NAME))
                .orthoFacilities(new ArrayList<>())
                .mentalHealthPhoneNumbers(emptyMap())
                .stopCodesMap(sc)
                .websites(emptyMap())
                .build()
                .toDatamartFacility())
        .isEqualTo(
            DatamartFacility.builder()
                .id("vha_689")
                .type(va_facilities)
                .attributes(
                    FacilityAttributes.builder()
                        .facilityType(va_health_facility)
                        .services(
                            Services.builder()
                                .health(
                                    List.of(
                                        Service.<HealthService>builder()
                                            .serviceType(CaregiverSupport)
                                            .name(CaregiverSupport.name())
                                            .source(Source.internal)
                                            .build()))
                                .build())
                        .build())
                .build());
  }

  @Test
  void facilityWithOrthopedics() {
    var orthoStationNumbers = new ArrayList<>(List.of("vha_123GA", "vha_321GA", "vha_789GA"));
    HealthTransformer hasOrthopedicsAndStationNumber =
        HealthTransformer.builder()
            .vast(VastEntity.builder().stationNumber("123GA").build())
            .accessToCareCollector(mock(AccessToCareCollector.class))
            .accessToPwtCollector(mock(AccessToPwtCollector.class))
            .mentalHealthPhoneNumbers(emptyMap())
            .stopCodesMap(ArrayListMultimap.create())
            .websites(emptyMap())
            .cscFacilities(new ArrayList<>())
            .orthoFacilities(orthoStationNumbers)
            .build();
    assertThat(hasOrthopedicsAndStationNumber.hasOrthopedics()).isTrue();
    HealthTransformer hasOrthopedicsWithNoStationNumber =
        HealthTransformer.builder()
            .vast(VastEntity.builder().build())
            .accessToCareCollector(mock(AccessToCareCollector.class))
            .accessToPwtCollector(mock(AccessToPwtCollector.class))
            .mentalHealthPhoneNumbers(emptyMap())
            .stopCodesMap(ArrayListMultimap.create())
            .websites(emptyMap())
            .cscFacilities(new ArrayList<>())
            .orthoFacilities(orthoStationNumbers)
            .build();
    assertThat(hasOrthopedicsWithNoStationNumber.hasOrthopedics()).isFalse();
    ArrayListMultimap<String, StopCode> sc = ArrayListMultimap.create();
    sc.put("VHA_689", StopCode.builder().build());
    assertThat(
            HealthTransformer.builder()
                .vast(VastEntity.builder().stationNumber("689").build())
                .accessToCareCollector(mock(AccessToCareCollector.class))
                .accessToPwtCollector(mock(AccessToPwtCollector.class))
                .cscFacilities(new ArrayList<>())
                .orthoFacilities(loadFacilitiesFromResource(ORTHO_STATIONS_RESOURCE_NAME))
                .mentalHealthPhoneNumbers(emptyMap())
                .stopCodesMap(sc)
                .websites(emptyMap())
                .build()
                .toDatamartFacility())
        .isEqualTo(
            DatamartFacility.builder()
                .id("vha_689")
                .type(va_facilities)
                .attributes(
                    FacilityAttributes.builder()
                        .facilityType(va_health_facility)
                        .services(
                            Services.builder()
                                .health(
                                    List.of(
                                        Service.<HealthService>builder()
                                            .serviceType(Orthopedics)
                                            .name(Orthopedics.name())
                                            .source(Source.internal)
                                            .build()))
                                .build())
                        .build())
                .build());
  }

  @Test
  public void facilityWithoutCaregiverSupport() {
    var cscStationNumbers = new ArrayList<>(List.of("vha_123GA", "vha_321GA", "vha_789GA"));
    HealthTransformer lacksCaregiverSupport =
        HealthTransformer.builder()
            .vast(VastEntity.builder().stationNumber("456GA").build())
            .accessToCareCollector(mock(AccessToCareCollector.class))
            .accessToPwtCollector(mock(AccessToPwtCollector.class))
            .mentalHealthPhoneNumbers(emptyMap())
            .stopCodesMap(ArrayListMultimap.create())
            .websites(emptyMap())
            .cscFacilities(cscStationNumbers)
            .orthoFacilities(new ArrayList<>())
            .build();
    assertThat(lacksCaregiverSupport.hasCaregiverSupport()).isFalse();
    HealthTransformer noCaregiverSupportForStationNumber =
        HealthTransformer.builder()
            .vast(VastEntity.builder().stationNumber("123GA").build())
            .accessToCareCollector(mock(AccessToCareCollector.class))
            .accessToPwtCollector(mock(AccessToPwtCollector.class))
            .mentalHealthPhoneNumbers(emptyMap())
            .stopCodesMap(ArrayListMultimap.create())
            .websites(emptyMap())
            .cscFacilities(new ArrayList<>())
            .orthoFacilities(new ArrayList<>())
            .build();
    assertThat(noCaregiverSupportForStationNumber.hasCaregiverSupport()).isFalse();
    HealthTransformer noCaregiverSupportOrStationNumber =
        HealthTransformer.builder()
            .vast(VastEntity.builder().build())
            .accessToCareCollector(mock(AccessToCareCollector.class))
            .accessToPwtCollector(mock(AccessToPwtCollector.class))
            .mentalHealthPhoneNumbers(emptyMap())
            .stopCodesMap(ArrayListMultimap.create())
            .websites(emptyMap())
            .cscFacilities(new ArrayList<>())
            .orthoFacilities(new ArrayList<>())
            .build();
    assertThat(noCaregiverSupportOrStationNumber.hasCaregiverSupport()).isFalse();
  }

  @Test
  public void facilityWithoutOrthopedics() {
    var orthoStationNumbers = new ArrayList<>(List.of("vha_123GA", "vha_321GA", "vha_789GA"));
    HealthTransformer lacksOrthopedics =
        HealthTransformer.builder()
            .vast(VastEntity.builder().stationNumber("456GA").build())
            .accessToCareCollector(mock(AccessToCareCollector.class))
            .accessToPwtCollector(mock(AccessToPwtCollector.class))
            .mentalHealthPhoneNumbers(emptyMap())
            .stopCodesMap(ArrayListMultimap.create())
            .websites(emptyMap())
            .cscFacilities(new ArrayList<>())
            .orthoFacilities(orthoStationNumbers)
            .build();
    assertThat(lacksOrthopedics.hasOrthopedics()).isFalse();
    HealthTransformer noOrthopedicsForStationNumber =
        HealthTransformer.builder()
            .vast(VastEntity.builder().stationNumber("123GA").build())
            .accessToCareCollector(mock(AccessToCareCollector.class))
            .accessToPwtCollector(mock(AccessToPwtCollector.class))
            .mentalHealthPhoneNumbers(emptyMap())
            .stopCodesMap(ArrayListMultimap.create())
            .websites(emptyMap())
            .cscFacilities(new ArrayList<>())
            .orthoFacilities(new ArrayList<>())
            .build();
    assertThat(noOrthopedicsForStationNumber.hasOrthopedics()).isFalse();
    HealthTransformer noOrthopedicsOrStationNumber =
        HealthTransformer.builder()
            .vast(VastEntity.builder().build())
            .accessToCareCollector(mock(AccessToCareCollector.class))
            .accessToPwtCollector(mock(AccessToPwtCollector.class))
            .mentalHealthPhoneNumbers(emptyMap())
            .stopCodesMap(ArrayListMultimap.create())
            .websites(emptyMap())
            .cscFacilities(new ArrayList<>())
            .orthoFacilities(new ArrayList<>())
            .build();
    assertThat(noOrthopedicsOrStationNumber.hasOrthopedics()).isFalse();
  }

  @Test
  public void website() {
    HealthTransformer blankId =
        HealthTransformer.builder()
            .vast(VastEntity.builder().build())
            .accessToCareCollector(mock(AccessToCareCollector.class))
            .accessToPwtCollector(mock(AccessToPwtCollector.class))
            .mentalHealthPhoneNumbers(emptyMap())
            .stopCodesMap(ArrayListMultimap.create())
            .websites(emptyMap())
            .cscFacilities(new ArrayList<>())
            .orthoFacilities(new ArrayList<>())
            .build();
    assertThat(blankId.website()).isNull();
    var expectedWebsite = "https://developer.va.gov";
    HealthTransformer healthTransformer =
        HealthTransformer.builder()
            .vast(VastEntity.builder().stationNumber("123").build())
            .accessToCareCollector(mock(AccessToCareCollector.class))
            .accessToPwtCollector(mock(AccessToPwtCollector.class))
            .mentalHealthPhoneNumbers(emptyMap())
            .stopCodesMap(ArrayListMultimap.create())
            .websites(Map.of("vha_123", expectedWebsite))
            .cscFacilities(new ArrayList<>())
            .orthoFacilities(new ArrayList<>())
            .build();
    assertThat(healthTransformer.website()).isEqualTo(expectedWebsite);
  }

  @Test
  @SneakyThrows
  public void zip() {
    Method zipMethod = HealthTransformer.class.getDeclaredMethod("zip", null);
    zipMethod.setAccessible(true);
    HealthTransformer zip =
        HealthTransformer.builder()
            .vast(VastEntity.builder().zip("32934").build())
            .accessToCareCollector(mock(AccessToCareCollector.class))
            .accessToPwtCollector(mock(AccessToPwtCollector.class))
            .mentalHealthPhoneNumbers(emptyMap())
            .stopCodesMap(ArrayListMultimap.create())
            .websites(emptyMap())
            .cscFacilities(new ArrayList<>())
            .orthoFacilities(new ArrayList<>())
            .build();
    assertThat(zipMethod.invoke(zip)).isEqualTo("32934");
    HealthTransformer zipWithPlus4 =
        HealthTransformer.builder()
            .vast(VastEntity.builder().zip("32934").zip4("2807").build())
            .accessToCareCollector(mock(AccessToCareCollector.class))
            .accessToPwtCollector(mock(AccessToPwtCollector.class))
            .mentalHealthPhoneNumbers(emptyMap())
            .stopCodesMap(ArrayListMultimap.create())
            .websites(emptyMap())
            .cscFacilities(new ArrayList<>())
            .orthoFacilities(new ArrayList<>())
            .build();
    assertThat(zipMethod.invoke(zipWithPlus4)).isEqualTo("32934-2807");
  }
}
