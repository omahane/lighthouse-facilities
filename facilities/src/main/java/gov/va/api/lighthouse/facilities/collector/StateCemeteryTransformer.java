package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.lighthouse.facilities.DatamartFacility.FacilityType.va_cemetery;
import static gov.va.api.lighthouse.facilities.DatamartFacility.Type.va_facilities;
import static gov.va.api.lighthouse.facilities.collector.Transformers.allBlank;
import static gov.va.api.lighthouse.facilities.collector.Transformers.checkAngleBracketNull;
import static gov.va.api.lighthouse.facilities.collector.Transformers.phoneTrim;
import static org.apache.commons.lang3.StringUtils.indexOf;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.upperCase;

import gov.va.api.lighthouse.facilities.DatamartFacility;
import gov.va.api.lighthouse.facilities.DatamartFacility.Address;
import gov.va.api.lighthouse.facilities.DatamartFacility.Addresses;
import gov.va.api.lighthouse.facilities.DatamartFacility.FacilityAttributes;
import gov.va.api.lighthouse.facilities.DatamartFacility.Hours;
import gov.va.api.lighthouse.facilities.DatamartFacility.Phone;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.NonNull;

@Builder
final class StateCemeteryTransformer {
  private static final Pattern ZIP_PATTERN =
      Pattern.compile(".*(\\d{5}-\\d{4}$)|.*(\\d{9}$)|.*(\\d{5}$)");

  private final StateCemeteries.@NonNull StateCemetery xml;

  @NonNull private final Map<String, String> websites;

  static Address asAddress(String state, String line1, String line2, String line3) {
    // Zip and city must be parsed out of the last nonblank line
    if (isNotBlank(line3)) {
      if (allBlank(parseZip(line3), parseCity(line3), line1, line2)) {
        return null;
      }
      return Address.builder()
          .zip(parseZip(line3))
          .city(parseCity(line3))
          .state(state)
          .address1(line1)
          .address2(line2)
          .build();
    }
    if (isNotBlank(line2)) {
      if (allBlank(parseZip(line2), parseCity(line2), line1)) {
        return null;
      }
      return Address.builder()
          .zip(parseZip(line2))
          .city(parseCity(line2))
          .state(state)
          .address1(line1)
          .build();
    }
    if (isNotBlank(line1)) {
      if (allBlank(parseZip(line1), parseCity(line1))) {
        return Address.builder().address1(line1).build();
      }
      return Address.builder().zip(parseZip(line1)).city(parseCity(line1)).state(state).build();
    }
    return null;
  }

  private static String parseCity(String addressLine) {
    int comma = indexOf(addressLine, ",");
    if (comma <= -1) {
      return null;
    }
    return addressLine.substring(0, comma);
  }

  private static String parseZip(String addressLine) {
    Matcher matcher = ZIP_PATTERN.matcher(trimToEmpty(addressLine));
    if (!matcher.find()) {
      return null;
    }
    return Stream.of(matcher.group(1), matcher.group(2), matcher.group(3))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }

  private Addresses address() {
    if (allBlank(mailing(), physical())) {
      return null;
    }
    return Addresses.builder().mailing(mailing()).physical(physical()).build();
  }

  private FacilityAttributes attributes() {
    if (allBlank(xml.name(), website(), latitude(), longitude(), address(), phone())) {
      return null;
    }
    return FacilityAttributes.builder()
        .name(xml.name())
        .facilityType(va_cemetery)
        .classification("State Cemetery")
        .website(website())
        .latitude(latitude())
        .longitude(longitude())
        .timeZone(TimeZoneFinder.calculateTimeZonesWithMap(latitude(), longitude(), id()))
        .address(address())
        .phone(phone())
        .hours(defaultHours())
        .build();
  }

  private Hours defaultHours() {
    String hours = "Sunrise - Sunset";
    return Hours.builder()
        .monday(hours)
        .tuesday(hours)
        .wednesday(hours)
        .thursday(hours)
        .friday(hours)
        .saturday(hours)
        .sunday(hours)
        .build();
  }

  private String id() {
    if (allBlank(xml.id())) {
      return null;
    }
    return "nca_s" + xml.id();
  }

  private BigDecimal latitude() {
    if (allBlank(xml.latitude())) {
      return null;
    }
    return new BigDecimal(xml.latitude());
  }

  private BigDecimal longitude() {
    if (allBlank(xml.longitude())) {
      return null;
    }
    return new BigDecimal(xml.longitude());
  }

  private Address mailing() {
    return asAddress(
        upperCase(xml.stateCode(), Locale.US),
        checkAngleBracketNull(xml.mailingLine1()),
        checkAngleBracketNull(xml.mailingLine2()),
        xml.mailingLine3());
  }

  private Phone phone() {
    String fax = phoneTrim(xml.fax());
    String main = phoneTrim(xml.phone());
    if (allBlank(fax, main)) {
      return null;
    }
    return Phone.builder().fax(fax).main(main).build();
  }

  private Address physical() {
    return asAddress(
        upperCase(xml.stateCode(), Locale.US),
        checkAngleBracketNull(xml.addressLine1()),
        checkAngleBracketNull(xml.addressLine2()),
        xml.addressLine3());
  }

  DatamartFacility toDatamartFacility() {
    if (allBlank(id())) {
      return null;
    }
    return DatamartFacility.builder().id(id()).type(va_facilities).attributes(attributes()).build();
  }

  String website() {
    if (isNotBlank(xml.url())) {
      return xml.url();
    }
    if (allBlank(id())) {
      return null;
    }
    return websites.get(id());
  }
}
