package gov.va.api.lighthouse.facilities.collector;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
final class AtcAllData {

  @JsonProperty("Data")
  List<AtcFacility> data;

  @JsonIgnoreProperties(ignoreUnknown = true)
  static final class AtcAllDataBuilder {}

  @Data
  @Builder
  @JsonIgnoreProperties(ignoreUnknown = true)
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
  public static final class AtcFacility {
    @JsonProperty("facilityID")
    String facilityId;

    @JsonProperty("ED")
    Boolean emergencyCare;

    @JsonProperty("UC")
    Boolean urgentCare;

    @JsonProperty("PwtData")
    List<AtcPwtData> pwtData;

    @JsonIgnoreProperties(ignoreUnknown = true)
    static final class AtcFacilityBuilder {}
  }

  @Data
  @Builder
  @JsonIgnoreProperties(ignoreUnknown = true)
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonInclude(value = Include.NON_NULL, content = Include.NON_NULL)
  public static final class AtcPwtData {

    @JsonProperty("ClinicType")
    String clinicType;

    BigDecimal estWaitTime;

    BigDecimal newWaitTime;

    String reportDate;

    @JsonIgnoreProperties(ignoreUnknown = true)
    static final class AtcPwtDataBuilder {}
  }
}
