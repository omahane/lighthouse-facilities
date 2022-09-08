package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.Audiology;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.Cardiology;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.Dermatology;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.Gastroenterology;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.Gynecology;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.MentalHealth;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.Ophthalmology;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.Optometry;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.Orthopedics;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.PrimaryCare;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.SpecialtyCare;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.Urology;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.WomensHealth;

import com.google.common.collect.ImmutableMap;
import gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService;
import gov.va.api.lighthouse.facilities.DatamartFacility.HealthService;
import gov.va.api.lighthouse.facilities.DatamartFacility.OtherService;

public interface IsAtcAware {

  static final ImmutableMap<String, HealthService> HEALTH_SERVICES =
      ImmutableMap.<String, HealthService>builder()
          .put("AUDIOLOGY", Audiology)
          .put("CARDIOLOGY", Cardiology)
          .put("WOMEN'S HEALTH", WomensHealth)
          .put("DERMATOLOGY", Dermatology)
          .put("GASTROENTEROLOGY", Gastroenterology)
          .put("OB/GYN", Gynecology)
          .put("MENTAL HEALTH INDIVIDUAL", MentalHealth)
          .put("OPHTHALMOLOGY", Ophthalmology)
          .put("OPTOMETRY", Optometry)
          .put("ORTHOPEDICS", Orthopedics)
          .put("PRIMARY CARE", PrimaryCare)
          .put("SPECIALTY CARE", SpecialtyCare)
          .put("UROLOGY", Urology)
          .build();

  static final ImmutableMap<String, BenefitsService> BENEFITS_SERVICES =
      ImmutableMap.<String, BenefitsService>builder().build();

  static final ImmutableMap<String, OtherService> OTHER_SERVICES =
      ImmutableMap.<String, OtherService>builder().build();
}
