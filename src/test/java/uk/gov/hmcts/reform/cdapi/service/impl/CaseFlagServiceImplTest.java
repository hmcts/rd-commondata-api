package uk.gov.hmcts.reform.cdapi.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ResourceUtils;
import uk.gov.hmcts.reform.cdapi.domain.CaseFlag;
import uk.gov.hmcts.reform.cdapi.domain.CaseFlagDto;
import uk.gov.hmcts.reform.cdapi.domain.FlagDetail;
import uk.gov.hmcts.reform.cdapi.domain.ListOfValue;
import uk.gov.hmcts.reform.cdapi.exception.ResourceNotFoundException;
import uk.gov.hmcts.reform.cdapi.repository.CaseFlagRepository;
import uk.gov.hmcts.reform.cdapi.repository.ListOfVenueRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.cdapi.controllers.constant.Constant.FLAG_PF0015;
import static uk.gov.hmcts.reform.cdapi.controllers.constant.Constant.FLAG_RA0042;
import static uk.gov.hmcts.reform.cdapi.service.impl.CaseFlagServiceImpl.IGNORE_JSON;

@ExtendWith(MockitoExtension.class)
class CaseFlagServiceImplTest {

    @InjectMocks
    CaseFlagServiceImpl caseFlagService;

    @Mock
    CaseFlagRepository caseFlagRepository;

    @Mock
    ListOfVenueRepository listOfVenueRepository;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(caseFlagService, "flaglistLov",
                                     Arrays.asList("PF0015", "RA0042")
        );
    }

    @ParameterizedTest
    @CsvSource({
        "XXXX,,N,N",
        "XXXX,PARTY,null,null",
        "XXXX,CASE,n,n"
    })
    void testGetCaseFlag_ByServiceId_Returns200(String serviceId,String flagType,String welshRequired,
                                                String availableExternalFlag) {
        when(caseFlagRepository.findAll(anyString())).thenReturn(getCaseFlagDtoList());
        var caseFlag = caseFlagService
            .retrieveCaseFlagByServiceId(serviceId, flagType, welshRequired, availableExternalFlag);
        assertNotNull(caseFlag);
        assertEquals(1, caseFlag.getFlags().size());
        if (!(flagType == null)) {
            assertEquals(flagType, caseFlag.getFlags().get(0).getFlagDetails().get(0).getName());
        } else {
            assertEquals(2, caseFlag.getFlags().get(0).getFlagDetails().size());
        }

        verify(caseFlagRepository, times(1)).findAll(anyString());
    }

    @Test
    void testGetCaseFlag_ByServiceIdAndWelshRequiredasY() {
        when(caseFlagRepository.findAll(anyString())).thenReturn(getCaseFlagDtoList());
        assertThrows(ResourceNotFoundException.class, () ->
            caseFlagService.retrieveCaseFlagByServiceId("XXXX", "TEST", "y", "n"));
    }

    @ParameterizedTest
    @CsvSource({
        "XXXX,PARTY,N,false",
        "XXXX,PARTY,Y,true",
        "XXXX,PARTY,N,false"
    })
    void testGetCaseFlags(String serviceId,String flagType,String welshRequired,
                                                               boolean flag) {
        when(caseFlagRepository.findAll(anyString()))
            .thenReturn(getCaseFlagDtoListWithLanguageInterpreter());
        when(listOfVenueRepository.findListOfValues(anyString()))
            .thenReturn(getListOfValuesForLanguageInterPreter(flag));
        var caseFlag = caseFlagService.retrieveCaseFlagByServiceId(serviceId, flagType, welshRequired, "");
        assertNotNull(caseFlag);
        verify(caseFlagRepository, times(1)).findAll(anyString());
        verify(listOfVenueRepository, times(1)).findListOfValues(anyString());
        verifyListOfValuesResponse(caseFlag, flag);
    }

    @Test
    void testGetCaseFlag_WhenNoDataFound_return404() {
        when(caseFlagRepository.findAll(anyString())).thenReturn(getCaseFlagDtoList());
        assertThrows(
            ResourceNotFoundException.class,
            () -> caseFlagService.retrieveCaseFlagByServiceId("XXXX", "Hello", "", "")
        );
    }

    @ParameterizedTest
    @CsvSource({
        "XXXX,Y,N",
        "XXXX,N,N",
        "XXXX,N,Y",
        "XXXX,Y,Y",
        "XXXX,N,N"
    })
    void testGetCaseFlag_ByServiceIWithWelshRequired200(String serviceId, String welshRequired,
                                                                   String availableExternalFlag) {
        when(caseFlagRepository.findAll(anyString())).thenReturn(getCaseFlagDtoList());
        var caseFlag = caseFlagService.retrieveCaseFlagByServiceId(serviceId, "", welshRequired,
                                                                   availableExternalFlag);
        assertNotNull(caseFlag);
        assertEquals(1, caseFlag.getFlags().size());
        assertEquals(2, caseFlag.getFlags().get(0).getFlagDetails().size());
        verify(caseFlagRepository, times(1)).findAll(anyString());
        caseFlag.getFlags().forEach(caseFlagObj -> {
            for (FlagDetail flagDetail : caseFlagObj.getFlagDetails()) {
                if (("N").equals(welshRequired)) {
                    assertEquals(IGNORE_JSON, flagDetail.getNameCy());
                }
                assertNotNull(flagDetail.getDefaultStatus());
                assertNotNull(flagDetail.getExternallyAvailable());
                assertTrue(flagDetail.getExternallyAvailable());
            }
        });
    }

    @Test
    void testGetCaseFlag_ByServiceIWithFlagDetailsNull_Returns200() {
        when(caseFlagRepository.findAll(anyString())).thenReturn(getEmptyCaseFlagDtoList(getCaseFlagDtoList()));
        when(listOfVenueRepository.findListOfValues(anyString()))
            .thenReturn(getListOfValuesForLanguageInterPreter(false));

        var caseFlag = caseFlagService.retrieveCaseFlagByServiceId("XXXX", "", "N", "Y");
        assertNotNull(caseFlag);
        assertEquals(1, caseFlag.getFlags().size());
        assertEquals(2, caseFlag.getFlags().get(0).getFlagDetails().size());
        verify(caseFlagRepository, times(1)).findAll(anyString());
        caseFlag.getFlags().forEach(caseFlagObj -> {
            for (FlagDetail flagDetail : caseFlagObj.getFlagDetails()) {
                assertNotNull(flagDetail.getDefaultStatus());
                assertNotNull(flagDetail.getExternallyAvailable());
            }
        });
    }

    @Test
    @DisplayName("Positive scenario -Should return 200 with Welsh-Required=N and available-externally=N")
    void testGetCaseFlag_ByServiceIWithWelshRequiredIsNandAvailableExternallyIsN_Returns200() {
        when(caseFlagRepository.findAll(anyString())).thenReturn(getCaseFlagDtoList());
        var caseFlag = caseFlagService.retrieveCaseFlagByServiceId("XXXX", "", "N", "N");
        assertNotNull(caseFlag);
        assertEquals(1, caseFlag.getFlags().size());
        assertEquals(2, caseFlag.getFlags().get(0).getFlagDetails().size());
        verify(caseFlagRepository, times(1)).findAll(anyString());
        caseFlag.getFlags().forEach(caseFlagObj -> {
            for (FlagDetail flagDetail : caseFlagObj.getFlagDetails()) {
                assertEquals(IGNORE_JSON, flagDetail.getNameCy());
                assertNotNull(flagDetail.getDefaultStatus());
                assertNotNull(flagDetail.getExternallyAvailable());
            }
        });
    }

    @ParameterizedTest
    @CsvSource({
        "XXXX,PARTY,N,N",
        "XXXX,PARTY,N,Y"
    })
    @DisplayName("Externally available true or false flags")
    void externallyAvailableFlagsShouldBeIgnored(String serviceId,
                                                 String flagType,
                                                 String welshRequired,
                                                 String availableExternalFlag) throws IOException {
        List<CaseFlagDto> caseFlagDtoList = readFlagDetails();
        when(caseFlagRepository.findAll(anyString())).thenReturn(caseFlagDtoList);
        var caseFlag = caseFlagService.retrieveCaseFlagByServiceId(serviceId,
                                                                   flagType,
                                                                   welshRequired,
                                                                   availableExternalFlag);
        validateCaseFlags(caseFlag, availableExternalFlag);
    }

    private Boolean booleanValue(String value) {
        return value.equalsIgnoreCase("y")
            || value.equalsIgnoreCase("true");
    }

    private void validateCaseFlags(CaseFlag caseFlags, String flag) {
        boolean externallyAvailable = (StringUtils.isNotEmpty(flag) && (flag.trim().equalsIgnoreCase("y")));
        assertNotNull(caseFlags);
        assertNotNull(caseFlags.getFlags());
        if (externallyAvailable) {
            caseFlags.getFlags().stream().forEach(caseFlag -> {
                assertNotNull(caseFlag.getFlagDetails());
                List<FlagDetail> flagDetailsList = caseFlag.getFlagDetails();
                flagDetailsList.stream().forEach(flagDetail -> {
                    boolean flagExternallyAvailable = flagDetail.getExternallyAvailable();
                    assertThat(flagExternallyAvailable, anyOf(is(true), is(false)));
                    assertTrue(flagDetail.getParent());
                    validateChildFlags(flagDetail.getChildFlags(), externallyAvailable);
                });
            });
        } else {
            caseFlags.getFlags().stream().forEach(caseFlag -> {
                assertNotNull(caseFlag.getFlagDetails());
                List<FlagDetail> flagDetailsList = caseFlag.getFlagDetails();
                flagDetailsList.stream().forEach(flagDetail -> {
                    boolean flagExternallyAvailable = flagDetail.getExternallyAvailable();
                    assertThat(flagExternallyAvailable, anyOf(is(false)));
                    assertTrue(flagDetail.getParent());
                    validateChildFlags(flagDetail.getChildFlags(), externallyAvailable);
                });
            });
        }
    }

    private void validateChildFlags(List<FlagDetail> flagDetails,
                                    boolean externallyAvailable) {
        if (flagDetails != null) {
            flagDetails.stream()
                .forEach(flagDetail -> {
                    if (externallyAvailable) {
                        assertThat(flagDetail.getExternallyAvailable(), anyOf(is(false), is(true)));
                    } else {
                        assertThat(flagDetail.getExternallyAvailable(), anyOf(is(false)));
                    }
                    validateChildFlags(flagDetail.getChildFlags(), externallyAvailable);
                });
        }
    }

    List<CaseFlagDto> getEmptyCaseFlagDtoList(List<CaseFlagDto> caseFlagDtoList) {
        var caseFlagDto6 = new CaseFlagDto();
        caseFlagDto6.setFlagCode("CATEGORY");
        caseFlagDto6.setCategoryId(4);
        caseFlagDto6.setCategoryPath("CASE");
        caseFlagDto6.setId(5);
        caseFlagDto6.setHearingRelevant(true);
        caseFlagDto6.setRequestReason(false);
        caseFlagDto6.setValueEn("COMPLEX CASE");
        caseFlagDto6.setValueCy("");
        caseFlagDto6.setIsParent(false);
        caseFlagDto6.setExternallyAvailable(false);
        caseFlagDto6.setDefaultStatus("Requested");
        var caseFlagDto3 = new CaseFlagDto();
        caseFlagDto3.setFlagCode("PF0015");
        caseFlagDto3.setCategoryId(2);
        caseFlagDto3.setCategoryPath("PARTY/REASONABLE ADJUSTMENT");
        caseFlagDto3.setId(3);
        caseFlagDto3.setHearingRelevant(true);
        caseFlagDto3.setRequestReason(false);
        caseFlagDto3.setValueEn("CHILD OF REASONABLE ADJUSTMENT");
        caseFlagDto3.setValueCy("");
        caseFlagDto3.setIsParent(false);
        caseFlagDto3.setExternallyAvailable(true);
        caseFlagDto3.setDefaultStatus("Requested");
        caseFlagDtoList.add(caseFlagDto3);
        return caseFlagDtoList;
    }

    List<CaseFlagDto> getCaseFlagDtoList() {
        var caseFlagDto1 = new CaseFlagDto();
        caseFlagDto1.setFlagCode("CATEGORY");
        caseFlagDto1.setCategoryId(0);
        caseFlagDto1.setCategoryPath("");
        caseFlagDto1.setId(1);
        caseFlagDto1.setHearingRelevant(true);
        caseFlagDto1.setRequestReason(false);
        caseFlagDto1.setValueEn("PARTY");
        caseFlagDto1.setValueCy("");
        caseFlagDto1.setIsParent(true);
        caseFlagDto1.setFlagCode("CATEGORY");
        caseFlagDto1.setExternallyAvailable(true);
        caseFlagDto1.setDefaultStatus("Requested");

        var caseFlagDto2 = new CaseFlagDto();
        caseFlagDto2.setCategoryId(1);
        caseFlagDto2.setCategoryPath("PARTY");
        caseFlagDto2.setId(2);
        caseFlagDto2.setHearingRelevant(true);
        caseFlagDto2.setRequestReason(false);
        caseFlagDto2.setValueEn("REASONABLE ADJUSTMENT");
        caseFlagDto2.setValueCy("");
        caseFlagDto2.setIsParent(true);
        caseFlagDto2.setExternallyAvailable(true);
        caseFlagDto2.setDefaultStatus("Active");

        var caseFlagDto3 = new CaseFlagDto();
        caseFlagDto3.setFlagCode("FLAG001");
        caseFlagDto3.setCategoryId(2);
        caseFlagDto3.setCategoryPath("PARTY/REASONABLE ADJUSTMENT");
        caseFlagDto3.setId(3);
        caseFlagDto3.setHearingRelevant(true);
        caseFlagDto3.setRequestReason(false);
        caseFlagDto3.setValueEn("CHILD OF REASONABLE ADJUSTMENT");
        caseFlagDto3.setValueCy("");
        caseFlagDto3.setIsParent(false);
        caseFlagDto3.setExternallyAvailable(true);
        caseFlagDto3.setDefaultStatus("Requested");

        var caseFlagDto4 = new CaseFlagDto();
        caseFlagDto4.setFlagCode("CATEGORY");
        caseFlagDto4.setCategoryId(0);
        caseFlagDto4.setCategoryPath("");
        caseFlagDto4.setId(4);
        caseFlagDto4.setHearingRelevant(true);
        caseFlagDto4.setRequestReason(false);
        caseFlagDto4.setValueEn("CASE");
        caseFlagDto4.setValueCy("");
        caseFlagDto4.setIsParent(false);
        caseFlagDto4.setExternallyAvailable(true);
        caseFlagDto4.setDefaultStatus("Active");

        var caseFlagDto5 = new CaseFlagDto();
        caseFlagDto5.setFlagCode("CATEGORY");
        caseFlagDto5.setCategoryId(4);
        caseFlagDto5.setCategoryPath("CASE");
        caseFlagDto5.setId(5);
        caseFlagDto5.setHearingRelevant(true);
        caseFlagDto5.setRequestReason(false);
        caseFlagDto5.setValueEn("COMPLEX CASE");
        caseFlagDto5.setValueCy("");
        caseFlagDto5.setIsParent(false);
        caseFlagDto5.setExternallyAvailable(true);
        caseFlagDto5.setDefaultStatus("Requested");


        var caseFlagDtoList = new ArrayList<CaseFlagDto>();
        caseFlagDtoList.add(caseFlagDto1);
        caseFlagDtoList.add(caseFlagDto2);
        caseFlagDtoList.add(caseFlagDto3);
        caseFlagDtoList.add(caseFlagDto4);
        caseFlagDtoList.add(caseFlagDto5);
        return caseFlagDtoList;
    }

    List<CaseFlagDto> getCaseFlagDtoListWithLanguageInterpreter() {
        var caseFlagDto1 = new CaseFlagDto();
        caseFlagDto1.setFlagCode("CATEGORY");
        caseFlagDto1.setCategoryId(0);
        caseFlagDto1.setCategoryPath("");
        caseFlagDto1.setId(1);
        caseFlagDto1.setHearingRelevant(true);
        caseFlagDto1.setRequestReason(false);
        caseFlagDto1.setValueEn("PARTY");
        caseFlagDto1.setValueCy("PARTY");
        caseFlagDto1.setIsParent(true);

        var caseFlagDto2 = new CaseFlagDto();
        caseFlagDto2.setFlagCode("PF0015");
        caseFlagDto2.setCategoryId(1);
        caseFlagDto2.setCategoryPath("PARTY");
        caseFlagDto2.setId(3);
        caseFlagDto2.setHearingRelevant(true);
        caseFlagDto2.setRequestReason(false);
        caseFlagDto2.setValueEn("Language Interpreter");
        caseFlagDto2.setValueCy("Language Interpreter");
        caseFlagDto2.setIsParent(false);
        caseFlagDto1.setExternallyAvailable(false);
        caseFlagDto1.setDefaultStatus("Requested");

        var caseFlagDtoList = new ArrayList<CaseFlagDto>();
        caseFlagDtoList.add(caseFlagDto1);
        caseFlagDtoList.add(caseFlagDto2);

        return caseFlagDtoList;
    }

    private List<CaseFlagDto> getCaseFlagDtoListWithSignLanguage() {
        var caseFlagDto1 = new CaseFlagDto();
        caseFlagDto1.setFlagCode("CATEGORY");
        caseFlagDto1.setCategoryId(0);
        caseFlagDto1.setCategoryPath("");
        caseFlagDto1.setId(1);
        caseFlagDto1.setHearingRelevant(true);
        caseFlagDto1.setRequestReason(false);
        caseFlagDto1.setValueEn("PARTY");
        caseFlagDto1.setValueCy("PARTY");
        caseFlagDto1.setIsParent(true);
        caseFlagDto1.setExternallyAvailable(false);
        caseFlagDto1.setDefaultStatus("Requested");

        var caseFlagDto2 = new CaseFlagDto();
        caseFlagDto2.setFlagCode("RA0042");
        caseFlagDto2.setCategoryId(1);
        caseFlagDto2.setCategoryPath("PARTY");
        caseFlagDto2.setValueCy("PARTY");
        caseFlagDto2.setId(2);
        caseFlagDto2.setHearingRelevant(true);
        caseFlagDto2.setRequestReason(false);
        caseFlagDto2.setValueEn("Sign Language");
        caseFlagDto2.setValueCy("");
        caseFlagDto2.setIsParent(false);
        caseFlagDto1.setExternallyAvailable(true);
        caseFlagDto1.setDefaultStatus("Approved");

        var caseFlagDtoList = new ArrayList<CaseFlagDto>();
        caseFlagDtoList.add(caseFlagDto1);
        caseFlagDtoList.add(caseFlagDto2);

        return caseFlagDtoList;
    }

    private List<CaseFlagDto> getCaseFlagDtoListWithOther() {
        var caseFlagDto1 = new CaseFlagDto();
        caseFlagDto1.setFlagCode("OT0001");
        caseFlagDto1.setCategoryId(0);
        caseFlagDto1.setCategoryPath("");
        caseFlagDto1.setId(1);
        caseFlagDto1.setHearingRelevant(true);
        caseFlagDto1.setRequestReason(false);
        caseFlagDto1.setValueEn("Other");
        caseFlagDto1.setValueCy("Arall");
        caseFlagDto1.setIsParent(true);
        caseFlagDto1.setExternallyAvailable(false);
        caseFlagDto1.setDefaultStatus("Requested");

        var caseFlagDto2 = new CaseFlagDto();
        caseFlagDto2.setFlagCode("RA0042");
        caseFlagDto2.setCategoryId(1);
        caseFlagDto2.setCategoryPath("PARTY");
        caseFlagDto2.setValueCy("PARTY");
        caseFlagDto2.setId(2);
        caseFlagDto2.setHearingRelevant(true);
        caseFlagDto2.setRequestReason(false);
        caseFlagDto2.setValueEn("Sign Language");
        caseFlagDto2.setValueCy("");
        caseFlagDto2.setIsParent(false);
        caseFlagDto1.setExternallyAvailable(true);
        caseFlagDto1.setDefaultStatus("Approved");

        var caseFlagDtoList = new ArrayList<CaseFlagDto>();
        caseFlagDtoList.add(caseFlagDto1);
        caseFlagDtoList.add(caseFlagDto2);

        return caseFlagDtoList;
    }



    private List<ListOfValue> getListOfValuesForLanguageInterPreter(boolean isWelshRequired) {
        var list = new ListOfValue();
        list.setId("1");
        list.setKey("EN");
        list.setValue("ENGLISH");
        if (isWelshRequired) {
            list.setValueCy("CY ENGLISH");
        } else {
            list.setValueCy(IGNORE_JSON);
        }

        var listOfValues = new ArrayList<ListOfValue>();
        listOfValues.add(list);
        return listOfValues;
    }

    private List<ListOfValue> getListOfValuesForSignLanguage(boolean isWelshRequired) {
        var list = new ListOfValue();
        list.setId("2");
        list.setKey("AF");
        list.setValue("AFRICAN");
        if (isWelshRequired) {
            list.setValueCy("CY AFRICAN");
        } else {
            list.setValueCy(IGNORE_JSON);
        }
        var listOfValues = new ArrayList<ListOfValue>();
        listOfValues.add(list);
        return listOfValues;
    }

    private void verifyListOfValuesResponse(CaseFlag caseFlag, boolean isWelshRequired) {
        for (FlagDetail flagDetail : caseFlag.getFlags()
            .get(0).getFlagDetails().get(0).getChildFlags()) {
            if (flagDetail.getListOfValues() != null) {
                assertEquals(1, flagDetail.getListOfValuesLength());
            }
            if (flagDetail.getFlagCode().equals(FLAG_RA0042)) {
                assertNull(flagDetail.getChildFlags());
                assertEquals(getListOfValuesForSignLanguage(isWelshRequired), flagDetail.getListOfValues());
            }
            if (flagDetail.getFlagCode().equals(FLAG_PF0015)) {
                assertNull(flagDetail.getChildFlags());
                assertEquals(getListOfValuesForLanguageInterPreter(isWelshRequired), flagDetail.getListOfValues());
            }
        }
        assertEquals(2, caseFlag.getFlags()
            .get(0).getFlagDetails().get(0).getChildFlags().size());
    }

    private List<CaseFlagDto> readFlagDetails() throws IOException {
        return MAPPER.readValue(ResourceUtils.getFile("classpath:Flag-Details.json"), new TypeReference<>() {});
    }
}
