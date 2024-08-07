package uk.gov.hmcts.reform.cdapi.config;

import com.auth0.jwt.JWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gov.hmcts.reform.cdapi.exception.ForbiddenException;
import uk.gov.hmcts.reform.cdapi.service.FeatureToggleService;

import java.util.Map;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.StringUtils.SPACE;

@Component
@AllArgsConstructor
public class FeatureConditionEvaluation implements HandlerInterceptor {

    public static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    public static final String BEARER = "Bearer ";

    public static final String FORBIDDEN_EXCEPTION_LD = "feature flag is not released";

    @Autowired
    private final FeatureToggleService featureToggleService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) throws Exception {
        Map<String, String> launchDarklyUrlMap = featureToggleService.getLaunchDarklyMap();
        if (handler instanceof HandlerMethod) {
            String restMethod = ((HandlerMethod) handler).getMethod().getName();
            String clazz = ((HandlerMethod) handler).getMethod().getDeclaringClass().getSimpleName();
            boolean flagStatus;

            String flagName = launchDarklyUrlMap.get(clazz + "." + restMethod);

            if (isNotTrue(launchDarklyUrlMap.isEmpty()) && nonNull(flagName)) {

                flagStatus = featureToggleService
                    .isFlagEnabled(getServiceName(flagName), launchDarklyUrlMap.get(clazz + "." + restMethod));

                if (!flagStatus) {
                    throw new ForbiddenException(flagName.concat(SPACE).concat(FORBIDDEN_EXCEPTION_LD));
                }
            }
        }
        return true;
    }


    public String getServiceName(String flagName) {
        String serviceName = null;
        ServletRequestAttributes servletRequestAttributes =
            ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());

        if (nonNull(servletRequestAttributes)) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            serviceName = JWT.decode(removeBearerFromToken(request.getHeader(SERVICE_AUTHORIZATION))).getSubject();
        }
        if (StringUtils.isEmpty(serviceName)) {
            throw new ForbiddenException(flagName.concat(SPACE).concat(FORBIDDEN_EXCEPTION_LD));
        }
        return serviceName;
    }

    private String removeBearerFromToken(String token) {
        if (isNotTrue(token.startsWith(BEARER))) {
            return token;
        } else {
            return token.substring(BEARER.length());
        }
    }

}
