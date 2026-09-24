package uk.gov.hmcts.reform.cdapi;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.cdapi.util.TestApplicationServer;
import uk.gov.hmcts.reform.cdapi.wiremock.WireMockContextInitializer;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CommonDataApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@ContextConfiguration(initializers = WireMockContextInitializer.class)
public abstract class SpringBootIntegrationTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestApplicationServer testApplicationServer;

    public static ObjectMapper getObjectMapper() {

        return new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

}
