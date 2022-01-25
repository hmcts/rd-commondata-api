package uk.gov.hmcts.reform.cdapi.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RootControllerTest {

    private final RootController rootController = new RootController();

    @Test
    void test_should_return_welcome_response() {

        ResponseEntity<String> responseEntity = rootController.welcome();
        String expectedMessage = "Welcome to RD Common Data API";

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertThat(responseEntity.getBody()).contains(expectedMessage);
    }
}
