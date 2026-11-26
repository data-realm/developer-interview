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
)

@Configuration
class DunningConfig {
    @Bean
    fun billingClock(): BillingClock = BillingClock(Clock.systemUTC())
}
