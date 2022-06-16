package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.CmsOverlayResponse;
import lombok.SneakyThrows;

public class CmsOverlayResponseSerializer extends NonEmptySerializer<CmsOverlayResponse> {

  public CmsOverlayResponseSerializer() {
    this(null);
  }

  public CmsOverlayResponseSerializer(Class<CmsOverlayResponse> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(CmsOverlayResponse value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "overlay", value.overlay());
    jgen.writeEndObject();
  }
}
