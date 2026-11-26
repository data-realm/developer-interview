# Developer interview at Data Realm: dunning service

## What this session is

This is not a puzzle and there are no trick questions. It's a small, realistic service with a
couple of real-looking tickets. It gives us shared code to work on together and talk about.

**Please use the AI tools you use day to day** (Claude Code, Cursor, Copilot, ChatGPT, or none).
We're not testing whether you can work without them; we want to see how you work with them. Share
your screen and think aloud as you go. We care far more about how you approach the problem than
about finishing.

If anything in a ticket is unclear, ask us, as you would ask a product owner. We'll answer the
same way.

## The service

When a subscription invoice isn't paid, this service runs *dunning*: it retries the card on set
days after the due date and suspends the account once the grace period is over. It runs hourly as
a scheduled job.

```
src/main/kotlin/com/data_realm/dunning/
  dunning/        DunningJob (the hourly run), SuspensionPolicy, RetryPolicy
  domain/         Customer, PlanTier, Invoice, InvoiceAdjustment, PaymentAttempt
  money/          Money
  time/           BillingClock
  payments/       PaymentGateway (and a fake)
  notifications/  CustomerNotifier (and a logging fake)
  repository/     in-memory repositories
  api/            a small REST API
docs/             conventions and billing rules
```

## Running it

Nothing to install. If you don't have JDK 17 or newer, the first `./gradlew` run downloads a
portable JDK into `.jdk/` in this folder (about 200 MB, one time only, nothing installed
system-wide). Gradle comes with the repo through the wrapper. On Windows, use `gradlew.bat`.

If you open the project in IntelliJ, point it at `.jdk/` or let it download a JDK when it asks.

```bash
./gradlew test        # run the tests
./gradlew bootRun     # start on :8080 with some demo customers
```

With the app running:

```bash
curl localhost:8080/invoices                     # open invoices
curl localhost:8080/invoices?status=SUSPENDED
curl -X POST localhost:8080/admin/dunning/run    # run the dunning job now
```

## Tickets

Work on them in whichever order you like.

### BIL-209: US customers suspended a day early (bug)

> **From support:** Sunset Studios (based in Los Angeles) say their account was suspended on the
> evening of 10 March. Their invoice was due on 3 March and our terms give 7 days' grace, so they
> should have had until the end of the 10th. Two other US customers have said something similar.
> Nobody in Europe has complained.

### BIL-214: Late fee on suspension (feature)

> **From finance:** When we suspend an account for non-payment, add a late fee of 2.5% of the
> outstanding amount, with a minimum of £5. Customers on legacy plans shouldn't be charged the
> fee. The fee needs to show on the invoice and in the suspension email.
