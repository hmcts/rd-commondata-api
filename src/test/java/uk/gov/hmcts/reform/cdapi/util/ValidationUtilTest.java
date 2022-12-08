package uk.gov.hmcts.reform.cdapi.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.cdapi.exception.InvalidRequestException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ValidationUtilTest {


    @Test
    void test_invalid_flagtype() {
        InvalidRequestException invalidRequestException = assertThrows(
            InvalidRequestException.class,
            () -> ValidationUtil.validationFlagType(
                "test"
            )
        );
        Assertions.assertEquals("Allowed values are PARTY or CASE", invalidRequestException.getMessage());
    }

    @Test
    void test_validation_flagtype() {
        Assertions.assertDoesNotThrow(() -> ValidationUtil.validationFlagType(
            "Party"));

    }

    @Test
    @DisplayName("Positive Case: validation Case flagtype")
    void test_validation_case_flagtype() {
        Assertions.assertDoesNotThrow(() -> ValidationUtil.validationFlagType(
            "Case"));

    }

    @Test
    void test_welshRequiredWhenNotYorN() {
        InvalidRequestException invalidRequestException = assertThrows(
            InvalidRequestException.class,
            () -> ValidationUtil.validateValueForYorNRequired(
                "test"
            )
        );
        Assertions.assertEquals("Allowed values are Y or N", invalidRequestException.getMessage());
    }

    @Test
    void test_validation_welshRequired() {
        Assertions.assertDoesNotThrow(() -> ValidationUtil.validateValueForYorNRequired(
            "Y"));

    }

    @Test
    void test_availableExternalFlagWhenNotYorN() {
        InvalidRequestException invalidRequestException = assertThrows(
            InvalidRequestException.class,
            () -> ValidationUtil.validateValueForYorNRequired(
                "X"
            )
        );
        Assertions.assertEquals("Allowed values are Y or N", invalidRequestException.getMessage());
    }

    @Test
    void test_validation_availableExternalFlag() {
        Assertions.assertDoesNotThrow(() -> ValidationUtil.validateValueForYorNRequired(
            "Y"));
        Assertions.assertDoesNotThrow(() -> ValidationUtil.validateValueForYorNRequired(
            "N"));

    }

}
