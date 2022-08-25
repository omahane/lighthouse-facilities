package gov.va.api.lighthouse.facilities.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.math.BigDecimal;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

public interface FacilitiesSearchApi {
  @Operation(
      summary = "Query facilities by location or IDs, with optional filters",
      description =
<<<<<<< HEAD
          "Query facilities using bounding box, latitude and longitude, state, visn, zip code, "
              + "facility type, facility ids, available services, and mobile status. "
              + "To bulk download all facilities, simply query without any parameters. "
              + "Bounding box is specified as four `bbox[]` parameters, long1, lat1, long2, lat2. "
              + "(Relative order is unimportant.)"
              + "\n\n"
              + "A query by latitude and longitude returns all facilities matching "
              + "other given parameters, sorted by distance from that location. "
              + "Providing an optional radius in miles to this query will narrow "
              + "the scope of the returned facilities to those falling "
              + "within the specified radius from that location."
              + "\n\n"
              + "If one wishes to only get facilities with specific ids, simply include "
              + "a comma separated list like `?facilityIds=id1,id2`. "
              + "When requesting multiple facilities by ID, the API will return "
              + "as many results as it can find matches for given other included parameters, "
              + "omitting ids where there is no match. "
=======
          "Query facilities by bounding box, latitude and longitude, state, visn, or zip code, "
              + "or include no parameters to bulk download all facilities. "
              + "Bounding box is specified as four `bbox[]` parameters, long1, lat1, long2, lat2. "
              + "(Relative order is unimportant.)"
              + "\n\n"
              + "A query by latitude and longitude returns all facilities in the system, "
              + "sorted by distance from that location. Providing an optional radius in miles to "
              + "this query will narrow the scope of the returned facilities to those falling "
              + "within the specified radius from that location."
              + "\n\n"
              + "All location queries support filtering by facility type, available services, and"
              + " mobile status."
              + "\n\n"
              + "One can also retrieve facilities by ID using a comma-separated "
              + "list like `?ids=id1,id2`. "
              + "When requesting multiple facilities by ID, the API will return "
              + "as many results as it can find matches for, omitting IDs where "
              + "there is no match. "
>>>>>>> parent of 90213680 (Rollback to 3.0.89 baseline with selected updates)
              + "It will not return an HTTP error code if it is unable to match a requested ID. "
              + "Clients may supply IDs up to the limit their HTTP client enforces for "
              + "URI path lengths. (Usually 2048 characters.)"
              + "\n\n"
              + "Results are paginated. "
              + "JSON responses include pagination information in the standard JSON API "
              + "\"links\" and \"meta\" elements. "
              + "\n\n"
              + "### Parameter combinations\n"
              + "You may optionally specify `page` and `per_page` with any query. "
<<<<<<< HEAD
              + "You can query with any combination of the following: "
              + "\n\n"
              + "- `bbox[]`"
              + "\n\n"
              + "- `ids`"
              + "\n\n"
              + "- `lat` and `long`, with the option to filter by `radius`"
              + "\n\n"
              + "- `state`"
              + "\n\n"
              + "- `visn`"
              + "\n\n"
              + "- `zip`"
              + "\n\n"
              + "- `facilityIds`"
              + "\n\n"
              + "- `type`"
              + "\n\n"
              + "- `services[]`"
              + "\n\n"
              + "- `mobile`"
=======
              + "You must specify one of the following parameter combinations: "
              + "\n\n"
              + "- `bbox[]`, with the option of any combination of `type`, `services[]`, or"
              + " `mobile`"
              + "\n\n"
              + "- `ids`"
              + "\n\n"
              + "- `lat` and `long`, with the option "
              + "of any combination of `radius`, `ids`, `type`, `services[]`, or `mobile`"
              + "\n\n"
              + "- `state`, with the option of any combination of `type`, `services[]`, or"
              + " `mobile`"
              + "\n\n"
              + "- `visn`"
              + "\n\n"
              + "- `zip`, with the option of any combination of `type`, `services[]`, or"
              + " `mobile`"
>>>>>>> parent of 90213680 (Rollback to 3.0.89 baseline with selected updates)
              + "\n\n"
              + " Invalid combinations will return `400 Bad Request`. ",
      tags = {"facilities"},
      operationId = "getFacilitiesByLocation",
      security = @SecurityRequirement(name = "apikey"))
  @GET
  @Path("/facilities")
  @ApiResponse(
      responseCode = "200",
      description = "Success",
      content = {
        @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = FacilitiesResponse.class)),
      })
  @ApiResponse(
      responseCode = "401",
      description = "Missing API token",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = GenericError.class)))
  @ApiResponse(
      responseCode = "403",
      description = "Invalid API token",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = GenericError.class)))
  @ApiResponse(
      responseCode = "406",
      description = "Requested format unacceptable",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ApiError.class)))
  @ApiResponse(
      responseCode = "429",
      description = "API rate limit exceeded",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ApiError.class)))
  FacilitiesResponse getFacilitiesByLocation(
      @Parameter(
<<<<<<< HEAD
              name = "facilityIds",
              description = "List of comma-separated facility IDs",
              in = ParameterIn.QUERY,
              style = ParameterStyle.FORM,
              explode = Explode.FALSE,
              examples = @ExampleObject(name = "facilityIds", value = "[\"vha_688\",\"vha_644\"]"))
=======
              name = "ids",
              description =
                  "List of comma-separated facility IDs to retrieve in a single request. "
                      + "Can be combined with lat and long parameters to retrieve facilities "
                      + "sorted by distance from a location.",
              in = ParameterIn.QUERY,
              style = ParameterStyle.FORM,
              explode = Explode.FALSE,
              examples = @ExampleObject(name = "ids", value = "[\"vha_688\",\"vha_644\"]"))
>>>>>>> parent of 90213680 (Rollback to 3.0.89 baseline with selected updates)
          List<String> id,
      @Parameter(
              name = "zip",
              in = ParameterIn.QUERY,
              description =
                  "Zip code to search for facilities. "
                      + "More detailed zip codes can be passed in, but only the first five "
                      + "digits are used to determine facilities to return.",
              schema = @Schema(format = "##### or #####-####"),
              examples = @ExampleObject(name = "zip", value = "80301-1000"))
          String zip,
      @Parameter(
              name = "state",
              in = ParameterIn.QUERY,
              description =
                  "State in which to search for facilities. "
                      + "Except in rare cases, this is two characters.",
              schema = @Schema(format = "XX"),
              examples = @ExampleObject(name = "state", value = "CO"))
          String state,
      @Parameter(
              name = "lat",
              in = ParameterIn.QUERY,
              description =
                  "Latitude of point to search for facilities, "
<<<<<<< HEAD
                      + "in WGS84 coordinate reference system."
                      + "Must be accompanied by a valid longitude.",
=======
                      + "in WGS84 coordinate reference system.",
>>>>>>> parent of 90213680 (Rollback to 3.0.89 baseline with selected updates)
              schema = @Schema(type = "number", format = "float"),
              examples = @ExampleObject(name = "coordinates", value = "56.7"))
          BigDecimal lat,
      @Parameter(
              name = "long",
              in = ParameterIn.QUERY,
              description =
                  "Longitude of point to search for facilities, "
<<<<<<< HEAD
                      + "in WGS84 coordinate reference system."
                      + "Must be accompanied by a valid latitude.",
=======
                      + "in WGS84 coordinate reference system.",
>>>>>>> parent of 90213680 (Rollback to 3.0.89 baseline with selected updates)
              style = ParameterStyle.FORM,
              explode = Explode.TRUE,
              schema = @Schema(type = "number", format = "float"),
              examples = @ExampleObject(name = "coordinates", value = "-123.4"))
          BigDecimal lng,
      @Parameter(
              name = "radius",
              in = ParameterIn.QUERY,
              description =
                  "Optional radial distance from specified latitude and longitude to "
<<<<<<< HEAD
                      + "filter facilities search in WGS84 coordinate reference system."
                      + "Must be accompanied by a valid latitude and longitude.",
=======
                      + "filter facilities search in WGS84 coordinate reference system.",
>>>>>>> parent of 90213680 (Rollback to 3.0.89 baseline with selected updates)
              style = ParameterStyle.FORM,
              explode = Explode.TRUE,
              schema = @Schema(type = "number", format = "float"),
              examples = @ExampleObject(name = "distance", value = "75.0"))
          BigDecimal radius,
      @Parameter(
              name = "bbox[]",
              in = ParameterIn.QUERY,
              description =
                  "Bounding box (longitude, latitude, longitude, latitude) "
                      + "within which facilities will be returned. "
                      + "(WGS84 coordinate reference system)",
              style = ParameterStyle.FORM,
              explode = Explode.TRUE,
              array =
                  @ArraySchema(
                      minItems = 4,
                      maxItems = 4,
                      schema = @Schema(type = "number", format = "float")),
              examples = @ExampleObject(name = "bbox", value = "[-105.4, 39.4, -104.5, 40.1]"))
          List<BigDecimal> bbox,
      @Parameter(
              name = "visn",
              in = ParameterIn.QUERY,
              description = "VISN search of matching facilities.",
              schema = @Schema(type = "number"))
          String visn,
      @Parameter(
              name = "type",
<<<<<<< HEAD
              description = "Type of facility location",
=======
              description = "Optional facility type search filter",
>>>>>>> parent of 90213680 (Rollback to 3.0.89 baseline with selected updates)
              in = ParameterIn.QUERY,
              schema =
                  @Schema(
                      type = "string",
                      allowableValues = {"health", "cemetery", "benefits", "vet_center"}))
          String type,
      @Parameter(
              name = "services[]",
<<<<<<< HEAD
              description = "List of services a facility offers",
=======
              description = "Optional facility service search filter",
>>>>>>> parent of 90213680 (Rollback to 3.0.89 baseline with selected updates)
              in = ParameterIn.QUERY,
              style = ParameterStyle.FORM,
              explode = Explode.TRUE)
          List<String> services,
      @Parameter(
              name = "mobile",
              in = ParameterIn.QUERY,
<<<<<<< HEAD
              description = "Boolean flag to include or exclude mobile facilities",
              schema = @Schema(type = "Boolean"),
              examples = @ExampleObject(name = "mobile", value = "True"))
=======
              description = "Optional facility mobile search filter",
              schema = @Schema(type = "Boolean"))
>>>>>>> parent of 90213680 (Rollback to 3.0.89 baseline with selected updates)
          Boolean mobile,
      @Parameter(
              name = "page",
              description = "Page of results to return per paginated response.",
              in = ParameterIn.QUERY,
              schema = @Schema(type = "integer", defaultValue = "1"))
          Integer page,
      @Parameter(
              name = "per_page",
              description = "Number of results to return per paginated response.",
              in = ParameterIn.QUERY,
              schema = @Schema(type = "integer", defaultValue = "10"))
          Integer perPage);
}
