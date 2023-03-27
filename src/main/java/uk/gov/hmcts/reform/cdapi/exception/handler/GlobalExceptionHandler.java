package uk.gov.hmcts.reform.cdapi.exception.handler;

import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import uk.gov.hmcts.reform.cdapi.controllers.constant.ErrorConstants;
import uk.gov.hmcts.reform.cdapi.exception.CommonDataApiException;
import uk.gov.hmcts.reform.cdapi.exception.ErrorResponse;
import uk.gov.hmcts.reform.cdapi.exception.ForbiddenException;
import uk.gov.hmcts.reform.cdapi.exception.InvalidRequestException;
import uk.gov.hmcts.reform.cdapi.exception.ResourceNotFoundException;

import java.nio.file.AccessDeniedException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.reform.cdapi.controllers.constant.ErrorConstants.INVALID_REQUEST_EXCEPTION;

@Slf4j
@ControllerAdvice(basePackages = "uk.gov.hmcts.reform.cdapi.controllers")
@RequestMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
public class GlobalExceptionHandler {

    @Value("${loggingComponentName}")
    private String loggingComponentName;

    private static final String HANDLING_EXCEPTION_TEMPLATE = "{}:: handling exception: {}";

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex) {
        return errorDetailsResponseEntity(ex, NOT_FOUND, ErrorConstants.EMPTY_RESULT_DATA_ACCESS.getErrorMessage());
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<Object> handleMissingPathVariableException(MissingPathVariableException ex) {
        return errorDetailsResponseEntity(ex, BAD_REQUEST, ErrorConstants.INVALID_REQUEST_EXCEPTION.getErrorMessage());
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return errorDetailsResponseEntity(ex, BAD_REQUEST, ErrorConstants.INVALID_REQUEST_EXCEPTION.getErrorMessage());
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<Object> handleHttpStatusException(HttpStatusCodeException ex) {
        HttpStatusCode httpStatus = ex.getStatusCode();
        return errorDetailsResponseEntity(ex, httpStatus, "");
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Object> customValidationError(InvalidRequestException ex) {
        return errorDetailsResponseEntity(ex, BAD_REQUEST, ErrorConstants.INVALID_REQUEST_EXCEPTION.getErrorMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> customSerializationError(
        HttpMessageNotReadableException ex) {

        String field = "";
        if (ex.getCause() != null) {
            JsonMappingException jme = (JsonMappingException) ex.getCause();
            field = jme.getPath().get(0).getFieldName();
        }
        var errorDetails = new ErrorResponse(BAD_REQUEST.value(), BAD_REQUEST.getReasonPhrase(),
                                             field + " in invalid format",
                                             INVALID_REQUEST_EXCEPTION.getErrorMessage(), getTimeStamp()
        );

        return new ResponseEntity<>(errorDetails, BAD_REQUEST);
    }

    @ExceptionHandler(CommonDataApiException.class)
    public ResponseEntity<Object> getExceptionError(CommonDataApiException ex) {
        return errorDetailsResponseEntity(ex, INTERNAL_SERVER_ERROR, ex.getErrorMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        return errorDetailsResponseEntity(
            ex,
            INTERNAL_SERVER_ERROR,
            ErrorConstants.UNKNOWN_EXCEPTION.getErrorMessage()
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return errorDetailsResponseEntity(ex, NOT_FOUND, ErrorConstants.EMPTY_RESULT_DATA_ACCESS.getErrorMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleForbiddenException(Exception ex) {
        return errorDetailsResponseEntity(ex, FORBIDDEN, ErrorConstants.ACCESS_EXCEPTION.getErrorMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleLaunchDarklyException(Exception ex) {
        return errorDetailsResponseEntity(ex, FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(Exception ex) {
        return errorDetailsResponseEntity(ex, BAD_REQUEST, ex.getMessage());
    }

    private String getTimeStamp() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS", Locale.ENGLISH).format(new Date());
    }

    private static Throwable getRootException(Throwable exception) {
        Throwable rootException = exception;
        while (rootException.getCause() != null) {
            rootException = rootException.getCause();
        }
        return rootException;
    }

    private ResponseEntity<Object> errorDetailsResponseEntity(Exception ex, HttpStatusCode httpStatus, String errorMsg) {

        log.info(HANDLING_EXCEPTION_TEMPLATE, loggingComponentName, ex.getMessage(), ex);
        ErrorResponse errorDetails = new ErrorResponse(
            httpStatus.value(),
            "",
            errorMsg,
            getRootException(ex).getLocalizedMessage(),
            getTimeStamp()
        );

        return new ResponseEntity<>(errorDetails, httpStatus);
    }

    private ResponseEntity<Object> errorDetailsResponseEntity(Exception ex, HttpStatus httpStatus, String errorMsg) {

        log.info(HANDLING_EXCEPTION_TEMPLATE, loggingComponentName, ex.getMessage(), ex);
        ErrorResponse errorDetails = new ErrorResponse(
            httpStatus.value(),
            httpStatus.getReasonPhrase(),
            errorMsg,
            getRootException(ex).getLocalizedMessage(),
            getTimeStamp()
        );

        return new ResponseEntity<>(errorDetails, httpStatus);
    }
}
