package uk.gov.hmcts.reform.cdapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.cdapi.domain.CaseFlag;
import uk.gov.hmcts.reform.cdapi.domain.FlagDetail;
import uk.gov.hmcts.reform.cdapi.domain.FlagType;
import uk.gov.hmcts.reform.cdapi.exception.ErrorResponse;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@WithTags({@WithTag("testType:Integration")})
class RetrieveCaseFlagsIntegrationTest extends CdAuthorizationEnabledIntegrationTest {
    private static final String path = "/caseflags/service-id={service-id}";

    @Test
    void shouldRetrieveCaseFlag_For_ServiceId_WithStatusCode_200()
        throws JsonProcessingException {

        final var response = (CaseFlag) commonDataApiClient.retrieveCaseFlagsByServiceId("AAA1", CaseFlag.class, path);
        assertNotNull(response);
        assertEquals(2, response.getFlags().get(0).getFlagDetails().size());
        verifyResponse(response.getFlags().get(0).getFlagDetails());
    }

    @Test
    void shouldRetrieveCaseFlag_For_DefaultServiceId()
        throws JsonProcessingException {

        final var response = (CaseFlag) commonDataApiClient.retrieveCaseFlagsByServiceId("", CaseFlag.class, path);
        assertNotNull(response);
        assertNotNull(response.getFlags().get(0).getFlagDetails());
        assertEquals(1, response.getFlags().get(0).getFlagDetails().size());
    }

    @Test
    void shouldRetrieveCaseFlag_For_ServiceId_FlagTypeAsParty()
        throws JsonProcessingException {

        final var response = (CaseFlag) commonDataApiClient.retrieveCaseFlagsByServiceId(
            "AAA1" + "?flag-type=party",
            CaseFlag.class,
            path
        );
        assertNotNull(response);
        assertEquals(1, response.getFlags().get(0).getFlagDetails().size());
        verifyResponse(response.getFlags().get(0).getFlagDetails());

    }

    @Test
    void shouldRetrieveCaseFlag_For_ServiceId_FlagTypeAsCase()
        throws JsonProcessingException {

        final var response = (CaseFlag) commonDataApiClient.retrieveCaseFlagsByServiceId(
            "AAA1" + "?flag-type=case",
            CaseFlag.class,
            path
        );
        assertNotNull(response);
        assertEquals(1, response.getFlags().get(0).getFlagDetails().size());
        verifyResponse(response.getFlags().get(0).getFlagDetails());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldRetrieveCaseFlag_For_ServiceId_WithStatusCode_400()
        throws JsonProcessingException {

        var errorResponseMap = commonDataApiClient.retrieveCaseFlagsByServiceId(
            "XXXX?flag-type=hello",
            ErrorResponse.class,
            path
        );
        assertNotNull(errorResponseMap);
        assertThat((Map<String, Object>) errorResponseMap).containsEntry("http_status", HttpStatus.BAD_REQUEST);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldRetrieveCaseFlag_For_ServiceId_WithStatusCode_404()
        throws JsonProcessingException {

        var errorResponseMap = commonDataApiClient.retrieveCaseFlagsByServiceId(
            "XXXX?flag-type=case",
            ErrorResponse.class,
            path
        );
        assertNotNull(errorResponseMap);
        assertThat((Map<String, Object>) errorResponseMap).containsEntry("http_status", HttpStatus.NOT_FOUND);
    }

    private void verifyResponse(List<FlagDetail> flagDetails) {
        for (FlagDetail flagDetail : flagDetails) {
            if (flagDetail.getName().equalsIgnoreCase(FlagType.CASE.name())) {
                assertEquals(2, flagDetail.getChildFlags().size());
            }
            if (flagDetail.getName().equalsIgnoreCase(FlagType.PARTY.name())) {
                assertEquals(4, flagDetail.getChildFlags().size());
            }
            switch (flagDetail.getFlagCode()) {
                case "RA0004":
                    assertEquals("Pre- Hearing visit", flagDetail.getName());
                    break;
                case "RA0042":
                    assertEquals("Sign Language Interpreter", flagDetail.getName());
                    break;
                case "PF0015":
                    assertEquals("Language Interpreter", flagDetail.getName());
                    break;
                case "CF0002":
                    assertEquals("Complex Case", flagDetail.getName());
                    break;
                case "PF0003":
                    assertEquals("Potentially suicidal", flagDetail.getName());
                    break;
                case "OT0001":
                    assertEquals("Other", flagDetail.getName());
                    break;
                default:
                    break;
            }
            verifyResponse(flagDetail.getChildFlags());
        }
    }
}
