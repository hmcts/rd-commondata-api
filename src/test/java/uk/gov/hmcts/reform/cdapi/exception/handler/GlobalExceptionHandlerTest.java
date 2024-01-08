package uk.gov.hmcts.reform.cdapi.exception.handler;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.client.HttpStatusCodeException;
import uk.gov.hmcts.reform.cdapi.exception.CommonDataApiException;
import uk.gov.hmcts.reform.cdapi.exception.ErrorResponse;
import uk.gov.hmcts.reform.cdapi.exception.ForbiddenException;
import uk.gov.hmcts.reform.cdapi.exception.InvalidRequestException;
import uk.gov.hmcts.reform.cdapi.exception.ResourceNotFoundException;

import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;




@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    HttpMessageNotReadableException httpMessageNotReadableException;

    @Mock
    LinkedList<JsonMappingException.Reference> path = new LinkedList<>();

    @Test
    void test_handle_empty_result_exception() {
        EmptyResultDataAccessException emptyResultDataAccessException = new EmptyResultDataAccessException(1);

        ResponseEntity<Object> responseEntity
            = globalExceptionHandler.handleEmptyResultDataAccessException(emptyResultDataAccessException);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(
            emptyResultDataAccessException.getMessage(),
            ((ErrorResponse) responseEntity.getBody()).getErrorDescription()
        );
    }

    @Test
    void test_handle_resource_not_found_exception() {
        ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException("Resource not found");

        ResponseEntity<Object> responseEntity
            = globalExceptionHandler.handleResourceNotFoundException(resourceNotFoundException);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Resource not found", ((ErrorResponse) responseEntity.getBody()).getErrorDescription());

    }

    @Test
    void test_handle_illegal_argument_exception() {
        IllegalArgumentException exception = new IllegalArgumentException();

        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleIllegalArgumentException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(exception.getMessage(), ((ErrorResponse) responseEntity.getBody()).getErrorDescription());

    }


    @Test
    void test_handle_invalid_serialization_exception() {

        JsonMappingException jm = mock(JsonMappingException.class);
        JsonMappingException.Reference rf = mock(JsonMappingException.Reference.class);
        when(httpMessageNotReadableException.getCause()).thenReturn(jm);
        when(jm.getPath()).thenReturn(Collections.unmodifiableList(path));
        when(jm.getPath().get(0)).thenReturn(rf);
        when(jm.getPath().get(0).getFieldName()).thenReturn("field");
        ResponseEntity<Object> responseEntity = globalExceptionHandler
            .customSerializationError(httpMessageNotReadableException);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    }

    @Test
    void test_handle_forbidden_error_exception() {
        AccessDeniedException exception = new AccessDeniedException("Access Denied");

        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleForbiddenException(exception);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertEquals(exception.getMessage(), ((ErrorResponse) responseEntity.getBody()).getErrorDescription());

    }

    @Test
    void test_handle_http_status_code_exception() {
        HttpStatusCodeException exception = mock(HttpStatusCodeException.class);
        HttpStatusCode httpStatus = HttpStatusCode.valueOf(404);

        when(exception.getStatusCode()).thenReturn(httpStatus);

        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleHttpStatusException(exception);
        assertNotNull(responseEntity.getStatusCode());
        assertEquals(exception.getMessage(), ((ErrorResponse) responseEntity.getBody()).getErrorDescription());
        verify(exception, times(1)).getStatusCode();

    }

    @Test
    void test_handle_exception() {
        Exception exception = new Exception();

        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(exception.getMessage(), ((ErrorResponse) responseEntity.getBody()).getErrorDescription());

    }


    @Test
    void test_handle_invalid_request_exception() {
        InvalidRequestException invalidRequestException = new InvalidRequestException("Invalid Request");

        ResponseEntity<Object> responseEntity = globalExceptionHandler.customValidationError(invalidRequestException);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(invalidRequestException.getMessage(), ((ErrorResponse) responseEntity.getBody())
            .getErrorDescription());

    }

    @Test
    void test_handle_external_api_exception() {
        CommonDataApiException externalApiException = mock(CommonDataApiException.class);
        ResponseEntity<Object> responseEntity = globalExceptionHandler.getExceptionError(externalApiException);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(externalApiException.getMessage(), ((ErrorResponse) responseEntity.getBody())
            .getErrorDescription());

    }

    @Test
    void test_handle_launchDarkly_exception() {
        ForbiddenException forbiddenException = new ForbiddenException("LD Forbidden Exception");
        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleLaunchDarklyException(forbiddenException);
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertEquals(forbiddenException.getMessage(), ((ErrorResponse) responseEntity.getBody())
            .getErrorDescription());
    }
}
