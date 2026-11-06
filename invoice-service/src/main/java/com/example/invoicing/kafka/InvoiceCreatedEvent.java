// Put in invoice-service/src/main/java/com/example/invoicing/kafka/
package com.example.invoicing.kafka;
import java.util.UUID;
public record InvoiceCreatedEvent(UUID invoiceId, UUID shipmentId, int revisionNumber) {}
