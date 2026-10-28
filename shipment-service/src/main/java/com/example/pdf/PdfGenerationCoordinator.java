package com.example.pdf;

import com.example.pdf.service.FakePdfGenerationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
public class PdfGenerationCoordinator {

    private final ExecutorService pdfExecutor;
    private final FakePdfGenerationService fakePdfGenerationService;
    private final PdfJobRegistry pdfJobRegistry;
    private final AsyncPdfWorker asyncPdfWorker;

    private static final Logger log =
            LoggerFactory.getLogger(PdfGenerationCoordinator.class);

    public PdfGenerationCoordinator(
            ExecutorService pdfExecutor,
            FakePdfGenerationService fakePdfGenerationService, PdfJobRegistry pdfJobRegistry, AsyncPdfWorker asyncPdfWorker) {
        this.pdfExecutor = pdfExecutor;
        this.fakePdfGenerationService = fakePdfGenerationService;
        this.pdfJobRegistry = pdfJobRegistry;
        this.asyncPdfWorker = asyncPdfWorker;
    }

    public UUID startGenerationAsync(UUID shipmentId) {

        UUID jobId = UUID.randomUUID();

        pdfJobRegistry.createJob(jobId);

        System.out.println("JOB ID: " + jobId);
        pdfJobRegistry.markRunning(jobId);
        CompletableFuture<String> future = asyncPdfWorker.generatePdfAsync(shipmentId);

        future.whenComplete((result, exception) -> {
            if (exception == null) {
                pdfJobRegistry.markCompleted(jobId);
            } else {
                System.out.println("FUTURE COMPLETED EXCEPTIONALLY: " + future.isCompletedExceptionally());
                pdfJobRegistry.markFailed(jobId);
                log.error("PDF generation failed for shipment {}", shipmentId, exception);
                exception.printStackTrace();
            }
        }).thenAccept(pdfContent -> {
            System.out.println(pdfContent);
        });

        return jobId;
    }

    public UUID startGenerationAsyncV1(UUID shipmentId) {

        UUID jobId = UUID.randomUUID();

        pdfJobRegistry.createJob(jobId);

        // whenComplete: completion callback that always runs, regardless of success or failurr
        // When this future is done, inspect whether it succeeded or failed and do some side effect

        CompletableFuture
                .supplyAsync(() -> {

                    pdfJobRegistry.markRunning(jobId);

                    String pdfContent = fakePdfGenerationService
                            .generateShipmentPdf(shipmentId);

                    return pdfContent;
                }, pdfExecutor)
                .whenComplete((result, exception) -> {

                    if (exception == null) {
                        pdfJobRegistry.markCompleted(jobId);
                        System.out.println("Generated PDF content: " + result);
                    } else {
                        pdfJobRegistry.markFailed(jobId);
                        exception.printStackTrace();
                    }

                });

        return jobId;
    }
}