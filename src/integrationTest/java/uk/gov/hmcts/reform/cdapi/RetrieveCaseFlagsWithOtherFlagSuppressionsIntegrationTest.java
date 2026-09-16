package uk.gov.hmcts.reform.cdapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.cdapi.domain.CaseFlag;
import uk.gov.hmcts.reform.cdapi.domain.FlagDetail;
import uk.gov.hmcts.reform.cdapi.service.impl.CaseFlagServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@WithTags({@WithTag("testType:Integration")})
@TestPropertySource(properties = "other-flag-code-suppressions=RA0004")
class RetrieveCaseFlagsWithOtherFlagSuppressionsIntegrationTest extends CdAuthorizationEnabledIntegrationTest {

    private static final String PATH = "/caseflags/service-id={service-id}";

    @Autowired
    private CaseFlagServiceImpl caseFlagService;

    @Test
    void shouldConfigureOtherFlagSuppressionsProperty() {
        assertEquals("RA0004", ReflectionTestUtils.getField(caseFlagService, "otherFlagCodeSuppressions"));
    }

    @Test
    void shouldSuppressOtherFlagWhenSuppressionCodeIsConfigured() throws JsonProcessingException {
        final var configuredSuppressions = ReflectionTestUtils.getField(caseFlagService, "otherFlagCodeSuppressions");

        try {
            ReflectionTestUtils.setField(caseFlagService, "otherFlagCodeSuppressions", "");
            final var unsuppressedResponse = retrieveCaseFlags();
            final var suppressingFlagCode = findSuppressingFlagCode(unsuppressedResponse);
            assertNotNull(suppressingFlagCode);

            ReflectionTestUtils.setField(caseFlagService, "otherFlagCodeSuppressions", suppressingFlagCode);
            final var suppressedResponse = retrieveCaseFlags();

            assertTrue(countFlagsByCode(suppressedResponse, "OT0001")
                           < countFlagsByCode(unsuppressedResponse, "OT0001"));
        } finally {
            ReflectionTestUtils.setField(caseFlagService, "otherFlagCodeSuppressions", configuredSuppressions);
        }
    }

    private CaseFlag retrieveCaseFlags() throws JsonProcessingException {
        return (CaseFlag) commonDataApiClient.retrieveCaseFlagsByServiceId(
            "AAA1?available-external-flag=N",
            CaseFlag.class,
            PATH
        );
    }

    private static long countFlagsByCode(CaseFlag caseFlag, String flagCode) {
        return caseFlag.getFlags().stream()
            .flatMap(flag -> flag.getFlagDetails().stream())
            .mapToLong(flagDetail -> countFlagsByCode(flagDetail, flagCode))
            .sum();
    }

    private static long countFlagsByCode(FlagDetail flagDetail, String flagCode) {
        long currentCount = flagCode.equals(flagDetail.getFlagCode()) ? 1 : 0;
        if (flagDetail.getChildFlags() == null) {
            return currentCount;
        }
        return currentCount + flagDetail.getChildFlags().stream()
            .mapToLong(childFlag -> countFlagsByCode(childFlag, flagCode))
            .sum();
    }

    private static String findSuppressingFlagCode(CaseFlag response) {
        return response.getFlags().get(0).getFlagDetails().stream()
            .map(RetrieveCaseFlagsWithOtherFlagSuppressionsIntegrationTest::findSuppressingFlagCode)
            .filter(flagCode -> flagCode != null)
            .findFirst()
            .orElse(null);
    }

    private static String findSuppressingFlagCode(FlagDetail flagDetail) {
        if (flagDetail.getChildFlags() != null
            && flagDetail.getChildFlags().stream().anyMatch(childFlag -> "OT0001".equals(childFlag.getFlagCode()))) {
            return flagDetail.getChildFlags().stream()
                .map(FlagDetail::getFlagCode)
                .filter(flagCode -> !"OT0001".equals(flagCode))
                .filter(flagCode -> !"CATGRY".equals(flagCode))
                .findFirst()
                .orElse(null);
        }
        if (flagDetail.getChildFlags() == null) {
            return null;
        }
        return flagDetail.getChildFlags().stream()
            .map(RetrieveCaseFlagsWithOtherFlagSuppressionsIntegrationTest::findSuppressingFlagCode)
            .filter(flagCode -> flagCode != null)
            .findFirst()
            .orElse(null);
    }
}
