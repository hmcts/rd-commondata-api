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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.cdapi.controllers.response.Categories;
import uk.gov.hmcts.reform.cdapi.controllers.response.Category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@WithTags({@WithTag("testType:Integration")})
public class RetrieveCategoriesIntegrationTest extends CdAuthorizationEnabledIntegrationTest {

    private static final String path = "/lov/categories/{category-id}";

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


}
