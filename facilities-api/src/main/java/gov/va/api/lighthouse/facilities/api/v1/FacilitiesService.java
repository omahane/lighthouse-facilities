package gov.va.api.lighthouse.facilities.api.v1;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.ws.rs.Path;

@OpenAPIDefinition(
    info =
        @Info(
            version = "1.0.0",
            title = "VA Facilities",
            description =
                "## Background"
                    + "\n\n"
                    + "The VA Facilities API version 1 (v1) "
                    + "provides information about VA facilities, "
                    + "including locations, addresses, "
                    + "phone numbers, hours of operation,"
                    + " and available services."
                    + "\n\n"
                    + "VA operates several different types of facilities. "
                    + "This API gives information about:\n"
                    + "- Health facilities (vha)\n"
                    + "- Benefits facilities (vba)\n"
                    + "- Cemeteries (nca)\n"
                    + "- Vet Centers (vc)"
                    + "\n\n"
                    + "## Technical Overview"
                    + "\n\n"
                    + "For in-depth details on data sources for the Facilities API, "
                    + "[read our Facilities GitHub documentation]"
                    + "(https://github.com/department-of-veterans-affairs/lighthouse-facilities#readme)."
                    + "\n\n"
                    + "Health service data for v1 of this API is based on "
                    + "both real-time and historical data."
                    + "\n\n"
                    + "- Historical data is returned for the previous 30 days. "
                    + "Data is based on both pending and completed "
                    + "appointments for a given facility.\n"
                    + "- Service-related data may be added, removed, or modified "
                    + "by an authorized individual at the data facility. "
                    + "These data changes are available to v1 of this API in real time.\n"
                    + "\n\n"
                    + "### Authentication and Authorization"
                    + "\n\n"
                    + "VA Facilities is an open data API. "
                    + "Open data API requests are authorized through "
                    + "a symmetric API token that's provided "
                    + "in an HTTP header with the name 'apikey'."
                    + "\n\n"
                    + "### Test data"
                    + "\n\n"
                    + "Test data for the sandbox environment is only for "
                    + "testing the API and is not guaranteed to be up-to-date. "
                    + "After testing this API in sandbox, "
                    + "you can start the process of moving to production."
                    + "\n\n"
                    + "### Facility ID formats"
                    + "\n\n"
                    + "A facility ID has the format prefix_stationNumber. "
                    + "The prefix is nca, vc, vba, or vha. Cemeteries may be VA "
                    + "national cemeteries or non-national; non-national cemeteries "
                    + "have the station number prefixed with an s. "
                    + "There are no other constraints on the format. "
                    + "Some facility ID examples are:\n"
                    + "- Health: `vha_402GA`\n"
                    + "- Benefits: `vba_539GB`\n"
                    + "- National cemetery: `nca_063`\n"
                    + "- Non-national cemetery: `nca_s1082`\n"
                    + "- Vet Center: `vc_0872MVC`\n"
                    + "\n\n### Mobile Facilities\n\n"
                    + "The mobile health facilities move regularly within "
                    + "a region. If a facility comes back from this API "
                    + "with \"mobile\": \"true\", the latitude/longitude "
                    + "and address could be inaccurate. To get the exact"
                    + " current location, please call the mobile facility"
                    + " number listed.",
            contact = @Contact(name = "developer.va.gov")),
    tags = @Tag(name = "facilities", description = "VA Facilities API"))
@Path("/")
public interface FacilitiesService
    extends FacilitiesSearchApi,
        FacilitiesReadApi,
        DetailedServicesApi,
        DetailedServiceApi,
        FacilitiesIdsApi,
        FacilitiesNearbyApi {}
