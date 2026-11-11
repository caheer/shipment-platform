package com.example.shipment.controller;

import com.example.shipment.dto.invoice.InvoiceResponseDto;
import com.example.shipment.service.ShipmentInvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/shipments")
public class ShipmentInvoiceController {

    private final ShipmentInvoiceService shipmentInvoiceService;

    public ShipmentInvoiceController(ShipmentInvoiceService shipmentInvoiceService) {
        this.shipmentInvoiceService = shipmentInvoiceService;
    }

    @PostMapping("/{shipmentId}/invoice")
    public ResponseEntity<InvoiceResponseDto> createShipmentInvoice(@PathVariable UUID shipmentId) {
        return ResponseEntity.ok(shipmentInvoiceService.createShipmentInvoice(shipmentId));
    }

    @PostMapping("/{shipmentId}/invoice/revisions/{revisionNumber}")
    public ResponseEntity<InvoiceResponseDto> createShipmentInvoiceRevision(
            @PathVariable UUID shipmentId, @PathVariable int revisionNumber) {
        return ResponseEntity.ok(shipmentInvoiceService.createShipmentInvoice(shipmentId, revisionNumber));
    }
}
