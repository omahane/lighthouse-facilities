package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.FacilitiesJacksonConfigV1.createMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v1.FacilitiesResponse;
import java.io.InputStream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class FacilitiesJacksonConfigV1Test {
  private static final ObjectMapper MAPPER = createMapper();

  @Test
  @SneakyThrows
  void quietlyMapInputStream() {
    String path = "/v1/all.json";
    FacilitiesResponse actual =
        MAPPER.readValue(getClass().getResourceAsStream(path), FacilitiesResponse.class);
    InputStream is = getClass().getResourceAsStream(path);
    FacilitiesResponse expected =
        FacilitiesJacksonConfigV1.quietlyMap(MAPPER, is, FacilitiesResponse.class);
    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  void quietlyMapInputStreamException() {
    FacilitiesJacksonConfigV1 f = mock(FacilitiesJacksonConfigV1.class);
    ObjectMapper om = mock(ObjectMapper.class);
    InputStream json = mock(InputStream.class);
    when(f.quietlyMap(om, json, GeoFacilitiesResponse.class)).thenThrow(JsonMappingException.class);
    assertThatThrownBy(() -> f.quietlyMap(om, json, GeoFacilitiesResponse.class))
        .isInstanceOf(JsonMappingException.class);
  }

  @Test
  @SneakyThrows
  void quietlyMapString() {
    String path = "/v1/all.json";
    FacilitiesResponse actual =
        MAPPER.readValue(getClass().getResourceAsStream(path), FacilitiesResponse.class);
    String facilityResponse = FacilitiesJacksonConfigV1.quietlyWriteValueAsString(MAPPER, actual);
    FacilitiesResponse expected =
        FacilitiesJacksonConfigV1.quietlyMap(MAPPER, facilityResponse, FacilitiesResponse.class);
    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    String os =
        "{\"code\";\"CLOSED\",\"additional_info\":\"Your VA health care team will contact you if you’re eligible to get a vaccine during this time. As the supply of vaccine increases\"}";
    assertThatThrownBy(
            () -> FacilitiesJacksonConfigV1.quietlyMap(MAPPER, os, Facility.OperatingStatus.class))
        .isInstanceOf(JsonParseException.class);
  }

  @Test
  @SneakyThrows
  void quietlyWriteValueAsString() {
    String path = "/v1/all.json";
    FacilitiesResponse response =
        MAPPER.readValue(getClass().getResourceAsStream(path), FacilitiesResponse.class);
    String actual = FacilitiesJacksonConfigV1.quietlyWriteValueAsString(MAPPER, response);
    String expected =
        MAPPER.writeValueAsString(MAPPER.readTree(getClass().getResourceAsStream(path)));
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  void quietlyWriteValueAsStringException() {
    FacilitiesJacksonConfigV1 f = mock(FacilitiesJacksonConfigV1.class);
    ObjectMapper om = mock(ObjectMapper.class);
    Object ob = mock(Object.class);
    when(f.quietlyWriteValueAsString(om, ob)).thenThrow(JsonMappingException.class);
    assertThatThrownBy(() -> f.quietlyWriteValueAsString(om, ob))
        .isInstanceOf(JsonMappingException.class);
  }
}
