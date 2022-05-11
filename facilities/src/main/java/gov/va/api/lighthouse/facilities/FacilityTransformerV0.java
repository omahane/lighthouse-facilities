package gov.va.api.lighthouse.facilities;

import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/** Utility class for transforming DatamartFacility to version 0 facility object and back. */
@UtilityClass
public final class FacilityTransformerV0 extends BaseVersionedTransformer {
  /** Transform persisted DatamartFacility to version 0 facility. */
  static Facility toFacility(@NonNull DatamartFacility df) {
    return Facility.builder()
        .id(df.id())
        .type(transformType(df.type()))
        .attributes(
            (df.attributes() != null)
                ? Facility.FacilityAttributes.builder()
                    .facilityType(transformFacilityType(df.attributes().facilityType()))
                    .address(transformFacilityAddresses(df.attributes().address()))
                    .hours(transformFacilityHours(df.attributes().hours()))
                    .latitude(df.attributes().latitude())
                    .longitude(df.attributes().longitude())
                    .name(df.attributes().name())
                    .phone(transformFacilityPhone(df.attributes().phone()))
                    .website(df.attributes().website())
                    .classification(df.attributes().classification())
                    .timeZone(df.attributes().timeZone())
                    .mobile(df.attributes().mobile())
                    .services(transformFacilityServices(df.attributes().services()))
                    .activeStatus(transformFacilityActiveStatus(df.attributes().activeStatus()))
                    .visn(df.attributes().visn())
                    .satisfaction(transformFacilitySatisfaction(df.attributes().satisfaction()))
                    .waitTimes(transformFacilityWaitTimes(df.attributes().waitTimes()))
                    .operatingStatus(toFacilityOperatingStatus(df.attributes().operatingStatus()))
                    .detailedServices(
                        DetailedServiceTransformerV0.toDetailedServices(
                            df.attributes().detailedServices()))
                    .operationalHoursSpecialInstructions(
                        df.attributes().operationalHoursSpecialInstructions())
                    .build()
                : null)
        .build();
  }

  /** Transform DatamartFacility operating status to version 0 facility operating status. */
  public static Facility.OperatingStatus toFacilityOperatingStatus(
      DatamartFacility.OperatingStatus datamartFacilityOperatingStatus) {
    return (datamartFacilityOperatingStatus != null)
        ? Facility.OperatingStatus.builder()
            .code(
                (datamartFacilityOperatingStatus.code() != null)
                    ? containsValueOfName(
                            Facility.OperatingStatusCode.values(),
                            datamartFacilityOperatingStatus.code().name())
                        ? Facility.OperatingStatusCode.valueOf(
                            datamartFacilityOperatingStatus.code().name())
                        : null
                    : null)
            .additionalInfo(datamartFacilityOperatingStatus.additionalInfo())
            .supplementalStatuses(
                toFacilitySupplementalStatuses(
                    datamartFacilityOperatingStatus.supplementalStatuses()))
            .build()
        : null;
  }

  /** Transform DatamartFacility supplemental status to version 0 facility supplemental status. */
  public static Facility.SupplementalStatus toFacilitySupplementalStatus(
      @NonNull DatamartFacility.SupplementalStatus datamartFacilitySupplementalStatus) {
    return Facility.SupplementalStatus.builder()
        .id(datamartFacilitySupplementalStatus.id())
        .label(datamartFacilitySupplementalStatus.label())
        .build();
  }

  /**
   * Transform list of DatamartFacility supplemental statuses to version 0 facility supplemental
   * statuses.
   */
  public static List<Facility.SupplementalStatus> toFacilitySupplementalStatuses(
      List<DatamartFacility.SupplementalStatus> datamartFacilitySupplementalStatuses) {
    if (datamartFacilitySupplementalStatuses != null) {
      return datamartFacilitySupplementalStatuses.parallelStream()
          .map(fss -> toFacilitySupplementalStatus(fss))
          .collect(Collectors.toList());
    }
    return null;
  }

  /** Transform version 0 facility to DatamartFacility for persistence. */
  public static DatamartFacility toVersionAgnostic(@NonNull Facility f) {
    return DatamartFacility.builder()
        .id(f.id())
        .type(transformType(f.type()))
        .attributes(
            (f.attributes() != null)
                ? DatamartFacility.FacilityAttributes.builder()
                    .facilityType(transformFacilityType(f.attributes().facilityType()))
                    .address(transformFacilityAddresses(f.attributes().address()))
                    .hours(transformFacilityHours(f.attributes().hours()))
                    .latitude(f.attributes().latitude())
                    .longitude(f.attributes().longitude())
                    .name(f.attributes().name())
                    .phone(transformFacilityPhone(f.attributes().phone()))
                    .website(f.attributes().website())
                    .classification(f.attributes().classification())
                    .timeZone(f.attributes().timeZone())
                    .mobile(f.attributes().mobile())
                    .services(transformFacilityServices(f.attributes().services()))
                    .activeStatus(transformFacilityActiveStatus(f.attributes().activeStatus()))
                    .visn(f.attributes().visn())
                    .satisfaction(transformFacilitySatisfaction(f.attributes().satisfaction()))
                    .waitTimes(transformFacilityWaitTimes(f.attributes().waitTimes()))
                    .operatingStatus(
                        toVersionAgnosticFacilityOperatingStatus(f.attributes().operatingStatus()))
                    .detailedServices(
                        DetailedServiceTransformerV0.toVersionAgnosticDetailedServices(
                            f.attributes().detailedServices()))
                    .operationalHoursSpecialInstructions(
                        f.attributes().operationalHoursSpecialInstructions())
                    .build()
                : null)
        .build();
  }

  /** Transform version 0 facility operating status to DatamartFacility operating status. */
  public static DatamartFacility.OperatingStatus toVersionAgnosticFacilityOperatingStatus(
      Facility.OperatingStatus facilityOperatingStatus) {
    return (facilityOperatingStatus != null)
        ? DatamartFacility.OperatingStatus.builder()
            .code(
                (facilityOperatingStatus.code() != null)
                    ? containsValueOfName(
                            DatamartFacility.OperatingStatusCode.values(),
                            facilityOperatingStatus.code().name())
                        ? DatamartFacility.OperatingStatusCode.valueOf(
                            facilityOperatingStatus.code().name())
                        : null
                    : null)
            .additionalInfo(facilityOperatingStatus.additionalInfo())
            .supplementalStatuses(
                toVersionAgnosticSupplementalStatuses(
                    facilityOperatingStatus.supplementalStatuses()))
            .build()
        : null;
  }

  /** Transform version 0 facility supplemental status to DatamartFacility supplemental status. */
  public static DatamartFacility.SupplementalStatus toVersionAgnosticSupplementalStatus(
      @NonNull Facility.SupplementalStatus facilitySupplementalStatus) {
    return DatamartFacility.SupplementalStatus.builder()
        .id(facilitySupplementalStatus.id())
        .label(facilitySupplementalStatus.label())
        .build();
  }

  /**
   * Transform list of version 0 facility supplemental statuses to DatamartFacility supplemental
   * statuses.
   */
  public static List<DatamartFacility.SupplementalStatus> toVersionAgnosticSupplementalStatuses(
      List<Facility.SupplementalStatus> facilitySupplementalStatuses) {
    if (facilitySupplementalStatuses != null) {
      return facilitySupplementalStatuses.parallelStream()
          .map(fss -> toVersionAgnosticSupplementalStatus(fss))
          .collect(Collectors.toList());
    }
    return null;
  }

  /** Transform DatamartFacility active status to version 0 facility active status. */
  private static Facility.ActiveStatus transformFacilityActiveStatus(
      DatamartFacility.ActiveStatus datamartFacilityActiveStatus) {
    return (datamartFacilityActiveStatus != null)
        ? containsValueOfName(Facility.ActiveStatus.values(), datamartFacilityActiveStatus.name())
            ? Facility.ActiveStatus.valueOf(datamartFacilityActiveStatus.name())
            : null
        : null;
  }

  /** Transform version 0 facility active status to DatamartFacility active status. */
  private static DatamartFacility.ActiveStatus transformFacilityActiveStatus(
      Facility.ActiveStatus facilityActiveStatus) {
    return (facilityActiveStatus != null)
        ? containsValueOfName(DatamartFacility.ActiveStatus.values(), facilityActiveStatus.name())
            ? DatamartFacility.ActiveStatus.valueOf(facilityActiveStatus.name())
            : null
        : null;
  }

  /** Transform DatamartFacility address to version 0 facility address. */
  private static Facility.Address transformFacilityAddress(
      DatamartFacility.Address datamartFacilityAddress) {
    return (datamartFacilityAddress != null)
        ? Facility.Address.builder()
            .address1(datamartFacilityAddress.address1())
            .address2(datamartFacilityAddress.address2())
            .address3(datamartFacilityAddress.address3())
            .city(datamartFacilityAddress.city())
            .state(datamartFacilityAddress.state())
            .zip(datamartFacilityAddress.zip())
            .build()
        : Facility.Address.builder().build();
  }

  /** Transform version 0 facility address to DatamartFacility address. */
  private static DatamartFacility.Address transformFacilityAddress(
      Facility.Address facilityAddress) {
    return (facilityAddress != null)
        ? DatamartFacility.Address.builder()
            .address1(facilityAddress.address1())
            .address2(facilityAddress.address2())
            .address3(facilityAddress.address3())
            .city(facilityAddress.city())
            .state(facilityAddress.state())
            .zip(facilityAddress.zip())
            .build()
        : DatamartFacility.Address.builder().build();
  }

  /** Transform DatamartFacility addresses to version 0 facility addresses. */
  private static Facility.Addresses transformFacilityAddresses(
      DatamartFacility.Addresses datamartFacilityAddresses) {
    return (datamartFacilityAddresses != null)
        ? Facility.Addresses.builder()
            .physical(transformFacilityAddress(datamartFacilityAddresses.physical()))
            .mailing(transformFacilityAddress(datamartFacilityAddresses.mailing()))
            .build()
        : Facility.Addresses.builder().build();
  }

  /** Transform version 0 facility addresses to DatamartFacility addresses. */
  private static DatamartFacility.Addresses transformFacilityAddresses(
      Facility.Addresses facilityAddresses) {
    return (facilityAddresses != null)
        ? DatamartFacility.Addresses.builder()
            .physical(transformFacilityAddress(facilityAddresses.physical()))
            .mailing(transformFacilityAddress(facilityAddresses.mailing()))
            .build()
        : DatamartFacility.Addresses.builder().build();
  }

  /** Transform DatamartFacility benefits service to version 0 facility benefits service. */
  private static Facility.BenefitsService transformFacilityBenefitsService(
      @NonNull DatamartFacility.BenefitsService datamartFacilityBenefitsService) {
    return containsValueOfName(
            Facility.BenefitsService.values(), datamartFacilityBenefitsService.name())
        ? Facility.BenefitsService.valueOf(datamartFacilityBenefitsService.name())
        : null;
  }

  /** Transform version 0 facility benefits service to DatamartFacility benefits service. */
  private static DatamartFacility.BenefitsService transformFacilityBenefitsService(
      @NonNull Facility.BenefitsService facilityBenefitsService) {
    return containsValueOfName(
            DatamartFacility.BenefitsService.values(), facilityBenefitsService.name())
        ? DatamartFacility.BenefitsService.valueOf(facilityBenefitsService.name())
        : null;
  }

  /** Transform DatamartFacility health service to version 0 facility health service. */
  private static Facility.HealthService transformFacilityHealthService(
      @NonNull DatamartFacility.HealthService datamartFacilityHealthService) {
    return containsValueOfName(
            Facility.HealthService.values(), datamartFacilityHealthService.name())
        ? Facility.HealthService.valueOf(datamartFacilityHealthService.name())
        : datamartFacilityHealthService.equals(DatamartFacility.HealthService.MentalHealth)
            ? Facility.HealthService.MentalHealthCare
            : datamartFacilityHealthService.equals(DatamartFacility.HealthService.Dental)
                ? Facility.HealthService.DentalServices
                : null;
  }

  /** Transform version 0 facility health service to DatamartFacility health service. */
  private static DatamartFacility.HealthService transformFacilityHealthService(
      @NonNull Facility.HealthService facilityHealthService) {
    return containsValueOfName(
            DatamartFacility.HealthService.values(), facilityHealthService.name())
        ? DatamartFacility.HealthService.valueOf(facilityHealthService.name())
        : facilityHealthService.equals(Facility.HealthService.MentalHealthCare)
            ? DatamartFacility.HealthService.MentalHealth
            : facilityHealthService.equals(Facility.HealthService.DentalServices)
                ? DatamartFacility.HealthService.Dental
                : null;
  }

  /** Transform DatamartFacility hours to version 0 facility hours. */
  private static Facility.Hours transformFacilityHours(
      DatamartFacility.Hours datamartFacilityHours) {
    return (datamartFacilityHours != null)
        ? Facility.Hours.builder()
            .monday(datamartFacilityHours.monday())
            .tuesday(datamartFacilityHours.tuesday())
            .wednesday(datamartFacilityHours.wednesday())
            .thursday(datamartFacilityHours.thursday())
            .friday(datamartFacilityHours.friday())
            .saturday(datamartFacilityHours.saturday())
            .sunday(datamartFacilityHours.sunday())
            .build()
        : Facility.Hours.builder().build();
  }

  /** Transform version 0 facility hours to DatamartFacility hours. */
  private static DatamartFacility.Hours transformFacilityHours(Facility.Hours facilityHours) {
    return (facilityHours != null)
        ? DatamartFacility.Hours.builder()
            .monday(facilityHours.monday())
            .tuesday(facilityHours.tuesday())
            .wednesday(facilityHours.wednesday())
            .thursday(facilityHours.thursday())
            .friday(facilityHours.friday())
            .saturday(facilityHours.saturday())
            .sunday(facilityHours.sunday())
            .build()
        : DatamartFacility.Hours.builder().build();
  }

  /** Transform DatamartFacility other service to version 0 facility other service. */
  private static Facility.OtherService transformFacilityOtherService(
      @NonNull DatamartFacility.OtherService datamartFacilityOtherService) {
    return containsValueOfName(Facility.OtherService.values(), datamartFacilityOtherService.name())
        ? Facility.OtherService.valueOf(datamartFacilityOtherService.name())
        : null;
  }

  /** Transform version 0 facility other service to DatamartFacility other service. */
  private static DatamartFacility.OtherService transformFacilityOtherService(
      @NonNull Facility.OtherService facilityOtherService) {
    return containsValueOfName(DatamartFacility.OtherService.values(), facilityOtherService.name())
        ? DatamartFacility.OtherService.valueOf(facilityOtherService.name())
        : null;
  }

  /** Transform DatamartFacility patient wait times to version 0 facility patient wait times. */
  private static Facility.PatientWaitTime transformFacilityPatientWaitTime(
      @NonNull DatamartFacility.PatientWaitTime datamartPatientWaitTime) {
    return Facility.PatientWaitTime.builder()
        .newPatientWaitTime(datamartPatientWaitTime.newPatientWaitTime())
        .establishedPatientWaitTime(datamartPatientWaitTime.establishedPatientWaitTime())
        .service(
            (datamartPatientWaitTime.service() != null)
                ? transformFacilityHealthService(datamartPatientWaitTime.service())
                : null)
        .build();
  }

  /** Transform version 0 facility patient wait times to DatamartFacility patient wait times. */
  private static DatamartFacility.PatientWaitTime transformFacilityPatientWaitTime(
      @NonNull Facility.PatientWaitTime facilityPatientWaitTime) {
    return DatamartFacility.PatientWaitTime.builder()
        .newPatientWaitTime(facilityPatientWaitTime.newPatientWaitTime())
        .establishedPatientWaitTime(facilityPatientWaitTime.establishedPatientWaitTime())
        .service(
            (facilityPatientWaitTime.service() != null)
                ? transformFacilityHealthService(facilityPatientWaitTime.service())
                : null)
        .build();
  }

  /** Transform DatamartFacility phone to version 0 facility phone. */
  private static Facility.Phone transformFacilityPhone(
      DatamartFacility.Phone datamartFacilityPhone) {
    return (datamartFacilityPhone != null)
        ? Facility.Phone.builder()
            .fax(datamartFacilityPhone.fax())
            .main(datamartFacilityPhone.main())
            .healthConnect(datamartFacilityPhone.healthConnect())
            .afterHours(datamartFacilityPhone.afterHours())
            .enrollmentCoordinator(datamartFacilityPhone.enrollmentCoordinator())
            .mentalHealthClinic(datamartFacilityPhone.mentalHealthClinic())
            .patientAdvocate(datamartFacilityPhone.patientAdvocate())
            .pharmacy(datamartFacilityPhone.pharmacy())
            .build()
        : Facility.Phone.builder().build();
  }

  /** Transform version 0 facility phone to DatamartFacility phone. */
  private static DatamartFacility.Phone transformFacilityPhone(Facility.Phone facilityPhone) {
    return (facilityPhone != null)
        ? DatamartFacility.Phone.builder()
            .fax(facilityPhone.fax())
            .main(facilityPhone.main())
            .healthConnect(facilityPhone.healthConnect())
            .afterHours(facilityPhone.afterHours())
            .enrollmentCoordinator(facilityPhone.enrollmentCoordinator())
            .mentalHealthClinic(facilityPhone.mentalHealthClinic())
            .patientAdvocate(facilityPhone.patientAdvocate())
            .pharmacy(facilityPhone.pharmacy())
            .build()
        : DatamartFacility.Phone.builder().build();
  }

  /** Transform DatamartFacility satisfaction to version 0 facility satisfaction. */
  private static Facility.Satisfaction transformFacilitySatisfaction(
      DatamartFacility.Satisfaction datamartFacilitySatisfaction) {
    return (datamartFacilitySatisfaction != null)
        ? Facility.Satisfaction.builder()
            .health(
                (datamartFacilitySatisfaction.health() != null)
                    ? Facility.PatientSatisfaction.builder()
                        .primaryCareRoutine(
                            datamartFacilitySatisfaction.health().primaryCareRoutine())
                        .primaryCareUrgent(
                            datamartFacilitySatisfaction.health().primaryCareUrgent())
                        .specialtyCareRoutine(
                            datamartFacilitySatisfaction.health().specialtyCareRoutine())
                        .specialtyCareUrgent(
                            datamartFacilitySatisfaction.health().specialtyCareUrgent())
                        .build()
                    : null)
            .effectiveDate(datamartFacilitySatisfaction.effectiveDate())
            .build()
        : Facility.Satisfaction.builder().build();
  }

  /** Transform version 0 facility satisfaction to DatamartFacility satisfaction. */
  private static DatamartFacility.Satisfaction transformFacilitySatisfaction(
      Facility.Satisfaction facilitySatisfaction) {
    return (facilitySatisfaction != null)
        ? DatamartFacility.Satisfaction.builder()
            .health(
                (facilitySatisfaction.health() != null)
                    ? DatamartFacility.PatientSatisfaction.builder()
                        .primaryCareRoutine(facilitySatisfaction.health().primaryCareRoutine())
                        .primaryCareUrgent(facilitySatisfaction.health().primaryCareUrgent())
                        .specialtyCareRoutine(facilitySatisfaction.health().specialtyCareRoutine())
                        .specialtyCareUrgent(facilitySatisfaction.health().specialtyCareUrgent())
                        .build()
                    : null)
            .effectiveDate(facilitySatisfaction.effectiveDate())
            .build()
        : DatamartFacility.Satisfaction.builder().build();
  }

  /** Transform DatamartFacility services to version 0 facility services. */
  private static Facility.Services transformFacilityServices(
      DatamartFacility.Services datamartFacilityServices) {
    return (datamartFacilityServices != null)
        ? Facility.Services.builder()
            .health(
                (datamartFacilityServices.health() != null)
                    ? datamartFacilityServices.health().parallelStream()
                        .filter(
                            e ->
                                containsValueOfName(Facility.HealthService.values(), e.name())
                                    || checkHealthServiceNameChange(e))
                        .map(e -> transformFacilityHealthService(e))
                        .collect(Collectors.toList())
                    : null)
            .benefits(
                (datamartFacilityServices.benefits() != null)
                    ? datamartFacilityServices.benefits().parallelStream()
                        .filter(
                            e -> containsValueOfName(Facility.BenefitsService.values(), e.name()))
                        .map(e -> transformFacilityBenefitsService(e))
                        .collect(Collectors.toList())
                    : null)
            .other(
                (datamartFacilityServices.other() != null)
                    ? datamartFacilityServices.other().parallelStream()
                        .filter(e -> containsValueOfName(Facility.OtherService.values(), e.name()))
                        .map(e -> transformFacilityOtherService(e))
                        .collect(Collectors.toList())
                    : null)
            .lastUpdated(datamartFacilityServices.lastUpdated())
            .build()
        : Facility.Services.builder().build();
  }

  /** Transform version 0 facility services to DatamartFacility services. */
  private static DatamartFacility.Services transformFacilityServices(
      Facility.Services facilityServices) {
    return (facilityServices != null)
        ? DatamartFacility.Services.builder()
            .health(
                (facilityServices.health() != null)
                    ? facilityServices.health().parallelStream()
                        .filter(
                            e ->
                                containsValueOfName(
                                        DatamartFacility.HealthService.values(), e.name())
                                    || checkHealthServiceNameChange(e))
                        .map(e -> transformFacilityHealthService(e))
                        .collect(Collectors.toList())
                    : null)
            .benefits(
                (facilityServices.benefits() != null)
                    ? facilityServices.benefits().parallelStream()
                        .filter(
                            e ->
                                containsValueOfName(
                                    DatamartFacility.BenefitsService.values(), e.name()))
                        .map(e -> transformFacilityBenefitsService(e))
                        .collect(Collectors.toList())
                    : null)
            .other(
                (facilityServices.other() != null)
                    ? facilityServices.other().parallelStream()
                        .filter(
                            e ->
                                containsValueOfName(
                                    DatamartFacility.OtherService.values(), e.name()))
                        .map(e -> transformFacilityOtherService(e))
                        .collect(Collectors.toList())
                    : null)
            .lastUpdated(facilityServices.lastUpdated())
            .build()
        : DatamartFacility.Services.builder().build();
  }

  /** Transform DatamartFacility facility type to version 0 facility type. */
  private static Facility.FacilityType transformFacilityType(
      DatamartFacility.FacilityType datamartFacilityType) {
    return (datamartFacilityType != null)
        ? containsValueOfName(Facility.FacilityType.values(), datamartFacilityType.name())
            ? Facility.FacilityType.valueOf(datamartFacilityType.name())
            : null
        : null;
  }

  /** Transform version 0 facility type to DatamartFacility facility type. */
  private static DatamartFacility.FacilityType transformFacilityType(
      Facility.FacilityType facilityType) {
    return (facilityType != null)
        ? containsValueOfName(DatamartFacility.FacilityType.values(), facilityType.name())
            ? DatamartFacility.FacilityType.valueOf(facilityType.name())
            : null
        : null;
  }

  /** Transform DatamartFacility wait times to version 0 facility wait times. */
  private static Facility.WaitTimes transformFacilityWaitTimes(
      DatamartFacility.WaitTimes datamartFacilityWaitTimes) {
    return (datamartFacilityWaitTimes != null)
        ? Facility.WaitTimes.builder()
            .health(
                (datamartFacilityWaitTimes.health() != null)
                    ? datamartFacilityWaitTimes.health().parallelStream()
                        .map(e -> transformFacilityPatientWaitTime(e))
                        .collect(Collectors.toList())
                    : null)
            .effectiveDate(datamartFacilityWaitTimes.effectiveDate())
            .build()
        : Facility.WaitTimes.builder().build();
  }

  /** Transform version 0 facility wait times to DatamartFacility wait times. */
  private static DatamartFacility.WaitTimes transformFacilityWaitTimes(
      Facility.WaitTimes facilityWaitTimes) {
    return (facilityWaitTimes != null)
        ? DatamartFacility.WaitTimes.builder()
            .health(
                (facilityWaitTimes.health() != null)
                    ? facilityWaitTimes.health().parallelStream()
                        .map(e -> transformFacilityPatientWaitTime(e))
                        .collect(Collectors.toList())
                    : null)
            .effectiveDate(facilityWaitTimes.effectiveDate())
            .build()
        : DatamartFacility.WaitTimes.builder().build();
  }

  /** Transform version 0 facility type to DatamartFacility facility type. */
  private static Facility.Type transformType(DatamartFacility.Type datamartType) {
    return (datamartType != null)
        ? containsValueOfName(Facility.Type.values(), datamartType.name())
            ? Facility.Type.valueOf(datamartType.name())
            : null
        : null;
  }

  /** Transform DatamartFacility type to version 0 facility type. */
  private static DatamartFacility.Type transformType(Facility.Type type) {
    return (type != null)
        ? containsValueOfName(DatamartFacility.Type.values(), type.name())
            ? DatamartFacility.Type.valueOf(type.name())
            : null
        : null;
  }
}
