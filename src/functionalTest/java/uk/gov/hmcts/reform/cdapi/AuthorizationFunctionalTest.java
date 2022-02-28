package uk.gov.hmcts.reform.cdapi;

import io.restassured.parsing.Parser;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.rest.SerenityRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.cdapi.client.CommonDataApiClient;
import uk.gov.hmcts.reform.cdapi.client.S2sClient;
import uk.gov.hmcts.reform.cdapi.config.Oauth2;
import uk.gov.hmcts.reform.cdapi.config.TestConfigProperties;
import uk.gov.hmcts.reform.cdapi.exception.ErrorResponse;
import uk.gov.hmcts.reform.cdapi.idam.IdamOpenIdClient;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = {TestConfigProperties.class, Oauth2.class})
@ComponentScan("uk.gov.hmcts.reform.cdapi")
@TestPropertySource("classpath:application-functional.yaml")
@Slf4j
public abstract class AuthorizationFunctionalTest {

    protected CommonDataApiClient commonDataApiClient;

    protected static IdamOpenIdClient idamOpenIdClient;

    public static final String EMAIL_TEMPLATE = "freg-test-user-%s@cdfunctestuser.com";

    @Autowired
    protected TestConfigProperties testConfigProperties;

    protected static String s2sToken;

    public static List<String> emailsTobeDeleted = new ArrayList<>();

    public static final String EMAIL = "EMAIL";

    public static final String CREDS = "CREDS";

    @PostConstruct
    public void setup() {

        SerenityRest.useRelaxedHTTPSValidation();
        SerenityRest.setDefaultParser(Parser.JSON);

        log.info("Configured S2S secret: " + testConfigProperties.getS2sSecret().substring(0, 2) + "************"
                     + testConfigProperties.getS2sSecret().substring(14));
        log.info("Configured S2S microservice: " + testConfigProperties.getS2sName());
        log.info("Configured S2S URL: " + testConfigProperties.getS2sUrl());

        if (null == s2sToken) {
            s2sToken = new S2sClient(
                testConfigProperties.getS2sUrl(),
                testConfigProperties.getS2sName(),
                testConfigProperties.getS2sSecret()
            )
                .signIntoS2S();
        }
        if (null == idamOpenIdClient) {
            idamOpenIdClient = new IdamOpenIdClient(testConfigProperties);
        }

        commonDataApiClient = new CommonDataApiClient(
            testConfigProperties.getCommonDataApiUrl(),
            s2sToken,
            idamOpenIdClient
        );

    }

    public static String generateRandomEmail() {
        String generatedEmail = String.format(EMAIL_TEMPLATE, randomAlphanumeric(10));
        emailsTobeDeleted.add(generatedEmail);
        return generatedEmail;
    }

    public static void destroy() {
        emailsTobeDeleted.forEach(email -> idamOpenIdClient.deleteSidamUser(email));
    }

    public void validateErrorResponse(ErrorResponse errorResponse, String expectedErrorMessage,
                                      String expectedErrorDescription) {
        assertEquals(expectedErrorDescription, errorResponse.getErrorDescription());
        assertEquals(expectedErrorMessage, errorResponse.getErrorMessage());
    }

}
