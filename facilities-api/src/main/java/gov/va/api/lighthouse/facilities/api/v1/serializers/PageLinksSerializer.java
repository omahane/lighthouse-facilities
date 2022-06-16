package gov.va.api.lighthouse.facilities.api.v1.serializers;

import static gov.va.api.lighthouse.facilities.api.v1.serializers.SerializerHelper.hasParent;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.NearbyResponse;
import gov.va.api.lighthouse.facilities.api.v1.PageLinks;
import lombok.SneakyThrows;

public class PageLinksSerializer extends NonEmptySerializer<PageLinks> {

  public PageLinksSerializer() {
    this(null);
  }

  public PageLinksSerializer(Class<PageLinks> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(PageLinks value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    if (hasParent(jgen, NearbyResponse.class)) {
      writeNonEmpty(jgen, "related", value.related());
    }
    writeNonEmpty(jgen, "self", value.self());
    writeNonEmpty(jgen, "first", value.first());
    writeNonEmpty(jgen, "prev", value.prev());
    writeNonEmpty(jgen, "next", value.next());
    writeNonEmpty(jgen, "last", value.last());
    jgen.writeEndObject();
  }
}
