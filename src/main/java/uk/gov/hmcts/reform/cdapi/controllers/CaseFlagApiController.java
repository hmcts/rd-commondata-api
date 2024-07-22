package uk.gov.hmcts.reform.cdapi.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.cdapi.domain.CaseFlag;
import uk.gov.hmcts.reform.cdapi.exception.InvalidRequestException;
import uk.gov.hmcts.reform.cdapi.service.CaseFlagService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.reform.cdapi.util.ValidationUtil.validateValueForYorNRequired;
import static uk.gov.hmcts.reform.cdapi.util.ValidationUtil.validationFlagType;

@RestController
@Slf4j
@RequestMapping(
    path = "/refdata/commondata"
)
public class CaseFlagApiController {

    @Autowired
    CaseFlagService caseFlagService;

    @Operation(
        summary = "CommonData API will be used to retrieve the list of case flags for a "
            + "given service id.",
        description = "Any valid IDAM role should be able to access this API ",
        security =
            {
                @SecurityRequirement(name = "Authorization"),
                @SecurityRequirement(name = "ServiceAuthorization")
            }
    )

    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved list of Case Flag for the request provided",
        content = @Content(schema = @Schema(implementation = CaseFlag.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Bad Request",
        content = @Content
    )
    @ApiResponse(
        responseCode = "401",
        description = "Forbidden Error: Access denied",
        content = @Content
    )
    @ApiResponse(
        responseCode = "500",
        description = "Internal Server Error",
        content = @Content
    )
    @GetMapping(
        produces = APPLICATION_JSON_VALUE,
        path = {"/caseflags/service-id={service-id}"}
    )
    public ResponseEntity<CaseFlag> retrieveCaseFlagsByServiceId(
        @PathVariable(value = "service-id")
        @Parameter(name = "service-id",
            description = "Any Valid String is allowed",
            required = true)
        String serviceId,
        @RequestParam(value = "flag-type", required = false)
        @Parameter(name = "flag-type",
            description = "Allowed Values are PARTY or CASE")
        String flagType,
        @RequestParam(value = "welsh-required", required = false)
        @Parameter(name = "welsh-required",
            description = "Allowed Values are Y or N")
        String welshRequired,
        @RequestParam(value = "available-external-flag", required = false)
        @Parameter(name = "available-external-flag",
            description = "Allowed Values are Y or N")
        String availableExternalFlag
    ) {
        if (StringUtils.isEmpty(serviceId)) {
            throw new InvalidRequestException("service Id can not be null or empty");
        }
        if (null != flagType) {
            validationFlagType(flagType.trim());
        }
        if (null != welshRequired) {
            validateValueForYorNRequired(welshRequired.trim());
        }
        if (null != availableExternalFlag) {
            validateValueForYorNRequired(availableExternalFlag.trim());
        }
        log.info("Calling Service layer");
        var caseFlag = caseFlagService.retrieveCaseFlagByServiceId(serviceId, flagType,
                                                                   welshRequired, availableExternalFlag
        );
        return ResponseEntity.ok().body(caseFlag);
    }

}
