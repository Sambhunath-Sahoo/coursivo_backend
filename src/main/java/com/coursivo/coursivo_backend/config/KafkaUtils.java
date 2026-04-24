package com.coursivo.coursivo_backend.config;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;

public final class KafkaUtils {

    private static final String RETRY_HEADER = "retry_count";

    private KafkaUtils() {}

    public static int getRetryCount(ConsumerRecord<?, ?> record) {
        Header header = record.headers().lastHeader(RETRY_HEADER);
        if (header == null) return 0;
        return Integer.parseInt(new String(header.value()));
    }

    public static void incrementRetryCount(ConsumerRecord<?, ?> record) {
        int retryCount = getRetryCount(record);
        record.headers().remove(RETRY_HEADER);
        record.headers().add(RETRY_HEADER, String.valueOf(retryCount + 1).getBytes());
    }
}
