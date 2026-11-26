package com.data_realm.dunning.dunning

import com.data_realm.dunning.TestFixtures.customer
import com.data_realm.dunning.TestFixtures.invoice
import com.data_realm.dunning.TestFixtures.properties
import com.data_realm.dunning.config.DunningProperties
import com.data_realm.dunning.config.LateFeeProperties
import com.data_realm.dunning.domain.AdjustmentType
import com.data_realm.dunning.domain.InvoiceAdjustment
import com.data_realm.dunning.domain.PlanTier
import com.data_realm.dunning.money.Money
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LateFeePolicyTest {

    private val policy = LateFeePolicy(properties)
    private val standard = customer()

    private fun gbp(amount: String) = Money.of(amount, "GBP")

    private fun adjustment(type: AdjustmentType, amount: Money) =
        InvoiceAdjustment(type, amount, "test", Instant.parse("2026-03-01T00:00:00Z"))

    @Test
    fun `charges 2_5 percent of the outstanding amount when that is above the minimum`() {
        assertEquals(gbp("6.00"), policy.lateFee(invoice(amount = gbp("240.00")), standard))
    }

    @Test
    fun `charges the minimum when 2_5 percent is below it`() {
        assertEquals(gbp("5.00"), policy.lateFee(invoice(amount = gbp("40.00")), standard))
    }

    @Test
    fun `rounds half-even at a boundary`() {
        // 2.5% of 1000.20 is 25.005 and of 1000.60 is 25.015: both exact halves, rounded to even.
        assertEquals(gbp("25.00"), policy.lateFee(invoice(amount = gbp("1000.20")), standard))
        assertEquals(gbp("25.02"), policy.lateFee(invoice(amount = gbp("1000.60")), standard))
    }

    @Test
    fun `JPY uses its own minimum`() {
        assertEquals(Money.of("1000", "JPY"), policy.lateFee(invoice(amount = Money.of("4800", "JPY")), standard))
    }

    @Test
    fun `each currency uses its own minimum`() {
        assertEquals(Money.of("6.00", "EUR"), policy.lateFee(invoice(amount = Money.of("89.00", "EUR")), standard))
        assertEquals(Money.of("6.50", "USD"), policy.lateFee(invoice(amount = Money.of("19.00", "USD")), standard))
    }

    @Test
    fun `grandfathered plans are not charged a late fee`() {
        val legacy = customer(plan = PlanTier.CLASSIC_STARTER)
        assertNull(policy.lateFee(invoice(amount = gbp("240.00")), legacy))
    }

    @Test
    fun `no late fee when the outstanding amount is zero`() {
        assertNull(policy.lateFee(invoice(amount = gbp("0.00")), standard))
    }

    @Test
    fun `no late fee when the outstanding amount is negative`() {
        val credited = invoice(amount = gbp("100.00")).withAdjustment(adjustment(AdjustmentType.CREDIT, gbp("-150.00")))
        assertNull(policy.lateFee(credited, standard))
    }

    @Test
    fun `the outstanding amount includes existing adjustments`() {
        val credited = invoice(amount = gbp("300.00")).withAdjustment(adjustment(AdjustmentType.CREDIT, gbp("-60.00")))
        assertEquals(gbp("6.00"), policy.lateFee(credited, standard))
    }

    @Test
    fun `an invoice that already has a late fee does not get another`() {
        val charged = invoice(amount = gbp("240.00")).withAdjustment(adjustment(AdjustmentType.LATE_FEE, gbp("6.00")))
        assertNull(policy.lateFee(charged, standard))
    }

    @Test
    fun `a missing minimum for a billing currency fails at startup`() {
        val noJpy = DunningProperties(
            lateFee = LateFeeProperties(minimum = mapOf("GBP" to "5.00", "EUR" to "6.00", "USD" to "6.50")),
        )
        val error = assertFailsWith<IllegalStateException> { LateFeePolicy(noJpy) }
        assertTrue("JPY" in error.message!!)
    }

    @Test
    fun `an unparseable minimum fails at startup`() {
        val bad = DunningProperties(lateFee = LateFeeProperties(minimum = LateFeeProperties().minimum + ("GBP" to "five")))
        val error = assertFailsWith<IllegalStateException> { LateFeePolicy(bad) }
        assertTrue("GBP" in error.message!!)
    }

    @Test
    fun `lower-case currency keys from environment overrides are accepted`() {
        val lowerCase = DunningProperties(
            lateFee = LateFeeProperties(minimum = mapOf("gbp" to "5.00", "eur" to "6.00", "usd" to "6.50", "jpy" to "1000")),
        )
        assertEquals(gbp("5.00"), LateFeePolicy(lowerCase).lateFee(invoice(amount = gbp("40.00")), standard))
    }

    @Test
    fun `a currency with no configured minimum fails rather than defaulting to zero`() {
        assertFailsWith<IllegalStateException> {
            policy.lateFee(invoice(amount = Money.of("100.00", "CHF")), standard)
        }
    }
}
