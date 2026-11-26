package com.data_realm.dunning.domain

import com.data_realm.dunning.money.Money
import java.time.Instant
import java.time.LocalDate

enum class InvoiceStatus { OPEN, PAID, SUSPENDED }

/**
 * An invoice. [amount] is what was originally billed and never changes once issued: finance
 * reconciles against it. Anything that changes what the customer owes (discounts, credits, fees)
 * is recorded as an [InvoiceAdjustment]; the amount due is [total].
 */
data class Invoice(
    val id: String,
    val customerId: String,
    val amount: Money,
    /** Calendar date in the customer's time zone. */
    val dueDate: LocalDate,
    val status: InvoiceStatus = InvoiceStatus.OPEN,
    val adjustments: List<InvoiceAdjustment> = emptyList(),
    val attempts: List<PaymentAttempt> = emptyList(),
) {
    fun total(): Money = adjustments.fold(amount) { sum, adjustment -> sum + adjustment.amount }

    fun withAdjustment(adjustment: InvoiceAdjustment): Invoice {
        require(adjustment.amount.currency == amount.currency) { "Adjustment currency must match invoice" }
        return copy(adjustments = adjustments + adjustment)
    }

    fun withAttempt(attempt: PaymentAttempt): Invoice = copy(attempts = attempts + attempt)
}

enum class AdjustmentType { DISCOUNT, CREDIT, TAX_CORRECTION }

/** A signed change to what the customer owes: negative for discounts and credits. */
data class InvoiceAdjustment(
    val type: AdjustmentType,
    val amount: Money,
    val reason: String,
    val createdAt: Instant,
)

data class PaymentAttempt(
    val at: Instant,
    /** The customer-local date of the attempt; used to avoid retrying twice on the same day. */
    val localDate: LocalDate,
    val succeeded: Boolean,
    val failureCode: String? = null,
)
