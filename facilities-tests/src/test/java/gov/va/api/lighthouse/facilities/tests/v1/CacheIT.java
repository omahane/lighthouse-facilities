package gov.va.api.lighthouse.facilities.tests.v1;

// import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentNotIn;
// import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.CLIENT_KEY_DEFAULT;
// import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;
// import static org.assertj.core.api.Assertions.assertThat;
//
// import gov.va.api.health.sentinel.Environment;
// import gov.va.api.health.sentinel.ExpectedResponse;
// import gov.va.api.lighthouse.facilities.tests.SystemDefinitions;
// import io.restassured.RestAssured;
// import io.restassured.http.Method;
// import io.restassured.specification.RequestSpecification;
// import java.time.Duration;
// import java.time.Instant;
// import org.junit.jupiter.api.Test;

public class CacheIT {
  //  private static final String CMS_OVERLAY_BODY =
  //      "{"
  //          + "\"operatingStatus\": {"
  //          + "\"code\": \"NORMAL\","
  //          + "\"additionalInfo\": \"Masks are required when in the facility.\""
  //          + "},"
  //          + "\"detailedServices\": ["
  //          + "{"
  //          + "\"serviceInfo\": {"
  //          + "\"name\": \"COVID-19 vaccines\""
  //          + "},"
  //          + "\"active\": \"true\","
  //          + "\"descriptionFacility\": null,"
  //          + "\"appointmentLeadin\": \"Your VA health care team will contact you if you???re
  // eligible to get a vaccine during this time. As the supply of vaccine increases, we'll work with
  // our care teams to let Veterans know their options.\","
  //          + "\"appointmentPhones\": ["
  //          + "{"
  //          + "\"extension\": null,"
  //          + "\"label\": \"Main phone\","
  //          + "\"number\": \"252-830-2149\","
  //          + "\"type\": \"tel\""
  //          + "}"
  //          + "],"
  //          + "\"onlineSchedulingAvailable\": null,"
  //          + "\"referralRequired\": \"true\","
  //          + "\"walkInsAccepted\": \"false\","
  //          + "\"serviceLocations\": null,"
  //          + "\"path\": \"https://www.va.gov/durham-health-care/programs/covid-19-vaccines/\""
  //          + "}"
  //          + "]"
  //          + "}";
  //
  //  private static RequestSpecification requestSpecification() {
  //    SystemDefinitions.Service svcInternal = systemDefinition().facilitiesInternal();
  //    return RestAssured.given()
  //        .baseUri(svcInternal.url())
  //        .port(svcInternal.port())
  //        .relaxedHTTPSValidation()
  //        .header("client-key", System.getProperty("client-key", CLIENT_KEY_DEFAULT));
  //  }
  //
  //  @Test
  //  void allCsvWithCacheEvictedByDeletingFacility() {
  //    assumeEnvironmentNotIn(Environment.LAB, Environment.PROD);
  //    SystemDefinitions.Service svc = systemDefinition().facilities();
  //    SystemDefinitions.Service svcInternal = systemDefinition().facilitiesInternal();
  //    // Load all facilities
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .request(Method.GET, svcInternal.urlWithApiPath() +
  // "internal/management/reload"))
  //        .expect(200);
  //    // Initial call to /all csv
  //    Instant start = Instant.now();
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("text/csv")
  //                .request(Method.GET, svc.urlWithApiPath() + "v1/facilities"))
  //        .expect(200);
  //    Instant finish = Instant.now();
  //    long initialCallTimeInMillis = Duration.between(start, finish).toMillis();
  //    assertThat(initialCallTimeInMillis).isBetween(0L, 4500L);
  //    // Call to /all csv returning cached result
  //    start = Instant.now();
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("text/csv")
  //                .request(Method.GET, svc.urlWithApiPath() + "v1/facilities"))
  //        .expect(200);
  //    finish = Instant.now();
  //    long cachedResponseTimeInMillis = Duration.between(start, finish).toMillis();
  //    assertThat(cachedResponseTimeInMillis < initialCallTimeInMillis).isTrue();
  //    assertThat(cachedResponseTimeInMillis).isBetween(0L, 2500L);
  //    // Delete a facility, thus evicting cache
  //    var facilityId = "vha_521GF";
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .request(
  //                    Method.DELETE,
  //                    svcInternal.urlWithApiPath() + "internal/management/facilities/" +
  // facilityId))
  //        .expect(200);
  //    // Call to /all csv after cache evicted
  //    start = Instant.now();
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("text/csv")
  //                .request(Method.GET, svc.urlWithApiPath() + "v1/facilities"))
  //        .expect(200);
  //    finish = Instant.now();
  //    long cachedEvictedResponseTimeInMillis = Duration.between(start, finish).toMillis();
  //    assertThat(cachedEvictedResponseTimeInMillis > cachedResponseTimeInMillis).isTrue();
  //    assertThat(cachedEvictedResponseTimeInMillis).isBetween(0L, 4500L);
  //  }
  //
  //  @Test
  //  void allCsvWithCacheEvictedByDeletingOverlay() {
  //    assumeEnvironmentNotIn(Environment.LAB, Environment.PROD);
  //    SystemDefinitions.Service svc = systemDefinition().facilities();
  //    SystemDefinitions.Service svcInternal = systemDefinition().facilitiesInternal();
  //    var facilityId = "vha_521GF";
  //    // Load all facilities
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .request(Method.GET, svcInternal.urlWithApiPath() +
  // "internal/management/reload"))
  //        .expect(200);
  //    // Load overlay
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("application/json")
  //                .body(CMS_OVERLAY_BODY)
  //                .request(
  //                    Method.POST,
  //                    svcInternal.urlWithApiPath() + "v1/facilities/" + facilityId +
  // "/cms-overlay/"))
  //        .expect(200);
  //    // Initial call to /all csv
  //    Instant start = Instant.now();
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("text/csv")
  //                .request(Method.GET, svc.urlWithApiPath() + "v1/facilities"))
  //        .expect(200);
  //    Instant finish = Instant.now();
  //    long initialCallTimeInMillis = Duration.between(start, finish).toMillis();
  //    assertThat(initialCallTimeInMillis).isBetween(0L, 4500L);
  //    // Call to /all csv returning cached result
  //    start = Instant.now();
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("text/csv")
  //                .request(Method.GET, svc.urlWithApiPath() + "v1/facilities"))
  //        .expect(200);
  //    finish = Instant.now();
  //    long cachedResponseTimeInMillis = Duration.between(start, finish).toMillis();
  //    assertThat(cachedResponseTimeInMillis < initialCallTimeInMillis).isTrue();
  //    assertThat(cachedResponseTimeInMillis).isBetween(0L, 2500L);
  //    // Delete an overlay, thus evicting cache
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .request(
  //                    Method.DELETE,
  //                    svcInternal.urlWithApiPath()
  //                        + "internal/management/facilities/"
  //                        + facilityId
  //                        + "/cms-overlay"))
  //        .expect(200);
  //    // Call to /all csv after cache evicted
  //    start = Instant.now();
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("text/csv")
  //                .request(Method.GET, svc.urlWithApiPath() + "v1/facilities"))
  //        .expect(200);
  //    finish = Instant.now();
  //    long cachedEvictedResponseTimeInMillis = Duration.between(start, finish).toMillis();
  //    assertThat(cachedEvictedResponseTimeInMillis > cachedResponseTimeInMillis).isTrue();
  //    assertThat(cachedEvictedResponseTimeInMillis).isBetween(0L, 4500L);
  //  }
  //
  //  @Test
  //  void allCsvWithCacheEvictedByPerformingReload() {
  //    assumeEnvironmentNotIn(Environment.LAB, Environment.PROD);
  //    SystemDefinitions.Service svc = systemDefinition().facilities();
  //    SystemDefinitions.Service svcInternal = systemDefinition().facilitiesInternal();
  //    // Load all facilities
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .request(Method.GET, svcInternal.urlWithApiPath() +
  // "internal/management/reload"))
  //        .expect(200);
  //    // Initial call to /all csv
  //    Instant start = Instant.now();
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("text/csv")
  //                .request(Method.GET, svc.urlWithApiPath() + "v1/facilities"))
  //        .expect(200);
  //    Instant finish = Instant.now();
  //    long initialCallTimeInMillis = Duration.between(start, finish).toMillis();
  //    assertThat(initialCallTimeInMillis).isBetween(0L, 4500L);
  //    // Call to /all csv returning cached result
  //    start = Instant.now();
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("text/csv")
  //                .request(Method.GET, svc.urlWithApiPath() + "v1/facilities"))
  //        .expect(200);
  //    finish = Instant.now();
  //    long cachedResponseTimeInMillis = Duration.between(start, finish).toMillis();
  //    assertThat(cachedResponseTimeInMillis < initialCallTimeInMillis).isTrue();
  //    assertThat(cachedResponseTimeInMillis).isBetween(0L, 2500L);
  //    // Perform reload, thus evicting cache
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .request(Method.GET, svcInternal.urlWithApiPath() +
  // "internal/management/reload"))
  //        .expect(200);
  //    // Call to /all csv after cache evicted
  //    start = Instant.now();
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("text/csv")
  //                .request(Method.GET, svc.urlWithApiPath() + "v1/facilities"))
  //        .expect(200);
  //    finish = Instant.now();
  //    long cachedEvictedResponseTimeInMillis = Duration.between(start, finish).toMillis();
  //    assertThat(cachedEvictedResponseTimeInMillis > cachedResponseTimeInMillis).isTrue();
  //    assertThat(cachedEvictedResponseTimeInMillis).isBetween(0L, 4500L);
  //  }
  //
  //  @Test
  //  void allCsvWithCacheEvictedByUploadingOverlay() {
  //    assumeEnvironmentNotIn(Environment.LAB, Environment.PROD);
  //    SystemDefinitions.Service svc = systemDefinition().facilities();
  //    SystemDefinitions.Service svcInternal = systemDefinition().facilitiesInternal();
  //    // Load all facilities
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .request(Method.GET, svcInternal.urlWithApiPath() +
  // "internal/management/reload"))
  //        .expect(200);
  //    // Initial call to /all csv
  //    Instant start = Instant.now();
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("text/csv")
  //                .request(Method.GET, svc.urlWithApiPath() + "v1/facilities"))
  //        .expect(200);
  //    Instant finish = Instant.now();
  //    long initialCallTimeInMillis = Duration.between(start, finish).toMillis();
  //    assertThat(initialCallTimeInMillis).isBetween(0L, 4500L);
  //    // Call to /all csv returning cached result
  //    start = Instant.now();
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("text/csv")
  //                .request(Method.GET, svc.urlWithApiPath() + "v1/facilities"))
  //        .expect(200);
  //    finish = Instant.now();
  //    long cachedResponseTimeInMillis = Duration.between(start, finish).toMillis();
  //    assertThat(cachedResponseTimeInMillis < initialCallTimeInMillis).isTrue();
  //    assertThat(cachedResponseTimeInMillis).isBetween(0L, 2500L);
  //    // Upload overlay, thus evicting cache
  //    var facilityId = "vha_521GF";
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("application/json")
  //                .body(CMS_OVERLAY_BODY)
  //                .request(
  //                    Method.POST,
  //                    svcInternal.urlWithApiPath() + "v1/facilities/" + facilityId +
  // "/cms-overlay/"))
  //        .expect(200);
  //    // Call to /all csv after cache evicted
  //    start = Instant.now();
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("text/csv")
  //                .request(Method.GET, svc.urlWithApiPath() + "v1/facilities"))
  //        .expect(200);
  //    finish = Instant.now();
  //    long cachedEvictedResponseTimeInMillis = Duration.between(start, finish).toMillis();
  //    assertThat(cachedEvictedResponseTimeInMillis > cachedResponseTimeInMillis).isTrue();
  //    assertThat(cachedEvictedResponseTimeInMillis).isBetween(0L, 4500L);
  //  }
  //
  //  @Test
  //  void allWithCacheEvictedByDeletingFacility() {
  //    assumeEnvironmentNotIn(Environment.LAB, Environment.PROD);
  //    SystemDefinitions.Service svc = systemDefinition().facilities();
  //    SystemDefinitions.Service svcInternal = systemDefinition().facilitiesInternal();
  //    // Load all facilities
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .request(Method.GET, svcInternal.urlWithApiPath() +
  // "internal/management/reload"))
  //        .expect(200);
  //    // Initial call to /all
  //    Instant start = Instant.now();
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("application/json")
  //                .request(Method.GET, svc.urlWithApiPath() +
  // "v1/facilities?page=1&per_page=100000"))
  //        .expect(200);
  //    Instant finish = Instant.now();
  //    long initialCallTimeInMillis = Duration.between(start, finish).toMillis();
  //    assertThat(initialCallTimeInMillis).isBetween(0L, 4500L);
  //    // Call to /all returning cached result
  //    start = Instant.now();
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("application/json")
  //                .request(Method.GET, svc.urlWithApiPath() +
  // "v1/facilities?page=1&per_page=100000"))
  //        .expect(200);
  //    finish = Instant.now();
  //    long cachedResponseTimeInMillis = Duration.between(start, finish).toMillis();
  //    assertThat(cachedResponseTimeInMillis < initialCallTimeInMillis).isTrue();
  //    assertThat(cachedResponseTimeInMillis).isBetween(0L, 2500L);
  //    // Delete a facility, thus evicting cache
  //    var facilityId = "vha_521GF";
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .request(
  //                    Method.DELETE,
  //                    svcInternal.urlWithApiPath() + "internal/management/facilities/" +
  // facilityId))
  //        .expect(200);
  //    // Call to /all after cache evicted
  //    start = Instant.now();
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("application/json")
  //                .request(Method.GET, svc.urlWithApiPath() +
  // "v1/facilities?page=1&per_page=100000"))
  //        .expect(200);
  //    finish = Instant.now();
  //    long cachedEvictedResponseTimeInMillis = Duration.between(start, finish).toMillis();
  //    assertThat(cachedEvictedResponseTimeInMillis > cachedResponseTimeInMillis).isTrue();
  //    assertThat(cachedEvictedResponseTimeInMillis).isBetween(0L, 4500L);
  //  }
  //
  //  @Test
  //  void allWithCacheEvictedByDeletingOverlay() {
  //    assumeEnvironmentNotIn(Environment.LAB, Environment.PROD);
  //    SystemDefinitions.Service svc = systemDefinition().facilities();
  //    SystemDefinitions.Service svcInternal = systemDefinition().facilitiesInternal();
  //    // Load all facilities
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .request(Method.GET, svcInternal.urlWithApiPath() +
  // "internal/management/reload"))
  //        .expect(200);
  //    // Initial call to /all
  //    Instant start = Instant.now();
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("application/json")
  //                .request(Method.GET, svc.urlWithApiPath() +
  // "v1/facilities?page=1&per_page=100000"))
  //        .expect(200);
  //    Instant finish = Instant.now();
  //    long initialCallTimeInMillis = Duration.between(start, finish).toMillis();
  //    assertThat(initialCallTimeInMillis).isBetween(0L, 4500L);
  //    // Call to /all returning cached result
  //    start = Instant.now();
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("application/json")
  //                .request(Method.GET, svc.urlWithApiPath() +
  // "v1/facilities?page=1&per_page=100000"))
  //        .expect(200);
  //    finish = Instant.now();
  //    long cachedResponseTimeInMillis = Duration.between(start, finish).toMillis();
  //    assertThat(cachedResponseTimeInMillis < initialCallTimeInMillis).isTrue();
  //    assertThat(cachedResponseTimeInMillis).isBetween(0L, 2500L);
  //    // Delete an overlay, thus evicting cache
  //    var facilityId = "vha_521GF";
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .request(
  //                    Method.DELETE,
  //                    svcInternal.urlWithApiPath()
  //                        + "internal/management/facilities/"
  //                        + facilityId
  //                        + "/cms-overlay"))
  //        .expect(200);
  //    // Call to /all after cache evicted
  //    start = Instant.now();
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("application/json")
  //                .request(Method.GET, svc.urlWithApiPath() +
  // "v1/facilities?page=1&per_page=100000"))
  //        .expect(200);
  //    finish = Instant.now();
  //    long cachedEvictedResponseTimeInMillis = Duration.between(start, finish).toMillis();
  //    assertThat(cachedEvictedResponseTimeInMillis > cachedResponseTimeInMillis).isTrue();
  //    assertThat(cachedEvictedResponseTimeInMillis).isBetween(0L, 4500L);
  //  }
  //
  //  @Test
  //  void allWithCacheEvictedByPerformingReload() {
  //    assumeEnvironmentNotIn(Environment.LAB, Environment.PROD);
  //    SystemDefinitions.Service svc = systemDefinition().facilities();
  //    SystemDefinitions.Service svcInternal = systemDefinition().facilitiesInternal();
  //    // Load all facilities
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .request(Method.GET, svcInternal.urlWithApiPath() +
  // "internal/management/reload"))
  //        .expect(200);
  //    // Initial call to /all
  //    Instant start = Instant.now();
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("application/json")
  //                .request(Method.GET, svc.urlWithApiPath() +
  // "v1/facilities?page=1&per_page=100000"))
  //        .expect(200);
  //    Instant finish = Instant.now();
  //    long initialCallTimeInMillis = Duration.between(start, finish).toMillis();
  //    assertThat(initialCallTimeInMillis).isBetween(0L, 4500L);
  //    // Call to /all returning cached result
  //    start = Instant.now();
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("application/json")
  //                .request(Method.GET, svc.urlWithApiPath() +
  // "v1/facilities?page=1&per_page=100000"))
  //        .expect(200);
  //    finish = Instant.now();
  //    long cachedResponseTimeInMillis = Duration.between(start, finish).toMillis();
  //    assertThat(cachedResponseTimeInMillis < initialCallTimeInMillis).isTrue();
  //    assertThat(cachedResponseTimeInMillis).isBetween(0L, 2500L);
  //    // Perform reload, thus evicting cache
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .request(Method.GET, svcInternal.urlWithApiPath() +
  // "internal/management/reload"))
  //        .expect(200);
  //    // Call to /all after cache evicted
  //    start = Instant.now();
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("application/json")
  //                .request(Method.GET, svc.urlWithApiPath() +
  // "v1/facilities?page=1&per_page=100000"))
  //        .expect(200);
  //    finish = Instant.now();
  //    long cachedEvictedResponseTimeInMillis = Duration.between(start, finish).toMillis();
  //    assertThat(cachedEvictedResponseTimeInMillis > cachedResponseTimeInMillis).isTrue();
  //    assertThat(cachedEvictedResponseTimeInMillis).isBetween(0L, 4500L);
  //  }
  //
  //  @Test
  //  void allWithCacheEvictedByUploadingOverlay() {
  //    assumeEnvironmentNotIn(Environment.LAB, Environment.PROD);
  //    SystemDefinitions.Service svc = systemDefinition().facilities();
  //    SystemDefinitions.Service svcInternal = systemDefinition().facilitiesInternal();
  //    // Load all facilities
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .request(Method.GET, svcInternal.urlWithApiPath() +
  // "internal/management/reload"))
  //        .expect(200);
  //    // Initial call to /all
  //    Instant start = Instant.now();
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("application/json")
  //                .request(Method.GET, svc.urlWithApiPath() +
  // "v1/facilities?page=1&per_page=100000"))
  //        .expect(200);
  //    Instant finish = Instant.now();
  //    long initialCallTimeInMillis = Duration.between(start, finish).toMillis();
  //    assertThat(initialCallTimeInMillis).isBetween(0L, 4500L);
  //    // Call to /all returning cached result
  //    start = Instant.now();
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("application/json")
  //                .request(Method.GET, svc.urlWithApiPath() +
  // "v1/facilities?page=1&per_page=100000"))
  //        .expect(200);
  //    finish = Instant.now();
  //    long cachedResponseTimeInMillis = Duration.between(start, finish).toMillis();
  //    assertThat(cachedResponseTimeInMillis < initialCallTimeInMillis).isTrue();
  //    assertThat(cachedResponseTimeInMillis).isBetween(0L, 2500L);
  //    // Upload overlay, thus evicting cache
  //    var facilityId = "vha_521GF";
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("application/json")
  //                .body(CMS_OVERLAY_BODY)
  //                .request(
  //                    Method.POST,
  //                    svcInternal.urlWithApiPath() + "v1/facilities/" + facilityId +
  // "/cms-overlay/"))
  //        .expect(200);
  //    // Call to /all after cache evicted
  //    start = Instant.now();
  //    ExpectedResponse.of(
  //            requestSpecification()
  //                .contentType("application/json")
  //                .request(Method.GET, svc.urlWithApiPath() +
  // "v1/facilities?page=1&per_page=100000"))
  //        .expect(200);
  //    finish = Instant.now();
  //    long cachedEvictedResponseTimeInMillis = Duration.between(start, finish).toMillis();
  //    assertThat(cachedEvictedResponseTimeInMillis > cachedResponseTimeInMillis).isTrue();
  //    assertThat(cachedEvictedResponseTimeInMillis).isBetween(0L, 4500L);
  //  }
}
