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
import org.springframework.beans.factory.annotation.Value;
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
    private static final Logger log = LoggerFactory.getLogger(ShipmentPdfGenerationService.class);
    private final ShipmentClient shipmentClient;
    private final Path outputDirectory;

    public ShipmentPdfGenerationService(ShipmentClient shipmentClient,
            @Value("${app.pdf.output-directory:./generated-pdfs}") String outputDirectory) {
        this.shipmentClient = shipmentClient;
        this.outputDirectory = Path.of(outputDirectory).toAbsolutePath().normalize();
    }

    public PdfGenerationResult generateShipmentPdf(UUID shipmentId) {
        ShipmentPdfDataDto shipmentPdfDataDto = shipmentClient.getShipmentPdfDataByShipmentId(shipmentId);
        // 3. The worker is blocked until the HTTP response arrives or the request fails. If successful, it receives ShipmentPdfDataDto.
        /*If this throws an exception, the remainder of generateShipmentPdf() will not execute.
        If invoked through your Spring @Async method returning CompletableFuture<PdfGenerationResult>, the asynchronous computation will complete exceptionally.*/

        // Keep the 3–8 second delay from the original concurrency exercise.
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(3000, 8001));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("PDF generation interrupted", e);
        }

        try {
            byte[] pdfBytes = generateShipmentPdfBox(shipmentId);
            Files.createDirectories(outputDirectory);
            Path path = outputDirectory.resolve("shipment_" + shipmentId + "_" + UUID.randomUUID() + ".pdf");
            Files.write(path, pdfBytes);
            log.info("PDF generated: {}", path);
            //4. Worker generates the PDF
            //Your fake service simulates the generation using Thread.sleep() for 3–8 seconds. Once finished, it creates and returns PdfGenerationResult.
            // Existing record's pdfContent() field currently contains a file path.
            return new PdfGenerationResult(shipmentPdfDataDto.shipmentId(),
                    path.toString(), LocalDateTime.now());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save PDF for " + shipmentId, e);
        }
    }

    public byte[] generateShipmentPdfBox(UUID shipmentId) throws IOException {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 14);
                content.newLineAtOffset(50, 750);
                content.showText("Shipment report");
                content.endText();
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
                content.newLineAtOffset(50, 720);
                content.showText("Shipment ID: " + shipmentId);
                content.endText();
            }
            document.save(output);
            return output.toByteArray();
        }
    }
}
