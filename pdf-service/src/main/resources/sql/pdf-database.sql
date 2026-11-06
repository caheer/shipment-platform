-- Run against a freshly created pdf_db database.
-- WARNING: DROP TABLE removes previously stored job history.
DROP TABLE IF EXISTS pdf_generation_jobs;
CREATE TABLE pdf_generation_jobs (
    job_id UUID PRIMARY KEY,
    shipment_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    started_at TIMESTAMP,
    finished_at TIMESTAMP,
    file_path VARCHAR(2048),
    error_message VARCHAR(2000),
    CONSTRAINT chk_pdf_job_status CHECK (status IN ('PENDING','RUNNING','COMPLETED','FAILED'))
);
CREATE INDEX ix_pdf_jobs_shipment_created
    ON pdf_generation_jobs (shipment_id, created_at DESC);
