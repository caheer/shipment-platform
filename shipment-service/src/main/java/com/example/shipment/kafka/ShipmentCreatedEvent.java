// Place in shipment-service/src/main/java/com/example/shipment/kafka/
package com.example.shipment.kafka;

import java.time.LocalDateTime;
import java.util.UUID;

public record ShipmentCreatedEvent(UUID shipmentId, String shipmentName,
                                   LocalDateTime createdAt) {}
