package uk.gov.hmcts.reform.cdapi.service;

import uk.gov.hmcts.reform.cdapi.controllers.request.CategoryRequest;
import uk.gov.hmcts.reform.cdapi.controllers.response.Category;

import java.util.List;

public interface CrdService {

    List<Category> retrieveListOfValuesByCategory(CategoryRequest request);
}
