package com.data_realm.dunning.dunning

import com.data_realm.dunning.config.DunningProperties
import com.data_realm.dunning.domain.Customer
import com.data_realm.dunning.domain.Invoice
import com.data_realm.dunning.domain.InvoiceStatus
import com.data_realm.dunning.time.BillingClock
import org.springframework.stereotype.Component

@Component
class SuspensionPolicy(
    private val clock: BillingClock,
    private val properties: DunningProperties,
) {
    /**
     * An unpaid invoice is suspended once its grace period has fully elapsed. With a 7-day grace
     * period and a due date of 3 March, the customer has until the end of 10 March to pay and is
     * suspended from 11 March.
     */
    fun shouldSuspend(invoice: Invoice, customer: Customer): Boolean {
        if (invoice.status != InvoiceStatus.OPEN) return false
        val lastDayOfGrace = invoice.dueDate.plusDays(properties.graceDays.toLong())
        return clock.systemDate().isAfter(lastDayOfGrace)
    }
}
