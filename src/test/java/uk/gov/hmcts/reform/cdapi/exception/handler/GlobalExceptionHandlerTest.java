package uk.gov.hmcts.reform.cdapi.exception.handler;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.hmcts.reform.cdapi.exception.CommonDataApiException;
import uk.gov.hmcts.reform.cdapi.exception.ErrorResponse;
import uk.gov.hmcts.reform.cdapi.exception.ForbiddenException;
import uk.gov.hmcts.reform.cdapi.exception.InvalidRequestException;
import uk.gov.hmcts.reform.cdapi.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS");

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private HttpMessageNotReadableException httpMessageNotReadableException;

    @Test
    void should_handle_empty_result_exception() {
        EmptyResultDataAccessException exception =
                new EmptyResultDataAccessException(1);

        ResponseEntity<Object> responseEntity =
                globalExceptionHandler.handleEmptyResultDataAccessException(exception);

        ErrorResponse errorResponse = getErrorResponse(responseEntity);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorResponse.getErrorDescription()).isEqualTo(exception.getMessage());
    }

    @Test
    void should_handle_resource_not_found_exception() {
        ResourceNotFoundException exception =
                new ResourceNotFoundException("Resource not found");

        ResponseEntity<Object> responseEntity =
                globalExceptionHandler.handleResourceNotFoundException(exception);

        ErrorResponse errorResponse = getErrorResponse(responseEntity);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorResponse.getErrorDescription()).isEqualTo("Resource not found");
    }

    @Test
    void should_handle_illegal_argument_exception() {
        IllegalArgumentException exception =
                new IllegalArgumentException("Invalid argument");

        ResponseEntity<Object> responseEntity =
                globalExceptionHandler.handleIllegalArgumentException(exception);

        ErrorResponse errorResponse = getErrorResponse(responseEntity);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorResponse.getErrorDescription()).isEqualTo("Invalid argument");
    }

    @Test
    void should_handle_invalid_serialization_exception() {
        JsonMappingException jsonMappingException =
                mock(JsonMappingException.class);

        JsonMappingException.Reference reference =
                mock(JsonMappingException.Reference.class);

        when(reference.getFieldName()).thenReturn("field");
        when(jsonMappingException.getPath()).thenReturn(List.of(reference));
        when(httpMessageNotReadableException.getCause()).thenReturn(jsonMappingException);

        ResponseEntity<Object> responseEntity =
                globalExceptionHandler.customSerializationError(
                        httpMessageNotReadableException
                );

        ErrorResponse errorResponse = getErrorResponse(responseEntity);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorResponse.getTimeStamp()).isNotBlank();

        assertThat(LocalDateTime.parse(
                errorResponse.getTimeStamp(),
                TIMESTAMP_FORMATTER
        )).isNotNull();
    }

    @Test
    void should_handle_forbidden_exception() {
        java.nio.file.AccessDeniedException exception =
                new java.nio.file.AccessDeniedException("Access Denied");

        ResponseEntity<Object> responseEntity =
                globalExceptionHandler.handleForbiddenException(exception);

        ErrorResponse errorResponse = getErrorResponse(responseEntity);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(errorResponse.getErrorDescription()).isEqualTo("Access Denied");
    }

    @Test
    void should_handle_http_status_code_exception() {
        HttpClientErrorException exception =
                new HttpClientErrorException(
                        HttpStatus.BAD_REQUEST,
                        "Bad Request"
                );

        ResponseEntity<Object> responseEntity =
                globalExceptionHandler.handleHttpStatusException(exception);

        ErrorResponse errorResponse = getErrorResponse(responseEntity);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorResponse.getErrorDescription()).isEqualTo(exception.getMessage());
    }

    @Test
    void should_handle_generic_exception() {
        Exception exception =
                new Exception("Unexpected error");

        ResponseEntity<Object> responseEntity =
                globalExceptionHandler.handleException(exception);

        ErrorResponse errorResponse = getErrorResponse(responseEntity);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(errorResponse.getErrorDescription()).isEqualTo("Unexpected error");
    }

    @Test
    void should_handle_invalid_request_exception() {
        InvalidRequestException exception =
                new InvalidRequestException("Invalid Request");

        ResponseEntity<Object> responseEntity =
                globalExceptionHandler.customValidationError(exception);

        ErrorResponse errorResponse = getErrorResponse(responseEntity);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorResponse.getErrorDescription()).isEqualTo("Invalid Request");
    }

    @Test
    void should_handle_external_api_exception() {
        CommonDataApiException exception = mock(CommonDataApiException.class);

        when(exception.getMessage()).thenReturn("External API failure");

        ResponseEntity<Object> responseEntity =
                globalExceptionHandler.getExceptionError(exception);

        ErrorResponse errorResponse = getErrorResponse(responseEntity);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void should_handle_launch_darkly_exception() {
        ForbiddenException exception =
                new ForbiddenException("LD Forbidden Exception");

        ResponseEntity<Object> responseEntity =
                globalExceptionHandler.handleLaunchDarklyException(exception);

        ErrorResponse errorResponse = getErrorResponse(responseEntity);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(errorResponse.getErrorDescription()).isEqualTo("LD Forbidden Exception");
    }

    private ErrorResponse getErrorResponse(ResponseEntity<Object> responseEntity) {
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getBody())
                .isNotNull()
                .isInstanceOf(ErrorResponse.class);

        return (ErrorResponse) responseEntity.getBody();
    }
}
