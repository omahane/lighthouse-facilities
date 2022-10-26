package gov.va.api.lighthouse.facilities;

import gov.va.api.lighthouse.facilities.api.v1.Facility;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.NonNull;

@Builder
final class CsvTransformerV1 {
  static final List<String> HEADERS =
      List.of(
          "id",
          "name",
          "station_id",
          "latitude",
          "longitude",
          "facility_type",
          "classification",
          "website",
          "mobile",
          "active_status",
          "visn",
          "physical_address_1",
          "physical_address_2",
          "physical_address_3",
          "physical_city",
          "physical_state",
          "physical_zip",
          "mailing_address_1",
          "mailing_address_2",
          "mailing_address_3",
          "mailing_city",
          "mailing_state",
          "mailing_zip",
          "phone_main",
          "phone_fax",
          "phone_mental_health_clinic",
          "phone_pharmacy",
          "phone_after_hours",
          "phone_patient_advocate",
          "phone_enrollment_coordinator",
          "hours_monday",
          "hours_tuesday",
          "hours_wednesday",
          "hours_thursday",
          "hours_friday",
          "hours_saturday",
          "hours_sunday",
          "operating_status_code",
          "operating_status_additional_info");

  @NonNull private final Facility facility;

  private Optional<Facility.Address> addressMailing() {
    return addresses().map(a -> a.mailing());
  }

  private Optional<Facility.Address> addressPhysical() {
    return addresses().map(a -> a.physical());
  }

  private Optional<Facility.Addresses> addresses() {
    return attributes().map(a -> a.address());
  }

  private Optional<Facility.FacilityAttributes> attributes() {
    return Optional.ofNullable(facility.attributes());
  }

  private Optional<Facility.Hours> hours() {
    return attributes().map(a -> a.hours());
  }

  private Optional<Facility.OperatingStatus> operatingStatus() {
    return attributes().map(a -> a.operatingStatus());
  }

  private Optional<Facility.Phone> phone() {
    return attributes().map(a -> a.phone());
  }

  List<String> toRow() {
    return List.of(
        facility.id(),
        attributes().map(a -> a.name()).orElse(""),
        FacilityEntity.Pk.fromIdString(facility.id()).stationNumber(),
        attributes().map(a -> a.latitude()).map(d -> d.toString()).orElse(""),
        attributes().map(a -> a.longitude()).map(d -> d.toString()).orElse(""),
        attributes().map(a -> a.facilityType()).map(t -> t.toString()).orElse(""),
        attributes().map(a -> a.classification()).orElse(""),
        attributes().map(a -> a.website()).orElse(""),
        attributes().map(a -> a.mobile()).map(b -> b.toString()).orElse(""),
        attributes().map(a -> a.visn()).orElse(""),
        addressPhysical().map(a -> a.address1()).orElse(""),
        addressPhysical().map(a -> a.address2()).orElse(""),
        addressPhysical().map(a -> a.address3()).orElse(""),
        addressPhysical().map(a -> a.city()).orElse(""),
        addressPhysical().map(a -> a.state()).orElse(""),
        addressPhysical().map(a -> a.zip()).orElse(""),
        addressMailing().map(a -> a.address1()).orElse(""),
        addressMailing().map(a -> a.address2()).orElse(""),
        addressMailing().map(a -> a.address3()).orElse(""),
        addressMailing().map(a -> a.city()).orElse(""),
        addressMailing().map(a -> a.state()).orElse(""),
        addressMailing().map(a -> a.zip()).orElse(""),
        phone().map(p -> p.main()).orElse(""),
        phone().map(p -> p.fax()).orElse(""),
        phone().map(p -> p.mentalHealthClinic()).orElse(""),
        phone().map(p -> p.pharmacy()).orElse(""),
        phone().map(p -> p.afterHours()).orElse(""),
        phone().map(p -> p.patientAdvocate()).orElse(""),
        phone().map(p -> p.enrollmentCoordinator()).orElse(""),
        hours().map(h -> h.monday()).orElse(""),
        hours().map(h -> h.tuesday()).orElse(""),
        hours().map(h -> h.wednesday()).orElse(""),
        hours().map(h -> h.thursday()).orElse(""),
        hours().map(h -> h.friday()).orElse(""),
        hours().map(h -> h.saturday()).orElse(""),
        hours().map(h -> h.sunday()).orElse(""),
        operatingStatus().map(os -> os.code()).map(c -> c.toString()).orElse(""),
        operatingStatus().map(os -> os.additionalInfo()).orElse(""));
  }
}
