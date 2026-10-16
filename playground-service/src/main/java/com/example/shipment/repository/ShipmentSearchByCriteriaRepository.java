package com.example.shipment.repository;

import com.example.shipment.model.Shipment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentSearchByCriteriaRepository {

    List<Shipment> searchShipmentByCriteria(ShipmentSearchCriteria shipmentSearchCriteria);
}
