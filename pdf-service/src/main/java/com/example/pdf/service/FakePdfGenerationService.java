package com.example.pdf.service;

import com.example.pdf.client.ShipmentClient;
import com.example.pdf.dto.ShipmentPdfDataDto;
import com.example.pdf.model.PdfGenerationResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class FakePdfGenerationService {

    private final ShipmentClient shipmentClient;

    public FakePdfGenerationService(ShipmentClient shipmentClient) {
        this.shipmentClient = shipmentClient;
    }

    @Transactional
    public PdfGenerationResult generateShipmentPdf(UUID shipmentId) {

        ShipmentPdfDataDto shipmentPdfDataDto = shipmentClient.getShipmentPdfDataByShipmentId(shipmentId);


        String pdfContent =
                "\"################ PDF START ################\");" +
                        "SHIPMENT PDF\n" +
                        "ID: " + shipmentPdfDataDto.shipmentId() + "\n" +
                        "Name: " + shipmentPdfDataDto.shipmentName() + "\n" +
                        "Status: " + shipmentPdfDataDto.status() + "\n" +
                        "Description: " + shipmentPdfDataDto.shipmentDescription() + "\n" +
                        "\"################ PDF END ################\")";

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

        PdfGenerationResult pdfGenerationResult = new PdfGenerationResult(shipmentPdfDataDto.shipmentId(),pdfContent, LocalDateTime.now());

        System.out.println("################ PDF GENERATED ################");
        System.out.println(pdfContent);
        System.out.println("################################################");
        return pdfGenerationResult;
    }
}