package gov.va.api.lighthouse.facilities;

import static java.util.stream.Collectors.joining;

import com.google.common.collect.Iterables;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.v0.ApiError;
import java.util.Arrays;
import java.util.List;
import javax.validation.ConstraintViolationException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public final class WebExceptionHandlerV0 {
  private static final String[] nearbyAddressRequestParams = {
    "street_address", "city", "state", "zip"
  };

  private static final String[] nearbyLatlngRequestParams = {"lat", "lng"};

  private static final String nearbyAddressMessageRegex =
      "(?:OR)? \"street_address, city, state, zip\" (?:OR)?";

  @SneakyThrows
  private static ResponseEntity<ApiError> response(
      HttpStatus status, Throwable tr, ApiError error) {
    log.error("Response {}", JacksonConfig.createMapper().writeValueAsString(error), tr);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return ResponseEntity.status(status).headers(headers).body(error);
  }

  private boolean areNearbyParameters(List<String[]> paramConditionGroups) {
    return paramConditionGroups != null
        && paramConditionGroups.size() >= 2
        && ((Arrays.equals(paramConditionGroups.get(0), nearbyAddressRequestParams)
                || Arrays.equals(paramConditionGroups.get(0), nearbyLatlngRequestParams))
            && (Arrays.equals(paramConditionGroups.get(1), nearbyAddressRequestParams)
                || Arrays.equals(paramConditionGroups.get(1), nearbyLatlngRequestParams)));
  }

  @ExceptionHandler(ExceptionsUtilsV0.BingException.class)
  ResponseEntity<ApiError> handleBing(ExceptionsUtilsV0.BingException ex) {
    ApiError response =
        ApiError.builder()
            .errors(
                List.of(
                    ApiError.ErrorMessage.builder()
                        .title("Bing error")
                        .detail(ex.getMessage())
                        .code("400")
                        .status("400")
                        .build()))
            .build();
    return response(HttpStatus.BAD_REQUEST, ex, response);
  }

  @ExceptionHandler(ExceptionsUtils.InvalidParameter.class)
  ResponseEntity<ApiError> handleInvalidParameter(ExceptionsUtils.InvalidParameter ex) {
    ApiError response =
        ApiError.builder()
            .errors(
                List.of(
                    ApiError.ErrorMessage.builder()
                        .title("Invalid field value")
                        .detail(ex.getMessage())
                        .code("103")
                        .status("400")
                        .build()))
            .build();
    return response(HttpStatus.BAD_REQUEST, ex, response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiError> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {
    ApiError response =
        ApiError.builder()
            .errors(
                List.of(
                    ApiError.ErrorMessage.builder()
                        .title("Method argument not valid")
                        .detail(ex.getMessage())
                        .code("400")
                        .status("400")
                        .build()))
            .build();
    return response(HttpStatus.BAD_REQUEST, ex, response);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  ResponseEntity<ApiError> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex) {
    ApiError response =
        ApiError.builder()
            .errors(
                List.of(
                    ApiError.ErrorMessage.builder()
                        .title("Invalid field value")
                        .detail(
                            String.format(
                                "'%s' is not a valid value for '%s'", ex.getValue(), ex.getName()))
                        .code("103")
                        .status("400")
                        .build()))
            .build();
    return response(HttpStatus.BAD_REQUEST, ex, response);
  }

  @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
  ResponseEntity<ApiError> handleNotAcceptable(HttpMediaTypeNotAcceptableException ex) {
    ApiError error =
        ApiError.builder()
            .errors(
                List.of(
                    ApiError.ErrorMessage.builder()
                        .title("Not acceptable")
                        .detail("The resource could not be returned in the requested format")
                        .code("406")
                        .status("406")
                        .build()))
            .build();
    return response(HttpStatus.NOT_ACCEPTABLE, ex, error);
  }

  @ExceptionHandler(ExceptionsUtils.NotFound.class)
  ResponseEntity<ApiError> handleNotFound(ExceptionsUtils.NotFound ex) {
    ApiError error =
        ApiError.builder()
            .errors(
                List.of(
                    ApiError.ErrorMessage.builder()
                        .title("Record not found")
                        .detail(ex.getMessage())
                        .code("404")
                        .status("404")
                        .build()))
            .build();
    return response(HttpStatus.NOT_FOUND, ex, error);
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<ApiError> handleSnafu(Exception ex) {
    ApiError response =
        ApiError.builder()
            .errors(
                List.of(
                    ApiError.ErrorMessage.builder()
                        .title("Internal server error")
                        .detail(ex.getMessage())
                        .code("500")
                        .status("500")
                        .build()))
            .build();
    return response(HttpStatus.INTERNAL_SERVER_ERROR, ex, response);
  }

  @ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
  ResponseEntity<ApiError> handleUnsatisfiedServletRequestParameter(
      UnsatisfiedServletRequestParameterException ex) {
    String message = ex.getMessage();
    if (message != null && areNearbyParameters(ex.getParamConditionGroups())) {
      message = message.replaceAll(nearbyAddressMessageRegex, "");
    }
    ApiError response =
        ApiError.builder()
            .errors(
                List.of(
                    ApiError.ErrorMessage.builder()
                        .title("Missing parameter")
                        .detail(message)
                        .code("108")
                        .status("400")
                        .build()))
            .build();
    return response(HttpStatus.BAD_REQUEST, ex, response);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  ResponseEntity<ApiError> handleValidationException(ConstraintViolationException ex) {
    String violations =
        ex.getConstraintViolations().stream()
            .map(v -> Iterables.getLast(v.getPropertyPath()) + " " + v.getMessage())
            .collect(joining(", "));
    ApiError response =
        ApiError.builder()
            .errors(
                List.of(
                    ApiError.ErrorMessage.builder()
                        .title("Invalid field value")
                        .detail(violations)
                        .code("400")
                        .status("400")
                        .build()))
            .build();
    return response(HttpStatus.BAD_REQUEST, ex, response);
  }
}
