# Minimal notification-service for Shipment Platform

**Purpose:** consume Kafka events and record notification history in a separate `notification_db`. It does **not** send email, SMS or push notifications yet. Notifications are simple records you can inspect with one GET endpoint.

## What is included
- Spring Boot 4 / Java 21 Maven module, same parent and style as reports-service.
- Four Kafka consumers: `shipment-created`, `shipment-status-changed`, `invoice-created`, `pdf-generated`.
- PostgreSQL notifications table; `ON CONFLICT DO NOTHING` prevents redelivered events from creating duplicate rows.
- `GET http://localhost:8084/api/notifications` returns the 100 newest records.
- Optional producer classes for invoice-service and pdf-service in `integration-examples/`.

## Setup
1. Add `<module>notification-service</module>` to `shipment-platform/pom.xml`.
2. Place `notification-service/` beside the other service modules.
3. Create PostgreSQL DB `notification_db`; run `notification-service/src/main/resources/sql/notification-database.sql` in that DB.
4. Start Kafka and PostgreSQL; create the four topics or enable Kafka topic auto-creation in your local broker.
5. Start notification-service (port 8084).
6. Start producing events; check `GET http://localhost:8084/api/notifications`.

### Shipment events (no extra event type needed)
- `ShipmentStatusChangedEvent` already exists in shipment-service and is sent to `shipment-status-changed` in `updateShipmentStatus()`.
- `ShipmentCreatedEvent` and publishing to `shipment-created` were supplied in **reports-service-starter**. If you have applied those changes, notification-service consumes the same events without modifying shipment-service again.

### Invoice-created publisher (optional integration)
Copy both Java files in `integration-examples/invoice-service/` to `com.example.invoicing.kafka`.
In `InvoiceService`, inject `InvoiceEventProducer`. In the **new invoice branch only**, after `invoiceRepository.save(invoice)`:

```java
// Minimal demonstration: Kafka publish is not atomic with DB commit.
invoiceEventPublisher.publish(new InvoiceCreatedEvent(
        invoice.getInvoiceId(), invoice.getShipmentId(), invoice.getRevisionNumber()));
```

Import `InvoiceCreatedEvent` and `InvoiceEventProducer`. Do not publish in the `existingInvoice.isPresent()` path (idempotent retry). The same event type covers the initial invoice and later revisions using `revisionNumber`.

In invoice-service `application.yaml`, configure Kafka producer JSON serialization (in addition to existing `spring.kafka.bootstrap-servers`):

```yaml
spring:
  kafka:
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JacksonJsonSerializer
```

**Important:** place the producer block under your existing `spring.kafka`, don't introduce a second top-level `spring:` key.

### Pdf-generated publisher (optional integration)
Copy both Java files from `integration-examples/pdf-service/` to `com.example.pdf.kafka`.
Inject `PdfEventPublisher` into `PdfGenerationCoordinator` (add constructor parameter and field). Inside the existing `future.whenComplete(...)` success branch, **after** marking the job completed:

```java
pdfEventPublisher.publish(new PdfGeneratedEvent(
        jobId, result.shipmentId(), result.pdfContent()));
```

Import `PdfGeneratedEvent` and `PdfEventPublisher`. This assumes your corrected `ShipmentPdfGenerationService` stores the generated PDF **path** in `PdfGenerationResult.pdfContent()` (as discussed). Publishing is a demo side effect; a Kafka failure after PDF completion should eventually be handled separately.

In pdf-service `application.yaml`, configure Kafka producer JSON serialization under its existing `spring.kafka` (same block as for invoice-service above).

## Practical boundaries / intentionally deferred
- Nothing is actually emailed; this is notification history, not delivery infrastructure.
- No existing shipment/invoice data is backfilled automatically. Kafka consumer has `auto-offset-reset: earliest`, which only applies if the group has no committed offset; it does not invent events that were never published.
- Idempotency is based on a deterministic event key. Two distinct status changes with the *exact* same `changedAt` value would collide; use a real eventId later if needed.
- Events published directly inside database transactions can be lost or emitted prematurely. The transactional outbox pattern / after-commit publication is a separate reliability exercise.
- For invoice and PDF producers, only a `send()` invocation is shown; production-quality delivery guarantees, logging, failure handling and retries are deliberately not implemented.
- The Kafka consumers use StringDeserializer + Jackson ObjectMapper; producer JSON type headers are ignored rather than shared as Java classes across services.
- Build/run not executed in this environment; verify locally against your parent Maven version.
