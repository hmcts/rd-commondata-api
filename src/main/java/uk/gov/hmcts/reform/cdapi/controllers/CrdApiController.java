package uk.gov.hmcts.reform.cdapi.controllers;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.cdapi.domain.Categories;
import uk.gov.hmcts.reform.cdapi.domain.Category;
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

    @ApiOperation(
        value = "CommonData API will be used to retrieve the list of category values for a given category id.",
        notes = "Any valid IDAM role should be able to access this API ",
        authorizations = {
            @Authorization(value = "ServiceAuthorization"),
            @Authorization(value = "Authorization")
        }
    )
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "Successfully retrieved list of Category Values for the request provided",
            response = Categories.class
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
    @GetMapping(
        produces = APPLICATION_JSON_VALUE,
        path = {"/lov/categories", "/lov/categories/{category-id}"}
    )
    public ResponseEntity<Categories> retrieveListOfValuesByCategoryId(
        @PathVariable(value = "category-id")
        @ApiParam(name = "category-id", value = "Any Valid String is allowed", required = true)
            Optional<String> categoryKey,
        @RequestParam(value = "service-id", required = false)
        @ApiParam(name = "service-id", value = "Any Valid String is allowed") String serviceId,
        @RequestParam(value = "parent-category", required = false)
        @ApiParam(name = "parent-category", value = "Any Valid String is allowed") String parentCategory,
        @RequestParam(value = "parent-key", required = false)
        @ApiParam(name = "parent-key", value = "Any Valid String is allowed") String parentKey,
        @RequestParam(value = "key", required = false)
        @ApiParam(name = "key", value = "Any Valid String is allowed") String key,
        @RequestParam(value = "is-child-required", required = false)
        @ApiParam(name = "is-child-required", value = "Any Valid String is allowed") String isChildRequired) {
        if (!categoryKey.isPresent()) {
            throw new InvalidRequestException("Syntax error or Bad request");
        }
        List<Category> listOfValues = crdService.retrieveListOfValuesByCategoryId(categoryKey.get(), serviceId,
                                                                              parentCategory, parentKey, key,
                                                                              "Y".equalsIgnoreCase(isChildRequired));
        return ResponseEntity.ok().body(new Categories(listOfValues));
    }

}
