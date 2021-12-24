package uk.gov.hmcts.reform.cdapi.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ErrorResponseTest {

    @Test
    void test_ErrorResponse() {

        int code = 1;
        String status = "status";
        String expectMsg = "msg";

        ErrorResponse errorDetails = ErrorResponse.builder()
            .errorCode(code)
            .status(status)
            .errorDescription("desc")
            .errorMessage(expectMsg)
            .timeStamp("time")
            .build();

        assertNotNull(errorDetails);
        assertEquals(code, errorDetails.getErrorCode());
        assertEquals(status, errorDetails.getStatus());
        assertEquals(expectMsg, errorDetails.getErrorMessage());

        String expectDesc = "desc";
        String expectTs = "time";
        assertEquals(expectTs, errorDetails.getTimeStamp());
        assertEquals(expectDesc, errorDetails.getErrorDescription());
    }

    @Test
    void test_NoArgsConstructor() {
        ErrorResponse errorResponse = new ErrorResponse();
        assertNotNull(errorResponse);
    }

    @Test
    void test_ErrorResponseWithConstructor() {
        ErrorResponse errorResponse = new ErrorResponse(1, "status", "msg",
                                                        "desc", "time"
        );

        int code = 1;
        String status = "status";

        assertNotNull(errorResponse);
        assertEquals(code, errorResponse.getErrorCode());
        assertEquals(status, errorResponse.getStatus());

        String expectMsg = "msg";
        String expectDesc = "desc";
        String expectTs = "time";
        assertEquals(expectMsg, errorResponse.getErrorMessage());
        assertEquals(expectTs, errorResponse.getTimeStamp());
        assertEquals(expectDesc, errorResponse.getErrorDescription());
    }
}
