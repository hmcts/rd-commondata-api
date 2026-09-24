package uk.gov.hmcts.reform.cdapi;

import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.hmcts.reform.cdapi.service.impl.FeatureToggleServiceImpl;
import uk.gov.hmcts.reform.cdapi.util.CommonDataApiClient;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Configuration
@TestPropertySource(properties = {"S2S_URL=http://127.0.0.1:8990", "IDAM_URL:http://127.0.0.1:5000"})
@DirtiesContext
@ExtendWith({SerenityJUnit5Extension.class})
@WithTags({@WithTag("testType:Integration")})
public abstract class CdAuthorizationEnabledIntegrationTest extends SpringBootIntegrationTest {

    protected CommonDataApiClient commonDataApiClient;

    @MockitoBean
    protected FeatureToggleServiceImpl featureToggleService;

    @Value("${oidc.issuer}")
    private String issuer;

    @Value("${oidc.expiration}")
    private long expiration;

    @Value("${idam.s2s-auth.microservice}")
    static String authorisedService;

    @BeforeEach
    public void setUpClient() {
        when(featureToggleService.isFlagEnabled(anyString(), anyString())).thenReturn(true);
        commonDataApiClient = new CommonDataApiClient(port, issuer, expiration);
    }
}

