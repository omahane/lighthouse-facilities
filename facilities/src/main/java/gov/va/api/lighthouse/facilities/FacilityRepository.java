package gov.va.api.lighthouse.facilities;

import static java.util.Collections.emptySet;
import static org.springframework.util.CollectionUtils.isEmpty;

import gov.va.api.health.autoconfig.logging.Loggable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

@Loggable
public interface FacilityRepository
    extends CrudRepository<FacilityEntity, FacilityEntity.Pk>,
        JpaSpecificationExecutor<FacilityEntity> {

  @Query("select e.id from #{#entityName} e")
  List<FacilityEntity.Pk> findAllIds();

  List<HasFacilityPayload> findAllProjectedBy();

  List<FacilityEntity> findByIdIn(Collection<FacilityEntity.Pk> ids);

  List<FacilityEntity> findByVisn(String visn);

  @Query("select max(e.lastUpdated) from #{#entityName} e")
  Instant findLastUpdated();

  abstract class ServicesSpecificationHelper implements Specification<FacilityEntity> {

    @SneakyThrows
    protected Predicate buildServicesPredicate(
        Root<FacilityEntity> root, CriteriaBuilder criteriaBuilder, Set<String> services) {
      Predicate[] servicePredicates =
          services.stream()
              .map(s -> criteriaBuilder.isMember(s, root.get("services")))
              .toArray(Predicate[]::new);
      return criteriaBuilder.or(servicePredicates);
    }
  }

  @Builder
  @Value
  @EqualsAndHashCode(callSuper = false)
  class FacilitySpecificationHelper extends ServicesSpecificationHelper {
    VisnSpecification visn;

    FacilityTypeSpecification facilityType;

    MobileSpecification mobile;

    BoundingBoxSpecification boundingBox;

    StateSpecification state;

    TypeServicesIdsSpecification ids;

    ZipSpecification zip;

    @Builder.Default Set<String> services = emptySet();

    private static void addToPredicates(
        Specification<FacilityEntity> spec,
        Root<FacilityEntity> root,
        CriteriaQuery<?> criteriaQuery,
        CriteriaBuilder criteriaBuilder,
        List<Predicate> predicates) {
      if (spec != null) {
        predicates.add(spec.toPredicate(root, criteriaQuery, criteriaBuilder));
      }
    }

    @Override
    public Predicate toPredicate(
        Root<FacilityEntity> root,
        CriteriaQuery<?> criteriaQuery,
        CriteriaBuilder criteriaBuilder) {
      List<Predicate> predicates = new ArrayList<>();
      addToPredicates(visn, root, criteriaQuery, criteriaBuilder, predicates);
      addToPredicates(facilityType, root, criteriaQuery, criteriaBuilder, predicates);
      addToPredicates(mobile, root, criteriaQuery, criteriaBuilder, predicates);
      addToPredicates(boundingBox, root, criteriaQuery, criteriaBuilder, predicates);
      addToPredicates(state, root, criteriaQuery, criteriaBuilder, predicates);
      addToPredicates(ids, root, criteriaQuery, criteriaBuilder, predicates);
      addToPredicates(zip, root, criteriaQuery, criteriaBuilder, predicates);
      if (!services.isEmpty()) {
        predicates.add(buildServicesPredicate(root, criteriaBuilder, services));
      }
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
  }

  @Value
  @Builder
  final class VisnSpecification implements Specification<FacilityEntity> {
    @NonNull String visn;

    @Override
    public Predicate toPredicate(
        Root<FacilityEntity> root,
        CriteriaQuery<?> criteriaQuery,
        CriteriaBuilder criteriaBuilder) {
      return criteriaBuilder.equal(root.get("visn"), visn);
    }
  }

  @Value
  @Builder
  final class FacilityTypeSpecification implements Specification<FacilityEntity> {
    @NonNull FacilityEntity.Type facilityType;

    public Predicate toPredicate(
        Root<FacilityEntity> root,
        CriteriaQuery<?> criteriaQuery,
        CriteriaBuilder criteriaBuilder) {
      return criteriaBuilder.equal(root.get("id").get("type"), facilityType);
    }
  }

  @Value
  @Builder
  final class MobileSpecification implements Specification<FacilityEntity> {
    @NonNull Boolean mobile;

    @Override
    public Predicate toPredicate(
        Root<FacilityEntity> root,
        CriteriaQuery<?> criteriaQuery,
        CriteriaBuilder criteriaBuilder) {
      return criteriaBuilder.equal(root.get("mobile"), mobile);
    }
  }

  @Value
  @Builder
  @EqualsAndHashCode(callSuper = false)
  final class BoundingBoxSpecification implements Specification<FacilityEntity> {
    @NonNull BigDecimal minLongitude;

    @NonNull BigDecimal maxLongitude;

    @NonNull BigDecimal minLatitude;

    @NonNull BigDecimal maxLatitude;

    @Override
    @SneakyThrows
    public Predicate toPredicate(
        Root<FacilityEntity> root,
        CriteriaQuery<?> criteriaQuery,
        CriteriaBuilder criteriaBuilder) {
      List<Predicate> basePredicates = new ArrayList<>(5);
      basePredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("longitude"), minLongitude));
      basePredicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("longitude"), maxLongitude));
      basePredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("latitude"), minLatitude));
      basePredicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("latitude"), maxLatitude));
      return criteriaBuilder.and(basePredicates.toArray(new Predicate[0]));
    }
  }

  @Value
  @Builder
  @EqualsAndHashCode(callSuper = false)
  final class StateSpecification implements Specification<FacilityEntity> {
    @NonNull String state;

    @Override
    @SneakyThrows
    public Predicate toPredicate(
        Root<FacilityEntity> root,
        CriteriaQuery<?> criteriaQuery,
        CriteriaBuilder criteriaBuilder) {
      return criteriaBuilder.equal(root.get("state"), state);
    }
  }

  @Value
  @Builder
  @EqualsAndHashCode(callSuper = false)
  final class StationNumbersSpecification extends ServicesSpecificationHelper {
    @Builder.Default Set<String> stationNumbers = emptySet();

    FacilityEntity.Type facilityType;

    @Builder.Default Set<String> services = emptySet();

    @Override
    @SneakyThrows
    public Predicate toPredicate(
        Root<FacilityEntity> root,
        CriteriaQuery<?> criteriaQuery,
        CriteriaBuilder criteriaBuilder) {
      if (isEmpty(stationNumbers)) {
        return criteriaBuilder.isTrue(criteriaBuilder.literal(false));
      }
      List<Predicate> basePredicates = new ArrayList<>(2);
      CriteriaBuilder.In<String> stationsInClause =
          criteriaBuilder.in(root.get("id").get("stationNumber"));
      stationNumbers.forEach(stationsInClause::value);
      basePredicates.add(stationsInClause);
      if (facilityType != null) {
        basePredicates.add(criteriaBuilder.equal(root.get("id").get("type"), facilityType));
      }

      Predicate combinedBase = criteriaBuilder.and(basePredicates.toArray(new Predicate[0]));
      if (isEmpty(services)) {
        return combinedBase;
      }
      return criteriaBuilder.and(
          combinedBase, buildServicesPredicate(root, criteriaBuilder, services));
    }
  }

  @Value
  @Builder
  @EqualsAndHashCode(callSuper = false)
  final class TypeServicesIdsSpecification implements Specification<FacilityEntity> {
    @Builder.Default Collection<FacilityEntity.Pk> ids = emptySet();

    @Override
    @SneakyThrows
    public Predicate toPredicate(
        Root<FacilityEntity> root,
        CriteriaQuery<?> criteriaQuery,
        CriteriaBuilder criteriaBuilder) {
      List<Predicate> basePredicates = new ArrayList<>(2);
      if (!isEmpty(ids)) {
        CriteriaBuilder.In<FacilityEntity.Pk> idsInClause = criteriaBuilder.in(root.get("id"));
        ids.forEach(idsInClause::value);
        basePredicates.add(idsInClause);
      }
      return criteriaBuilder.and(basePredicates.toArray(new Predicate[0]));
    }
  }

  @Value
  @Builder
  @EqualsAndHashCode(callSuper = false)
  final class ZipSpecification implements Specification<FacilityEntity> {
    @NonNull String zip;

    @Override
    @SneakyThrows
    public Predicate toPredicate(
        Root<FacilityEntity> root,
        CriteriaQuery<?> criteriaQuery,
        CriteriaBuilder criteriaBuilder) {
      return criteriaBuilder.equal(root.get("zip"), zip);
    }
  }
}
