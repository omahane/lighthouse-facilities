package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.ApplyingForBenefits;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.BurialClaimAssistance;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.DisabilityClaimAssistance;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.EducationAndCareerCounseling;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.EducationClaimAssistance;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.FamilyMemberClaimAssistance;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.HomelessAssistance;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.InsuranceClaimAssistanceAndFinancialCounseling;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.IntegratedDisabilityEvaluationSystemAssistance;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.Pensions;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.PreDischargeClaimAssistance;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.TransitionAssistance;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.UpdatingDirectDepositInformation;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.VAHomeLoanAssistance;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.VocationalRehabilitationAndEmploymentAssistance;
import static gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService.eBenefitsRegistrationAssistance;
import static gov.va.api.lighthouse.facilities.DatamartFacility.FacilityType.va_benefits_facility;
import static gov.va.api.lighthouse.facilities.DatamartFacility.Type.va_facilities;
import static gov.va.api.lighthouse.facilities.collector.Transformers.allBlank;
import static gov.va.api.lighthouse.facilities.collector.Transformers.checkAngleBracketNull;
import static gov.va.api.lighthouse.facilities.collector.Transformers.phoneTrim;
import static org.apache.commons.lang3.StringUtils.upperCase;

import gov.va.api.lighthouse.facilities.DatamartFacility;
import gov.va.api.lighthouse.facilities.DatamartFacility.Address;
import gov.va.api.lighthouse.facilities.DatamartFacility.Addresses;
import gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService;
import gov.va.api.lighthouse.facilities.DatamartFacility.FacilityAttributes;
import gov.va.api.lighthouse.facilities.DatamartFacility.Hours;
import gov.va.api.lighthouse.facilities.DatamartFacility.Phone;
import gov.va.api.lighthouse.facilities.DatamartFacility.Services;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

@Builder
final class BenefitsTransformer {
  @NonNull CdwBenefits cdwFacility;

  String csvWebsite;

  private FacilityAttributes attributes() {
    return FacilityAttributes.builder()
        .name(cdwFacility.facilityName())
        .facilityType(va_benefits_facility)
        .classification(cdwFacility.facilityType())
        .latitude(cdwFacility.latitude())
        .longitude(cdwFacility.longitude())
        .timeZone(
            TimeZoneFinder.calculateTimeZonesWithMap(
                cdwFacility.latitude(),
                cdwFacility.longitude(),
                "vba_" + cdwFacility.facilityNumber()))
        .website(website(cdwFacility.websiteUrl()))
        .address(
            Addresses.builder()
                .physical(
                    Address.builder()
                        .address1(checkAngleBracketNull(cdwFacility.address1()))
                        .address2(checkAngleBracketNull(cdwFacility.address2()))
                        .city(cdwFacility.city())
                        .state(upperCase(cdwFacility.state(), Locale.US))
                        .zip(cdwFacility.zip())
                        .build())
                .build())
        .phone(phone())
        .hours(
            Hours.builder()
                .monday(cdwFacility.monday())
                .tuesday(cdwFacility.tuesday())
                .wednesday(cdwFacility.wednesday())
                .thursday(cdwFacility.thursday())
                .friday(cdwFacility.friday())
                .saturday(cdwFacility.saturday())
                .sunday(cdwFacility.sunday())
                .build())
        .services(services())
        .build();
  }

  private Phone phone() {
    String main = phoneTrim(cdwFacility.phone());
    String fax = phoneTrim(cdwFacility.fax());
    if (allBlank(main, fax)) {
      return null;
    } else {
      return Phone.builder().main(main).fax(fax).build();
    }
  }

  Services services() {
    List<BenefitsService> benefitsServices = new ArrayList<>();
    if (yesNoToBoolean(cdwFacility.applyingForBenefits())) {
      benefitsServices.add(ApplyingForBenefits);
    }
    if (yesNoToBoolean(cdwFacility.burialClaimAssistance())) {
      benefitsServices.add(BurialClaimAssistance);
    }
    if (yesNoToBoolean(cdwFacility.disabilityClaimAssistance())) {
      benefitsServices.add(DisabilityClaimAssistance);
    }
    if (yesNoToBoolean(cdwFacility.ebenefitsRegistration())) {
      benefitsServices.add(eBenefitsRegistrationAssistance);
    }
    if (yesNoToBoolean(cdwFacility.educationAndCareerCounseling())) {
      benefitsServices.add(EducationAndCareerCounseling);
    }
    if (yesNoToBoolean(cdwFacility.educationClaimAssistance())) {
      benefitsServices.add(EducationClaimAssistance);
    }
    if (yesNoToBoolean(cdwFacility.familyMemberClaimAssistance())) {
      benefitsServices.add(FamilyMemberClaimAssistance);
    }
    if (yesNoToBoolean(cdwFacility.homelessAssistance())) {
      benefitsServices.add(HomelessAssistance);
    }
    if (yesNoToBoolean(cdwFacility.vaHomeLoanAssistance())) {
      benefitsServices.add(VAHomeLoanAssistance);
    }
    if (yesNoToBoolean(cdwFacility.insuranceClaimAssistance())) {
      benefitsServices.add(InsuranceClaimAssistanceAndFinancialCounseling);
    }
    if (yesNoToBoolean(cdwFacility.integratedDisabilityEvaluationSystem())) {
      benefitsServices.add(IntegratedDisabilityEvaluationSystemAssistance);
    }
    if (yesNoToBoolean(cdwFacility.preDischargeClaimAssistance())) {
      benefitsServices.add(PreDischargeClaimAssistance);
    }
    if (yesNoToBoolean(cdwFacility.transitionAssistance())) {
      benefitsServices.add(TransitionAssistance);
    }
    if (yesNoToBoolean(cdwFacility.updatingDirectDepositInformation())) {
      benefitsServices.add(UpdatingDirectDepositInformation);
    }
    if (yesNoToBoolean(cdwFacility.vocationalRehabilitationEmplo())) {
      benefitsServices.add(VocationalRehabilitationAndEmploymentAssistance);
    }
    if (StringUtils.containsIgnoreCase(cdwFacility.otherServices(), "PENSION")) {
      benefitsServices.add(Pensions);
    }
    return Services.builder().benefits(benefitsServices).build();
  }

  DatamartFacility toDatamartFacility() {
    return DatamartFacility.builder()
        .id("vba_" + cdwFacility.facilityNumber())
        .type(va_facilities)
        .attributes(attributes())
        .build();
  }

  String website(String website) {
    /* CDW returns a string NULL... We don't want to return that.*/
    return website == null || website.equalsIgnoreCase("NULL") ? csvWebsite : website;
  }

  private boolean yesNoToBoolean(String yesNo) {
    /* Assume no if the answer is not an emphatic yes or the string is null. */
    return "YES".equalsIgnoreCase(yesNo);
  }
}
