package com.example.pdf;

import com.example.pdf.model.PdfGenerationResult;
import com.example.pdf.service.FakePdfGenerationService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncPdfWorker {

    private final FakePdfGenerationService fakePdfGenerationService;


    public AsyncPdfWorker(FakePdfGenerationService fakePdfGenerationService) {
        this.fakePdfGenerationService = fakePdfGenerationService;
    }

    @Async
    public CompletableFuture<PdfGenerationResult> generatePdfAsync(UUID shipmentId) {
        System.out.println("Worker: " + Thread.currentThread().getName());
        PdfGenerationResult pdfGenerationResult = fakePdfGenerationService.generateShipmentPdf(shipmentId);
        return CompletableFuture.completedFuture(pdfGenerationResult);
    }
}
