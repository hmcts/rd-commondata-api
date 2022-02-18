package uk.gov.hmcts.reform.cdapi.helper;

import uk.gov.hmcts.reform.cdapi.controllers.request.CategoryRequest;
import uk.gov.hmcts.reform.cdapi.domain.CategoryKey;
import uk.gov.hmcts.reform.cdapi.domain.ListOfValueDto;

public class CrdTestSupport {

    private CrdTestSupport() {
        // empty constructor
    }

    public static ListOfValueDto createListOfCategoriesDtoMock(String categoryId, String serviceId,
                                                               String parentCategory, String parentKey, String key) {
        CategoryKey categoryKey = new CategoryKey();
        categoryKey.setCategoryKey(categoryId);
        categoryKey.setKey(key);
        ListOfValueDto listOfValueDtoMock = new ListOfValueDto();
        listOfValueDtoMock.setServiceId(serviceId);
        listOfValueDtoMock.setActive(true);
        listOfValueDtoMock.setCategoryKey(categoryKey);
        listOfValueDtoMock.setParentCategory(parentCategory);
        listOfValueDtoMock.setParentKey(parentKey);
        return listOfValueDtoMock;
    }

    public static CategoryRequest buildCategoryRequest(String categoryId, String serviceId, String key,
                                                 String parentCategory, String parentKey, String isChileRequired) {
        return CategoryRequest.builder()
            .categoryId(categoryId)
            .serviceId(serviceId)
            .key(key)
            .parentCategory(parentCategory)
            .parentKey(parentKey)
            .isChildRequired(isChileRequired)
            .build();
    }
}
