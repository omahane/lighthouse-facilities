package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class DatamartJacksonSerializersTest {
  ObjectMapper mapper =
      JacksonConfig.createMapper().registerModule(DatamartJacksonSerializers.datamartSerializers());

  @Test
  @SneakyThrows
  void serializeAddress() {
    DatamartFacility df =
        DatamartFacility.builder()
            .id("nca_s1001")
            .attributes(
                DatamartFacility.FacilityAttributes.builder()
                    .address(
                        DatamartFacility.Addresses.builder()
                            .physical(
                                DatamartFacility.Address.builder()
                                    .address1("34904 State Highway 225")
                                    .address2(null)
                                    .address3(null)
                                    .city(null)
                                    .zip(null)
                                    .state(null)
                                    .build())
                            .build())
                    .build())
            .build();
    String output = mapper.writeValueAsString(df);
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rootNode = objectMapper.readTree(output);
    JsonNode address1Node =
        rootNode.path("attributes").path("address").path("physical").path("address_1");
    String address_1 = address1Node.asText();
    // Assert that address 1 is correctly serialized
    assertThat(address_1).isEqualTo("34904 State Highway 225");

    df =
        DatamartFacility.builder()
            .id("nca_1001")
            .attributes(
                DatamartFacility.FacilityAttributes.builder()
                    .address(
                        DatamartFacility.Addresses.builder()
                            .physical(
                                DatamartFacility.Address.builder()
                                    .address1(null)
                                    .address2(null)
                                    .address3(null)
                                    .city(null)
                                    .zip(null)
                                    .state(null)
                                    .build())
                            .build())
                    .build())
            .build();

    output = mapper.writeValueAsString(df);
    rootNode = objectMapper.readTree(output);
    JsonNode physicalNode = rootNode.path("attributes").path("address").path("physical");
    String physicalAddress = physicalNode.asText();
    assertThat(physicalAddress).isEmpty();
  }

  @Test
  @SneakyThrows
  void serializePhone() {
    DatamartFacility df =
        DatamartFacility.builder()
            .id("nca_1001")
            .attributes(
                DatamartFacility.FacilityAttributes.builder()
                    .phone(DatamartFacility.Phone.builder().main("321-213-123").build())
                    .build())
            .build();

    String output = mapper.writeValueAsString(df);
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rootNode = objectMapper.readTree(output);
    JsonNode faxNode = rootNode.path("attributes").path("fax");
    String fax = faxNode.asText();
    assertThat(fax).isEmpty();
  }

  @Test
  @SneakyThrows
  void serializeServices() {
    DatamartFacility df =
        DatamartFacility.builder()
            .id("vba_1001")
            .attributes(
                DatamartFacility.FacilityAttributes.builder()
                    .services(
                        DatamartFacility.Services.builder()
                            .benefits(
                                List.of(DatamartFacility.BenefitsService.TransitionAssistance))
                            .build())
                    .build())
            .build();

    String output = mapper.writeValueAsString(df);
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rootNode = objectMapper.readTree(output);
    JsonNode servicesNode = rootNode.path("attributes").path("services");
    String services = servicesNode.toString();
    assertThat(services).isEqualTo("{\"benefits\":[\"TransitionAssistance\"]}");
  }
}
