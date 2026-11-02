package com.example.pdf.service;

import com.example.pdf.client.ShipmentClient;
import com.example.pdf.dto.ShipmentPdfDataDto;
import com.example.pdf.model.PdfGenerationResult;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ShipmentPdfGenerationService {

    private final ShipmentClient shipmentClient;

    private static final Logger log =
            LoggerFactory.getLogger(ShipmentPdfGenerationService.class);

    public ShipmentPdfGenerationService(ShipmentClient shipmentClient) {
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
        String pdfContent;
        Path path;

        try {
            byte[] pdfBytes = generateShipmentPdfBox(shipmentId);
            path = Path.of("D:/ENV/IDEA_PROJECTS/shipment.pdf");
            Files.write(path, pdfBytes);

            pdfContent =
                    """
                                                 "############################PDF CONTENTS ############################");" + "\\n" +
                                                 "############################PDF CONTENTS ############################");" + "\\n" +
                                                 ""############################ PDF CONTENTS ############################")"+ "\\n" +;
                            
                            """ + "WRITTEN TO PATH: " + path;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Generated PDF Content:\n " + pdfContent);

        PdfGenerationResult pdfGenerationResult = new PdfGenerationResult(shipmentPdfDataDto.shipmentId(), pdfContent, LocalDateTime.now());
        //4. Worker generates the PDF
        //Your fake service simulates the generation using Thread.sleep() for 3–8 seconds. Once finished, it creates and returns PdfGenerationResult.
        log.info("PDF GENERATED");
        log.info("################################################################################################");
        return pdfGenerationResult;
    }

    public byte[] generateShipmentPdfBox(UUID shipmentId)
            throws IOException {

        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content =
                         new PDPageContentStream(document, page)) {
                content.setFont(
                        new PDType1Font(Standard14Fonts.FontName.HELVETICA),
                        14
                );
                content.beginText();
                content.setFont(
                        new PDType1Font(Standard14Fonts.FontName.HELVETICA),
                        14);
                content.newLineAtOffset(50, 750);
                content.showText("Shipment report");
                content.endText();

                content.beginText();
                content.setFont(
                        new PDType1Font(Standard14Fonts.FontName.HELVETICA),
                        11);
                content.newLineAtOffset(50, 720);
                content.showText("Shipment ID: " + shipmentId);
                content.endText();
            }
            document.save(output);

            log.info("PDF generated successfully. Size: {} bytes",
                    output.size());
            return output.toByteArray();
        }
    }
}

/*
String pdfContent =
        """
                                     "############################PDF CONTENTS START ############################");" + "\\n" +
                                     "SHIPMENT PDF\\n" +
                                     "ID: " + shipmentPdfDataDto.shipmentId() + "\\n" +
                                     "Name: " + shipmentPdfDataDto.shipmentName() + "\\n" +
                                     "Status: " + shipmentPdfDataDto.status() + "\\n" +
                                     "Description: " + shipmentPdfDataDto.shipmentDescription() + "\\n" +
                                     ""############################ PDF CONTENTS END ############################")";

                """;*/
