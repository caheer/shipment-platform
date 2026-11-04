-- ============================================================
-- SHIPMENT DATABASE
-- RECREATE DATABASE STRUCTURE
-- Database: shipment_db
-- ============================================================


-- ============================================================
-- DROP EXISTING TABLES
-- Child tables must be dropped before referenced tables.
-- ============================================================

DROP TABLE IF EXISTS invoices; -- legacy table (was moved to invoice_db)
DROP TABLE IF EXISTS shipment_invoice_revisions;
DROP TABLE IF EXISTS shipments_comments;
DROP TABLE IF EXISTS customers_comments;
DROP TABLE IF EXISTS shipment_tags;
DROP TABLE IF EXISTS shipment_insurance;
DROP TABLE IF EXISTS shipment_events;
DROP TABLE IF EXISTS comments; -- remove former JOINED-inheritance base table
DROP TABLE IF EXISTS shipments;

DROP TABLE IF EXISTS tags;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS insurance_providers;

DROP TABLE IF EXISTS couriers;
DROP TABLE IF EXISTS locations;
DROP TABLE IF EXISTS customers;


-- ============================================================
-- CUSTOMERS
-- ============================================================

CREATE TABLE customers (
                           customer_id     UUID,
                           customer_name            VARCHAR(255) NOT NULL,
                           customer_email           VARCHAR(255) NOT NULL,
                           version                  BIGINT NOT NULL DEFAULT 0,

                           CONSTRAINT pk_customers
                               PRIMARY KEY (customer_id),

                           CONSTRAINT uq_customers_email
                               UNIQUE (customer_email)
);


-- ============================================================
-- LOCATIONS
-- ============================================================

CREATE TABLE locations (
                           location_id     UUID,
                           location_name   VARCHAR(255) NOT NULL,

                           CONSTRAINT pk_locations
                               PRIMARY KEY (location_id)
);


-- ============================================================
-- COURIERS
-- ============================================================

CREATE TABLE couriers (
                          courier_id      UUID,
                          courier_name    VARCHAR(255) NOT NULL,
                          courier_phone_number    VARCHAR(30),

                          CONSTRAINT pk_couriers
                              PRIMARY KEY (courier_id)
);


-- ============================================================
-- EVENTS DICTIONARY
-- ============================================================

CREATE TABLE events (
                        event_code      BIGINT,
                        event_name      VARCHAR(100) NOT NULL,

                        CONSTRAINT pk_events
                            PRIMARY KEY (event_code),

                        CONSTRAINT uq_events_event_name
                            UNIQUE (event_name)
);


-- ============================================================
-- TAGS DICTIONARY
-- ============================================================

CREATE TABLE tags (
                      tag_code        BIGINT,
                      tag_name        VARCHAR(100) NOT NULL,

                      CONSTRAINT pk_tags
                          PRIMARY KEY (tag_code),

                      CONSTRAINT uq_tags_tag_name
                          UNIQUE (tag_name)
);


-- ============================================================
-- INSURANCE PROVIDERS DICTIONARY
-- ============================================================

CREATE TABLE insurance_providers (
                                     provider_code   BIGINT,
                                     provider_name   VARCHAR(255) NOT NULL,

                                     CONSTRAINT pk_insurance_providers
                                         PRIMARY KEY (provider_code),

                                     CONSTRAINT uq_insurance_providers_name
                                         UNIQUE (provider_name)
);


-- ============================================================
-- SHIPMENTS
-- ============================================================

CREATE TABLE shipments (
                           shipment_id                 UUID,

                           customer_id                 UUID,
                           current_location_id         UUID,
                           destination_location_id     UUID,
                           assigned_courier_id         UUID,

                           shipment_status                      VARCHAR(50) NOT NULL,
                           shipment_name               VARCHAR(255) NOT NULL,
                           shipment_description          VARCHAR(1000),

                           weight_kg                   NUMERIC(10,3),
                           declared_value              NUMERIC(12,2),
                           shipping_type               VARCHAR(30) NOT NULL,
                           shipment_date_created       TIMESTAMP NOT NULL,
                           version                     BIGINT NOT NULL DEFAULT 0,

                           CONSTRAINT pk_shipments
                               PRIMARY KEY (shipment_id),

                           CONSTRAINT fk_shipments_customer
                               FOREIGN KEY (customer_id)
                                   REFERENCES customers(customer_id),

                           CONSTRAINT fk_shipments_current_location
                               FOREIGN KEY (current_location_id)
                                   REFERENCES locations(location_id),

                           CONSTRAINT fk_shipments_destination_location
                               FOREIGN KEY (destination_location_id)
                                   REFERENCES locations(location_id),

                           CONSTRAINT fk_shipments_assigned_courier
                               FOREIGN KEY (assigned_courier_id)
                                   REFERENCES couriers(courier_id),

                           CONSTRAINT chk_shipments_weight
                               CHECK (weight_kg IS NULL OR weight_kg > 0),

                           CONSTRAINT chk_shipments_declared_value
                               CHECK (declared_value IS NULL OR declared_value >= 0)
);


-- ============================================================
-- SHIPMENT EVENTS
-- ============================================================

CREATE TABLE shipment_events (
                                 shipment_event_id   UUID,
                                 shipment_id         UUID NOT NULL,
                                 event_code          BIGINT NOT NULL,
                                 occurred_at         TIMESTAMP NOT NULL,

                                 CONSTRAINT pk_shipment_events
                                     PRIMARY KEY (shipment_event_id),

                                 CONSTRAINT fk_shipment_events_shipment
                                     FOREIGN KEY (shipment_id)
                                         REFERENCES shipments(shipment_id),

                                 CONSTRAINT fk_shipment_events_event
                                     FOREIGN KEY (event_code)
                                         REFERENCES events(event_code)
);

-- ============================================================
-- SHIPMENT INVOICE REVISIONS
-- Each row identifies a distinct invoice creation operation.
-- Same revision ID is reused for retries; new invoice => new revision ID.
-- Revision numbers are allocated by shipment-service.
-- ============================================================
CREATE TABLE shipment_invoice_revisions (
                                            shipment_invoice_revision_id UUID PRIMARY KEY,
                                            shipment_id UUID NOT NULL,
                                            revision_number INTEGER NOT NULL,
                                            creation_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
                                            created_at TIMESTAMP NOT NULL,

                                            CONSTRAINT fk_shipment_invoice_revision_shipment
                                                FOREIGN KEY (shipment_id) REFERENCES shipments(shipment_id),
                                            CONSTRAINT uq_shipment_invoice_revision_number
                                                UNIQUE (shipment_id, revision_number),
                                            CONSTRAINT chk_shipment_invoice_revision_number
                                                CHECK (revision_number > 0),
                                            CONSTRAINT chk_shipment_invoice_creation_status
                                                CHECK (creation_status IN ('PENDING', 'COMPLETED', 'FAILED'))
);

-- ============================================================
-- SHIPMENT COMMENTS (independent table; no JOINED inheritance)
-- ============================================================
CREATE TABLE shipments_comments (
                                    comment_id UUID PRIMARY KEY,
                                    shipment_id UUID NOT NULL,
                                    comment_text VARCHAR(255) NOT NULL,
                                    comment_date TIMESTAMP NOT NULL,
                                    added_by VARCHAR(255),
                                    priority_action BOOLEAN NOT NULL,
                                    CONSTRAINT fk_shipments_comments_shipment FOREIGN KEY (shipment_id)
                                        REFERENCES shipments(shipment_id)
);

-- ============================================================
-- CUSTOMER COMMENTS (independent table)
-- ============================================================
CREATE TABLE customers_comments (
                                    comment_id UUID PRIMARY KEY,
                                    customer_id UUID NOT NULL,
                                    comment_text VARCHAR(255) NOT NULL,
                                    comment_date TIMESTAMP NOT NULL,
                                    added_by VARCHAR(255),
                                    CONSTRAINT fk_customers_comments_customer FOREIGN KEY (customer_id)
                                        REFERENCES customers(customer_id)
);

-- ============================================================
-- SHIPMENT TAGS
-- Association entity between Shipment and Tag.
-- Uses a surrogate primary key.
-- The (shipment_id, tag_code) pair remains unique.
-- ============================================================

CREATE TABLE shipment_tags (
                               shipment_tag_id UUID NOT NULL,
                               shipment_id     UUID NOT NULL,
                               tag_code        BIGINT NOT NULL,

                               CONSTRAINT pk_shipment_tags
                                   PRIMARY KEY (shipment_tag_id),

                               CONSTRAINT uq_shipment_tags_shipment_tag
                                   UNIQUE (shipment_id, tag_code),

                               CONSTRAINT fk_shipment_tags_shipment
                                   FOREIGN KEY (shipment_id)
                                       REFERENCES shipments(shipment_id),

                               CONSTRAINT fk_shipment_tags_tag
                                   FOREIGN KEY (tag_code)
                                       REFERENCES tags(tag_code)
);


-- ============================================================
-- SHIPMENT INSURANCE
-- One-to-one with Shipment.
-- Many insurance rows can reference one provider.
-- ============================================================

CREATE TABLE shipment_insurance (
                                    shipment_insurance_id        UUID,
                                    shipment_id         UUID NOT NULL,
                                    provider_code       BIGINT NOT NULL,
                                    insured_value       NUMERIC(12,2) NOT NULL,

                                    CONSTRAINT pk_shipment_insurance
                                        PRIMARY KEY (shipment_insurance_id),

                                    CONSTRAINT uq_shipment_insurance_shipment
                                        UNIQUE (shipment_id),

                                    CONSTRAINT fk_shipment_insurance_shipment
                                        FOREIGN KEY (shipment_id)
                                            REFERENCES shipments(shipment_id),

                                    CONSTRAINT fk_shipment_insurance_provider
                                        FOREIGN KEY (provider_code)
                                            REFERENCES insurance_providers(provider_code),

                                    CONSTRAINT chk_shipment_insurance_value
                                        CHECK (insured_value >= 0)
);
