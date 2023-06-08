package gov.va.api.lighthouse.facilities.api;

import static gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Audiology;
import static gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Cardiology;
import static gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Dermatology;
import static gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Gastroenterology;
import static gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Gynecology;
import static gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.MentalHealth;
import static gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Ophthalmology;
import static gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Optometry;
import static gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Orthopedics;
import static gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.PrimaryCare;
import static gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.Urology;
import static gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService.WomensHealth;

import com.google.common.collect.ImmutableMap;
import gov.va.api.lighthouse.facilities.api.v1.Facility.BenefitsService;
import gov.va.api.lighthouse.facilities.api.v1.Facility.HealthService;
import gov.va.api.lighthouse.facilities.api.v1.Facility.OtherService;

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
          // SPECIALTY CARE no longer served by ATC
          .put("UROLOGY", Urology)
          .build();

  static final ImmutableMap<String, BenefitsService> BENEFITS_SERVICES =
      ImmutableMap.<String, BenefitsService>builder().build();

  static final ImmutableMap<String, OtherService> OTHER_SERVICES =
      ImmutableMap.<String, OtherService>builder().build();
}
