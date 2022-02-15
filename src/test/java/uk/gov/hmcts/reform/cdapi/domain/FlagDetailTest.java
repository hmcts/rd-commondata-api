package uk.gov.hmcts.reform.cdapi.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FlagDetailTest {

    @Test
    void testFlagDetail() {
        var flagDetail1 = FlagDetail.builder()
            .name("PARTY")
            .flagCode("CATEGORY")
            .id(1)
            .cateGoryId(1)
            .flagComment(true)
            .path(Arrays.asList("party", "ra"))
            .hearingRelevant(false)
            .listOfValues(new ArrayList<>())
            .listOfValuesLength(0)
            .childFlags(new ArrayList<>())
            .parent(true)
            .flagComment(true)
            .build();
        assertEquals("PARTY", flagDetail1.getName());
        assertEquals("CATEGORY", flagDetail1.getFlagCode());
        assertEquals(1, flagDetail1.getId());
        assertEquals(1, flagDetail1.getCateGoryId());
        assertEquals(true, flagDetail1.getFlagComment());
        assertEquals(Arrays.asList("party", "ra"), flagDetail1.getPath());
        assertNotNull(flagDetail1.getListOfValues());
        assertNotNull(flagDetail1.getChildFlags());
        assertEquals(0, flagDetail1.getListOfValuesLength());
        assertEquals(true, flagDetail1.getParent());
        assertEquals(false, flagDetail1.getHearingRelevant());
        assertEquals(true, flagDetail1.getFlagComment());
    }
}
