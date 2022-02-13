package uk.gov.hmcts.reform.cdapi.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FlagTest {

    @Test
    void testFlag() {
        var flag = new Flag();
        flag.setFlagDetails(getFlagDetail());
        assertEquals("PARTY", flag.getFlagDetails().get(0).getName());
        assertEquals("CATEGORY", flag.getFlagDetails().get(0).getFlagCode());
        assertEquals(1, flag.getFlagDetails().get(0).getId());
        assertEquals(1, flag.getFlagDetails().get(0).getCateGoryId());
        assertEquals(true, flag.getFlagDetails().get(0).getFlagComment());
        assertEquals(Arrays.asList("party", "ra"), flag.getFlagDetails().get(0).getPath());
        assertNotNull(flag.getFlagDetails().get(0).getListOfValues());
        assertEquals(0, flag.getFlagDetails().get(0).getListOfValuesLength());
        assertEquals(true, flag.getFlagDetails().get(0).getParent());
        assertEquals(false, flag.getFlagDetails().get(0).getHearingRelevant());
        assertEquals(true, flag.getFlagDetails().get(0).getFlagComment());


    }

    private List<FlagDetail> getFlagDetail() {
        FlagDetail flagDetail1 = FlagDetail.builder()
            .name("PARTY")
            .flagCode("CATEGORY")
            .id(1)
            .cateGoryId(1)
            .flagComment(true)
            .path(Arrays.asList("party", "ra"))
            .hearingRelevant(false)
            .listOfValues(new ArrayList<>())
            .listOfValuesLength(0)
            .parent(true)
            .flagComment(true)
            .build();
        FlagDetail flagDetail2 = FlagDetail.builder()
            .name("CASE")
            .flagCode("CATEGORY")
            .id(2)
            .cateGoryId(2)
            .flagComment(true)
            .path(Arrays.asList("case", "complexcase"))
            .hearingRelevant(false)
            .listOfValues(new ArrayList<>())
            .listOfValuesLength(0)
            .parent(true)
            .flagComment(false)
            .build();

        List<FlagDetail> flagDetails = new ArrayList<>();
        flagDetails.add(flagDetail1);
        flagDetails.add(flagDetail2);
        return flagDetails;
    }
}

