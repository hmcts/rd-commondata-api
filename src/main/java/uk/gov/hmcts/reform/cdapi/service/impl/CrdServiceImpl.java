package uk.gov.hmcts.reform.cdapi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.cdapi.domain.HearingChannel;
import uk.gov.hmcts.reform.cdapi.domain.HearingChannelDto;
import uk.gov.hmcts.reform.cdapi.repository.HearingChannelRepository;
import uk.gov.hmcts.reform.cdapi.service.CrdService;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.Specification.where;
import static uk.gov.hmcts.reform.cdapi.domain.QuerySpecification.hearingChannelCategoryKey;
import static uk.gov.hmcts.reform.cdapi.domain.QuerySpecification.hearingChannelParentCategory;
import static uk.gov.hmcts.reform.cdapi.domain.QuerySpecification.hearingChannelParentKey;
import static uk.gov.hmcts.reform.cdapi.domain.QuerySpecification.hearingChannelServiceId;

@Service
public class CrdServiceImpl implements CrdService {

    @Autowired
    HearingChannelRepository hearingChannelRepository;

    @Override
    public List<HearingChannel> retrieveHearingChannelsByCategoryId(String categoryId, String serviceId,
                                                                    String parentCategory, String parentKey) {
        List<HearingChannelDto> list = hearingChannelRepository.findAll(
            where(hearingChannelCategoryKey(categoryId))
                .and(hearingChannelServiceId(serviceId))
                .and(hearingChannelParentCategory(parentCategory))
                .and(hearingChannelParentKey(parentKey)));

        return convertHearingChannelList(list);
    }

    private List<HearingChannel> convertHearingChannelList(List<HearingChannelDto> hearingChannelDtos) {

        return hearingChannelDtos.stream()
            .map(dto -> HearingChannel.builder()
                .categoryKey(dto.getCategoryKey().getCategoryKey())
                .serviceId(dto.getCategoryKey().getServiceId())
                .active(dto.getActive())
                .hintTextCy(dto.getHintTextCy())
                .hintTextEn(dto.getHintTextEn())
                .key(dto.getCategoryKey().getKey())
                .lovOrder(dto.getLovOrder())
                .valueCy(dto.getValueCy())
                .valueEn(dto.getValueEn())
                .parentCategory(dto.getParentCategory())
                .parentKey(dto.getParentKey())
                .build()).collect(Collectors.toUnmodifiableList());
    }
}
