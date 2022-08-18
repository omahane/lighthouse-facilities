package gov.va.api.lighthouse.facilities.collector;

import static gov.va.api.lighthouse.facilities.DatamartFacility.FacilityType.va_health_facility;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.CaregiverSupport;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.Dental;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.Nutrition;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.Orthopedics;
import static gov.va.api.lighthouse.facilities.DatamartFacility.HealthService.Podiatry;
import static gov.va.api.lighthouse.facilities.DatamartFacility.Service;
import static gov.va.api.lighthouse.facilities.DatamartFacility.Type.va_facilities;
import static gov.va.api.lighthouse.facilities.collector.Transformers.allBlank;
import static gov.va.api.lighthouse.facilities.collector.Transformers.checkAngleBracketNull;
import static gov.va.api.lighthouse.facilities.collector.Transformers.emptyToNull;
import static gov.va.api.lighthouse.facilities.collector.Transformers.hoursToClosed;
import static gov.va.api.lighthouse.facilities.collector.Transformers.phoneTrim;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.upperCase;

import com.google.common.collect.ListMultimap;
import gov.va.api.lighthouse.facilities.DatamartFacility;
import gov.va.api.lighthouse.facilities.DatamartFacility.ActiveStatus;
import gov.va.api.lighthouse.facilities.DatamartFacility.Address;
import gov.va.api.lighthouse.facilities.DatamartFacility.Addresses;
import gov.va.api.lighthouse.facilities.DatamartFacility.FacilityAttributes;
import gov.va.api.lighthouse.facilities.DatamartFacility.HealthService;
import gov.va.api.lighthouse.facilities.DatamartFacility.Hours;
import gov.va.api.lighthouse.facilities.DatamartFacility.PatientSatisfaction;
import gov.va.api.lighthouse.facilities.DatamartFacility.Phone;
import gov.va.api.lighthouse.facilities.DatamartFacility.Satisfaction;
import gov.va.api.lighthouse.facilities.DatamartFacility.Services;
import gov.va.api.lighthouse.facilities.DatamartFacility.WaitTimes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.Builder;
import lombok.NonNull;

@Builder
final class HealthTransformer {

  @NonNull private final VastEntity vast;

  @NonNull private final AccessToCareCollector accessToCareCollector;

  @NonNull private final AccessToPwtCollector accessToPwtCollector;

  @NonNull private final Map<String, String> mentalHealthPhoneNumbers;

  @NonNull private final ListMultimap<String, StopCode> stopCodesMap;

  @NonNull private final Map<String, String> websites;

  @NonNull private final ArrayList<String> cscFacilities;

  @NonNull private final ArrayList<String> orthoFacilities;

  private ActiveStatus activeStatus() {
    if (allBlank(vast.pod())) {
      return null;
    }
    return vast.pod().equalsIgnoreCase("A") ? ActiveStatus.A : ActiveStatus.T;
  }

  private Addresses address() {
    if (allBlank(physical())) {
      return null;
    }
    return Addresses.builder().physical(physical()).build();
  }

  private FacilityAttributes attributes() {
    final var classification = classification();
    final var website = website();
    final var address = address();
    final var phone = phone();
    final var hours = hours();
    final var services = services();
    final var satisfaction = satisfaction();
    final var waitTimes = waitTimes();
    final var activeStatus = activeStatus();

    if (allBlank(
        vast.stationName(),
        classification,
        website,
        vast.latitude(),
        vast.longitude(),
        address,
        phone,
        hours,
        vast.operationalHoursSpecialInstructions(),
        services,
        satisfaction,
        waitTimes,
        vast.mobile(),
        activeStatus,
        vast.visn())) {
      return null;
    }
    return FacilityAttributes.builder()
        .name(vast.stationName())
        .facilityType(va_health_facility)
        .classification(classification)
        .website(website)
        .latitude(vast.latitude())
        .longitude(vast.longitude())
        .timeZone(TimeZoneFinder.calculateTimeZonesWithMap(vast.latitude(), vast.longitude(), id()))
        .address(address)
        .phone(phone)
        .hours(hours)
        .operationalHoursSpecialInstructions(vast.operationalHoursSpecialInstructions())
        .services(services)
        .satisfaction(satisfaction)
        .waitTimes(waitTimes)
        .mobile(vast.mobile())
        .activeStatus(activeStatus)
        .visn(vast.visn())
        .build();
  }

  String classification() {
    switch (trimToEmpty(vast.cocClassificationId())) {
      case "1":
        return "VA Medical Center (VAMC)";
      case "2":
        return "Health Care Center (HCC)";
      case "3":
        return "Multi-Specialty CBOC";
      case "4":
        return "Primary Care CBOC";
      case "5":
        return "Other Outpatient Services (OOS)";
      case "7":
        return "Residential Care Site (MH RRTP/DRRTP) (Stand-Alone)";
      case "8":
        return "Extended Care Site (Community Living Center) (Stand-Alone)";
      default:
        if (isNotBlank(vast.cocClassificationId())) {
          return vast.cocClassificationId();
        }
        return vast.abbreviation();
    }
  }

  boolean hasCaregiverSupport() {
    return !allBlank(id()) && cscFacilities.contains(id());
  }

  boolean hasOrthopedics() {
    return !allBlank(id()) && orthoFacilities.contains(id());
  }

  private Hours hours() {
    String mon = hoursToClosed(vast.monday());
    String tue = hoursToClosed(vast.tuesday());
    String wed = hoursToClosed(vast.wednesday());
    String thu = hoursToClosed(vast.thursday());
    String fri = hoursToClosed(vast.friday());
    String sat = hoursToClosed(vast.saturday());
    String sun = hoursToClosed(vast.sunday());
    if (allBlank(mon, tue, wed, thu, fri, sat, sun)) {
      return null;
    }
    return Hours.builder()
        .monday(mon)
        .tuesday(tue)
        .wednesday(wed)
        .thursday(thu)
        .friday(fri)
        .saturday(sat)
        .sunday(sun)
        .build();
  }

  private String id() {
    if (allBlank(vast.stationNumber())) {
      return null;
    }
    return "vha_" + vast.stationNumber();
  }

  private Phone phone() {
    String fax = phoneTrim(vast.staFax());
    String main = phoneTrim(vast.staPhone());
    String pharmacy = phoneTrim(vast.pharmacyPhone());
    String afterHours = phoneTrim(vast.afterHoursPhone());
    String patientAdvocate = phoneTrim(vast.patientAdvocatePhone());
    String mentalHealth = phoneTrim(mentalHealthPhoneNumbers.get(id()));
    String enrollmentCoordinator = phoneTrim(vast.enrollmentCoordinatorPhone());
    if (allBlank(
        fax, main, pharmacy, afterHours, patientAdvocate, mentalHealth, enrollmentCoordinator)) {
      return null;
    }
    return Phone.builder()
        .fax(fax)
        .main(main)
        .pharmacy(pharmacy)
        .afterHours(afterHours)
        .patientAdvocate(patientAdvocate)
        .mentalHealthClinic(mentalHealth)
        .enrollmentCoordinator(enrollmentCoordinator)
        .build();
  }

  private Address physical() {
    if (allBlank(
        zip(),
        vast.city(),
        vast.state(),
        checkAngleBracketNull(vast.address2()),
        checkAngleBracketNull(vast.address1()),
        checkAngleBracketNull(vast.address3()))) {
      return null;
    }
    // address1 and address2 swapped
    return Address.builder()
        .zip(zip())
        .city(vast.city())
        .state(upperCase(vast.state(), Locale.US))
        .address1(checkAngleBracketNull(vast.address2()))
        .address2(checkAngleBracketNull(vast.address1()))
        .address3(checkAngleBracketNull(vast.address3()))
        .build();
  }

  private Satisfaction satisfaction() {
    final var satisfactionScores = satisfactionScores();
    final var atpEffectiveDate = accessToPwtCollector.atpEffectiveDate(id());

    if (allBlank(satisfactionScores, atpEffectiveDate)) {
      return null;
    }
    return Satisfaction.builder()
        .health(satisfactionScores)
        .effectiveDate(atpEffectiveDate)
        .build();
  }

  private PatientSatisfaction satisfactionScores() {
    final var facilityId = id();
    final var primaryCareUrgentShep =
        accessToPwtCollector.satisfactionScore(facilityId, "Primary Care (Urgent)");
    final var primaryCareRoutineShep =
        accessToPwtCollector.satisfactionScore(facilityId, "Primary Care (Routine)");
    final var specialtyCareUrgentShep =
        accessToPwtCollector.satisfactionScore(facilityId, "Specialty Care (Urgent)");
    final var specialtyCareRoutineShep =
        accessToPwtCollector.satisfactionScore(facilityId, "Specialty Care (Routine)");

    if (allBlank(
        primaryCareUrgentShep,
        primaryCareRoutineShep,
        specialtyCareUrgentShep,
        specialtyCareRoutineShep)) {
      return null;
    }
    return PatientSatisfaction.builder()
        .primaryCareUrgent(primaryCareUrgentShep)
        .primaryCareRoutine(primaryCareRoutineShep)
        .specialtyCareUrgent(specialtyCareUrgentShep)
        .specialtyCareRoutine(specialtyCareRoutineShep)
        .build();
  }

  private Services services() {
    final var healthServices = servicesHealth();
    final var atcEffectiveDate = accessToCareCollector.atcEffectiveDate(id());

    if (allBlank(healthServices, atcEffectiveDate)) {
      return null;
    }
    return Services.builder().health(healthServices).lastUpdated(atcEffectiveDate).build();
  }

  private List<Service<HealthService>> servicesHealth() {
    List<Service<HealthService>> services = accessToCareCollector.servicesHealth(id());

    if (stopCodes().stream().anyMatch(sc -> StopCode.DENTISTRY.contains(trimToEmpty(sc.code())))) {
      services.add(Service.<HealthService>builder().serviceType(Dental).build());
    }
    if (stopCodes().stream().anyMatch(sc -> StopCode.NUTRITION.contains(trimToEmpty(sc.code())))) {
      services.add(Service.<HealthService>builder().serviceType(Nutrition).build());
    }
    if (stopCodes().stream().anyMatch(sc -> StopCode.PODIATRY.contains(trimToEmpty(sc.code())))) {
      services.add(Service.<HealthService>builder().serviceType(Podiatry).build());
    }
    if (hasCaregiverSupport()) {
      services.add(Service.<HealthService>builder().serviceType(CaregiverSupport).build());
    }
    if (hasOrthopedics()) {
      services.add(Service.<HealthService>builder().serviceType(Orthopedics).build());
    }
    Collections.sort(services, (left, right) -> left.name().compareToIgnoreCase(right.name()));
    return emptyToNull(services);
  }

  private List<StopCode> stopCodes() {
    return stopCodesMap.get(trimToEmpty(upperCase(id(), Locale.US)));
  }

  DatamartFacility toDatamartFacility() {
    final var facilityId = id();
    if (allBlank(facilityId)) {
      return null;
    }
    return DatamartFacility.builder()
        .id(facilityId)
        .type(va_facilities)
        .attributes(attributes())
        .build();
  }

  private WaitTimes waitTimes() {
    final var facilityId = id();
    final var waitTimesHealth = accessToCareCollector.waitTimesHealth(facilityId);
    final var atcEffectiveDate = accessToCareCollector.atcEffectiveDate(facilityId);

    if (allBlank(waitTimesHealth, atcEffectiveDate)) {
      return null;
    }
    return WaitTimes.builder().health(waitTimesHealth).effectiveDate(atcEffectiveDate).build();
  }

  String website() {
    return allBlank(id()) ? null : websites.get(id());
  }

  private String zip() {
    String zip = vast.zip();
    String zipPlus4 = vast.zip4();
    if (isNotBlank(zip) && isNotBlank(zipPlus4) && !zipPlus4.matches("^[0]+$")) {
      return zip + "-" + zipPlus4;
    }
    return zip;
  }
}
