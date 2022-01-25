package uk.gov.hmcts.reform.cdapi.repository;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CaseFlagRepositoryTest {
    CaseFlagRepository caseFlagRepository = mock(CaseFlagRepository.class);

    @Test
    void testFindAll() {
        when(caseFlagRepository.findAll(anyString())).thenReturn(new ArrayList<>());
        assertNotNull(caseFlagRepository.findAll(anyString()));
    }
}
