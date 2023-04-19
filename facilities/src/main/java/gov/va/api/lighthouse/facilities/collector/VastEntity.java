package gov.va.api.lighthouse.facilities.collector;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.BooleanUtils;

@Value
@Builder
final class VastEntity {
  private Boolean vetCenter;

  private Boolean mobileVetCenter;

  private BigDecimal latitude;

  private BigDecimal longitude;

  private String stationNumber;

  private String stationName;

  private String abbreviation;

  private String cocClassificationId;

  private String address1;

  private String address2;

  private String address3;

  private String city;

  private String state;

  private String zip;

  private String zip4;

  private String monday;

  private String tuesday;

  private String wednesday;

  private String thursday;

  private String friday;

  private String saturday;

  private String sunday;

  private String operationalHoursSpecialInstructions;

  private String staPhone;

  private String staFax;

  private String afterHoursPhone;

  private String patientAdvocatePhone;

  private String enrollmentCoordinatorPhone;

  private String pharmacyPhone;

  private String pod;

  private Boolean mobile;

  private String visn;

  private Instant lastUpdated;

  private String parentStationNumber;

  boolean isVetCenter() {
    return BooleanUtils.isTrue(vetCenter()) || BooleanUtils.isTrue(mobileVetCenter());
  }

  // Unused fields:
  // ExtractDate
  // GeoExtractDate
  // D_VISN
  // StationNo
  // CategoryID
  // CD
  // CNAME
  // CONT
  // District_VISN
  // EXTRDATE
  // FIPS
  // LOC_NAME
  // MARKET
  // NewVISN
  // OldVISN
  // OPERA_DATE
  // PAR_STA_NO
  // ParentStationCL
  // PROV
  // SCORE
  // SECTOR
  // SHAR
  // STA_NAME_Official
  // STATE_FIPS
  // SUBMARKET
  // SUSPENDED
  // UNIQUESTAT
  // URH
  // VA_SITE
  // CLC
  // District
  // DistrictName
  // DRRTP
  // HCC
  // MCS
  // MSCBOC
  // NewVAMC
  // OOS
  // OutPatientRating
  // PCCBOC
  // S_CLASS_NAME
  // VAH
  // SH_PAR_STA
}
