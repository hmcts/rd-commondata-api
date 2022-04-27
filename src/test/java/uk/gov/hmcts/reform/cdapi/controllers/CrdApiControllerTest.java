package uk.gov.hmcts.reform.cdapi.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.cdapi.controllers.request.CategoryRequest;
import uk.gov.hmcts.reform.cdapi.controllers.response.Categories;
import uk.gov.hmcts.reform.cdapi.controllers.response.Category;
import uk.gov.hmcts.reform.cdapi.exception.InvalidRequestException;
import uk.gov.hmcts.reform.cdapi.service.impl.CrdServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.cdapi.helper.CrdTestSupport.buildCategoryRequest;

class CrdApiControllerTest {

    @Mock
    CrdServiceImpl crdService;

    @InjectMocks
    CrdApiController crdApiController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testWithValidCategoryId_ShouldReturnStatusCode200() {
        CategoryRequest request = buildCategoryRequest(null, null, null,
                                                       null, null, "N");
        List<Category> categoryList = List.of(Category.builder().categoryKey("HearingChannel")
                                                  .childNodes(Collections.emptyList()).build());

        when(crdService.retrieveListOfValuesByCategory(any(CategoryRequest.class))).thenReturn(categoryList);

        ResponseEntity<Categories> result = crdApiController.retrieveListOfValuesByCategoryId(
            java.util.Optional.of("HearingChannel"), request);
        assertNotNull(result.getBody());
        assertFalse(result.getBody().getListOfCategory().isEmpty());
        assertTrue(result.getBody().getListOfCategory().get(0).getChildNodes().isEmpty());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("HearingChannel", request.getCategoryId());
        verify(crdService, times(1)).retrieveListOfValuesByCategory(request);
    }

    @Test
     void whenIdIsNull_thenExceptionIsThrown() {
        CategoryRequest request = buildCategoryRequest("HearingChannel", null, null,
                                                       null, null, "N");

        Optional<String> empty = Optional.empty();
        assertThrows(InvalidRequestException.class, () -> crdApiController.retrieveListOfValuesByCategoryId(
            empty, request));

    }

    @Test
    void testWithAllValidParamValues_ShouldReturnStatusCode200() {
        CategoryRequest request = buildCategoryRequest(null, "BBA3", "1",
                                                       "1", "5", "y");
        when(crdService.retrieveListOfValuesByCategory(any(CategoryRequest.class))).thenReturn(Collections.emptyList());

        Optional<String> key = java.util.Optional.of("HearingChannel");
        ResponseEntity<Categories> result = crdApiController.retrieveListOfValuesByCategoryId(key, request);
        assertNotNull(result.getBody());
        assertEquals("[]", result.getBody().getListOfCategory().toString());
        assertTrue(result.getBody().getListOfCategory().isEmpty());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(request.getCategoryId(), key.get());
        verify(crdService, times(1)).retrieveListOfValuesByCategory(request);
    }

    @Test
    void testWithOnlyCategoryId() {
        CategoryRequest request = mock(CategoryRequest.class);
        Optional<String> key = java.util.Optional.of("HearingChannel");
        ResponseEntity<?> result = crdApiController.retrieveListOfValuesByCategoryId(key, request);
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(request, times(1)).setCategoryId(any());
        verify(crdService, times(1)).retrieveListOfValuesByCategory(request);
    }
}
