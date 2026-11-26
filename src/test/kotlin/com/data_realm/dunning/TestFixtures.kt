package com.data_realm.dunning

import com.data_realm.dunning.config.DunningProperties
import com.data_realm.dunning.domain.Customer
import com.data_realm.dunning.domain.Invoice
import com.data_realm.dunning.domain.PlanTier
import com.data_realm.dunning.money.Money
import com.data_realm.dunning.time.BillingClock
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

object TestFixtures {
    val properties = DunningProperties(graceDays = 7, retryDaysAfterDue = listOf(1, 3, 5))

    fun clockAt(instant: String): BillingClock = BillingClock(Clock.fixed(Instant.parse(instant), ZoneOffset.UTC))

    fun customer(
        id: String = "cus_1",
        zone: String = "Europe/London",
        plan: PlanTier = PlanTier.GROWTH,
    ) = Customer(id, "Customer $id", "$id@example.com", ZoneId.of(zone), plan)

    fun invoice(
        id: String = "inv_1",
        customerId: String = "cus_1",
        amount: Money = Money.of("100.00", "GBP"),
        dueDate: String = "2026-03-03",
    ) = Invoice(id, customerId, amount, LocalDate.parse(dueDate))
}
