package com.example.invoicing.repository;

import com.example.invoicing.dto.InvoiceDetailedDto;
import com.example.invoicing.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    @Query("""
    SELECT i FROM Invoice i
    LEFT JOIN FETCH i.shipment
    LEFT JOIN FETCH i.customer
    WHERE i.invoiceId = :id
    """)
    Invoice findInvoiceByInvoiceId(@Param("id") UUID id);

    @Query("""
            select new com.example.invoicing.dto.InvoiceDetailedDto(
                                 i.invoiceNumber,
                                 i.shipment,
                                 i.customer,
                                 i.billingAddress,
                                 i.ratePerKm,
                                 i.kilometersSpent,
                                 i.taxes,
                                 i.additionalCharges,
                                 i.totalAmount,
                                 i.invoiceStatus,
                                 i.invoiceType,
                                 i.issuedAt,
                                 i.issuedBy) from Invoice i  LEFT JOIN i.shipment
                LEFT JOIN i.customer
               WHERE i.invoiceId = :id
            """)
    InvoiceDetailedDto findInvoiceDetailsById(UUID id);
}
