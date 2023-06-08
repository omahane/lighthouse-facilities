package gov.va.api.lighthouse.facilities;

import gov.va.api.health.autoconfig.logging.Loggable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

@Loggable
public interface FacilityServicesRepository
    extends CrudRepository<FacilityServicesEntity, FacilityServicesEntity.Pk>,
        JpaSpecificationExecutor<FacilityServicesEntity> {}
