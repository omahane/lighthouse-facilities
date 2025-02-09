package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.jupiter.api.Test;

public class CmsOverlayHelperTest {
  @Test
  void getDetailedServices() {
    String ds = "[;]";
    assertThatThrownBy(() -> CmsOverlayHelper.getDetailedServices(ds))
        .isInstanceOf(JsonMappingException.class);
    assertThat(CmsOverlayHelper.getDetailedServices(null)).isEmpty();
  }

  @Test
  void getHealthcareSystem() {
    String system =
        "{\"name\";\"VA Maine health care\",\"url\":\"https://www.va.gov/maine-health-care\",\"covid_url\":\"https://www.va.gov/maine-health-care/programs/covid-19-vaccines\"}}";
    assertThatThrownBy(() -> CmsOverlayHelper.getHealthCareSystem(system))
        .isInstanceOf(JsonParseException.class);
  }

  @Test
  void getOperatingStatus() {
    String os =
        "{\"code\";\"CLOSED\",\"additional_info\":\"Your VA health care team will contact you if you???re eligible to get a vaccine during this time. As the supply of vaccine increases\"}";
    assertThatThrownBy(() -> CmsOverlayHelper.getOperatingStatus(os))
        .isInstanceOf(JsonParseException.class);
  }
}
