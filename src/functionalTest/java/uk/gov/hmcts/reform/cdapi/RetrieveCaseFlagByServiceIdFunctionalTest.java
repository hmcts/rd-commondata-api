package uk.gov.hmcts.reform.cdapi;

import io.restassured.response.Response;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.reform.cdapi.domain.CaseFlag;
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
class RetrieveCaseFlagByServiceIdFunctionalTest extends AuthorizationFunctionalTest {

    public static final String mapKey = "CaseFlagApiController.retrieveCaseFlagsByServiceId";
    private static final String path = "/caseflags";

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void shouldThrowError_WhenFlagTypeisInvalid() {
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
    @ToggleEnable(mapKey = mapKey, withFeature = false)
    void shouldNotRetrieveCaseFlag_WhenToggleOff_WithStatusCode_403() {
        ErrorResponse response = (ErrorResponse)
            commonDataApiClient
                .retrieveResponseForGivenRequest(HttpStatus.FORBIDDEN,
                                                 "/service-id=xxxx?flag-type=hello",
                                                 CaseFlag.class, path
                );
        assertNotNull(response);
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void retrieveCaseFlags_UnauthorizedDueToNoBearerToken_ShouldReturnStatusCode401() {
        Response response =
            commonDataApiClient.retrieveResponseForGivenRequest_NoBearerToken("/service-id=AAA1", path);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode());
    }

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void retrieveBuildingLocations_UnauthorizedDueToNoS2SToken_ShouldReturnStatusCode401() {
        Response response =
            commonDataApiClient.retrieveResponseForGivenRequest_NoS2SToken("/service-id=AAA1", path);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode());
    }
}
