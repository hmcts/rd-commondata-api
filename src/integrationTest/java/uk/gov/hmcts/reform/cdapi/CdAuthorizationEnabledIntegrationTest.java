package uk.gov.hmcts.reform.cdapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.cdapi.service.impl.FeatureToggleServiceImpl;
import uk.gov.hmcts.reform.cdapi.util.CommonDataApiClient;
import uk.gov.hmcts.reform.cdapi.util.KeyGenUtil;
import uk.gov.hmcts.reform.cdapi.util.WireMockExtension;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Configuration
@TestPropertySource(properties = {"S2S_URL=http://127.0.0.1:8990", "IDAM_URL:http://127.0.0.1:5000"})
@DirtiesContext
public abstract class CdAuthorizationEnabledIntegrationTest extends SpringBootIntegrationTest {

    @RegisterExtension
    public static WireMockExtension s2sService = new WireMockExtension(8990);

    @RegisterExtension
    public static WireMockExtension idamService = new WireMockExtension(5000);

    @RegisterExtension
    public static WireMockExtension mockHttpServerForOidc = new WireMockExtension(7000);


    protected CommonDataApiClient commonDataApiClient;

    @MockBean
    protected FeatureToggleServiceImpl featureToggleService;

    @Value("${oidc.issuer}")
    private String issuer;

    @Value("${oidc.expiration}")
    private long expiration;

    @BeforeEach
    public void setUpClient() {
        when(featureToggleService.isFlagEnabled(anyString(), anyString())).thenReturn(true);
        commonDataApiClient = new CommonDataApiClient(port, issuer, expiration);
    }

    @BeforeEach
    public void setUpIdamStubs() throws Exception {

        s2sService.stubFor(get(urlEqualTo("/details"))
                               .willReturn(aResponse()
                                               .withStatus(200)
                                               .withHeader("Content-Type", "application/json")
                                               .withBody("rd_commondata_api")));

        idamService.stubFor(get(urlPathMatching("/o/userinfo"))
                                .willReturn(aResponse()
                                                .withStatus(200)
                                                .withHeader("Content-Type", "application/json")
                                                .withBody("{"
                                                              + "  \"id\": \"%s\","
                                                              + "  \"uid\": \"%s\","
                                                              + "  \"forename\": \"Super\","
                                                              + "  \"surname\": \"User\","
                                                              + "  \"email\": \"super.user@hmcts.net\","
                                                              + "  \"accountStatus\": \"active\","
                                                              + "  \"roles\": ["
                                                              + "  \"%s\""
                                                              + "  ]"
                                                              + "}")
                                                .withTransformers("external_user-token-response")));

        mockHttpServerForOidc.stubFor(get(urlPathMatching("/jwks"))
                                          .willReturn(aResponse()
                                                          .withStatus(200)
                                                          .withHeader("Content-Type", "application/json")
                                                          .withBody(getDynamicJwksResponse())));
    }

    public static String getDynamicJwksResponse() throws JOSEException, JsonProcessingException {
        RSAKey rsaKey = KeyGenUtil.getRsaJwk();
        Map<String, List<JSONObject>> body = new LinkedHashMap<>();
        List<JSONObject> keyList = new ArrayList<>();
        keyList.add(rsaKey.toJSONObject());
        body.put("keys", keyList);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(body);
    }


}

