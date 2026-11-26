package uk.gov.hmcts.reform.cdapi;

import io.restassured.response.Response;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.stream.Stream;

@ExtendWith(SerenityJUnit5Extension.class)
@SpringBootTest
@WithTags({@WithTag("testType:Functional")})
@ActiveProfiles("functional")
public class FlagPathFieldsTest extends AuthorizationFunctionalTest {

    @Steps
    SuppressionSteps apiSteps;

    @Steps
    PathVerificationSteps pathSteps;

    static Stream<String> serviceIds() {

        return Stream.of("AAA3", "AAA6", "AAA7", "ABA1", "ABA2", "ABA3", "ABA4", "ABA5", "BBA2", "BCA1", "BCA2",
                "BCA3", "BCA4", "BFA1", "BHA1", "XXXX");
    }

    @ParameterizedTest(name = "Case Flag Paths: serviceId {0}")
    @MethodSource("serviceIds")
    void namePathAndCodePathMatchHierarchy(String serviceId) throws IOException {
        Response response = apiSteps.callCaseFlagsApi("path fields", serviceId, commonDataApiClient);
        String json = response.asString();

        pathSteps.recordPathTable(serviceId, json);
        pathSteps.verifyPaths(serviceId, json);
    }
}