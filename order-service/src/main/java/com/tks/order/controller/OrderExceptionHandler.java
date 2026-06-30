package com.tks.order.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class OrderExceptionHandler {

    private static final Logger log = LogManager.getLogger(OrderExceptionHandler.class);

    private final Counter badRequestCounter;

    public OrderExceptionHandler(MeterRegistry meterRegistry) {
        this.badRequestCounter = Counter.builder("orders.failed")
                .description("Total failed order requests")
                .tag("reason", "bad_request")
                .register(meterRegistry);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRequestBody(HttpMessageNotReadableException ex) {
        badRequestCounter.increment();
        log.warn("Order request failed because request body is invalid");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "timestamp", Instant.now().toString(),
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "error", "Bad Request",
                        "message", "Invalid order request body",
                        "path", "/orders"));
    }
}
