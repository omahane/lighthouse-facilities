package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.CmsOverlay;
import lombok.SneakyThrows;

public class CmsOverlaySerializer extends NonEmptySerializer<CmsOverlay> {

  public CmsOverlaySerializer() {
    this(null);
  }

  public CmsOverlaySerializer(Class<CmsOverlay> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(CmsOverlay value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "operatingStatus", value.operatingStatus());
    writeNonEmpty(jgen, "detailedServices", value.detailedServices());
    writeNonEmpty(jgen, "system", value.healthCareSystem());
    writeNonEmpty(jgen, "core", value.core());
    jgen.writeEndObject();
  }
}
