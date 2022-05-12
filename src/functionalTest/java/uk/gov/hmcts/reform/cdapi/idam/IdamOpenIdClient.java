package uk.gov.hmcts.reform.cdapi.idam;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.mifmif.common.regex.Generex;
import io.restassured.RestAssured;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.reform.lib.idam.IdamOpenId;
import uk.gov.hmcts.reform.cdapi.config.TestConfigProperties;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;


@Slf4j
public class IdamOpenIdClient extends IdamOpenId {

    private TestConfigProperties testConfig;

    private Gson gson = new Gson();

    private static String openIdTokenCommonDataAdmin;

    private static String sidamPassword;

    public IdamOpenIdClient(TestConfigProperties testConfig) {
        super(testConfig);
        this.testConfig = testConfig;
    }

    public void deleteSidamUser(String email) {
        try {
            log.info("delete sidam user with email");
            RestAssured
                .given()
                .relaxedHTTPSValidation()
                .baseUri(testConfig.getIdamApiUrl())
                .header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
                .delete("/testing-support/accounts/" + email);
        } catch (Exception ex) {
            log.error("unable to delete sidam user with email");
        }
    }

    @AllArgsConstructor
    class User {
        private String email;
        private String forename;
        private String id;
        private String surname;
        private String password;
        private List<Role> roles;
        private Group group;
    }

    @AllArgsConstructor
    class Role {
        private String code;
    }

    @AllArgsConstructor
    class Group {
        private String code;
    }

    @Getter
    @AllArgsConstructor
    class AuthorizationResponse {
        private String code;
    }

    @Getter
    @AllArgsConstructor
    class BearerTokenResponse {
        @SerializedName("access_token")
        private String accessToken;
    }

    public static String generateSidamPassword() {
        if (isBlank(sidamPassword)) {
            sidamPassword = new Generex("([A-Z])([a-z]{4})([0-9]{4})").random();
        }
        return sidamPassword;
    }

}
