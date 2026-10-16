package com.example.playground.service;

import com.example.shipment.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

// ONLY FOR STREAM PRACTICE
@Service
public class ShipmentStreamingService {

    private final ShipmentRepository shipmentRepository;
    //Object lockA = new Object();

    public ShipmentStreamingService(ShipmentRepository shipmentRepository, EventRepository eventRepository, ShipmentEventRepository shipmentEventRepository, ShipmentSearchByCriteriaRepository shipmentSearchByCriteriaRepository, EntityManager entityManager) {
        this.shipmentRepository = shipmentRepository;
    }

    final Object lockA = new Object();


    public void doWorkA() {
        synchronized (lockA) {

        }
    }







    public void doWork() {
        System.out.println("DO SOME STUFF");

        synchronized (this) {
            // LOCK IS ON THE INSTANCE FOR THE WHOLE DURATION OF THIS BLOCK
        }
        System.out.println("DO SOME STUFF");
    }


    public synchronized void doWork2() {
        // LOCK IS ON THE INSTANCE FOR THE WHOLE DURATION OF THE METHOD?
        System.out.println("DO SOME STUFF");
    }






    @Transactional
    public void processShipmentsData(String textToFind) {

        Object lockA = new Object();

        synchronized (this) {
            System.out.println("DO SOMETHING");
        }

        synchronized (lockA) {
            System.out.println("DO SOMETHING");
        }

/*
        List<ShipmentComment> shipmentCommentList = shipmentRepository.findAllShipmentsWithComments()
                .stream()
                .flatMap(shipment -> shipment.getShipmentComments()
                        .stream())
                .filter(c -> c.getCommentText()
                        .contains(textToFind)).toList();
*/


/*        List<String> shipmentListDtos = shipmentRepository.findShipmentList(PageRequest.of(0, 10)).stream().map(a -> a.shipmentName()).toList();

        List<ShipmentListDto> shipmentListDtos1 = shipmentRepository.getShipmentsWithComments()
                .stream().map(shipment -> new ShipmentListDto(shipment.getShipmentId(),
                        shipment.getCustomer().getCustomerId(), shipment.getCustomer().getCustomerName(),
                        shipment.getShipmentName(), shipment.getShipmentStatus())).toList();


    }*/

/*    @Transactional
    public List<Shipment> getAllShipmentsWithDetails() {
        List<Shipment> shipments = shipmentRepository.findAllWithDetails();
        return shipments;
    }

    @Transactional
    public Shipment getShipmentByShipmentId(UUID shipmentId) {
        Optional<Shipment> shipment = shipmentRepository.findWithDetailsByShipmentId(shipmentId);

*//*        if(shipment.isPresent()){
            return shipment.get();
        } else {
            return null;
        }*//*

        return shipment.orElse(null);
    }*/


    }
}