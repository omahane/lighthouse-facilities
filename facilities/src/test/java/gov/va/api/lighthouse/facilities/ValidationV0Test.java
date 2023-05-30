package gov.va.api.lighthouse.facilities;

import static gov.va.api.health.autoconfig.configuration.JacksonConfig.createMapper;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.api.v0.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.FacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v0.NearbyResponse;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class ValidationV0Test {
  private static final Validator VALIDATOR =
      Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void all() {
    String path = "/v0/all.json";
    assertValid(path, GeoFacilitiesResponse.class);
  }

  @SneakyThrows
  private <T> void assertValid(String path, Class<T> clazz) {
    T response = createMapper().readValue(getClass().getResourceAsStream(path), clazz);
    Set<ConstraintViolation<T>> violations = VALIDATOR.validate(response);
    assertThat(violations).isEmpty();
  }

  @Test
  void nearby() {
    String path = "/nearby.json";
    assertValid(path, NearbyResponse.class);
  }

  @Test
  @SneakyThrows
  void readBenefits() {
    String path = "/v0/read-benefits.json";
    assertValid(path, FacilityReadResponse.class);
  }

  @Test
  @SneakyThrows
  void readBenefitsGeoJson() {
    String path = "/v0/read-benefits-geojson.json";
    assertValid(path, GeoFacilityReadResponse.class);
  }

  @Test
  @SneakyThrows
  void readCemetery() {
    String path = "/v0/read-cemetery.json";
    assertValid(path, FacilityReadResponse.class);
  }

  @Test
  @SneakyThrows
  void readCemeteryGeoJson() {
    String path = "/v0/read-cemetery-geojson.json";
    assertValid(path, GeoFacilityReadResponse.class);
  }

  @Test
  void readHealth() {
    String path = "/v0/read-health.json";
    assertValid(path, FacilityReadResponse.class);
  }

  @Test
  void readHealthGeoJson() {
    String path = "/v0/read-health-geojson.json";
    assertValid(path, GeoFacilityReadResponse.class);
  }

  @Test
  @SneakyThrows
  void readStateCemetery() {
    String path = "/v0/read-state-cemetery.json";
    assertValid(path, FacilityReadResponse.class);
  }

  @Test
  @SneakyThrows
  void readStateCemeteryGeoJson() {
    String path = "/v0/read-state-cemetery-geojson.json";
    assertValid(path, GeoFacilityReadResponse.class);
  }

  @Test
  void readVetCenter() {
    String path = "/v0/read-vet-center.json";
    assertValid(path, FacilityReadResponse.class);
  }

  @Test
  void readVetCenterGeoJson() {
    String path = "/v0/read-vet-center-geojson.json";
    assertValid(path, GeoFacilityReadResponse.class);
  }

  @Test
  void searchByBbox() {
    String path = "/v0/search-bbox.json";
    assertValid(path, FacilitiesResponse.class);
  }

  @Test
  void searchByBboxGeoJson() {
    String path = "/v0/search-bbox-geojson.json";
    assertValid(path, GeoFacilitiesResponse.class);
  }

  @Test
  void searchByIds() {
    String path = "/v0/search-ids.json";
    assertValid(path, FacilitiesResponse.class);
  }

  @Test
  void searchByIdsGeoJson() {
    String path = "/v0/search-ids-geojson.json";
    assertValid(path, GeoFacilitiesResponse.class);
  }

  @Test
  void searchByLatLong() {
    String path = "/v0/search-lat-long.json";
    assertValid(path, FacilitiesResponse.class);
  }

  @Test
  void searchByLatLongGeoJson() {
    String path = "/v0/search-lat-long-geojson.json";
    assertValid(path, GeoFacilitiesResponse.class);
  }

  @Test
  void searchByState() {
    String path = "/v0/search-state.json";
    assertValid(path, FacilitiesResponse.class);
  }

  @Test
  void searchByStateGeoJson() {
    String path = "/v0/search-state-geojson.json";
    assertValid(path, GeoFacilitiesResponse.class);
  }

  @Test
  void searchByZip() {
    String path = "/v0/search-zip.json";
    assertValid(path, FacilitiesResponse.class);
  }

  @Test
  void searchByZipGeoJson() {
    String path = "/v0/search-zip-geojson.json";
    assertValid(path, GeoFacilitiesResponse.class);
  }
}
