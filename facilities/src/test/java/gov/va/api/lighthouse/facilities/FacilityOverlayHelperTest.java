package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

public class FacilityOverlayHelperTest {
  @Test
  void filterOutInvalidDetailedServices() {
    String detailedServicesJson =
        "[\n"
            + "    {\n"
            + "      \"name\":\"ApplyingForBenefits\",\n"
            + "\t   \"serviceId\": \"applyingForBenefits\",\n"
            + "      \"active\":true,\n"
            + "      \"description_facility\":\"This is not null\",\n"
            + "      \"health_service_api_id\":null,\n"
            + "      \"appointment_leadin\":\"Your VA health care team will contact you if you...more text\",\n"
            + "      \"online_scheduling_available\": \"True\",\n"
            + "      \"path\": \"\\/erie-health-care\\/locations\\/erie-va-medical-center\\/covid-19-vaccines\",\n"
            + "      \"appointment_phones\": [\n"
            + "        {\n"
            + "          \"type\": \"tel\",\n"
            + "          \"label\": \"Main phone\",\n"
            + "          \"number\": \"555-555-1212\",\n"
            + "          \"extension\": \"123\" \n"
            + "        }\n"
            + "      ],\n"
            + "      \"referral_required\": \"False\",\n"
            + "      \"service_locations\": null,\n"
            + "      \"walk_ins_accepted\": \"True\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"name\":\"Invalid\",\n"
            + "     \"serviceId\": \"invalid\",\n"
            + "      \"active\":true,\n"
            + "      \"description_facility\":\"This is not null\",\n"
            + "      \"health_service_api_id\":null,\n"
            + "      \"appointment_leadin\":\"Your VA health care team will contact you if you...more text\",\n"
            + "      \"online_scheduling_available\": \"True\",\n"
            + "      \"path\": \"\\/erie-health-care\\/locations\\/erie-va-medical-center\\/covid-19-vaccines\",\n"
            + "      \"appointment_phones\": [\n"
            + "        {\n"
            + "          \"type\": \"tel\",\n"
            + "          \"label\": \"Main phone\",\n"
            + "          \"number\": \"555-555-1212\",\n"
            + "          \"extension\": \"123\" \n"
            + "        }\n"
            + "      ],\n"
            + "      \"referral_required\": \"False\",\n"
            + "      \"service_locations\": null,\n"
            + "      \"walk_ins_accepted\": \"True\"\n"
            + "    }\n"
            + "    ]";

    List<DatamartDetailedService> datamartDetailedServices =
        CmsOverlayHelper.getDetailedServices(detailedServicesJson);
    DatamartFacility datamartFacility =
        DatamartFacility.builder()
            .attributes(
                DatamartFacility.FacilityAttributes.builder()
                    .detailedServices(datamartDetailedServices)
                    .build())
            .build();
    assertThat(
            FacilityOverlayHelper.filterOutInvalidDetailedServices(datamartFacility)
                .attributes()
                .detailedServices())
        .hasSize(1);
  }
}
