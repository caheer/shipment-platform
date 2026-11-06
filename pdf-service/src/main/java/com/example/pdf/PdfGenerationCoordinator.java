package com.example.pdf;

import com.example.pdf.kafka.PdfEventPublisher;
import com.example.pdf.kafka.PdfGeneratedEvent;
import com.example.pdf.model.PdfGenerationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class PdfGenerationCoordinator {
    private static final Logger log = LoggerFactory.getLogger(PdfGenerationCoordinator.class);
    private final PdfJobRegistry pdfJobRegistry;
    private final AsyncPdfWorker asyncPdfWorker;
    private final PdfEventPublisher pdfEventPublisher;

    public PdfGenerationCoordinator(PdfJobRegistry pdfJobRegistry,
                                    AsyncPdfWorker asyncPdfWorker,
                                    PdfEventPublisher pdfEventPublisher) {
        this.pdfJobRegistry = pdfJobRegistry;
        this.asyncPdfWorker = asyncPdfWorker;
        this.pdfEventPublisher = pdfEventPublisher;
    }

    public UUID startGenerationAsync(UUID shipmentId) {
        UUID jobId = UUID.randomUUID();
        // The DB transaction commits before the worker is scheduled.
        pdfJobRegistry.createJob(jobId, shipmentId);
        pdfJobRegistry.markRunning(jobId);

        // it's recommended to set timeout here CompletableFuture<PdfGenerationResult> future
        // = asyncPdfWorker.generatePdfAsync(shipmentId).orTimeout(10, TimeUnit.SECONDS);

        CompletableFuture<PdfGenerationResult> future = asyncPdfWorker.generatePdfAsync(shipmentId);

        future.whenComplete((result, exception) -> {
            if (exception != null) {
                log.error("PDF generation failed for job {}", jobId, exception);
                pdfJobRegistry.markFailed(jobId, exception);
                return;
            }
            // Mark completed only after the PDF has been written to disk.
            pdfJobRegistry.markCompleted(jobId, result.pdfContent());
            // Optional Kafka notification. Broker delivery is not atomically tied to the DB update.
            try {
                pdfEventPublisher.publish(new PdfGeneratedEvent(
                        jobId, result.shipmentId(), result.pdfContent()));
            } catch (RuntimeException e) {
                log.error("Could not publish PDF-generated event for job {}", jobId, e);
            }
        });
        return jobId;
    }
}
