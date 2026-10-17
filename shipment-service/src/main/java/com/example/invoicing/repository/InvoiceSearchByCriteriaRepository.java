package com.example.invoicing.repository;

import com.example.invoicing.dto.InvoiceSearchItemDto;
import com.example.invoicing.model.Invoice;
import com.example.shipment.model.Shipment;
import com.example.shipment.repository.ShipmentSearchCriteria;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceSearchByCriteriaRepository {

    List<InvoiceSearchItemDto> invoiceSearchByCriteria(InvoiceSearchCriteria invoiceSearchCriteria);

}
