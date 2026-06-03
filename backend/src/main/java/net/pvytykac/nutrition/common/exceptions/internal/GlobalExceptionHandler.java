package net.pvytykac.nutrition.common.exceptions.internal;

import jakarta.servlet.http.HttpServletRequest;
import net.pvytykac.nutrition.common.exceptions.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

/**
 * Global exception handler for all application exceptions.
 * Maps exceptions to appropriate HTTP status codes and ProblemDetail responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException - maps to 404 NOT FOUND.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.getMessage()
        );
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("resourceType", ex.getResourceType());
        problemDetail.setProperty("resourceId", ex.getResourceId());

        return problemDetail;
    }

    /**
     * Handles DataIntegrityViolationException - maps to 409 CONFLICT.
     * This occurs when a unique constraint is violated (e.g., duplicate ingredient name).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolationException(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            "A data integrity violation exception occurred"
        );
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        return problemDetail;
    }
}
