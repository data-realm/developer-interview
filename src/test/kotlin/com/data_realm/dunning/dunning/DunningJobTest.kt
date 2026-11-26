package com.data_realm.dunning.dunning

import com.data_realm.dunning.TestFixtures.clockAt
import com.data_realm.dunning.TestFixtures.customer
import com.data_realm.dunning.TestFixtures.invoice
import com.data_realm.dunning.TestFixtures.properties
import com.data_realm.dunning.domain.Customer
import com.data_realm.dunning.domain.Invoice
import com.data_realm.dunning.domain.InvoiceStatus
import com.data_realm.dunning.money.Money
import com.data_realm.dunning.notifications.CustomerNotifier
import com.data_realm.dunning.payments.ChargeResult
import com.data_realm.dunning.payments.PaymentGateway
import com.data_realm.dunning.repository.InMemoryCustomerRepository
import com.data_realm.dunning.repository.InMemoryInvoiceRepository
import com.data_realm.dunning.time.BillingClock
import kotlin.test.Test
import kotlin.test.assertEquals

class DunningJobTest {

    private class RecordingGateway(private val succeed: Boolean) : PaymentGateway {
        val charges = mutableListOf<Pair<String, Money>>()
        override fun charge(customerId: String, amount: Money): ChargeResult {
            charges += customerId to amount
            return if (succeed) ChargeResult(true) else ChargeResult(false, "card_declined")
        }
    }

    private class RecordingNotifier : CustomerNotifier {
        val suspended = mutableListOf<String>()
        override fun invoiceSuspended(customer: Customer, invoice: Invoice) {
            suspended += invoice.id
        }
    }

    private val invoices = InMemoryInvoiceRepository()
    private val customers = InMemoryCustomerRepository()
    private val notifier = RecordingNotifier()

    private fun job(clock: BillingClock, gateway: PaymentGateway) = DunningJob(
        invoices, customers, SuspensionPolicy(clock, properties), RetryPolicy(clock, properties), gateway, notifier, clock,
    )

    @Test
    fun `suspends overdue invoices and notifies the customer`() {
        customers.save(customer())
        invoices.save(invoice(dueDate = "2026-03-03"))

        val summary = job(clockAt("2026-03-11T09:00:00Z"), RecordingGateway(succeed = false)).run()

        assertEquals(1, summary.suspended)
        assertEquals(InvoiceStatus.SUSPENDED, invoices.findById("inv_1")!!.status)
        assertEquals(listOf("inv_1"), notifier.suspended)
    }

    @Test
    fun `charges the invoice total on a retry day and marks it paid on success`() {
        customers.save(customer())
        invoices.save(invoice(dueDate = "2026-03-03", amount = Money.of("100.00", "GBP")))
        val gateway = RecordingGateway(succeed = true)

        val summary = job(clockAt("2026-03-04T09:00:00Z"), gateway).run()

        assertEquals(1, summary.paid)
        assertEquals(listOf("cus_1" to Money.of("100.00", "GBP")), gateway.charges)
        assertEquals(InvoiceStatus.PAID, invoices.findById("inv_1")!!.status)
    }

    @Test
    fun `records failed attempts and leaves the invoice open`() {
        customers.save(customer())
        invoices.save(invoice(dueDate = "2026-03-03"))

        job(clockAt("2026-03-04T09:00:00Z"), RecordingGateway(succeed = false)).run()

        val saved = invoices.findById("inv_1")!!
        assertEquals(InvoiceStatus.OPEN, saved.status)
        assertEquals("card_declined", saved.attempts.single().failureCode)
    }

    @Test
    fun `running twice on the same day charges once`() {
        customers.save(customer())
        invoices.save(invoice(dueDate = "2026-03-03"))
        val gateway = RecordingGateway(succeed = false)
        val job = job(clockAt("2026-03-04T09:00:00Z"), gateway)

        job.run()
        job.run()

        assertEquals(1, gateway.charges.size)
    }
}
