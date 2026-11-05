package com.example.shipment.service;

import com.example.shipment.client.InvoiceRestClient;
import com.example.shipment.dto.invoice.CreateInvoiceRequestDto;
import com.example.shipment.dto.invoice.InvoiceResponseDto;
import com.example.shipment.exception.ShipmentNotFoundException;
import com.example.shipment.model.Customer;
import com.example.shipment.model.InvoiceCreationStatus;
import com.example.shipment.model.Shipment;
import com.example.shipment.model.ShipmentInvoiceRevision;
import com.example.shipment.repository.ShipmentInvoiceRevisionRepository;
import com.example.shipment.repository.ShipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;
import java.util.UUID;

@Service
public class ShipmentInvoiceService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentInvoiceRevisionRepository revisionRepository;
    private final InvoiceRestClient invoiceRestClient;
    private final TransactionTemplate transactionTemplate;

    public ShipmentInvoiceService(
            ShipmentRepository shipmentRepository,
            ShipmentInvoiceRevisionRepository revisionRepository,
            InvoiceRestClient invoiceRestClient,
            TransactionTemplate transactionTemplate) {
        this.shipmentRepository = shipmentRepository;
        this.revisionRepository = revisionRepository;
        this.invoiceRestClient = invoiceRestClient;
        this.transactionTemplate = transactionTemplate;
    }

    public InvoiceResponseDto createShipmentInvoice(UUID shipmentId) {
        // This transaction commits BEFORE the remote HTTP call.
        CreateInvoiceRequestDto request = Objects.requireNonNull(
                transactionTemplate.execute(status -> prepareInitialInvoice(shipmentId)));

        // If the call fails, the revision stays PENDING for a future attempt.
        // The invoice-service endpoint must be idempotent for the revision UUID.
        InvoiceResponseDto response = invoiceRestClient.createInvoice(request);
        if (response == null || response.invoiceId() == null) {
            throw new IllegalStateException("Invoice-service did not return an invoice ID");
        }

        // A separate short local transaction.
        transactionTemplate.executeWithoutResult(status -> {
            ShipmentInvoiceRevision revision = revisionRepository
                    .findById(request.shipmentInvoiceRevisionId())
                    .orElseThrow();
            revision.setCreationStatus(InvoiceCreationStatus.COMPLETED);
        });

        return response;
    }

    private CreateInvoiceRequestDto prepareInitialInvoice(UUID shipmentId) {
        Shipment shipment = shipmentRepository.findShipmentWithCustomerData(shipmentId)
                .orElseThrow(() -> new ShipmentNotFoundException(shipmentId));

        Customer customer = Objects.requireNonNull(shipment.getCustomer(),
                "Shipment must have a customer before invoicing");

        ShipmentInvoiceRevision revision = revisionRepository
                .findByShipment_ShipmentIdAndRevisionNumber(shipmentId, 1)
                .orElseGet(() -> revisionRepository.save(
                        new ShipmentInvoiceRevision(UUID.randomUUID(), shipment, 1)));

        return new CreateInvoiceRequestDto(
                shipment.getShipmentId(),
                shipment.getShipmentName(),
                shipment.getShipmentDescription(),
                shipment.getShippingType(),
                customer.getCustomerId(),
                customer.getCustomerName(),
                customer.getCustomerEmail(),
                revision.getShipmentInvoiceRevisionId(),
                revision.getRevisionNumber());
    }
}