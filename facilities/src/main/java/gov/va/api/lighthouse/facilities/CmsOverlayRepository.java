package gov.va.api.lighthouse.facilities;

import gov.va.api.health.autoconfig.logging.Loggable;
import org.springframework.data.repository.CrudRepository;

@Loggable
public interface CmsOverlayRepository extends CrudRepository<CmsOverlayEntity, FacilityEntity.Pk> {}
