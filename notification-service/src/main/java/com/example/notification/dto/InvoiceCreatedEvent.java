package com.example.notification.dto;
import java.util.UUID;
public record InvoiceCreatedEvent(UUID invoiceId, UUID shipmentId, int revisionNumber) {}
