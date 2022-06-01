package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.FacilitiesJacksonConfigV0.createMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import java.io.InputStream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class FacilitiesJacksonConfigV0Test {
  private static final ObjectMapper MAPPER = createMapper();

  @Test
  @SneakyThrows
  void quietlyMapInputStream() {
    String path = "/v0/all.json";
    GeoFacilitiesResponse actual =
        MAPPER.readValue(
            getClass().getResourceAsStream("/v0/all.json"), GeoFacilitiesResponse.class);
    InputStream is = getClass().getResourceAsStream(path);
    GeoFacilitiesResponse expected =
        FacilitiesJacksonConfigV0.quietlyMap(MAPPER, is, GeoFacilitiesResponse.class);
    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  void quietlyMapInputStreamException() {
    FacilitiesJacksonConfigV0 f = mock(FacilitiesJacksonConfigV0.class);
    ObjectMapper om = mock(ObjectMapper.class);
    InputStream json = mock(InputStream.class);
    when(f.quietlyMap(om, json, GeoFacilitiesResponse.class)).thenThrow(JsonMappingException.class);
    assertThatThrownBy(() -> f.quietlyMap(om, json, GeoFacilitiesResponse.class))
        .isInstanceOf(JsonMappingException.class);
  }

  @Test
  @SneakyThrows
  void quietlyMapString() {
    String path = "/v0/all.json";
    GeoFacilitiesResponse actual =
        MAPPER.readValue(getClass().getResourceAsStream(path), GeoFacilitiesResponse.class);
    String geoFacilitiesResponse =
        FacilitiesJacksonConfigV0.quietlyWriteValueAsString(MAPPER, actual);
    GeoFacilitiesResponse expected =
        FacilitiesJacksonConfigV0.quietlyMap(
            MAPPER, geoFacilitiesResponse, GeoFacilitiesResponse.class);
    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    String os =
        "{\"code\";\"CLOSED\",\"additional_info\":\"Your VA health care team will contact you if you’re eligible to get a vaccine during this time. As the supply of vaccine increases\"}";
    assertThatThrownBy(
            () -> FacilitiesJacksonConfigV0.quietlyMap(MAPPER, os, Facility.OperatingStatus.class))
        .isInstanceOf(JsonParseException.class);
  }

  @Test
  @SneakyThrows
  void quietlyWriteValueAsString() {
    String path = "/v0/all.json";
    GeoFacilitiesResponse response =
        MAPPER.readValue(getClass().getResourceAsStream(path), GeoFacilitiesResponse.class);
    String actual = FacilitiesJacksonConfigV0.quietlyWriteValueAsString(MAPPER, response);
    String expected =
        MAPPER.writeValueAsString(MAPPER.readTree(getClass().getResourceAsStream(path)));
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  void quietlyWriteValueAsStringException() {
    FacilitiesJacksonConfigV0 f = mock(FacilitiesJacksonConfigV0.class);
    ObjectMapper om = mock(ObjectMapper.class);
    Object ob = mock(Object.class);
    when(f.quietlyWriteValueAsString(om, ob)).thenThrow(JsonMappingException.class);
    assertThatThrownBy(() -> f.quietlyWriteValueAsString(om, ob))
        .isInstanceOf(JsonMappingException.class);
  }
}
