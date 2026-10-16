package com.example.shipment.exception;

public class EventConfigurationException extends RuntimeException {

    public EventConfigurationException(String eventName) {
        super("Event dictionary entry not found: " + eventName);
    }
}