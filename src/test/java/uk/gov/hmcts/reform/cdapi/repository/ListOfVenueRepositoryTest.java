package uk.gov.hmcts.reform.cdapi.repository;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListOfVenueRepositoryTest {

    ListOfVenueRepository listOfVenueRepository = mock(ListOfVenueRepository.class);

    @Test
    void testFindListOfValues() {
        when(listOfVenueRepository.findAll()).thenReturn(new ArrayList<>());
        assertNotNull(listOfVenueRepository.findAll());
    }
}
