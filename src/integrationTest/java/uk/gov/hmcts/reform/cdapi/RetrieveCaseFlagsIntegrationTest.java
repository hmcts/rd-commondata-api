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
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@WithTags({@WithTag("testType:Integration")})
class RetrieveCaseFlagsIntegrationTest extends CdAuthorizationEnabledIntegrationTest {
    private static final String path = "/caseflags/service-id={service-id}";

    @Test
    void shouldRetrieveCaseFlagForServiceIdWithStatusCode_200()
        throws JsonProcessingException {

        final var response = (CaseFlag) commonDataApiClient.retrieveCaseFlagsByServiceId("AAA1", CaseFlag.class, path);
        assertNotNull(response);
        assertEquals(2, response.getFlags().get(0).getFlagDetails().size());
        verifyResponse(response.getFlags().get(0).getFlagDetails());
    }

    @Test
    void shouldRetrieveCaseFlagForDefaultServiceId()
        throws JsonProcessingException {

        final var response = (CaseFlag) commonDataApiClient.retrieveCaseFlagsByServiceId("XXXX", CaseFlag.class, path);
        assertNotNull(response);
        assertNotNull(response.getFlags().get(0).getFlagDetails());
        assertEquals(1, response.getFlags().get(0).getFlagDetails().size());
    }

    @Test
    void shouldRetrieveCaseFlagForServiceIdFlagTypeAsParty()
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
    void shouldRetrieveCaseFlagForServiceIdFlagTypeAsCase()
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
    void shouldRetrieveCaseFlagForServiceIdWithStatusCode_400()
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

    @Test
    @SuppressWarnings("unchecked")
    void shouldRetrieveCaseFlagForWelshRequiredasWithStatusCode_404()
        throws JsonProcessingException {

        var errorResponseMap = commonDataApiClient.retrieveCaseFlagsByServiceId(
            "XXXX?flag-type=case&welsh-required=y",
            ErrorResponse.class,
            path
        );
        assertNotNull(errorResponseMap);
        assertThat((Map<String, Object>) errorResponseMap).containsEntry("http_status", HttpStatus.NOT_FOUND);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldRetrieveCaseFlagForWelshRequiredFlagWithNStatusCode_200()
        throws JsonProcessingException {
        final var response = (CaseFlag) commonDataApiClient.retrieveCaseFlagsByServiceId(
            "AAA1" + "?available-external-flag=n&welsh-required=n",
            CaseFlag.class,
            path
        );
        assertEquals(2, response.getFlags().get(0).getFlagDetails().size());
        verifyResponse(response.getFlags().get(0).getFlagDetails());
        List<FlagDetail> flagDetailList = response.getFlags().get(0).getFlagDetails();
        for (FlagDetail flagDetail : flagDetailList) {
            assertEquals(false, flagDetail.getExternallyAvailable() == null);
            assertEquals(false, flagDetail.getDefaultStatus() == null);
            if (flagDetail.getParent()) {
                flagDetail.getChildFlags().forEach(cf -> assertNull(cf.getNameCy()));
                flagDetail.getChildFlags().forEach(childFlagObj -> {
                    if (childFlagObj.getListOfValuesLength() != null && childFlagObj.getListOfValuesLength() > 0) {
                        childFlagObj.getListOfValues().forEach(lov -> assertNull(lov.getValueCy()));
                    }
                });
            }
        }
    }

    @Test
    void shouldRetrieveCaseFlagForWelshRequiredFlagWithYStatusCode_200()
        throws JsonProcessingException {
        final var response = (CaseFlag) commonDataApiClient.retrieveCaseFlagsByServiceId(
            "AAA1" + "?available-external-flag=N&welsh-required=y",
            CaseFlag.class,
            path
        );
        assertEquals(2, response.getFlags().get(0).getFlagDetails().size());
        List<FlagDetail> flagDetailList = response.getFlags().get(0).getFlagDetails();
        for (FlagDetail flagDetail : flagDetailList) {
            if (flagDetail.getParent()) {
                flagDetail.getChildFlags().forEach(cf -> {
                    if (!cf.getName().equalsIgnoreCase("Other")) {
                        assertNotNull(cf.getNameCy());
                        assertNotNull(cf.getExternallyAvailable());
                        assertNotNull(cf.getDefaultStatus());
                    }
                });
            }
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldRetrieveCaseFlagForAvailableExternalFlagForNForGetNameCyAndLovWithStatusCode_200()
        throws JsonProcessingException {

        final var response = (CaseFlag) commonDataApiClient.retrieveCaseFlagsByServiceId(
            "AAA1" + "?available-external-flag=N&welsh-required=Y",
            CaseFlag.class,
            path
        );
        assertEquals(2, response.getFlags().get(0).getFlagDetails().size());
        List<FlagDetail> flagDetailList = response.getFlags().get(0).getFlagDetails();
        for (FlagDetail flagDetail : flagDetailList) {
            if (flagDetail.getParent()) {
                for (FlagDetail childFlag : flagDetail.getChildFlags()) {
                    //child flag
                    if (childFlag.getFlagCode().equalsIgnoreCase("PF0027")) {
                        assertEquals("Test27", childFlag.getNameCy());
                    }
                    //List Of Values
                    if (childFlag.getFlagCode().equals("PF0015")) {
                        assertEquals(1, childFlag.getListOfValuesLength());
                        assertEquals("test2", childFlag.getListOfValues().get(0).getValueCy());
                    }
                }
            }
        }
    }

    private void verifyResponse(List<FlagDetail> flagDetails) {
        for (FlagDetail flagDetail : flagDetails) {
            if (flagDetail.getName().equalsIgnoreCase(FlagType.CASE.name())) {
                assertEquals(2, flagDetail.getChildFlags().size());
            }
            if (flagDetail.getName().equalsIgnoreCase(FlagType.PARTY.name())) {
                assertEquals(7, flagDetail.getChildFlags().size());
            }
            switch (flagDetail.getFlagCode()) {
                case "RA0004":
                    assertEquals("I need adjustments to get to, into and around our buildings",
                                 flagDetail.getName());
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
