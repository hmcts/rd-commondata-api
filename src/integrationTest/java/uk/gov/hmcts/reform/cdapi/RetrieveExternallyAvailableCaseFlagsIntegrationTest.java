package uk.gov.hmcts.reform.cdapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.cdapi.domain.CaseFlag;
import uk.gov.hmcts.reform.cdapi.domain.FlagDetail;
import uk.gov.hmcts.reform.cdapi.util.WireMockUtil;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;

import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@WithTags({@WithTag("testType:Integration")})
public class RetrieveExternallyAvailableCaseFlagsIntegrationTest extends CdAuthorizationEnabledIntegrationTest {
    private static final String path = "/caseflags/service-id={service-id}";

    @Test
    @SuppressWarnings("unchecked")
    void shouldRetrieveCaseFlagForWelshRequiredFlagPrdRoleWithNStatusCode_200()
        throws JsonProcessingException {
        UserInfo userDetails = UserInfo.builder()
            .uid(UUID.randomUUID().toString())
            .givenName("Super")
            .familyName("User")
            .roles(List.of("pui-organisation-manager"))
            .build();
        idamService.stubFor(get(urlPathMatching("/o/userinfo"))
                                .willReturn(aResponse()
                                                .withStatus(200)
                                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                                .withBody(WireMockUtil.getObjectMapper()
                                                              .writeValueAsString(userDetails))
                                                .withTransformers("external_user-token-response")));

        final var response = (CaseFlag) commonDataApiClient.retrieveCaseFlagsByServiceId(
            "AAA1" + "?available-external-flag=y&welsh-required=n",
            CaseFlag.class,
            path
        );
        validateCaseFlags(response, "n");
    }

    @Test
    void shouldReturnFailureForRetrieveCaseFlagsByServiceIdWithAvailableExternalFlagIsY() {
        Exception exception = assertThrows(UnrecognizedPropertyException.class, () -> commonDataApiClient.retrieveCaseFlagsByServiceId(
            "AAA1?flag-type=party&available-external-flag=Y",
            CaseFlag.class,
            path
        ));
        assertNotNull(exception);
        assertTrue(exception.getLocalizedMessage().contains("Data not found"));
    }

    private void validateCaseFlags(CaseFlag caseFlags, String flag) {
        boolean externallyAvailable = (StringUtils.isNotEmpty(flag) && (flag.trim().equalsIgnoreCase("y")));
        assertNotNull(caseFlags);
        assertNotNull(caseFlags.getFlags());
        caseFlags.getFlags().stream().forEach(caseFlag -> {
            assertNotNull(caseFlag.getFlagDetails());
            List<FlagDetail> flagDetailsList = caseFlag.getFlagDetails();
            flagDetailsList.stream().forEach(flagDetail -> {
                boolean flagExternallyAvailable = flagDetail.getExternallyAvailable();
                if (externallyAvailable) {
                    MatcherAssert.assertThat(flagExternallyAvailable, anyOf(is(true)));
                } else {
                    MatcherAssert.assertThat(flagExternallyAvailable, anyOf(is(false), is(true)));
                }
                assertTrue(flagDetail.getParent());
                validateChildFlags(flagDetail.getChildFlags(), externallyAvailable);
            });
        });
    }

    private void validateChildFlags(List<FlagDetail> flagDetails,
                                    boolean externallyAvailable) {
        if (flagDetails != null) {
            flagDetails.stream()
                .forEach(flagDetail -> {
                    if (externallyAvailable) {
                        MatcherAssert.assertThat(flagDetail.getExternallyAvailable(), anyOf(is(true)));
                    } else {
                        MatcherAssert.assertThat(flagDetail.getExternallyAvailable(), anyOf(is(false), is(true)));
                    }
                    validateChildFlags(flagDetail.getChildFlags(), externallyAvailable);
                });
        }
    }

}
