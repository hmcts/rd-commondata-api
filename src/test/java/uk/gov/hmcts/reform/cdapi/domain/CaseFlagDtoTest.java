package uk.gov.hmcts.reform.cdapi.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CaseFlagDtoTest {

    @Test
    void testCaseFlagDto() {
        var caseFlagDto = new CaseFlagDto();
        caseFlagDto.setFlagCode("RA0001");
        caseFlagDto.setCategoryId(1);
        caseFlagDto.setCategoryPath("PARTY");
        caseFlagDto.setId(2);
        caseFlagDto.setHearingRelevant(true);
        caseFlagDto.setRequestReason(false);
        caseFlagDto.setValueEn("E");
        caseFlagDto.setIsParent(false);

        assertEquals("RA0001", caseFlagDto.getFlagCode());
        assertEquals(1, caseFlagDto.getCategoryId());
        assertEquals("PARTY", caseFlagDto.getCategoryPath());
        assertEquals(2, caseFlagDto.getId());
        assertEquals(true, caseFlagDto.getHearingRelevant());
        assertEquals(false, caseFlagDto.getRequestReason());
        assertEquals("E", caseFlagDto.getValueEn());
        assertEquals(false, caseFlagDto.getIsParent());
    }

}
