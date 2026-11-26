# Billing rules

## Currencies

Customers are billed in their own currency: GBP, EUR, USD or JPY. This service never converts
between currencies.

## Dates

An invoice's due date is a calendar date in the customer's time zone. "Day N after the due date"
also means the customer's calendar day.

## Retries

If a payment fails, the card is retried on days 1, 3 and 5 after the due date, at most once per
day. These are configurable as `dunning.retry-days-after-due`.

## Grace period and suspension

The customer has a grace period of 7 days (`dunning.grace-days`) after the due date. They have
until the end of the last day of grace, in their own time zone, to pay. An invoice still unpaid
after that is suspended and the customer is emailed.

Example: due 3 March, 7 days' grace. The customer can pay any time up to the end of 10 March
local time and is suspended from 11 March local time.

## Plans

Plans from the pre-2023 price list are *grandfathered* (see `PlanTier`). Grandfathered customers
keep the terms they signed up on.
