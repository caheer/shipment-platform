package com.example.notification.kafka;

import com.example.notification.dto.*;
import com.example.notification.repository.NotificationRepository;
import tools.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {
    private final ObjectMapper json;
    private final NotificationRepository repository;

    public NotificationEventListener(ObjectMapper json, NotificationRepository repository) {
        this.json = json;
        this.repository = repository;
    }

    // Consume JSON as String so the listener doesn't depend on producer package names.
    @KafkaListener(topics = "shipment-created")
    public void shipmentCreated(String payload) throws Exception {
        var event = json.readValue(payload, ShipmentCreatedEvent.class);
        repository.saveIfAbsent("shipment-created:" + event.shipmentId(),
                "SHIPMENT_CREATED", event.shipmentId(),
                "Shipment created: " + event.shipmentName());
    }

    @KafkaListener(topics = "shipment-status-changed")
    public void statusChanged(String payload) throws Exception {
        var event = json.readValue(payload, ShipmentStatusChangedEvent.class);
        // changedAt distinguishes separate status changes for the same shipment.
        repository.saveIfAbsent("shipment-status:" + event.shipmentId() + ":" + event.changedAt(),
                "SHIPMENT_STATUS_CHANGED", event.shipmentId(),
                "Shipment status changed to " + event.newStatus());
    }

    @KafkaListener(topics = "invoice-created")
    public void invoiceCreated(String payload) throws Exception {
        var event = json.readValue(payload, InvoiceCreatedEvent.class);
        repository.saveIfAbsent("invoice-created:" + event.invoiceId(),
                "INVOICE_CREATED", event.shipmentId(),
                "Invoice created for shipment (revision " + event.revisionNumber() + ")");
    }

    @KafkaListener(topics = "pdf-generated")
    public void pdfGenerated(String payload) throws Exception {
        var event = json.readValue(payload, PdfGeneratedEvent.class);
        repository.saveIfAbsent("pdf-generated:" + event.jobId(),
                "PDF_GENERATED", event.shipmentId(),
                "PDF generated: " + event.filePath());
    }
}
