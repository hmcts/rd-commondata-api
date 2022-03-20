package uk.gov.hmcts.reform.cdapi;

import io.restassured.response.Response;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.reform.cdapi.controllers.response.Categories;
import uk.gov.hmcts.reform.cdapi.domain.CaseFlag;
import uk.gov.hmcts.reform.cdapi.exception.ErrorResponse;
import uk.gov.hmcts.reform.cdapi.serenity5.SerenityTest;
import uk.gov.hmcts.reform.cdapi.util.FeatureToggleConditionExtension;
import uk.gov.hmcts.reform.cdapi.util.ToggleEnable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SerenityTest
@SpringBootTest
@WithTags({@WithTag("testType:Functional")})
@ActiveProfiles("functional")
class CommonDataApiFunctionalTest extends AuthorizationFunctionalTest {

    private static final String MAP_KEY_CASE_FLAGS = "CaseFlagApiController.retrieveCaseFlagsByServiceId";
    private static final String SLASH = "/";
    private static final String PATH_CASE_FLAGS = SLASH.concat("caseflags");
    private static final String MAP_KEY_LOV = "CrdApiController.retrieveListOfValuesByCategoryId";
    private static final String PATH_LOV = SLASH.concat("lov").concat(SLASH).concat("categories");
    private static final String PARAM_SIGN = "?";
    private static final String PARAM_HEARING = SLASH.concat("HearingChannel");
    private static final String PARAM_HEARING_WITH_PARAM_SIGN = PARAM_HEARING.concat(PARAM_SIGN);
    private static final String DATA_NOT_FOUND = "Data not found";

    @Test
    @ExtendWith(FeatureToggleConditionExtension.class)
    @ToggleEnable(mapKey = MAP_KEY_CASE_FLAGS, withFeature = true)
    void shouldThrowErrorWhenFlagTypeIsInvalid() {
        final var response = (ErrorResponse)
            commonDataApiClient.retrieveCaseFlagsByServiceId(
                HttpStatus.BAD_REQUEST,
                "hello"
            );
        assertNotNull(response);
        assertEquals("Allowed values are PARTY or CASE", response.getErrorDescription());
    }

    @Test
    @ExtendWith(FeatureToggleConditionExtension.class)
    @ToggleEnable(mapKey = MAP_KEY_CASE_FLAGS, withFeature = false)
    void shouldNotRetrieveCaseFlagWhenToggleOffWithStatusCode403() {
        ErrorResponse response = (ErrorResponse)
            commonDataApiClient
                .retrieveResponseForGivenRequest(HttpStatus.FORBIDDEN,
                                                 "/service-id=xxxx?flag-type=hello",
                                                 CaseFlag.class, PATH_CASE_FLAGS
                );
        assertNotNull(response);
    }

    @Test
    @ToggleEnable(mapKey = MAP_KEY_CASE_FLAGS, withFeature = true)
    void retrieveCaseFlagsUnauthorizedDueToNoBearerTokenShouldReturnStatusCode401() {
        Response response =
            commonDataApiClient.retrieveResponseForGivenRequest_NoBearerToken("/service-id=AAA1", PATH_CASE_FLAGS);

        assertNotNull(response);
        assertThat(response.getHeader("UnAuthorized-Token-Error").contains("Authentication Exception"));
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode());
    }

    @Test
    @ToggleEnable(mapKey = MAP_KEY_CASE_FLAGS, withFeature = true)
    void retrieveBuildingLocationsUnauthorizedDueToNoS2STokenShouldReturnStatusCode401() {
        Response response =
            commonDataApiClient.retrieveResponseForGivenRequest_NoS2SToken("/service-id=AAA1", PATH_CASE_FLAGS);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode());
    }

    @Test
    @ToggleEnable(mapKey = MAP_KEY_LOV, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnEmptyListWhenNoDataFound() {
        final var response = (ErrorResponse)
            commonDataApiClient.retrieveListOfValuesByCategoryId(
                HttpStatus.NOT_FOUND,
                "hello"
            );
        assertNotNull(response);
        assertEquals(DATA_NOT_FOUND, response.getErrorDescription());
    }

    @Test
    @ToggleEnable(mapKey = MAP_KEY_LOV, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnSuccess() {
        final Categories response =
            commonDataApiClient.retrieveCategoriesByCategoryIdSuccess(PATH_LOV, PARAM_HEARING);
        assertNotNull(response);
        assertThat(response.getListOfCategory()).hasSizeGreaterThan(1);
        response.getListOfCategory().forEach(h -> assertEquals("HearingChannel", h.getCategoryKey()));
        response.getListOfCategory().forEach(h -> assertNull(h.getChildNodes()));
    }

    @Test
    @ToggleEnable(mapKey = MAP_KEY_LOV, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnSuccessWithChildren() {
        final Categories response =
            commonDataApiClient.retrieveCategoriesByCategoryIdSuccess(PATH_LOV, PARAM_HEARING_WITH_PARAM_SIGN
                + "isChildRequired=y&key=telephone");
        assertNotNull(response);
        assertThat(response.getListOfCategory()).hasSizeGreaterThan(0);
        response.getListOfCategory().forEach(h -> assertEquals("HearingChannel", h.getCategoryKey()));
        response.getListOfCategory().forEach(h -> assertFalse(h.getChildNodes().isEmpty()));
    }

    @Test
    @ToggleEnable(mapKey = MAP_KEY_LOV, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnSuccessForChildCategories() {
        final Categories response =
            commonDataApiClient.retrieveCategoriesByCategoryIdSuccess(PATH_LOV, "/HearingSubChannel?"
                + "isChildRequired=y&parentKey=telephone");
        assertNotNull(response);
        response.getListOfCategory().forEach(h -> assertEquals("HearingSubChannel", h.getCategoryKey()));
        assertThat(response.getListOfCategory()).hasSizeGreaterThan(0);
    }

    @Test
    @ToggleEnable(mapKey = MAP_KEY_LOV, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnSuccessWithNoChilds() {
        final Categories response =
            commonDataApiClient.retrieveCategoriesByCategoryIdSuccess(PATH_LOV, PARAM_HEARING_WITH_PARAM_SIGN
                + "isChildRequired=n&key=telephone");
        assertNotNull(response);
        response.getListOfCategory().forEach(h -> assertEquals("HearingChannel", h.getCategoryKey()));
        assertThat(response.getListOfCategory()).hasSizeGreaterThan(0);
    }

    @Test
    @ToggleEnable(mapKey = MAP_KEY_LOV, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnResourceNotFoundWithChildsAndInvalidParentKey() {
        final var response = (ErrorResponse)
            commonDataApiClient.retrieveListOfValuesByCategoryId(
                HttpStatus.NOT_FOUND,
                PARAM_HEARING_WITH_PARAM_SIGN
                    + "isChildRequired=y&parentKey=telephone"
            );
        assertNotNull(response);
        assertEquals(DATA_NOT_FOUND, response.getErrorDescription());
    }

    @Test
    @ToggleEnable(mapKey = MAP_KEY_LOV, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnResourceNotFoundWithChildsAndInvalidParentCategory() {
        final var response = (ErrorResponse)
            commonDataApiClient.retrieveListOfValuesByCategoryId(
                HttpStatus.NOT_FOUND,
                PARAM_HEARING_WITH_PARAM_SIGN
                    + "isChildRequired=y&parentCategory=HearingChannel"
            );
        assertNotNull(response);
        assertEquals(DATA_NOT_FOUND, response.getErrorDescription());
    }

    @Test
    @ToggleEnable(mapKey = MAP_KEY_LOV, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnResourceNotFoundInvalidServiceId() {
        final var response = (ErrorResponse)
            commonDataApiClient.retrieveListOfValuesByCategoryId(
                HttpStatus.NOT_FOUND,
                PARAM_HEARING_WITH_PARAM_SIGN
                    + "serviceId=BBA5"
            );
        assertNotNull(response);
        assertEquals(DATA_NOT_FOUND, response.getErrorDescription());
    }

    @Test
    @ToggleEnable(mapKey = MAP_KEY_LOV, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnResourceNotFoundWithChildrenAndInvalidKey() {
        final var response = (ErrorResponse)
            commonDataApiClient.retrieveListOfValuesByCategoryId(
                HttpStatus.NOT_FOUND,
                PARAM_HEARING_WITH_PARAM_SIGN
                    + "isChildRequired=y&key=telephone-CVP"
            );
        assertNotNull(response);
        assertEquals(DATA_NOT_FOUND, response.getErrorDescription());
    }

    @Test
    @ToggleEnable(mapKey = MAP_KEY_LOV, withFeature = true)
    void retrieveCategoriesUnauthorizedDueToNoBearerTokenShouldReturnStatusCode401() {
        Response response =
            commonDataApiClient.retrieveResponseForGivenRequest_NoBearerToken(PARAM_HEARING, PATH_LOV);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode());
    }

    @Test
    @ToggleEnable(mapKey = MAP_KEY_LOV, withFeature = true)
    void retrieveCategoriesUnauthorizedDueToNoS2STokenShouldReturnStatusCode401() {
        Response response =
            commonDataApiClient.retrieveResponseForGivenRequest_NoS2SToken(PARAM_HEARING, PATH_LOV);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode());
    }

    @Test
    @ExtendWith(FeatureToggleConditionExtension.class)
    @ToggleEnable(mapKey = MAP_KEY_LOV, withFeature = true)
    void shouldThrowErrorWhenCategoryIdIsEmpty() {
        final var response = (ErrorResponse)
            commonDataApiClient.retrieveBadRequestByEmptyCategoryId(
                HttpStatus.BAD_REQUEST,
                " "
            );
        assertNotNull(response);
        assertEquals("Syntax error or Bad request", response.getErrorDescription());
    }
}
