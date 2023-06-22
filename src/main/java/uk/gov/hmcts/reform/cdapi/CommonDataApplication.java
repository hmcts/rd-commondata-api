package uk.gov.hmcts.reform.cdapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import uk.gov.hmcts.reform.idam.client.IdamApi;


@EnableJpaAuditing
@EnableJpaRepositories
@SpringBootApplication
@EnableCaching
@ImportAutoConfiguration({FeignAutoConfiguration.class})
@ComponentScan({"uk.gov.hmcts.reform.cdapi"})
@EnableFeignClients(basePackages = {
    "uk.gov.hmcts.reform.cdapi" },
    basePackageClasses = { IdamApi.class }
)
@SuppressWarnings("HideUtilityClassConstructor") // Spring needs a constructor, its not a utility class
public class CommonDataApplication {

    public static void main(final String[] args) {
        SpringApplication.run(CommonDataApplication.class, args);
    }
}
