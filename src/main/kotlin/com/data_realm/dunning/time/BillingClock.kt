package com.data_realm.dunning.time

import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

/**
 * The only source of "now" in this service. Do not call Instant.now(), LocalDate.now() and so on
 * directly: tests pin time through this class.
 *
 * Billing dates (due dates, grace periods, retry days) are calendar dates in the customer's own
 * time zone, so use [today] with the customer's zone. [systemDate] is the UTC date and is for logs
 * and batch identifiers only.
 */
class BillingClock(private val clock: Clock) {

    fun now(): Instant = clock.instant()

    fun today(zone: ZoneId): LocalDate = LocalDate.ofInstant(clock.instant(), zone)

    fun systemDate(): LocalDate = LocalDate.ofInstant(clock.instant(), ZoneOffset.UTC)
}
