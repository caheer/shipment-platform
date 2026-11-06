package com.example.notification.dto;
import java.time.LocalDateTime;
import java.util.UUID;
public record ShipmentCreatedEvent(UUID shipmentId, String shipmentName, LocalDateTime createdAt) {}
