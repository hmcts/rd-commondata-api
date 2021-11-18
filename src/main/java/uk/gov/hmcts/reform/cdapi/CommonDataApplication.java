package uk.gov.hmcts.reform.cdapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SuppressWarnings("HideUtilityClassConstructor") // Spring needs a constructor, its not a utility class
public class CommonDataApplication {

    public static void main(final String[] args) {
        SpringApplication.run(CommonDataApplication.class, args);
    }
}
