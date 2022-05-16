package gov.va.api.lighthouse.facilities;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@Table(name = "cms_overlay", schema = "app")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CmsOverlayEntity {
  @NotNull @EqualsAndHashCode.Include @EmbeddedId private FacilityEntity.Pk id;

  @Lob
  @Basic(fetch = FetchType.EAGER)
  @Column(name = "cms_operating_status")
  private String cmsOperatingStatus;

  @Lob
  @Basic(fetch = FetchType.EAGER)
  @Column(name = "cms_services")
  private String cmsServices;

  @Default
  @ElementCollection(targetClass = String.class)
  @CollectionTable(
      name = "cms_overlay_detailed_services",
      schema = "app",
      joinColumns = {@JoinColumn(name = "station_number"), @JoinColumn(name = "type")})
  @Column(length = 48, name = "overlay_detailed_services")
  private Set<String> overlayServices = new HashSet<>();

  @Lob
  @Basic(fetch = FetchType.EAGER)
  @Column(name = "health_care_system")
  private String healthCareSystem;

}
