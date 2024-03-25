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
import uk.gov.hmcts.reform.cdapi.util.ErrorInvalidRequestResponse;
import uk.gov.hmcts.reform.cdapi.util.FeatureToggleConditionExtension;
import uk.gov.hmcts.reform.cdapi.util.ToggleEnable;
import uk.gov.hmcts.reform.lib.util.serenity5.SerenityTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

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
    private static final String SERVICE_ID = "ServiceId=";
    private static final String SERVICE_ID_BBA3 = "BBA3";
    private static final String PARAM_LISTING_STATUS_WITH_SERVICE_ID = SLASH.concat("ListingStatus")
        .concat(PARAM_SIGN).concat(SERVICE_ID + SERVICE_ID_BBA3);
    private static final String PARAM_LISTING_STATUS_WITH_EMPTY_SERVICE_ID = SLASH.concat("ListingStatus")
        .concat(PARAM_SIGN).concat(SERVICE_ID);
    private static final String DATA_NOT_FOUND = "Data not found";

    @Test
    @ExtendWith(FeatureToggleConditionExtension.class)
    @ToggleEnable(mapKey = MAP_KEY_CASE_FLAGS, withFeature = true)
    void shouldThrowErrorWhenFlagTypeIsInvalid() {
        final var response = (ErrorResponse)
            commonDataApiClient.retrieveCaseFlagsByServiceId(
                HttpStatus.BAD_REQUEST,
                "service-id=XXXX?flag-type=hello"
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
                NOT_FOUND,
                "hello"
            );
        assertNotNull(response);
        assertEquals(DATA_NOT_FOUND, response.getErrorDescription());
    }

    //When no service id specified and category exists then a list of categories where serviceids are empty is returned
    @Test
    @ToggleEnable(mapKey = MAP_KEY_LOV, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnSuccess() {
        Response response =
            commonDataApiClient.retrieveCategoriesByCategoryIdSuccess(PATH_LOV, "CaseLinkingReasonCode");

        if (OK.value() == response.getStatusCode()) {
            var categories = response.getBody().as(Categories.class);
            assertNotNull(categories);
            assertThat(categories.getListOfCategory()).hasSizeGreaterThan(1);
            categories.getListOfCategory().forEach(h -> assertEquals("CaseLinkingReasonCode", h.getCategoryKey()));
            categories.getListOfCategory().forEach(h -> assertNull(h.getChildNodes()));
        } else {
            assertEquals(NOT_FOUND.value(), response.getStatusCode());
        }

    }

    //Category and key provided exist ,
    // fetches the filtered record along with childern if they exist
    @Test
    @ToggleEnable(mapKey = MAP_KEY_LOV, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnSuccessWithChildren() {
        Response response =
            commonDataApiClient.retrieveCategoriesByCategoryIdSuccess(PATH_LOV, "CaseLinkingReasonCode?"
                + "isChildRequired=y&key=CLRC004");

        if (OK.value() == response.getStatusCode()) {
            var categories = response.getBody().as(Categories.class);
            assertNotNull(categories);
            assertThat(categories.getListOfCategory()).hasSizeGreaterThan(0);
            categories.getListOfCategory().forEach(h -> assertEquals("HearingChannel", h.getCategoryKey()));
            categories.getListOfCategory().forEach(h -> assertFalse(h.getChildNodes().isEmpty()));
        } else {
            assertEquals(NOT_FOUND.value(), response.getStatusCode());
        }
    }


    //Category and parentkey provided exist ,
    // fetches the filtered record else empty list like below
    @Test
    @ToggleEnable(mapKey = MAP_KEY_LOV, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnSuccessForChildCategories() {
        Response response =
            commonDataApiClient.retrieveCategoriesByCategoryIdSuccess(PATH_LOV, "/CaseLinkingReasonCode?"
                + "isChildRequired=y&parentKey=CLRC013");

        if (OK.value() == response.getStatusCode()) {
            var categories = response.getBody().as(Categories.class);
            assertNotNull(categories);
            categories.getListOfCategory().forEach(h -> assertEquals("CaseLinkingReasonCode", h.getCategoryKey()));
            assertThat(categories.getListOfCategory()).hasSize(0);
        } else {
            assertEquals(NOT_FOUND.value(), response.getStatusCode());
        }
    }

    //Category and key provided exist ,
    // fetches the filtered record along with no childern
    @Test
    @ToggleEnable(mapKey = MAP_KEY_LOV, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnSuccessWithNoChilds() {
        Response response =
            commonDataApiClient.retrieveCategoriesByCategoryIdSuccess(PATH_LOV, "CaseLinkingReasonCode?"
                + "isChildRequired=n&key=CLRC013");

        if (OK.value() == response.getStatusCode()) {
            var categories = response.getBody().as(Categories.class);
            assertNotNull(categories);
            categories.getListOfCategory().forEach(h -> assertEquals("CaseLinkingReasonCode", h.getCategoryKey()));
            assertThat(categories.getListOfCategory()).hasSizeGreaterThan(0);
        } else {
            assertEquals(NOT_FOUND.value(), response.getStatusCode());
        }
    }

    @Test
    @ToggleEnable(mapKey = MAP_KEY_LOV, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnResourceNotFoundWithChildsAndInvalidParentKey() {
        final var response = (ErrorResponse)
            commonDataApiClient.retrieveListOfValuesByCategoryId(
                NOT_FOUND,
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
                NOT_FOUND,
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
                NOT_FOUND,
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
                NOT_FOUND,
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
        final var response = (ErrorInvalidRequestResponse)
            commonDataApiClient.retrieveBadRequestByEmptyCategoryId(
                NOT_FOUND,
                ""
            );
        assertNotNull(response);
        assertEquals("Not Found", response.getError());
    }

    @Test
    @ToggleEnable(mapKey = MAP_KEY_LOV, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnChildCategoriesInParticularToServiceId() {
        Response response =
            commonDataApiClient.retrieveCategoriesByCategoryIdSuccess(PATH_LOV, "/HearingChannel?"
                + "isChildRequired=y&serviceId=BBA3&key=VID");

        if (OK.value() == response.getStatusCode()) {
            var categories = response.getBody().as(Categories.class);
            assertNotNull(categories);
            categories.getListOfCategory().forEach(h -> assertEquals("HearingChannel", h.getCategoryKey()));
            assertThat(categories.getListOfCategory()).hasSizeGreaterThan(0);
            assertThat(categories.getListOfCategory().get(0).getChildNodes()).hasSizeGreaterThan(0);
            assertEquals("HearingChannel", categories.getListOfCategory().get(0).getChildNodes().get(0)
                .getParentCategory());

        } else {
            assertEquals(NOT_FOUND.value(), response.getStatusCode());
        }
    }

    @Test
    @ToggleEnable(mapKey = MAP_KEY_LOV, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnSuccessWithServiceIdNoChilds() {
        Response response =
            commonDataApiClient.retrieveCategoriesByCategoryIdSuccess(PATH_LOV, PARAM_HEARING_WITH_PARAM_SIGN
                + "isChildRequired=n&serviceId=BBA3");

        if (OK.value() == response.getStatusCode()) {
            var categories = response.getBody().as(Categories.class);
            assertNotNull(categories);
            categories.getListOfCategory().forEach(h -> assertEquals("HearingChannel", h.getCategoryKey()));
            assertThat(categories.getListOfCategory()).hasSizeGreaterThan(0);
            categories.getListOfCategory().forEach(h -> assertNull(h.getChildNodes()));
        } else {
            assertEquals(NOT_FOUND.value(), response.getStatusCode());
        }
    }

    @Test
    @ToggleEnable(mapKey = MAP_KEY_LOV, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnSuccess_Valid_ServiceID() {
        Response response =
            commonDataApiClient.retrieveCategoriesByCategoryIdSuccess(PATH_LOV, PARAM_LISTING_STATUS_WITH_SERVICE_ID);
        if (OK.value() == response.getStatusCode()) {
            var categories = response.getBody().as(Categories.class);
            assertNotNull(categories);
            assertThat(categories.getListOfCategory()).hasSizeGreaterThan(1);
            categories.getListOfCategory().forEach(h -> assertEquals("ListingStatus", h.getCategoryKey()));
        } else {
            assertEquals(NOT_FOUND.value(), response.getStatusCode());
        }
    }

    @Test
    @ToggleEnable(mapKey = MAP_KEY_LOV, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnSuccess_Empty_ServiceID() {
        Response response =
            commonDataApiClient.retrieveCategoriesByCategoryIdSuccess(
                PATH_LOV,
                PARAM_LISTING_STATUS_WITH_EMPTY_SERVICE_ID
            );
        if (OK.value() == response.getStatusCode()) {
            var categories = response.getBody().as(Categories.class);
            assertNotNull(categories);
            assertThat(categories.getListOfCategory()).hasSizeGreaterThan(1);
            categories.getListOfCategory().forEach(h -> assertEquals("ListingStatus", h.getCategoryKey()));
        } else {
            assertEquals(NOT_FOUND.value(), response.getStatusCode());
        }
    }

    @Test
    @ToggleEnable(mapKey = MAP_KEY_CASE_FLAGS, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnBadRequestForRetrieveCaseFlagsByServiceIdWithAvailableExternalFlagIsEmpty() {
        final var response = (ErrorResponse)
            commonDataApiClient.retrieveCaseFlagsByServiceId(
                HttpStatus.BAD_REQUEST,
                "service-id=XXXX?available-external-flag="
            );
        assertEquals(response.getErrorCode(), 400);
        assertEquals(response.getErrorDescription(), "Allowed values are Y or N");
    }

}
