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

    public static void validationYesOrNo(String yesOrNoParam) {
        boolean validYesOrNoParam = Pattern.compile(ALLOW_Y_OR_N_REGEX).matcher(yesOrNoParam).matches();
        if (Boolean.FALSE.equals(validYesOrNoParam)) {
            throw new InvalidRequestException("Allowed values are Y or N");
        }
    }
}
