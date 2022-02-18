package uk.gov.hmcts.reform.cdapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;
import java.util.Optional;


@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket api() {

        return new Docket(DocumentationType.SWAGGER_2)
            .useDefaultResponseMessages(false)
            .genericModelSubstitutes(Optional.class)

            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
            .paths(PathSelectors.any())
            .paths(PathSelectors.ant("/refdata/commondata/caseflags").negate())
            .paths(PathSelectors.ant("/refdata/commondata/lov/categories").negate())
            .build()
            .securitySchemes(apiKeyList());
    }

    private  List<SecurityScheme> apiKeyList() {
        return
            List.of(
                new ApiKey("Authorization", "Authorization","header"),
                new ApiKey("ServiceAuthorization", "ServiceAuthorization", "header")

            );
    }

}
