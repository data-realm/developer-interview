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
        val losAngeles = customer(zone = "America/Los_Angeles")
        val policy = SuspensionPolicy(clockAt("2026-03-10T15:00:00Z"), properties)
        assertFalse(policy.shouldSuspend(invoice(dueDate = "2026-03-03"), losAngeles))
    }
}
