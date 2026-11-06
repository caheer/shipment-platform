// Replace existing shipment-service/src/main/java/com/example/shipment/kafka/ShipmentEventProducer.java
package com.example.shipment.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ShipmentEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ShipmentEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(ShipmentStatusChangedEvent event) {
        kafkaTemplate.send("shipment-status-changed", event.shipmentId().toString(), event);
    }

    public void publish(ShipmentCreatedEvent event) {
        kafkaTemplate.send("shipment-created", event.shipmentId().toString(), event);
    }
}
