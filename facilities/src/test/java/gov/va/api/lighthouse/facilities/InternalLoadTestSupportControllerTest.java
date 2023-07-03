package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class InternalLoadTestSupportControllerTest {
  private int numIterations;

  private String facilityType;

  private CmsOverlayRepository mockCmsOverlayRepository;

  private FacilityRepository mockFacilityRepository;

  private InternalLoadTestSupportController internalController() {
    return InternalLoadTestSupportController.builder()
        .cmsOverlayRepository(mockCmsOverlayRepository)
        .facilityRepository(mockFacilityRepository)
        .build();
  }

  @Test
  @SneakyThrows
  void nearbyAddress() {
    final ResponseEntity<Resource> response = internalController().nearbyAddress(numIterations);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getInputStream().readAllBytes()).isNotEmpty();
  }

  @Test
  @SneakyThrows
  void nearbyLatLonForFacilityType() {
    final ResponseEntity<Resource> response =
        internalController().nearbyLatLonForFacilityType(numIterations, facilityType);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getInputStream().readAllBytes()).isNotEmpty();
  }

  @Test
  @SneakyThrows
  void randomBoundingBoxForFacilityType() {
    final ResponseEntity<Resource> response =
        internalController().randomBoundingBoxForFacilityType(numIterations, facilityType);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getInputStream().readAllBytes()).isNotEmpty();
  }

  @Test
  @SneakyThrows
  void randomDetailedServiceForFacility() {
    final ResponseEntity<Resource> response =
        internalController().randomDetailedServiceForFacility(numIterations, facilityType);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getInputStream().readAllBytes()).isNotEmpty();
  }

  @Test
  @SneakyThrows
  void randomFacilityIdStringForFacilityType() {
    final ResponseEntity<Resource> response =
        internalController().randomFacilityIdStringForFacilityType(numIterations, facilityType);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getInputStream().readAllBytes()).isNotEmpty();
  }

  @Test
  @SneakyThrows
  void randomFacilityIds() {
    final ResponseEntity<Resource> response =
        internalController().randomFacilityIds(numIterations, facilityType);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getInputStream().readAllBytes()).isNotEmpty();
  }

  @Test
  @SneakyThrows
  void randomFacilityTypes() {
    final ResponseEntity<Resource> response =
        internalController().randomFacilityTypes(numIterations);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getInputStream().readAllBytes()).isNotEmpty();
  }

  @Test
  @SneakyThrows
  void randomLatLonForFacilityType() {
    final ResponseEntity<Resource> response =
        internalController().randomLatLonForFacilityType(numIterations, facilityType);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getInputStream().readAllBytes()).isNotEmpty();
  }

  @Test
  @SneakyThrows
  void randomStatesForFacilityType() {
    final ResponseEntity<Resource> response =
        internalController().randomStatesForFacilityType(numIterations, facilityType);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getInputStream().readAllBytes()).isNotEmpty();
  }

  @Test
  @SneakyThrows
  void randomVisnForFacilityType() {
    final ResponseEntity<Resource> response =
        internalController().randomVisnForFacilityType(numIterations, facilityType);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getInputStream().readAllBytes()).isNotEmpty();
  }

  @Test
  @SneakyThrows
  void randomZipCodesForFacilityType() {
    final ResponseEntity<Resource> response =
        internalController().randomZipCodesForFacilityType(numIterations, facilityType);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getInputStream().readAllBytes()).isNotEmpty();
  }

  @BeforeEach
  void setup() {
    numIterations = 100;
    facilityType = "health";
    mockCmsOverlayRepository = mock(CmsOverlayRepository.class);
    mockFacilityRepository = mock(FacilityRepository.class);

    final FacilityEntity.Pk pk =
        FacilityEntity.Pk.fromIdString("vha_777").type(FacilityEntity.Type.vha);
    setupFacilityFixture(pk);
    setupDetailedServicesFixture(pk);
  }

  private void setupDetailedServicesFixture(FacilityEntity.@NonNull Pk pk) {
    when(mockCmsOverlayRepository.findById(pk))
        .thenReturn(
            Optional.of(
                CmsOverlayEntity.builder()
                    .cmsServices(
                        CmsOverlayHelper.serializeDetailedServices(
                            CmsOverlayFixture.overlayDetailedServices()))
                    .build()));
  }

  private void setupFacilityFixture(FacilityEntity.@NonNull Pk pk) {
    when(mockFacilityRepository.findAll())
        .thenReturn(
            List.of(
                FacilityEntity.builder()
                    .id(pk)
                    .latitude(28.131044)
                    .longitude(-80.698581)
                    .state("FL")
                    .zip("32934")
                    .visn("6")
                    .build()));
    when(mockFacilityRepository.findAllIds()).thenReturn(List.of(pk));
  }
}
