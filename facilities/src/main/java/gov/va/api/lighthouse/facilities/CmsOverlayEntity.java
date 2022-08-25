package gov.va.api.lighthouse.facilities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
}
