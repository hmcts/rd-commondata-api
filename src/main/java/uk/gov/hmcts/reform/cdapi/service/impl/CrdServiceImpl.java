package uk.gov.hmcts.reform.cdapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.cdapi.controllers.request.CategoryRequest;
import uk.gov.hmcts.reform.cdapi.controllers.response.Category;
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

@Slf4j
@Service
public class CrdServiceImpl implements CrdService {

    private static final String PARENT = "PARENT";

    @Autowired
    ListOfValuesRepository listOfValuesRepository;

    @Override
    public List<Category> retrieveListOfValuesByCategory(CategoryRequest request) {
        List<ListOfValueDto> list;
        boolean isChildRequired = isChildRequired(request);
        checkCategoryExists(request);
        Specification<ListOfValueDto> query = prepareBaseQuerySpecification(request);
        if (isChildRequired) {
            query = query.or(parentCategory(request.getCategoryId()).and(parentKey(request.getKey()))
                    .and(serviceId(request.getServiceId())));
        }

        list = checkServiceIdExists(request, query,isChildRequired);
        if(list != null) {
            log.info("***************** List:: " + list.size());
        }
        List<Category> channelList = convertCategoryList(list);

        if (isChildRequired) {
            channelList = mapToParentCategory(channelList);
        }
        log.info("***************** channelList:: " + channelList.size());
        log.info("***************** channelList:: " + channelList.get(0).getKey());
        log.info("***************** channelList:: " + channelList.get(0).getCategoryKey());
        if(channelList.get(0).getChildNodes() != null) {
            log.info("***************** channelList:: " + channelList.get(0).getChildNodes().size());
        }
        return channelList;
    }

    public void checkCategoryExists(CategoryRequest request) {
        log.info("checkCategoryExists {}", request.getCategoryId());
        Specification<ListOfValueDto> doesCategoryExistQuery = prepareCategoryExistsQuerySpecification(request);
        List<ListOfValueDto> list  = listOfValuesRepository.findAll(doesCategoryExistQuery);
        if (request.getCategoryId() == null || request.getCategoryId().isEmpty() || list.isEmpty()) {
            log.info("Before Data not found error");
            throw new ResourceNotFoundException("Data not found");
        }
    }

    public List<ListOfValueDto> checkServiceIdExists(CategoryRequest request,
                                                     Specification<ListOfValueDto> query,
                                                     boolean isChildRequired) {
        List<ListOfValueDto> list  = listOfValuesRepository.findAll(query);
        log.info("checkServiceIdExists list size {}", list.size());

        if (list.isEmpty()) {
            log.info("checkServiceIdExists list empty");
            request.setServiceId("");
            query = prepareBaseQuerySpecification(request);
            if (isChildRequired) {
                query = query.or(parentCategory(request.getCategoryId()).and(parentKey(request.getKey()))
                                     .and(serviceId(request.getServiceId())));
            }
            list = listOfValuesRepository.findAll(query);
            log.info("checkServiceIdExists second query list size {}", list.size());
        }
        return list;
    }

    public boolean isChildRequired(CategoryRequest request) {
        return "Y".equalsIgnoreCase(request.getIsChildRequired())
            && request.getParentCategory() == null && request.getParentKey() == null;
    }

    public Specification<ListOfValueDto> prepareBaseQuerySpecification(CategoryRequest request) {
        return where(categoryKey(request.getCategoryId()))
            .and(serviceId(request.getServiceId()))
            .and(parentCategory(request.getParentCategory()))
            .and(parentKey(request.getParentKey()))
            .and(key(request.getKey()));
    }

    public Specification<ListOfValueDto> prepareCategoryExistsQuerySpecification(CategoryRequest request) {
        return where(categoryKey(request.getCategoryId()));
    }

    private List<Category> mapToParentCategory(List<Category> channelList) {
        Map<String, List<Category>> result = channelList.stream().collect(
            Collectors.groupingBy(h -> StringUtils.isEmpty(h.getParentKey()) ? PARENT
                                      : h.getParentKey() + h.getParentCategory() + h.getServiceId(), HashMap::new,
                                  Collectors.toCollection(ArrayList::new)));
        log.info("result.get(PARENT) {}", result.toString());
        if (result.get(PARENT) != null) {
            result.get(PARENT).forEach(channel -> channel.setChildNodes(result.get(channel.getKey() + channel
                .getCategoryKey() + channel.getServiceId())));
            channelList = result.get(PARENT);
        }
        log.info("channelList {}", channelList);
        return channelList;
    }

    public List<Category> convertCategoryList(List<ListOfValueDto> listOfValueDtos) {
        return listOfValueDtos.stream()
            .map(dto -> Category.builder()
                .categoryKey(dto.getCategoryKey().getCategoryKey())
                .serviceId(dto.getCategoryKey().getServiceId())
                .activeFlag(dto.getActive())
                .hintTextCy(dto.getHintTextCy())
                .hintTextEn(dto.getHintTextEn())
                .key(dto.getCategoryKey().getKey())
                .lovOrder(dto.getLovOrder())
                .valueCy(dto.getValueCy())
                .valueEn(dto.getValueEn())
                .parentCategory(dto.getParentCategory())
                .parentKey(dto.getParentKey())
                .build())
            .sorted((c1, c2) -> {
                if (c1.getLovOrder() != null && c2.getLovOrder() != null) {
                    return Long.compare(c1.getLovOrder(), c2.getLovOrder());
                }
                return CharSequence.compare(c1.getValueEn(), c2.getValueEn());
            })
            .collect(Collectors.toUnmodifiableList());
    }
}
