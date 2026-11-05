package com.example.shipment.client;

import com.example.shipment.dto.invoice.CreateInvoiceRequestDto;
import com.example.shipment.dto.invoice.InvoiceResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InvoiceRestClient {

    private final RestClient restClient;

    public InvoiceRestClient(RestClient.Builder builder,
                             @Value("${services.invoice.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public InvoiceResponseDto createInvoice(CreateInvoiceRequestDto request) {
        return restClient.post()
                .uri("/api/invoices/createInvoice")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(InvoiceResponseDto.class);
    }
}