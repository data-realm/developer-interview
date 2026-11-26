package com.data_realm.dunning.config

import com.data_realm.dunning.time.BillingClock
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@ConfigurationProperties("dunning")
data class DunningProperties(
    val cron: String = "0 0 * * * *",
    /** Number of whole days after the due date that the customer still has to pay. */
    val graceDays: Int = 7,
    /** Days after the due date on which a failed payment is retried. */
    val retryDaysAfterDue: List<Int> = listOf(1, 3, 5),
    val lateFee: LateFeeProperties = LateFeeProperties(),
)

/** Late fee charged when an account is suspended. Bound from `dunning.late-fee`. */
data class LateFeeProperties(
    /** Fee as a percentage of the outstanding amount, in basis points (250 = 2.5%). */
    val basisPoints: Int = 250,
    /**
     * Minimum fee per currency code, as a major-unit amount (e.g. "5.00", "1000" for JPY). These are
     * fixed, rough equivalents of 5 GBP; the service never converts currencies.
     */
    val minimum: Map<String, String> = mapOf("GBP" to "5.00", "EUR" to "6.00", "USD" to "6.50", "JPY" to "1000"),
)

@Configuration
class DunningConfig {
    @Bean
    fun billingClock(): BillingClock = BillingClock(Clock.systemUTC())
}
