package com.example.shipment.repository;

import com.example.shipment.model.ShipmentEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShipmentEventRepository extends JpaRepository<ShipmentEvent, UUID> {
}
