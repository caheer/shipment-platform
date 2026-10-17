package com.example.pdf.service;

import com.example.pdf.client.ShipmentClient;
import com.example.pdf.dto.ShipmentDto;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PdfService {

    private final ShipmentClient shipmentClient;

    public PdfService(ShipmentClient shipmentClient) {
        this.shipmentClient = shipmentClient;
    }

    public String generate(UUID shipmentId) {
        ShipmentDto shipment = shipmentClient.getShipment(shipmentId);

        return """
                SHIPMENT PDF
                ID: %s
                Name: %s
                Status: %s
                Description: %s
                """.formatted(
                shipment.shipmentId(),
                shipment.shipmentName(),
                shipment.shipmentStatus(),
                shipment.shipmentDescription()
        );
    }
}