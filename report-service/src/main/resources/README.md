# Minimal reports-service for Shipment Platform

A small read-only reporting microservice: Kafka events -> PostgreSQL `reports_db` -> two REST GET endpoints.
No changes to invoice-service or pdf-service are required.

## What it reports
- Number of known shipments per latest status: `GET http://localhost:8083/api/reports/shipments/status-counts`
- Shipment reporting rows: `GET http://localhost:8083/api/reports/shipments`

## Setup
1. Add `<module>reports-service</module>` to `shipment-platform/pom.xml` in its `<modules>` block.
2. Place `reports-service/` alongside `shipment-service/`, `invoice-service/`, `pdf-service/`.
3. Create a PostgreSQL database `reports_db`; run `reports-service/src/main/resources/sql/reports-database.sql` against it.
4. Copy the two Java files from `shipment-service-changes/` to the indicated existing/new package paths.
5. In `ShipmentService.createShipment()`, immediately after saving the shipment event, add:

   ```java
   shipmentEventProducer.publish(new ShipmentCreatedEvent(
           savedShipment.getShipmentId(), savedShipment.getShipmentName(), currentDate));
   ```

   Also import `com.example.shipment.kafka.ShipmentCreatedEvent`.
   The existing `ShipmentService.updateShipmentStatus()` already publishes `ShipmentStatusChangedEvent`, so do not duplicate it.
6. Start PostgreSQL and Kafka, then run shipment-service and reports-service.
7. Create a NEW shipment, then change its status; use either reports GET endpoint above.

## Important limits (intentional)
- This service learns about shipments created/updated **after** event publishing is enabled; it does not automatically import the 96 seeded shipments. No fictitious initial snapshot is created.
- Two topics can be consumed in either order. SQL UPSERTs tolerate this and duplicate deliveries. The status timestamp is used to avoid applying an older status over a newer one (ties between distinct events are not resolved).
- When `ShipmentService` publishes inside a database transaction, a message could be emitted before the DB commits. For strong guarantees, publish after commit or use an outbox later. This example is a learning baseline.
- Missing Kafka topics must be created (or topic auto-creation allowed in your local Kafka instance).
- If the consumer repeatedly fails (e.g. DB down), messages are retried rather than silently ignored. Advanced retry/DLT handling is deferred.
- Cross-service enum labels are represented as strings in the reporting database.
- No automated builds/runtime tests executed here; build in your parent Maven project.
