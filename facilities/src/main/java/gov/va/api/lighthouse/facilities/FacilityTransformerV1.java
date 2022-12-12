package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildServicesLink;
import static gov.va.api.lighthouse.facilities.api.ServiceLinkBuilder.buildTypedServiceLink;

import gov.va.api.lighthouse.facilities.DatamartFacility.BenefitsService;
import gov.va.api.lighthouse.facilities.DatamartFacility.HealthService;
import gov.va.api.lighthouse.facilities.DatamartFacility.OtherService;
import gov.va.api.lighthouse.facilities.DatamartFacility.Service;
import gov.va.api.lighthouse.facilities.DatamartFacility.Service.Source;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/** Utility class for transforming DatamartFacility to version 1 facility object and back. */
@UtilityClass
public final class FacilityTransformerV1 {
  private static Facility.OperatingStatus determineOperatingStatusFromActiveStatus(
      DatamartFacility.ActiveStatus activeStatus) {
    return Facility.OperatingStatus.builder()
        .code(
            activeStatus == DatamartFacility.ActiveStatus.T
                ? Facility.OperatingStatusCode.CLOSED
                : Facility.OperatingStatusCode.NORMAL)
        .build();
  }

  /** Transform persisted DatamartFacility to version 1 facility. */
  static Facility toFacility(
      @NonNull DatamartFacility df, @NonNull String linkerUrl, List<String> serviceSources) {
    return Facility.builder()
        .id(df.id())
        .type(toType(df.type()))
        .attributes(
            (df.attributes() != null)
                ? Facility.FacilityAttributes.builder()
                    .facilityType(toFacilityType(df.attributes().facilityType()))
                    .address(toFacilityAddresses(df.attributes().address()))
                    .hours(toFacilityHours(df.attributes().hours()))
                    .latitude(df.attributes().latitude())
                    .longitude(df.attributes().longitude())
                    .name(df.attributes().name())
                    .phone(toFacilityPhone(df.attributes().phone()))
                    .website(df.attributes().website())
                    .classification(df.attributes().classification())
                    .timeZone(df.attributes().timeZone())
                    .mobile(df.attributes().mobile())
                    .services(
                        toFacilityServices(
                            df.attributes().services(), linkerUrl, serviceSources, df.id()))
                    .visn(df.attributes().visn())
                    .satisfaction(toFacilitySatisfaction(df.attributes().satisfaction()))
                    .operatingStatus(toFacilityOperatingStatus(df.attributes().operatingStatus()))
                    .operationalHoursSpecialInstructions(
                        toFacilityOperationalHoursSpecialInstructions(
                            df.attributes().operationalHoursSpecialInstructions()))
                    .build()
                : null)
        .build();
  }

  /** Transform DatamartFacility address to version 1 facility address. */
  private static Facility.Address toFacilityAddress(
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

  /** Transform DatamartFacility addresses to version 1 facility addresses. */
  private static Facility.Addresses toFacilityAddresses(
      DatamartFacility.Addresses datamartFacilityAddresses) {
    return (datamartFacilityAddresses != null)
        ? Facility.Addresses.builder()
            .physical(toFacilityAddress(datamartFacilityAddresses.physical()))
            .mailing(toFacilityAddress(datamartFacilityAddresses.mailing()))
            .build()
        : Facility.Addresses.builder().build();
  }

  /** Transform DatamartFacility benefits service to version 1 facility benefits service object. */
  private static Facility.Service<Facility.BenefitsService> toFacilityBenefitsService(
      @NonNull
          DatamartFacility.Service<DatamartFacility.BenefitsService>
              datamartFacilityBenefitsService,
      @NonNull String linkUrl,
      @NonNull String facilityId) {
    final Optional<Facility.BenefitsService> benefitsService =
        Facility.BenefitsService.fromServiceId(datamartFacilityBenefitsService.serviceId());
    return benefitsService.isPresent()
        ? Facility.Service.<Facility.BenefitsService>builder()
            .serviceType(benefitsService.get())
            .name(datamartFacilityBenefitsService.name())
            .link(buildTypedServiceLink(linkUrl, facilityId, benefitsService.get().serviceId()))
            .build()
        : null;
  }

  /** Transform DatamartFacility health service to version 1 facility health service object. */
  public static Facility.Service<Facility.HealthService> toFacilityHealthService(
      @NonNull
          DatamartFacility.Service<DatamartFacility.HealthService> datamartFacilityHealthService,
      @NonNull String linkUrl,
      @NonNull String facilityId) {
    final Optional<Facility.HealthService> healthService =
        Facility.HealthService.fromServiceId(datamartFacilityHealthService.serviceId());
    return healthService.isPresent()
        ? Facility.Service.<Facility.HealthService>builder()
            .serviceType(healthService.get())
            .name(
                Facility.HealthService.isRecognizedServiceNameException(
                        datamartFacilityHealthService.name())
                    ? healthService.get().name()
                    : datamartFacilityHealthService.name())
            .link(buildTypedServiceLink(linkUrl, facilityId, healthService.get().serviceId()))
            .build()
        : null;
  }

  /** Transform DatamartFacility hours to version 1 facility hours. */
  private static Facility.Hours toFacilityHours(DatamartFacility.Hours datamartFacilityHours) {
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

  /** Transform DatamartFacility operating status to version 1 facility operating status. */
  public static Facility.OperatingStatus toFacilityOperatingStatus(
      DatamartFacility.OperatingStatus datamartFacilityOperatingStatus,
      DatamartFacility.ActiveStatus datamartFacilityActiveStatus) {
    return (datamartFacilityOperatingStatus != null)
        ? Facility.OperatingStatus.builder()
            .code(
                (datamartFacilityOperatingStatus.code() != null)
                    ? Facility.OperatingStatusCode.valueOf(
                        datamartFacilityOperatingStatus.code().name())
                    : null)
            .additionalInfo(datamartFacilityOperatingStatus.additionalInfo())
            .build()
        : determineOperatingStatusFromActiveStatus(datamartFacilityActiveStatus);
  }

  /** Transform DatamartFacility operating status to version 1 facility operating status. */
  public static Facility.OperatingStatus toFacilityOperatingStatus(
      DatamartFacility.OperatingStatus datamartFacilityOperatingStatus) {
    return (datamartFacilityOperatingStatus != null)
        ? Facility.OperatingStatus.builder()
            .code(
                (datamartFacilityOperatingStatus.code() != null)
                    ? Facility.OperatingStatusCode.valueOf(
                        datamartFacilityOperatingStatus.code().name())
                    : null)
            .additionalInfo(datamartFacilityOperatingStatus.additionalInfo())
            .supplementalStatuses(
                toFacilitySupplementalStatuses(
                    datamartFacilityOperatingStatus.supplementalStatuses()))
            .build()
        : null;
  }

  /**
   * Transform DatamartFacility operational hours special instructions to version 1 operational
   * hours special instructions.
   */
  public static List<String> toFacilityOperationalHoursSpecialInstructions(String instructions) {
    if (instructions != null) {
      return Stream.of(instructions.split("\\|")).map(String::trim).collect(Collectors.toList());
    } else {
      return null;
    }
  }

  /** Transform DatamartFacility other service to version 1 facility other service object. */
  private static Facility.Service<Facility.OtherService> toFacilityOtherService(
      @NonNull DatamartFacility.Service<DatamartFacility.OtherService> datamartFacilityOtherService,
      @NonNull String linkUrl,
      @NonNull String facilityId) {
    final Optional<Facility.OtherService> otherService =
        Facility.OtherService.fromServiceId(datamartFacilityOtherService.serviceId());
    return otherService.isPresent()
        ? Facility.Service.<Facility.OtherService>builder()
            .serviceType(otherService.get())
            .name(datamartFacilityOtherService.name())
            .link(buildTypedServiceLink(linkUrl, facilityId, otherService.get().serviceId()))
            .build()
        : null;
  }

  /** Transform DatamartFacility phone to version 1 facility phone. */
  private static Facility.Phone toFacilityPhone(DatamartFacility.Phone datamartFacilityPhone) {
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

  /** Transform DatamartFacility satisfaction to version 1 facility satisfaction. */
  private static Facility.Satisfaction toFacilitySatisfaction(
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

  /** Transform DatamartFacility services to version 1 facility services. */
  private static Facility.Services toFacilityServices(
      DatamartFacility.Services datamartFacilityServices,
      @NonNull String linkUrl,
      List<String> serviceSources,
      @NonNull String facilityId) {
    return (datamartFacilityServices != null)
        ? Facility.Services.builder()
            .health(
                (datamartFacilityServices.health() != null)
                    ? filterOutDuplicatesAndBuildFacilityHealthServices(
                        datamartFacilityServices.health().stream()
                            .filter(
                                hs ->
                                    (hs.source() != null
                                        && serviceSources.contains(hs.source().name())))
                            .collect(Collectors.toList()),
                        linkUrl,
                        facilityId)
                    : null)
            .benefits(
                (datamartFacilityServices.benefits() != null)
                    ? filterOutDuplicatesAndBuildFacilityBenefitsServices(
                        datamartFacilityServices.benefits().parallelStream()
                            .filter(
                                bs ->
                                    (bs.source() != null
                                        && serviceSources.contains(bs.source().name())))
                            .collect(Collectors.toList()),
                        linkUrl,
                        facilityId)
                    : null)
            .other(
                (datamartFacilityServices.other() != null)
                    ? filterOutDuplicatesAndBuildFacilityOtherServices(
                        datamartFacilityServices.other().parallelStream()
                            .filter(
                                os ->
                                    (os.source() != null
                                        && serviceSources.contains(os.source().name())))
                            .collect(Collectors.toList()),
                        linkUrl,
                        facilityId)
                    : null)
            .link(buildServicesLink(linkUrl, facilityId))
            .lastUpdated(datamartFacilityServices.lastUpdated())
            .build()
        : Facility.Services.builder().build();
  }

  /** Transform DatamartFacility supplemental status to version 1 facility supplemental status. */
  public static Facility.SupplementalStatus toFacilitySupplementalStatus(
      @NonNull DatamartFacility.SupplementalStatus datamartFacilitySupplementalStatus) {
    return Facility.SupplementalStatus.builder()
        .id(datamartFacilitySupplementalStatus.id())
        .label(datamartFacilitySupplementalStatus.label())
        .build();
  }

  /**
   * Transform list of DatamartFacility supplemental statuses to version 1 facility supplemental
   * statuses.
   */
  public static List<Facility.SupplementalStatus> toFacilitySupplementalStatuses(
      List<DatamartFacility.SupplementalStatus> datamartFacilitySupplementalStatuses) {
    if (datamartFacilitySupplementalStatuses != null) {
      return datamartFacilitySupplementalStatuses.parallelStream()
          .map(FacilityTransformerV1::toFacilitySupplementalStatus)
          .collect(Collectors.toList());
    }
    return null;
  }

  /** Transform DatamartFacility facility type to version 1 facility type. */
  private static Facility.FacilityType toFacilityType(
      DatamartFacility.FacilityType datamartFacilityType) {
    return (datamartFacilityType != null)
        ? Facility.FacilityType.valueOf(datamartFacilityType.name())
        : null;
  }

  /** Transform version 1 facility type to DatamartFacility facility type. */
  private static Facility.Type toType(DatamartFacility.Type datamartType) {
    return (datamartType != null) ? Facility.Type.valueOf(datamartType.name()) : null;
  }

  /** Transform version 1 facility to DatamartFacility for persistence. */
  public static DatamartFacility toVersionAgnostic(@NonNull Facility f) {
    return DatamartFacility.builder()
        .id(f.id())
        .type(toVersionAgnosticType(f.type()))
        .attributes(
            (f.attributes() != null)
                ? DatamartFacility.FacilityAttributes.builder()
                    .facilityType(toVersionAgnosticFacilityType(f.attributes().facilityType()))
                    .address(toVersionAgnosticFacilityAddresses(f.attributes().address()))
                    .hours(toVersionAgnosticFacilityHours(f.attributes().hours()))
                    .latitude(f.attributes().latitude())
                    .longitude(f.attributes().longitude())
                    .name(f.attributes().name())
                    .phone(toVersionAgnosticFacilityPhone(f.attributes().phone()))
                    .website(f.attributes().website())
                    .classification(f.attributes().classification())
                    .timeZone(f.attributes().timeZone())
                    .mobile(f.attributes().mobile())
                    .services(toVersionAgnosticFacilityServices(f.attributes().services()))
                    .visn(f.attributes().visn())
                    .satisfaction(
                        toVersionAgnosticFacilitySatisfaction(f.attributes().satisfaction()))
                    .operatingStatus(
                        toVersionAgnosticFacilityOperatingStatus(f.attributes().operatingStatus()))
                    .operationalHoursSpecialInstructions(
                        toVersionAgnosticFacilityOperationalHoursSpecialInstructions(
                            f.attributes().operationalHoursSpecialInstructions()))
                    .build()
                : null)
        .build();
  }

  /** Transform version 1 facility address to DatamartFacility address. */
  private static DatamartFacility.Address toVersionAgnosticFacilityAddress(
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

  /** Transform version 1 facility addresses to DatamartFacility addresses. */
  private static DatamartFacility.Addresses toVersionAgnosticFacilityAddresses(
      Facility.Addresses facilityAddresses) {
    return (facilityAddresses != null)
        ? DatamartFacility.Addresses.builder()
            .physical(toVersionAgnosticFacilityAddress(facilityAddresses.physical()))
            .mailing(toVersionAgnosticFacilityAddress(facilityAddresses.mailing()))
            .build()
        : DatamartFacility.Addresses.builder().build();
  }

  /** Transform version 1 facility benefits service to DatamartFacility benefits service object. */
  private static DatamartFacility.Service<DatamartFacility.BenefitsService>
      toVersionAgnosticFacilityBenefitsService(
          @NonNull Facility.Service<Facility.BenefitsService> facilityBenefitsService) {
    final Optional<DatamartFacility.BenefitsService> versionAgnosticBenefitsService =
        toVersionAgnosticFacilityBenefitsServiceType(facilityBenefitsService.serviceType());
    return versionAgnosticBenefitsService.isPresent()
        ? DatamartFacility.Service.<DatamartFacility.BenefitsService>builder()
            .serviceType(versionAgnosticBenefitsService.get())
            .name(facilityBenefitsService.name())
            .build()
        : null;
  }

  /**
   * Transform version 1 facility benefits service type to DatamartFacility benefits service type.
   */
  public static Optional<DatamartFacility.BenefitsService>
      toVersionAgnosticFacilityBenefitsServiceType(
          @NonNull Facility.BenefitsService facilityBenefitsService) {
    return DatamartFacility.BenefitsService.fromServiceId(facilityBenefitsService.serviceId());
  }

  /** Transform version 1 facility health service to DatamartFacility health service object. */
  public static DatamartFacility.Service<DatamartFacility.HealthService>
      toVersionAgnosticFacilityHealthService(
          @NonNull Facility.Service<Facility.HealthService> facilityHealthService) {
    final Optional<DatamartFacility.HealthService> versionAgnosticHealthService =
        toVersionAgnosticFacilityHealthServiceType(facilityHealthService.serviceType());
    return versionAgnosticHealthService.isPresent()
        ? DatamartFacility.Service.<DatamartFacility.HealthService>builder()
            .serviceType(versionAgnosticHealthService.get())
            .name(
                DatamartFacility.HealthService.isRecognizedServiceNameException(
                        facilityHealthService.name())
                    ? versionAgnosticHealthService.get().name()
                    : facilityHealthService.name())
            .build()
        : null;
  }

  /** Transform version 1 facility health service type to DatamartFacility health service type. */
  public static Optional<DatamartFacility.HealthService> toVersionAgnosticFacilityHealthServiceType(
      @NonNull Facility.HealthService facilityHealthService) {
    return DatamartFacility.HealthService.fromServiceId(facilityHealthService.serviceId());
  }

  /** Transform version 1 facility hours to DatamartFacility hours. */
  private static DatamartFacility.Hours toVersionAgnosticFacilityHours(
      Facility.Hours facilityHours) {
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

  /** Transform version 1 facility operating status to DatamartFacility operating status. */
  public static DatamartFacility.OperatingStatus toVersionAgnosticFacilityOperatingStatus(
      Facility.OperatingStatus facilityOperatingStatus) {
    return (facilityOperatingStatus != null)
        ? DatamartFacility.OperatingStatus.builder()
            .code(
                (facilityOperatingStatus.code() != null)
                    ? DatamartFacility.OperatingStatusCode.valueOf(
                        facilityOperatingStatus.code().name())
                    : null)
            .additionalInfo(facilityOperatingStatus.additionalInfo())
            .supplementalStatuses(
                toVersionAgnosticSupplementalStatuses(
                    facilityOperatingStatus.supplementalStatuses()))
            .build()
        : null;
  }

  /**
   * Transform Facility operational hours special instructions to version agnostic operational hours
   * special instructions.
   */
  public static String toVersionAgnosticFacilityOperationalHoursSpecialInstructions(
      List<String> instructions) {
    if (instructions != null) {
      return String.join(" | ", instructions);
    } else {
      return null;
    }
  }

  /** Transform version 1 facility other service to DatamartFacility other service object. */
  private static DatamartFacility.Service<DatamartFacility.OtherService>
      toVersionAgnosticFacilityOtherService(
          @NonNull Facility.Service<Facility.OtherService> facilityOtherService) {
    final Optional<DatamartFacility.OtherService> versionAgnosticOtherService =
        toVersionAgnosticFacilityOtherServiceType(facilityOtherService.serviceType());
    return versionAgnosticOtherService.isPresent()
        ? DatamartFacility.Service.<DatamartFacility.OtherService>builder()
            .serviceType(versionAgnosticOtherService.get())
            .name(facilityOtherService.name())
            .build()
        : null;
  }

  /** Transform version 1 facility other service type to DatamartFacility other service type. */
  public static Optional<DatamartFacility.OtherService> toVersionAgnosticFacilityOtherServiceType(
      @NonNull Facility.OtherService facilityOtherService) {
    return DatamartFacility.OtherService.fromServiceId(facilityOtherService.serviceId());
  }

  /** Transform version 1 facility phone to DatamartFacility phone. */
  private static DatamartFacility.Phone toVersionAgnosticFacilityPhone(
      Facility.Phone facilityPhone) {
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

  /** Transform version 1 facility satisfaction to DatamartFacility satisfaction. */
  private static DatamartFacility.Satisfaction toVersionAgnosticFacilitySatisfaction(
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

  /** Transform version 1 facility services to DatamartFacility services. */
  private static DatamartFacility.Services toVersionAgnosticFacilityServices(
      Facility.Services facilityServices) {
    return (facilityServices != null)
        ? DatamartFacility.Services.builder()
            .health(
                (facilityServices.health() != null)
                    ? facilityServices.health().parallelStream()
                        .map(FacilityTransformerV1::toVersionAgnosticFacilityHealthService)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
                    : null)
            .benefits(
                (facilityServices.benefits() != null)
                    ? facilityServices.benefits().parallelStream()
                        .map(FacilityTransformerV1::toVersionAgnosticFacilityBenefitsService)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
                    : null)
            .other(
                (facilityServices.other() != null)
                    ? facilityServices.other().parallelStream()
                        .map(FacilityTransformerV1::toVersionAgnosticFacilityOtherService)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
                    : null)
            .lastUpdated(facilityServices.lastUpdated())
            .build()
        : DatamartFacility.Services.builder().build();
  }

  /** Transform version 1 facility type to DatamartFacility facility type. */
  private static DatamartFacility.FacilityType toVersionAgnosticFacilityType(
      Facility.FacilityType facilityType) {
    return (facilityType != null)
        ? DatamartFacility.FacilityType.valueOf(facilityType.name())
        : null;
  }

  /** Transform version 1 facility supplemental status to DatamartFacility supplemental status. */
  public static DatamartFacility.SupplementalStatus toVersionAgnosticSupplementalStatus(
      @NonNull Facility.SupplementalStatus facilitySupplementalStatus) {
    return DatamartFacility.SupplementalStatus.builder()
        .id(facilitySupplementalStatus.id())
        .label(facilitySupplementalStatus.label())
        .build();
  }

  /**
   * Transform list of version 1 facility supplemental statuses to DatamartFacility supplemental
   * statuses.
   */
  public static List<DatamartFacility.SupplementalStatus> toVersionAgnosticSupplementalStatuses(
      List<Facility.SupplementalStatus> facilitySupplementalStatuses) {
    if (facilitySupplementalStatuses != null) {
      return facilitySupplementalStatuses.parallelStream()
          .map(FacilityTransformerV1::toVersionAgnosticSupplementalStatus)
          .collect(Collectors.toList());
    }
    return null;
  }

  /** Transform DatamartFacility type to version 1 facility type. */
  private static DatamartFacility.Type toVersionAgnosticType(Facility.Type type) {
    return (type != null) ? DatamartFacility.Type.valueOf(type.name()) : null;
  }

  private List<Facility.Service<Facility.BenefitsService>>
      filterOutDuplicatesAndBuildFacilityBenefitsServices(
          @NonNull List<Service<BenefitsService>> datamartBenefitsServices,
          @NonNull String linkUrl,
          @NonNull String facilityId) {
    // Obtain CMS service ids
    final var cmsServiceIds =
        datamartBenefitsServices.parallelStream()
            .filter(dhs -> Source.CMS.equals(dhs.source()))
            .map(dhs -> dhs.serviceId())
            .collect(Collectors.toList());
    // Remove ATC, DST, BISL, and internal services for which there is CMS benefits service at
    // facility
    cmsServiceIds.stream()
        .forEach(
            cmsServiceId -> {
              datamartBenefitsServices.removeIf(
                  dhs -> dhs.serviceId().equals(cmsServiceId) && !dhs.source().equals(Source.CMS));
            });
    // Build facility benefits services
    return datamartBenefitsServices.parallelStream()
        .map(dhs -> toFacilityBenefitsService(dhs, linkUrl, facilityId))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private List<Facility.Service<Facility.HealthService>>
      filterOutDuplicatesAndBuildFacilityHealthServices(
          @NonNull List<Service<HealthService>> datamartHealthServices,
          @NonNull String linkUrl,
          @NonNull String facilityId) {
    // Obtain CMS service ids
    final var cmsServiceIds =
        datamartHealthServices.parallelStream()
            .filter(dhs -> Source.CMS.equals(dhs.source()))
            .map(dhs -> dhs.serviceId())
            .collect(Collectors.toList());
    // Remove ATC, DST, BISL, and internal services for which there is CMS health service at
    // facility
    cmsServiceIds.stream()
        .forEach(
            cmsServiceId -> {
              datamartHealthServices.removeIf(
                  dhs -> dhs.serviceId().equals(cmsServiceId) && !dhs.source().equals(Source.CMS));
            });
    // Build facility health services
    return datamartHealthServices.parallelStream()
        .map(dhs -> toFacilityHealthService(dhs, linkUrl, facilityId))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private List<Facility.Service<Facility.OtherService>>
      filterOutDuplicatesAndBuildFacilityOtherServices(
          @NonNull List<Service<OtherService>> datamartOtherServices,
          @NonNull String linkUrl,
          @NonNull String facilityId) {
    // Obtain CMS service ids
    final var cmsServiceIds =
        datamartOtherServices.parallelStream()
            .filter(dhs -> Source.CMS.equals(dhs.source()))
            .map(dhs -> dhs.serviceId())
            .collect(Collectors.toList());
    // Remove ATC, DST, BISL, and internal services for which there is CMS other service at facility
    cmsServiceIds.stream()
        .forEach(
            cmsServiceId -> {
              datamartOtherServices.removeIf(
                  dhs -> dhs.serviceId().equals(cmsServiceId) && !dhs.source().equals(Source.CMS));
            });
    // Build facility other services
    return datamartOtherServices.parallelStream()
        .map(dhs -> toFacilityOtherService(dhs, linkUrl, facilityId))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }
}
