-- Run on reports_db, not shipment_db or invoice_db.
CREATE TABLE IF NOT EXISTS shipment_reports (
    shipment_id UUID PRIMARY KEY,
    shipment_name VARCHAR(255),
    shipment_status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP,
    status_changed_at TIMESTAMP NOT NULL
);
