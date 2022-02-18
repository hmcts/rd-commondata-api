package uk.gov.hmcts.reform.cdapi.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.cdapi.controllers.request.CategoryRequest;
import uk.gov.hmcts.reform.cdapi.exception.InvalidRequestException;
import uk.gov.hmcts.reform.cdapi.service.CrdService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.hmcts.reform.cdapi.helper.CrdTestSupport.buildCategoryRequest;

class CrdApiControllerTest {

    @Mock
    CrdService crdService;

    @InjectMocks
    CrdApiController crdApiController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testWithValidCategoryId_ShouldReturnStatusCode200() {
        CategoryRequest request = buildCategoryRequest("HearingChannel", null, null,
                                                       null, null, "N");

        ResponseEntity<?> result = crdApiController.retrieveListOfValuesByCategoryId(
            java.util.Optional.of("HearingChannel"), request);
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
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
        CategoryRequest request = buildCategoryRequest("HearingChannel", "BBA3", "1",
                                                       "1", "5", "y");

        ResponseEntity<?> result = crdApiController.retrieveListOfValuesByCategoryId(
            java.util.Optional.of("HearingChannel"), request);
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());

        verify(crdService, times(1)).retrieveListOfValuesByCategory(request);
    }
}
