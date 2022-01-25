package uk.gov.hmcts.reform.cdapi.service;

import uk.gov.hmcts.reform.cdapi.domain.HearingChannel;

import java.util.List;

public interface CrdService {

    List<HearingChannel> retrieveHearingChannelsByCategoryId(String categoryId, String serviceId,
                                                             String parentCategory, String parentKey);
}
