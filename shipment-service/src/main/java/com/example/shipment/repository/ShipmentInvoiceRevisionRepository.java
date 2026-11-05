package com.example.shipment.repository;

import com.example.shipment.model.ShipmentInvoiceRevision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ShipmentInvoiceRevisionRepository
        extends JpaRepository<ShipmentInvoiceRevision, UUID> {

    Optional<ShipmentInvoiceRevision> findByShipment_ShipmentIdAndRevisionNumber(
            UUID shipmentId, int revisionNumber);
}