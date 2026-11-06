package com.example.notification.dto;
import java.time.LocalDateTime;
import java.util.UUID;
public record ShipmentStatusChangedEvent(UUID shipmentId, String newStatus, LocalDateTime changedAt) {}
