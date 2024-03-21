package uk.gov.hmcts.reform.cdapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.cdapi.domain.CaseFlag;
import uk.gov.hmcts.reform.cdapi.domain.FlagDetail;
import uk.gov.hmcts.reform.cdapi.domain.FlagType;
import uk.gov.hmcts.reform.cdapi.exception.ErrorResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@WithTags({@WithTag("testType:Integration")})
class RetrieveCaseFlagsIntegrationTest extends CdAuthorizationEnabledIntegrationTest {
    private static final String path = "/caseflags/service-id={service-id}";

    @ParameterizedTest
    @ValueSource(strings = {"AAA1", "XXXX"})
    void shouldRetrieveCaseFlagForServiceIdWithStatusCode_200(String serviceId)
        throws JsonProcessingException {

        final var response = (CaseFlag) commonDataApiClient
            .retrieveCaseFlagsByServiceId(serviceId, CaseFlag.class, path);
        assertNotNull(response);
        if (("AAA1").equals(serviceId)) {
            assertEquals(2, response.getFlags().get(0).getFlagDetails().size());
            verifyResponse(response.getFlags().get(0).getFlagDetails());
        } else {
            assertEquals(1, response.getFlags().get(0).getFlagDetails().size());
        }

    }

    @ParameterizedTest
    @ValueSource(strings = {"party", "case"})
    void shouldRetrieveCaseFlagForServiceIdFlagTypeAsParty(String serviceId)
        throws JsonProcessingException {

        final var response = (CaseFlag) commonDataApiClient.retrieveCaseFlagsByServiceId(
            "AAA1" + "?flag-type=" + serviceId + "&available-external-flag=N",
            CaseFlag.class,
            path
        );
        assertNotNull(response);
        assertEquals(1, response.getFlags().get(0).getFlagDetails().size());
        verifyResponse(response.getFlags().get(0).getFlagDetails());

    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "case"})
    @SuppressWarnings("unchecked")
    void shouldRetrieveCaseFlagForServiceIdWithStatusCode_400(String serviceId)
        throws JsonProcessingException {

        var errorResponseMap = commonDataApiClient.retrieveCaseFlagsByServiceId(
            "XXXX?flag-type=" + serviceId,
            ErrorResponse.class,
            path
        );
        assertNotNull(errorResponseMap);
        if (("hello").equals(serviceId)) {
            assertThat((Map<String, Object>) errorResponseMap).containsEntry("http_status", HttpStatus.BAD_REQUEST);
        } else if (("case").equals(serviceId)) {
            assertThat((Map<String, Object>) errorResponseMap).containsEntry("http_status", HttpStatus.NOT_FOUND);
        }

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
                flagDetail.getChildFlags().forEach(childFlagObj -> {
                    if (childFlagObj.getFlagCode().equalsIgnoreCase("Other")) {
                        flagDetail.getChildFlags().forEach(cf -> assertEquals("Other", cf.getNameCy()));
                    }
                    if (childFlagObj.getListOfValuesLength() != null && childFlagObj.getListOfValuesLength() > 0) {
                        childFlagObj.getListOfValues().forEach(lov -> assertNull(lov.getValueCy()));
                    }
                });
            }
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldRetrieveCaseFlag_WelshRequiredWithNStatusCode_200()
        throws Exception {
        final var responseBody = commonDataApiClient.retrieveCaseFlagsByServiceIdJsonFormat(
            "AAA1" + "?welsh-required=n",
            CaseFlag.class,
            path
        );

        verifyWelshIsNorNullorEmpty(responseBody);
    }

    private void verifyWelshIsNorNullorEmpty(Object responseBody) throws Exception {
        Exception exception = assertThrows(PathNotFoundException.class, () -> {
            this.jsonPathResult(responseBody, "$.flags[0].FlagDetails[0].name_cy");
        });
        String expectedMessage = "No results for path: $['flags'][0]['FlagDetails'][0]['name_cy']";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        assertThat(jsonPathResult(
            responseBody,
            "$.flags[0].FlagDetails[1].childFlags[0].childFlags[0].defaultStatus"
        )).isNotNull().isEqualTo("Active");

        assertThat(this.jsonPathResult(
            responseBody,
            "$.flags[0].FlagDetails[1].childFlags[0].childFlags[0].externallyAvailable"
        )).isNotNull().isEqualTo(false);


        exception = assertThrows(PathNotFoundException.class, () -> {
            this.jsonPathResult(responseBody,
                                "$.flags[0].FlagDetails[1].childFlags[0].childFlags[0].value_cy");
        });
        expectedMessage = "No results for path: "
            + "$['flags'][0]['FlagDetails'][1]['childFlags'][0]['childFlags'][0]['value_cy']";
        actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void shouldRetrieveCaseFlagForWelshRequiredFlagWithYStatusCode_200()
        throws JsonProcessingException {
        final var response = (CaseFlag) commonDataApiClient.retrieveCaseFlagsByServiceId(
            "AAA1" + "?available-external-flag=N&welsh-required=y",
            CaseFlag.class,
            path
        );
        assertResponseContent(response);
    }


    @Test
    void shouldReturnSuccessForRetrieveCaseFlagsByServiceIdWithWelshFlagIsY() throws JsonProcessingException {
        final var response = (CaseFlag) commonDataApiClient.retrieveCaseFlagsByServiceId(
            "AAA1?welsh-required=Y",
            CaseFlag.class,
            path
        );
        assertResponseContent(response);
    }

    @Test
    void shouldReturnSuccessForRetrieveCaseFlagsByServiceIdWithAvailableExternalFlagIsY_200()
        throws JsonProcessingException {
        final var response = (CaseFlag) commonDataApiClient.retrieveCaseFlagsByServiceId(
            "AAA1?flag-type=party&available-external-flag=Y",
            CaseFlag.class,
            path
        );
        assertEquals(1, response.getFlags().get(0).getFlagDetails().size());
        List<FlagDetail> parentFlags = response.getFlags().get(0).getFlagDetails();
        List<String> sampleDefaultStatus = Arrays.asList("Active", "Requested");
        for (FlagDetail parentFlag : parentFlags) {
            if (parentFlag.getParent()) {
                assertNotNull(parentFlag);
                parentFlag.getChildFlags().forEach(childFlag -> {
                    if (!childFlag.getName().equalsIgnoreCase("Other") && !childFlag.getParent()) {
                        assertEquals(true, childFlag.getExternallyAvailable());
                        assertTrue(sampleDefaultStatus.contains(childFlag.getDefaultStatus()));
                    }
                });
            }
        }
    }


    private static void assertResponseContent(CaseFlag response) {
        assertEquals(2, response.getFlags().get(0).getFlagDetails().size());
        List<FlagDetail> flagDetailList = response.getFlags().get(0).getFlagDetails();
        List<String> nameCyFlagCodes = Arrays.asList("PF0011", "PF0012", "PF0015");
        List<String> sampleDefaultStatus = Arrays.asList("Active", "Requested");
        for (FlagDetail flagDetail : flagDetailList) {
            if (flagDetail.getParent()) {
                flagDetail.getChildFlags().forEach(childFlag -> {
                    if (!childFlag.getName().equalsIgnoreCase("Other")) {
                        assertNotNull(childFlag.getExternallyAvailable());
                        assertNotNull(childFlag.getDefaultStatus());
                        if (nameCyFlagCodes.contains(childFlag.getFlagCode())) {
                            assertEquals("Test", childFlag.getNameCy());
                            assertEquals(Boolean.FALSE, childFlag.getExternallyAvailable());
                            assertTrue(sampleDefaultStatus.contains(childFlag.getDefaultStatus()));
                            assertTrue(nameCyFlagCodes.contains(childFlag.getNativeFlagCode()));
                        }
                        if (childFlag.getFlagCode().equalsIgnoreCase("PF0027")) {
                            assertEquals("Test27", childFlag.getNameCy());
                            assertEquals("PF0027", childFlag.getNativeFlagCode());
                        }
                        //List Of Values
                        if (childFlag.getFlagCode().equals("PF0015")) {
                            assertEquals(1, childFlag.getListOfValuesLength());
                            assertEquals("test2", childFlag.getListOfValues().get(0).getValueCy());
                            assertEquals("PF0015", childFlag.getNativeFlagCode());
                        }
                    }
                });
            }
        }
    }

    private Object jsonPathResult(Object responseBody, String jsonPath) throws Exception {
        return JsonPath.read(responseBody.toString(), jsonPath);
    }

    private void verifyResponse(List<FlagDetail> flagDetails) {
        for (FlagDetail flagDetail : flagDetails) {
            if (flagDetail.getName().equalsIgnoreCase(FlagType.CASE.name())) {
                assertEquals(2, flagDetail.getChildFlags().size());
            }
            if (flagDetail.getName().equalsIgnoreCase(FlagType.PARTY.name())) {
                assertEquals(8, flagDetail.getChildFlags().size());
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
