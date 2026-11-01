package com.example.pdf;

import com.example.pdf.model.PdfGenerationResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class PdfGenerationCoordinator {


    private final PdfJobRegistry pdfJobRegistry;
    private final AsyncPdfWorker asyncPdfWorker;

    private static final Logger log =
            LoggerFactory.getLogger(PdfGenerationCoordinator.class);

    public PdfGenerationCoordinator(
            PdfJobRegistry pdfJobRegistry, AsyncPdfWorker asyncPdfWorker) {
        this.pdfJobRegistry = pdfJobRegistry;
        this.asyncPdfWorker = asyncPdfWorker;
    }

    public UUID startGenerationAsync(UUID shipmentId) {

        UUID jobId = UUID.randomUUID();

        pdfJobRegistry.createJob(jobId);

        log.info("JOB ID: " + jobId);
        pdfJobRegistry.markRunning(jobId);

        // it's recommended to set timeout here CompletableFuture<PdfGenerationResult> future
        // = asyncPdfWorker.generatePdfAsync(shipmentId).orTimeout(10, TimeUnit.SECONDS);

        CompletableFuture<PdfGenerationResult> future = asyncPdfWorker.generatePdfAsync(shipmentId);

        future.whenComplete((result, exception) -> {
            if (exception == null) {
                pdfJobRegistry.markCompleted(jobId);
            } else {
                pdfJobRegistry.markFailed(jobId);
            }
        });

        return jobId;
    }
}