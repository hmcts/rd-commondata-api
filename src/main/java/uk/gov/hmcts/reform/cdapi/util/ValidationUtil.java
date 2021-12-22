package uk.gov.hmcts.reform.cdapi.util;

import uk.gov.hmcts.reform.cdapi.domain.FlagType;
import uk.gov.hmcts.reform.cdapi.exception.InvalidRequestException;

public class ValidationUtil {

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
}
