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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        final var response = (CaseFlag) commonDataApiClient.retrieveCaseFlagsByServiceId(
            "AAA1?available-external-flag=N",
            CaseFlag.class,
            PATH
        );

        assertEquals(3, countFlagsByCode(response, "OT0001"));
        assertTrue(containsImmediateFlagCode(getFlagByName(response, "Case").getChildFlags(), "OT0001"));
        assertTrue(containsImmediateFlagCode(getFlagByName(response, "Party").getChildFlags(), "OT0001"));
        assertFalse(containsImmediateFlagCode(getFlagByName(response, "Reasonable adjustment")
                                                  .getChildFlags(), "OT0001"));
        assertTrue(containsImmediateFlagCode(getFlagByName(response, "I need help communicating and understanding")
                                                 .getChildFlags(), "OT0001"));
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

    private static FlagDetail getFlagByName(CaseFlag response, String name) {
        return response.getFlags().get(0).getFlagDetails().stream()
            .map(flagDetail -> getFlagByName(flagDetail, name))
            .filter(FlagDetail.class::isInstance)
            .findFirst()
            .orElseThrow();
    }

    private static FlagDetail getFlagByName(FlagDetail flagDetail, String name) {
        if (name.equals(flagDetail.getName())) {
            return flagDetail;
        }
        if (flagDetail.getChildFlags() == null) {
            return null;
        }
        return flagDetail.getChildFlags().stream()
            .map(childFlag -> getFlagByName(childFlag, name))
            .filter(FlagDetail.class::isInstance)
            .findFirst()
            .orElse(null);
    }

    private static boolean containsImmediateFlagCode(List<FlagDetail> flagDetails, String flagCode) {
        return flagDetails.stream().anyMatch(flagDetail -> flagCode.equals(flagDetail.getFlagCode()));
    }
}
