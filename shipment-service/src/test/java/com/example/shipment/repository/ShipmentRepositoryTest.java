package com.example.shipment.repository;

import com.example.shipment.model.*;
import com.example.shipment.repository.ShipmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ShipmentRepositoryTest {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Test
    void shouldSaveAndFindShipment() {

        // Arrange
        UUID shipmentId = UUID.randomUUID();

        Shipment shipment = new Shipment(shipmentId);
        shipment.setShipmentName("Laptop");
        shipment.setShipmentStatus(ShipmentStatus.CREATED);
        shipment.setShippingType(ShippingType.STANDARD);
        shipment.setShipmentDateCreated(LocalDateTime.now());

        // Act
        shipmentRepository.saveAndFlush(shipment);

        Shipment result = shipmentRepository.findById(shipmentId)
                .orElseThrow();

        // Assert
        assertEquals("Laptop", result.getShipmentName());
        assertEquals(ShipmentStatus.CREATED,
                result.getShipmentStatus());
    }
}