package com.example.notification.dto;
import java.util.UUID;
public record PdfGeneratedEvent(UUID jobId, UUID shipmentId, String filePath) {}
