package gov.va.api.lighthouse.facilities;

import gov.va.api.lighthouse.facilities.DatamartFacility.Service.Source;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@Table(name = "facility_services", schema = "app")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class FacilityServicesEntity {

  @EqualsAndHashCode.Include @EmbeddedId private Pk id;

  @Basic(fetch = FetchType.LAZY)
  @Column(name = "service_id")
  private String serviceId;

  @Basic(fetch = FetchType.LAZY)
  @Column
  @Enumerated(EnumType.STRING)
  private Source source;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor(staticName = "of")
  @Embeddable
  public static final class Pk implements Serializable {
    @Column(nullable = false, length = 3, insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private FacilityEntity.Type type;

    @Column(
        name = "station_number",
        nullable = false,
        length = 16,
        insertable = false,
        updatable = false)
    private String stationNumber;

    @Column(nullable = false, insertable = false, updatable = false)
    private String services;

    public String toIdString() {
      return type() + "_" + stationNumber() + ": " + services();
    }
  }
}
