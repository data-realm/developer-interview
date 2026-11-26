package com.data_realm.dunning.dunning

import com.data_realm.dunning.TestFixtures.clockAt
import com.data_realm.dunning.TestFixtures.customer
import com.data_realm.dunning.TestFixtures.invoice
import com.data_realm.dunning.TestFixtures.properties
import com.data_realm.dunning.domain.InvoiceStatus
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SuspensionPolicyTest {

    private val london = customer(zone = "Europe/London")
    private val losAngeles = customer(zone = "America/Los_Angeles")
    private val tokyo = customer(zone = "Asia/Tokyo")

    @Test
    fun `suspends the day after the grace period ends`() {
        val policy = SuspensionPolicy(clockAt("2026-03-11T09:00:00Z"), properties)
        assertTrue(policy.shouldSuspend(invoice(dueDate = "2026-03-03"), london))
    }

    @Test
    fun `does not suspend on the last day of the grace period`() {
        val policy = SuspensionPolicy(clockAt("2026-03-10T09:00:00Z"), properties)
        assertFalse(policy.shouldSuspend(invoice(dueDate = "2026-03-03"), london))
    }

    @Test
    fun `never suspends an invoice that is already paid`() {
        val policy = SuspensionPolicy(clockAt("2026-04-01T09:00:00Z"), properties)
        assertFalse(policy.shouldSuspend(invoice(dueDate = "2026-03-03").copy(status = InvoiceStatus.PAID), london))
    }

    @Test
    fun `customer in Los Angeles is not suspended before their grace period ends`() {
        // 02:00 UTC on 11 March is 19:00 on 10 March in Los Angeles (PDT, UTC-7): still the last day of grace.
        val policy = SuspensionPolicy(clockAt("2026-03-11T02:00:00Z"), properties)
        assertFalse(policy.shouldSuspend(invoice(dueDate = "2026-03-03"), losAngeles))
    }

    @Test
    fun `customer in Los Angeles is suspended once 11 March starts in Los Angeles`() {
        // 08:00 UTC on 11 March is 01:00 on 11 March in Los Angeles.
        val policy = SuspensionPolicy(clockAt("2026-03-11T08:00:00Z"), properties)
        assertTrue(policy.shouldSuspend(invoice(dueDate = "2026-03-03"), losAngeles))
    }

    @Test
    fun `customer in Tokyo is suspended once 11 March starts in Tokyo, even while it is still 10 March in UTC`() {
        // 16:00 UTC on 10 March is 01:00 on 11 March in Tokyo (UTC+9).
        val policy = SuspensionPolicy(clockAt("2026-03-10T16:00:00Z"), properties)
        assertTrue(policy.shouldSuspend(invoice(dueDate = "2026-03-03"), tokyo))
    }
}
