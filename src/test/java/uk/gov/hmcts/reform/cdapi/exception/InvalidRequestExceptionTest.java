package uk.gov.hmcts.reform.cdapi.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InvalidRequestExceptionTest {

    @Test
    public void test_handle_invalid_request_exception() {
        InvalidRequestException invalidRequestException = new InvalidRequestException("Bad Request");
        assertNotNull(invalidRequestException);
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), invalidRequestException.getMessage());
    }
}
