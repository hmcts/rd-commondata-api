package uk.gov.hmcts.reform.cdapi.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.cdapi.service.CrdService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CrdApiControllerTest {

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
            "HearingChannel", null, null, null);
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(crdService, times(1)).retrieveHearingChannelsByCategoryId(
            "HearingChannel",null,null,null);
    }

    @Test
    void testWithEmptyCategoryId() {
        ResponseEntity<?> result = crdApiController.retrieveHearingChannelByCategoryId(
            "", null, null, null);
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(crdService, times(1)).retrieveHearingChannelsByCategoryId(
            "",null, null, null);
    }


    @Test
    void testWithAllValidParamValues_ShouldReturnStatusCode200() {
        ResponseEntity<?> result = crdApiController.retrieveHearingChannelByCategoryId(
            "HearingChannel", "BBA3", "1", "5");
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(crdService, times(1)).retrieveHearingChannelsByCategoryId(
            "HearingChannel","BBA3","1","5");
    }
}



