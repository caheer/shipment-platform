-- ============================================================
-- INCOICES DATABASE
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
                          invoice_id           UUID NOT NULL,
                          invoice_number       VARCHAR(255),
                          shipment_id          UUID,
                          shipment_name        VARCHAR(255) NOT NULL,
                          shipping_type        VARCHAR(30) NOT NULL,
                          customer_id          UUID,
                          customer_name        VARCHAR(255) NOT NULL,
                          customer_email          VARCHAR(255) NOT NULL,
                          billing_address      VARCHAR(255),
                          rate_per_km          NUMERIC(12,2),
                          kilometers_spent     INTEGER,
                          taxes                NUMERIC(12,2),
                          additional_charges   NUMERIC(12,2),
                          total_amount         NUMERIC(12,2),
                          invoice_status       VARCHAR(255),
                          invoice_type         VARCHAR(255),
                          issued_at            TIMESTAMP,
                          issued_by            VARCHAR(255),
                          version              BIGINT NOT NULL DEFAULT 0,

                          CONSTRAINT pk_invoices PRIMARY KEY (invoice_id),
                          CONSTRAINT uq_invoices_invoice_number UNIQUE (invoice_number)
);