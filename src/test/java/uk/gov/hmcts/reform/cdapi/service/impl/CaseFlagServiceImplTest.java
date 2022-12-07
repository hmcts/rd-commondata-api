package uk.gov.hmcts.reform.cdapi.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.cdapi.domain.CaseFlag;
import uk.gov.hmcts.reform.cdapi.domain.CaseFlagDto;
import uk.gov.hmcts.reform.cdapi.domain.FlagDetail;
import uk.gov.hmcts.reform.cdapi.domain.ListOfValue;
import uk.gov.hmcts.reform.cdapi.exception.ResourceNotFoundException;
import uk.gov.hmcts.reform.cdapi.repository.CaseFlagRepository;
import uk.gov.hmcts.reform.cdapi.repository.ListOfVenueRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.cdapi.controllers.constant.Constant.FLAG_PF0015;
import static uk.gov.hmcts.reform.cdapi.controllers.constant.Constant.FLAG_RA0042;

@ExtendWith(MockitoExtension.class)
class CaseFlagServiceImplTest {

    @InjectMocks
    CaseFlagServiceImpl caseFlagService;

    @Mock
    CaseFlagRepository caseFlagRepository;

    @Mock
    ListOfVenueRepository listOfVenueRepository;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(caseFlagService, "flaglistLov",
                                     Arrays.asList("PF0015", "RA0042")
        );
    }

    @Test
    void testGetCaseFlag_ByServiceId_Returns200() {
        when(caseFlagRepository.findAll(anyString())).thenReturn(getCaseFlagDtoList());
        var caseFlag = caseFlagService.retrieveCaseFlagByServiceId("XXXX", "", "N");
        assertNotNull(caseFlag);
        assertEquals(1, caseFlag.getFlags().size());
        assertEquals(2, caseFlag.getFlags().get(0).getFlagDetails().size());
        verify(caseFlagRepository, times(1)).findAll(anyString());
    }

    @Test
    void testGetCaseFlag_ByServiceId_Returns_200() {
        when(caseFlagRepository.findAll(anyString())).thenReturn(getCaseFlagDtoList());
        var caseFlag = caseFlagService.retrieveCaseFlagByServiceId("XXXX", "", "N");
        assertNotNull(caseFlag);
        assertEquals(2, caseFlag.getFlags().get(0).getFlagDetails().size());
        assertEquals(1, caseFlag.getFlags().size());
        verify(caseFlagRepository, times(1)).findAll(anyString());
    }

    @Test
    void testGetCaseFlag_ByServiceId_Welsh_Y_Returns_200() {
        when(caseFlagRepository.findAll(anyString())).thenReturn(getCaseFlagDtoList());
        var caseFlag = caseFlagService.retrieveCaseFlagByServiceId("XXXX", "", "Y");
        assertNotNull(caseFlag);
        assertEquals(2, caseFlag.getFlags().get(0).getFlagDetails().size());
        assertEquals(1, caseFlag.getFlags().size());
        verify(caseFlagRepository, times(1)).findAll(anyString());
    }

    @Test
    void testGetCaseFlag_ByServiceId_Welsh_N_Returns_200() {
        when(caseFlagRepository.findAll(anyString())).thenReturn(getCaseFlagDtoList());
        var caseFlag = caseFlagService.retrieveCaseFlagByServiceId("XXXX", "", "N");
        assertNotNull(caseFlag);
        assertEquals(2, caseFlag.getFlags().get(0).getFlagDetails().size());
        assertEquals(1, caseFlag.getFlags().size());
        verify(caseFlagRepository, times(1)).findAll(anyString());
    }

    @Test
    void testGetCaseFlag_ByServiceIdAndFlagTypeParty() {
        when(caseFlagRepository.findAll(anyString())).thenReturn(getCaseFlagDtoList());
        var caseFlag = caseFlagService.retrieveCaseFlagByServiceId("XXXX", "PARTY", null);
        assertNotNull(caseFlag);
        assertEquals(1, caseFlag.getFlags().get(0).getFlagDetails().size());
        assertEquals("PARTY", caseFlag.getFlags().get(0).getFlagDetails().get(0).getName());
        verify(caseFlagRepository, times(1)).findAll(anyString());
    }

    @Test
    void testGetCaseFlag_ByServiceIdAndFlagTypeCase() {
        when(caseFlagRepository.findAll(anyString())).thenReturn(getCaseFlagDtoList());
        var caseFlag = caseFlagService.retrieveCaseFlagByServiceId("XXXX", "CASE", "n");
        assertNotNull(caseFlag);
        assertEquals(1, caseFlag.getFlags().get(0).getFlagDetails().size());
        assertEquals("CASE", caseFlag.getFlags().get(0).getFlagDetails().get(0).getName());
        verify(caseFlagRepository, times(1)).findAll(anyString());
    }

    @Test
    void testGetCaseFlag_ByServiceIdAndWelshRequiredasY() {
        when(caseFlagRepository.findAll(anyString())).thenReturn(getCaseFlagDtoList());
        assertThrows(ResourceNotFoundException.class, () ->
            caseFlagService.retrieveCaseFlagByServiceId("XXXX", "CASE", "y"));
    }

    @Test
    void testGetCaseFlag_WhenLanguageInterpreterFlag_return200() {
        when(caseFlagRepository.findAll(anyString())).thenReturn(getCaseFlagDtoListWithLanguageInterpreter());
        when(listOfVenueRepository.findListOfValues(anyString())).thenReturn(getListOfValuesForLanguageInterPreter());
        var caseFlag = caseFlagService.retrieveCaseFlagByServiceId("XXXX", "PARTY", "");
        assertNotNull(caseFlag);
        verify(caseFlagRepository, times(1)).findAll(anyString());
        verify(listOfVenueRepository, times(1)).findListOfValues(anyString());
        verifyListOfValuesResponse(caseFlag);
    }

    @Test
    void testGetCaseFlag_WhenSignLanguageFlag_return200() {
        when(caseFlagRepository.findAll(anyString())).thenReturn(getCaseFlagDtoListWithSignLanguage());
        when(listOfVenueRepository.findListOfValues(anyString())).thenReturn(getListOfValuesForSignLanguage());
        var caseFlag = caseFlagService.retrieveCaseFlagByServiceId("XXXX", "PARTY", "");
        assertNotNull(caseFlag);
        verify(caseFlagRepository, times(1)).findAll(anyString());
        verify(listOfVenueRepository, times(1)).findListOfValues(anyString());
        verifyListOfValuesResponse(caseFlag);
    }

    @Test
    void testGetCaseFlag_WhenNoDataFound_return404() {
        when(caseFlagRepository.findAll(anyString())).thenReturn(getCaseFlagDtoList());
        assertThrows(
            ResourceNotFoundException.class,
            () -> caseFlagService.retrieveCaseFlagByServiceId("XXXX", "Hello", "")
        );
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
        caseFlagDto1.setDefaultStatus("Active");
        caseFlagDto1.setExternallyAvailable(true);
        caseFlagDto1.setIsParent(true);

        var caseFlagDto2 = new CaseFlagDto();
        caseFlagDto2.setFlagCode("CATEGORY");
        caseFlagDto2.setCategoryId(1);
        caseFlagDto2.setCategoryPath("PARTY");
        caseFlagDto2.setId(2);
        caseFlagDto2.setHearingRelevant(true);
        caseFlagDto2.setRequestReason(false);
        caseFlagDto2.setValueEn("REASONABLE ADJUSTMENT");
        caseFlagDto2.setValueCy("");
        caseFlagDto2.setDefaultStatus("Active");
        caseFlagDto2.setExternallyAvailable(false);
        caseFlagDto2.setIsParent(true);

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
        caseFlagDto1.setDefaultStatus("Active");
        caseFlagDto1.setExternallyAvailable(false);

        var caseFlagDto4 = new CaseFlagDto();
        caseFlagDto4.setFlagCode("CATEGORY");
        caseFlagDto4.setCategoryId(0);
        caseFlagDto4.setCategoryPath("");
        caseFlagDto4.setId(4);
        caseFlagDto4.setHearingRelevant(true);
        caseFlagDto4.setRequestReason(false);
        caseFlagDto4.setValueEn("CASE");
        caseFlagDto4.setValueCy("");
        caseFlagDto4.setIsParent(true);
        caseFlagDto4.setDefaultStatus("Active");
        caseFlagDto4.setExternallyAvailable(false);

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
        caseFlagDto5.setDefaultStatus("Active");
        caseFlagDto5.setExternallyAvailable(false);


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
        caseFlagDto1.setValueCy("");
        caseFlagDto1.setIsParent(true);

        var caseFlagDto2 = new CaseFlagDto();
        caseFlagDto2.setFlagCode("PF0015");
        caseFlagDto2.setCategoryId(1);
        caseFlagDto2.setCategoryPath("PARTY");
        caseFlagDto2.setId(3);
        caseFlagDto2.setHearingRelevant(true);
        caseFlagDto2.setRequestReason(false);
        caseFlagDto2.setValueEn("Language Interpreter");
        caseFlagDto2.setValueCy("");
        caseFlagDto2.setIsParent(false);

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
        caseFlagDto1.setValueCy("");
        caseFlagDto1.setIsParent(true);

        var caseFlagDto2 = new CaseFlagDto();
        caseFlagDto2.setFlagCode("RA0042");
        caseFlagDto2.setCategoryId(1);
        caseFlagDto2.setCategoryPath("PARTY");
        caseFlagDto2.setValueCy("");
        caseFlagDto2.setId(2);
        caseFlagDto2.setHearingRelevant(true);
        caseFlagDto2.setRequestReason(false);
        caseFlagDto2.setValueEn("Sign Language");
        caseFlagDto2.setValueCy("");
        caseFlagDto2.setIsParent(false);

        var caseFlagDtoList = new ArrayList<CaseFlagDto>();
        caseFlagDtoList.add(caseFlagDto1);
        caseFlagDtoList.add(caseFlagDto2);

        return caseFlagDtoList;
    }

    private List<ListOfValue> getListOfValuesForLanguageInterPreter() {
        var list = new ListOfValue();
        list.setId("1");
        list.setKey("EN");
        list.setValue("ENGLISH");


        var listOfValues = new ArrayList<ListOfValue>();
        listOfValues.add(list);
        return listOfValues;
    }

    private List<ListOfValue> getListOfValuesForSignLanguage() {
        var list = new ListOfValue();
        list.setId("2");
        list.setKey("AF");
        list.setValue("AFRICAN");

        var listOfValues = new ArrayList<ListOfValue>();
        listOfValues.add(list);
        return listOfValues;
    }

    private void verifyListOfValuesResponse(CaseFlag caseFlag) {
        for (FlagDetail flagDetail : caseFlag.getFlags()
            .get(0).getFlagDetails().get(0).getChildFlags()) {
            if (flagDetail.getListOfValues() != null) {
                assertEquals(1, flagDetail.getListOfValuesLength());
            }
            if (flagDetail.getFlagCode().equals(FLAG_RA0042)) {
                assertNull(flagDetail.getChildFlags());
                assertEquals(getListOfValuesForSignLanguage(), flagDetail.getListOfValues());
            }
            if (flagDetail.getFlagCode().equals(FLAG_PF0015)) {
                assertNull(flagDetail.getChildFlags());
                assertEquals(getListOfValuesForLanguageInterPreter(), flagDetail.getListOfValues());
            }
        }
        assertEquals(2, caseFlag.getFlags()
            .get(0).getFlagDetails().get(0).getChildFlags().size());
    }
}
