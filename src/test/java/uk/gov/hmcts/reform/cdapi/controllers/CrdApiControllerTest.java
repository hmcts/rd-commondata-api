package uk.gov.hmcts.reform.cdapi.controllers;

import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.cdapi.controllers.request.CategoryRequest;
import uk.gov.hmcts.reform.cdapi.controllers.response.Category;
import uk.gov.hmcts.reform.cdapi.exception.InvalidRequestException;
import uk.gov.hmcts.reform.cdapi.exception.ResourceNotFoundException;
import uk.gov.hmcts.reform.cdapi.exception.handler.GlobalExceptionHandler;
import uk.gov.hmcts.reform.cdapi.service.impl.CrdServiceImpl;

import java.util.List;
import java.util.stream.Stream;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@WithMockUser
@ContextConfiguration(classes = CrdApiController.class)
class CrdApiControllerTest {

    @MockBean
    CrdServiceImpl crdService;

    @SpyBean
    private GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Positive scenario - Should return 200 when passing correct categoryId and request")
    void should_return_200_with_all_positive_scenario_flags() throws Exception {

        // given
        String hearingChannel = "HearingChannel";
        String key = randomAlphabetic(5);
        String valueEn = randomAlphabetic(5);
        String valueCy = randomAlphabetic(5);
        String hintTextEn = randomAlphabetic(5);
        String hintTextCy = randomAlphabetic(5);
        long lovOrder = RandomUtils.nextLong();
        String parentCategory = randomAlphabetic(5);
        String parentKey = randomAlphabetic(5);
        String activeFlag = randomAlphabetic(5);


        List<Category> categoryList = getCategoryList(
            hearingChannel,
            key,
            valueEn,
            valueCy,
            hintTextEn,
            hintTextCy,
            lovOrder,
            parentCategory,
            parentKey,
            activeFlag
        );

        //when
        given(crdService.retrieveListOfValuesByCategory(any(CategoryRequest.class))).willReturn(categoryList);


        mockMvc.perform(get("/refdata/commondata//lov/categories/{categoryId}", "HearingChannel")
                            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())

            //then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.list_of_values", hasSize(1)))

            .andExpect(jsonPath("$.list_of_values[0].category_key", is(hearingChannel)))
            .andExpect(jsonPath("$.list_of_values[0].key", is(key)))
            .andExpect(jsonPath("$.list_of_values[0].value_en", is(valueEn)))
            .andExpect(jsonPath("$.list_of_values[0].value_cy", is(valueCy)))
            .andExpect(jsonPath("$.list_of_values[0].hint_text_en", is(hintTextEn)))
            .andExpect(jsonPath("$.list_of_values[0].hint_text_cy", is(hintTextCy)))
            .andExpect(jsonPath("$.list_of_values[0].lov_order", is(lovOrder)))
            .andExpect(jsonPath("$.list_of_values[0].parent_category", is(parentCategory)))
            .andExpect(jsonPath("$.list_of_values[0].parent_key", is(parentKey)))
            .andExpect(jsonPath("$.list_of_values[0].active_flag", is(activeFlag)))


            .andExpect(jsonPath("$.list_of_values[0].child_nodes[0].category_key", is(hearingChannel)))
            .andExpect(jsonPath("$.list_of_values[0].child_nodes[0].key", is(key)))
            .andExpect(jsonPath("$.list_of_values[0].child_nodes[0].value_en", is(valueEn)))
            .andExpect(jsonPath("$.list_of_values[0].child_nodes[0].value_cy", is(valueCy)))
            .andExpect(jsonPath("$.list_of_values[0].child_nodes[0].hint_text_en", is(hintTextEn)))
            .andExpect(jsonPath("$.list_of_values[0].child_nodes[0].hint_text_cy", is(hintTextCy)))
            .andExpect(jsonPath("$.list_of_values[0].child_nodes[0].lov_order", is(lovOrder)))
            .andExpect(jsonPath("$.list_of_values[0].child_nodes[0].parent_category", is(parentCategory)))
            .andExpect(jsonPath("$.list_of_values[0].child_nodes[0].parent_key", is(parentKey)))
            .andExpect(jsonPath("$.list_of_values[0].child_nodes[0].active_flag", is(activeFlag)));


        then(crdService).should().retrieveListOfValuesByCategory(any(CategoryRequest.class));

    }

    @NotNull
    private static List<Category> getCategoryList(String hearingChannel, String key, String valueEn, String valueCy,
                                                  String hintTextEn, String hintTextCy,
                                                  long lovOrder, String parentCategory,
                                                  String parentKey, String activeFlag) {


        Category childNodeCategory = new Category();
        childNodeCategory.setChildNodes(List.of(Category.builder().categoryKey(hearingChannel)
                                                    .key(key)
                                                    .valueEn(valueEn)
                                                    .valueCy(valueCy)
                                                    .hintTextEn(hintTextEn)
                                                    .lovOrder(lovOrder)
                                                    .parentCategory(parentCategory)
                                                    .parentKey(parentKey)
                                                    .activeFlag(activeFlag).hintTextCy(hintTextCy).build()));

        List<Category> categoryList = List.of(Category.builder().categoryKey(hearingChannel)
                                                  .key(key)
                                                  .valueEn(valueEn)
                                                  .valueCy(valueCy)
                                                  .hintTextEn(hintTextEn)
                                                  .hintTextCy(hintTextCy)
                                                  .lovOrder(lovOrder)
                                                  .parentCategory(parentCategory)
                                                  .parentKey(parentKey)
                                                  .activeFlag(activeFlag)
                                                  .childNodes(childNodeCategory.getChildNodes()).build());
        return categoryList;
    }

    @Test
    @DisplayName("Negative scenario - Should return 404 when categoryId is NULL")
    void should_return_404_for_serviceId_is_null() throws Exception {

        //when
        willThrow(ResourceNotFoundException.class).given(crdService)
            .retrieveListOfValuesByCategory(any(CategoryRequest.class));


        mockMvc.perform(get("/refdata/commondata/lov/categories/{categoryId}", " ")
                            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())

            //then
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode", is(404)))
            .andExpect(jsonPath("$.status", is("Not Found")))
            .andExpect(jsonPath("$.errorMessage", is("4 : Resource not found")));

        then(crdService).should().retrieveListOfValuesByCategory(any(CategoryRequest.class));
    }

    @ParameterizedTest
    @MethodSource("invalidScenarios")
    @DisplayName("Negative scenario - Should return 400 when passing categoryId other than HearingChannel ")
    void should_return_400_for_categoryid_other_than_hearingchannel(final String categoryId,
                                                                    final String errorDescription) throws Exception {

        willThrow(InvalidRequestException.class).given(crdService)
            .retrieveListOfValuesByCategory(any(CategoryRequest.class));

        //when
        mockMvc.perform(get("/refdata/commondata/lov/categories/{categoryId}", categoryId)
                            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())

            //then
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode", is(400)))
            .andExpect(jsonPath("$.status", is("Bad Request")))
            .andExpect(jsonPath(
                "$.errorMessage",
                is("3 : There is a problem with your request. Please check and try again")
            ))
            .andExpect(jsonPath("$.errorDescription",is(errorDescription)));

    }

    private static Stream<Arguments> invalidScenarios() {
        final String categoryIdErrorDesc = "Syntax error or Bad request";
        final String categoryIdotherThanHearingChannel = null;
        return Stream.of(
            arguments("", categoryIdErrorDesc),
            arguments(null, categoryIdErrorDesc),
            arguments("XXXX", categoryIdotherThanHearingChannel)
        );
    }

}
