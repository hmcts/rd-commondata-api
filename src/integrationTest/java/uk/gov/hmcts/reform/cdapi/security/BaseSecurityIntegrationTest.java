package uk.gov.hmcts.reform.cdapi.security;

import io.restassured.specification.RequestSpecification;
import net.serenitybdd.rest.SerenityRest;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.cdapi.CdAuthorizationEnabledIntegrationTest;
import uk.gov.hmcts.reform.cdapi.util.TestApplicationServer;

import static uk.gov.hmcts.reform.cdapi.util.TestAuthenticationUtils.getJwtHeaders;

public class BaseSecurityIntegrationTest extends CdAuthorizationEnabledIntegrationTest {

    protected static final String VALID_ISSUER_1 = "http://localhost:5062/o";
    protected static final String VALID_ISSUER_2 = "https://secondary-idam.platform.hmcts.net";
    protected static final String ROGUE_ISSUER = "https://rogue-issuer.com";
    private static final String CATEGORIES_URL = "/refdata/commondata/lov/categories/";
    protected static final String QUERY_PARAM =  "panelCategory?isChildRequired=Y&serviceId=BBA3";


    @Autowired
    private TestApplicationServer testApplicationServer;

    protected RequestSpecification jwtRequest(
            String issuer,
            boolean expired)
            throws Exception {

        return SerenityRest.given().log().all()
                .baseUri(testApplicationServer.url(CATEGORIES_URL))
                .headers(getJwtHeaders(issuer, expired));
    }

    protected RequestSpecification unexpiredJwt(
            String issuer)
            throws Exception {

        return jwtRequest(issuer, false);
    }

    protected RequestSpecification expiredJwt(
            String issuer)
            throws Exception {

        return jwtRequest(issuer, true);
    }
}
