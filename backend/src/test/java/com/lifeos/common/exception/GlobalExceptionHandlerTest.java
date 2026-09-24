package com.lifeos.common.exception;

import com.lifeos.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should format DataIntegrityViolationException as 409 Conflict when unique constraint fails")
    void shouldFormatDuplicateKeyAsConflict() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/users");
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Unique index or primary key violation: Duplicate entry 'test@example.com'");

        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrityViolation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        ErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(409);
        assertThat(body.getCode()).isEqualTo("CONFLICT");
        assertThat(body.getMessage()).isEqualTo("A resource with the specified unique attribute already exists.");
        assertThat(body.getPath()).isEqualTo("/api/v1/users");
        // Verify no raw SQL or exception message is leaked in the response message
        assertThat(body.getMessage()).doesNotContain("Duplicate entry");
        assertThat(body.getMessage()).doesNotContain("SQL");
    }

    @Test
    @DisplayName("Should format DataIntegrityViolationException as 400 Bad Request for non-duplicate violations")
    void shouldFormatGeneralIntegrityViolationAsBadRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/goals");
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Foreign key constraint fails");

        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrityViolation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(400);
        assertThat(body.getCode()).isEqualTo("DATA_INTEGRITY_VIOLATION");
        assertThat(body.getMessage()).isEqualTo("The requested operation violates a database constraint.");
    }

    @Test
    @DisplayName("Should format IllegalArgumentException as 400 Bad Request")
    void shouldFormatIllegalArgumentException() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/goals");
        IllegalArgumentException ex = new IllegalArgumentException("Invalid sort parameter");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(400);
        assertThat(body.getCode()).isEqualTo("BAD_REQUEST");
        assertThat(body.getMessage()).isEqualTo("Invalid sort parameter");
    }

    @Test
    @DisplayName("Should format ConstraintViolationException as 400 Bad Request with field errors")
    void shouldFormatConstraintViolationException() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/test");

        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("email");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must be a well-formed email address");

        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(400);
        assertThat(body.getCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(body.getFieldErrors()).containsEntry("email", "must be a well-formed email address");
    }
}
