package net.pvytykac.nutrition.util.exceptions;

import lombok.Getter;

/**
 * Exception thrown when a requested resource is not found.
 * Maps to HTTP 404 NOT FOUND.
 */
@Getter
public class ResourceNotFoundException extends ApplicationException {
    
    private final String resourceType;
    private final Object resourceId;
    
    public ResourceNotFoundException(String resourceType, Object id) {
        super(String.format("%s not found with id: %s", resourceType, id));
        this.resourceType = resourceType;
        this.resourceId = id;
    }
}
