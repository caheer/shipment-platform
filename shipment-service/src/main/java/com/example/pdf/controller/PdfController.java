package com.example.pdf.controller;

import com.example.pdf.PdfGenerationCoordinator;
import com.example.pdf.PdfJobRegistry;
import com.example.pdf.PdfJobStatus;
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

    @PostMapping("/generatePdfAsync/{shipmentId}")
    public UUID generatePdfAsync(@PathVariable UUID shipmentId) {
        return pdfGenerationCoordinator.startGenerationAsync(shipmentId);
    }

    @PostMapping("/generatePdf/{shipmentId}")
    public UUID generatePdf(@PathVariable UUID shipmentId) {

        return pdfGenerationCoordinator.startGeneration(shipmentId);
    }
}
