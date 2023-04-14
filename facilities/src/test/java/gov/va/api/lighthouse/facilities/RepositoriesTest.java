package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.lighthouse.facilities.DatamartFacility.HealthService;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class RepositoriesTest {
  @Autowired FacilityRepository facilityRepository;
  @Autowired DriveTimeBandRepository driveTimeBandRepository;
  @PersistenceContext private EntityManager entityManager;

  @Test
  void addDriveTimeBandEntities() {
    DriveTimeBandEntity b1230to10 =
        DriveTimeBandEntity.builder()
            .id(DriveTimeBandEntity.Pk.of("123", 0, 10))
            .maxLatitude(80)
            .maxLongitude(50)
            .minLatitude(40)
            .minLongitude(25)
            .monthYear("AUG2020")
            .band("{garbage}")
            .version(null)
            .build();
    var b1230to10Saved = driveTimeBandRepository.save(b1230to10);
    entityManager.flush();
    var b1230to10Found = driveTimeBandRepository.findById(b1230to10.id()).get();
    assertThat(b1230to10Found).usingRecursiveComparison().isEqualTo(b1230to10Saved);
  }

  @Test
  void addFacilityEntities() {
    FacilityEntity f123Vha =
        FacilityEntity.typeSafeBuilder()
            .id(FacilityEntity.Pk.of(FacilityEntity.Type.vha, "123"))
            .latitude(70)
            .longitude(30)
            .zip("90210")
            .state("CA")
            .servicesTypes(Set.of(HealthService.PrimaryCare, HealthService.EmergencyCare))
            .overlayServiceTypes(Set.of(HealthService.Covid19Vaccine))
            .facility("{garbage}")
            .version(null)
            .build();
    var f123VhaSaved = facilityRepository.save(f123Vha);
    entityManager.flush();
    var f123VhaFound = facilityRepository.findById(f123Vha.id()).get();
    assertThat(f123VhaFound).usingRecursiveComparison().isEqualTo(f123VhaSaved);
  }
}
