package com.example.reports.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ShipmentReportDto(UUID shipmentId, String shipmentName,
                                String shipmentStatus, LocalDateTime createdAt,
                                LocalDateTime statusChangedAt) {}
