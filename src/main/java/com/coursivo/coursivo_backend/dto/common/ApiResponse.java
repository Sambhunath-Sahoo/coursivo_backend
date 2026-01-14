package com.coursivo.coursivo_backend.dto.common;

public record ApiResponse<T>(
        ApiMetaData metaData,
        T data
) {
    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<>(ApiMetaData.success(message), data);
    }
}

