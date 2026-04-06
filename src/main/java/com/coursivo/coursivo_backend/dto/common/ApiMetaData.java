package com.coursivo.coursivo_backend.dto.common;

import java.time.Instant;

public record ApiMetaData(boolean success, String message, Instant timestamp) {
	public static ApiMetaData success(String message) {
		return new ApiMetaData(true, message, Instant.now());
	}

	public static ApiMetaData failure(String message) {
		return new ApiMetaData(false, message, Instant.now());
	}
}
