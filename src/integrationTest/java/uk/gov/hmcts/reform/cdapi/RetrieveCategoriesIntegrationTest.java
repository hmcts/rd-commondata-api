package uk.gov.hmcts.reform.cdapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import jxl.common.Assert;
import net.serenitybdd.annotations.Description;
import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@WithTags({@WithTag("testType:Integration")})
public class RetrieveCategoriesIntegrationTest extends CdAuthorizationEnabledIntegrationTest {

    private static final String path = "/lov/categories/{category-id}";


    @Test
    @DisplayName("Retrieve categories with Parent and child nodes externalReference ")
    void shouldRetrieveParentAndChildNodesWithExternalReferenceStatusCode200()
        throws JsonProcessingException {

        final var response = (Categories)
            commonDataApiClient.retrieveCaseFlagsByServiceId("panelCategory?isChildRequired=Y&serviceId=BBA3",
                                                             Categories.class, path
            );
        assertNotNull(response);
        assertEquals(1, response.getListOfCategory().size());
        assertEquals(3, response.getListOfCategory().get(0).getChildNodes().size());

        /// Parent
        assertThat(response.getListOfCategory().get(0).getKey()).isEqualTo("BBA3-panelCategory-001");
        assertThat(response.getListOfCategory().get(0).getCategoryKey()).isEqualTo("panelCategory");
        assertThat(response.getListOfCategory().get(0).getExternalReference()).isNull();
        assertThat(response.getListOfCategory().get(0).getExternalReferenceType()).isNull();

        //child nodes
        assertThat(response.getListOfCategory().get(0).getChildNodes().get(0).getKey()).isEqualTo("PC1-01-94");
        assertThat(response.getListOfCategory().get(0).getChildNodes().get(0).getValueEn())
            .isEqualTo("Financial office holder");
        assertThat(response.getListOfCategory().get(0).getChildNodes().get(0).getParentCategory())
            .isEqualTo("panelCategory");
        assertThat(response.getListOfCategory().get(0).getChildNodes().get(0).getParentKey())
            .isEqualTo("BBA3-panelCategory-001");
        assertThat(response.getListOfCategory().get(0).getChildNodes().get(0).getExternalReference())
            .isEqualTo("94");
        assertThat(response.getListOfCategory().get(0).getChildNodes().get(0).getExternalReferenceType())
            .isEqualTo("FinancialRole");

        assertThat(response.getListOfCategory().get(0).getChildNodes().get(1).getKey()).isEqualTo("PC1-01-84");
        assertThat(response.getListOfCategory().get(0).getChildNodes().get(1).getValueEn())
            .isEqualTo("Judicial office holder");
        assertThat(response.getListOfCategory().get(0).getChildNodes().get(1).getParentCategory())
            .isEqualTo("panelCategory");
        assertThat(response.getListOfCategory().get(0).getChildNodes().get(1).getParentKey())
            .isEqualTo("BBA3-panelCategory-001");
        assertThat(response.getListOfCategory().get(0).getChildNodes().get(1).getExternalReference())
            .isEqualTo("84");
        assertThat(response.getListOfCategory().get(0).getChildNodes().get(1).getExternalReferenceType())
            .isEqualTo("JudicialRole");

        assertThat(response.getListOfCategory().get(0).getChildNodes().get(2).getKey()).isEqualTo("PC1-01-74");
        assertThat(response.getListOfCategory().get(0).getChildNodes().get(2).getValueEn())
            .isEqualTo("Medical office holder");
        assertThat(response.getListOfCategory().get(0).getChildNodes().get(2).getParentCategory())
            .isEqualTo("panelCategory");
        assertThat(response.getListOfCategory().get(0).getChildNodes().get(2).getParentKey())
            .isEqualTo("BBA3-panelCategory-001");
        assertThat(response.getListOfCategory().get(0).getChildNodes().get(2).getExternalReference())
            .isEqualTo("74");
        assertThat(response.getListOfCategory().get(0).getChildNodes().get(2).getExternalReferenceType())
            .isEqualTo("MedicalRole");
    }


    @Test
    @DisplayName("Retrieve categories with externalReference ")
    void shouldRetrieveCategoriesWithExternalReferenceStatusCode200()
        throws JsonProcessingException {

        final var response = (Categories)
            commonDataApiClient.retrieveCaseFlagsByServiceId("panelCategoryMember?serviceId=BBA3",
                                                             Categories.class, path
            );
        assertNotNull(response);
        assertEquals(3, response.getListOfCategory().size());
        assertThat(response.getListOfCategory().get(0).getKey()).isEqualTo("PC1-01-94");
        assertThat(response.getListOfCategory().get(0).getValueEn()).isEqualTo("Financial office holder");
        assertThat(response.getListOfCategory().get(0).getExternalReference()).isEqualTo("94");
        assertThat(response.getListOfCategory().get(0).getExternalReferenceType()).isEqualTo("FinancialRole");

        assertThat(response.getListOfCategory().get(1).getKey()).isEqualTo("PC1-01-84");
        assertThat(response.getListOfCategory().get(1).getValueEn()).isEqualTo("Judicial office holder");
        assertThat(response.getListOfCategory().get(1).getExternalReference()).isEqualTo("84");
        assertThat(response.getListOfCategory().get(1).getExternalReferenceType()).isEqualTo("JudicialRole");

        assertThat(response.getListOfCategory().get(2).getKey()).isEqualTo("PC1-01-74");
        assertThat(response.getListOfCategory().get(2).getValueEn()).isEqualTo("Medical office holder");
        assertThat(response.getListOfCategory().get(2).getExternalReference()).isEqualTo("74");
        assertThat(response.getListOfCategory().get(2).getExternalReferenceType()).isEqualTo("MedicalRole");
    }

    @Test
    @DisplayName("Retrieve categories without externalReference ")
    @Description("test to show that Json ignore did not pick up fields external_reference "
        + "and external_reference_type when they were empty")
    void shouldRetrieveCategoriesWithOutExternalReferenceStatusCode200()
        throws JsonProcessingException {
        final String response = (String)
            commonDataApiClient.retrieveCategoriesWithOutExternalReference("HearingChannel?serviceId=BBA3",
                                                             Categories.class, path);
        List<String> listofValues = Arrays.asList(response.split("},"));
        assertNotNull(listofValues);
        assertEquals(4, listofValues.size());
        assertThat(listofValues.get(0)).doesNotContain("external_reference");
        assertThat(listofValues.get(0)).doesNotContain("external_reference_type");
        assertThat(listofValues.get(1)).doesNotContain("external_reference");
        assertThat(listofValues.get(1)).doesNotContain("external_reference_type");
        assertThat(listofValues.get(2)).doesNotContain("external_reference");
        assertThat(listofValues.get(2)).doesNotContain("external_reference_type");
        assertThat(listofValues.get(3)).doesNotContain("external_reference");
        assertThat(listofValues.get(3)).doesNotContain("external_reference_type");

    }

    @Test
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

    @Test
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

    @ParameterizedTest
    @ValueSource(strings = {"Y","N"})
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

    @ParameterizedTest
    @ValueSource(strings = {"Y","N"})
    @DisplayName("Retrieve categories for Child with Parent Category")
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

    @ParameterizedTest
    @ValueSource(strings = {"Y","N"})
    @DisplayName("Retrieve categories for Child with Parent Key")
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

    @ParameterizedTest
    @ValueSource(strings = {"Y","N"})
    @DisplayName("Retrieve categories for Parent with serviceId")
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


    @ParameterizedTest
    @ValueSource(strings = {"Y","N"})
    @DisplayName("Retrieve categories for Child with serviceId")
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


    @Test
    @SuppressWarnings("unchecked")
    void shouldThrowStatusCode404ForInvalidCategoryId()
        throws JsonProcessingException {

        var errorResponseMap = commonDataApiClient.retrieveCaseFlagsByServiceId(
            "abc", ErrorResponse.class, path);
        assertNotNull(errorResponseMap);
        assertThat((Map<String, Object>) errorResponseMap).containsEntry("http_status", HttpStatus.NOT_FOUND);
    }

    @Test
    @SuppressWarnings("unchecked")
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

    @Test
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

    @Test
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
    @DisplayName("Retrieve categories for empty ServiceId ")
    @ParameterizedTest
    @ValueSource(strings = {" ", "XXXX", "", "null"})
    void retrieveCategoriesWithInvalidServiceIdWithStatusCode200(String serviceId)
        throws JsonProcessingException {
        final var response = (Categories)
            commonDataApiClient.retrieveCaseFlagsByServiceId("CaseLinkingReasonCode?serviceId=" + serviceId,
                                                             Categories.class, path
            );
        assertNotNull(response);
        assertEquals(3, response.getListOfCategory().size());
        assertThat(response.getListOfCategory().get(0).getKey()).isEqualTo("CLRC006");
        assertThat(response.getListOfCategory().get(0).getValueEn()).isEqualTo("Guardian");
        assertThat(response.getListOfCategory().get(0).getActiveFlag()).isEqualTo("Y");
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Retrieve categories for null ServiceId ")
    void retrieveCategoriesWithNullServiceIdWithStatusCode200()
        throws JsonProcessingException {
        final var response = (Categories)
            commonDataApiClient.retrieveCaseFlagsByServiceId("CaseLinkingReasonCode?serviceId=" + null,
                                                             Categories.class, path
            );
        assertNotNull(response);
        assertEquals(3, response.getListOfCategory().size());
        assertThat(response.getListOfCategory().get(0).getKey()).isEqualTo("CLRC006");
        assertThat(response.getListOfCategory().get(0).getValueEn()).isEqualTo("Guardian");
        assertThat(response.getListOfCategory().get(0).getActiveFlag()).isEqualTo("Y");
    }

    @Test
    @DisplayName("Retrieve categories for valid ServiceId ")
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

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Retrieve categories where Service Id provided does"
        + " not exist and no data exists with empty service ids for the category ")
    void retrieveCategoriesWithNotExistingServiceIdAndResultEmptyWithStatusCode200()
        throws JsonProcessingException {
        final var response = (Categories)
            commonDataApiClient.retrieveCaseFlagsByServiceId("HearingChannel?serviceId='XXX'",
                                                             Categories.class, path);
        assertNotNull(response);
        assertEquals(2, response.getListOfCategory().size());
        assertFalse(response.getListOfCategory().isEmpty());
    }


    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Retrieve categories where Service Id provided does"
        + " not exist and  data exists with empty service ids for the category and isChildRequired Y")
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

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Retrieve categories where Service Id provided does"
        + " not exist and data exists with empty service ids for the category and isChildRequired N")
    void retrieveCategoriesWithNotExistingServiceIdAndResultNotEmptyAndChildNotRequiredWithStatusCode200()
        throws JsonProcessingException {
        final var response = (Categories)
            commonDataApiClient.retrieveCaseFlagsByServiceId("CaseLinkingReasonCode?isChildRequired=N&serviceId='XXX'",
                                                             Categories.class, path);
        assertNotNull(response);
        assertEquals(3, response.getListOfCategory().size());
        assertThat(response.getListOfCategory().get(0).getKey()).isEqualTo("CLRC006");
        assertThat(response.getListOfCategory().get(0).getValueEn()).isEqualTo("Guardian");
        assertThat(response.getListOfCategory().get(0).getActiveFlag()).isEqualTo("Y");
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Retrieve categories where Service Id provided does"
        + " not exist and no data exists with empty service ids for the category and isChildRequired Y")
    void retrieveCategoriesWithNotExistingServiceIdAndResultEmptyAnChildRequiredWithStatusCode200()
        throws JsonProcessingException {
        final var response = (Categories)
            commonDataApiClient.retrieveCaseFlagsByServiceId("HearingChannel?isChildRequired=Y&serviceId='XXX'",
                                                             Categories.class, path);
        assertNotNull(response);
        assertEquals(2, response.getListOfCategory().size());
        assertTrue(!response.getListOfCategory().isEmpty());
    }

    @Test
    @DisplayName("Retrieve data when category id provided is null or blank or whitespace")
    @SuppressWarnings("unchecked")
    void shouldThrowErrorWhenRetrievingDataForNotEsistingCategory()
        throws JsonProcessingException {

        var errorResponseMap = commonDataApiClient.retrieveCaseFlagsByServiceId(
            "XXXXX?isChildRequired=Y&serviceId='XXX'", ErrorResponse.class, path);

        assertNotNull(errorResponseMap);
        assertThat((Map<String, Object>) errorResponseMap).containsEntry("http_status", HttpStatus.NOT_FOUND);
    }


    @Test
    @DisplayName("Retrieve data when category id provided is null ")
    @SuppressWarnings("unchecked")
    void shouldThrowErrorWhenRetrievingDataForNullCategory()
        throws JsonProcessingException {

        var errorResponseMap = commonDataApiClient.retrieveCaseFlagsByServiceId(
            null + "?isChildRequired=Y&serviceId='XXX'", ErrorResponse.class, path);

        assertNotNull(errorResponseMap);
        assertThat((Map<String, Object>) errorResponseMap).containsEntry("http_status", HttpStatus.NOT_FOUND);
    }



}
