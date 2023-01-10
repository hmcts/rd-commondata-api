package uk.gov.hmcts.reform.cdapi.domain.jsonfilter;

import org.apache.commons.lang3.ObjectUtils;

import static uk.gov.hmcts.reform.cdapi.service.impl.CaseFlagServiceImpl.IGNORE_JSON;

public class IgnoreJsonFilter {

    @Override
    public boolean equals(Object name) {
        if (ObjectUtils.isNotEmpty(name) && name.equals(IGNORE_JSON)) {
            return true;
        }
        return false;
    }
}
