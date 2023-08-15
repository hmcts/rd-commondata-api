package uk.gov.hmcts.reform.cdapi.controllers;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.hmcts.reform.cdapi.domain.CaseFlag;
import uk.gov.hmcts.reform.cdapi.domain.Flag;
import uk.gov.hmcts.reform.cdapi.domain.FlagDetail;
import uk.gov.hmcts.reform.cdapi.domain.ListOfValue;
import uk.gov.hmcts.reform.cdapi.exception.ResourceNotFoundException;
import uk.gov.hmcts.reform.cdapi.exception.handler.GlobalExceptionHandler;
import uk.gov.hmcts.reform.cdapi.service.impl.CaseFlagServiceImpl;

import java.util.List;
import java.util.stream.Stream;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@WithMockUser
@ContextConfiguration(classes = CaseFlagApiController.class)
class CaseFlagApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private GlobalExceptionHandler globalExceptionHandler;

    @MockBean
    private CaseFlagServiceImpl caseFlagService;


    @Test
    @DisplayName("Positive scenario -Should return 200 with case flags only for service-id")
    void should_return_200_with_caseFlags_for_serviceId() throws Exception {

        //given
        final String serviceId = "XXXX";
        final String flagType = null;
        final String welshRequired = null;
        final String availableExternalFlag = null;

        final CaseFlag caseFlag = createCaseFlag();
        given(caseFlagService.retrieveCaseFlagByServiceId(serviceId, flagType, welshRequired,
                                                          availableExternalFlag
        )).willReturn(caseFlag);

        //when
        final ResultActions resultActions =
            mockMvc.perform(
                    get("/refdata/commondata/caseflags/service-id={service-id}", serviceId)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        //then
        assertResponseContent(resultActions, caseFlag);
        then(caseFlagService).should().retrieveCaseFlagByServiceId(serviceId, flagType, welshRequired,
                                                                   availableExternalFlag
        );
    }

    @Test
    @DisplayName("Positive scenario -Should return 200 with case flags for service-id and flag-type")
    void should_return_200_with_caseFlags_for_serviceId_and_flagType() throws Exception {

        //given
        final String serviceId = "XXXX";
        final String flagType = "CASE";
        final String welshRequired = null;
        final String availableExternalFlag = null;

        final CaseFlag caseFlag = createCaseFlag();
        given(caseFlagService.retrieveCaseFlagByServiceId(serviceId, flagType, welshRequired,
                                                          availableExternalFlag
        )).willReturn(caseFlag);

        //when
        final ResultActions resultActions =
            mockMvc.perform(
                    get("/refdata/commondata/caseflags/service-id={service-id}", serviceId)
                        .queryParam("flag-type", flagType)
                        .queryParam("available-external-flag", availableExternalFlag)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        //then
        assertResponseContent(resultActions, caseFlag);
        then(caseFlagService).should().retrieveCaseFlagByServiceId(serviceId, flagType, welshRequired,
                                                                   availableExternalFlag
        );
    }

    @Test
    @DisplayName("Positive scenario -Should return 200 with case flags for service-id and welsh-required")
    void should_return_200_with_caseFlags_for_serviceId_and_welshRequired() throws Exception {

        //given
        final String serviceId = "XXXX";
        final String flagType = null;
        final String welshRequired = "Y";
        final String availableExternalFlag = "Y";

        final CaseFlag caseFlag = createCaseFlag();
        given(caseFlagService.retrieveCaseFlagByServiceId(serviceId, flagType, welshRequired,
                                                          availableExternalFlag
        )).willReturn(caseFlag);

        //when
        final ResultActions resultActions =
            mockMvc.perform(
                    get("/refdata/commondata/caseflags/service-id={service-id}", serviceId)
                        .queryParam("welsh-required", welshRequired)
                        .queryParam("available-external-flag", availableExternalFlag)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        //then
        assertResponseContent(resultActions, caseFlag);
        then(caseFlagService).should().retrieveCaseFlagByServiceId(serviceId, flagType, welshRequired,
                                                                   availableExternalFlag
        );
    }

    @Test
    @DisplayName("Positive scenario -Should return 200 with case flags for service-id, flag-type and welsh-required")
    void should_return_200_with_caseFlags_For_serviceId_flagType_welshRequired() throws Exception {

        //given
        final String serviceId = "XXXX";
        final CaseFlag caseFlag = createCaseFlag();
        final String flagType = "PARTY";
        final String welshRequired = "Y";
        final String availableExternalFlag = "Y";


        given(caseFlagService.retrieveCaseFlagByServiceId(serviceId, flagType, welshRequired,
                                                          availableExternalFlag
        )).willReturn(caseFlag);

        //when
        final ResultActions resultActions =
            mockMvc.perform(
                    get("/refdata/commondata/caseflags/service-id={service-id}", serviceId)
                        .queryParam("flag-type", flagType)
                        .queryParam("welsh-required", welshRequired)
                        .queryParam("available-external-flag", availableExternalFlag)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        //then
        assertResponseContent(resultActions, caseFlag);
        then(caseFlagService).should().retrieveCaseFlagByServiceId(serviceId, flagType, welshRequired,
                                                                   availableExternalFlag
        );
    }

    @Test
    @DisplayName("Negative scenario - Should return 404 case flags for given service-id, flag-type and welsh-required")
    void should_return_404_for_given_serviceId_flagType_Welsh_required() throws Exception {

        //given
        doThrow(ResourceNotFoundException.class).when(caseFlagService)
            .retrieveCaseFlagByServiceId(anyString(), anyString(), anyString(), anyString());

        //when
        mockMvc.perform(
                get("/refdata/commondata/caseflags/service-id={service-id}", "XXXX")
                    .queryParam("flag-type", "PARTY")
                    .queryParam("welsh-required", "N")
                    .queryParam("available-external-flag", "N")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            //then
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode", is(404)))
            .andExpect(jsonPath("$.status", is("Not Found")))
            .andExpect(jsonPath("$.errorMessage", is("4 : Resource not found")));

        then(caseFlagService).should().retrieveCaseFlagByServiceId(anyString(), anyString(), anyString(),
                                                                   anyString());
    }

    @ParameterizedTest
    @MethodSource("invalidScenarios")
    @DisplayName("Negative scenario - Should return 400 case flags for given service-id, flag-type and welsh-required")
    void should_return_400_for_all_negative_serviceId_flagType_Welsh_required(final String serviceId,
                                                                              final String flagType,
                                                                              final String welshRequired,
                                                                              final String availableExternally,
                                                                              final String expectedErrorDescription)
        throws Exception {

        //when
        mockMvc.perform(
                get("/refdata/commondata/caseflags/service-id={service-id}", serviceId)
                    .queryParam("flag-type", flagType)
                    .queryParam("welsh-required", welshRequired)
                    .queryParam("available-external-flag", availableExternally)
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            //then
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode", is(400)))
            .andExpect(jsonPath("$.status", is("Bad Request")))
            .andExpect(jsonPath(
                "$.errorMessage",
                is("3 : There is a problem with your request. Please check and try again")
            ))
            .andExpect(jsonPath("$.errorDescription", is(expectedErrorDescription)
            ));
    }

    private void assertResponseContent(final ResultActions resultActions,
                                       final CaseFlag caseFlag) throws Exception {
        final FlagDetail parentFlagDetail = caseFlag.getFlags().get(0).getFlagDetails().get(0);
        final FlagDetail childFlagDetail = parentFlagDetail.getChildFlags().get(0);
        final ListOfValue parentListOfValue = parentFlagDetail.getListOfValues().get(0);
        final ListOfValue childListOfValue = childFlagDetail.getListOfValues().get(0);

        resultActions
            .andExpect(jsonPath("$.flags", hasSize(1)))
            .andExpect(jsonPath("$.flags[0].FlagDetails", hasSize(1)))
            .andExpect(jsonPath("$.flags[0].FlagDetails[0].name", is(parentFlagDetail.getName())))
            .andExpect(jsonPath("$.flags[0].FlagDetails[0].hearingRelevant", is(parentFlagDetail.getHearingRelevant())))
            .andExpect(jsonPath("$.flags[0].FlagDetails[0].flagComment", is(parentFlagDetail.getFlagComment())))
            .andExpect(jsonPath("$.flags[0].FlagDetails[0].flagCode", is(parentFlagDetail.getFlagCode())))
            .andExpect(jsonPath("$.flags[0].FlagDetails[0].nativeFlagCode", is(parentFlagDetail.getNativeFlagCode())))
            .andExpect(jsonPath("$.flags[0].FlagDetails[0].isParent", is(parentFlagDetail.getParent())))
            .andExpect(jsonPath(
                "$.flags[0].FlagDetails[0].listOfValuesLength",
                is(parentFlagDetail.getListOfValuesLength())
            ))
            .andExpect(jsonPath("$.flags[0].FlagDetails[0].listOfValuesLength", is(notNullValue())))
            .andExpect(jsonPath("$.flags[0].FlagDetails[0].listOfValues", hasSize(1)))
            .andExpect(jsonPath("$.flags[0].FlagDetails[0].listOfValues[0].key", is(parentListOfValue.getKey())))
            .andExpect(jsonPath("$.flags[0].FlagDetails[0].listOfValues[0].value", is(parentListOfValue.getValue())))
            .andExpect(jsonPath("$.flags[0].FlagDetails[0].Path", is(nullValue())))

            .andExpect(jsonPath("$.flags[0].FlagDetails[0].childFlags", hasSize(1)))
            .andExpect(jsonPath("$.flags[0].FlagDetails[0].childFlags[0].name", is(childFlagDetail.getName())))
            .andExpect(jsonPath(
                "$.flags[0].FlagDetails[0].childFlags[0].hearingRelevant",
                is(childFlagDetail.getHearingRelevant())
            ))
            .andExpect(jsonPath(
                "$.flags[0].FlagDetails[0].childFlags[0].flagComment",
                is(childFlagDetail.getFlagComment())
            ))
            .andExpect(jsonPath("$.flags[0].FlagDetails[0].childFlags[0].flagCode", is(childFlagDetail.getFlagCode())))
            .andExpect(jsonPath("$.flags[0].FlagDetails[0].childFlags[0].nativeFlagCode", is(childFlagDetail.getNativeFlagCode())))
            .andExpect(jsonPath("$.flags[0].FlagDetails[0].childFlags[0].isParent", is(childFlagDetail.getParent())))
            .andExpect(jsonPath("$.flags[0].FlagDetails[0].childFlags[0].childFlags", hasSize(0)))
            .andExpect(jsonPath("$.flags[0].FlagDetails[0].childFlags[0].listOfValuesLength", is(notNullValue())))
            .andExpect(jsonPath("$.flags[0].FlagDetails[0].childFlags[0].listOfValues", hasSize(1)))
            .andExpect(jsonPath(
                "$.flags[0].FlagDetails[0].childFlags[0].listOfValues[0].key",
                is(childListOfValue.getKey())
            ))
            .andExpect(jsonPath(
                "$.flags[0].FlagDetails[0].childFlags[0].listOfValues[0].value",
                is(childListOfValue.getValue())
            ))
            .andExpect(jsonPath("$.flags[0].FlagDetails[0].childFlags[0].isParent", is(childFlagDetail.getParent())))
            .andExpect(jsonPath("$.flags[0].FlagDetails[0].childFlags[0].Path", is(nullValue())));
    }

    private static Stream<Arguments> invalidScenarios() {
        final String serviceIdErrorDesc = "service Id can not be null or empty";
        final String flagTypeErrorDesc = "Allowed values are PARTY or CASE";
        final String welshRequiredErrorDesc = "Allowed values are Y or N";

        return Stream.of(
            arguments("", "PARTY", "Y", "Y", serviceIdErrorDesc),
            arguments(null, "PARTY", "Y", "Y", serviceIdErrorDesc),
            arguments("XXXX", "", "Y", "Y", flagTypeErrorDesc),
            arguments("XXXX", "CASE", "", "", welshRequiredErrorDesc),
            arguments("XXXX", "CASE", "", "", welshRequiredErrorDesc),
            arguments("XXXX", "CASE", "Y", "", welshRequiredErrorDesc)
        );
    }

    @NotNull
    private CaseFlag createCaseFlag() {

        final ListOfValue listOfValue = new ListOfValue();
        listOfValue.setValue(randomAlphabetic(5));
        listOfValue.setId(randomAlphabetic(5));
        listOfValue.setKey(randomAlphabetic(5));
        String flagCode = randomAlphabetic(5);

        final FlagDetail.FlagDetailBuilder flagDetailBuilder =
            FlagDetail.builder()
                .id(nextInt())
                .flagCode(flagCode)
                .nativeFlagCode(flagCode)
                .name(randomAlphabetic(5))
                .cateGoryId(nextInt())
                .flagComment(nextBoolean())
                .hearingRelevant(nextBoolean())
                .parent(nextBoolean())
                .listOfValues(List.of(listOfValue))
                .listOfValuesLength(nextInt());

        final FlagDetail childFlagDetail = flagDetailBuilder.build();
        final FlagDetail parentFlagDetail = flagDetailBuilder.childFlags(List.of(childFlagDetail)).build();
        final Flag flag = Flag.builder().flagDetails(List.of(parentFlagDetail)).build();

        return CaseFlag.builder().flags(List.of(flag)).build();
    }
}
