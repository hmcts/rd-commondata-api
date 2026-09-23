package uk.gov.hmcts.reform.cdapi.controller;

import io.restassured.specification.RequestSpecification;
import net.serenitybdd.rest.SerenityRest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import uk.gov.hmcts.reform.cdapi.CdAuthorizationEnabledIntegrationTest;

public abstract class BaseAuthorisedTestIntegration extends CdAuthorizationEnabledIntegrationTest {

    protected static final String BASEURL = "http://localhost";
    protected static final String CASEFLAGS_URL = "/caseflags";
    protected static final String SERVICE_ID_PARAM = "service-id";
    protected static final String SERVICE_ID_PARAM_VALUE = "123456789";

    @LocalServerPort
    private int serverPort;

    protected RequestSpecification getRequestSpecification(HttpHeaders httpHeaders) {
        return SerenityRest.given()
                .baseUri(BASEURL)
                .port(serverPort)
                .headers(httpHeaders);
    }
}
