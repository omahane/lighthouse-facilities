package gov.va.api.lighthouse.facilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import java.io.InputStream;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatamartFacilitiesJacksonConfig {
  public static ObjectMapper createMapper() {
    return new DatamartFacilitiesJacksonConfig().datamartMapper();
  }

  /** Mask away checked exception so this Jackson can be used in streams. */
  @SneakyThrows
  static <T> T quietlyMap(ObjectMapper mapper, InputStream json, Class<T> type) {
    return mapper.readValue(json, type);
  }

  @Bean
  public ObjectMapper datamartMapper() {
    return JacksonConfig.createMapper()
        .registerModule(DatamartJacksonSerializers.datamartSerializers());
  }
}
