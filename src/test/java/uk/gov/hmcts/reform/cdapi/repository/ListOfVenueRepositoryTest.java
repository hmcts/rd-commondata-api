package uk.gov.hmcts.reform.cdapi.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class ListOfVenueRepositoryTest {

    ListOfVenueRepository listOfVenueRepository = spy(ListOfVenueRepository.class);

    @Test
    void testFindListOfValues() {
        doReturn(new ArrayList<>()).when(listOfVenueRepository).findAll();
        assertNotNull(listOfVenueRepository.findAll());
    }
}
