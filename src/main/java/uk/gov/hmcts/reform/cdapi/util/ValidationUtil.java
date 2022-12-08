package uk.gov.hmcts.reform.cdapi.util;

import uk.gov.hmcts.reform.cdapi.domain.FlagType;
import uk.gov.hmcts.reform.cdapi.exception.InvalidRequestException;

import java.util.regex.Pattern;

import static uk.gov.hmcts.reform.cdapi.controllers.constant.Constant.ALLOW_Y_OR_N_REGEX;

public class ValidationUtil {

    private ValidationUtil() {

    }

    public static void validationFlagType(String flagType) {
        boolean validFlag = false;
        for (FlagType flagTypes : FlagType.values()) {
            if (flagTypes.name().equalsIgnoreCase(flagType)) {
                validFlag = true;
                break;
            }

        }
        if (!validFlag) {
            throw new InvalidRequestException("Allowed values are PARTY or CASE");
        }
    }

    public static void validateValueForYorNRequired(String value) {
        boolean validWelshLanguage = Pattern.compile(ALLOW_Y_OR_N_REGEX).matcher(value).matches();
        if (Boolean.FALSE.equals(validWelshLanguage)) {
            throw new InvalidRequestException("Allowed values are Y or N");
        }
    }
}
