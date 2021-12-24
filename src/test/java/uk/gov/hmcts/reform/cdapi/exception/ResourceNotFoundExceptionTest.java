package uk.gov.hmcts.reform.cdapi.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ResourceNotFoundExceptionTest {

    @Test
    public void test_handle_resource_not_found_exception() {
        ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException("Resource not found");
        assertNotNull(resourceNotFoundException);
        assertEquals("Resource not found", resourceNotFoundException.getMessage());

    }
}
