package uk.gov.hmcts.reform.cdapi.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class ErrorInvalidRequestResponse {

    private String status;

    private String error;

    private String path;

    private String timestamp;

    public ErrorInvalidRequestResponse(String status, String error, String path, String timestamp) {
        this.status = status;
        this.error = error;
        this.path = path;
        this.timestamp = timestamp;
    }
}
