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
import org.springframework.test.web.servlet.ResultActions;
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
import static org.hamcrest.CoreMatchers.nullValue;
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
import static uk.gov.hmcts.reform.cdapi.helper.CrdTestSupport.buildCategoryRequest;

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
    @DisplayName("Positive scenario - Should return 200 for valid categoryId and request with isChildRequired :y")
    void should_return_200_with_categories_for_valid_categoryId_and_request() throws Exception {

        String hearingChannel = "HearingChannel";
        CategoryRequest request = buildCategoryRequest("HearingChannel", "BBA3", "1",
                                                       "1", "5", "y"
        );

        List<Category> categoryList = getCategoryList(hearingChannel,request);

        // given
        given(crdService.retrieveListOfValuesByCategory(any(CategoryRequest.class))).willReturn(categoryList);

        //when
        ResultActions resultActions = mockMvc.perform(get(
                "/refdata/commondata/lov/categories/{categoryId}",
                request.getCategoryId())
                                                          .queryParam("serviceId",request.getServiceId())
                                                          .queryParam("key",request.getKey())
                                                          .queryParam("parentKey",request.getParentKey())
                                                          .queryParam("parentCategory",request.getParentCategory())
                                                          .queryParam("isChildRequired",request.getIsChildRequired())
                                                          .accept(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isOk());

        //then
        assertResponseContent(resultActions,categoryList);
        then(crdService).should().retrieveListOfValuesByCategory(any(CategoryRequest.class));

    }

    @Test
    @DisplayName("Positive scenario - Should return 200 for valid categoryId and request with isChildRequired :n")
    void should_return_200_with_categories_for_valid_categoryId_categoryRequest() throws Exception {

        String hearingChannel = "HearingChannel";
        CategoryRequest request = buildCategoryRequest("HearingChannel", "BBA3", "1",
                                                       "1", "5", "n"
        );
        List<Category> categoryList = getCategoryList(hearingChannel,request);
        // given
        given(crdService.retrieveListOfValuesByCategory(any(CategoryRequest.class))).willReturn(categoryList);

        //when
        ResultActions resultActions = mockMvc.perform(get(
                "/refdata/commondata/lov/categories/{categoryId}",
                request.getCategoryId()
            ).queryParam("serviceId",request.getServiceId())
                                                          .queryParam("key",request.getKey())
                                                          .queryParam("parentKey",request.getParentKey())
                                                          .queryParam("parentCategory",request.getParentCategory())
                                                          .queryParam("isChildRequired",request.getIsChildRequired())
                                                          .accept(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isOk());

        //then
        assertResponseContent(resultActions, categoryList);
        then(crdService).should().retrieveListOfValuesByCategory(any(CategoryRequest.class));

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

    private static void assertResponseContent(ResultActions resultActions, List<Category> categoryList)
        throws Exception {
        final Category parentCategoryListDetails = categoryList.get(0);
        final Category childCategoryListDetails = categoryList.get(0).getChildNodes().get(0);

        resultActions
            .andExpect(jsonPath("$.list_of_values", hasSize(1)))

            .andExpect(jsonPath("$.list_of_values[0].category_key", is(parentCategoryListDetails.getCategoryKey())))
            .andExpect(jsonPath("$.list_of_values[0].key", is(parentCategoryListDetails.getKey())))
            .andExpect(jsonPath("$.list_of_values[0].value_en", is(parentCategoryListDetails.getValueEn())))
            .andExpect(jsonPath("$.list_of_values[0].value_cy", is(parentCategoryListDetails.getValueCy())))
            .andExpect(jsonPath("$.list_of_values[0].hint_text_en", is(parentCategoryListDetails.getHintTextEn())))
            .andExpect(jsonPath("$.list_of_values[0].hint_text_cy", is(parentCategoryListDetails.getHintTextCy())))
            .andExpect(jsonPath("$.list_of_values[0].lov_order", is(parentCategoryListDetails.getLovOrder())))
            .andExpect(jsonPath(
                "$.list_of_values[0].parent_category",
                is(parentCategoryListDetails.getParentCategory())
            ))
            .andExpect(jsonPath("$.list_of_values[0].parent_key", is(parentCategoryListDetails.getParentKey())))
            .andExpect(jsonPath("$.list_of_values[0].active_flag", is(parentCategoryListDetails.getActiveFlag())))


            .andExpect(jsonPath(
                "$.list_of_values[0].child_nodes[0].category_key",
                is(childCategoryListDetails.getCategoryKey())
            ))
            .andExpect(jsonPath("$.list_of_values[0].child_nodes[0].key", is(childCategoryListDetails.getKey())))
            .andExpect(jsonPath(
                "$.list_of_values[0].child_nodes[0].value_en",
                is(childCategoryListDetails.getValueEn())
            ))
            .andExpect(jsonPath(
                "$.list_of_values[0].child_nodes[0].value_cy",
                is(childCategoryListDetails.getValueCy())
            ))
            .andExpect(jsonPath(
                "$.list_of_values[0].child_nodes[0].hint_text_en",
                is(childCategoryListDetails.getHintTextEn())
            ))
            .andExpect(jsonPath(
                "$.list_of_values[0].child_nodes[0].hint_text_cy",
                is(childCategoryListDetails.getHintTextCy())
            ))
            .andExpect(jsonPath(
                "$.list_of_values[0].child_nodes[0].lov_order",
                is(childCategoryListDetails.getLovOrder())
            ))
            .andExpect(jsonPath(
                "$.list_of_values[0].child_nodes[0].parent_category",
                is(childCategoryListDetails.getParentCategory())
            ))
            .andExpect(jsonPath(
                "$.list_of_values[0].child_nodes[0].parent_key",
                is(childCategoryListDetails.getParentKey())
            ))
            .andExpect(jsonPath(
                "$.list_of_values[0].child_nodes[0].active_flag",
                is(childCategoryListDetails.getActiveFlag())
            ))
            .andExpect(jsonPath("$.list_of_values[0].child_nodes[0].child_nodes", is(nullValue())));
    }

    @NotNull
    private static List<Category> getCategoryList(String hearingChannel, CategoryRequest request) {

        String valueEn = randomAlphabetic(5);
        String key = randomAlphabetic(5);
        String valueCy = randomAlphabetic(5);
        String hintTextEn = randomAlphabetic(5);
        String hintTextCy = randomAlphabetic(5);
        long lovOrder = RandomUtils.nextLong();
        String parentKey = randomAlphabetic(5);
        String activeFlag = randomAlphabetic(5);
        String parentCategory = randomAlphabetic(5);


        if ("Y".equalsIgnoreCase(request.getIsChildRequired())) {
            System.out.println("Value of Key: " + request.getKey());
            key = request.getKey();

            parentCategory = request.getParentCategory();
        }


        Category childNodeCategory = new Category();
        childNodeCategory.setChildNodes(List.of(Category.builder().categoryKey(hearingChannel)
                                                    .key(key)
                                                    .valueEn(valueEn)
                                                    .valueCy(valueCy)
                                                    .hintTextEn(hintTextEn)
                                                    .lovOrder(lovOrder)
                                                    .parentKey(parentKey)
                                                    .parentCategory(parentCategory)
                                                    .activeFlag(activeFlag)
                                                    .hintTextCy(hintTextCy).build()));

        List<Category> categoryList = List.of(Category.builder().categoryKey(hearingChannel)
                                                  .key(key)
                                                  .valueEn(valueEn)
                                                  .valueCy(valueCy)
                                                  .hintTextEn(hintTextEn)
                                                  .hintTextCy(hintTextCy)
                                                  .lovOrder(lovOrder)
                                                  .parentKey(parentKey)
                                                  .activeFlag(activeFlag)
                                                  .parentCategory(parentCategory)
                                                  .childNodes(childNodeCategory.getChildNodes()).build());
        return categoryList;
    }


}
