package uk.gov.hmcts.reform.cdapi.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.cdapi.controllers.request.CategoryRequest;
import uk.gov.hmcts.reform.cdapi.controllers.response.Category;
import uk.gov.hmcts.reform.cdapi.exception.InvalidRequestException;
import uk.gov.hmcts.reform.cdapi.exception.ResourceNotFoundException;
import uk.gov.hmcts.reform.cdapi.exception.handler.GlobalExceptionHandler;
import uk.gov.hmcts.reform.cdapi.service.impl.CrdServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.reform.cdapi.helper.CrdTestSupport.buildCategoryRequest;

@WebMvcTest
@WithMockUser
@ContextConfiguration(classes = CrdApiController.class)
@ExtendWith(SpringExtension.class)
class CrdApiControllerTest {

    @MockBean
    CrdServiceImpl crdService;

    @SpyBean
    CrdApiController crdApiController;

    @SpyBean
    private GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testShouldReturn200WhenPassingRightCategoryId() throws Exception {

        List<Category> categoryList = List.of(Category.builder().categoryKey("HearingChannel")
                                                  .childNodes(Collections.emptyList()).build());
        //when
        given(crdService.retrieveListOfValuesByCategory(any(CategoryRequest.class))).willReturn(categoryList);

        //then
        mockMvc.perform(get("/refdata/commondata//lov/categories/{categoryId}", "HearingChannel")
                            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.list_of_values", hasSize(1)));

        verify(crdService).retrieveListOfValuesByCategory(any(CategoryRequest.class));

    }

    @Test
    void testShouldReturn404WhenPassingCategoryIdAsNull() throws Exception {

        //when
        willThrow(ResourceNotFoundException.class).given(crdService)
            .retrieveListOfValuesByCategory(any(CategoryRequest.class));

        //then
        mockMvc.perform(get("/refdata/commondata/lov/categories/{categoryId}", " ")
                            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode", is(404)))
            .andExpect(jsonPath("$.status", is("Not Found")))
            .andExpect(jsonPath("$.errorMessage", is("4 : Resource not found")));

        verify(crdService).retrieveListOfValuesByCategory(any(CategoryRequest.class));
    }

    @Test
    void testShouldReturn400WhenPassingCategoryIdOtherThanHearingChannel() throws Exception {

        CategoryRequest request = buildCategoryRequest("HearingChannel", null, null,
                                                       null, null, "N");
        //when
        willThrow(InvalidRequestException.class).given(crdApiController)
            .retrieveListOfValuesByCategoryId(Optional.empty(), request);

        //then
        mockMvc.perform(get("/refdata/commondata/lov/categories/{categoryId}", "")
                            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode", is(400)))
            .andExpect(jsonPath("$.status", is("Bad Request")))
            .andExpect(jsonPath(
                "$.errorMessage",
                is("3 : There is a problem with your request. Please check and try again")
            ));

    }

}
