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
import uk.gov.hmcts.reform.cdapi.exception.ErrorResponse;
import uk.gov.hmcts.reform.cdapi.serenity5.SerenityTest;
import uk.gov.hmcts.reform.cdapi.util.FeatureToggleConditionExtension;
import uk.gov.hmcts.reform.cdapi.util.ToggleEnable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


@SerenityTest
@SpringBootTest
@WithTags({@WithTag("testType:Functional")})
@ActiveProfiles("functional")
public class RetrieveListOfValuesByCategoryKeyFunctionalTest extends AuthorizationFunctionalTest {

    public static final String mapKey = "CrdApiController.retrieveListOfValuesByCategoryId";
    private static final String path = "/lov/categories";

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnEmptyList_WhenNoDataFound() {
        final var response = (ErrorResponse)
            commonDataApiClient.retrieveListOfValuesByCategoryId(
                HttpStatus.NOT_FOUND,
                "hello"
            );
        assertNotNull(response);
        assertEquals("Data not found", response.getErrorDescription());
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnSuccess() {
        final Categories response =
            commonDataApiClient.retrieveCategoriesByCategoryIdSuccess(path, "/HearingChannel");
        assertNotNull(response);
        assertThat(response.getListOfCategory()).hasSizeGreaterThan(1);
        response.getListOfCategory().forEach(h -> assertNull(h.getChildNodes()));
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnSuccessWithChilds() {
        final Categories response =
            commonDataApiClient.retrieveCategoriesByCategoryIdSuccess(path, "/HearingChannel?"
                + "isChildRequired=y&key=telephone");
        assertNotNull(response);
        assertThat(response.getListOfCategory()).hasSizeGreaterThan(0);
        response.getListOfCategory().forEach(h -> assertFalse(h.getChildNodes().isEmpty()));
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnSuccessForChildCategories() {
        final Categories response =
            commonDataApiClient.retrieveCategoriesByCategoryIdSuccess(path, "/HearingSubChannel?"
                + "isChildRequired=y&parentKey=telephone");
        assertNotNull(response);
        assertThat(response.getListOfCategory()).hasSizeGreaterThan(0);
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnSuccessWithNoChilds() {
        final Categories response =
            commonDataApiClient.retrieveCategoriesByCategoryIdSuccess(path, "/HearingChannel?"
                + "isChildRequired=n&key=telephone");
        assertNotNull(response);
        assertThat(response.getListOfCategory()).hasSizeGreaterThan(0);
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnResourceNotFoundWithChildsAndInvalidParentKey() {
        final var response = (ErrorResponse)
            commonDataApiClient.retrieveListOfValuesByCategoryId(
                HttpStatus.NOT_FOUND,
                "/HearingChannel?"
                    + "isChildRequired=y&parentKey=telephone"
            );
        assertNotNull(response);
        assertEquals("Data not found", response.getErrorDescription());
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnResourceNotFoundWithChildsAndInvalidParentCategory() {
        final var response = (ErrorResponse)
            commonDataApiClient.retrieveListOfValuesByCategoryId(
                HttpStatus.NOT_FOUND,
                "/HearingChannel?"
                    + "isChildRequired=y&parentCategory=HearingChannel"
            );
        assertNotNull(response);
        assertEquals("Data not found", response.getErrorDescription());
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnResourceNotFoundInvalidServiceId() {
        final var response = (ErrorResponse)
            commonDataApiClient.retrieveListOfValuesByCategoryId(
                HttpStatus.NOT_FOUND,
                "/HearingChannel?"
                    + "serviceId=BBA5"
            );
        assertNotNull(response);
        assertEquals("Data not found", response.getErrorDescription());
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnResourceNotFoundWithChildsAndInvalidKey() {
        final var response = (ErrorResponse)
            commonDataApiClient.retrieveListOfValuesByCategoryId(
                HttpStatus.NOT_FOUND,
                "/HearingChannel?"
                    + "isChildRequired=y&key=telephone-CVP"
            );
        assertNotNull(response);
        assertEquals("Data not found", response.getErrorDescription());
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void retrieveCategories_UnauthorizedDueToNoBearerToken_ShouldReturnStatusCode401() {
        Response response =
            commonDataApiClient.retrieveResponseForGivenRequest_NoBearerToken("/HearingChannel", path);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode());
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void retrieveCategories_UnauthorizedDueToNoS2SToken_ShouldReturnStatusCode401() {
        Response response =
            commonDataApiClient.retrieveResponseForGivenRequest_NoS2SToken("/HearingChannel", path);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode());
    }

    @Test
    @ExtendWith(FeatureToggleConditionExtension.class)
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void shouldThrowError_WhenCategoryIdisEmpty() {
        final var response = (ErrorResponse)
            commonDataApiClient.retrieveBadRequestByEmptyCategoryId(
                HttpStatus.BAD_REQUEST,
                " "
            );
        assertNotNull(response);
        assertEquals("Syntax error or Bad request", response.getErrorDescription());
    }
}
