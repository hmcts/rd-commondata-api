package uk.gov.hmcts.reform.cdapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import net.thucydides.core.annotations.WithTag;
import net.thucydides.core.annotations.WithTags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.reform.cdapi.controllers.response.Categories;
import uk.gov.hmcts.reform.cdapi.util.FeatureToggleConditionExtension;
import uk.gov.hmcts.reform.cdapi.util.ToggleEnable;
import uk.gov.hmcts.reform.lib.util.serenity5.SerenityTest;

@SerenityTest
@SpringBootTest
@WithTags({@WithTag("testType:Functional")})
@ActiveProfiles("functional")
@Slf4j
class CommonDataApiFunctionalTestTemp extends AuthorizationFunctionalTest {

    private static final String MAP_KEY_LOV = "CrdApiController.retrieveListOfValuesByCategoryId";
    private static final String SLASH = "/";
    private static final String PATH_LOV = SLASH.concat("lov").concat(SLASH).concat("categories");

    @Test
    @ToggleEnable(mapKey = MAP_KEY_LOV, withFeature = true)
    @ExtendWith(FeatureToggleConditionExtension.class)
    void shouldReturnChildCategoriesInParticularToServiceId() {
        Response response =
            commonDataApiClient.retrieveCategoriesByCategoryIdSuccess(PATH_LOV, "/HearingChannel?"
                + "isChildRequired=y&serviceId=BBA3&key=VID");

        if (OK.value() == response.getStatusCode()) {
            var categories = response.getBody().as(Categories.class);
            assertNotNull(categories);
            categories.getListOfCategory().forEach(h -> {
                log.info("***************** h:: " + h.getCategoryKey() + h.getServiceId()
                             + h.getChildNodes().size() + h.getKey());
                assertEquals("HearingChannel", h.getCategoryKey());
            });
            //assertThat(categories.getListOfCategory().size()).isGreaterThan(0);
            //assertNotNull(categories.getListOfCategory().get(0).getChildNodes());
            //assertThat(categories.getListOfCategory().get(0).getChildNodes()).hasSizeGreaterThan(0);
            //assertEquals("HearingChannel", categories.getListOfCategory().get(0).getChildNodes().get(0)
            //.getParentCategory());

        } else {
            assertEquals(NOT_FOUND.value(), response.getStatusCode());
        }
    }



}
