package uk.gov.hmcts.reform.cdapi.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FlagTypeTest {

    @Test
    void testFlagType() {
        assertEquals("PARTY", FlagType.PARTY.name());
        assertEquals("CASE", FlagType.CASE.name());
    }
}
