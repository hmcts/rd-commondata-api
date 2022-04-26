package uk.gov.hmcts.reform.cdapi.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.cdapi.domain.CaseFlag;
import uk.gov.hmcts.reform.cdapi.exception.InvalidRequestException;
import uk.gov.hmcts.reform.cdapi.service.impl.CaseFlagServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CaseFlagApiControllerTest {

    @InjectMocks
    CaseFlagApiController caseFlagApiController;

    @Mock
    CaseFlagServiceImpl caseFlagService;

    @Test
    void testGetCaseFlag_ByServiceId_Returns200() {
        ResponseEntity<CaseFlag> responseEntity =
            caseFlagApiController.retrieveCaseFlagsByServiceId("XXXX",
                                                               "PARTY", "N"
            );
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(caseFlagService, times(1))
            .retrieveCaseFlagByServiceId("XXXX", "PARTY", "N");
    }

    @Test
    void testGetCaseFlag_ByInvalidFlag_Returns400() {
        assertThrows(InvalidRequestException.class, () ->
            caseFlagApiController.retrieveCaseFlagsByServiceId(
                "XXXX", "Hello", ""));
    }

    @Test
    void testGetCaseFlag_WhenServiceIdIsEmpty_Returns400() {
        assertThrows(InvalidRequestException.class, () ->
            caseFlagApiController.retrieveCaseFlagsByServiceId(
                "", "Hello", ""));
    }

    @Test
    void testGetCaseFlag_ByLowerCaseFlagType_Returns200() {
        ResponseEntity<CaseFlag> responseEntity =
            caseFlagApiController.retrieveCaseFlagsByServiceId("XXXX",
                                                               "case", "N"
            );
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(caseFlagService, times(1))
            .retrieveCaseFlagByServiceId("XXXX", "case", "N");
    }

    @Test
    void testGetCaseFlag_ByWelshRequiredIsNotYorN_Returns400() {
        assertThrows(InvalidRequestException.class, () ->
            caseFlagApiController.retrieveCaseFlagsByServiceId(
                "XXXX", "Hello", ""));
    }

    @Test
    void testGetCaseFlag_When_FlagType_WelshRequired_isnull() {
        ResponseEntity<CaseFlag> responseEntity = caseFlagApiController.retrieveCaseFlagsByServiceId(
            "XXXX", null, null);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(caseFlagService, times(1))
            .retrieveCaseFlagByServiceId("XXXX", null, null);
    }
}
