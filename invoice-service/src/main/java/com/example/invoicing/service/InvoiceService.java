package com.example.invoicing.service;

import com.example.AIGeneratedCode;
import com.example.invoicing.dto.CreateInvoiceRequestDto;
import com.example.invoicing.dto.InvoiceDetailedDto;
import com.example.invoicing.dto.InvoiceResponseDto;
import com.example.invoicing.dto.InvoiceSearchItemDto;
import com.example.invoicing.kafka.InvoiceCreatedEvent;
import com.example.invoicing.kafka.InvoiceEventProducer;
import com.example.invoicing.model.Invoice;
import com.example.invoicing.pricing.PricingStrategyFactory;
import com.example.invoicing.pricing.ShippingPricingStrategy;
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

    private final PricingStrategyFactory pricingStrategyFactory;

    private final InvoiceEventProducer invoiceEventProducer;

    public InvoiceService(InvoiceRepository invoiceRepository, InvoiceSearchByCriteriaRepository invoiceSearchByCriteriaRepository, PricingStrategyFactory pricingStrategyFactory, InvoiceEventProducer invoiceEventProducer) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceSearchByCriteriaRepository = invoiceSearchByCriteriaRepository;
        this.pricingStrategyFactory = pricingStrategyFactory;
        this.invoiceEventProducer = invoiceEventProducer;
    }


    @Transactional
    public InvoiceResponseDto createInvoice(CreateInvoiceRequestDto request) {

        // If the invoice already exists, return its ID.
        // This handles repeated requests with the same revision ID.
        var existingInvoice = invoiceRepository
                .findByShipmentInvoiceRevisionId(
                        request.shipmentInvoiceRevisionId());

        if (existingInvoice.isPresent()) {
            return new InvoiceResponseDto(
                    existingInvoice.get().getInvoiceId()
            );
        }

        // Create a new invoice.
        Invoice invoice = new Invoice(UUID.randomUUID());

        invoice.setShipmentInvoiceRevisionId(
                request.shipmentInvoiceRevisionId()
        );

        invoice.setRevisionNumber(request.revisionNumber());
        invoice.setShipmentId(request.shipmentId());
        invoice.setShipmentName(request.shipmentName());
        invoice.setShippingType(request.shippingType());

        invoice.setCustomerId(request.customerId());
        invoice.setCustomerName(request.customerName());
        invoice.setCustomerEmail(request.customerEmail());

        // Initial values
        invoice.setRatePerKm(BigDecimal.ZERO);
        invoice.setKilometersSpent(0);
        invoice.setTaxes(BigDecimal.ZERO);
        invoice.setAdditionalCharges(BigDecimal.ZERO);
        invoice.setTotalAmount(BigDecimal.ZERO);

        Invoice newInvoice = invoiceRepository.save(invoice);

        invoiceEventProducer.publish(new InvoiceCreatedEvent(newInvoice.getInvoiceId(), newInvoice.getShipmentId(), newInvoice.getRevisionNumber()));

        return new InvoiceResponseDto(newInvoice.getInvoiceId());
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

        ShippingPricingStrategy strategy =
                pricingStrategyFactory.getStrategy(
                        invoice.getShippingType()
                );

        BigDecimal transportCharge = strategy.calculatePrice(
                invoice.getRatePerKm(),
                invoice.getKilometersSpent()
        );


        BigDecimal total = transportCharge
                .add(invoice.getTaxes())
                .add(invoice.getAdditionalCharges());

        invoice.setTotalAmount(total);
    }

}
