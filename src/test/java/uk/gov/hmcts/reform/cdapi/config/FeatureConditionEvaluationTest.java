package uk.gov.hmcts.reform.cdapi.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import uk.gov.hmcts.reform.cdapi.controllers.RootController;
import uk.gov.hmcts.reform.cdapi.exception.ForbiddenException;
import uk.gov.hmcts.reform.cdapi.service.impl.FeatureToggleServiceImpl;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.cdapi.config.FeatureConditionEvaluation.BEARER;
import static uk.gov.hmcts.reform.cdapi.config.FeatureConditionEvaluation.SERVICE_AUTHORIZATION;

@ExtendWith(MockitoExtension.class)
class FeatureConditionEvaluationTest {

    FeatureToggleServiceImpl featureToggleService = mock(FeatureToggleServiceImpl.class);
    @Spy
    FeatureConditionEvaluation featureConditionEvaluation = new FeatureConditionEvaluation(featureToggleService);
    HttpServletRequest httpRequest = mock(HttpServletRequest.class);
    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
    HandlerMethod handlerMethod = mock(HandlerMethod.class);
    Method method = mock(Method.class);

    @BeforeEach
    public void before() {
        when(method.getName()).thenReturn("test");
        doReturn(RootController.class).when(method).getDeclaringClass();
        when(handlerMethod.getMethod()).thenReturn(method);
    }

    @Test
    void testPreHandleValidFlag() throws Exception {
        Map<String, String> launchDarklyMap = new HashMap<>();
        launchDarklyMap.put("RootController.test", "test-flag");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpRequest));
        when(featureToggleService.getLaunchDarklyMap()).thenReturn(launchDarklyMap);
        String token = generateDummyS2SToken("rd_commondata_api");
        when(httpRequest.getHeader(SERVICE_AUTHORIZATION)).thenReturn(BEARER + token);
        when(featureToggleService.isFlagEnabled(anyString(), anyString())).thenReturn(true);
        assertTrue(featureConditionEvaluation.preHandle(httpRequest, httpServletResponse, handlerMethod));
        verify(featureConditionEvaluation, times(1))
            .preHandle(httpRequest, httpServletResponse, handlerMethod);
    }

    @Test
    void testPreHandleInvalidFlag() throws Exception {
        Map<String, String> launchDarklyMap = new HashMap<>();
        launchDarklyMap.put("RootController.test", "test-flag");
        when(featureToggleService.getLaunchDarklyMap()).thenReturn(launchDarklyMap);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpRequest));
        String token = generateDummyS2SToken("rd_commondata_api");
        when(httpRequest.getHeader(SERVICE_AUTHORIZATION)).thenReturn(BEARER + token);
        when(featureToggleService.isFlagEnabled(anyString(), anyString())).thenReturn(false);
        assertThrows(ForbiddenException.class, () -> featureConditionEvaluation
            .preHandle(httpRequest, httpServletResponse, handlerMethod));

        verify(featureConditionEvaluation, times(1))
            .preHandle(httpRequest, httpServletResponse, handlerMethod);
    }

    @Test
    void testPreHandleInvalidServletRequestAttributes() throws Exception {
        Map<String, String> launchDarklyMap = new HashMap<>();
        launchDarklyMap.put("RootController.test", "test-flag");
        when(featureToggleService.getLaunchDarklyMap()).thenReturn(launchDarklyMap);
        assertThrows(ForbiddenException.class, () -> featureConditionEvaluation.preHandle(
            httpRequest,
            httpServletResponse,
            handlerMethod
        ));
        verify(featureConditionEvaluation, times(1))
            .preHandle(httpRequest, httpServletResponse, handlerMethod);
    }

    @Test
    void testPreHandleNoFlag() throws Exception {
        assertTrue(featureConditionEvaluation.preHandle(httpRequest, httpServletResponse, handlerMethod));
        verify(featureConditionEvaluation, times(1))
            .preHandle(httpRequest, httpServletResponse, handlerMethod);
    }

    @Test
    void testPreHandleNonConfiguredValues() throws Exception {
        Map<String, String> launchDarklyMap = new HashMap<>();
        launchDarklyMap.put("DummyController.test", "test-flag");
        when(featureToggleService.getLaunchDarklyMap()).thenReturn(launchDarklyMap);
        assertTrue(featureConditionEvaluation.preHandle(httpRequest, httpServletResponse, handlerMethod));
        verify(featureConditionEvaluation, times(1))
            .preHandle(httpRequest, httpServletResponse, handlerMethod);
    }

    public static String generateDummyS2SToken(String serviceName) {
        SecretKey signKey = Keys.hmacShaKeyFor("00112233445566778899aabbccddeeff".getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
            .subject(serviceName)
            .issuedAt(new Date())
            .signWith(signKey, Jwts.SIG.HS256)
            .compact();
    }
}
