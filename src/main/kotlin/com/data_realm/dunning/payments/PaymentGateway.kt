package com.data_realm.dunning.payments

import com.data_realm.dunning.money.Money
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

data class ChargeResult(val succeeded: Boolean, val failureCode: String? = null)

interface PaymentGateway {
    /** Charges the customer's card on file. Each call is a new charge. */
    fun charge(customerId: String, amount: Money): ChargeResult
}

/**
 * Stand-in for the card processor. Customers whose id contains "declined" always fail, and
 * everyone else succeeds.
 */
@Component
class FakePaymentGateway : PaymentGateway {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun charge(customerId: String, amount: Money): ChargeResult {
        val result = if ("declined" in customerId) ChargeResult(false, "card_declined") else ChargeResult(true)
        log.info("charge customer={} amount={} result={}", customerId, amount, result)
        return result
    }
}
