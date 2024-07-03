package uk.gov.hmcts.reform.cdapi.util;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class CommonDataApiPostgresqlContainer extends PostgreSQLContainer<CommonDataApiPostgresqlContainer> {
    private static final DockerImageName dockerImageName = DockerImageName
        .parse("sdshmctspublic.azurecr.io/imported/postgres:11.1")
        .asCompatibleSubstituteFor("postgres");

    private CommonDataApiPostgresqlContainer() {
        super(dockerImageName);
    }

    @Container
    private static final CommonDataApiPostgresqlContainer container = new CommonDataApiPostgresqlContainer();

}
