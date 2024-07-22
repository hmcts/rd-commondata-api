package uk.gov.hmcts.reform.cdapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

/**
 * Default endpoints per application.
 */
@RestController
public class RootController {

    /**
     * Root GET endpoint.
     *
     * <p>Azure application service has a hidden feature of making requests to root endpoint when
     * "Always On" is turned on.
     * This is the endpoint to deal with that and therefore silence the unnecessary 404s as a response code.
     *
     * @return Welcome message from the service.
     */
    @Operation(
        summary = "Welcome"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Welcome message"
    )
    @GetMapping(
        path = "/",
        produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> welcome() {
        return ok("Welcome to RD Common Data API");
    }
}
