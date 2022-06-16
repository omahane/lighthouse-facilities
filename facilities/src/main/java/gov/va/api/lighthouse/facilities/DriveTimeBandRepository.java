package gov.va.api.lighthouse.facilities;

import gov.va.api.health.autoconfig.logging.Loggable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

@Loggable
public interface DriveTimeBandRepository
    extends CrudRepository<DriveTimeBandEntity, DriveTimeBandEntity.Pk>,
        JpaSpecificationExecutor<DriveTimeBandEntity> {
  @Query("select distinct e.monthYear from #{#entityName} e")
  List<String> findAllBandVersions();

  @Query("select e.id from #{#entityName} e")
  List<DriveTimeBandEntity.Pk> findAllIds();

  @Query("select min(e.monthYear) from #{#entityName} e")
  String getDefaultBandVersion();

  @Value
  @Builder
  class MinMaxSpecification implements Specification<DriveTimeBandEntity> {
    @NonNull BigDecimal longitude;

    @NonNull BigDecimal latitude;

    Integer maxDriveTime;

    @Override
    public Predicate toPredicate(
        Root<DriveTimeBandEntity> root,
        CriteriaQuery<?> criteriaQuery,
        CriteriaBuilder criteriaBuilder) {
      List<Predicate> predicates = new ArrayList<>(5);
      predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("minLongitude"), longitude));
      predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("maxLongitude"), longitude));
      predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("minLatitude"), latitude));
      predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("maxLatitude"), latitude));
      if (maxDriveTime != null) {
        predicates.add(
            criteriaBuilder.lessThanOrEqualTo(root.get("id").get("toMinutes"), maxDriveTime));
      }
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
  }
}
