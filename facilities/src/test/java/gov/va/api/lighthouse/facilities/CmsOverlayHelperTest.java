package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.util.ArrayList;
import java.util.List;
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
  void getOperatingStatus() {
    String os =
        "{\"code\";\"CLOSED\",\"additional_info\":\"Your VA health care team will contact you if you???re eligible to get a vaccine during this time. As the supply of vaccine increases\"}";
    assertThatThrownBy(() -> CmsOverlayHelper.getOperatingStatus(os))
        .isInstanceOf(JsonParseException.class);
  }

  @Test
  void serializeDetailedServices() {
    assertThat(CmsOverlayHelper.serializeDetailedServices(null)).isNull();
    assertThat(CmsOverlayHelper.serializeDetailedServices(List.of())).isNull();
    List<DatamartDetailedService> dds = new ArrayList<>();
    DatamartDetailedService detailedService = mock(DatamartDetailedService.class);
    dds.add(detailedService);
    assertThatThrownBy(() -> CmsOverlayHelper.serializeDetailedServices(dds))
        .isInstanceOf(JsonMappingException.class);
  }
}
