package com.coursivo.coursivo_backend.event;

import java.time.LocalDateTime;

public class EnrollmentEvent {

	private Long enrollmentId;
	private Long studentId;
	private String studentEmail;
	private String studentName;
	private Long courseId;
	private String courseTitle;
	private String instructorEmail;
	private String instructorName;
	private LocalDateTime enrolledAt;

	// Required by Jackson for deserialization
	public EnrollmentEvent() {
	}

	public EnrollmentEvent(Long enrollmentId, Long studentId, String studentEmail, String studentName, Long courseId,
			String courseTitle, String instructorEmail, String instructorName, LocalDateTime enrolledAt) {
		this.enrollmentId = enrollmentId;
		this.studentId = studentId;
		this.studentEmail = studentEmail;
		this.studentName = studentName;
		this.courseId = courseId;
		this.courseTitle = courseTitle;
		this.instructorEmail = instructorEmail;
		this.instructorName = instructorName;
		this.enrolledAt = enrolledAt;
	}

	public Long getEnrollmentId() {
		return enrollmentId;
	}

	public void setEnrollmentId(Long enrollmentId) {
		this.enrollmentId = enrollmentId;
	}

	public Long getStudentId() {
		return studentId;
	}

	public void setStudentId(Long studentId) {
		this.studentId = studentId;
	}

	public String getStudentEmail() {
		return studentEmail;
	}

	public void setStudentEmail(String studentEmail) {
		this.studentEmail = studentEmail;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public Long getCourseId() {
		return courseId;
	}

	public void setCourseId(Long courseId) {
		this.courseId = courseId;
	}

	public String getCourseTitle() {
		return courseTitle;
	}

	public void setCourseTitle(String courseTitle) {
		this.courseTitle = courseTitle;
	}

	public String getInstructorEmail() {
		return instructorEmail;
	}

	public void setInstructorEmail(String instructorEmail) {
		this.instructorEmail = instructorEmail;
	}

	public String getInstructorName() {
		return instructorName;
	}

	public void setInstructorName(String instructorName) {
		this.instructorName = instructorName;
	}

	public LocalDateTime getEnrolledAt() {
		return enrolledAt;
	}

	public void setEnrolledAt(LocalDateTime enrolledAt) {
		this.enrolledAt = enrolledAt;
	}

}
