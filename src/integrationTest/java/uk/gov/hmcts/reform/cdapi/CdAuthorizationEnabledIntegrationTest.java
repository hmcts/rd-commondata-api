package uk.gov.hmcts.reform.cdapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.hmcts.reform.cdapi.service.impl.FeatureToggleServiceImpl;
import uk.gov.hmcts.reform.cdapi.util.CommonDataApiClient;
import uk.gov.hmcts.reform.cdapi.util.KeyGenUtil;
import uk.gov.hmcts.reform.cdapi.util.WireMockExtension;
import uk.gov.hmcts.reform.cdapi.util.WireMockUtil;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
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
    public static final WireMockExtension s2sService = new WireMockExtension(8990);

    @RegisterExtension
    public static final WireMockExtension idamService = new WireMockExtension(5000);

    @RegisterExtension
    public static final WireMockExtension mockHttpServerForOidc = new WireMockExtension(7000);


    protected CommonDataApiClient commonDataApiClient;

    @MockitoBean
    protected FeatureToggleServiceImpl featureToggleService;

    @Value("${oidc.issuer}")
    private String issuer;

    @Value("${oidc.expiration}")
    private long expiration;

    @Value("${idam.s2s-auth.microservice}")
    static String authorisedService;

    @MockitoBean
    protected JwtDecoder jwtDecoder;

    @BeforeEach
    public void setUpClient() {
        when(featureToggleService.isFlagEnabled(anyString(), anyString())).thenReturn(true);
        commonDataApiClient = new CommonDataApiClient(port, issuer, expiration);
        when(jwtDecoder.decode(anyString())).thenReturn(getJwt());
    }

    @BeforeEach
    public void setUpIdamStubs() throws Exception {

        s2sService.stubFor(get(urlEqualTo("/details"))
                               .willReturn(aResponse()
                                               .withStatus(200)
                                               .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                               .withBody("rd_commondata_api")));


        UserInfo userDetails = UserInfo.builder()
            .uid("%s")
            .givenName("Super")
            .familyName("User")
            .roles(List.of("%s"))
            .build();


        idamService.stubFor(get(urlPathMatching("/o/userinfo"))
                                .willReturn(aResponse()
                                                .withStatus(200)
                                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                                .withBody(WireMockUtil.getObjectMapper()
                                                              .writeValueAsString(userDetails))
                                                .withTransformers("external_user-token-response")));

        mockHttpServerForOidc.stubFor(get(urlPathMatching("/jwks"))
                                          .willReturn(aResponse()
                                                          .withStatus(200)
                                                          .withHeader(HttpHeaders.CONTENT_TYPE,
                                                                      MediaType.APPLICATION_JSON_VALUE)
                                                          .withBody(getDynamicJwksResponse())));
    }

    public static String generateDummyS2SToken(String serviceName) {
        return Jwts.builder()
            .setSubject(serviceName)
            .setIssuedAt(new Date())
            .signWith(SignatureAlgorithm.HS256, TextCodec.BASE64.encode("AA"))
            .compact();
    }

    public static synchronized Jwt getJwt() {
        var s2SToken = generateDummyS2SToken(authorisedService);
        return Jwt.withTokenValue(s2SToken)
            .claim("exp", Instant.ofEpochSecond(1585763216))
            .claim("iat", Instant.ofEpochSecond(1585734416))
            .claim("token_type", "Bearer")
            .claim("tokenName", "access_token")
            .claim("expires_in", 28800)
            .header("kid", "b/O6OvVv1+y+WgrH5Ui9WTioLt0=")
            .header("typ", "RS256")
            .header("alg", "RS256")
            .build();
    }

    public static String getDynamicJwksResponse() throws JOSEException, JsonProcessingException {
        RSAKey rsaKey = KeyGenUtil.getRsaJwk();
        Map<String, List<Map<String, Object>>> body = new LinkedHashMap<>();
        List<Map<String, Object>> keyList = new ArrayList<>();
        keyList.add(rsaKey.toJSONObject());
        body.put("keys", keyList);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(body);
    }


}

