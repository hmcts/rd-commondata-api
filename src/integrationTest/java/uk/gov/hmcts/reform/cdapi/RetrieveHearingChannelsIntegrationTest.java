package uk.gov.hmcts.reform.cdapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.cdapi.domain.HearingChannel;
import uk.gov.hmcts.reform.cdapi.domain.HearingChannels;
import uk.gov.hmcts.reform.cdapi.exception.ErrorResponse;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@WithTags({@WithTag("testType:Integration")})
public class RetrieveHearingChannelsIntegrationTest extends CdAuthorizationEnabledIntegrationTest {

    private static final String path = "/lov/categories/{category-id}";

    @Test
    void shouldRetrieveHearingChannel_For_CategoryId_WithStatusCode_200()
        throws JsonProcessingException {
        final var response = (HearingChannels)
            commonDataApiClient.retrieveCaseFlagsByServiceId("HearingSubChannel",
                                                             HearingChannels.class, path
            );
        assertNotNull(response);
        assertEquals(9, response.getHearingChannels().size());
        responseVerification(response.getHearingChannels().get(0));
    }

    @Test
    void shouldRetrieveHearingChannel_WithAllParams_WithStatusCode_200()
        throws JsonProcessingException {
        final var response = (HearingChannels)
            commonDataApiClient.retrieveCaseFlagsByServiceId("HearingSubChannel?service-id=BBA3"
                                                             + "&parent-category=HearingChannel&parent-key=telephone",
                                                             HearingChannels.class, path
            );
        assertNotNull(response);
        assertEquals(4, response.getHearingChannels().size());
        responseVerification(response);
    }


    @Test
    @SuppressWarnings("unchecked")
    void shouldThrowStatusCode_404_For_InvalidCategoryId()
        throws JsonProcessingException {

        var errorResponseMap = commonDataApiClient.retrieveCaseFlagsByServiceId(
            "abc", ErrorResponse.class, path);
        assertNotNull(errorResponseMap);
        assertThat((Map<String, Object>) errorResponseMap).containsEntry("http_status", HttpStatus.NOT_FOUND);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldThrowStatusCode_404_For_EmptyCategoryId()
        throws JsonProcessingException {
        var errorResponseMap = commonDataApiClient.retrieveCaseFlagsByServiceId(
            "",Map.class, path);
        assertNotNull(errorResponseMap);
        assertThat((Map<String, Object>) errorResponseMap).containsEntry("http_status", HttpStatus.NOT_FOUND);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldThrowStatusCode_404_For_NullCategoryId()
        throws JsonProcessingException {
        var errorResponseMap = commonDataApiClient.retrieveCaseFlagsByServiceId(
            null,Map.class, path);
        assertNotNull(errorResponseMap);
        assertThat((Map<String, Object>) errorResponseMap).containsEntry("http_status", HttpStatus.NOT_FOUND);
    }

    private void responseVerification(HearingChannels response) {
        for (HearingChannel hearingChannels : response.getHearingChannels()) {
            assertThat(hearingChannels.getParentCategory()).isEqualTo("HearingChannel");
            assertThat(hearingChannels.getParentKey()).isEqualTo("telephone");
            assertThat(hearingChannels.getActive()).isEqualTo(true);
        }
    }

    private void responseVerification(HearingChannel response) {
        assertThat(response.getKey()).isEqualTo("telephone-btMeetMe");
        assertThat(response.getValueEn()).isEqualTo("Telephone - BTMeetme");
        assertThat(response.getValueCy()).isNull();
        assertThat(response.getHintTextCy()).isNull();
        assertThat(response.getHintTextEn()).isNull();
        assertThat(response.getLovOrder()).isNull();
        assertThat(response.getParentCategory()).isEqualTo("HearingChannel");
        assertThat(response.getParentKey()).isEqualTo("telephone");
        assertThat(response.getActive()).isEqualTo(true);
    }

}
