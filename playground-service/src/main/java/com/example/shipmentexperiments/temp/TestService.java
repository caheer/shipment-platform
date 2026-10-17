package com.example.shipmentexperiments.temp;

import com.example.shipment.model.Shipment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TestService {

    private final EntityManager entityManager;

    public TestService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public void doSomething() {
        Query query = entityManager.createNativeQuery(
                "SELECT * FROM shipments WHERE shipment_status = :status",
                Shipment.class
        );

        query.setParameter("status", "IN_TRANSIT");

        List<Shipment> shipmentList = query.getResultList();

        shipmentList.forEach(s ->
                System.out.println(
                        s.getShipmentName() + " | " + s.getShipmentStatus()
                )
        );
    }


}



