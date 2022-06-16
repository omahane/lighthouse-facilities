package gov.va.api.lighthouse.facilities.api.v1.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import gov.va.api.lighthouse.facilities.api.v1.Pagination;
import lombok.SneakyThrows;

public class PaginationSerializer extends NonEmptySerializer<Pagination> {

  public PaginationSerializer() {
    this(null);
  }

  public PaginationSerializer(Class<Pagination> t) {
    super(t);
  }

  @Override
  @SneakyThrows
  public void serialize(Pagination value, JsonGenerator jgen, SerializerProvider provider) {
    jgen.writeStartObject();
    writeNonEmpty(jgen, "currentPage", value.currentPage());
    writeNonEmpty(jgen, "perPage", value.entriesPerPage());
    writeNonEmpty(jgen, "totalPages", value.totalPages());
    writeNonEmpty(jgen, "totalEntries", value.totalEntries());
    jgen.writeEndObject();
  }
}
