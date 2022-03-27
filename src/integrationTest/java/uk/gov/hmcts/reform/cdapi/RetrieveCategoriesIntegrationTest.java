package uk.gov.hmcts.reform.cdapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.cdapi.controllers.response.Categories;
import uk.gov.hmcts.reform.cdapi.controllers.response.Category;
import uk.gov.hmcts.reform.cdapi.exception.ErrorResponse;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@WithTags({@WithTag("testType:Integration")})
public class RetrieveCategoriesIntegrationTest extends CdAuthorizationEnabledIntegrationTest {

    private static final String path = "/lov/categories/{category-id}";

    @Test
    void shouldRetrieveCategoriesForCategoryIdWithStatusCode200()
        throws JsonProcessingException {
        final var response = (Categories)
            commonDataApiClient.retrieveCaseFlagsByServiceId("HearingSubChannel",
                                                             Categories.class, path
            );
        assertNotNull(response);
        assertEquals(9, response.getListOfCategory().size());
        responseVerification(response.getListOfCategory().get(0));
    }

    @Test
    void shouldRetrieveCategoriesWithAllParamsWithStatusCode200()
        throws JsonProcessingException {
        final var response = (Categories)
            commonDataApiClient.retrieveCaseFlagsByServiceId("HearingSubChannel?serviceId=BBA3"
                                                             + "&parentCategory=HearingChannel&parentKey=telephone",
                                                             Categories.class, path
            );
        assertNotNull(response);
        assertEquals(4, response.getListOfCategory().size());
        responseVerification(response);
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
}
