package uk.gov.hmcts.reform.cdapi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.cdapi.domain.HearingChannel;
import uk.gov.hmcts.reform.cdapi.domain.HearingChannelDto;
import uk.gov.hmcts.reform.cdapi.exception.ResourceNotFoundException;
import uk.gov.hmcts.reform.cdapi.repository.HearingChannelRepository;
import uk.gov.hmcts.reform.cdapi.service.CrdService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.Specification.where;
import static uk.gov.hmcts.reform.cdapi.domain.QuerySpecification.hearingChannelCategoryKey;
import static uk.gov.hmcts.reform.cdapi.domain.QuerySpecification.hearingChannelKey;
import static uk.gov.hmcts.reform.cdapi.domain.QuerySpecification.hearingChannelParentCategory;
import static uk.gov.hmcts.reform.cdapi.domain.QuerySpecification.hearingChannelParentKey;
import static uk.gov.hmcts.reform.cdapi.domain.QuerySpecification.hearingChannelServiceId;

@Service
public class CrdServiceImpl implements CrdService {

    private static final String PARENT = "PARENT";

    @Autowired
    HearingChannelRepository hearingChannelRepository;

    @Override
    public List<HearingChannel> retrieveHearingChannelsByCategoryId(String categoryId, String serviceId,
                                                                    String parentCategory, String parentKey, String key, Boolean isChildRequired) {
        Specification<HearingChannelDto> query = where(hearingChannelCategoryKey(categoryId))
            .and(hearingChannelServiceId(serviceId))
            .and(hearingChannelParentCategory(parentCategory))
            .and(hearingChannelParentKey(parentKey))
            .and(hearingChannelKey(key));

        isChildRequired = isChildRequired ? parentCategory == null && parentKey == null : isChildRequired;
        if (isChildRequired) {
            query = query.or(hearingChannelParentCategory(categoryId).and(hearingChannelParentKey(key)));
        }

        List<HearingChannelDto> list = hearingChannelRepository.findAll(query);
        if (list.isEmpty()) {
            throw new ResourceNotFoundException("Data not found");
        }

        List<HearingChannel> channelList = convertHearingChannelList(list);

        if (isChildRequired) {
            Map<String, List<HearingChannel>> result = channelList.stream().collect(
                Collectors.groupingBy(h -> h.getParentKey() == null ? PARENT : h.getParentKey(), HashMap::new,
                                      Collectors.toCollection(ArrayList::new)));
            if(result.get(PARENT)!=null) {
                result.get(PARENT).forEach(channel -> channel.setChildNodes(result.get(channel.getKey())));
                channelList = result.get(PARENT);
            }
        }
        return channelList;
    }

    private List<HearingChannel> convertHearingChannelList(List<HearingChannelDto> hearingChannelDtos) {
        return hearingChannelDtos.stream()
            .map(dto -> HearingChannel.builder()
                .categoryKey(dto.getCategoryKey().getCategoryKey())
                .serviceId(dto.getServiceId())
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
