package uk.gov.hmcts.reform.cdapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LaunchDarklyConfiguration implements WebMvcConfigurer {

    @Autowired
    private FeatureConditionEvaluation featureConditionEvaluation;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(featureConditionEvaluation)
            .addPathPatterns("/refdata/commondata/caseflags/**")
            .addPathPatterns("/refdata/commondata/lov/**");
    }
}
