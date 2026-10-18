package com.example.invoicing.service;

import com.example.AIGeneratedCode;
import com.example.invoicing.dto.InvoiceDetailedDto;
import com.example.invoicing.dto.InvoiceSearchItemDto;
import com.example.invoicing.model.Invoice;
import com.example.invoicing.repository.InvoiceRepository;
import com.example.invoicing.repository.InvoiceSearchByCriteriaRepository;
import com.example.invoicing.repository.InvoiceSearchCriteria;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    private final InvoiceSearchByCriteriaRepository invoiceSearchByCriteriaRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, InvoiceSearchByCriteriaRepository invoiceSearchByCriteriaRepository) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceSearchByCriteriaRepository = invoiceSearchByCriteriaRepository;
    }

    @Transactional
    public InvoiceDetailedDto findInvoiceDetailsById(UUID id) {
        return invoiceRepository.findInvoiceDetailsById(id);
    }

    @Transactional
    public List<InvoiceSearchItemDto> invoiceSearchByCriteria(InvoiceSearchCriteria invoiceSearchCriteria) {
        return invoiceSearchByCriteriaRepository.invoiceSearchByCriteria(invoiceSearchCriteria);
    }

    @AIGeneratedCode
    @Transactional
    public void updateRatePerKm(UUID invoiceId, BigDecimal ratePerKm) {
        Invoice invoice = findInvoice(invoiceId);
        validateAmount(ratePerKm);

        invoice.setRatePerKm(ratePerKm);
        recalculateTotal(invoice);
    }

    @AIGeneratedCode
    @Transactional
    public void updateTaxes(UUID invoiceId, BigDecimal taxes) {
        Invoice invoice = findInvoice(invoiceId);
        validateAmount(taxes);

        invoice.setTaxes(taxes);
        recalculateTotal(invoice);
    }

    @AIGeneratedCode
    @Transactional
    public void updateAdditionalCharges(UUID invoiceId, BigDecimal additionalCharges) {
        Invoice invoice = findInvoice(invoiceId);
        validateAmount(additionalCharges);

        invoice.setAdditionalCharges(additionalCharges);
        recalculateTotal(invoice);
    }

    @AIGeneratedCode
    private Invoice findInvoice(UUID invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Invoice not found: " + invoiceId));
    }

    @AIGeneratedCode
    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException(
                    "Amount must not be null or negative");
        }
    }

    @AIGeneratedCode
    private void recalculateTotal(Invoice invoice) {

        BigDecimal transportCharge = invoice.getRatePerKm()
                .multiply(BigDecimal.valueOf(invoice.getKilometersSpent()));

        BigDecimal total = transportCharge
                .add(invoice.getTaxes())
                .add(invoice.getAdditionalCharges());

        invoice.setTotalAmount(total);
    }
}
