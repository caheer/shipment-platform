package com.example.invoicing.service;

import com.example.invoicing.dto.InvoiceDetailedDto;
import com.example.invoicing.dto.InvoiceSearchItemDto;
import com.example.invoicing.repository.InvoiceRepository;
import com.example.invoicing.repository.InvoiceSearchByCriteriaRepository;
import com.example.invoicing.repository.InvoiceSearchCriteria;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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
}
