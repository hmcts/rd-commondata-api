package uk.gov.hmcts.reform.cdapi.repository;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class CaseFlagRepositoryTest {
    CaseFlagRepository caseFlagRepository = spy(CaseFlagRepository.class);

    @Test
    void testFindAll() {
        doReturn(new ArrayList<>()).when(caseFlagRepository).findAll(anyString(), anyString());
        assertNotNull(caseFlagRepository.findAll(anyString(), anyString()));
    }
}
