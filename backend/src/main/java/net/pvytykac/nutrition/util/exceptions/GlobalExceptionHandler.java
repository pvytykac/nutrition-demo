package net.pvytykac.nutrition.util.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
     * Handles validation errors from @Valid - maps to 400 BAD REQUEST.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Validation failed"
        );
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        
        return problemDetail;
    }
    
    /**
     * Handles ConstraintViolationException - maps to 409 CONFLICT.
     * This occurs when a unique constraint is violated (e.g., duplicate ingredient name).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            "A constraint violation occurred"
        );
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        return problemDetail;
    }

    /**
     * Handles DataIntegrityViolationException - maps to 409 CONFLICT.
     * This occurs when a database-level unique constraint is violated.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            "A data integrity constraint was violated. The resource may already exist."
        );
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        return problemDetail;
    }
}
