package com.data_realm.dunning.dunning

import com.data_realm.dunning.config.DunningProperties
import com.data_realm.dunning.domain.Customer
import com.data_realm.dunning.domain.Invoice
import com.data_realm.dunning.domain.InvoiceStatus
import com.data_realm.dunning.time.BillingClock
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit

@Component
class RetryPolicy(
    private val clock: BillingClock,
    private val properties: DunningProperties,
) {
    /** A retry is due on each configured day after the due date, at most once per customer-local day. */
    fun isRetryDue(invoice: Invoice, customer: Customer): Boolean {
        if (invoice.status != InvoiceStatus.OPEN) return false
        val today = clock.today(customer.zone)
        val daysOverdue = ChronoUnit.DAYS.between(invoice.dueDate, today)
        if (daysOverdue.toInt() !in properties.retryDaysAfterDue) return false
        return invoice.attempts.none { it.localDate == today }
    }
}
