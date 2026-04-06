package com.coursivo.coursivo_backend.exception;

/**
 * Thrown when a requested resource cannot be found in the database. Maps to HTTP 404 via
 * {@link GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {

	public ResourceNotFoundException(String message) {
		super(message);
	}

	public ResourceNotFoundException(String resource, Long id) {
		super(resource + " with id " + id + " not found");
	}

}
