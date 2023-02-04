package uk.gov.hmcts.reform.cdapi.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.rest.SerenityRest;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.cdapi.controllers.response.Categories;
import uk.gov.hmcts.reform.cdapi.util.ErrorInvalidRequestResponse;
import uk.gov.hmcts.reform.cdapi.exception.ErrorResponse;
import uk.gov.hmcts.reform.cdapi.idam.IdamOpenIdClient;

import java.io.IOException;
import java.util.Arrays;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class CommonDataApiClient {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String SERVICE_HEADER = "ServiceAuthorization";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BASE_URL_CASE_FLAGS = "/refdata/commondata";

    private final String commonDataApiUrl;
    private final String s2sToken;
    private IdamOpenIdClient idamOpenIdClient;


    public CommonDataApiClient(
        String commonDataApiUrl,
        String s2sToken,
        IdamOpenIdClient idamOpenIdClient) {
        this.commonDataApiUrl = commonDataApiUrl;
        this.idamOpenIdClient = idamOpenIdClient;
        this.s2sToken = s2sToken;
    }


    public Object retrieveResponseForGivenRequest(HttpStatus expectedStatus,
                                                  String param,
                                                  Class<?> clazz,
                                                  String path) {
        String queryParam = "";
        if (!isEmpty(param)) {
            queryParam = param;
        }
        Response response = getMultipleAuthHeaders()
            .get(BASE_URL_CASE_FLAGS + path + queryParam)
            .andReturn();
        response.then()
            .assertThat()
            .statusCode(expectedStatus.value());
        if (expectedStatus.is2xxSuccessful()) {
            return response
                .getBody()
                .as(clazz);
        } else {
            return response.getBody().as(ErrorResponse.class);
        }
    }

    public Object retrieveCaseFlagsByServiceId(HttpStatus expectedStatus, String param) {
        log.info(" In retrieveCaseFlagsByServiceId");
        Response response = getMultipleAuthHeaders()
            .get(BASE_URL_CASE_FLAGS + "/caseflags/" + param)
            .andReturn();
        response.then()
            .assertThat()
            .statusCode(expectedStatus.value());
        if (expectedStatus.is2xxSuccessful()) {
            return response.getBody();
        } else {
            return response.getBody().as(ErrorResponse.class);
        }
    }

    public Object retrieveBadRequestByEmptyCategoryId(HttpStatus expectedStatus, String param) {
        Response response = getMultipleAuthHeaders()
            .get(BASE_URL_CASE_FLAGS + "/lov/categories/" + param)
            .andReturn();

        response.then()
            .assertThat()
            .statusCode(expectedStatus.value());
        if (expectedStatus.is2xxSuccessful()) {
            return Arrays.asList(response.getBody().as(Categories[].class));
        } else {
            return response.getBody().as(ErrorInvalidRequestResponse.class);
        }
    }

    public Object retrieveListOfValuesByCategoryId(HttpStatus expectedStatus, String param) {
        Response response = getMultipleAuthHeaders()
            .get(BASE_URL_CASE_FLAGS + "/lov/categories/XXXX?service-id=" + param)
            .andReturn();

        response.then()
            .assertThat()
            .statusCode(expectedStatus.value());
        if (expectedStatus.is2xxSuccessful()) {
            return Arrays.asList(response.getBody().as(Categories[].class));
        } else {
            return response.getBody().as(ErrorResponse.class);
        }
    }

    public Response retrieveCategoriesByCategoryIdSuccess(String path, String param) {
        Response response = getMultipleAuthHeaders()
            .get(BASE_URL_CASE_FLAGS + path + param)
            .andReturn();
        return response;

    }


    public Response retrieveResponseForGivenRequest_NoBearerToken(String param, String path) {
        Response response = withUnauthenticatedRequest_NoBearerToken()
            .get(BASE_URL_CASE_FLAGS + path + param)
            .andReturn();

        return response;
    }

    public Response retrieveResponseForGivenRequest_NoS2SToken(String param, String path) {
        Response response = withUnauthenticatedRequest_NoS2SToken()
            .get(BASE_URL_CASE_FLAGS + path + param)
            .andReturn();

        return response;
    }


    public String getWelcomePage() {
        return withUnauthenticatedRequest()
            .get("/")
            .then()
            .statusCode(OK.value())
            .and()
            .extract()
            .response()
            .body()
            .asString();
    }

    public RequestSpecification withUnauthenticatedRequest() {
        return SerenityRest.given()
            .relaxedHTTPSValidation()
            .baseUri(commonDataApiUrl)
            .header("Content-Type", APPLICATION_JSON_VALUE)
            .header("Accepts", APPLICATION_JSON_VALUE);
    }

    public RequestSpecification withUnauthenticatedRequest_NoBearerToken() {
        return SerenityRest.given()
            .relaxedHTTPSValidation()
            .baseUri(commonDataApiUrl)
            .header("Content-Type", APPLICATION_JSON_VALUE)
            .header("Accepts", APPLICATION_JSON_VALUE)
            .header(SERVICE_HEADER, "Bearer " + s2sToken);
    }

    private RequestSpecification withUnauthenticatedRequest_NoS2SToken() {
        return SerenityRest.with()
            .relaxedHTTPSValidation()
            .baseUri(commonDataApiUrl)
            .header("Content-Type", APPLICATION_JSON_VALUE)
            .header("Accepts", APPLICATION_JSON_VALUE)
            .header(AUTHORIZATION_HEADER, "Bearer " + idamOpenIdClient.getcwdAdminOpenIdToken("crd-admin"));
    }

    public RequestSpecification getMultipleAuthHeaders() {
        return SerenityRest.with()
            .relaxedHTTPSValidation()
            .baseUri(commonDataApiUrl)
            .header("Content-Type", APPLICATION_JSON_VALUE)
            .header("Accepts", APPLICATION_JSON_VALUE)
            .header(SERVICE_HEADER, "Bearer " + s2sToken)
            .header(AUTHORIZATION_HEADER, "Bearer " + idamOpenIdClient.getcwdAdminOpenIdToken("crd-admin"));
    }

    @SuppressWarnings("unused")
    private JsonNode parseJson(String jsonString) throws IOException {
        return mapper.readTree(jsonString);
    }

}
