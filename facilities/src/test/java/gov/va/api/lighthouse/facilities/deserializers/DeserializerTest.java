package gov.va.api.lighthouse.facilities.deserializers;

import static gov.va.api.lighthouse.facilities.DatamartFacilitiesJacksonConfig.createMapper;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.JsonMappingException;
import gov.va.api.lighthouse.facilities.DatamartCmsOverlay;
import gov.va.api.lighthouse.facilities.DatamartDetailedService;
import gov.va.api.lighthouse.facilities.DatamartFacility;
import gov.va.api.lighthouse.facilities.api.TypeOfService;
import gov.va.api.lighthouse.facilities.api.TypedService;
import java.time.LocalDate;
import java.util.List;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class DeserializerTest {
  @SneakyThrows
  private <T, U> void assertExceptionThrown(
      @NonNull String json,
      @NonNull Class<T> expectedClass,
      @NonNull Class<U> expectedExceptionClass,
      @NonNull String expectedMsg) {
    assertThatThrownBy(() -> createMapper().readValue(json, expectedClass))
        .isInstanceOf(expectedExceptionClass)
        .hasMessage(expectedMsg);
  }

  @SneakyThrows
  private <T> void assertJson(
      @NonNull String json, @NonNull Class<T> expectedClass, @NonNull T expectedValue) {
    assertThat(createMapper().readValue(json, expectedClass))
        .usingRecursiveComparison()
        .isEqualTo(expectedValue);
  }

  @Test
  @SneakyThrows
  void deserializeBenefitsCmsOverlay() {
    DatamartCmsOverlay overlay =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.BenefitsService.Pensions.serviceId())
                                .name(DatamartFacility.BenefitsService.Pensions.name())
                                .serviceType(TypeOfService.Benefits)
                                .build())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"serviceType\":\"benefits\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    // At least one service containing invalid service id and/or invalid service type
    DatamartCmsOverlay invalid =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.BenefitsService.Pensions.serviceId())
                                .name(DatamartFacility.BenefitsService.Pensions.name())
                                .serviceType(TypeOfService.Benefits)
                                .build())
                        .build(),
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(TypedService.INVALID_SVC_ID)
                                .name("Unrecognized Service Id")
                                .serviceType(TypeOfService.Benefits)
                                .build())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Unrecognized Service Id\",\"serviceType\":\"benefits\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        invalid);
    assertExceptionThrown(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"applyingForBenefits\",\"name\":\"Unrecognized Service Type\",\"serviceType\":\"bar\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},{\"serviceInfo\":{\"serviceId\":\"applyingForBenefits\",\"name\":\"Unrecognized Service Type\",\"serviceType\":\"bar\"}}]}\"; line: 1, column: 206] (through reference chain: gov.va.api.lighthouse.facilities.DatamartCmsOverlay$DatamartCmsOverlayBuilder[\"detailed_services\"]->java.util.ArrayList[1]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");

    overlay =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.BenefitsService.Pensions.serviceId())
                                .name(DatamartFacility.BenefitsService.Pensions.name())
                                .serviceType(TypeOfService.Benefits)
                                .build())
                        .phoneNumbers(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    // At least one service containing invalid service id and/or invalid service type
    invalid =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.BenefitsService.Pensions.serviceId())
                                .name(DatamartFacility.BenefitsService.Pensions.name())
                                .serviceType(TypeOfService.Benefits)
                                .build())
                        .phoneNumbers(emptyList())
                        .build(),
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(TypedService.INVALID_SVC_ID)
                                .name("Unrecognized Service Id")
                                .serviceType(TypeOfService.Benefits)
                                .build())
                        .phoneNumbers(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Unrecognized Service Id\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        invalid);
    assertExceptionThrown(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"applyingForBenefits\",\"name\":\"Unrecognized Service Type\",\"serviceType\":\"bar\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},{\"serviceInfo\":{\"serviceId\":\"applyingForBenefits\",\"name\":\"Unrecognized Service Type\",\"serviceType\":\"bar\"},\"appointment_phones\":[]}]}\"; line: 1, column: 230] (through reference chain: gov.va.api.lighthouse.facilities.DatamartCmsOverlay$DatamartCmsOverlayBuilder[\"detailed_services\"]->java.util.ArrayList[1]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");

    overlay =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.BenefitsService.Pensions.serviceId())
                                .name(DatamartFacility.BenefitsService.Pensions.name())
                                .serviceType(TypeOfService.Benefits)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    // At least one service containing invalid service id and/or invalid service type
    invalid =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.BenefitsService.Pensions.serviceId())
                                .name(DatamartFacility.BenefitsService.Pensions.name())
                                .serviceType(TypeOfService.Benefits)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build(),
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(TypedService.INVALID_SVC_ID)
                                .name("Unrecognized Service Id")
                                .serviceType(TypeOfService.Benefits)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Unrecognized Service Id\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        invalid);
    assertExceptionThrown(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"applyingForBenefits\",\"name\":\"Unrecognized Service Type\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},{\"serviceInfo\":{\"serviceId\":\"applyingForBenefits\",\"name\":\"Unrecognized Service Type\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}]}\"; line: 1, column: 253] (through reference chain: gov.va.api.lighthouse.facilities.DatamartCmsOverlay$DatamartCmsOverlayBuilder[\"detailed_services\"]->java.util.ArrayList[1]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");
  }

  @Test
  @SneakyThrows
  void deserializeBenefitsDetailedService() {
    DatamartDetailedService detailedService =
        DatamartDetailedService.builder()
            .serviceInfo(
                DatamartDetailedService.ServiceInfo.builder()
                    .serviceId(DatamartFacility.BenefitsService.Pensions.serviceId())
                    .name(DatamartFacility.BenefitsService.Pensions.name())
                    .serviceType(TypeOfService.Benefits)
                    .build())
            .build();
    assertJson(
        "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"}}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"serviceType\":\"benefits\"}}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}}",
        DatamartDetailedService.class,
        detailedService);
    // Service containing invalid service id and invalid service type
    DatamartDetailedService invalid =
        DatamartDetailedService.builder()
            .serviceInfo(
                DatamartDetailedService.ServiceInfo.builder()
                    .serviceId(TypedService.INVALID_SVC_ID)
                    .name("Unrecognized Service Id")
                    .serviceType(TypeOfService.Benefits)
                    .build())
            .build();
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Unrecognized Service Id\",\"serviceType\":\"benefits\"}}",
        DatamartDetailedService.class,
        invalid);
    assertExceptionThrown(
        "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Unrecognized Service Type\",\"serviceType\":\"bar\"}}",
        DatamartDetailedService.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Unrecognized Service Type\",\"serviceType\":\"bar\"}}\"; line: 1, column: 89] (through reference chain: gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");

    detailedService =
        DatamartDetailedService.builder()
            .serviceInfo(
                DatamartDetailedService.ServiceInfo.builder()
                    .serviceId(DatamartFacility.BenefitsService.Pensions.serviceId())
                    .name(DatamartFacility.BenefitsService.Pensions.name())
                    .serviceType(TypeOfService.Benefits)
                    .build())
            .phoneNumbers(emptyList())
            .build();
    assertJson(
        "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    // Service containing invalid service id and invalid service type
    invalid =
        DatamartDetailedService.builder()
            .serviceInfo(
                DatamartDetailedService.ServiceInfo.builder()
                    .serviceId(TypedService.INVALID_SVC_ID)
                    .name("Unrecognized Service Id")
                    .serviceType(TypeOfService.Benefits)
                    .build())
            .phoneNumbers(emptyList())
            .build();
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Unrecognized Service Id\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        invalid);
    assertExceptionThrown(
        "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Unrecognized Service Type\",\"serviceType\":\"bar\"},\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Unrecognized Service Type\",\"serviceType\":\"bar\"},\"appointment_phones\":[]}\"; line: 1, column: 89] (through reference chain: gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");

    detailedService =
        DatamartDetailedService.builder()
            .serviceInfo(
                DatamartDetailedService.ServiceInfo.builder()
                    .serviceId(DatamartFacility.BenefitsService.Pensions.serviceId())
                    .name(DatamartFacility.BenefitsService.Pensions.name())
                    .serviceType(TypeOfService.Benefits)
                    .build())
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson(
        "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        detailedService);
    // Service containing invalid service id and invalid service type
    invalid =
        DatamartDetailedService.builder()
            .serviceInfo(
                DatamartDetailedService.ServiceInfo.builder()
                    .serviceId(TypedService.INVALID_SVC_ID)
                    .name("Unrecognized Service Id")
                    .serviceType(TypeOfService.Benefits)
                    .build())
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Unrecognized Service Id\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        invalid);
    assertExceptionThrown(
        "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Unrecognized Service Type\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Unrecognized Service Type\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}\"; line: 1, column: 89] (through reference chain: gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");
  }

  @Test
  @SneakyThrows
  void deserializeBenefitsFacilityAttributes() {
    DatamartFacility.FacilityAttributes attributes =
        DatamartFacility.FacilityAttributes.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.BenefitsService.Pensions.serviceId())
                                .name(DatamartFacility.BenefitsService.Pensions.name())
                                .serviceType(TypeOfService.Benefits)
                                .build())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"}}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"serviceType\":\"benefits\"}}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    // At least one service containing invalid service id and/or invalid service type
    DatamartFacility.FacilityAttributes invalid =
        DatamartFacility.FacilityAttributes.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.BenefitsService.Pensions.serviceId())
                                .name(DatamartFacility.BenefitsService.Pensions.name())
                                .serviceType(TypeOfService.Benefits)
                                .build())
                        .build(),
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(TypedService.INVALID_SVC_ID)
                                .name("Unrecognized Service Id")
                                .serviceType(TypeOfService.Benefits)
                                .build())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Unrecognized Service Id\",\"serviceType\":\"benefits\"}}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        invalid);
    assertExceptionThrown(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"applyingForBenefits\",\"name\":\"Unrecognized Service Type\",\"serviceType\":\"bar\"}}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},{\"serviceInfo\":{\"serviceId\":\"applyingForBenefits\",\"name\":\"Unrecognized Service Type\",\"serviceType\":\"bar\"}}]}\"; line: 1, column: 206] (through reference chain: gov.va.api.lighthouse.facilities.DatamartFacility$FacilityAttributes$FacilityAttributesBuilder[\"detailed_services\"]->java.util.ArrayList[1]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");

    attributes =
        DatamartFacility.FacilityAttributes.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.BenefitsService.Pensions.serviceId())
                                .name(DatamartFacility.BenefitsService.Pensions.name())
                                .serviceType(TypeOfService.Benefits)
                                .build())
                        .phoneNumbers(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    // At least one service containing invalid service id and/or invalid service type
    invalid =
        DatamartFacility.FacilityAttributes.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.BenefitsService.Pensions.serviceId())
                                .name(DatamartFacility.BenefitsService.Pensions.name())
                                .serviceType(TypeOfService.Benefits)
                                .build())
                        .phoneNumbers(emptyList())
                        .build(),
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(TypedService.INVALID_SVC_ID)
                                .name("Unrecognized Service Id")
                                .serviceType(TypeOfService.Benefits)
                                .build())
                        .phoneNumbers(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Unrecognized Service Id\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        invalid);
    assertExceptionThrown(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"applyingForBenefits\",\"name\":\"Unrecognized Service Type\",\"serviceType\":\"bar\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},{\"serviceInfo\":{\"serviceId\":\"applyingForBenefits\",\"name\":\"Unrecognized Service Type\",\"serviceType\":\"bar\"},\"appointment_phones\":[]}]}\"; line: 1, column: 230] (through reference chain: gov.va.api.lighthouse.facilities.DatamartFacility$FacilityAttributes$FacilityAttributesBuilder[\"detailed_services\"]->java.util.ArrayList[1]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");

    attributes =
        DatamartFacility.FacilityAttributes.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.BenefitsService.Pensions.serviceId())
                                .name(DatamartFacility.BenefitsService.Pensions.name())
                                .serviceType(TypeOfService.Benefits)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    // At least one service containing invalid service id and/or invalid service type
    invalid =
        DatamartFacility.FacilityAttributes.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.BenefitsService.Pensions.serviceId())
                                .name(DatamartFacility.BenefitsService.Pensions.name())
                                .serviceType(TypeOfService.Benefits)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build(),
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(TypedService.INVALID_SVC_ID)
                                .name("Unrecognized Service Id")
                                .serviceType(TypeOfService.Benefits)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Unrecognized Service Id\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        invalid);
    assertExceptionThrown(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"applyingForBenefits\",\"name\":\"Unrecognized Service Type\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},{\"serviceInfo\":{\"serviceId\":\"applyingForBenefits\",\"name\":\"Unrecognized Service Type\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}]}\"; line: 1, column: 253] (through reference chain: gov.va.api.lighthouse.facilities.DatamartFacility$FacilityAttributes$FacilityAttributesBuilder[\"detailed_services\"]->java.util.ArrayList[1]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");
  }

  @Test
  @SneakyThrows
  void deserializeCmsOverlayWithInvalidDetailedServices() {
    DatamartCmsOverlay invalid =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(TypedService.INVALID_SVC_ID)
                                .name(DatamartFacility.OtherService.OnlineScheduling.name())
                                .serviceType(TypeOfService.Other)
                                .build())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        invalid);
    assertExceptionThrown(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"bar\",\"serviceType\":\"baz\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: baz\n"
            + " at [Source: (String)\"{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"bar\",\"serviceType\":\"baz\"}}]}\"; line: 1, column: 84] (through reference chain: gov.va.api.lighthouse.facilities.DatamartCmsOverlay$DatamartCmsOverlayBuilder[\"detailed_services\"]->java.util.ArrayList[0]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");
    invalid =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(TypedService.INVALID_SVC_ID)
                                .name("baz")
                                .serviceType(TypeOfService.Other)
                                .build())
                        .phoneNumbers(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"baz\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        invalid);
    invalid =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(TypedService.INVALID_SVC_ID)
                                .name(DatamartFacility.OtherService.OnlineScheduling.name())
                                .serviceType(TypeOfService.Other)
                                .build())
                        .phoneNumbers(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        invalid);
    invalid =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(TypedService.INVALID_SVC_ID)
                                .name("baz")
                                .serviceType(TypeOfService.Other)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"baz\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        invalid);
    invalid =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(TypedService.INVALID_SVC_ID)
                                .name(DatamartFacility.OtherService.OnlineScheduling.name())
                                .serviceType(TypeOfService.Other)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        invalid);
    invalid =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(TypedService.INVALID_SVC_ID)
                                .name("bar")
                                .serviceType(TypeOfService.Other)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"bar\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        invalid);
  }

  @Test
  @SneakyThrows
  void deserializeCmsOverlayWithMixedDetailedServices() {
    DatamartCmsOverlay overlay =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.BenefitsService.Pensions.serviceId())
                                .name(DatamartFacility.BenefitsService.Pensions.name())
                                .serviceType(TypeOfService.Benefits)
                                .build())
                        .build(),
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.HealthService.Smoking.serviceId())
                                .name(DatamartFacility.HealthService.Smoking.name())
                                .serviceType(TypeOfService.Health)
                                .build())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"serviceType\":\"health\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    // At least one service containing invalid service id and/or invalid service type
    assertExceptionThrown(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Unrecognized Service Id\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"cardiology\",\"name\":\"Unrecognized Service Type\",\"serviceType\":\"bin\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bin\n"
            + " at [Source: (String)\"{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"}},{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Unrecognized Service Id\",\"serviceType\":\"benefits\"}},{\"serviceInfo\":{\"serviceId\":\"cardiology\",\"name\":\"Unrecognized Service Type\",\"serviceType\":\"bin\"}}]}\"; line: 1, column: 371] (through reference chain: gov.va.api.lighthouse.facilities.DatamartCmsOverlay$DatamartCmsOverlayBuilder[\"detailed_services\"]->java.util.ArrayList[3]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");

    overlay =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.BenefitsService.Pensions.serviceId())
                                .name(DatamartFacility.BenefitsService.Pensions.name())
                                .serviceType(TypeOfService.Benefits)
                                .build())
                        .phoneNumbers(emptyList())
                        .build(),
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.HealthService.Smoking.serviceId())
                                .name(DatamartFacility.HealthService.Smoking.name())
                                .serviceType(TypeOfService.Health)
                                .build())
                        .phoneNumbers(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    // At least one service containing invalid service id and/or invalid service type
    assertExceptionThrown(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Unrecognized Service Id\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"cardiology\",\"name\":\"Unrecognized Service Type\",\"serviceType\":\"bin\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bin\n"
            + " at [Source: (String)\"{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[]},{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Unrecognized Service Id\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},{\"serviceInfo\":{\"serviceId\":\"cardiology\",\"name\":\"Unrecognized Service Type\",\"serviceType\":\"bin\"},\"appointment_phones\":[]}]}\"; line: 1, column: 443] (through reference chain: gov.va.api.lighthouse.facilities.DatamartCmsOverlay$DatamartCmsOverlayBuilder[\"detailed_services\"]->java.util.ArrayList[3]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");

    overlay =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.BenefitsService.Pensions.serviceId())
                                .name(DatamartFacility.BenefitsService.Pensions.name())
                                .serviceType(TypeOfService.Benefits)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build(),
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.HealthService.Smoking.serviceId())
                                .name(DatamartFacility.HealthService.Smoking.name())
                                .serviceType(TypeOfService.Health)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    // At least one service containing invalid service id and/or invalid service type
    assertExceptionThrown(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Unrecognized Service Id\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"Unrecognized Service Type\",\"serviceType\":\"bin\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bin\n"
            + " at [Source: (String)\"{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]},{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Unrecognized Service Id\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"Unrecognized Service Type\",\"serviceTy\"[truncated 60 chars]; line: 1, column: 505] (through reference chain: gov.va.api.lighthouse.facilities.DatamartCmsOverlay$DatamartCmsOverlayBuilder[\"detailed_services\"]->java.util.ArrayList[3]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");
  }

  @Test
  @SneakyThrows
  void deserializeFacilityWithInvalidDetailedServices() {
    DatamartFacility invalid =
        DatamartFacility.builder()
            .id("vha_402")
            .type(DatamartFacility.Type.va_facilities)
            .attributes(
                DatamartFacility.FacilityAttributes.builder()
                    .detailedServices(
                        List.of(
                            DatamartDetailedService.builder()
                                .serviceInfo(
                                    DatamartDetailedService.ServiceInfo.builder()
                                        .serviceId(TypedService.INVALID_SVC_ID)
                                        .name(DatamartFacility.OtherService.OnlineScheduling.name())
                                        .serviceType(TypeOfService.Other)
                                        .build())
                                .build()))
                    .build())
            .build();
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}"
            + "]"
            + "}}",
        DatamartFacility.class,
        invalid);
    assertExceptionThrown(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"bar\",\"serviceType\":\"baz\"}}"
            + "]"
            + "}}",
        DatamartFacility.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: baz\n"
            + " at [Source: (String)\"{\"id\":\"vha_402\",\"type\":\"va_facilities\",\"attributes\":{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"bar\",\"serviceType\":\"baz\"}}]}}\"; line: 1, column: 136] (through reference chain: gov.va.api.lighthouse.facilities.DatamartFacility[\"attributes\"]->gov.va.api.lighthouse.facilities.DatamartFacility$FacilityAttributes$FacilityAttributesBuilder[\"detailed_services\"]->java.util.ArrayList[0]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");
    invalid =
        DatamartFacility.builder()
            .id("vha_402")
            .type(DatamartFacility.Type.va_facilities)
            .attributes(
                DatamartFacility.FacilityAttributes.builder()
                    .detailedServices(
                        List.of(
                            DatamartDetailedService.builder()
                                .serviceInfo(
                                    DatamartDetailedService.ServiceInfo.builder()
                                        .serviceId(TypedService.INVALID_SVC_ID)
                                        .name("baz")
                                        .serviceType(TypeOfService.Other)
                                        .build())
                                .phoneNumbers(emptyList())
                                .build()))
                    .build())
            .build();
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"baz\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]"
            + "}}",
        DatamartFacility.class,
        invalid);
    invalid =
        DatamartFacility.builder()
            .id("vha_402")
            .type(DatamartFacility.Type.va_facilities)
            .attributes(
                DatamartFacility.FacilityAttributes.builder()
                    .detailedServices(
                        List.of(
                            DatamartDetailedService.builder()
                                .serviceInfo(
                                    DatamartDetailedService.ServiceInfo.builder()
                                        .serviceId(TypedService.INVALID_SVC_ID)
                                        .name(DatamartFacility.OtherService.OnlineScheduling.name())
                                        .serviceType(TypeOfService.Other)
                                        .build())
                                .phoneNumbers(emptyList())
                                .build()))
                    .build())
            .build();
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]"
            + "}}",
        DatamartFacility.class,
        invalid);
    assertExceptionThrown(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"baz\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]"
            + "}}",
        DatamartFacility.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"id\":\"vha_402\",\"type\":\"va_facilities\",\"attributes\":{\"detailed_services\":[{\"serviceInfo\":{\"name\":\"baz\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}]}}\"; line: 1, column: 118] (through reference chain: gov.va.api.lighthouse.facilities.DatamartFacility[\"attributes\"]->gov.va.api.lighthouse.facilities.DatamartFacility$FacilityAttributes$FacilityAttributesBuilder[\"detailed_services\"]->java.util.ArrayList[0]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");
    invalid =
        DatamartFacility.builder()
            .id("vha_402")
            .type(DatamartFacility.Type.va_facilities)
            .attributes(
                DatamartFacility.FacilityAttributes.builder()
                    .detailedServices(
                        List.of(
                            DatamartDetailedService.builder()
                                .serviceInfo(
                                    DatamartDetailedService.ServiceInfo.builder()
                                        .serviceId(TypedService.INVALID_SVC_ID)
                                        .name(DatamartFacility.OtherService.OnlineScheduling.name())
                                        .serviceType(TypeOfService.Other)
                                        .build())
                                .phoneNumbers(emptyList())
                                .serviceLocations(emptyList())
                                .build()))
                    .build())
            .build();
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]"
            + "}}",
        DatamartFacility.class,
        invalid);
    assertExceptionThrown(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"bar\",\"serviceType\":\"baz\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]"
            + "}}",
        DatamartFacility.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: baz\n"
            + " at [Source: (String)\"{\"id\":\"vha_402\",\"type\":\"va_facilities\",\"attributes\":{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"bar\",\"serviceType\":\"baz\"},\"appointment_phones\":[],\"service_locations\":[]}]}}\"; line: 1, column: 136] (through reference chain: gov.va.api.lighthouse.facilities.DatamartFacility[\"attributes\"]->gov.va.api.lighthouse.facilities.DatamartFacility$FacilityAttributes$FacilityAttributesBuilder[\"detailed_services\"]->java.util.ArrayList[0]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");
  }

  @Test
  @SneakyThrows
  void deserializeFacilityWithInvalidServices() {
    final var facilityId = "vha_402";
    DatamartFacility facility =
        DatamartFacility.builder()
            .id(facilityId)
            .type(DatamartFacility.Type.va_facilities)
            .attributes(
                DatamartFacility.FacilityAttributes.builder()
                    .services(
                        DatamartFacility.Services.builder()
                            .lastUpdated(LocalDate.parse("2022-03-07"))
                            .build())
                    .build())
            .build();
    // No services
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"services\":{"
            + "\"last_updated\":\"2022-03-07\""
            + "}}}",
        DatamartFacility.class,
        facility);
    // Invalid services express in V0 format
    assertExceptionThrown(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"services\":{"
            + "\"benefits\": [\"foo\"],"
            + "\"health\": [\"bar\"],"
            + "\"other\": [\"baz\"],"
            + "\"last_updated\":\"2022-03-07\""
            + "}}}",
        DatamartFacility.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.DatamartFacility$BenefitsService`, problem: No enum constant gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.Foo\n"
            + " at [Source: (String)\"{\"id\":\"vha_402\",\"type\":\"va_facilities\",\"attributes\":{\"services\":{\"benefits\": [\"foo\"],\"health\": [\"bar\"],\"other\": [\"baz\"],\"last_updated\":\"2022-03-07\"}}}\"; line: 1, column: 79] (through reference chain: gov.va.api.lighthouse.facilities.DatamartFacility[\"attributes\"]->gov.va.api.lighthouse.facilities.DatamartFacility$FacilityAttributes$FacilityAttributesBuilder[\"services\"]->gov.va.api.lighthouse.facilities.DatamartFacility$Services$ServicesBuilder[\"benefits\"]->java.util.ArrayList[0])");
    // Invalid services expressed in V1 format
    assertExceptionThrown(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"services\":{"
            + "\"benefits\": [{"
            + "\"serviceId\":\"foo\","
            + "\"name\":\"Foo\""
            + "}],"
            + "\"health\": [{"
            + "\"serviceId\":\"bar\","
            + "\"name\":\"Bar\""
            + "}],"
            + "\"other\": [{"
            + "\"serviceId\":\"baz\","
            + "\"name\":\"Baz\""
            + "}],"
            + "\"last_updated\":\"2022-03-07\""
            + "}}}",
        DatamartFacility.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.DatamartFacility$BenefitsService`, problem: Name is null\n"
            + " at [Source: (String)\"{\"id\":\"vha_402\",\"type\":\"va_facilities\",\"attributes\":{\"services\":{\"benefits\": [{\"serviceId\":\"foo\",\"name\":\"Foo\"}],\"health\": [{\"serviceId\":\"bar\",\"name\":\"Bar\"}],\"other\": [{\"serviceId\":\"baz\",\"name\":\"Baz\"}],\"last_updated\":\"2022-03-07\"}}}\"; line: 1, column: 79] (through reference chain: gov.va.api.lighthouse.facilities.DatamartFacility[\"attributes\"]->gov.va.api.lighthouse.facilities.DatamartFacility$FacilityAttributes$FacilityAttributesBuilder[\"services\"]->gov.va.api.lighthouse.facilities.DatamartFacility$Services$ServicesBuilder[\"benefits\"]->java.util.ArrayList[0])");
  }

  @Test
  @SneakyThrows
  void deserializeFacilityWithMixedDetailedServices() {
    final var facilityId = "vha_402";
    DatamartFacility facility =
        DatamartFacility.builder()
            .id(facilityId)
            .type(DatamartFacility.Type.va_facilities)
            .attributes(
                DatamartFacility.FacilityAttributes.builder()
                    .detailedServices(
                        List.of(
                            DatamartDetailedService.builder()
                                .serviceInfo(
                                    DatamartDetailedService.ServiceInfo.builder()
                                        .serviceId(
                                            DatamartFacility.BenefitsService.Pensions.serviceId())
                                        .name(DatamartFacility.BenefitsService.Pensions.name())
                                        .serviceType(TypeOfService.Benefits)
                                        .build())
                                .build(),
                            DatamartDetailedService.builder()
                                .serviceInfo(
                                    DatamartDetailedService.ServiceInfo.builder()
                                        .serviceId(
                                            DatamartFacility.HealthService.Smoking.serviceId())
                                        .name(DatamartFacility.HealthService.Smoking.name())
                                        .serviceType(TypeOfService.Health)
                                        .build())
                                .build()))
                    .build())
            .build();
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"}}"
            + "]"
            + "}}",
        DatamartFacility.class,
        facility);
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"serviceType\":\"health\"}}"
            + "]"
            + "}}",
        DatamartFacility.class,
        facility);
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"}}"
            + "]"
            + "}}",
        DatamartFacility.class,
        facility);
    // At least one service containing invalid service id and valid service type
    DatamartFacility invalid =
        DatamartFacility.builder()
            .id(facilityId)
            .type(DatamartFacility.Type.va_facilities)
            .attributes(
                DatamartFacility.FacilityAttributes.builder()
                    .detailedServices(
                        List.of(
                            DatamartDetailedService.builder()
                                .serviceInfo(
                                    DatamartDetailedService.ServiceInfo.builder()
                                        .serviceId(
                                            DatamartFacility.BenefitsService.Pensions.serviceId())
                                        .name(DatamartFacility.BenefitsService.Pensions.name())
                                        .serviceType(TypeOfService.Benefits)
                                        .build())
                                .build(),
                            DatamartDetailedService.builder()
                                .serviceInfo(
                                    DatamartDetailedService.ServiceInfo.builder()
                                        .serviceId(
                                            DatamartFacility.HealthService.Smoking.serviceId())
                                        .name(DatamartFacility.HealthService.Smoking.name())
                                        .serviceType(TypeOfService.Health)
                                        .build())
                                .build(),
                            DatamartDetailedService.builder()
                                .serviceInfo(
                                    DatamartDetailedService.ServiceInfo.builder()
                                        .serviceId(TypedService.INVALID_SVC_ID)
                                        .serviceType(TypeOfService.Other)
                                        .build())
                                .build()))
                    .build())
            .build();
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"serviceType\":\"health\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"serviceType\":\"other\"}}"
            + "]"
            + "}}",
        DatamartFacility.class,
        invalid);
    // At least one service containing valid service id and invalid service type
    assertExceptionThrown(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"serviceType\":\"health\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"serviceType\":\"bar\"}}"
            + "]"
            + "}}",
        DatamartFacility.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"id\":\"vha_402\",\"type\":\"va_facilities\",\"attributes\":{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"pensions\",\"serviceType\":\"benefits\"}},{\"serviceInfo\":{\"serviceId\":\"smoking\",\"serviceType\":\"health\"}},{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"serviceType\":\"bar\"}}]}}\"; line: 1, column: 265] (through reference chain: gov.va.api.lighthouse.facilities.DatamartFacility[\"attributes\"]->gov.va.api.lighthouse.facilities.DatamartFacility$FacilityAttributes$FacilityAttributesBuilder[\"detailed_services\"]->java.util.ArrayList[2]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");
    // At least one service containing invalid service id and invalid service type
    assertExceptionThrown(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"serviceType\":\"health\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"serviceType\":\"bar\"}}"
            + "]"
            + "}}",
        DatamartFacility.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"id\":\"vha_402\",\"type\":\"va_facilities\",\"attributes\":{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"pensions\",\"serviceType\":\"benefits\"}},{\"serviceInfo\":{\"serviceId\":\"smoking\",\"serviceType\":\"health\"}},{\"serviceInfo\":{\"serviceId\":\"foo\",\"serviceType\":\"bar\"}}]}}\"; line: 1, column: 252] (through reference chain: gov.va.api.lighthouse.facilities.DatamartFacility[\"attributes\"]->gov.va.api.lighthouse.facilities.DatamartFacility$FacilityAttributes$FacilityAttributesBuilder[\"detailed_services\"]->java.util.ArrayList[2]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");
    // At least one service containing invalid service id and/or invalid service type
    assertExceptionThrown(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"foo\"}}"
            + "]"
            + "}}",
        DatamartFacility.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: foo\n"
            + " at [Source: (String)\"{\"id\":\"vha_402\",\"type\":\"va_facilities\",\"attributes\":{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"}},{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"}},{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"foo\"}}]}}\"; line: 1, column: 379] (through reference chain: gov.va.api.lighthouse.facilities.DatamartFacility[\"attributes\"]->gov.va.api.lighthouse.facilities.DatamartFacility$FacilityAttributes$FacilityAttributesBuilder[\"detailed_services\"]->java.util.ArrayList[3]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");

    facility =
        DatamartFacility.builder()
            .id("vha_402")
            .type(DatamartFacility.Type.va_facilities)
            .attributes(
                DatamartFacility.FacilityAttributes.builder()
                    .detailedServices(
                        List.of(
                            DatamartDetailedService.builder()
                                .serviceInfo(
                                    DatamartDetailedService.ServiceInfo.builder()
                                        .serviceId(
                                            DatamartFacility.BenefitsService.Pensions.serviceId())
                                        .name(DatamartFacility.BenefitsService.Pensions.name())
                                        .serviceType(TypeOfService.Benefits)
                                        .build())
                                .phoneNumbers(emptyList())
                                .build(),
                            DatamartDetailedService.builder()
                                .serviceInfo(
                                    DatamartDetailedService.ServiceInfo.builder()
                                        .serviceId(
                                            DatamartFacility.HealthService.Smoking.serviceId())
                                        .name(DatamartFacility.HealthService.Smoking.name())
                                        .serviceType(TypeOfService.Health)
                                        .build())
                                .phoneNumbers(emptyList())
                                .build()))
                    .build())
            .build();
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[]}"
            + "]"
            + "}}",
        DatamartFacility.class,
        facility);
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[]}"
            + "]"
            + "}}",
        DatamartFacility.class,
        facility);
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[]}"
            + "]"
            + "}}",
        DatamartFacility.class,
        facility);
    // At least one service containing invalid service id and/or invalid service type
    assertExceptionThrown(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"foo\"},\"appointment_phones\":[]}"
            + "]"
            + "}}",
        DatamartFacility.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: foo\n"
            + " at [Source: (String)\"{\"id\":\"vha_402\",\"type\":\"va_facilities\",\"attributes\":{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[]},{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"benefits\"},\"appointment_phones\":[]},{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"foo\"},\"appointment_phones\":[]}]}}\"; line: 1, column: 456] (through reference chain: gov.va.api.lighthouse.facilities.DatamartFacility[\"attributes\"]->gov.va.api.lighthouse.facilities.DatamartFacility$FacilityAttributes$FacilityAttributesBuilder[\"detailed_services\"]->java.util.ArrayList[3]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");

    facility =
        DatamartFacility.builder()
            .id("vha_402")
            .type(DatamartFacility.Type.va_facilities)
            .attributes(
                DatamartFacility.FacilityAttributes.builder()
                    .detailedServices(
                        List.of(
                            DatamartDetailedService.builder()
                                .serviceInfo(
                                    DatamartDetailedService.ServiceInfo.builder()
                                        .serviceId(
                                            DatamartFacility.BenefitsService.Pensions.serviceId())
                                        .name(DatamartFacility.BenefitsService.Pensions.name())
                                        .serviceType(TypeOfService.Benefits)
                                        .build())
                                .phoneNumbers(emptyList())
                                .serviceLocations(emptyList())
                                .build(),
                            DatamartDetailedService.builder()
                                .serviceInfo(
                                    DatamartDetailedService.ServiceInfo.builder()
                                        .serviceId(
                                            DatamartFacility.HealthService.Smoking.serviceId())
                                        .name(DatamartFacility.HealthService.Smoking.name())
                                        .serviceType(TypeOfService.Health)
                                        .build())
                                .phoneNumbers(emptyList())
                                .serviceLocations(emptyList())
                                .build()))
                    .build())
            .build();
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]"
            + "}}",
        DatamartFacility.class,
        facility);
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]"
            + "}}",
        DatamartFacility.class,
        facility);
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]"
            + "}}",
        DatamartFacility.class,
        facility);
    // At least one service containing invalid service id and/or invalid service type
    assertExceptionThrown(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"baz\",\"serviceType\":\"foo\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]"
            + "}}",
        DatamartFacility.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: foo\n"
            + " at [Source: (String)\"{\"id\":\"vha_402\",\"type\":\"va_facilities\",\"attributes\":{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"pensions\",\"name\":\"Pensions\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},{\"serviceInfo\":{\"serviceId\":\"smoking\",\"name\":\"Smoking\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]},{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"benefits\"},\"appointment_phones\":[],\"service_locations\":[]},{\"serviceInfo\":{\"serviceId\":\"bar\",\"na\"[truncated 81 chars]; line: 1, column: 525] (through reference chain: gov.va.api.lighthouse.facilities.DatamartFacility[\"attributes\"]->gov.va.api.lighthouse.facilities.DatamartFacility$FacilityAttributes$FacilityAttributesBuilder[\"detailed_services\"]->java.util.ArrayList[3]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");
  }

  @Test
  @SneakyThrows
  void deserializeFacilityWithServicesInBothFormats() {
    DatamartFacility facility =
        DatamartFacility.builder()
            .id("vha_402")
            .type(DatamartFacility.Type.va_facilities)
            .attributes(
                DatamartFacility.FacilityAttributes.builder()
                    .services(
                        DatamartFacility.Services.builder()
                            .benefits(List.of(DatamartFacility.BenefitsService.Pensions))
                            .health(
                                List.of(
                                    DatamartFacility.HealthService.Cardiology,
                                    DatamartFacility.HealthService.PrimaryCare))
                            .other(List.of(DatamartFacility.OtherService.OnlineScheduling))
                            .lastUpdated(LocalDate.parse("2022-03-07"))
                            .build())
                    .build())
            .build();
    // Services expressed in service name format
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"services\":{"
            + "\"benefits\": ["
            + "\"Pensions\""
            + "],"
            + "\"health\": ["
            + "\"Cardiology\","
            + "\"PrimaryCare\""
            + "],"
            + "\"other\": ["
            + "\"OnlineScheduling\""
            + "],"
            + "\"last_updated\":\"2022-03-07\""
            + "}}}",
        DatamartFacility.class,
        facility);
    // Services expressed in service id format
    assertJson(
        "{\"id\":\"vha_402\","
            + "\"type\":\"va_facilities\","
            + "\"attributes\":{"
            + "\"services\":{"
            + "\"benefits\": ["
            + "\"pensions\""
            + "],"
            + "\"health\": ["
            + "\"cardiology\","
            + "\"primaryCare\""
            + "],"
            + "\"other\": ["
            + "\"onlineScheduling\""
            + "],"
            + "\"last_updated\":\"2022-03-07\""
            + "}}}",
        DatamartFacility.class,
        facility);
  }

  @Test
  @SneakyThrows
  void deserializeHealthCmsOverlay() {
    DatamartCmsOverlay overlay =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.HealthService.Dental.serviceId())
                                .name(DatamartFacility.HealthService.Dental.name())
                                .serviceType(TypeOfService.Health)
                                .build())
                        .build(),
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.HealthService.MentalHealth.serviceId())
                                .name(DatamartFacility.HealthService.MentalHealth.name())
                                .serviceType(TypeOfService.Health)
                                .build())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"}},"
            + "{\"serviceInfo\":{\"name\":\"MentalHealth\",\"serviceType\":\"health\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"serviceType\":\"health\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"mentalHealth\",\"serviceType\":\"health\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"mentalHealth\",\"name\":\"MentalHealth\",\"serviceType\":\"health\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    // At least one service containing invalid service id and/or invalid service type
    assertExceptionThrown(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"mentalHealth\",\"name\":\"MentalHealth\",\"serviceType\":\"health\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"}},{\"serviceInfo\":{\"serviceId\":\"mentalHealth\",\"name\":\"MentalHealth\",\"serviceType\":\"health\"}},{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"}}]}\"; line: 1, column: 262] (through reference chain: gov.va.api.lighthouse.facilities.DatamartCmsOverlay$DatamartCmsOverlayBuilder[\"detailed_services\"]->java.util.ArrayList[2]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");

    overlay =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.HealthService.Dental.serviceId())
                                .name(DatamartFacility.HealthService.Dental.name())
                                .serviceType(TypeOfService.Health)
                                .build())
                        .phoneNumbers(emptyList())
                        .build(),
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.HealthService.MentalHealth.serviceId())
                                .name(DatamartFacility.HealthService.MentalHealth.name())
                                .serviceType(TypeOfService.Health)
                                .build())
                        .phoneNumbers(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"name\":\"MentalHealth\",\"serviceType\":\"health\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"serviceType\":\"health\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"mentalHealth\",\"serviceType\":\"health\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"mentalHealth\",\"name\":\"MentalHealth\",\"serviceType\":\"health\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    // At least one service containing invalid service id and/or invalid service type
    assertExceptionThrown(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"mentalHealth\",\"name\":\"MentalHealth\",\"serviceType\":\"health\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[]},{\"serviceInfo\":{\"serviceId\":\"mentalHealth\",\"name\":\"MentalHealth\",\"serviceType\":\"health\"},\"appointment_phones\":[]},{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"},\"appointment_phones\":[]}]}\"; line: 1, column: 310] (through reference chain: gov.va.api.lighthouse.facilities.DatamartCmsOverlay$DatamartCmsOverlayBuilder[\"detailed_services\"]->java.util.ArrayList[2]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");

    overlay =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.HealthService.Dental.serviceId())
                                .name(DatamartFacility.HealthService.Dental.name())
                                .serviceType(TypeOfService.Health)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build(),
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.HealthService.MentalHealth.serviceId())
                                .name(DatamartFacility.HealthService.MentalHealth.name())
                                .serviceType(TypeOfService.Health)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"name\":\"MentalHealth\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"mentalHealth\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"mentalHealth\",\"name\":\"MentalHealth\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    // At least one service containing invalid service id and/or invalid service type
    assertExceptionThrown(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"mentalHealth\",\"name\":\"MentalHealth\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]},{\"serviceInfo\":{\"serviceId\":\"mentalHealth\",\"name\":\"MentalHealth\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]},{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}]}\"; line: 1, column: 356] (through reference chain: gov.va.api.lighthouse.facilities.DatamartCmsOverlay$DatamartCmsOverlayBuilder[\"detailed_services\"]->java.util.ArrayList[2]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");
  }

  @Test
  @SneakyThrows
  void deserializeHealthDetailedService() {
    DatamartDetailedService detailedService =
        DatamartDetailedService.builder()
            .serviceInfo(
                DatamartDetailedService.ServiceInfo.builder()
                    .serviceId(DatamartFacility.HealthService.Dental.serviceId())
                    .name(DatamartFacility.HealthService.Dental.name())
                    .serviceType(TypeOfService.Health)
                    .build())
            .build();
    assertJson(
        "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"}}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"dental\",\"serviceType\":\"health\"}}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"}}",
        DatamartDetailedService.class,
        detailedService);
    // Service containing invalid service id and invalid service type
    assertExceptionThrown(
        "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"}}",
        DatamartDetailedService.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"}}\"; line: 1, column: 72] (through reference chain: gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");

    detailedService =
        DatamartDetailedService.builder()
            .serviceInfo(
                DatamartDetailedService.ServiceInfo.builder()
                    .serviceId(DatamartFacility.HealthService.Dental.serviceId())
                    .name(DatamartFacility.HealthService.Dental.name())
                    .serviceType(TypeOfService.Health)
                    .build())
            .phoneNumbers(emptyList())
            .build();
    assertJson(
        "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"dental\",\"serviceType\":\"health\"},\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    // Service containing invalid service id and invalid service type
    assertExceptionThrown(
        "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"},\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"},\"appointment_phones\":[]}\"; line: 1, column: 72] (through reference chain: gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");

    detailedService =
        DatamartDetailedService.builder()
            .serviceInfo(
                DatamartDetailedService.ServiceInfo.builder()
                    .serviceId(DatamartFacility.HealthService.Dental.serviceId())
                    .name(DatamartFacility.HealthService.Dental.name())
                    .serviceType(TypeOfService.Health)
                    .build())
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson(
        "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"dental\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        detailedService);
    // Service containing invalid service id and invalid service type
    assertExceptionThrown(
        "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}\"; line: 1, column: 72] (through reference chain: gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");
  }

  @Test
  @SneakyThrows
  void deserializeHealthFacilityAttributes() {
    DatamartFacility.FacilityAttributes attributes =
        DatamartFacility.FacilityAttributes.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.HealthService.Dental.serviceId())
                                .name(DatamartFacility.HealthService.Dental.name())
                                .serviceType(TypeOfService.Health)
                                .build())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"}}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"serviceType\":\"health\"}}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"}}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    // At least one service containing invalid service id and/or invalid service type
    assertExceptionThrown(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"}}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"}},{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"}}]}\"; line: 1, column: 172] (through reference chain: gov.va.api.lighthouse.facilities.DatamartFacility$FacilityAttributes$FacilityAttributesBuilder[\"detailed_services\"]->java.util.ArrayList[1]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");

    attributes =
        DatamartFacility.FacilityAttributes.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.HealthService.Dental.serviceId())
                                .name(DatamartFacility.HealthService.Dental.name())
                                .serviceType(TypeOfService.Health)
                                .build())
                        .phoneNumbers(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"serviceType\":\"health\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    // At least one service containing invalid service id and/or invalid service type
    assertExceptionThrown(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[]},{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"},\"appointment_phones\":[]}]}\"; line: 1, column: 196] (through reference chain: gov.va.api.lighthouse.facilities.DatamartFacility$FacilityAttributes$FacilityAttributesBuilder[\"detailed_services\"]->java.util.ArrayList[1]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");

    attributes =
        DatamartFacility.FacilityAttributes.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(DatamartFacility.HealthService.Dental.serviceId())
                                .name(DatamartFacility.HealthService.Dental.name())
                                .serviceType(TypeOfService.Health)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    // At least one service containing invalid service id and/or invalid service type
    assertExceptionThrown(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"dental\",\"name\":\"Dental\",\"serviceType\":\"health\"},\"appointment_phones\":[],\"service_locations\":[]},{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}]}\"; line: 1, column: 219] (through reference chain: gov.va.api.lighthouse.facilities.DatamartFacility$FacilityAttributes$FacilityAttributesBuilder[\"detailed_services\"]->java.util.ArrayList[1]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");
  }

  @Test
  @SneakyThrows
  void deserializeInvalidDetailedService() {
    assertExceptionThrown(
        "{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"bar\"}}",
        DatamartDetailedService.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"bar\"}}\"; line: 1, column: 44] (through reference chain: gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");
    DatamartDetailedService invalid =
        DatamartDetailedService.builder()
            .serviceInfo(
                DatamartDetailedService.ServiceInfo.builder()
                    .serviceId(TypedService.INVALID_SVC_ID)
                    .name(DatamartFacility.OtherService.OnlineScheduling.name())
                    .serviceType(TypeOfService.Other)
                    .build())
            .build();
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}",
        DatamartDetailedService.class,
        invalid);
    assertExceptionThrown(
        "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"baz\",\"serviceType\":\"bar\"}}",
        DatamartDetailedService.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"baz\",\"serviceType\":\"bar\"}}\"; line: 1, column: 62] (through reference chain: gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");
    invalid =
        DatamartDetailedService.builder()
            .serviceInfo(
                DatamartDetailedService.ServiceInfo.builder()
                    .serviceId(TypedService.INVALID_SVC_ID)
                    .name(DatamartFacility.HealthService.Smoking.name())
                    .serviceType(TypeOfService.Health)
                    .build())
            .build();
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"bar\",\"name\":\"Smoking\",\"serviceType\":\"health\"}}",
        DatamartDetailedService.class,
        invalid);
  }

  @Test
  @SneakyThrows
  void deserializeOtherCmsOverlay() {
    DatamartCmsOverlay overlay =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(
                                    DatamartFacility.OtherService.OnlineScheduling.serviceId())
                                .name(DatamartFacility.OtherService.OnlineScheduling.name())
                                .serviceType(TypeOfService.Other)
                                .build())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"serviceType\":\"other\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    // At least one service containing invalid service id and/or invalid service type
    assertExceptionThrown(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"}}"
            + "]}",
        DatamartCmsOverlay.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}},{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"}}]}\"; line: 1, column: 191] (through reference chain: gov.va.api.lighthouse.facilities.DatamartCmsOverlay$DatamartCmsOverlayBuilder[\"detailed_services\"]->java.util.ArrayList[1]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");

    overlay =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(
                                    DatamartFacility.OtherService.OnlineScheduling.serviceId())
                                .name(DatamartFacility.OtherService.OnlineScheduling.name())
                                .serviceType(TypeOfService.Other)
                                .build())
                        .phoneNumbers(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    // At least one service containing invalid service id and/or invalid service type
    assertExceptionThrown(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]},{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"},\"appointment_phones\":[]}]}\"; line: 1, column: 215] (through reference chain: gov.va.api.lighthouse.facilities.DatamartCmsOverlay$DatamartCmsOverlayBuilder[\"detailed_services\"]->java.util.ArrayList[1]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");

    overlay =
        DatamartCmsOverlay.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(
                                    DatamartFacility.OtherService.OnlineScheduling.serviceId())
                                .name(DatamartFacility.OtherService.OnlineScheduling.name())
                                .serviceType(TypeOfService.Other)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        overlay);
    // At least one service containing invalid service id and/or invalid service type
    assertExceptionThrown(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartCmsOverlay.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]},{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}]}\"; line: 1, column: 238] (through reference chain: gov.va.api.lighthouse.facilities.DatamartCmsOverlay$DatamartCmsOverlayBuilder[\"detailed_services\"]->java.util.ArrayList[1]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");
  }

  @Test
  @SneakyThrows
  void deserializeOtherDetailedService() {
    DatamartDetailedService detailedService =
        DatamartDetailedService.builder()
            .serviceInfo(
                DatamartDetailedService.ServiceInfo.builder()
                    .serviceId(DatamartFacility.OtherService.OnlineScheduling.serviceId())
                    .name(DatamartFacility.OtherService.OnlineScheduling.name())
                    .serviceType(TypeOfService.Other)
                    .build())
            .build();
    assertJson(
        "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"serviceType\":\"other\"}}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}",
        DatamartDetailedService.class,
        detailedService);
    // Service containing invalid service id and invalid service type
    assertExceptionThrown(
        "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"}}",
        DatamartDetailedService.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"}}\"; line: 1, column: 72] (through reference chain: gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");

    detailedService =
        DatamartDetailedService.builder()
            .serviceInfo(
                DatamartDetailedService.ServiceInfo.builder()
                    .serviceId(DatamartFacility.OtherService.OnlineScheduling.serviceId())
                    .name(DatamartFacility.OtherService.OnlineScheduling.name())
                    .serviceType(TypeOfService.Other)
                    .build())
            .phoneNumbers(emptyList())
            .build();
    assertJson(
        "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        detailedService);
    // Service containing invalid service id and invalid service type
    assertExceptionThrown(
        "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"},\"appointment_phones\":[]}",
        DatamartDetailedService.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"},\"appointment_phones\":[]}\"; line: 1, column: 72] (through reference chain: gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");

    detailedService =
        DatamartDetailedService.builder()
            .serviceInfo(
                DatamartDetailedService.ServiceInfo.builder()
                    .serviceId(DatamartFacility.OtherService.OnlineScheduling.serviceId())
                    .name(DatamartFacility.OtherService.OnlineScheduling.name())
                    .serviceType(TypeOfService.Other)
                    .build())
            .phoneNumbers(emptyList())
            .serviceLocations(emptyList())
            .build();
    assertJson(
        "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        detailedService);
    assertJson(
        "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        detailedService);
    // Service containing invalid service id and invalid service type
    assertExceptionThrown(
        "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}",
        DatamartDetailedService.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}\"; line: 1, column: 72] (through reference chain: gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");
  }

  @Test
  @SneakyThrows
  void deserializeOtherFacilityAttributes() {
    DatamartFacility.FacilityAttributes attributes =
        DatamartFacility.FacilityAttributes.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(
                                    DatamartFacility.OtherService.OnlineScheduling.serviceId())
                                .name(DatamartFacility.OtherService.OnlineScheduling.name())
                                .serviceType(TypeOfService.Other)
                                .build())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"serviceType\":\"other\"}}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    // At least one service containing invalid service name and valid service type
    assertExceptionThrown(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}},"
            + "{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"other\"}}"
            + "]}",
        DatamartFacility.class,
        JsonMappingException.class,
        "Unrecognized field \"detailed_services\" (class gov.va.api.lighthouse.facilities.DatamartFacility), not marked as ignorable (3 known properties: \"type\", \"id\", \"attributes\"])\n"
            + " at [Source: (String)\"{\"detailed_services\":[{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}},{\"serviceInfo\":{\"name\":\"foo\",\"serviceType\":\"other\"}}]}\"; line: 1, column: 23] (through reference chain: gov.va.api.lighthouse.facilities.DatamartFacility[\"detailed_services\"])");
    // At least one service containing invalid service id and valid service type
    assertExceptionThrown(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"serviceType\":\"other\"}}"
            + "]}",
        DatamartFacility.class,
        JsonMappingException.class,
        "Unrecognized field \"detailed_services\" (class gov.va.api.lighthouse.facilities.DatamartFacility), not marked as ignorable (3 known properties: \"type\", \"id\", \"attributes\"])\n"
            + " at [Source: (String)\"{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"}},{\"serviceInfo\":{\"serviceId\":\"foo\",\"serviceType\":\"other\"}}]}\"; line: 1, column: 23] (through reference chain: gov.va.api.lighthouse.facilities.DatamartFacility[\"detailed_services\"])");

    attributes =
        DatamartFacility.FacilityAttributes.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(
                                    DatamartFacility.OtherService.OnlineScheduling.serviceId())
                                .name(DatamartFacility.OtherService.OnlineScheduling.name())
                                .serviceType(TypeOfService.Other)
                                .build())
                        .phoneNumbers(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    // At least one service containing invalid service id and/or invalid service type
    assertExceptionThrown(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"},\"appointment_phones\":[]}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[]},{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"},\"appointment_phones\":[]}]}\"; line: 1, column: 215] (through reference chain: gov.va.api.lighthouse.facilities.DatamartFacility$FacilityAttributes$FacilityAttributesBuilder[\"detailed_services\"]->java.util.ArrayList[1]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");

    attributes =
        DatamartFacility.FacilityAttributes.builder()
            .detailedServices(
                List.of(
                    DatamartDetailedService.builder()
                        .serviceInfo(
                            DatamartDetailedService.ServiceInfo.builder()
                                .serviceId(
                                    DatamartFacility.OtherService.OnlineScheduling.serviceId())
                                .name(DatamartFacility.OtherService.OnlineScheduling.name())
                                .serviceType(TypeOfService.Other)
                                .build())
                        .phoneNumbers(emptyList())
                        .serviceLocations(emptyList())
                        .build()))
            .build();
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    assertJson(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        attributes);
    // At least one service containing invalid service id and/or invalid service type
    assertExceptionThrown(
        "{\"detailed_services\":["
            + "{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]},"
            + "{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}"
            + "]}",
        DatamartFacility.FacilityAttributes.class,
        JsonMappingException.class,
        "Cannot construct instance of `gov.va.api.lighthouse.facilities.api.TypeOfService`, problem: Unrecognized service type: bar\n"
            + " at [Source: (String)\"{\"detailed_services\":[{\"serviceInfo\":{\"serviceId\":\"onlineScheduling\",\"name\":\"OnlineScheduling\",\"serviceType\":\"other\"},\"appointment_phones\":[],\"service_locations\":[]},{\"serviceInfo\":{\"serviceId\":\"foo\",\"name\":\"NoSuchService\",\"serviceType\":\"bar\"},\"appointment_phones\":[],\"service_locations\":[]}]}\"; line: 1, column: 238] (through reference chain: gov.va.api.lighthouse.facilities.DatamartFacility$FacilityAttributes$FacilityAttributesBuilder[\"detailed_services\"]->java.util.ArrayList[1]->gov.va.api.lighthouse.facilities.DatamartDetailedService[\"serviceInfo\"]->gov.va.api.lighthouse.facilities.DatamartDetailedService$ServiceInfo$ServiceInfoBuilder[\"serviceType\"])");
  }
}
