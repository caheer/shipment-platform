package com.example.shipment.controller;

import com.example.shipment.pdf.PdfGenerationCoordinator;
import com.example.shipment.pdf.PdfJobRegistry;
import com.example.shipment.pdf.PdfJobStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/pdf")
public class PdfController {

    private final PdfGenerationCoordinator pdfGenerationCoordinator;
    private final PdfJobRegistry pdfJobRegistry;

    public PdfController(PdfGenerationCoordinator pdfGenerationCoordinator, PdfJobRegistry pdfJobRegistry) {
        this.pdfGenerationCoordinator = pdfGenerationCoordinator;
        this.pdfJobRegistry = pdfJobRegistry;
    }

    @GetMapping("/status/{jobId}")
    public PdfJobStatus getStatus(@PathVariable UUID jobId) {
        return pdfJobRegistry.getStatus(jobId);
    }

    @PostMapping("/generatePdf/{shipmentId}")
    public UUID generatePdf(@PathVariable UUID shipmentId) {

        return pdfGenerationCoordinator.startGeneration(shipmentId);

/*
        we don't really use manual Thread handling like this:
        Thread thread = new Thread(
                () -> fakePdfGenerationService.generateShipmentPdf(shipmentId)
        );

        thread.start();
*/


/*              Future is not very efficient - future.get blocks the thread by making it wait till the task is complete
                Future<?> future =
                pdfExecutor.submit(
                        () -> fakePdfGenerationService.generateShipmentPdf(shipmentId)
                );*/
    }
}
