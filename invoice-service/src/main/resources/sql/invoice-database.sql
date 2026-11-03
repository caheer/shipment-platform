-- ============================================================
-- INVOICES DATABASE
-- RECREATE DATABASE STRUCTURE
-- Database: invoice_db
-- ============================================================


-- ============================================================
-- DROP EXISTING TABLES
-- Child tables must be dropped before referenced tables.
-- ============================================================

DROP TABLE IF EXISTS invoices;

-- ============================================================
-- INVOICES
-- Multiple invoices can reference the same shipment or customer.
-- No invoice/customer junction table is needed.
-- UUIDs are assigned by the Java application, not the database.
-- ============================================================

CREATE TABLE invoices (
                          invoice_id UUID PRIMARY KEY,

                          shipment_invoice_revision_id UUID NOT NULL,

                          invoice_number VARCHAR(255),
                          revision_number INTEGER NOT NULL,

                          shipment_id UUID NOT NULL,
                          shipment_name VARCHAR(255) NOT NULL,
                          shipping_type VARCHAR(30) NOT NULL,

                          customer_id UUID,
                          customer_name VARCHAR(255) NOT NULL,
                          customer_email VARCHAR(255) NOT NULL,
                          billing_address VARCHAR(255),

                          rate_per_km NUMERIC(12,2),
                          kilometers_spent INTEGER,
                          taxes NUMERIC(12,2),
                          additional_charges NUMERIC(12,2),
                          total_amount NUMERIC(12,2),

                          invoice_status VARCHAR(30),
                          invoice_type VARCHAR(30),
                          issued_at TIMESTAMP,
                          issued_by VARCHAR(255),

                          version BIGINT NOT NULL DEFAULT 0,

                          CONSTRAINT uq_invoices_invoice_number
                              UNIQUE (invoice_number),
                          CONSTRAINT uq_invoices_shipment_revision
                              UNIQUE (shipment_invoice_revision_id),
                          CONSTRAINT uq_invoice_shipment_revision_number
                              UNIQUE (shipment_id, revision_number),
                          CONSTRAINT chk_invoice_revision_number
                              CHECK (revision_number > 0)
);