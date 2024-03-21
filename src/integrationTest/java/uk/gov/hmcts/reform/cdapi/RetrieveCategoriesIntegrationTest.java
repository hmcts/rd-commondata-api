package uk.gov.hmcts.reform.cdapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import jxl.common.Assert;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.cdapi.controllers.response.Categories;
import uk.gov.hmcts.reform.cdapi.controllers.response.Category;
import uk.gov.hmcts.reform.cdapi.exception.ErrorResponse;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


public class RetrieveCategoriesIntegrationTest extends CdAuthorizationEnabledIntegrationTest {

    private static final String path = "/lov/categories/{category-id}";


    @DisplayName("Retrieve categories for Child ")
    void shouldRetrieveCategoriesForCategoryIdWithStatusCode200()
        throws JsonProcessingException {
        final var response = (Categories)
            commonDataApiClient.retrieveCaseFlagsByServiceId("HearingSubChannel",
                                                             Categories.class, path
            );
        assertNotNull(response);
        assertEquals(1, response.getListOfCategory().size());
        responseVerification(response.getListOfCategory().get(0));
    }


    @DisplayName("Retrieve categories for Parent ")
    void shouldRetrieveCategoriesForChildCategoryIdWithStatusCode200()
        throws JsonProcessingException {
        final var response = (Categories)
            commonDataApiClient.retrieveCaseFlagsByServiceId("HearingChannel",
                                                             Categories.class, path
            );
        assertNotNull(response);
        Assert.verify(response.getListOfCategory().size() > 0);
        response.getListOfCategory().forEach(category -> {
            assertThat(category.getCategoryKey()).isEqualTo("HearingChannel");
            assertThat(category.getActiveFlag()).isEqualTo("Y");
        });
    }


    void shouldRetrieveCategoriesWithAllParamsWithStatusCode200(String flag)
        throws JsonProcessingException {
        final var response = (Categories)
            commonDataApiClient.retrieveCaseFlagsByServiceId("HearingSubChannel?serviceId=BBA3"
                                                             + "&parentCategory=HearingChannel&parentKey=telephone"
                                                                 + "&isChildRequired=" + flag,
                                                             Categories.class, path
            );
        assertNotNull(response);
        assertEquals(1, response.getListOfCategory().size());
        responseVerification(response);
    }


    void shouldRetrieveCategoriesWithParentCategory(String flag)
        throws JsonProcessingException {
        final var response = (Categories)
            commonDataApiClient.retrieveCaseFlagsByServiceId("HearingSubChannel?parentCategory"
                                                                 + "=HearingChannel&isChildRequired=" + flag,
                                                             Categories.class, path
            );
        assertNotNull(response);
        assertEquals(1, response.getListOfCategory().size());
        responseVerification(response);
    }


    void shouldRetrieveCategoriesWithParentKey(String flag)
        throws JsonProcessingException {
        final var response = (Categories)
            commonDataApiClient.retrieveCaseFlagsByServiceId("HearingSubChannel?parentKey"
                                                                 + "=telephone&isChildRequired=" + flag,
                                                             Categories.class, path
            );
        assertNotNull(response);
        assertEquals(1, response.getListOfCategory().size());
        responseVerification(response);
    }


    void shouldRetrieveCategoriesWithServiceIdWithChildNodesForParents(String flag)
        throws JsonProcessingException {
        final var response = (Categories)
            commonDataApiClient.retrieveCaseFlagsByServiceId("HearingChannel?serviceId=BBA3"
                                                                 + "&isChildRequired=" + flag,
                                                             Categories.class, path
            );
        assertNotNull(response);
        assertEquals(4, response.getListOfCategory().size());
        if (flag.equals("Y")) {
            assertEquals(1, response.getListOfCategory().get(1).getChildNodes().size());
            responseVerification(response.getListOfCategory().get(1).getChildNodes().get(0));
        } else {
            responseVerificationWithOutChildNodes(response);
        }
    }



    void shouldRetrieveCategoriesWithServiceIdWithChildNodesForChild(String flag)
        throws JsonProcessingException {
        final var response = (Categories)
            commonDataApiClient.retrieveCaseFlagsByServiceId("HearingSubChannel?serviceId=BBA3"
                                                                 + "&isChildRequired=" + flag,
                                                             Categories.class, path
            );
        assertNotNull(response);
        assertEquals(1, response.getListOfCategory().size());
        responseVerification(response.getListOfCategory().get(0));
    }



    void shouldThrowStatusCode404ForInvalidCategoryId()
        throws JsonProcessingException {

        var errorResponseMap = commonDataApiClient.retrieveCaseFlagsByServiceId(
            "abc", ErrorResponse.class, path);
        assertNotNull(errorResponseMap);
        assertThat((Map<String, Object>) errorResponseMap).containsEntry("http_status", HttpStatus.NOT_FOUND);
    }


    void shouldThrowStatusCode404ForNullCategoryId()
        throws JsonProcessingException {
        var errorResponseMap = commonDataApiClient.retrieveCaseFlagsByServiceId(
            null,Map.class, path);
        assertNotNull(errorResponseMap);
        assertThat((Map<String, Object>) errorResponseMap).containsEntry("http_status", HttpStatus.NOT_FOUND);
    }

    private void responseVerification(Categories response) {
        for (Category hearingChannels : response.getListOfCategory()) {
            assertThat(hearingChannels.getParentCategory()).isEqualTo("HearingChannel");
            assertThat(hearingChannels.getParentKey()).isEqualTo("telephone");
            assertThat(hearingChannels.getActiveFlag()).isEqualTo("Y");
            assertNull(hearingChannels.getChildNodes());

        }
    }

    private void responseVerification(Category response) {
        assertThat(response.getKey()).isEqualTo("telephone-btMeetMe");
        assertThat(response.getValueEn()).isEqualTo("Telephone - BTMeetme");
        assertThat(response.getValueCy()).isNull();
        assertThat(response.getHintTextCy()).isNull();
        assertThat(response.getHintTextEn()).isNull();
        assertThat(response.getLovOrder()).isNull();
        assertThat(response.getParentCategory()).isEqualTo("HearingChannel");
        assertThat(response.getParentKey()).isEqualTo("telephone");
        assertThat(response.getActiveFlag()).isEqualTo("Y");
    }

    private void responseVerificationWithOutChildNodes(Categories response) {
        for (Category hearingChannels : response.getListOfCategory()) {
            assertThat(hearingChannels.getParentCategory()).isEqualTo(null);
            assertThat(hearingChannels.getParentKey()).isEqualTo(null);
            assertThat(hearingChannels.getActiveFlag()).isEqualTo("Y");
            assertNull(hearingChannels.getChildNodes());

        }
    }


    void shouldRetrieveCategoriesWithEmptyServiceIdWithChildNodes()
        throws JsonProcessingException {
        final var response = (Categories)
            commonDataApiClient.retrieveCaseFlagsByServiceId("ListingStatus?"
                                                                 + "&isChildRequired=Y&serviceId=",
                                                             Categories.class, path
            );
        Category category = response.getListOfCategory().get(0);
        assertNotNull(category);
        assertEquals(1, response.getListOfCategory().size());
        assertThat(category.getKey()).isEqualTo("test");
        assertThat(category.getValueEn()).isEqualTo("test");
        assertThat(category.getValueCy()).isNull();
        assertThat(category.getHintTextCy()).isNull();
        assertThat(category.getHintTextEn()).isNull();
        assertThat(category.getLovOrder()).isNull();
        assertThat(category.getParentCategory()).isNull();
        assertThat(category.getParentKey()).isNull();
        assertThat(category.getActiveFlag()).isEqualTo("Y");
        assertEquals(1,category.getChildNodes().size());
        assertThat(category.getChildNodes().get(0).getCategoryKey()).isEqualTo("ListingStatusSubChannel");
        assertThat(category.getChildNodes().get(0).getParentKey()).isEqualTo("test");
        assertThat(category.getChildNodes().get(0).getParentCategory()).isEqualTo("ListingStatus");
    }


    void shouldRetrieveCategoriesWithEmptyServiceIdWithoutChildNodes()
        throws JsonProcessingException {
        final var response = (Categories)
            commonDataApiClient.retrieveCaseFlagsByServiceId("EmptySubCategory?"
                                                                 + "&isChildRequired=Y&serviceId=",
                                                             Categories.class, path
            );
        Category category = response.getListOfCategory().get(0);
        assertNotNull(category);
        assertEquals(1, response.getListOfCategory().size());
        assertThat(category.getKey()).isEqualTo("test");
        assertThat(category.getValueEn()).isEqualTo("test");
        assertThat(category.getValueCy()).isNull();
        assertThat(category.getHintTextCy()).isNull();
        assertThat(category.getHintTextEn()).isNull();
        assertThat(category.getLovOrder()).isNull();
        assertThat(category.getParentCategory()).isNull();
        assertThat(category.getParentKey()).isNull();
        assertThat(category.getActiveFlag()).isEqualTo("Y");
        assertNull(category.getChildNodes());
    }


    @SuppressWarnings("unchecked")

    void retrieveCategoriesWithInvalidServiceIdWithStatusCode200(String serviceId)
        throws JsonProcessingException {
        final var response = (Categories)
            commonDataApiClient.retrieveCaseFlagsByServiceId("CaseLinkingReasonCode?serviceId=" + serviceId,
                                                             Categories.class, path
            );
        assertNotNull(response);
        assertEquals(1, response.getListOfCategory().size());
        assertThat(response.getListOfCategory().get(0).getKey()).isEqualTo("CLRC006");
        assertThat(response.getListOfCategory().get(0).getValueEn()).isEqualTo("Guardian");
        assertThat(response.getListOfCategory().get(0).getActiveFlag()).isEqualTo("Y");
    }

    void retrieveCategoriesWithNullServiceIdWithStatusCode200()
        throws JsonProcessingException {
        final var response = (Categories)
            commonDataApiClient.retrieveCaseFlagsByServiceId("CaseLinkingReasonCode?serviceId=" + null,
                                                             Categories.class, path
            );
        assertNotNull(response);
        assertEquals(1, response.getListOfCategory().size());
        assertThat(response.getListOfCategory().get(0).getKey()).isEqualTo("CLRC006");
        assertThat(response.getListOfCategory().get(0).getValueEn()).isEqualTo("Guardian");
        assertThat(response.getListOfCategory().get(0).getActiveFlag()).isEqualTo("Y");
    }

    void shouldRetrieveCategoriesWithValidServiceIdWithStatusCode200()
        throws JsonProcessingException {
        final var response = (Categories)
            commonDataApiClient.retrieveCaseFlagsByServiceId("CaseLinkingReasonCode?serviceId=ABA3",
                                                             Categories.class, path
            );
        assertNotNull(response);
        assertEquals(2, response.getListOfCategory().size());
        assertThat(response.getListOfCategory().get(1).getKey()).isEqualTo("CLRC002");
        assertThat(response.getListOfCategory().get(1).getValueEn()).isEqualTo("Related proceedings");
        assertThat(response.getListOfCategory().get(1).getActiveFlag()).isEqualTo("Y");
        assertThat(response.getListOfCategory().get(0).getKey()).isEqualTo("CLRC017");
        assertThat(response.getListOfCategory().get(0).getValueEn()).isEqualTo("Linked for a hearing");
        assertThat(response.getListOfCategory().get(0).getActiveFlag()).isEqualTo("Y");
    }


    void retrieveCategoriesWithNotExistingServiceIdAndResultEmptyWithStatusCode200()
        throws JsonProcessingException {
        final var response = (Categories)
            commonDataApiClient.retrieveCaseFlagsByServiceId("HearingChannel?serviceId='XXX'",
                                                             Categories.class, path);
        assertNotNull(response);
        assertEquals(0, response.getListOfCategory().size());
        assertTrue(response.getListOfCategory().isEmpty());
    }



    void retrieveCategoriesWithNotExistingServiceIdAndResultNotEmptyAndIsChildRequiredWithStatusCode200()
        throws JsonProcessingException {
        final var response = (Categories)
            commonDataApiClient.retrieveCaseFlagsByServiceId("caseSubType?isChildRequired=Y&serviceId='XXX'",
                                                             Categories.class, path);
        assertNotNull(response);
        assertEquals(1, response.getListOfCategory().size());
        assertThat(response.getListOfCategory().get(0).getKey()).isEqualTo("CLRC017");
        assertThat(response.getListOfCategory().get(0).getValueEn()).isEqualTo("Linked for a hearing");
        assertThat(response.getListOfCategory().get(0).getActiveFlag()).isEqualTo("Y");
    }


    void retrieveCategoriesWithNotExistingServiceIdAndResultNotEmptyAndChildNotRequiredWithStatusCode200()
        throws JsonProcessingException {
        final var response = (Categories)
            commonDataApiClient.retrieveCaseFlagsByServiceId("CaseLinkingReasonCode?isChildRequired=N&serviceId='XXX'",
                                                             Categories.class, path);
        assertNotNull(response);
        assertEquals(1, response.getListOfCategory().size());
        assertThat(response.getListOfCategory().get(0).getKey()).isEqualTo("CLRC006");
        assertThat(response.getListOfCategory().get(0).getValueEn()).isEqualTo("Guardian");
        assertThat(response.getListOfCategory().get(0).getActiveFlag()).isEqualTo("Y");
    }


    void retrieveCategoriesWithNotExistingServiceIdAndResultEmptyAnChildRequiredWithStatusCode200()
        throws JsonProcessingException {
        final var response = (Categories)
            commonDataApiClient.retrieveCaseFlagsByServiceId("HearingChannel?isChildRequired=Y&serviceId='XXX'",
                                                             Categories.class, path);
        assertNotNull(response);
        assertEquals(0, response.getListOfCategory().size());
        assertTrue(response.getListOfCategory().isEmpty());
    }


    void shouldThrowErrorWhenRetrievingDataForNotEsistingCategory()
        throws JsonProcessingException {

        var errorResponseMap = commonDataApiClient.retrieveCaseFlagsByServiceId(
            "XXXXX?isChildRequired=Y&serviceId='XXX'", ErrorResponse.class, path);

        assertNotNull(errorResponseMap);
        assertThat((Map<String, Object>) errorResponseMap).containsEntry("http_status", HttpStatus.NOT_FOUND);
    }



    void shouldThrowErrorWhenRetrievingDataForNullCategory()
        throws JsonProcessingException {

        var errorResponseMap = commonDataApiClient.retrieveCaseFlagsByServiceId(
            null + "?isChildRequired=Y&serviceId='XXX'", ErrorResponse.class, path);

        assertNotNull(errorResponseMap);
        assertThat((Map<String, Object>) errorResponseMap).containsEntry("http_status", HttpStatus.NOT_FOUND);
    }



}
