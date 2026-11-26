package com.data_realm.dunning.dunning

import com.data_realm.dunning.domain.AdjustmentType
import com.data_realm.dunning.domain.Customer
import com.data_realm.dunning.domain.Invoice
import com.data_realm.dunning.domain.InvoiceAdjustment
import com.data_realm.dunning.domain.InvoiceStatus
import com.data_realm.dunning.domain.PaymentAttempt
import com.data_realm.dunning.notifications.CustomerNotifier
import com.data_realm.dunning.payments.PaymentGateway
import com.data_realm.dunning.repository.CustomerRepository
import com.data_realm.dunning.repository.InvoiceRepository
import com.data_realm.dunning.time.BillingClock
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

data class DunningRunSummary(val examined: Int, val retried: Int, val paid: Int, val suspended: Int)

/**
 * Works through every open invoice: suspends those whose grace period is over, and retries
 * payment on the configured retry days.
 */
@Component
class DunningJob(
    private val invoices: InvoiceRepository,
    private val customers: CustomerRepository,
    private val suspensionPolicy: SuspensionPolicy,
    private val retryPolicy: RetryPolicy,
    private val lateFeePolicy: LateFeePolicy,
    private val gateway: PaymentGateway,
    private val notifier: CustomerNotifier,
    private val clock: BillingClock,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "\${dunning.cron}")
    fun scheduledRun() {
        run()
    }

    fun run(): DunningRunSummary {
        val open = invoices.findByStatus(InvoiceStatus.OPEN)
        var retried = 0
        var paid = 0
        var suspended = 0

        for (invoice in open) {
            val customer = customers.require(invoice.customerId)
            when {
                suspensionPolicy.shouldSuspend(invoice, customer) -> {
                    suspend(invoice, customer)
                    suspended++
                }
                retryPolicy.isRetryDue(invoice, customer) -> {
                    retried++
                    if (retry(invoice, customer)) paid++
                }
            }
        }

        val summary = DunningRunSummary(open.size, retried, paid, suspended)
        log.info("dunning run batch={} {}", clock.systemDate(), summary)
        return summary
    }

    private fun suspend(invoice: Invoice, customer: Customer) {
        val lateFee = lateFeePolicy.lateFee(invoice, customer)
        val withFee = if (lateFee == null) invoice else invoice.withAdjustment(
            InvoiceAdjustment(
                type = AdjustmentType.LATE_FEE,
                amount = lateFee,
                reason = lateFeePolicy.reason(lateFee.currency),
                createdAt = clock.now(),
            ),
        )
        val updated = invoices.save(withFee.copy(status = InvoiceStatus.SUSPENDED))
        notifier.invoiceSuspended(customer, updated, lateFee)
    }

    private fun retry(invoice: Invoice, customer: Customer): Boolean {
        val result = gateway.charge(customer.id, invoice.total())
        val attempt = PaymentAttempt(
            at = clock.now(),
            localDate = clock.today(customer.zone),
            succeeded = result.succeeded,
            failureCode = result.failureCode,
        )
        var updated = invoice.withAttempt(attempt)
        if (result.succeeded) updated = updated.copy(status = InvoiceStatus.PAID)
        invoices.save(updated)
        return result.succeeded
    }
}
