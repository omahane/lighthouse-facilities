package gov.va.api.lighthouse.facilities.api.v0;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition(
    info =
        @Info(
            version = "0.0.1",
            title = "VA Facilities",
            description =
                "## Background"
                    + "\n\n"
                    + "The VA Facilities API version 0 (v0) provides "
                    + "information about VA facilities, including "
                    + "locations, addresses, phone numbers, "
                    + "hours of operation, and available services."
                    + "\n\n"
                    + "This API gives information about "
                    + "several types of VA facilities:\n"
                    + "- Health facilities (vha)\n"
                    + "- Benefits facilities (vba)\n"
                    + "- Cemeteries (nca)\n"
                    + "- Vet Centers (vc)"
                    + "\n\n"
                    + "## Technical overview"
                    + "\n\n"
                    + "For in-depth details on data sources "
                    + "for the Facilities API"
                    + ", [read our Facilities GitHub documentation]"
                    + "(https://github.com/department-of-veterans-affairs/lighthouse-facilities#readme)."
                    + "\n\n"
                    + "Health service and service availability "
                    + "data for v0 of this API are based on historical data."
                    + "\n\n"
                    + "- Historical data is returned for the previous 30 days.\n"
                    + "- Data is based on both pending and "
                    + "completed appointments for a service at a given facility.\n"
                    + "\n\n"
                    + "### Authentication and Authorization"
                    + "\n\n"
                    + "VA Facilities is an open data API. Open data API "
                    + "requests are authorized through a symmetric API "
                    + "token that’s provided in an HTTP header "
                    + "with the name 'apikey'."
                    + "\n\n"
                    + "### Test data"
                    + "\n\n"
                    + "Test data for the sandbox environment is only "
                    + "for testing the API and is not guaranteed to be "
                    + "up-to-date. After testing this API in sandbox, "
                    + "you can start the process of moving to production."
                    + "\n\n"
                    + "### Facility ID formats"
                    + "\n\n"
                    + "A facility ID has the format prefix_stationNumber. "
                    + "The prefix is nca, vc, vba, or vha. Cemeteries "
                    + "may be VA national cemeteries or non-national; "
                    + "non-national cemeteries have the station number "
                    + "prefixed with an s. There are no other "
                    + "constraints on the format. "
                    + "Some facility ID examples are:\n"
                    + "- Health: `vha_402GA`\n"
                    + "- Benefits: `vba_539GB`\n"
                    + "- National cemetery: `nca_063`\n"
                    + "- Non-national cemetery: `nca_s1082`\n"
                    + "- Vet Center: `vc_0872MVC`\n"
                    + "\n\n"
                    + "### Mobile facilities"
                    + "\n\n"
                    + "The mobile health facilities move regularly "
                    + "within a region. If a facility comes back "
                    + "from this API with \"mobile\": \"true\", the "
                    + "latitude/longitude and address could be inaccurate."
                    + " To get the exact current location, "
                    + "please call the mobile facility number listed.",
            contact = @Contact(name = "developer.va.gov")),
    tags = @Tag(name = "facilities", description = "VA Facilities API"))
public interface FacilitiesService
    extends FacilitiesAllApi,
        FacilitiesReadApi,
        FacilitiesSearchApi,
        FacilitiesIdsApi,
        FacilitiesNearbyApi {}
