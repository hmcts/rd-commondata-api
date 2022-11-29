package uk.gov.hmcts.reform.cdapi.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.cdapi.CdAuthorizationEnabledIntegrationTest;
import uk.gov.hmcts.reform.cdapi.CommonDataApplication;
import uk.gov.hmcts.reform.cdapi.SpringBootIntegrationTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})

public class CaseFlagRepositoryITTest {

    @Autowired
    CaseFlagRepository caseFlagRepository;

    @Test
    void testFindAll() {
        //doReturn(new ArrayList<>()).when(caseFlagRepository).findAll(anyString());
        assertNotNull(caseFlagRepository);
    }
}
