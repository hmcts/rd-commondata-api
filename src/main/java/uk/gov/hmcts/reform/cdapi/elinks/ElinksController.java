package uk.gov.hmcts.reform.cdapi.elinks;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@Slf4j
@RequestMapping(
    path = "/refdata/elinks/api/v4/proxy"
)
public class ElinksController {

    @Autowired
    ElinkService elinkService;


    @ApiOperation(
        value = "Elinks API will be used to retrieve the list of location.",

        authorizations = {
            @Authorization(value = "ServiceAuthorization")
        }
    )
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "Successfully retrieved list of locations from eLinks",
            response = Object.class
        ),
        @ApiResponse(
            code = 400,
            message = "Bad Request"
        ),
        @ApiResponse(
            code = 401,
            message = "Forbidden Error: Access denied"
        ),
        @ApiResponse(
            code = 500,
            message = "Internal Server Error"
        )
    })
    @GetMapping("/location")
    public ResponseEntity<Object> getLocations() {
        return elinkService.retrieveLocation();
    }

    @ApiOperation(
        value = "Elinks API will be used to retrieve the list of base location.",

        authorizations = {
            @Authorization(value = "ServiceAuthorization")
        }
    )
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "Successfully retrieved list of base location from eLinks",
            response = Object.class
        ),
        @ApiResponse(
            code = 400,
            message = "Bad Request"
        ),
        @ApiResponse(
            code = 401,
            message = "Forbidden Error: Access denied"
        ),
        @ApiResponse(
            code = 500,
            message = "Internal Server Error"
        )
    })
    @GetMapping("/base_location")
    public ResponseEntity<Object> getBaseLocations() {
        return elinkService.retrieveBaseLocation();
    }
}
