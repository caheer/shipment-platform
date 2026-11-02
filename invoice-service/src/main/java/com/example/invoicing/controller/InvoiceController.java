package com.example.invoicing.controller;

import com.example.AIGeneratedCode;
import com.example.invoicing.dto.InvoiceDetailedDto;
import com.example.invoicing.dto.InvoiceSearchItemDto;
import com.example.invoicing.repository.InvoiceSearchCriteria;
import com.example.invoicing.service.InvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@AIGeneratedCode
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceDetailedDto> getInvoice(
            @PathVariable UUID invoiceId) {

        return ResponseEntity.ok(
                invoiceService.findInvoiceDetailsById(invoiceId));
    }

    @PostMapping("/search")
    public ResponseEntity<List<InvoiceSearchItemDto>> searchInvoices(
            @RequestBody InvoiceSearchCriteria criteria) {

        return ResponseEntity.ok(
                invoiceService.invoiceSearchByCriteria(criteria));
    }

    @PatchMapping("/{invoiceId}/rate-per-km")
    public ResponseEntity<Void> updateRatePerKm(
            @PathVariable UUID invoiceId,
            @RequestBody BigDecimal ratePerKm) {

        invoiceService.updateRatePerKm(invoiceId, ratePerKm);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{invoiceId}/taxes")
    public ResponseEntity<Void> updateTaxes(
            @PathVariable UUID invoiceId,
            @RequestBody BigDecimal taxes) {

        invoiceService.updateTaxes(invoiceId, taxes);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{invoiceId}/additional-charges")
    public ResponseEntity<Void> updateAdditionalCharges(
            @PathVariable UUID invoiceId,
            @RequestBody BigDecimal additionalCharges) {

        invoiceService.updateAdditionalCharges(invoiceId, additionalCharges);
        return ResponseEntity.noContent().build();
    }
}