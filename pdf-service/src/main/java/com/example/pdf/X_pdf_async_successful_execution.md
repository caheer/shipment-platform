# PDF Generation — Successful Asynchronous Execution

## A. What happens when PDF generation succeeds?

Assume `PdfGenerationCoordinator` calls:

```java
CompletableFuture<PdfGenerationResult> future =
    asyncPdfWorker.generatePdfAsync(shipmentId);
```

The following sequence applies when Spring's `@Async` support is enabled and the call goes through the Spring proxy.

1. **The caller receives a `CompletableFuture<PdfGenerationResult>` representing the pending result**, without waiting for the actual PDF generation.
   - Spring's async infrastructure schedules the method on an executor.
   - The future may already be completed if the work finishes unusually quickly, but normally it is still pending.

2. **An async worker thread starts executing `generatePdfAsync()`.**
   - It prints its thread name.
   - It calls `shipmentPdfGenerationService.generateShipmentPdf(shipmentId)`.

3. **The worker calls `shipment-service` using `RestClient`.**
   - The worker is blocked until the HTTP response arrives or the request fails.
   - If successful, it receives `ShipmentPdfDataDto`.
   - If the REST call throws an exception, the remainder of `generateShipmentPdf()` does not execute. The caller's future completes exceptionally instead (see the separate exceptions and timeouts notes).

4. **The worker generates the PDF.**
   - Your fake service simulates generation using `Thread.sleep()` for 3–8 seconds.
   - Once finished, it creates and returns `PdfGenerationResult` to `generatePdfAsync()`.

5. **The worker executes `completedFuture(...)`.**

   ```java
   return CompletableFuture.completedFuture(pdfGenerationResult);
   ```

   - This creates an already-completed `CompletableFuture` containing the result.
   - It does **not** start a new asynchronous task.

6. **The caller's future (returned earlier from `asyncPdfWorker.generatePdfAsync(...)`) completes successfully.**
   - Spring's async infrastructure takes the result of the worker method and completes the future previously returned to the coordinator.
   - Any dependent callbacks, such as `thenApply()` or `thenAccept()`, can now execute.
   - A `whenComplete((result, exception) -> { ... })` callback also executes: `result` contains `PdfGenerationResult` and `exception` is `null`.

### Important distinction

The already-completed future created **inside** the worker method is conceptually separate from the future Spring returned **earlier** to the coordinator. Spring bridges the worker's eventual result into the caller's future.

**In short:** `@Async` moves the work to an executor thread; `CompletableFuture` represents its eventual outcome; `completedFuture(result)` wraps a result that has already been produced.
