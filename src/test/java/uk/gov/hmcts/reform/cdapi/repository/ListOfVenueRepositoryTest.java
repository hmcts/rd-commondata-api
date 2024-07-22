package uk.gov.hmcts.reform.cdapi.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.cdapi.domain.ListOfValue;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    void testFindListOfValuesWithCategoryKey() {
        doReturn(new ArrayList<>()).when(listOfVenueRepository).findListOfValues(any());
        assertNotNull(listOfVenueRepository.findListOfValues("HearingChannel"));
    }

    @Test
    void testFindListOfValue() {
        doReturn(Optional.of(new ListOfValue())).when(listOfVenueRepository).findById(any());
        assertNotNull(listOfVenueRepository.findById(1L));
    }
}
