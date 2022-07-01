package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.Facility.FacilityAttributes;
import lombok.SneakyThrows;

public class FacilityAttributesSerializer extends NonEmptySerializer<FacilityAttributes> {

  public FacilityAttributesSerializer() {
    this(null);
  }

  public FacilityAttributesSerializer(Class<FacilityAttributes> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(FacilityAttributes value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "name", value.name());
    writeNonEmpty(jgen, "facilityType", value.facilityType());
    writeNonEmpty(jgen, "classification", value.classification());
    writeNonEmpty(jgen, "parent", value.parent());
    writeNonEmpty(jgen, "website", value.website());
    writeNonEmpty(jgen, "lat", value.latitude());
    writeNonEmpty(jgen, "long", value.longitude());
    writeNonEmpty(jgen, "timeZone", value.timeZone());
    writeNonEmpty(jgen, "address", value.address());
    writeNonEmpty(jgen, "phone", value.phone());
    writeNonEmpty(jgen, "hours", value.hours());
    writeNonEmpty(
        jgen, "operationalHoursSpecialInstructions", value.operationalHoursSpecialInstructions());
    writeNonEmpty(jgen, "services", value.services());
    writeNonEmpty(jgen, "satisfaction", value.satisfaction());
    writeNonEmpty(jgen, "mobile", value.mobile());
    writeNonEmpty(jgen, "activeStatus", value.activeStatus());
    writeNonEmpty(jgen, "operatingStatus", value.operatingStatus());
    writeNonEmpty(jgen, "visn", value.visn());
    jgen.writeEndObject();
  }
}
