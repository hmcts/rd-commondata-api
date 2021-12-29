package uk.gov.hmcts.reform.cdapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.cdapi.domain.CaseFlag;

@ExtendWith(SpringExtension.class)
@WithTags({@WithTag("testType:Integration")})
public class RetrieveCaseFlagsIntegrationTest extends CdAuthorizationEnabledIntegrationTest {
    private static final String path = "/service-id={service-id}";
    private static final String HTTP_STATUS_STR = "http_status";

    @Test
    void shouldRetrieveCaseFlag_For_ServiceId_WithStatusCode_200()
        throws JsonProcessingException {

        commonDataApiClient.retrieveCaseFlagsByServiceId("XXXX", CaseFlag.class, path);
    }

    @Test
    void shouldRetrieveCaseFlag_For_ServiceId_WithStatusCode_400()
        throws JsonProcessingException {

        commonDataApiClient.retrieveCaseFlagsByServiceId("XXXX?flag-type=hello", CaseFlag.class, path);
    }
}
