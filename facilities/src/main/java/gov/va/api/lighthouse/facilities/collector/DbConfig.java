package gov.va.api.lighthouse.facilities.collector;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
class DbConfig {
  @Bean
  JdbcTemplate jdbcTemplate(
      @Value("${spring.fc-datasource.driver-class-name}") String driverClassName,
      @Value("${spring.fc-datasource.password}") String password,
      @Value("${spring.fc-datasource.url}") String url,
      @Value("${spring.fc-datasource.username}") String username,
      @Value("${spring.fc-datasource.minIdle}") int minIdle,
      @Value("${spring.fc-datasource.maxPoolSize}") int maxPoolSize,
      @Value("${spring.fc-datasource.idleTimeout}") int idleTimeout) {
    HikariConfig config = new HikariConfig();
    config.setDriverClassName(driverClassName);
    config.setPassword(password);
    config.setJdbcUrl(url);
    config.setUsername(username);
    config.setMinimumIdle(minIdle);
    config.setMaximumPoolSize(maxPoolSize);
    config.setIdleTimeout(idleTimeout);
    HikariDataSource ds = new HikariDataSource(config);
    return new JdbcTemplate(ds);
  }
}
