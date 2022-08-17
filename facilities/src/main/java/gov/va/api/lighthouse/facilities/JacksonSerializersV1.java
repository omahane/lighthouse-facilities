package gov.va.api.lighthouse.facilities;

import com.fasterxml.jackson.databind.module.SimpleModule;
import gov.va.api.lighthouse.facilities.api.v1.CmsOverlay;
import gov.va.api.lighthouse.facilities.api.v1.CmsOverlayResponse;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.AppointmentPhoneNumber;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.DetailedServiceAddress;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.DetailedServiceEmailContact;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.DetailedServiceHours;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.DetailedServiceLocation;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.PatientWaitTime;
import gov.va.api.lighthouse.facilities.api.v1.DetailedService.ServiceInfo;
import gov.va.api.lighthouse.facilities.api.v1.DetailedServiceResponse;
import gov.va.api.lighthouse.facilities.api.v1.DetailedServicesResponse;
import gov.va.api.lighthouse.facilities.api.v1.FacilitiesIdsResponse;
import gov.va.api.lighthouse.facilities.api.v1.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v1.FacilitiesResponse.FacilitiesMetadata;
import gov.va.api.lighthouse.facilities.api.v1.Facility;
import gov.va.api.lighthouse.facilities.api.v1.Facility.Address;
import gov.va.api.lighthouse.facilities.api.v1.Facility.Addresses;
import gov.va.api.lighthouse.facilities.api.v1.Facility.FacilityAttributes;
import gov.va.api.lighthouse.facilities.api.v1.Facility.Hours;
import gov.va.api.lighthouse.facilities.api.v1.Facility.OperatingStatus;
import gov.va.api.lighthouse.facilities.api.v1.Facility.PatientSatisfaction;
import gov.va.api.lighthouse.facilities.api.v1.Facility.Phone;
import gov.va.api.lighthouse.facilities.api.v1.Facility.Satisfaction;
import gov.va.api.lighthouse.facilities.api.v1.Facility.Services;
import gov.va.api.lighthouse.facilities.api.v1.FacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v1.NearbyResponse;
import gov.va.api.lighthouse.facilities.api.v1.NearbyResponse.Meta;
import gov.va.api.lighthouse.facilities.api.v1.NearbyResponse.Nearby;
import gov.va.api.lighthouse.facilities.api.v1.NearbyResponse.NearbyAttributes;
import gov.va.api.lighthouse.facilities.api.v1.PageLinks;
import gov.va.api.lighthouse.facilities.api.v1.Pagination;
import gov.va.api.lighthouse.facilities.api.v1.ReloadResponse;
import gov.va.api.lighthouse.facilities.api.v1.ReloadResponse.Problem;
import gov.va.api.lighthouse.facilities.api.v1.ReloadResponse.Timing;
import gov.va.api.lighthouse.facilities.api.v1.serializers.AddressSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.AddressesSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.CmsOverlayResponseSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.CmsOverlaySerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.DetailedServiceAddressSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.DetailedServiceAppointmentPhoneNumberSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.DetailedServiceEmailContactSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.DetailedServiceHoursSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.DetailedServiceInfoSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.DetailedServiceLocationSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.DetailedServiceResponseSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.DetailedServiceSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.DetailedServicesMetadataSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.DetailedServicesResponseSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.FacilitiesIdsResponseSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.FacilitiesMetadataSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.FacilitiesResponseSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.FacilityAttributesSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.FacilityReadResponseSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.FacilitySerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.HoursSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.MetaSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.NearbyAttributesSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.NearbyResponseSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.NearbySerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.OperatingStatusSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.PageLinksSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.PaginationSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.PatientSatisfactionSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.PatientWaitTimeSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.PhoneSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.ReloadResponseProblemSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.ReloadResponseSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.ReloadResponseTimingSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.SatisfactionSerializer;
import gov.va.api.lighthouse.facilities.api.v1.serializers.ServicesSerializer;
import lombok.experimental.UtilityClass;

@UtilityClass
final class JacksonSerializersV1 {

  /** Custom serialization rules for V1 API classes. */
  static SimpleModule serializersV1() {
    SimpleModule mod = new SimpleModule();
    mod.addSerializer(Address.class, new AddressSerializer());
    mod.addSerializer(Addresses.class, new AddressesSerializer());
    mod.addSerializer(Facility.class, new FacilitySerializer());
    mod.addSerializer(FacilitiesMetadata.class, new FacilitiesMetadataSerializer());
    mod.addSerializer(FacilityAttributes.class, new FacilityAttributesSerializer());
    mod.addSerializer(Hours.class, new HoursSerializer());
    mod.addSerializer(OperatingStatus.class, new OperatingStatusSerializer());
    mod.addSerializer(PatientWaitTime.class, new PatientWaitTimeSerializer());
    mod.addSerializer(PatientSatisfaction.class, new PatientSatisfactionSerializer());
    mod.addSerializer(Phone.class, new PhoneSerializer());
    mod.addSerializer(Satisfaction.class, new SatisfactionSerializer());
    mod.addSerializer(Services.class, new ServicesSerializer());
    mod.addSerializer(PageLinks.class, new PageLinksSerializer());
    mod.addSerializer(DetailedService.class, new DetailedServiceSerializer());
    mod.addSerializer(ServiceInfo.class, new DetailedServiceInfoSerializer());
    mod.addSerializer(DetailedServiceAddress.class, new DetailedServiceAddressSerializer());
    mod.addSerializer(
        AppointmentPhoneNumber.class, new DetailedServiceAppointmentPhoneNumberSerializer());
    mod.addSerializer(
        DetailedServiceEmailContact.class, new DetailedServiceEmailContactSerializer());
    mod.addSerializer(DetailedServiceHours.class, new DetailedServiceHoursSerializer());
    mod.addSerializer(DetailedServiceLocation.class, new DetailedServiceLocationSerializer());
    mod.addSerializer(DetailedServiceResponse.class, new DetailedServiceResponseSerializer());
    mod.addSerializer(DetailedServicesResponse.class, new DetailedServicesResponseSerializer());
    mod.addSerializer(
        DetailedServicesResponse.DetailedServicesMetadata.class,
        new DetailedServicesMetadataSerializer());
    mod.addSerializer(FacilitiesResponse.class, new FacilitiesResponseSerializer());
    mod.addSerializer(FacilitiesIdsResponse.class, new FacilitiesIdsResponseSerializer());
    mod.addSerializer(FacilityReadResponse.class, new FacilityReadResponseSerializer());
    mod.addSerializer(NearbyResponse.class, new NearbyResponseSerializer());
    mod.addSerializer(NearbyAttributes.class, new NearbyAttributesSerializer());
    mod.addSerializer(Nearby.class, new NearbySerializer());
    mod.addSerializer(Meta.class, new MetaSerializer());
    mod.addSerializer(Pagination.class, new PaginationSerializer());
    mod.addSerializer(ReloadResponse.class, new ReloadResponseSerializer());
    mod.addSerializer(Problem.class, new ReloadResponseProblemSerializer());
    mod.addSerializer(Timing.class, new ReloadResponseTimingSerializer());
    mod.addSerializer(CmsOverlay.class, new CmsOverlaySerializer());
    mod.addSerializer(CmsOverlayResponse.class, new CmsOverlayResponseSerializer());
    return mod;
  }
}
