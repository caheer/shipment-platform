package com.example.pdf.client;

import com.example.pdf.dto.ShipmentPdfDataDto;
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

    public ShipmentPdfDataDto getShipmentPdfDataByShipmentId(UUID shipmentId) {
        return restClient.get()
                .uri("/shipment/getShipmentPdfDataByShipmentId/{shipmentId}", shipmentId)
                .retrieve()
                .body(ShipmentPdfDataDto.class);
    }
}