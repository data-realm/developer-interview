package com.data_realm.dunning.notifications

import com.data_realm.dunning.domain.Customer
import com.data_realm.dunning.domain.Invoice
import com.data_realm.dunning.money.Money
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

interface CustomerNotifier {
    /** [lateFee] is the late fee added to [invoice] by this suspension, or null if none was charged. */
    fun invoiceSuspended(customer: Customer, invoice: Invoice, lateFee: Money?)
}

/** Stand-in for the email service. */
@Component
class LoggingCustomerNotifier : CustomerNotifier {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun invoiceSuspended(customer: Customer, invoice: Invoice, lateFee: Money?) {
        log.info(
            "email to={} template=invoice-suspended invoice={} lateFee={} total={}",
            customer.email, invoice.id, lateFee ?: "none", invoice.total(),
        )
    }
}
