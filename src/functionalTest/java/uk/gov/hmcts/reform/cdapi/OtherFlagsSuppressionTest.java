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

@SuppressWarnings("checkstyle:all")
@ExtendWith(SerenityJUnit5Extension.class)
@SpringBootTest
@WithTags({@WithTag("testType:Functional")})
@ActiveProfiles("functional")
class OtherFlagsSuppressionTest extends AuthorizationFunctionalTest {

    private static final List<String> SERVICE_IDS = List.of("AAA3", "AAA6", "AAA7", "ABA1", "ABA2", "ABA3", "ABA4",
            "ABA5", "BBA2", "BCA1", "BCA2", "BCA3", "BCA4", "BFA1", "BHA1", "XXXX");

    private static List<String> SUPPRESSED_FLAG_CODES = List.of("CF0001, PF0001, SM0001, RA0001, RA0002, RA0003, " +
            "RA0004, " +
            "RA0005, RA0006, RA0007");

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

        String baseline = steps.callBaselineFlagsApi(serviceId, commonDataApiClient).asString();
        Response after = steps.callFlagsApi("suppressed (OTHER_FLAG_CODE_SUPPRESSIONS=" + flagCodes + ")", serviceId,
                commonDataApiClient);

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
