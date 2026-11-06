package com.example.reports.kafka;

import com.example.reports.dto.ShipmentCreatedEvent;
import com.example.reports.dto.ShipmentStatusChangedEvent;
import com.example.reports.repository.ShipmentReportRepository;
import tools.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ShipmentReportListener {
    private final ObjectMapper objectMapper;
    private final ShipmentReportRepository repository;

    public ShipmentReportListener(ObjectMapper objectMapper, ShipmentReportRepository repository) {
        this.objectMapper = objectMapper;
        this.repository = repository;
    }

    // String messages avoid coupling consumers to producer Java package/type headers.
    @KafkaListener(topics = "shipment-created", groupId = "reports-service")
    public void shipmentCreated(String json) throws Exception {
        repository.onShipmentCreated(objectMapper.readValue(json, ShipmentCreatedEvent.class));
    }

    @KafkaListener(topics = "shipment-status-changed", groupId = "reports-service")
    public void shipmentStatusChanged(String json) throws Exception {
        repository.onStatusChanged(objectMapper.readValue(json, ShipmentStatusChangedEvent.class));
    }
}
