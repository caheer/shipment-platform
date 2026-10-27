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
        CompletableFuture<Void> future =
                asyncPdfWorker.generatePdfAsync(shipmentId);

        future.whenComplete((result, exception) -> {
            if (exception == null) {
                pdfJobRegistry.markCompleted(jobId);
            } else {
                System.out.println("FUTURE COMPLETED EXCEPTIONALLY: " + future.isCompletedExceptionally());
                pdfJobRegistry.markFailed(jobId);
                log.error("PDF generation failed for shipment {}", shipmentId, exception);
                exception.printStackTrace();
            }
        });

        return jobId;
    }

    public UUID startGeneration(UUID shipmentId) {

        UUID jobId = UUID.randomUUID();

        pdfJobRegistry.createJob(jobId);

        // whenComplete: completion callback that always runs, regardless of success or failurr
        // When this future is done, inspect whether it succeeded or failed and do some side effect

        CompletableFuture
                .runAsync(() -> {

                    pdfJobRegistry.markRunning(jobId);

                    fakePdfGenerationService
                            .generateShipmentPdf(shipmentId);

                }, pdfExecutor)
                .whenComplete((result, exception) -> {

                    if (exception == null) {
                        pdfJobRegistry.markCompleted(jobId);
                    } else {
                        pdfJobRegistry.markFailed(jobId);
                    }

                });


/*
    The version below splits success and failure handling into two separate stages.
    thenRun(...) runs only if the previous stage completed successfully


        CompletableFuture<Void> future =
                CompletableFuture.runAsync(() -> {
                    pdfJobRegistry.markRunning(jobId);
                    fakePdfGenerationService.generateShipmentPdf(shipmentId);
                }, pdfExecutor);

        future.thenRun(() -> pdfJobRegistry.markCompleted(jobId))
                .exceptionally(ex -> {
                    pdfJobRegistry.markFailed(jobId);
                    return null;
                });*/
//###########################################£££
        /* Just plain executor with execute
        pdfExecutor.execute(() -> {
            try {
                pdfJobRegistry.markRunning(jobId);

                fakePdfGenerationService.generateShipmentPdf(shipmentId);

                pdfJobRegistry.markCompleted(jobId);

            } catch (Exception e) {
                pdfJobRegistry.markFailed(jobId);
            }
        });*/

        return jobId;
    }
}