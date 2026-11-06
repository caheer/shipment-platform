package com.example.pdf.model;

import com.example.pdf.PdfJobStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pdf_generation_jobs")
public class PdfJob {
    @Id
    @Column(name = "job_id", nullable = false)
    private UUID jobId;

    @Column(name = "shipment_id", nullable = false)
    private UUID shipmentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PdfJobStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "file_path", length = 2048)
    private String filePath;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    protected PdfJob() { }

    public PdfJob(UUID jobId, UUID shipmentId) {
        this.jobId = jobId;
        this.shipmentId = shipmentId;
        this.status = PdfJobStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getJobId() { return jobId; }
    public UUID getShipmentId() { return shipmentId; }
    public PdfJobStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getFinishedAt() { return finishedAt; }
    public String getFilePath() { return filePath; }
    public String getErrorMessage() { return errorMessage; }

    public void markRunning() {
        status = PdfJobStatus.RUNNING;
        startedAt = LocalDateTime.now();
    }

    public void markCompleted(String filePath) {
        status = PdfJobStatus.COMPLETED;
        this.filePath = filePath;
        errorMessage = null;
        finishedAt = LocalDateTime.now();
    }

    public void markFailed(String errorMessage) {
        status = PdfJobStatus.FAILED;
        this.errorMessage = errorMessage;
        finishedAt = LocalDateTime.now();
    }
}
