package uk.gov.hmcts.reform.cdapi.service;

import uk.gov.hmcts.reform.cdapi.domain.Category;

import java.util.List;

public interface CrdService {

    List<Category> retrieveListOfValuesByCategoryId(String categoryId, String serviceId,
                                        String parentCategory, String parentKey, String key, boolean isChildRequired);
}
