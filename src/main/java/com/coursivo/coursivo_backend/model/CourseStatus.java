package com.coursivo.coursivo_backend.model;

/**
 * Course publishing lifecycle.
 *
 * - DRAFT: visible/editable by instructor only (not listed to students) - PUBLISHED:
 * visible to students - ARCHIVED: hidden from listings; read-only
 */
public enum CourseStatus {

	DRAFT, PUBLISHED, ARCHIVED

}
