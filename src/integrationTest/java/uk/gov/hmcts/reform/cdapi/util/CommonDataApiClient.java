package uk.gov.hmcts.reform.cdapi.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.cdapi.exception.ErrorResponse;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static uk.gov.hmcts.reform.cdapi.util.JwtTokenUtil.generateToken;

@Slf4j
@PropertySource(value = "/integrationTest/resources/application-test.yml")
public class CommonDataApiClient {

    private static final String APP_BASE_PATH = "/refdata/commondata/caseflags";

    private static String JWT_TOKEN = null;
    private final Integer commonDataApiPort;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    private String baseUrl;

    private String issuer;
    private long expiration;

    @Value("${s2s-authorised.services}")
    private String serviceName;

    public CommonDataApiClient(int port, String issuer, Long tokenExpirationInterval) {
        this.commonDataApiPort = port;
        this.baseUrl = "http://localhost:" + commonDataApiPort + APP_BASE_PATH;
        this.issuer = issuer;
        this.expiration = tokenExpirationInterval;
    }

    public Object retrieveCaseFlagsByServiceId(String queryParam,
                                               Class<?> clazz,
                                               String path) throws JsonProcessingException {
        ResponseEntity<Object> responseEntity = getRequest(APP_BASE_PATH + path + queryParam, clazz, "");
        return mapCaseFlagsByServiceIdResponse(responseEntity, clazz);
    }

    private Object mapCaseFlagsByServiceIdResponse(ResponseEntity<Object> responseEntity,
                                                   Class<?> clazz) throws JsonProcessingException {
        HttpStatus status = responseEntity.getStatusCode();

        if (status.is2xxSuccessful()) {
            return objectMapper.convertValue(responseEntity.getBody(), clazz);
        } else {
            Map<String, Object> errorResponseMap = new HashMap<>();
            errorResponseMap.put(
                "response_body",
                objectMapper.readValue(responseEntity.getBody().toString(), ErrorResponse.class)
            );
            errorResponseMap.put("http_status", status);
            return errorResponseMap;
        }

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private ResponseEntity<Object> getRequest(String uriPath, Class clasz, Object... params) {

        ResponseEntity<Object> responseEntity;
        try {
            HttpEntity<?> request = new HttpEntity<>(getMultipleAuthHeaders());
            responseEntity = restTemplate.exchange(
                "http://localhost:" + commonDataApiPort + uriPath,
                HttpMethod.GET,
                request,
                clasz,
                params
            );
        } catch (HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getRawStatusCode()).body(ex.getResponseBodyAsString());
        }
        return responseEntity;
    }

    private HttpHeaders getMultipleAuthHeaders() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        if (StringUtils.isBlank(JWT_TOKEN)) {

            JWT_TOKEN = generateDummyS2SToken(serviceName);
        }
        headers.add("ServiceAuthorization", JWT_TOKEN);
        String bearerToken = "Bearer ".concat(getBearerToken(UUID.randomUUID().toString()));
        headers.add("Authorization", bearerToken);

        return headers;
    }

    private final String getBearerToken(String userId) {

        return generateToken(issuer, expiration, userId);

    }

    public static String generateDummyS2SToken(String serviceName) {
        return Jwts.builder().setSubject(serviceName).setIssuedAt(new Date()).signWith(
            SignatureAlgorithm.HS256,
            TextCodec.BASE64.encode("AA")
        ).compact();
    }

}
