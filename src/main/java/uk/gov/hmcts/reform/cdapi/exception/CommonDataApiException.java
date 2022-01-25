package uk.gov.hmcts.reform.cdapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CommonDataApiException extends RuntimeException {

    private final HttpStatus httpStatus;

    private final String errorMessage;

    public CommonDataApiException(HttpStatus httpStatus, String errorMessage) {
        super(errorMessage);
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
