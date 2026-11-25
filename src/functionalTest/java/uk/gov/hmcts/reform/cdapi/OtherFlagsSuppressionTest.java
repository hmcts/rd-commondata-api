package uk.gov.hmcts.reform.cdapi;

import io.restassured.response.Response;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@ExtendWith(SerenityJUnit5Extension.class)
@SpringBootTest
@WithTags({@WithTag("testType:Functional")})
@ActiveProfiles("functional")
class OtherFlagsSuppressionTest extends AuthorizationFunctionalTest {

    private static final List<String> SERVICE_IDS =
            List.of("AAA3", "AAA6", "AAA7", "ABA1", "ABA2", "ABA3", "ABA4", "ABA5", "BBA2", "BCA1", "BCA2",
                    "BCA3", "BCA4", "BFA1", "BHA1", "XXXX");

    private static List<String> SUPPRESSED_FLAG_CODES =
            List.of("CF0002,PF0002,PF0004,PF0007,PF0008,PF0009,PF0011,PF0012,PF0013,CF0003,CF0004,CF0005,CF0006,"
                    + "CF0007,CF0008,PF0014,PF0015,CF0009,PF0016,PF0017,PF0018,CF0010,CF0011,PF0019,SM0002,SM0003,"
                    + "SM0004,SM0005,SM0006,SM0007,CF0012,CF0013,CF0014,PF0020,RA0047,PF0021,PF0022,CF0015,PF0023,"
                    + "PF0024,PF0025,PF0026");

    @Steps
    SuppressionSteps steps;

    static Stream<Arguments> suppressionCases() {
        return SERVICE_IDS
                .stream().flatMap(serviceId ->
                        SUPPRESSED_FLAG_CODES.stream()
                                .map(code -> Arguments.of(serviceId, code)));
    }

    @ParameterizedTest(name = "Service Id {0}: suppress flag code(s) {1}")
    @MethodSource("suppressionCases")
    void otherFlagSuppressedOnlyForGivenFlagCode(String serviceId, String flagCodes) throws IOException {
        Set<String> suppressedFlagCodes = SuppressionSteps.parse(flagCodes);

        String baseline = steps.callBaselineCaseFlagsApi(serviceId, commonDataApiClient).asString();
        Response after = steps.callCaseFlagsApi("suppressed (OTHER_FLAG_CODE_SUPPRESSIONS=" + flagCodes + ")",
                serviceId, commonDataApiClient);

        if (baseline != null) {
            steps.recordCaseFlagStructure("Case Flags BASELINE (serviceId " + serviceId + ")", baseline);
        }
        steps.recordCaseFlagStructure("Case Flags AFTER suppression of " + flagCodes + " (serviceId " + serviceId + ")",
                after.asString());

        Set<String> applicable = steps.applicableFlagCodes(suppressedFlagCodes, baseline);

        Assumptions.assumeTrue(!applicable.isEmpty(),
                "None of " + flagCodes + " exist in the baseline for serviceId " + serviceId + ", nothing to compare");

        steps.verifySuppression(suppressedFlagCodes, baseline, after.asString(), serviceId);
    }
}
