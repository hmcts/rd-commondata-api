package uk.gov.hmcts.reform.cdapi.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.cdapi.domain.ListOfValue;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class ListOfValuesRepositoryTest {

    ListOfValuesRepository listOfValuesRepository = spy(ListOfValuesRepository.class);

    @Test
    void testFindListOfValues() {
        doReturn(new ArrayList<>()).when(listOfValuesRepository).findAll();
        assertNotNull(listOfValuesRepository.findAll());
    }

    @Test
    void testFindListOfValuesWithEmpty() {
        assertEquals(0,listOfValuesRepository.findAll().size());
    }

    @Test
    void testFindListOfValueRepository() {
        doReturn(Optional.of(new ListOfValue())).when(listOfValuesRepository).findById(any());
        assertNotNull(listOfValuesRepository.findById(any()));
    }
}
