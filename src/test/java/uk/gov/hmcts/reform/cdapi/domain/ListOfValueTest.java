package uk.gov.hmcts.reform.cdapi.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListOfValueTest {

    @Test
    void testListOfValue() {
        var list = new ListOfValue();
        list.setId("1");
        list.setKey("EN");
        list.setValue("ENGLISH");

        assertEquals("1", list.getId());
        assertEquals("EN", list.getKey());
        assertEquals("ENGLISH", list.getValue());

    }
}
