package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.DatamartFacility.Address;
import static gov.va.api.lighthouse.facilities.DatamartFacility.Addresses;
import static gov.va.api.lighthouse.facilities.DatamartFacility.FacilityAttributes;
import static gov.va.api.lighthouse.facilities.DatamartFacility.Hours;
import static gov.va.api.lighthouse.facilities.DatamartFacility.PatientSatisfaction;
import static gov.va.api.lighthouse.facilities.DatamartFacility.PatientWaitTime;
import static gov.va.api.lighthouse.facilities.DatamartFacility.Phone;
import static gov.va.api.lighthouse.facilities.DatamartFacility.Satisfaction;
import static gov.va.api.lighthouse.facilities.DatamartFacility.Services;
import static gov.va.api.lighthouse.facilities.DatamartFacility.WaitTimes;
import static gov.va.api.lighthouse.facilities.FacilityUtils.writeNonNull;
import static java.util.Collections.emptyList;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
final class DatamartJacksonSerializers {
  /** Custom serialization rules for datamart classes. */
  static SimpleModule datamartSerializers() {
    SimpleModule mod = new SimpleModule();
    mod.addSerializer(Address.class, new AddressSerializer());
    mod.addSerializer(Addresses.class, new AddressesSerializer());
    mod.addSerializer(FacilityAttributes.class, new FacilityAttributesSerializer());
    mod.addSerializer(Hours.class, new HoursSerializer());
    mod.addSerializer(PatientWaitTime.class, new PatientWaitTimeSerializer());
    mod.addSerializer(Phone.class, new PhoneSerializer());
    mod.addSerializer(Satisfaction.class, new SatisfactionSerializer());
    mod.addSerializer(Services.class, new ServicesSerializer());
    mod.addSerializer(WaitTimes.class, new WaitTimesSerializer());
    return mod;
  }

  private static boolean hasParent(JsonGenerator jgen, Class<?> clazz) {
    return parents(jgen).stream().anyMatch(p -> clazz.isInstance(p));
  }

  private static Optional<String> id(Object obj) {
    if (obj instanceof DatamartFacility) {
      return Optional.ofNullable(((DatamartFacility) obj).id());
    }
    return Optional.empty();
  }

  private static boolean idStartsWith(JsonGenerator jgen, String prefix) {
    return parents(jgen).stream()
        .map(p -> id(p))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .anyMatch(id -> StringUtils.startsWithIgnoreCase(id, prefix));
  }

  private static List<Object> parents(JsonGenerator jgen) {
    List<Object> parents = new ArrayList<>();
    JsonStreamContext parent = jgen.getOutputContext().getParent();
    while (parent != null) {
      if (parent.getCurrentValue() != null) {
        parents.add(parent.getCurrentValue());
      }
      parent = parent.getParent();
    }
    return parents;
  }

  private static final class AddressSerializer extends StdSerializer<Address> {
    public AddressSerializer() {
      this(null);
    }

    public AddressSerializer(Class<Address> t) {
      super(t);
    }

    private static boolean empty(Address value) {
      return value.zip() == null
          && value.city() == null
          && value.state() == null
          && value.address1() == null
          && value.address2() == null
          && value.address3() == null;
    }

    private static boolean emptyExcludeAddr1(Address value) {
      return value.zip() == null
          && value.city() == null
          && value.state() == null
          && value.address2() == null
          && value.address3() == null;
    }

    @SneakyThrows
    private static void writeNonNullOrNonStateCem(
        JsonGenerator jgen, String fieldName, String value) {
      if (value != null || !idStartsWith(jgen, "nca_s")) {
        jgen.writeStringField(fieldName, value);
      }
    }

    @Override
    @SneakyThrows
    public void serialize(Address value, JsonGenerator jgen, SerializerProvider provider) {
      jgen.writeStartObject();
      if (!empty(value)) {
        if (emptyExcludeAddr1(value) && idStartsWith(jgen, "nca_s")) {
          jgen.writeStringField("address_1", value.address1());
        } else {
          jgen.writeStringField("zip", value.zip());
          writeNonNullOrNonStateCem(jgen, "city", value.city());
          writeNonNullOrNonStateCem(jgen, "state", value.state());
          writeNonNullOrNonStateCem(jgen, "address_1", value.address1());
          writeNonNullOrNonStateCem(jgen, "address_2", value.address2());
          writeNonNullOrNonStateCem(jgen, "address_3", value.address3());
        }
      } else if (idStartsWith(jgen, "nca_") && !idStartsWith(jgen, "nca_s")) {
        jgen.writeStringField("zip", null);
        jgen.writeStringField("city", null);
        jgen.writeStringField("state", null);
        jgen.writeStringField("address_1", null);
        jgen.writeStringField("address_2", null);
        jgen.writeStringField("address_3", null);
      }
      jgen.writeEndObject();
    }
  }

  private static final class AddressesSerializer extends StdSerializer<Addresses> {
    public AddressesSerializer() {
      this(null);
    }

    public AddressesSerializer(Class<Addresses> t) {
      super(t);
    }

    private static boolean empty(Addresses value) {
      return value.mailing() == null && value.physical() == null;
    }

    @Override
    @SneakyThrows
    public void serialize(Addresses value, JsonGenerator jgen, SerializerProvider provider) {
      jgen.writeStartObject();
      if (!empty(value)) {
        jgen.writeObjectField(
            "mailing", Optional.ofNullable(value.mailing()).orElse(Address.builder().build()));
        jgen.writeObjectField(
            "physical", Optional.ofNullable(value.physical()).orElse(Address.builder().build()));
      }
      jgen.writeEndObject();
    }
  }

  private static final class FacilityAttributesSerializer
      extends StdSerializer<FacilityAttributes> {
    public FacilityAttributesSerializer() {
      this(null);
    }

    public FacilityAttributesSerializer(Class<FacilityAttributes> t) {
      super(t);
    }

    @Override
    @SneakyThrows
    public void serialize(
        FacilityAttributes value, JsonGenerator jgen, SerializerProvider provider) {
      jgen.writeStartObject();
      jgen.writeObjectField("name", value.name());
      jgen.writeObjectField("facility_type", value.facilityType());
      jgen.writeObjectField("classification", value.classification());
      jgen.writeObjectField("parent_id", value.parentId());
      jgen.writeObjectField("website", value.website());
      jgen.writeObjectField("lat", value.latitude());
      jgen.writeObjectField("long", value.longitude());
      jgen.writeObjectField("time_zone", value.timeZone());
      jgen.writeObjectField(
          "address", Optional.ofNullable(value.address()).orElse(Addresses.builder().build()));
      jgen.writeObjectField(
          "phone", Optional.ofNullable(value.phone()).orElse(Phone.builder().build()));
      jgen.writeObjectField(
          "hours", Optional.ofNullable(value.hours()).orElse(Hours.builder().build()));
      jgen.writeObjectField(
          "operational_hours_special_instructions", value.operationalHoursSpecialInstructions());
      jgen.writeObjectField(
          "services", Optional.ofNullable(value.services()).orElse(Services.builder().build()));
      jgen.writeObjectField(
          "satisfaction",
          Optional.ofNullable(value.satisfaction()).orElse(Satisfaction.builder().build()));
      jgen.writeObjectField(
          "wait_times", Optional.ofNullable(value.waitTimes()).orElse(WaitTimes.builder().build()));
      jgen.writeObjectField("mobile", value.mobile());
      jgen.writeObjectField("active_status", value.activeStatus());
      jgen.writeObjectField("operating_status", value.operatingStatus());
      jgen.writeObjectField("detailed_services", value.detailedServices());
      jgen.writeObjectField("visn", value.visn());
      jgen.writeEndObject();
    }
  }

  private static final class HoursSerializer extends StdSerializer<Hours> {
    public HoursSerializer() {
      this(null);
    }

    public HoursSerializer(Class<Hours> t) {
      super(t);
    }

    private static boolean empty(Hours value) {
      return value.monday() == null
          && value.tuesday() == null
          && value.wednesday() == null
          && value.thursday() == null
          && value.friday() == null
          && value.saturday() == null
          && value.sunday() == null;
    }

    private static void writeLowerByLength(Hours value, JsonGenerator jgen) {
      writeNonNull(jgen, "monday", value.monday());
      writeNonNull(jgen, "tuesday", value.tuesday());
      writeNonNull(jgen, "wednesday", value.wednesday());
      writeNonNull(jgen, "thursday", value.thursday());
      writeNonNull(jgen, "friday", value.friday());
      writeNonNull(jgen, "saturday", value.saturday());
      writeNonNull(jgen, "sunday", value.sunday());
    }

    @Override
    @SneakyThrows
    public void serialize(Hours value, JsonGenerator jgen, SerializerProvider provider) {
      jgen.writeStartObject();
      if (!empty(value)) {
        if (hasParent(jgen, DatamartFacility.class)) {
          writeLowerByLength(value, jgen);
        }
      } else if (idStartsWith(jgen, "nca_")) {
        jgen.writeObjectField("Monday", null);
        jgen.writeObjectField("Tuesday", null);
        jgen.writeObjectField("Wednesday", null);
        jgen.writeObjectField("Thursday", null);
        jgen.writeObjectField("Friday", null);
        jgen.writeObjectField("Saturday", null);
        jgen.writeObjectField("Sunday", null);
      }
      jgen.writeEndObject();
    }
  }

  private static final class PatientWaitTimeSerializer extends StdSerializer<PatientWaitTime> {
    public PatientWaitTimeSerializer() {
      this(null);
    }

    public PatientWaitTimeSerializer(Class<PatientWaitTime> t) {
      super(t);
    }

    @Override
    @SneakyThrows
    public void serialize(PatientWaitTime value, JsonGenerator jgen, SerializerProvider provider) {
      jgen.writeStartObject();
      jgen.writeObjectField("service", value.service());
      jgen.writeObjectField("new", value.newPatientWaitTime());
      jgen.writeObjectField("established", value.establishedPatientWaitTime());
      jgen.writeEndObject();
    }
  }

  private static final class PhoneSerializer extends StdSerializer<Phone> {
    public PhoneSerializer() {
      this(null);
    }

    public PhoneSerializer(Class<Phone> t) {
      super(t);
    }

    private static boolean empty(Phone value) {
      return value.fax() == null
          && value.main() == null
          && value.pharmacy() == null
          && value.afterHours() == null
          && value.patientAdvocate() == null
          && value.mentalHealthClinic() == null
          && value.enrollmentCoordinator() == null
          && value.healthConnect() == null;
    }

    @SneakyThrows
    private static void writeNonNullOrBenOrHealthOrStateCem(
        JsonGenerator jgen, String fieldName, String value) {
      if (value != null
          || idStartsWith(jgen, "vba_")
          || idStartsWith(jgen, "vha_")
          || idStartsWith(jgen, "nca_s")) {
        jgen.writeObjectField(fieldName, value);
      }
    }

    @SneakyThrows
    private static void writeNonNullOrHealth(JsonGenerator jgen, String fieldName, String value) {
      if (value != null || idStartsWith(jgen, "vha_")) {
        jgen.writeObjectField(fieldName, value);
      }
    }

    @Override
    @SneakyThrows
    public void serialize(Phone value, JsonGenerator jgen, SerializerProvider provider) {
      jgen.writeStartObject();
      if (!empty(value)) {
        writeNonNullOrBenOrHealthOrStateCem(jgen, "fax", value.fax());
        jgen.writeObjectField("main", value.main());
        writeNonNullOrHealth(jgen, "pharmacy", value.pharmacy());
        writeNonNullOrHealth(jgen, "after_hours", value.afterHours());
        writeNonNullOrHealth(jgen, "patient_advocate", value.patientAdvocate());
        writeNonNull(jgen, "mental_health_clinic", value.mentalHealthClinic());
        writeNonNullOrHealth(jgen, "enrollment_coordinator", value.enrollmentCoordinator());
        writeNonNullOrHealth(jgen, "health_connect", value.healthConnect());
      } else {
        if (idStartsWith(jgen, "nca_")) {
          jgen.writeObjectField("fax", null);
        }
        jgen.writeObjectField("main", null);
      }
      jgen.writeEndObject();
    }
  }

  private static final class SatisfactionSerializer extends StdSerializer<Satisfaction> {
    public SatisfactionSerializer() {
      this(null);
    }

    public SatisfactionSerializer(Class<Satisfaction> t) {
      super(t);
    }

    private static boolean empty(Satisfaction value) {
      return value.health() == null && value.effectiveDate() == null;
    }

    @Override
    @SneakyThrows
    public void serialize(Satisfaction value, JsonGenerator jgen, SerializerProvider provider) {
      jgen.writeStartObject();
      if (!empty(value) || idStartsWith(jgen, "vha_")) {
        jgen.writeObjectField(
            "health",
            Optional.ofNullable(value.health()).orElse(PatientSatisfaction.builder().build()));
        jgen.writeObjectField("effective_date", value.effectiveDate());
      }
      jgen.writeEndObject();
    }
  }

  private static final class ServicesSerializer extends StdSerializer<Services> {
    public ServicesSerializer() {
      this(null);
    }

    public ServicesSerializer(Class<Services> t) {
      super(t);
    }

    @Override
    @SneakyThrows
    public void serialize(Services value, JsonGenerator jgen, SerializerProvider provider) {
      jgen.writeStartObject();
      if (idStartsWith(jgen, "vha_")) {
        jgen.writeObjectField("other", Optional.ofNullable(value.other()).orElse(emptyList()));
      } else {
        writeNonNull(jgen, "other", value.other());
      }
      if (idStartsWith(jgen, "vha_")) {
        jgen.writeObjectField("health", Optional.ofNullable(value.health()).orElse(emptyList()));
      } else {
        writeNonNull(jgen, "health", value.health());
      }
      if (idStartsWith(jgen, "vba_") && (value.health() == null || value.health().isEmpty())) {
        jgen.writeObjectField(
            "benefits", Optional.ofNullable(value.benefits()).orElse(emptyList()));
      } else {
        writeNonNull(jgen, "benefits", value.benefits());
      }
      if (value.lastUpdated() != null || idStartsWith(jgen, "vha_")) {
        jgen.writeObjectField("last_updated", value.lastUpdated());
      }
      jgen.writeEndObject();
    }
  }

  private static final class WaitTimesSerializer extends StdSerializer<WaitTimes> {
    public WaitTimesSerializer() {
      this(null);
    }

    public WaitTimesSerializer(Class<WaitTimes> t) {
      super(t);
    }

    private static boolean empty(WaitTimes value) {
      return value.health() == null && value.effectiveDate() == null;
    }

    @Override
    @SneakyThrows
    public void serialize(WaitTimes value, JsonGenerator jgen, SerializerProvider provider) {
      jgen.writeStartObject();
      if (!empty(value) || idStartsWith(jgen, "vha_")) {
        jgen.writeObjectField("health", Optional.ofNullable(value.health()).orElse(emptyList()));
        jgen.writeObjectField("effective_date", value.effectiveDate());
      }
      jgen.writeEndObject();
    }
  }
}
