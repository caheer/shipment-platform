package com.example.pdf;

import com.example.pdf.model.PdfGenerationResult;
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

        log.info("JOB ID: " + jobId);
        pdfJobRegistry.markRunning(jobId);
        CompletableFuture<PdfGenerationResult> future = asyncPdfWorker.generatePdfAsync(shipmentId);

        CompletableFuture<String> formattedFuture = future
                .thenApply(result -> {

                    log.info("Formatting PDF...");

                    if (result.pdfContent().contains("SHIPMENT PDF")) {
                        throw new IllegalStateException("Simulated formatting error");
                    }

                    return "Generated for shipment: " + result.shipmentId()
                            + "\nGenerated at: " + result.generatedAt()
                            + "\n\n" + result.pdfContent();
                })
                .exceptionally(exception -> {

                    log.warn("Formatting failed. Using fallback.", exception);

                    return "PDF content unavailable";
                });

        formattedFuture
                .thenAccept(content -> log.info("FINAL CONTENT: {}", content))
                .whenComplete((result, exception) -> {
                    if (exception == null) {
                        pdfJobRegistry.markCompleted(jobId);
                    } else {
                        pdfJobRegistry.markFailed(jobId);
                        log.error("PDF processing failed", exception);
                    }
                });

        CompletableFuture<PdfGenerationResult> generationFuture =
                asyncPdfWorker.generatePdfAsync(shipmentId)
                        .exceptionallyCompose(exception -> {

                            log.warn("First PDF generation attempt failed. Retrying...",
                                    exception);

                            return asyncPdfWorker.generatePdfAsync(shipmentId);
                        });



/*        CompletableFuture<Void> formattedFuture = future.thenApply(result -> {

            log.info("thenApply thread: " + Thread.currentThread().getName());
            return "Generated for shipment: " + result.shipmentId() + "\nGenerated at: " + result.generatedAt() + "\n\n" + result.pdfContent();

        }).thenAccept(content -> {

            log.info("thenAccept thread: " + Thread.currentThread().getName());
            log.info("CONTENT: " + content);

        }).whenComplete((result, exception) -> {

            if (exception == null) {
                pdfJobRegistry.markCompleted(jobId);
            } else {
                pdfJobRegistry.markFailed(jobId);
                log.error("FUTURE COMPLETED EXCEPTIONALLY: " + future.isCompletedExceptionally());
                log.error("STACKTRACE: " + Arrays.toString(exception.getStackTrace()));
                log.error("PDF generation failed for shipment {}", shipmentId, exception);
            }
        });*/

        return jobId;
    }

/*    public UUID startGenerationAsyncV1(UUID shipmentId) {

        UUID jobId = UUID.randomUUID();

        pdfJobRegistry.createJob(jobId);

        // whenComplete: completion callback that always runs, regardless of success or failurr
        // When this future is done, inspect whether it succeeded or failed and do some side effect

        CompletableFuture
                .supplyAsync(() -> {

                    pdfJobRegistry.markRunning(jobId);

                    PdfGenerationResult pdfContent = fakePdfGenerationService
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
    }*/
}