package com.data_realm.dunning.notifications

import com.data_realm.dunning.domain.Customer
import com.data_realm.dunning.domain.Invoice
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

interface CustomerNotifier {
    fun invoiceSuspended(customer: Customer, invoice: Invoice)
}

/** Stand-in for the email service. */
@Component
class LoggingCustomerNotifier : CustomerNotifier {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun invoiceSuspended(customer: Customer, invoice: Invoice) {
        log.info("email to={} template=invoice-suspended invoice={} total={}", customer.email, invoice.id, invoice.total())
    }
}
