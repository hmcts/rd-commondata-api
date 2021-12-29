package uk.gov.hmcts.reform.cdapi;

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

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SerenityTest
@SpringBootTest
@WithTags({@WithTag("testType:Functional")})
@ActiveProfiles("functional")
class RetrieveCaseFlagByServiceIdFunctionalTest extends AuthorizationFunctionalTest {

    public static final String mapKey = "CaseFlagApiController.retrieveCaseFlagsByServiceId";
    private static final String path = "/refdata/commondata/caseflags";

    @Test
    @ToggleEnable(mapKey = mapKey, withFeature = true)
    void shouldReturnEmptyList_WhenNoDataFound() {
        final var response = (ErrorResponse)
            commonDataApiClient.retrieveCaseFlagsByServiceId(
                HttpStatus.BAD_REQUEST,
                "service-id=xxxx?flag-type=hello"
            );
    }

    @Test
    @ExtendWith(FeatureToggleConditionExtension.class)
    @ToggleEnable(mapKey = mapKey, withFeature = false)
    void shouldNotRetrieveCaseFlag_WhenToggleOff_WithStatusCode_403() {
        ErrorResponse response = (ErrorResponse)
            commonDataApiClient
                .retrieveResponseForGivenRequest(HttpStatus.FORBIDDEN,
                                                 "service-id=xxxx?flag-type=hello",
                                                 CaseFlag.class, path
                );
        assertNotNull(response);
    }
}
