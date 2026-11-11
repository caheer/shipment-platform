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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
        return createShipmentInvoice(shipmentId, 1);
    }

    public InvoiceResponseDto createShipmentInvoice(UUID shipmentId, int revisionNumber) {
        if (revisionNumber < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Revision number must be positive");
        }
        // This transaction commits BEFORE the remote HTTP call.
        CreateInvoiceRequestDto request = Objects.requireNonNull(
                transactionTemplate.execute(status -> prepareInvoice(shipmentId, revisionNumber)));

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

    private CreateInvoiceRequestDto prepareInvoice(UUID shipmentId, int revisionNumber) {
        // Serialize revision allocation for this shipment, including the first revision.
        // The lock is released before making the HTTP request.
        Shipment shipment = shipmentRepository.findShipmentForInvoiceCreation(shipmentId)
                .orElseThrow(() -> new ShipmentNotFoundException(shipmentId));

        Customer customer = Objects.requireNonNull(shipment.getCustomer(),
                "Shipment must have a customer before invoicing");

        ShipmentInvoiceRevision revision = revisionRepository
                .findByShipment_ShipmentIdAndRevisionNumber(shipmentId, revisionNumber)
                .orElseGet(() -> {
                    if (revisionNumber > 1) {
                        ShipmentInvoiceRevision previous = revisionRepository
                                .findByShipment_ShipmentIdAndRevisionNumber(shipmentId, revisionNumber - 1)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
                                        "Create the previous invoice revision first"));
                        if (previous.getCreationStatus() != InvoiceCreationStatus.COMPLETED) {
                            throw new ResponseStatusException(HttpStatus.CONFLICT,
                                    "Complete the previous invoice revision before creating another");
                        }
                    }
                    return revisionRepository.save(
                            new ShipmentInvoiceRevision(UUID.randomUUID(), shipment, revisionNumber));
                });

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
