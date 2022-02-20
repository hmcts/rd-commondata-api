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
        path = {"/lov/categories", "/lov/categories/{categoryId}"}
    )
    public ResponseEntity<Categories> retrieveListOfValuesByCategoryId(
        @ApiParam(name = "categoryId", value = "Any Valid String is allowed", required = true)
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
