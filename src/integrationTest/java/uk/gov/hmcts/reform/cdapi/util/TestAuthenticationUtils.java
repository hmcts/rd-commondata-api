package uk.gov.hmcts.reform.cdapi.util;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.ACCESS_TOKEN;
import static uk.gov.hmcts.reform.cdapi.config.FeatureConditionEvaluation.SERVICE_AUTHORIZATION;
import static uk.gov.hmcts.reform.cdapi.util.CommonDataApiClient.generateDummyS2SToken;

@UtilityClass
@SuppressWarnings({"HideUtilityClassConstructor"})
public class TestAuthenticationUtils {

    public static final String S2S_XUI = "xui_webapp";
    public static final String TOKEN_NAME = "tokenName";
    public static final long AUTH_TOKEN_TTL = 14400000L;
    private static final RSAKey TEST_RSA_JWK;
    private static final String CRD_CLAIM = "CRD_Claim";

    static {
        try {
            TEST_RSA_JWK = KeyGenUtil.getRsaJwk();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static HttpHeaders getHttpHeaders(String serviceName) throws JOSEException {
        HttpHeaders headers = new HttpHeaders();
        var userAuthToken = generateAuthToken();
        headers.setBearerAuth(userAuthToken);
        headers.add(SERVICE_AUTHORIZATION, "Bearer " + generateDummyS2SToken(serviceName));
        headers.add("X-Correlation-Id", "38a90097-434e-47ee-8ea1-9ea2a267f51d");
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public static HttpHeaders getJwtHeaders(String issuer, boolean isExpired) throws Exception {

        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(generateAuthToken(issuer, isExpired));

        headers.add(SERVICE_AUTHORIZATION, "Bearer " + generateDummyS2SToken(S2S_XUI));

        headers.add("X-Correlation-Id", "38a90097-434e-47ee-8ea1-9ea2a267f51d");

        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }

    private static String generateAuthToken() throws JOSEException {
        JWTClaimsSet.Builder builder =
                getJwtClaimsBuilder(new Date(), new Date(System.currentTimeMillis() + AUTH_TOKEN_TTL));

        JWSHeader jwsHeader =
                new JWSHeader.Builder(JWSAlgorithm.RS256)
                        .keyID(TEST_RSA_JWK.getKeyID())
                        .build();

        SignedJWT signedJwt = new SignedJWT(jwsHeader, builder.build());

        signedJwt.sign(new RSASSASigner(TEST_RSA_JWK));

        return signedJwt.serialize();
    }

    public static String generateAuthToken(String issuer, boolean isExpired) throws Exception {

        Instant now = Instant.now();

        Instant issuedAt = isExpired
                ? now.minus(2, ChronoUnit.HOURS)
                : now.minusSeconds(60);

        Instant expiresAt = isExpired
                ? now.minus(1, ChronoUnit.HOURS)
                : now.plusSeconds(3600);

        JWTClaimsSet.Builder claimsBuilder =
                getJwtClaimsBuilder(Date.from(issuedAt), Date.from(expiresAt));

        if (issuer != null) {
            claimsBuilder.issuer(issuer);
        }

        JWSHeader header =
                new JWSHeader.Builder(JWSAlgorithm.RS256)
                        .keyID(TEST_RSA_JWK.getKeyID())
                        .build();

        SignedJWT signedJwt = new SignedJWT(header, claimsBuilder.build());

        signedJwt.sign(new RSASSASigner(TEST_RSA_JWK));

        return signedJwt.serialize();
    }

    private static JWTClaimsSet.Builder getJwtClaimsBuilder(Date issuedAt,
                                                            Date expiresAt) {
        return new JWTClaimsSet.Builder()
                .subject(CRD_CLAIM)
                .issueTime(issuedAt)
                .claim(TOKEN_NAME, ACCESS_TOKEN)
                .expirationTime(expiresAt);
    }
}
