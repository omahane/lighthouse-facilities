package gov.va.api.lighthouse.facilities.api.v1.serializers;

import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import gov.va.api.lighthouse.facilities.api.v1.FacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v1.NearbyResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;

/** Utility class for facilitating JSON serialization of FAPI V1 elements. */
@UtilityClass
public class SerializerHelper {

  public static boolean hasParent(JsonGenerator jgen, Class<?> clazz) {
    return parents(jgen).stream().anyMatch(p -> clazz.isInstance(p));
  }

  private static Optional<String> id(Object obj) {
    if (obj instanceof Facility) {
      return Optional.ofNullable(((Facility) obj).id());
    }
    if (obj instanceof FacilityReadResponse) {
      return Optional.ofNullable(((FacilityReadResponse) obj).facility()).map(x -> x.id());
    }
    if (obj instanceof NearbyResponse.Nearby) {
      return Optional.ofNullable(((NearbyResponse.Nearby) obj).id());
    }
    return Optional.empty();
  }

  /** Determine the facility id associated with the element. */
  public static boolean idStartsWith(JsonGenerator jgen, String prefix) {
    return parents(jgen).stream()
        .map(p -> id(p))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .anyMatch(id -> startsWithIgnoreCase(id, prefix));
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
}
