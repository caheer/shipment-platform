package com.example.shipment.service;

import com.example.shipment.client.InvoiceRestClient;
import com.example.shipment.dto.invoice.CreateInvoiceRequestDto;
import com.example.shipment.dto.invoice.InvoiceResponseDto;
import com.example.shipment.model.*;
import com.example.shipment.repository.ShipmentInvoiceRevisionRepository;
import com.example.shipment.repository.ShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ShipmentInvoiceServiceTest {
    private final ShipmentRepository shipments = mock(ShipmentRepository.class);
    private final ShipmentInvoiceRevisionRepository revisions = mock(ShipmentInvoiceRevisionRepository.class);
    private final InvoiceRestClient client = mock(InvoiceRestClient.class);
    private final TransactionTemplate transactions = mock(TransactionTemplate.class);
    private final ShipmentInvoiceService service = new ShipmentInvoiceService(shipments, revisions, client, transactions);
    private final UUID shipmentId = UUID.randomUUID();
    private final Shipment shipment = new Shipment(shipmentId);

    @BeforeEach
    void setup() {
        shipment.setCustomer(new Customer(UUID.randomUUID()));
        when(shipments.findShipmentForInvoiceCreation(shipmentId)).thenReturn(Optional.of(shipment));
        when(transactions.execute(any())).thenAnswer(invocation ->
                ((TransactionCallback<?>) invocation.getArgument(0)).doInTransaction(null));
        doAnswer(invocation -> {
            java.util.function.Consumer<org.springframework.transaction.TransactionStatus> callback = invocation.getArgument(0);
            callback.accept(null);
            return null;
        }).when(transactions).executeWithoutResult(any());
        when(revisions.save(any())).thenAnswer(invocation -> {
            ShipmentInvoiceRevision revision = invocation.getArgument(0);
            when(revisions.findById(revision.getShipmentInvoiceRevisionId())).thenReturn(Optional.of(revision));
            return revision;
        });
        when(client.createInvoice(any())).thenReturn(new InvoiceResponseDto(UUID.randomUUID()));
    }

    @Test
    void createsSecondRevisionAfterCompletedFirst() {
        ShipmentInvoiceRevision first = new ShipmentInvoiceRevision(UUID.randomUUID(), shipment, 1);
        first.setCreationStatus(InvoiceCreationStatus.COMPLETED);
        when(revisions.findByShipment_ShipmentIdAndRevisionNumber(shipmentId, 1)).thenReturn(Optional.of(first));

        service.createShipmentInvoice(shipmentId, 2);

        ArgumentCaptor<ShipmentInvoiceRevision> saved = ArgumentCaptor.forClass(ShipmentInvoiceRevision.class);
        verify(revisions).save(saved.capture());
        assertEquals(2, saved.getValue().getRevisionNumber());
        assertNotEquals(first.getShipmentInvoiceRevisionId(), saved.getValue().getShipmentInvoiceRevisionId());
        assertEquals(InvoiceCreationStatus.COMPLETED, saved.getValue().getCreationStatus());
    }

    @Test
    void retriesPendingRevisionWithSameIdAndMarksItCompleted() {
        ShipmentInvoiceRevision pending = new ShipmentInvoiceRevision(UUID.randomUUID(), shipment, 2);
        when(revisions.findByShipment_ShipmentIdAndRevisionNumber(shipmentId, 2)).thenReturn(Optional.of(pending));
        when(revisions.findById(pending.getShipmentInvoiceRevisionId())).thenReturn(Optional.of(pending));

        service.createShipmentInvoice(shipmentId, 2);

        ArgumentCaptor<CreateInvoiceRequestDto> request = ArgumentCaptor.forClass(CreateInvoiceRequestDto.class);
        verify(client).createInvoice(request.capture());
        assertEquals(pending.getShipmentInvoiceRevisionId(), request.getValue().shipmentInvoiceRevisionId());
        assertEquals(InvoiceCreationStatus.COMPLETED, pending.getCreationStatus());
        verify(revisions, never()).save(any());
    }

    @Test
    void rejectsSkippedRevision() {
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.createShipmentInvoice(shipmentId, 3));
        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        verifyNoInteractions(client);
        verify(revisions, never()).save(any());
    }

    @Test
    void rejectsNewRevisionWhilePreviousIsPending() {
        when(revisions.findByShipment_ShipmentIdAndRevisionNumber(shipmentId, 1))
                .thenReturn(Optional.of(new ShipmentInvoiceRevision(UUID.randomUUID(), shipment, 1)));
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.createShipmentInvoice(shipmentId, 2));
        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        verifyNoInteractions(client);
    }

    @Test
    void remoteFailureLeavesRevisionPending() {
        ShipmentInvoiceRevision pending = new ShipmentInvoiceRevision(UUID.randomUUID(), shipment, 2);
        when(revisions.findByShipment_ShipmentIdAndRevisionNumber(shipmentId, 2)).thenReturn(Optional.of(pending));
        when(client.createInvoice(any())).thenThrow(new IllegalStateException("Unavailable"));
        assertThrows(IllegalStateException.class, () -> service.createShipmentInvoice(shipmentId, 2));
        assertEquals(InvoiceCreationStatus.PENDING, pending.getCreationStatus());
        verify(transactions, never()).executeWithoutResult(any());
    }

    @Test
    void originalEndpointStillCreatesFirstRevision() {
        service.createShipmentInvoice(shipmentId);
        ArgumentCaptor<CreateInvoiceRequestDto> request = ArgumentCaptor.forClass(CreateInvoiceRequestDto.class);
        verify(client).createInvoice(request.capture());
        assertEquals(1, request.getValue().revisionNumber());
    }

    @Test
    void rejectsNonPositiveRevision() {
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.createShipmentInvoice(shipmentId, 0));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
        verifyNoInteractions(client);
    }
}
