package gov.va.api.lighthouse.facilities;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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

  @EqualsAndHashCode.Include @EmbeddedId private FacilityEntity.Pk id;

  @Column(name = "station_number", length = 16, insertable = false, updatable = false)
  private String stationNumber;

  @Column(length = 3, insertable = false, updatable = false)
  private String type;

  @Column(insertable = false, updatable = false)
  private String services;
}
