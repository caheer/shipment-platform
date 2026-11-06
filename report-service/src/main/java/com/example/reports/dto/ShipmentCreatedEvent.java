package com.example.reports.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ShipmentCreatedEvent(UUID shipmentId, String shipmentName,
                                   LocalDateTime createdAt) {}
