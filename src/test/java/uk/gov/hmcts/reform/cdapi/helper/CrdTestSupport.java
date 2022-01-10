package uk.gov.hmcts.reform.cdapi.helper;

import uk.gov.hmcts.reform.cdapi.domain.CategoryKey;
import uk.gov.hmcts.reform.cdapi.domain.HearingChannelDto;

public class CrdTestSupport {

    private CrdTestSupport() {
        // empty constructor
    }

    public static HearingChannelDto createHearingChannelDtoMock(String categoryId, String serviceId,
                                                                String parentCategory, String parentKey) {
        CategoryKey categoryKey = new CategoryKey();
        categoryKey.setCategoryKey(categoryId);
        HearingChannelDto hearingChannelDtoMock = new HearingChannelDto();
        hearingChannelDtoMock.setServiceId(serviceId);
        hearingChannelDtoMock.setKey("telephone-CVP");
        hearingChannelDtoMock.setActive(true);
        hearingChannelDtoMock.setCategoryKey(categoryKey);
        hearingChannelDtoMock.setParentCategory(parentCategory);
        hearingChannelDtoMock.setParentKey(parentKey);
        return hearingChannelDtoMock;
    }


}
