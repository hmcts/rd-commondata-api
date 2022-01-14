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
        ResponseEntity<?> result = crdApiController.retrieveHearingChannelByCategoryId(
            java.util.Optional.of("HearingChannel"), null, null, null);
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(crdService, times(1)).retrieveHearingChannelsByCategoryId(
            "HearingChannel",null,null,null);
    }

    @Test
    public void whenIdIsNull_thenExceptionIsThrown() {
        assertThrows(InvalidRequestException.class, () -> crdApiController.retrieveHearingChannelByCategoryId(
            Optional.empty(), null, null, null));
    }


    @Test
    void testWithAllValidParamValues_ShouldReturnStatusCode200() {
        ResponseEntity<?> result = crdApiController.retrieveHearingChannelByCategoryId(
            java.util.Optional.of("HearingChannel"), "BBA3", "1", "5");
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(crdService, times(1)).retrieveHearingChannelsByCategoryId(
            "HearingChannel","BBA3","1","5");
    }
}



