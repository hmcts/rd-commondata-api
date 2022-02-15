package uk.gov.hmcts.reform.cdapi;

import io.restassured.response.Response;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.reform.cdapi.domain.HearingChannels;
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
public class RetrieveHearingChannelsByCategoryKeyFunctionalTest extends AuthorizationFunctionalTest {

    public static final String mapKey = "CrdApiController.retrieveHearingChannelByCategoryId";
    private static final String path = "/lov/categories";

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnEmptyList_WhenNoDataFound() {
        final var response = (ErrorResponse)
            commonDataApiClient.retrieveHearingChannelsByCategoryId(
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
        final HearingChannels response =
            commonDataApiClient.retrieveHearingChannelsByCategoryIdSuccess(path,"/HearingChannel");
        assertNotNull(response);
        assertThat(response.getHearingChannels()).hasSizeGreaterThan(1);
        response.getHearingChannels().forEach(h -> assertNull(h.getChildNodes()));
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnSuccessWithChilds() {
        final HearingChannels response =
            commonDataApiClient.retrieveHearingChannelsByCategoryIdSuccess(path,"/HearingChannel?"
                + "is-child-required=y&key=telephone");
        assertNotNull(response);
        assertThat(response.getHearingChannels()).hasSizeGreaterThan(0);
        response.getHearingChannels().forEach(h -> assertFalse(h.getChildNodes().isEmpty()));
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void retrieveHearingChannels_UnauthorizedDueToNoBearerToken_ShouldReturnStatusCode401() {
        Response response =
            commonDataApiClient.retrieveResponseForGivenRequest_NoBearerToken("/HearingChannel", path);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode());
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void retrieveHearingChannels_UnauthorizedDueToNoS2SToken_ShouldReturnStatusCode401() {
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
