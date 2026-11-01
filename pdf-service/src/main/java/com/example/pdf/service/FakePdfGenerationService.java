package com.example.pdf.service;

import com.example.pdf.client.ShipmentClient;
import com.example.pdf.dto.ShipmentPdfDataDto;
import com.example.pdf.model.PdfGenerationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class FakePdfGenerationService {

    private final ShipmentClient shipmentClient;

    private static final Logger log =
            LoggerFactory.getLogger(FakePdfGenerationService.class);

    public FakePdfGenerationService(ShipmentClient shipmentClient) {
        this.shipmentClient = shipmentClient;
    }

    public PdfGenerationResult generateShipmentPdf(UUID shipmentId) {
        log.info("################################################################################################");

        log.info("Starting REST call to shipment-service");

        ShipmentPdfDataDto shipmentPdfDataDto = shipmentClient.getShipmentPdfDataByShipmentId(shipmentId);
        // 3. The worker is blocked until the HTTP response arrives or the request fails. If successful, it receives ShipmentPdfDataDto.
        /*If this throws an exception, the remainder of generateShipmentPdf() will not execute.
        If invoked through your Spring @Async method returning CompletableFuture<PdfGenerationResult>, the asynchronous computation will complete exceptionally.*/

        log.info("Finished REST call to shipment-service");
        log.info("Starting PDF generation for shipment " + shipmentId + " on thread " + Thread.currentThread().getName());

        try {
            int randomNumber = ThreadLocalRandom.current().nextInt(3000, 8001);
            Thread.sleep(randomNumber);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("PDF generation interrupted", e);
        }
        String pdfContent =
                """
                                             "############################PDF CONTENTS START ############################");" + "\\n" +
                                             "SHIPMENT PDF\\n" +
                                             "ID: " + shipmentPdfDataDto.shipmentId() + "\\n" +
                                             "Name: " + shipmentPdfDataDto.shipmentName() + "\\n" +
                                             "Status: " + shipmentPdfDataDto.status() + "\\n" +
                                             "Description: " + shipmentPdfDataDto.shipmentDescription() + "\\n" +
                                             ""############################ PDF CONTENTS END ############################")";
                        
                        """;

        log.info("Generated PDF Content: " + pdfContent);

        PdfGenerationResult pdfGenerationResult = new PdfGenerationResult(shipmentPdfDataDto.shipmentId(), pdfContent, LocalDateTime.now());
        //4. Worker generates the PDF
        //Your fake service simulates the generation using Thread.sleep() for 3–8 seconds. Once finished, it creates and returns PdfGenerationResult.
        log.info("PDF GENERATED");
        log.info("################################################################################################");
        return pdfGenerationResult;
    }
}