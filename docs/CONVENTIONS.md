# Conventions

Short list. Reviewers will hold changes to these.

## Money

- Every amount is a `Money`: minor units as a `Long`, plus a `Currency`.
- Never use `Double`, `Float` or raw `BigDecimal` for amounts in domain code.
- Percentages are in basis points and go through `Money.percent`, which rounds half-even.
  Finance reconciles to the penny, so a different rounding rule counts as a bug.

## Time

- Never call `Instant.now()`, `LocalDate.now()` or `System.currentTimeMillis()`. Inject
  `BillingClock`.
- Billing dates are in the **customer's** time zone: `clock.today(customer.zone)`.
- `clock.systemDate()` is UTC and is for logs and batch ids only.

## Invoices

- `Invoice.amount` is what was billed and is never changed after issue.
- Anything that changes what is owed is an `InvoiceAdjustment`, added with
  `invoice.withAdjustment(...)`. What the customer owes is `invoice.total()`.

## Tests

- Pin time with a fixed `BillingClock` (see `TestFixtures.clockAt`).
- Prefer small hand-written fakes over mocking libraries; see `DunningJobTest`.
- A test name should state the rule it protects.
