package uk.gov.hmcts.reform.cdapi.util;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class CommonDataApiPostgresqlContainer extends PostgreSQLContainer<CommonDataApiPostgresqlContainer> {
    private static final String IMAGE_VERSION = "hmctspublic.azurecr.io/imported/postgres:11.1";

    private CommonDataApiPostgresqlContainer() {
        super(IMAGE_VERSION);
    }

    @Container
    private static final CommonDataApiPostgresqlContainer container = new CommonDataApiPostgresqlContainer();

}
