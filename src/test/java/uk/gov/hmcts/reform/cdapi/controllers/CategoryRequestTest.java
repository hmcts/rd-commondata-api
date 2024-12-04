package uk.gov.hmcts.reform.cdapi.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.cdapi.controllers.request.CategoryRequest;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
class CategoryRequestTest {

    private final List<CategoryRequest> dxAddresses = new ArrayList<>();

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void test_has_mandatory_fields_specified_not_null() {
        CategoryRequest categoryRequest = new CategoryRequest(
                null, null, null, null, null, null,
                null, null);

        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(categoryRequest);
        assertThat(violations).hasSize(2);
    }

    @Test
    void test_creates_contact_information_creation_request_correctly() {
        CategoryRequest categoryRequest = new CategoryRequest("panelCategoryMember",
                                                              "BBA3", "panelCategory",
                                                              "BBA3-panelCategory-001",
                                                              "BBA3-panelCategory-001-74", "N",
                                                              "74", "JudicialRole");


        assertThat(categoryRequest.getCategoryId()).isEqualTo("panelCategoryMember");
        assertThat(categoryRequest.getParentCategory()).isEqualTo("panelCategory");
        assertThat(categoryRequest.getParentKey()).isEqualTo("BBA3-panelCategory-001");
        assertThat(categoryRequest.getKey()).isEqualTo("BBA3-panelCategory-001-74");
        assertThat(categoryRequest.getServiceId()).isEqualTo("BBA3");
        assertThat(categoryRequest.getExternalReference()).isEqualTo("JudicialRole");
        assertThat(categoryRequest.getExternalReferenceType()).isEqualTo("74");
        assertThat(categoryRequest.getIsChildRequired()).isEqualTo("N");

    }


    @Test
    void test_categoryRequestBuilder() {
        String categoryId = "panelCategoryMember";
        String serviceId = "BBA3";
        String parentCategory = "panelCategory";
        String parentKey = "BBA3-panelCategory-001";
        String key = "BBA3-panelCategory-001-74";
        String isChildRequired = "N";
        String externalReferenceType = "74";
        String externalReference = "JudicialRole";

        CategoryRequest categoryRequest = CategoryRequest.builder()
            .parentCategory(parentCategory)
            .categoryId(categoryId)
            .externalReferenceType(externalReferenceType)
            .externalReference(externalReference)
            .parentKey(parentKey)
            .key(key)
            .serviceId(serviceId)
            .isChildRequired(isChildRequired)
            .build();

        assertThat(categoryRequest.getCategoryId()).isEqualTo(categoryId);
        assertThat(categoryRequest.getParentCategory()).isEqualTo(parentCategory);
        assertThat(categoryRequest.getParentKey()).isEqualTo(parentKey);
        assertThat(categoryRequest.getKey()).isEqualTo(key);
        assertThat(categoryRequest.getServiceId()).isEqualTo(serviceId);
        assertThat(categoryRequest.getExternalReference()).isEqualTo(externalReference);
        assertThat(categoryRequest.getExternalReferenceType()).isEqualTo(externalReferenceType);
        assertThat(categoryRequest.getIsChildRequired()).isEqualTo(isChildRequired);

    }
}
