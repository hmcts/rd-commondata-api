package uk.gov.hmcts.reform.cdapi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.cdapi.domain.Category;
import uk.gov.hmcts.reform.cdapi.domain.ListOfValueDto;
import uk.gov.hmcts.reform.cdapi.exception.ResourceNotFoundException;
import uk.gov.hmcts.reform.cdapi.repository.ListOfValuesRepository;
import uk.gov.hmcts.reform.cdapi.service.CrdService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.Specification.where;
import static uk.gov.hmcts.reform.cdapi.domain.QuerySpecification.categoryKey;
import static uk.gov.hmcts.reform.cdapi.domain.QuerySpecification.key;
import static uk.gov.hmcts.reform.cdapi.domain.QuerySpecification.parentCategory;
import static uk.gov.hmcts.reform.cdapi.domain.QuerySpecification.parentKey;
import static uk.gov.hmcts.reform.cdapi.domain.QuerySpecification.serviceId;

@Service
public class CrdServiceImpl implements CrdService {

    private static final String PARENT = "PARENT";

    @Autowired
    ListOfValuesRepository listOfValuesRepository;

    @Override
    public List<Category> retrieveListOfValuesByCategoryId(String categoryId, String serviceId,
                                       String parentCategory, String parentKey, String key, boolean isChildRequired) {
        Specification<ListOfValueDto> query = where(categoryKey(categoryId))
            .and(serviceId(serviceId))
            .and(parentCategory(parentCategory))
            .and(parentKey(parentKey))
            .and(key(key));

        isChildRequired = isChildRequired && parentCategory == null && parentKey == null;
        if (isChildRequired) {
            query = query.or(parentCategory(categoryId).and(parentKey(key)));
        }

        List<ListOfValueDto> list = listOfValuesRepository.findAll(query);
        if (list.isEmpty()) {
            throw new ResourceNotFoundException("Data not found");
        }

        List<Category> channelList = convertCategoryList(list);

        if (isChildRequired) {
            Map<String, List<Category>> result = channelList.stream().collect(
                Collectors.groupingBy(h -> h.getParentKey() == null ? PARENT : h.getParentKey(), HashMap::new,
                                      Collectors.toCollection(ArrayList::new)));
            if (result.get(PARENT) != null) {
                result.get(PARENT).forEach(channel -> channel.setChildNodes(result.get(channel.getKey())));
                channelList = result.get(PARENT);
            }
        }
        return channelList;
    }

    private List<Category> convertCategoryList(List<ListOfValueDto> listOfValueDtos) {
        return listOfValueDtos.stream()
            .map(dto -> Category.builder()
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
