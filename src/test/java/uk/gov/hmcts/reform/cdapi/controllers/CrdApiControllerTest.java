package uk.gov.hmcts.reform.cdapi.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.cdapi.exception.InvalidRequestException;
import uk.gov.hmcts.reform.cdapi.service.CrdService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
        ResponseEntity<?> result = crdApiController.retrieveListOfValuesByCategoryId(
            java.util.Optional.of("HearingChannel"), null, null, null,null,
            "N");
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(crdService, times(1)).retrieveListOfValuesByCategoryId(
            "HearingChannel",null,null,null,null,false);
    }

    @Test
    public void whenIdIsNull_thenExceptionIsThrown() {
        assertThrows(InvalidRequestException.class, () -> crdApiController.retrieveListOfValuesByCategoryId(
            Optional.empty(), null, null, null,null,"N"));
    }


    @Test
    void testWithAllValidParamValues_ShouldReturnStatusCode200() {
        ResponseEntity<?> result = crdApiController.retrieveListOfValuesByCategoryId(
            java.util.Optional.of("HearingChannel"), "BBA3", "1", "5","1",
            "y");
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(crdService, times(1)).retrieveListOfValuesByCategoryId(
            "HearingChannel","BBA3","1","5","1",true);
    }
}
