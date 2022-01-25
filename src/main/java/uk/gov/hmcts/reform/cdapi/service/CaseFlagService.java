package uk.gov.hmcts.reform.cdapi.service;

import uk.gov.hmcts.reform.cdapi.domain.CaseFlag;

public interface CaseFlagService {

    CaseFlag retrieveCaseFlagByServiceId(String serviceId,String flagType,String welshRequired);
}
