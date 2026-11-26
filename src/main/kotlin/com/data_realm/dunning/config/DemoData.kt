package com.data_realm.dunning.config

import com.data_realm.dunning.domain.Customer
import com.data_realm.dunning.domain.Invoice
import com.data_realm.dunning.domain.PlanTier
import com.data_realm.dunning.money.Money
import com.data_realm.dunning.repository.CustomerRepository
import com.data_realm.dunning.repository.InvoiceRepository
import com.data_realm.dunning.time.BillingClock
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.time.ZoneId

/** Seeds a handful of customers with overdue invoices so the app has something to show when run locally. */
@Component
class DemoData(
    private val customers: CustomerRepository,
    private val invoices: InvoiceRepository,
    private val clock: BillingClock,
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        seed("cus_london", "Thames Analytics", "Europe/London", PlanTier.GROWTH, "inv_1001", "240.00", "GBP", overdueDays = 9)
        seed("cus_declined_la", "Sunset Studios", "America/Los_Angeles", PlanTier.SCALE, "inv_1002", "1200.00", "USD", overdueDays = 3)
        seed("cus_declined_tokyo", "Kanda Robotics", "Asia/Tokyo", PlanTier.STARTER, "inv_1003", "4800", "JPY", overdueDays = 8)
        seed("cus_declined_berlin", "Spree Logistik", "Europe/Berlin", PlanTier.CLASSIC_GROWTH, "inv_1004", "89.00", "EUR", overdueDays = 8)
        seed("cus_declined_nyc", "Hudson Health", "America/New_York", PlanTier.CLASSIC_STARTER, "inv_1005", "19.00", "USD", overdueDays = 1)
    }

    private fun seed(
        customerId: String, name: String, zone: String, plan: PlanTier,
        invoiceId: String, amount: String, currency: String, overdueDays: Long,
    ) {
        val customer = customers.save(Customer(customerId, name, "billing@$customerId.example", ZoneId.of(zone), plan))
        invoices.save(
            Invoice(
                id = invoiceId,
                customerId = customer.id,
                amount = Money.of(amount, currency),
                dueDate = clock.today(customer.zone).minusDays(overdueDays),
            ),
        )
    }
}
