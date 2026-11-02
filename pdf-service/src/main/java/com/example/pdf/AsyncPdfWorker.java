package com.example.pdf;

import com.example.pdf.model.PdfGenerationResult;
import com.example.pdf.service.ShipmentPdfGenerationService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncPdfWorker {

    private final ShipmentPdfGenerationService shipmentPdfGenerationService;


    public AsyncPdfWorker(ShipmentPdfGenerationService shipmentPdfGenerationService) {
        this.shipmentPdfGenerationService = shipmentPdfGenerationService;
    }

    @Async("pdfExecutor")
    public CompletableFuture<PdfGenerationResult> generatePdfAsync(UUID shipmentId) {
    //1.The caller receives a CompletableFuture<PdfGenerationResult> representing the pending result, without waiting for the actual PDF generation.

        //2.Async worker thread starts executing the method generateShipmentPdf
        //It prints its thread name and calls fakePdfGenerationService.generateShipmentPdf(shipmentId)
        System.out.println("Worker: " + Thread.currentThread().getName());
        PdfGenerationResult pdfGenerationResult = shipmentPdfGenerationService.generateShipmentPdf(shipmentId);

        //5.Worker executes completedFuture(...)
        //return CompletableFuture.completedFuture(pdfGenerationResult);
        //This creates an already-completed CompletableFuture containing the result
        return CompletableFuture.completedFuture(pdfGenerationResult);

        //6.The caller's (that called generatePdfAsync) future completes
        //Spring's async infrastructure takes the result of the worker method and completes the future previously returned to the coordinator.
        //Any dependent callbacks such as thenApply() or thenAccept() can now execute
    }
}
