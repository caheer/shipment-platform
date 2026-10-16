package com.example.shipment.pdf;

import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PdfJobRegistry {

    private final ConcurrentHashMap<UUID, PdfJobStatus> jobs =
            new ConcurrentHashMap<>();

    public void createJob(UUID jobId) {
        jobs.put(jobId, PdfJobStatus.PENDING);
    }

    public void markRunning(UUID jobId) {
        jobs.put(jobId, PdfJobStatus.RUNNING);
    }

    public void markCompleted(UUID jobId) {
        jobs.put(jobId, PdfJobStatus.COMPLETED);
    }

    public void markFailed(UUID jobId) {
        jobs.put(jobId, PdfJobStatus.FAILED);
    }

    public PdfJobStatus getStatus(UUID jobId) {
        return jobs.get(jobId);
    }
}