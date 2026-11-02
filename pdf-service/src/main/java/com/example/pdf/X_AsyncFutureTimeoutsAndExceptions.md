# PDF Service — Asynchronous Exceptions and Timeouts

Notes for the `@Async` PDF-generation flow using `RestClient` and `CompletableFuture<PdfGenerationResult>`.

## A. What if something throws an exception?

1. `RestClient` throws an exception (or another operation inside `ShipmentPdfGenerationService` throws an exception).
2. The exception propagates out of `ShipmentPdfGenerationService`.
3. Execution of `generatePdfAsync()` stops.
4. `CompletableFuture.completedFuture(...)` is never executed.
5. Spring's async infrastructure catches the exception and completes the caller's `CompletableFuture` exceptionally.
6. `future.whenComplete((result, exception) -> {...})` executes whether there was success or an exception:
    - **Success:** `result` contains `PdfGenerationResult`; `exception` is `null`.
    - **Failure:** `result` is `null`; `exception` contains the exception (possibly wrapped).

## B. What happens with HTTP timeouts?

1. `RestClient` exceeds its configured HTTP timeout (e.g., a read timeout of 5 seconds).
2. `RestClient` throws an exception, typically `ResourceAccessException`.
3. The exception propagates out of `ShipmentPdfGenerationService`.
4. Execution of `generatePdfAsync()` stops.
5. `CompletableFuture.completedFuture(...)` is never executed.
6. Spring's async infrastructure catches the exception and completes the caller's `CompletableFuture` exceptionally.
7. `future.whenComplete((result, exception) -> {...})` executes:
    - `result` is `null`.
    - `exception` contains the exception (possibly wrapped).

> **Note:** An HTTP timeout affects the REST request, whereas `CompletableFuture.orTimeout()` affects the future's completion and does **not** automatically stop the underlying worker thread.

## C. What if PDF generation takes too long?

This is different from an HTTP timeout.

Unless you introduce a separate timeout mechanism, there is **no automatic execution timeout** for the entire `@Async` method.

You could introduce a timeout on the future using `.orTimeout(10, TimeUnit.SECONDS)`:

```java
CompletableFuture<PdfGenerationResult> future =
    asyncPdfWorker.generatePdfAsync(shipmentId)
        .orTimeout(10, TimeUnit.SECONDS);
```

If the future is still incomplete after **10 seconds**, it completes exceptionally with a `TimeoutException`.

**Important catch:** `orTimeout()` does **not** automatically stop or interrupt the worker thread.

The actual PDF-generation operation may continue running even after the future has timed out. This distinction matters once we begin saving job statuses to the database.
