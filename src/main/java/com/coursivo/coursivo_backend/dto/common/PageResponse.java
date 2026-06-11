package com.coursivo.coursivo_backend.dto.common;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Generic paginated response wrapper returned by paginated endpoints.
 */
public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages, boolean hasNext,
		boolean hasPrevious) {
	public static <T> PageResponse<T> from(Page<T> page) {
		return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(),
				page.getTotalPages(), page.hasNext(), page.hasPrevious());
	}
}
