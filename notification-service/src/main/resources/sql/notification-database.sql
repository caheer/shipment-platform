-- Run against notification_db (not shipment_db).
CREATE TABLE IF NOT EXISTS notifications (
    event_key VARCHAR(180) PRIMARY KEY,
    event_type VARCHAR(60) NOT NULL,
    reference_id UUID NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_notifications_created_at ON notifications (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_notifications_reference ON notifications (reference_id);
