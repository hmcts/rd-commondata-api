package uk.gov.hmcts.reform.cdapi.controllers;

import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import uk.gov.hmcts.reform.cdapi.exception.ResourceNotFoundException;
import uk.gov.hmcts.reform.cdapi.exception.handler.GlobalExceptionHandler;
import uk.gov.hmcts.reform.cdapi.service.impl.CrdServiceImpl;

import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasSize;
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
    @DisplayName("Positive scenario -Should return 200 with list of categories for parent category without child nodes")
    void should_Return_200_With_Categories_For_CategoryId() throws Exception {

        final String categoryId = "HearingChannel";
        final boolean isChildRequired = false;
        final List<Category> categoryList = createCategoryList(categoryId, isChildRequired);

        // given
        given(crdService.retrieveListOfValuesByCategory(any(CategoryRequest.class))).willReturn(categoryList);

        //when
        final ResultActions resultActions =
            mockMvc.perform(get("/refdata/commondata/lov/categories/{categoryId}", categoryId)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        //then
        assertParentContent(resultActions, categoryList);
        final List<Category> childNodes = categoryList.get(0).getChildNodes();
        assertThat(childNodes).isNull();
        then(crdService).should().retrieveListOfValuesByCategory(any(CategoryRequest.class));
    }

    @Test
    @DisplayName("Positive scenario - Should return 200 with list of categories for parent category with child nodes")
    void should_Return_200_With_Categories_With_ChildNodes_For_Parent_CategoryId() throws Exception {

        final String parentCategoryId = "HearingChannel";
        final List<Category> categoryList = createCategoryList(parentCategoryId, true);

        // given
        given(crdService.retrieveListOfValuesByCategory(any(CategoryRequest.class))).willReturn(categoryList);

        //when
        final ResultActions resultActions =
            mockMvc.perform(get("/refdata/commondata/lov/categories/{categoryId}", parentCategoryId)
                                .queryParam("serviceId", randomAlphabetic(5))
                                .queryParam("key", randomAlphabetic(5))
                                .queryParam("isChildRequired", "Y")
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        //then
        assertParentContent(resultActions, categoryList);
        final List<Category> childNodes = categoryList.get(0).getChildNodes();
        assertThat(childNodes).isNotNull();
        assertChildContent(resultActions, categoryList);
        then(crdService).should().retrieveListOfValuesByCategory(any(CategoryRequest.class));
    }

    @Test
    @DisplayName("Positive scenario -Should return 200 with list of categories for parent category without child nodes")
    void should_Return_200_With_Categories_Without_ChildNodes_For_CategoryId() throws Exception {

        final String categoryId = "HearingChannel";
        final List<Category> categoryList = createCategoryList(categoryId, false);

        // given
        given(crdService.retrieveListOfValuesByCategory(any(CategoryRequest.class))).willReturn(categoryList);

        //when
        final ResultActions resultActions =
            mockMvc.perform(get("/refdata/commondata/lov/categories/{categoryId}", categoryId)
                                .queryParam("serviceId", randomAlphabetic(5))
                                .queryParam("key", randomAlphabetic(5))
                                .queryParam("isChildRequired", "N")
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        //then
        assertParentContent(resultActions, categoryList);
        final List<Category> childNodes = categoryList.get(0).getChildNodes();
        assertThat(childNodes).isNull();
        then(crdService).should().retrieveListOfValuesByCategory(any(CategoryRequest.class));
    }

    @Test
    @DisplayName("Positive scenario - Should return 200 with list of categories for sub-category without child nodes")
    void should_Return_200_With_Categories_And_Without_ChildNodes_For_SubCategoryId() throws Exception {

        final String subCategoryId = "SubHearingChannel";
        final List<Category> categoryList = createCategoryList(subCategoryId, false);

        // given
        given(crdService.retrieveListOfValuesByCategory(any(CategoryRequest.class))).willReturn(categoryList);

        //when
        final ResultActions resultActions =
            mockMvc.perform(get("/refdata/commondata/lov/categories/{categoryId}", subCategoryId)
                                .queryParam("serviceId", randomAlphabetic(5))
                                .queryParam("parentCategory", randomAlphabetic(5))
                                .queryParam("parentKey", randomAlphabetic(5))
                                .queryParam("key", randomAlphabetic(5))
                                .queryParam("isChildRequired", "N")
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        //then
        assertParentContent(resultActions, categoryList);
        final List<Category> childNodes = categoryList.get(0).getChildNodes();
        assertThat(childNodes).isNull();
        then(crdService).should().retrieveListOfValuesByCategory(any(CategoryRequest.class));
    }


    @Test
    @DisplayName("Negative scenario - Should return 404 when categories not found")
    void should_Return_404_When_Categories_Not_Found() throws Exception {

        //given
        willThrow(ResourceNotFoundException.class)
            .given(crdService).retrieveListOfValuesByCategory(any(CategoryRequest.class));

        //when
        mockMvc.perform(get("/refdata/commondata/lov/categories/{categoryId}", randomAlphabetic(5))
                            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())

            //then
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode", is(404)))
            .andExpect(jsonPath("$.status", is("Not Found")))
            .andExpect(jsonPath("$.errorMessage", is("4 : Resource not found")));

        then(crdService).should().retrieveListOfValuesByCategory(any(CategoryRequest.class));
    }

    @Test
    @DisplayName("Negative scenario - Should return 400 when categoryId not present")
    void should_Return_400_When_CategoryId_Is_Not_Present() throws Exception {

        //when
        mockMvc
            .perform(get("/refdata/commondata/lov/categories/{categoryId}", (Object) null)
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
            .andExpect(jsonPath("$.errorDescription", is("Syntax error or Bad request")));

        then(crdService).shouldHaveNoInteractions();
    }

    private static void assertParentContent(final ResultActions resultActions,
                                            final List<Category> categoryList)
        throws Exception {
        final Category parentCategoryListDetails = categoryList.get(0);

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
            .andExpect(jsonPath("$.list_of_values[0].active_flag", is(parentCategoryListDetails.getActiveFlag())));
    }

    private static void assertChildContent(ResultActions resultActions, List<Category> categoryList)
        throws Exception {
        final Category childCategoryListDetails = categoryList.get(0).getChildNodes().get(0);

        resultActions
            .andExpect(jsonPath("$.list_of_values[0].child_nodes", hasSize(1)))
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
    private static List<Category> createCategoryList(final String categoryId,
                                                     final boolean isChildRequired) {

        final String valueEn = randomAlphabetic(5);
        final String key = randomAlphabetic(5);
        final String valueCy = randomAlphabetic(5);
        final String hintTextEn = randomAlphabetic(5);
        final String hintTextCy = randomAlphabetic(5);
        final long lovOrder = RandomUtils.nextLong();
        final String parentKey = randomAlphabetic(5);
        final String activeFlag = randomAlphabetic(5);
        final String parentCategory = randomAlphabetic(5);

        final Category.CategoryBuilder categoryBuilder =
            Category
                .builder()
                .categoryKey(categoryId)
                .key(key)
                .valueEn(valueEn)
                .valueCy(valueCy)
                .hintTextEn(hintTextEn)
                .hintTextCy(hintTextCy)
                .lovOrder(lovOrder)
                .parentKey(parentKey)
                .activeFlag(activeFlag)
                .parentCategory(parentCategory);

        if (isChildRequired) {
            categoryBuilder.childNodes(List.of(categoryBuilder.categoryKey("Sub".concat(categoryId)).build()));
        }

        return List.of(categoryBuilder.build());
    }


}
