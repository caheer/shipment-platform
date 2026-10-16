package com.example.shipment.service;

import com.example.shipment.model.Shipment;
import com.example.shipment.repository.ShipmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class FakePdfGenerationService {

    private final ShipmentRepository shipmentRepository;

    public FakePdfGenerationService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    @Transactional
    public void generateShipmentPdf(UUID shipmentId) {

        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow();

        String pdfContent =
                "SHIPMENT PDF\n" +
                        "ID: " + shipment.getShipmentId() + "\n" +
                        "Name: " + shipment.getShipmentName() + "\n" +
                        "Status: " + shipment.getShipmentStatus() + "\n" +
                        "Description: " + shipment.getShipmentDescription();

        System.out.println(
                "Starting PDF generation for shipment " + shipmentId +
                        " on thread " + Thread.currentThread().getName()
        );

        try {
            int randomNumber = ThreadLocalRandom.current().nextInt(3000, 8001);
            Thread.sleep(randomNumber);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("PDF generation interrupted", e);
        }

        System.out.println("################ PDF GENERATED ################");
        System.out.println(pdfContent);
        System.out.println("################################################");
    }
}