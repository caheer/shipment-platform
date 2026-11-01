package com.example.pdf.client;

import com.example.pdf.dto.ShipmentPdfDataDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class ShipmentClient {

    private final RestClient restClient;

    public ShipmentClient(
            RestClient.Builder builder,
            @Value("${services.shipment.base-url}") String baseUrl) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    public ShipmentPdfDataDto getShipmentPdfDataByShipmentId(UUID shipmentId) {
        return restClient.get()
                .uri("/shipment/getShipmentPdfDataByShipmentId/{shipmentId}", shipmentId)
                .retrieve()
                .body(ShipmentPdfDataDto.class);
    }

    //the HTTP request is actually executed when you call the terminal operation .body(...), not when you call .get().

    //if ShipmentClient throws an exception, FakePdfGenerationService stops executing, and the exception propagates to AsyncPdfWorker.
}