package com.example.shipment.pdf;

import com.example.shipment.service.FakePdfGenerationService;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
public class PdfGenerationCoordinator {

    private final ExecutorService pdfExecutor;
    private final FakePdfGenerationService fakePdfGenerationService;
    private final PdfJobRegistry pdfJobRegistry;

    public PdfGenerationCoordinator(
            ExecutorService pdfExecutor,
            FakePdfGenerationService fakePdfGenerationService, PdfJobRegistry pdfJobRegistry) {
        this.pdfExecutor = pdfExecutor;
        this.fakePdfGenerationService = fakePdfGenerationService;
        this.pdfJobRegistry = pdfJobRegistry;
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