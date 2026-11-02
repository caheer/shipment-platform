package com.example.invoicing.repository;

import com.example.invoicing.dto.InvoiceSearchItemDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceSearchByCriteriaRepository {

    List<InvoiceSearchItemDto> invoiceSearchByCriteria(InvoiceSearchCriteria invoiceSearchCriteria);

}
