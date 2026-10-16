package com.example.shipment.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShipmentEventProducer {

    private final KafkaTemplate<String, ShipmentStatusChangedEvent> kafkaTemplate;

    public ShipmentEventProducer(
            KafkaTemplate<String, ShipmentStatusChangedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(ShipmentStatusChangedEvent event) {
        kafkaTemplate.send(
                "shipment-status-changed",
                event.shipmentId().toString(),
                event
        );
    }
}