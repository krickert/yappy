package com.krickert.search.pipeline.engine.kafka.admin.exceptions;

public class KafkaAdminServiceException extends RuntimeException {
    public KafkaAdminServiceException(String message) {
        super(message);
    }

    public KafkaAdminServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}