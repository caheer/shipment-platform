package com.example.report.client;

import com.example.report.dto.ShipmentDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class ShipmentClient {

    private final RestClient restClient;

    public ShipmentClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://localhost:8080")
                .build();
    }

    public ShipmentDto getShipment(UUID shipmentId) {
        return restClient.get()
                .uri("/shipment/getShipmentById/{shipmentId}", shipmentId)
                .retrieve()
                .body(ShipmentDto.class);
    }
}