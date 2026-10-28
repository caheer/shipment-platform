package com.example.report.service;

import com.example.report.client.ShipmentClient;
import com.example.report.dto.ShipmentDto;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReportService {

    private final ShipmentClient shipmentClient;

    public ReportService(ShipmentClient shipmentClient) {
        this.shipmentClient = shipmentClient;
    }

    public String generate(UUID shipmentId) {
        ShipmentDto shipment = shipmentClient.getShipment(shipmentId);

        return """
                SHIPMENT REPORT
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