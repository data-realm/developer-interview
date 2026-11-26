package com.data_realm.dunning.dunning

import com.data_realm.dunning.config.DunningProperties
import com.data_realm.dunning.domain.AdjustmentType
import com.data_realm.dunning.domain.Customer
import com.data_realm.dunning.domain.Invoice
import com.data_realm.dunning.money.Money
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.Currency

@Component
class LateFeePolicy(properties: DunningProperties) {
    private val basisPoints = properties.lateFee.basisPoints

    /** The configured minimum fee per currency, parsed and checked once at startup. */
    private val minimums: Map<Currency, Money> = parseMinimums(properties.lateFee.minimum)

    /**
     * The late fee to add when [invoice] is suspended, or null if none is due: grandfathered
     * customers are not charged, nothing is charged on an invoice that owes nothing, and an invoice
     * never gets a second late fee. Otherwise the fee is the configured percentage of what is
     * outstanding ([Invoice.total]), or the currency's minimum if that is higher.
     */
    fun lateFee(invoice: Invoice, customer: Customer): Money? {
        if (customer.plan.grandfathered) return null
        if (invoice.adjustments.any { it.type == AdjustmentType.LATE_FEE }) return null
        val outstanding = invoice.total()
        if (outstanding <= Money.zero(outstanding.currency)) return null
        return maxOf(outstanding.percent(basisPoints), minimumFor(outstanding.currency))
    }

    /** Human-readable reason recorded on the late fee adjustment. */
    fun reason(currency: Currency): String =
        "Late fee on suspension (${BigDecimal.valueOf(basisPoints.toLong(), 2).stripTrailingZeros().toPlainString()}%, " +
            "minimum ${minimumFor(currency)})"

    private fun minimumFor(currency: Currency): Money =
        minimums[currency] ?: error("No late fee minimum configured for ${currency.currencyCode}")

    private companion object {
        /** The billing currencies from docs/billing-rules.md. Each needs a minimum. */
        val BILLING_CURRENCIES = listOf("GBP", "EUR", "USD", "JPY")

        fun parseMinimums(configured: Map<String, String>): Map<Currency, Money> {
            // Keys are upper-cased because Spring lower-cases map keys bound from environment variables.
            val parsed = configured.entries.associate { (code, amount) ->
                val money = try {
                    Money.of(amount, code.uppercase())
                } catch (e: RuntimeException) {
                    throw IllegalStateException(
                        "Invalid dunning.late-fee.minimum.$code: '$amount' is not a valid amount in $code", e,
                    )
                }
                money.currency to money
            }
            val missing = BILLING_CURRENCIES.filter { Currency.getInstance(it) !in parsed }
            check(missing.isEmpty()) { "dunning.late-fee.minimum has no value for: ${missing.joinToString()}" }
            return parsed
        }
    }
}
