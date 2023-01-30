package uk.gov.hmcts.reform.cdapi.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.cdapi.controllers.request.CategoryRequest;
import uk.gov.hmcts.reform.cdapi.controllers.response.Categories;
import uk.gov.hmcts.reform.cdapi.controllers.response.Category;
import uk.gov.hmcts.reform.cdapi.exception.InvalidRequestException;
import uk.gov.hmcts.reform.cdapi.service.CrdService;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(path = "/refdata/commondata")
@RestController
@Slf4j
public class CrdApiController {

    @Autowired
    CrdService crdService;

    @Operation(
        summary = "CommonData API will be used to retrieve the list of category values for a given category id.",
        description = "Any valid IDAM role should be able to access this API ",
        security =
            {
                @SecurityRequirement(name = "Authorization"),
                @SecurityRequirement(name = "ServiceAuthorization")
            }
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved list of Category Values for the request provided",
            content = @Content(schema = @Schema(implementation = Categories.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Forbidden Error: Access denied"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error"
        )
    })
    @GetMapping(
        produces = APPLICATION_JSON_VALUE,
        path = {"/lov/categories", "/lov/categories/{categoryId}"}
    )
    public ResponseEntity<Categories> retrieveListOfValuesByCategoryId(
        @Parameter(name = "categoryId", description = "Any Valid String is allowed", required = true)
        @PathVariable(value = "categoryId") Optional<String> categoryId,
        CategoryRequest categoryRequest) {

        if (!categoryId.isPresent()) {
            throw new InvalidRequestException("Syntax error or Bad request");
        }

        categoryRequest.setCategoryId(categoryId.get());
        List<Category> listOfValues = crdService.retrieveListOfValuesByCategory(categoryRequest);
        return ResponseEntity.ok().body(new Categories(listOfValues));
    }

}
