package uk.gov.hmcts.reform.cdapi.helper;

import uk.gov.hmcts.reform.cdapi.domain.CategoryKey;
import uk.gov.hmcts.reform.cdapi.domain.HearingChannelDto;

public class CrdTestSupport {

    private CrdTestSupport() {
        // empty constructor
    }

    public static HearingChannelDto createHearingChannelDtoMock(String categoryId, String serviceId,
                                                                String parentCategory, String parentKey, String key) {
        CategoryKey categoryKey = new CategoryKey();
        categoryKey.setCategoryKey(categoryId);
        categoryKey.setKey(key);
        HearingChannelDto hearingChannelDtoMock = new HearingChannelDto();
        hearingChannelDtoMock.setServiceId(serviceId);
        hearingChannelDtoMock.setActive(true);
        hearingChannelDtoMock.setCategoryKey(categoryKey);
        hearingChannelDtoMock.setParentCategory(parentCategory);
        hearingChannelDtoMock.setParentKey(parentKey);
        return hearingChannelDtoMock;
    }


}
