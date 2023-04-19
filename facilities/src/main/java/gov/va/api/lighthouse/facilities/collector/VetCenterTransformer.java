package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.lighthouse.facilities.DatamartFacility.FacilityType.vet_center;
import static gov.va.api.lighthouse.facilities.DatamartFacility.Type.va_facilities;
import static gov.va.api.lighthouse.facilities.collector.Transformers.allBlank;
import static gov.va.api.lighthouse.facilities.collector.Transformers.checkAngleBracketNull;
import static gov.va.api.lighthouse.facilities.collector.Transformers.hoursToClosed;
import static gov.va.api.lighthouse.facilities.collector.Transformers.phoneTrim;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.upperCase;

import gov.va.api.lighthouse.facilities.DatamartFacility;
import gov.va.api.lighthouse.facilities.DatamartFacility.ActiveStatus;
import gov.va.api.lighthouse.facilities.DatamartFacility.Address;
import gov.va.api.lighthouse.facilities.DatamartFacility.Addresses;
import gov.va.api.lighthouse.facilities.DatamartFacility.FacilityAttributes;
import gov.va.api.lighthouse.facilities.DatamartFacility.Hours;
import gov.va.api.lighthouse.facilities.DatamartFacility.Phone;
import java.util.Locale;
import java.util.Map;
import lombok.Builder;
import lombok.NonNull;

@Builder
final class VetCenterTransformer {
  @NonNull private final VastEntity vast;

  @NonNull private final Map<String, String> websites;

  private ActiveStatus activeStatus() {
    if (allBlank(vast.pod())) {
      return null;
    }
    return vast.pod().equalsIgnoreCase("A") ? ActiveStatus.A : ActiveStatus.T;
  }

  private Addresses address() {
    if (allBlank(addressPhysical())) {
      return null;
    }
    return Addresses.builder().physical(addressPhysical()).build();
  }

  private Address addressPhysical() {
    // address1 is repeat of station name
    if (allBlank(
        addressZip(),
        vast.city(),
        vast.state(),
        checkAngleBracketNull(vast.address2()),
        checkAngleBracketNull(vast.address3()))) {
      return null;
    }
    return Address.builder()
        .zip(addressZip())
        .city(vast.city())
        .state(upperCase(vast.state(), Locale.US))
        .address1(checkAngleBracketNull(vast.address2()))
        .address2(checkAngleBracketNull(vast.address3()))
        .build();
  }

  private String addressZip() {
    String zip = vast.zip();
    String zipPlus4 = vast.zip4();
    if (isNotBlank(zip) && isNotBlank(zipPlus4) && !zipPlus4.matches("^[0]+$")) {
      return zip + "-" + zipPlus4;
    }
    return zip;
  }

  private FacilityAttributes attributes() {
    if (allBlank(
        vast.stationName(),
        website(),
        vast.latitude(),
        vast.longitude(),
        address(),
        phone(),
        hours(),
        vast.operationalHoursSpecialInstructions(),
        vast.mobile(),
        activeStatus(),
        vast.visn(),
        vast.parentStationNumber())) {
      return null;
    }
    return FacilityAttributes.builder()
        .name(vast.stationName())
        .facilityType(vet_center)
        .website(website())
        .latitude(vast.latitude())
        .longitude(vast.longitude())
        .timeZone(TimeZoneFinder.calculateTimeZonesWithMap(vast.latitude(), vast.longitude(), id()))
        .address(address())
        .phone(phone())
        .hours(hours())
        .operationalHoursSpecialInstructions(vast.operationalHoursSpecialInstructions())
        .mobile(vast.mobile())
        .activeStatus(activeStatus())
        .visn(vast.visn())
        .parentId(parentId())
        .build();
  }

  private Hours hours() {
    String mon = hoursToClosed(vast.monday());
    String tue = hoursToClosed(vast.tuesday());
    String wed = hoursToClosed(vast.wednesday());
    String thu = hoursToClosed(vast.thursday());
    String fri = hoursToClosed(vast.friday());
    String sat = hoursToClosed(vast.saturday());
    String sun = hoursToClosed(vast.sunday());
    if (allBlank(mon, tue, wed, thu, fri, sat, sun)) {
      return null;
    }
    return Hours.builder()
        .monday(mon)
        .tuesday(tue)
        .wednesday(wed)
        .thursday(thu)
        .friday(fri)
        .saturday(sat)
        .sunday(sun)
        .build();
  }

  private String id() {
    if (allBlank(vast.stationNumber())) {
      return null;
    }
    return "vc_" + vast.stationNumber();
  }

  private String parentId() {
    return allBlank(vast.parentStationNumber()) ? null : "vc_" + vast.parentStationNumber();
  }

  private Phone phone() {
    String fax = phoneTrim(vast.staFax());
    String main = phoneTrim(vast.staPhone());
    if (allBlank(fax, main)) {
      return null;
    }
    return Phone.builder().fax(fax).main(main).build();
  }

  DatamartFacility toDatamartFacility() {
    if (allBlank(id())) {
      return null;
    }
    return DatamartFacility.builder().id(id()).type(va_facilities).attributes(attributes()).build();
  }

  String website() {
    return allBlank(id()) ? null : websites.get(id());
  }
}
