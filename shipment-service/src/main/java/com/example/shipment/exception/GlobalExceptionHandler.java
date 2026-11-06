package com.example.shipment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            ShipmentNotFoundException.class,
            ShipmentCommentNotFoundException.class,
            CustomerNotFoundException.class,
            CourierNotFoundException.class,
            LocationNotFoundException.class
    })
    public ResponseEntity<String> handleNotFound(RuntimeException e) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(EventConfigurationException.class)
    public ResponseEntity<String> handleEventConfiguration(
            EventConfigurationException e) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
    }
}