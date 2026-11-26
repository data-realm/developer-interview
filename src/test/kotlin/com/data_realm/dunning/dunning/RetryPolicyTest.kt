package com.data_realm.dunning.dunning

import com.data_realm.dunning.TestFixtures.clockAt
import com.data_realm.dunning.TestFixtures.customer
import com.data_realm.dunning.TestFixtures.invoice
import com.data_realm.dunning.TestFixtures.properties
import com.data_realm.dunning.domain.PaymentAttempt
import java.time.Instant
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RetryPolicyTest {

    private val london = customer(zone = "Europe/London")

    @Test
    fun `retries on the configured days after the due date`() {
        listOf("2026-03-04", "2026-03-06", "2026-03-08").forEach { day ->
            val policy = RetryPolicy(clockAt("${day}T10:00:00Z"), properties)
            assertTrue(policy.isRetryDue(invoice(dueDate = "2026-03-03"), london), "expected a retry on $day")
        }
    }

    @Test
    fun `does not retry on other days`() {
        listOf("2026-03-03", "2026-03-05", "2026-03-07", "2026-03-09").forEach { day ->
            val policy = RetryPolicy(clockAt("${day}T10:00:00Z"), properties)
            assertFalse(policy.isRetryDue(invoice(dueDate = "2026-03-03"), london), "expected no retry on $day")
        }
    }

    @Test
    fun `retries at most once per local day`() {
        val policy = RetryPolicy(clockAt("2026-03-04T15:00:00Z"), properties)
        val alreadyTried = invoice(dueDate = "2026-03-03").withAttempt(
            PaymentAttempt(Instant.parse("2026-03-04T10:00:00Z"), LocalDate.parse("2026-03-04"), succeeded = false),
        )
        assertFalse(policy.isRetryDue(alreadyTried, london))
    }

    @Test
    fun `uses the customer's local date`() {
        // 16:00 UTC on 3 March is already 4 March in Tokyo, which is day 1 after the due date.
        val tokyo = customer(zone = "Asia/Tokyo")
        val policy = RetryPolicy(clockAt("2026-03-03T16:00:00Z"), properties)
        assertTrue(policy.isRetryDue(invoice(dueDate = "2026-03-03"), tokyo))
    }
}
