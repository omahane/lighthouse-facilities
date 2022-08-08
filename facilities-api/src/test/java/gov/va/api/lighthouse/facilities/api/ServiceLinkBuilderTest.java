package gov.va.api.lighthouse.facilities.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import gov.va.api.lighthouse.facilities.api.v1.Facility;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class ServiceLinkBuilderTest {
  @Test
  @SneakyThrows
  void buildLinkerUrlV0() {
    assertThatThrownBy(() -> ServiceLinkBuilder.buildLinkerUrlV0(null, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("baseUrl is marked non-null but is null");
    assertThatThrownBy(() -> ServiceLinkBuilder.buildLinkerUrlV0("http://foo/bar", null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("basePath is marked non-null but is null");
    assertThatThrownBy(() -> ServiceLinkBuilder.buildLinkerUrlV0(null, "/"))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("baseUrl is marked non-null but is null");
    assertThat(ServiceLinkBuilder.buildLinkerUrlV0("http://foo/bar", ""))
        .isEqualTo("http://foo/bar/v0/");
    assertThat(ServiceLinkBuilder.buildLinkerUrlV0("http://foo/bar", "/"))
        .isEqualTo("http://foo/bar/v0/");
    assertThat(ServiceLinkBuilder.buildLinkerUrlV0("http://foo/bar/", ""))
        .isEqualTo("http://foo/bar/v0/");
    assertThat(ServiceLinkBuilder.buildLinkerUrlV0("http://foo/bar/", "/"))
        .isEqualTo("http://foo/bar/v0/");
  }

  @Test
  @SneakyThrows
  void buildLinkerUrlV1() {
    assertThatThrownBy(() -> ServiceLinkBuilder.buildLinkerUrlV1(null, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("baseUrl is marked non-null but is null");
    assertThatThrownBy(() -> ServiceLinkBuilder.buildLinkerUrlV1("http://foo/bar", null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("basePath is marked non-null but is null");
    assertThatThrownBy(() -> ServiceLinkBuilder.buildLinkerUrlV1(null, "/"))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("baseUrl is marked non-null but is null");
    assertThat(ServiceLinkBuilder.buildLinkerUrlV1("http://foo/bar", ""))
        .isEqualTo("http://foo/bar/v1/");
    assertThat(ServiceLinkBuilder.buildLinkerUrlV1("http://foo/bar", "/"))
        .isEqualTo("http://foo/bar/v1/");
    assertThat(ServiceLinkBuilder.buildLinkerUrlV1("http://foo/bar/", ""))
        .isEqualTo("http://foo/bar/v1/");
    assertThat(ServiceLinkBuilder.buildLinkerUrlV1("http://foo/bar/", "/"))
        .isEqualTo("http://foo/bar/v1/");
  }

  @Test
  @SneakyThrows
  void buildServicesLink() {
    assertThatThrownBy(() -> ServiceLinkBuilder.buildServicesLink(null, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("linkerUrl is marked non-null but is null");
    assertThatThrownBy(() -> ServiceLinkBuilder.buildServicesLink("http://foo/bar/v1", null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("facilityId is marked non-null but is null");
    assertThatThrownBy(() -> ServiceLinkBuilder.buildServicesLink(null, "vha_402"))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("linkerUrl is marked non-null but is null");
    assertThat(ServiceLinkBuilder.buildServicesLink("http://foo/bar/v1", "vha_402"))
        .isEqualTo("http://foo/bar/v1/facilities/vha_402/services");
    assertThat(ServiceLinkBuilder.buildServicesLink("http://foo/bar/v1/", "vha_402"))
        .isEqualTo("http://foo/bar/v1/facilities/vha_402/services");
  }

  @Test
  @SneakyThrows
  void buildTypedServiceLink() {
    assertThatThrownBy(() -> ServiceLinkBuilder.buildTypedServiceLink(null, null, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("linkerUrl is marked non-null but is null");
    assertThatThrownBy(
            () -> ServiceLinkBuilder.buildTypedServiceLink("http://foo/bar/v1", null, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("facilityId is marked non-null but is null");
    assertThatThrownBy(
            () -> ServiceLinkBuilder.buildTypedServiceLink("http://foo/bar/v1", "vha_402", null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("serviceId is marked non-null but is null");
    assertThat(
            ServiceLinkBuilder.buildTypedServiceLink(
                "http://foo/bar/v1", "vha_402", Facility.HealthService.Cardiology.serviceId()))
        .isEqualTo("http://foo/bar/v1/facilities/vha_402/services/cardiology");
    assertThat(
            ServiceLinkBuilder.buildTypedServiceLink(
                "http://foo/bar/v1/", "vha_402", Facility.HealthService.Cardiology.serviceId()))
        .isEqualTo("http://foo/bar/v1/facilities/vha_402/services/cardiology");
  }
}
