package uk.gov.hmcts.reform.cdapi;

import io.restassured.response.Response;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.reform.cdapi.exception.ErrorResponse;
import uk.gov.hmcts.reform.cdapi.serenity5.SerenityTest;
import uk.gov.hmcts.reform.cdapi.util.FeatureToggleConditionExtension;
import uk.gov.hmcts.reform.cdapi.util.ToggleEnable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


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




}
