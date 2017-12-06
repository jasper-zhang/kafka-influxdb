package com.github.jasper.kafka;

/**
 * Indicates a Gauge that should be removed.
 */
public class InvalidGaugeException extends Exception {

    public InvalidGaugeException(String message, Throwable cause) {
        super(message, cause);
    }
}
