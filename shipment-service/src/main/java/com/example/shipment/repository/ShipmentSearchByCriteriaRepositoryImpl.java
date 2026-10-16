package com.example.shipment.repository;

import com.example.shipment.model.Shipment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ShipmentSearchByCriteriaRepositoryImpl implements ShipmentSearchByCriteriaRepository {

    private final EntityManager entityManager;

    public ShipmentSearchByCriteriaRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    @Override
    public List<Shipment> searchShipmentByCriteria(ShipmentSearchCriteria shipmentSearchCriteria) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Shipment> cq = cb.createQuery(Shipment.class);

        Root<Shipment> shipmentRoot = cq.from(Shipment.class);

        List<Predicate> predicates = new ArrayList<>();

        if (shipmentSearchCriteria.getStatus() != null) {
            predicates.add(cb.equal(shipmentRoot.get("shipmentStatus"), shipmentSearchCriteria.getStatus()));
        }

        if (shipmentSearchCriteria.getCustomerId() != null) {
            predicates.add(cb.equal(shipmentRoot.get("customer").get("customerId"), shipmentSearchCriteria.getCustomerId()));
        }

        if (shipmentSearchCriteria.getCourierId() != null) {
            predicates.add(cb.equal(shipmentRoot.get("assignedCourier").get("courierId"), shipmentSearchCriteria.getCourierId()));
        }

        if (shipmentSearchCriteria.getWeightKg() != null) {
            predicates.add(cb.equal(shipmentRoot.get("weightKg"), shipmentSearchCriteria.getWeightKg()));
        }
        if (shipmentSearchCriteria.getDeclaredValue() != null) {
            predicates.add(cb.equal(shipmentRoot.get("declaredValue"), shipmentSearchCriteria.getDeclaredValue()));
        }

        if (shipmentSearchCriteria.getShipmentName() != null) {
            predicates.add(cb.equal(shipmentRoot.get("shipmentName"), shipmentSearchCriteria.getShipmentName()));
        }

        cq.select(shipmentRoot).where(predicates.toArray(new Predicate[0]));

        List<Shipment> shipments = entityManager.createQuery(cq).getResultList();

        return shipments;
    }
}
