package com.example.invoicing.repository;

import com.example.invoicing.dto.InvoiceSearchItemDto;
import com.example.invoicing.model.Invoice;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class InvoiceSearchByCriteriaRepositoryImpl implements InvoiceSearchByCriteriaRepository {

    private final EntityManager entityManager;

    public InvoiceSearchByCriteriaRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    @Override
    public List<InvoiceSearchItemDto> invoiceSearchByCriteria(InvoiceSearchCriteria invoiceSearchCriteria) {

        // get CriteriaBuilder from EntityManager
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // create CriteriaQuery
        CriteriaQuery<Invoice> criteriaQuery = criteriaBuilder.createQuery(Invoice.class);

        // Root is basically equivalent of the table on which we are running select from
        Root<Invoice> invoiceRoot = criteriaQuery.from(Invoice.class);

        // lazy associations have to be fetched if we want to have them initialized on the returned objects
        invoiceRoot.fetch("shipment", JoinType.LEFT);
        invoiceRoot.fetch("customer", JoinType.LEFT);

        // create list for search criteria/conditions
        List<Predicate> predicates = new ArrayList<>();

        // criteriaBuilder has other conditions than equal if someone needs that
        if (invoiceSearchCriteria.getInvoiceNumber() != null) {
            predicates.add(criteriaBuilder.equal(invoiceRoot.get("invoiceNumber"), invoiceSearchCriteria.getInvoiceNumber()));
        }

        if (invoiceSearchCriteria.getShipmentName() != null) {
            predicates.add(criteriaBuilder.equal(invoiceRoot.get("shipment").get("shipmentName"), invoiceSearchCriteria.getShipmentName()));
        }

        if (invoiceSearchCriteria.getCustomerName() != null) {
            predicates.add(criteriaBuilder.equal(invoiceRoot.get("customer").get("customerName"), invoiceSearchCriteria.getCustomerName()));
        }

        if (invoiceSearchCriteria.getInvoiceStatus() != null) {
            predicates.add(criteriaBuilder.equal(invoiceRoot.get("invoiceStatus"), invoiceSearchCriteria.getInvoiceStatus()));
        }

        if (invoiceSearchCriteria.getInvoiceType() != null) {
            predicates.add(criteriaBuilder.equal(invoiceRoot.get("invoiceType"), invoiceSearchCriteria.getInvoiceType()));
        }

        // EFFECTIVELY CREATE Finalized criteriaQuery which is sort of like "SELECT * FROM SOMEWHERE WHERE A='asdad' AND ............"
        criteriaQuery.select(invoiceRoot).where(predicates.toArray(new Predicate[0]));

        // Run the query
        List<Invoice> invoiceList = entityManager.createQuery(criteriaQuery).getResultList();

        return invoiceList.stream()
                .map(invoice -> new InvoiceSearchItemDto(invoice.getInvoiceNumber(),
                        invoice.getShipment().getShipmentName(), invoice.getCustomer().getCustomerName(),
                        invoice.getInvoiceStatus(), invoice.getInvoiceType())).toList();


    }
}
