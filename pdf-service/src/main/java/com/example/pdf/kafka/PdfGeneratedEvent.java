// Put in pdf-service/src/main/java/com/example/pdf/kafka/
package com.example.pdf.kafka;
import java.util.UUID;
public record PdfGeneratedEvent(UUID jobId, UUID shipmentId, String filePath) {}
