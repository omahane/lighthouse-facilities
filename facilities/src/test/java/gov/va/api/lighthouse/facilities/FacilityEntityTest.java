package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.DatamartFacilitiesJacksonConfig.createMapper;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService;
import gov.va.api.lighthouse.facilities.DatamartFacility.HealthService;
import gov.va.api.lighthouse.facilities.DatamartFacility.OtherService;
import gov.va.api.lighthouse.facilities.DatamartFacility.Service;
import java.util.Set;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class FacilityEntityTest {
  private static final ObjectMapper DATAMART_MAPPER = createMapper();

  @Test
  @SneakyThrows
  void overlayServicesFromServiceTypes() {
    FacilityEntity e = FacilityEntity.builder().build();
    e.overlayServicesFromServiceTypes(
        Set.of(
            HealthService.SpecialtyCare,
            BenefitsService.ApplyingForBenefits,
            OtherService.OnlineScheduling));
    assertThat(e.overlayServices())
        .containsExactlyInAnyOrder(
            capitalize(HealthService.SpecialtyCare.serviceId()),
            capitalize(BenefitsService.ApplyingForBenefits.serviceId()),
            capitalize(OtherService.OnlineScheduling.serviceId()));
  }

  @Test
  void pkFromIdStringParsesId() {
    assertThat(FacilityEntity.Pk.fromIdString("vba_A1B2C3"))
        .isEqualTo(FacilityEntity.Pk.of(FacilityEntity.Type.vba, "A1B2C3"));
    assertThat(FacilityEntity.Pk.fromIdString("vc_A1B2C3"))
        .isEqualTo(FacilityEntity.Pk.of(FacilityEntity.Type.vc, "A1B2C3"));
    assertThat(FacilityEntity.Pk.fromIdString("vha_A1B2C3"))
        .isEqualTo(FacilityEntity.Pk.of(FacilityEntity.Type.vha, "A1B2C3"));
    assertThat(FacilityEntity.Pk.fromIdString("nca_A1B2C3"))
        .isEqualTo(FacilityEntity.Pk.of(FacilityEntity.Type.nca, "A1B2C3"));
    assertThat(FacilityEntity.Pk.fromIdString("nca_A1_B2_C3"))
        .describedAs("station number with underscores")
        .isEqualTo(FacilityEntity.Pk.of(FacilityEntity.Type.nca, "A1_B2_C3"));
    assertThatIllegalArgumentException()
        .describedAs("ID with unknown type")
        .isThrownBy(() -> FacilityEntity.Pk.fromIdString("nope_A1B2C3"));
    assertThatIllegalArgumentException()
        .describedAs("ID without type")
        .isThrownBy(() -> FacilityEntity.Pk.fromIdString("_A1B2C3"));
    assertThatIllegalArgumentException()
        .describedAs("ID without station number")
        .isThrownBy(() -> FacilityEntity.Pk.fromIdString("vha_"));
    assertThatIllegalArgumentException()
        .describedAs("garbage ID")
        .isThrownBy(() -> FacilityEntity.Pk.fromIdString("vha"));
    assertThatIllegalArgumentException()
        .describedAs("garbage ID")
        .isThrownBy(() -> FacilityEntity.Pk.fromIdString("vha"));
    assertThatIllegalArgumentException()
        .describedAs("empty ID")
        .isThrownBy(() -> FacilityEntity.Pk.fromIdString(""));
  }

  @Test
  @SneakyThrows
  void servicesFromServiceTypes() {
    FacilityEntity e = FacilityEntity.builder().build();
    e.servicesFromServiceTypes(
        Set.of(
            Service.<HealthService>builder()
                .serviceType(HealthService.SpecialtyCare)
                .name(HealthService.SpecialtyCare.name())
                .build(),
            Service.<BenefitsService>builder()
                .serviceType(BenefitsService.ApplyingForBenefits)
                .name(BenefitsService.ApplyingForBenefits.name())
                .build(),
            Service.<OtherService>builder()
                .serviceType(OtherService.OnlineScheduling)
                .name(OtherService.OnlineScheduling.name())
                .build()));
    assertThat(e.services())
        .containsExactlyInAnyOrder(
            DATAMART_MAPPER.writeValueAsString(
                Service.<HealthService>builder().serviceType(HealthService.SpecialtyCare).build()),
            DATAMART_MAPPER.writeValueAsString(
                Service.<BenefitsService>builder()
                    .serviceType(BenefitsService.ApplyingForBenefits)
                    .build()),
            DATAMART_MAPPER.writeValueAsString(
                Service.<OtherService>builder()
                    .serviceType(OtherService.OnlineScheduling)
                    .build()));
  }
}
