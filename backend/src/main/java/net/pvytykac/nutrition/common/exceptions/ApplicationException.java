package net.pvytykac.nutrition.common.exceptions;

/**
 * Base class for all application-specific exceptions.
 * Subclasses are mapped to appropriate HTTP status codes by GlobalExceptionHandler.
 */
public abstract class ApplicationException extends RuntimeException {
    
    protected ApplicationException(String message) {
        super(message);
    }
    
    protected ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
