# Shipment Platform — lightweight local frontend

Plain HTML, JavaScript `fetch()`, and Bootstrap 5. No npm/React/build step, no mocks.
Generated against **shipment_platform_project_snapshot_2026-11-06_11-04-22.txt**.

## Install

1. Copy the **contents** of `static/` into `shipment-platform/shipment-service/src/main/resources/static/`.
2. Start `shipment-service` on port **8080**, then visit **http://localhost:8080/**. Do **not** open index.html through `file://`.
3. Start optional services as needed: `invoice-service` **8082**, `pdf-service` **8081**, `report-service` **8083**, `notification-service` **8084**. PostgreSQL and Kafka dependencies need to be running as configured in your project.
4. For cross-origin browser access, copy each `optional-cors/<service>/src/.../LocalFrontendCorsConfig.java` into the matching source package of that service. These are **development-only** configurations. Shipment-service's same-origin requests require no CORS config.

**IMPORTANT: Port collision.** Your root `docker-compose.yaml` currently maps Kafka UI to host `8081:8080`, conflicting with the PDF service at port 8081. Change the `kafka-ui` mapping in the project's own docker-compose.yaml to `"8091:8080"` (or another available host port), then recreate that container. Kafka UI moves to http://localhost:8091; PDF service stays at http://localhost:8081. This archive does not alter your docker-compose.yaml automatically.

Bootstrap CSS/JS loads from a CDN, so an Internet connection is needed for the styling and the create-shipment modal. The REST calls themselves remain local.

## Pages

- **Shipments**: paginated list, global status counts, lookup by UUID, create shipment, change status/customer/current location, add/delete comments, inspect PDF input data, create the *initial* invoice through shipment-service.
- **Invoices**: search by criteria, lookup by invoice UUID, update rate/taxes/additional charges. Current `InvoiceSearchItemDto` has no invoice ID, so opening an invoice directly from search results is not supported.
- **PDF jobs**: start asynchronous job, show status/history, poll running jobs every 2.5s, download a finished PDF. Job history and status are obtained from pdf-service.
- **Reports**: Kafka-driven shipment projection list and status counts.
- **Notifications**: recent notification records.
- **Developer tools**: send a test Kafka message and view configured service URLs.

## Known backend constraints from the snapshot

1. `ShipmentRepository.findDtoByShipmentId(UUID)` has a JPQL query with **no WHERE clause**: `from Shipment s`, even though the controller accepts a UUID. Fix with `where s.shipmentId = :shipmentId` to make detail lookup reliable. The frontend does not conceal this problem.
2. `ShipmentService.getShipmentPdfDataByShipmentId()` directly calls getters on customer/current/destination associations. For shipments where they are null (including some newly created ones), this results in an error until the backend handles absent relationships. Comment display depends on this endpoint.
3. No REST list endpoints for customers, locations, and couriers exist in the snapshot. The create/update UI therefore takes UUIDs rather than offering dropdowns; the SQL sample data includes usable IDs.
4. Invoice search `InvoiceSearchItemDto` has no `invoiceId`. Use the invoice UUID returned by `POST /shipments/{shipmentId}/invoice` or enter a known UUID.
5. Shipment invoice creation currently prepares/retries revision **1**; the frontend does not claim to support creating arbitrary revision numbers.
6. Reports and notifications represent Kafka projections and may be empty until events have been processed. Creating a shipment may not yet emit every event needed by report-service: inspect your producers and listeners.
7. No authentication is implemented in this local test UI. Do not expose it publicly or use the permissive CORS configuration for production.
8. PDF downloads use a browser blob, so the user can save the file; the server-side output path is not exposed as a public static directory.

## API mapping (key routes)

| Service | Route | Method |
|---|---|---|
| shipment | `/shipment/getShipmentListPage?page=0&size=10` | GET |
| shipment | `/shipment` | POST |
| shipment | `/shipment/getShipmentById/{id}` | GET |
| shipment | `/shipment/{id}/status` | PATCH `{ "shipmentStatus": "IN_TRANSIT" }` |
| shipment | `/shipment/{id}/customer` | PATCH `{ "customerId": "..." }` |
| shipment | `/shipment/{id}/current-location` | PATCH `{ "locationId": "..." }` |
| shipment | `/comment/addShipmentComment/{id}` | POST |
| shipment | `/comment/deleteShipmentComment/{id}` | DELETE |
| shipment | `/shipments/{id}/invoice` | POST |
| shipment | `/statistics/getShipmentsCountByStatus` | GET |
| invoice | `/api/invoices/search` | POST |
| invoice | `/api/invoices/{invoiceId}` | GET |
| invoice | `/api/invoices/{invoiceId}/taxes` (and other amount fields) | PATCH raw JSON number |
| pdf | `/pdf/generatePdfAsync/{shipmentId}` | POST |
| pdf | `/pdf/shipments/{shipmentId}/jobs` | GET |
| pdf | `/pdf/jobs/{jobId}` | GET |
| pdf | `/pdf/download/{jobId}` | GET |
| reports | `/api/reports/shipments`, `/api/reports/shipments/status-counts` | GET |
| notifications | `/api/notifications` | GET |

## Validation

JavaScript syntax checked with `node --check`. Static file references and packaged files checked. End-to-end runtime integration has **not** been tested against running Spring Boot services.
