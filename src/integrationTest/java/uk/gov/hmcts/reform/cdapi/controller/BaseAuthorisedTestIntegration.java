package uk.gov.hmcts.reform.cdapi.controller;

import io.restassured.specification.RequestSpecification;
import net.serenitybdd.rest.SerenityRest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import uk.gov.hmcts.reform.cdapi.CdAuthorizationEnabledIntegrationTest;

public abstract class BaseAuthorisedTestIntegration extends CdAuthorizationEnabledIntegrationTest {

    protected static final String BASEURL = "http://localhost";

    @LocalServerPort
    private int serverPort;

    protected RequestSpecification getRequestSpecification(HttpHeaders httpHeaders) {
        return SerenityRest.given()
                .baseUri(BASEURL)
                .port(serverPort)
                .headers(httpHeaders);
    }
}
