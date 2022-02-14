package uk.gov.hmcts.reform.cdapi.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

class CommonDataApiExceptionTest {

    @Test
    void testCommonDataApiException() {
        CommonDataApiException externalApiException = new CommonDataApiException(BAD_REQUEST, "BAD REQUEST");
        assertNotNull(externalApiException);
        assertThat(externalApiException.getHttpStatus()).hasToString("400 BAD_REQUEST");
        assertEquals("BAD REQUEST", externalApiException.getErrorMessage());
    }
}
