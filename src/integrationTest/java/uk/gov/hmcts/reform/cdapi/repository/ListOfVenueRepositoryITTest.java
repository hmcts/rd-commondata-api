package uk.gov.hmcts.reform.cdapi.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
public class ListOfVenueRepositoryITTest {


    @Autowired
    ListOfVenueRepository listOfVenueRepository;

    @Test
    void testFindListOfValues() {
        //doReturn(new ArrayList<>()).when(listOfVenueRepository).findAll();
        //assertNotNull(listOfVenueRepository.findAll());
        assertNotNull(listOfVenueRepository);
    }
}
