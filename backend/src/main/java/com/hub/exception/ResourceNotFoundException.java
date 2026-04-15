// backend/src/main/java/com/hub/exception/ResourceNotFoundException.java
package com.hub.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
