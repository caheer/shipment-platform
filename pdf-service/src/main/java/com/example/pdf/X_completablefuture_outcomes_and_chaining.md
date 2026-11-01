# CompletableFuture — Outcomes and Chaining

## 1. What does the asynchronous call return?

```java
CompletableFuture<PdfGenerationResult> future =
    asyncPdfWorker.generatePdfAsync(shipmentId);
```

Assuming Spring `@Async` is enabled and the method is called through its Spring proxy, the caller normally receives a `CompletableFuture` immediately, without waiting for PDF generation.

The future can be:

| State | What it contains | What can happen next |
|---|---|---|
| **Incomplete (pending)** | No result yet | Register callbacks, wait, or apply a timeout. |
| **Completed successfully** | `PdfGenerationResult` | Process the result with `thenApply()` or `thenAccept()`. |
| **Completed exceptionally** | An exception, not a `PdfGenerationResult` | Inspect the failure with `whenComplete()`, or recover using `exceptionally()`. |

## 2. Common chaining methods

| Method | On success | On failure |
|---|---|---|
| `thenApply()` | Runs and transforms the result | Skipped |
| `thenAccept()` | Runs and consumes the result | Skipped |
| `thenRun()` | Runs without receiving the result | Skipped |
| `exceptionally()` | Skipped | Runs and may return a fallback value |
| `exceptionallyCompose()` | Skipped | Runs and may return another future, e.g., for a retry |
| `whenComplete()` | Runs | Runs |
| `handle()` | Runs | Runs |

Each stage receives **the outcome of the preceding stage**, not necessarily the original future. Most chaining methods create a new dependent future.

## 3. Example execution paths

```java
future
    .thenApply(result -> result.pdfContent())
    .exceptionally(exception -> "PDF unavailable")
    .whenComplete((result, exception) -> {
        // Observe the result of the preceding stage
    });
```

**Path A — successful generation and transformation**

1. `thenApply()` runs and produces the PDF content string.
2. `exceptionally()` is skipped.
3. `whenComplete()` runs with the content string; `exception == null`.

**Path B — generation or transformation fails**

1. `thenApply()` is skipped if the original future failed; alternatively, it may run and throw its own exception.
2. `exceptionally()` runs and returns `"PDF unavailable"`.
3. `whenComplete()` runs with that **fallback value**; `exception == null` because the failure was recovered.

**Important:** `whenComplete()` observes the completion of the stage directly before it. Once `exceptionally()` recovers, the following `whenComplete()` does **not** see the original exception.

If the fallback handler itself throws, the downstream future instead completes exceptionally.

## 4. What makes sense for the PDF service now?

We currently want to record a job as successful or failed, **without retries or fallback content**:

```java
future.whenComplete((result, exception) -> {
    if (exception == null) {
        pdfJobRegistry.markCompleted(jobId);
    } else {
        pdfJobRegistry.markFailed(jobId);
    }
});
```

This observes the original outcome directly:

- **Success:** `result` contains `PdfGenerationResult`; `exception` is `null`.
- **Failure:** `result` is `null`; `exception` contains the failure (possibly wrapped).

## 5. Timeout reminder

```java
future.orTimeout(10, TimeUnit.SECONDS);
```

If still incomplete after 10 seconds, the future completes exceptionally with `TimeoutException`. **This does not automatically interrupt or stop the worker thread.** `orTimeout()` modifies and returns the same future.

**Key takeaway:** Success and failure choose which stages run, but a recovery stage can change the outcome seen by later stages.
