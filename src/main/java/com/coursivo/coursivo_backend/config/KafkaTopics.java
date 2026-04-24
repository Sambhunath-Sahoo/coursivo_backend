package com.coursivo.coursivo_backend.config;

public final class KafkaTopics {

    private KafkaTopics() {}

    public static final String MAIN = "course.enrollment.events";
    public static final String RETRY_30S = "course.enrollment.events.retry.30s";
    public static final String RETRY_5M = "course.enrollment.events.retry.5m";
    public static final String DLQ = "course.enrollment.events.dlq";
}
