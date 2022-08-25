package gov.va.api.lighthouse.facilities;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.FilterChain;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

class InternalClientKeyFilterConfigurationTest {
  @Test
  @SneakyThrows
  void match() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader("client-key")).thenReturn("topsecret");
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain chain = mock(FilterChain.class);

    InternalClientKeyFilterConfiguration.ClientKeyFilter.builder()
        .clientKey("topsecret")
        .build()
        .doFilterInternal(request, response, chain);

    verify(chain, times(1)).doFilter(request, response);
  }

  @Test
  @SneakyThrows
  void mismatch() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader("client-key")).thenReturn("wrong");
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(response.getOutputStream()).thenReturn(mock(ServletOutputStream.class));
    FilterChain chain = mock(FilterChain.class);

    InternalClientKeyFilterConfiguration.ClientKeyFilter.builder()
        .clientKey("topsecret")
        .build()
        .doFilterInternal(request, response, chain);

    verify(response, times(1)).setStatus(401);
    verify(response, times(1)).setHeader("Content-Type", "application/json");
  }

  @Test
  @SneakyThrows
  void noHeader() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(response.getOutputStream()).thenReturn(mock(ServletOutputStream.class));
    FilterChain chain = mock(FilterChain.class);

    InternalClientKeyFilterConfiguration.ClientKeyFilter.builder()
        .clientKey("topsecret")
        .build()
        .doFilterInternal(request, response, chain);

    verify(response, times(1)).setStatus(401);
    verify(response, times(1)).setHeader("Content-Type", "application/json");
  }
}
