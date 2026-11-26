package com.data_realm.dunning.money

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Currency

/**
 * Every monetary amount in this service is a [Money]: an exact number of minor units (pence, cents,
 * or whole yen for JPY) in a single currency.
 *
 * Do not use Double/Float for money. Do not do arithmetic on raw minor units outside this class.
 * See docs/CONVENTIONS.md.
 */
data class Money(val minorUnits: Long, val currency: Currency) : Comparable<Money> {

    operator fun plus(other: Money): Money {
        requireSameCurrency(other)
        return Money(Math.addExact(minorUnits, other.minorUnits), currency)
    }

    operator fun minus(other: Money): Money {
        requireSameCurrency(other)
        return Money(Math.subtractExact(minorUnits, other.minorUnits), currency)
    }

    /**
     * A percentage of this amount, given in basis points (250 = 2.5%), rounded half-even to the
     * currency's minor unit.
     */
    fun percent(basisPoints: Int): Money {
        val result = BigDecimal.valueOf(minorUnits)
            .multiply(BigDecimal.valueOf(basisPoints.toLong()))
            .divide(BigDecimal.valueOf(10_000), 0, RoundingMode.HALF_EVEN)
        return Money(result.longValueExact(), currency)
    }

    fun isZero(): Boolean = minorUnits == 0L

    override fun compareTo(other: Money): Int {
        requireSameCurrency(other)
        return minorUnits.compareTo(other.minorUnits)
    }

    override fun toString(): String =
        "${BigDecimal.valueOf(minorUnits, currency.defaultFractionDigits).toPlainString()} ${currency.currencyCode}"

    private fun requireSameCurrency(other: Money) {
        require(currency == other.currency) { "Currency mismatch: $currency vs ${other.currency}" }
    }

    companion object {
        /** Parses a major-unit amount, e.g. `Money.of("12.50", "GBP")` or `Money.of("1500", "JPY")`. */
        fun of(amount: String, currencyCode: String): Money {
            val currency = Currency.getInstance(currencyCode)
            val minor = BigDecimal(amount)
                .setScale(currency.defaultFractionDigits, RoundingMode.UNNECESSARY)
                .unscaledValue()
                .longValueExact()
            return Money(minor, currency)
        }

        fun zero(currency: Currency): Money = Money(0, currency)
    }
}
