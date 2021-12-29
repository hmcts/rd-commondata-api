package uk.gov.hmcts.reform.cdapi;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@WithTags({@WithTag("testType:Smoke")})
@Slf4j
class SmokeTest {

    // use this when testing locally - replace the below content with this line
    private final String targetInstance =
        StringUtils.defaultIfBlank(
            System.getenv("TEST_URL"),
            "http://localhost:4550"
        );

    @Test
    void test_should_prove_app_is_running_and_healthy() {

        SerenityRest.useRelaxedHTTPSValidation();

        Response response = SerenityRest
            .given().log().all()
            .baseUri(targetInstance)
            .relaxedHTTPSValidation()
            .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .get("/")
            .andReturn();
        log.info("Response::" + response);
        if (null != response && response.statusCode() == 200) {
            log.info("Response::" + response.body().asString());
            assertThat(response.body().asString())
                .contains("Welcome to RD Common Data API");

        } else {

            Assertions.fail();
        }
    }
}
